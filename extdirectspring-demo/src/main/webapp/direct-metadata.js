Ext.onReady(function() {


  Ext.Direct.addProvider(Ext.app.REMOTING_API);

  var listStore = new Ext.data.SimpleStore( {
    fields: [ 'list' ],
    data: [ [ 'Persons with FullName' ], [ 'Persons with FullName and City' ], [ 'Persons with everything' ]]
  });
  
  var directStore = new Ext.data.DirectStore( {
    id: 'directStore',
    paramsAsHash: true,    
    autoLoad: true,
    fields: [],
    directFn: personAction.loadPersonFullName  
  });

  var pagingToolbar = {
    xtype: 'paging',
    id: 'pagingToolbar',
    store: directStore,
    displayInfo: true
  };

  var combo = {
    xtype: 'combo',
    name: 'combo',
    id: 'listcombo',
    fieldLabel: 'List',
    mode: 'local',
    store: listStore,
    displayField: 'list',
    forceSelection: true,
    triggerAction: 'all',
    width: 250,
    valueField: 'list',
    listeners: {
      select: function(combo, record, index) {
        if (index === 0) {
          directStore.remoteSort = false;
          directStore.proxy.setApi(Ext.data.Api.actions.read, personAction.loadPersonFullName);
        } else if (index === 1) {
          directStore.remoteSort = false;
          directStore.proxy.setApi(Ext.data.Api.actions.read, personAction.loadPersonFullNameCity);
        } else if (index === 2) {
          directStore.remoteSort = true;
          directStore.proxy.setApi(Ext.data.Api.actions.read, personAction.loadPersonEverything);
        }
        directStore.sortInfo = null;
        directStore.load();
      }
    }
  };
  
  var grid = {
    xtype: 'grid', 
    id: 'mygrid',
    height: 350,
    width: 700,      
    store: directStore,
    loadMask: true,
    bbar: pagingToolbar,
    stripeRows: true,
    columns: [],
    viewConfig: {
      forceFit: true
    }
  };

  new Ext.Panel( {
    renderTo: Ext.getBody(),    
    border: false,
    frame: false,
    items: [combo,grid]
  });
  
  directStore.on({
    metachange: function(store, meta) {

      var columns = [];
      var fields = meta.fields;
      for (var i = 0; i < fields.length; i++) {
        columns.push(Ext.apply({}, fields[i], {dataIndex: fields[i].name}));
      }
      if (Ext.isDefined(meta.limit)) {
        Ext.getCmp('pagingToolbar').pageSize = meta.limit;
      }
      Ext.getCmp('mygrid').colModel.setConfig(columns);
    }
  });  
  
  Ext.getCmp('listcombo').setValue('Persons with FullName');
  
});
