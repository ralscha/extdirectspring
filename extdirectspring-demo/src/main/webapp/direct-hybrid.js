Ext.onReady(function() {

  Ext.Direct.addProvider(Ext.app.REMOTING_API);

  var reader = new Ext.data.JsonReader( {
    idProperty: 'taskId',
    root: '',
    fields: [ {
      name: 'projectId',
      type: 'int'
    }, {
      name: 'project',
      type: 'string'
    }, {
      name: 'taskId',
      type: 'int'
    }, {
      name: 'description',
      type: 'string'
    }, {
      name: 'estimate',
      type: 'float'
    }, {
      name: 'rate',
      type: 'float'
    }, {
      name: 'cost',
      type: 'float'
    }, {
      name: 'due',
      type: 'date',
      dateFormat: 'm/d/Y'
    } ]
  });

  var proxy = new Ext.data.DirectProxy( {
    paramsAsHash: true,
    directFn: groupAction.loadHybrid
  });

  var store = new Ext.data.GroupingStore( {
    reader: reader,
    proxy: proxy,
    sortInfo: {
      field: 'due',
      direction: 'ASC'
    },
    groupField: 'project',
    autoLoad: true,
    remoteGroup:true,
    remoteSort: true
  });

  Ext.ux.grid.GroupSummary.Calculations['totalCost'] = function(v, record, field) {
    return v + (record.data.estimate * record.data.rate);
  };

  var summary = new Ext.ux.grid.HybridSummary();

  var grid = new Ext.grid.EditorGridPanel( {
    store: store,
    columns: [ {
      id: 'description',
      header: 'Task',
      width: 80,
      sortable: true,
      dataIndex: 'description',
      summaryType: 'count',
      hideable: false,
      summaryRenderer: function(v, params, data) {
        return ((v === 0 || v > 1) ? '(' + v + ' Tasks)' : '(1 Task)');
      },
      editor: new Ext.form.TextField( {
        allowBlank: false
      })
    }, {
      header: 'Project',
      width: 20,
      sortable: true,
      dataIndex: 'project'
    }, {
      header: 'Due Date',
      width: 25,
      sortable: true,
      dataIndex: 'due',
      summaryType: 'max',
      renderer: Ext.util.Format.dateRenderer('m/d/Y'),
      editor: new Ext.form.DateField( {
        format: 'm/d/Y'
      })
    }, {
      header: 'Estimate',
      width: 20,
      sortable: true,
      dataIndex: 'estimate',
      summaryType: 'sum',
      renderer: function(v) {
        return v + ' hours';
      },
      editor: new Ext.form.NumberField( {
        allowBlank: false,
        allowNegative: false,
        style: 'text-align:left'
      })
    }, {
      id: 'rate',
      header: 'Rate',
      width: 20,
      sortable: true,
      renderer: Ext.util.Format.usMoney,
      dataIndex: 'rate',
      summaryType: 'average',
      editor: new Ext.form.NumberField( {
        allowBlank: false,
        allowNegative: false,
        style: 'text-align:left'
      })
    }, {
      id: 'cost',
      header: 'Cost',
      width: 20,
      sortable: false,
      groupable: false,
      renderer: function(v, params, record) {
        return Ext.util.Format.usMoney(record.data.estimate * record.data.rate);
      },
      dataIndex: 'cost',
      summaryType: 'totalCost',
      summaryRenderer: Ext.util.Format.usMoney
    } ],

    view: new Ext.grid.GroupingView( {
      forceFit: true,
      showGroupName: false,
      enableNoGroups: false,
      enableGroupingMenu: false,
      hideGroupedColumn: true
    }),

    plugins: summary,

    tbar: [ {
      text: 'Toggle',
      tooltip: 'Toggle the visibility of summary row',
      handler: function() {
        summary.toggleSummaries();
      }
    } ],

    frame: true,
    width: 800,
    height: 450,
    clicksToEdit: 1,
    collapsible: true,
    animCollapse: false,
    trackMouseOver: false,
    enableColumnMove: false,
    title: 'Sponsored Projects',
    iconCls: 'icon-grid',
    renderTo: Ext.getBody()
  });

  
  grid.on('afteredit', function(){
    var groupValue = 'Ext Forms: Field Anchoring';
    summary.showSummaryMsg(groupValue, 'Updating Summary...');
    groupAction.updateSummary(groupValue, callback);
  });
  
  var callback = function(result) {
    var groupValue = 'Ext Forms: Field Anchoring';
    summary.updateSummaryData(groupValue, result);
  };
  
});
