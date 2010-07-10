Ext.onReady(function() {

      Ext.Direct.addProvider(Ext.app.REMOTING_API);

      var reader = new Ext.data.JsonReader({
            totalProperty: 'total',
            root: 'records',
            successProperty: 'success',
            idProperty: 'id',
            fields: ["lastName", "firstName", "id", "street", "city", "state", "zip"]
          });

      var proxy = new Ext.data.DirectProxy({
            paramsAsHash: true,
            directFn: personAction.loadWithPaging
          });

      var myStore = new Ext.data.GroupingStore({
            reader: reader,
            proxy: proxy,
            sortInfo: {
              field: 'lastName',
              direction: "ASC"
            },
            groupField: 'city',
            remoteSort: true,
            remoteGroup: true,
            autoLoad: false,
            id: 'myStore'

          });

      var columnModel = [{
            header: 'Last Name',
            dataIndex: 'lastName',
            sortable: true
          }, {
            header: 'First Name',
            dataIndex: 'firstName',
            sortable: true
          }, {
            header: 'Street Address',
            dataIndex: 'street',
            sortable: true
          }, {
            header: 'City',
            dataIndex: 'city',
            sortable: true
          }, {
            header: 'State',
            dataIndex: 'state',
            sortable: true
          }, {
            header: 'Zip Code',
            dataIndex: 'zip',
            sortable: true
          }];

      var pagingToolbar = {
        xtype: 'paging',
        store: myStore,
        pageSize: 50,
        displayInfo: true
      };

      var grid = new Ext.grid.GridPanel({
            renderTo: Ext.getBody(),
            height: 350,
            width: 700,
            columns: columnModel,
            store: myStore,
            loadMask: true,
            id: 'myEditorGrid',
            bbar: pagingToolbar,
            stripeRows: true,
            view: new Ext.grid.GroupingView({
                  forceFit: true,
                  groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})'
                }),
            fbar: ['->', {
                  text: 'Clear Grouping',
                  handler: function() {
                    store.clearGrouping();
                  }
                }]

          });

      Ext.StoreMgr.get('myStore').load({
            params: {
              start: 0,
              limit: 50
            }
          });

    });
