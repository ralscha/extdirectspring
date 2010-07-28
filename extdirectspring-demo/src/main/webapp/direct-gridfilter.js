Ext.onReady(function() {

      Ext.QuickTips.init();

      Ext.Direct.addProvider(Ext.app.REMOTING_API);

      var directStore = new Ext.data.DirectStore({
            autoDestroy: true,
            paramsAsHash: true,
            remoteSort: true,
            autoLoad: false,
            root: 'records',
            totalProperty: 'total',
            directFn: filterAction.load,
            sortInfo: {
              field: 'company',
              direction: 'ASC'
            },
            fields: [{
                  name: 'id'
                }, {
                  name: 'company'
                }, {
                  name: 'price',
                  type: 'float'
                }, {
                  name: 'date',
                  type: 'date',
                  dateFormat: 'Y-m-d H:i:s'
                }, {
                  name: 'visible',
                  type: 'boolean'
                }, {
                  name: 'size'
                }]

          });

      var filters = new Ext.ux.grid.GridFilters({
            encode: true, // json encode the filter query
            local: false, // defaults to false (remote filtering)
            filters: [{
                  type: 'numeric',
                  dataIndex: 'id'
                }, {
                  type: 'string',
                  dataIndex: 'company'
                  // disabled: true
              } , {
                  type: 'numeric',
                  dataIndex: 'price'
                }, {
                  type: 'date',
                  dataIndex: 'date'
                }, {
                  type: 'list',
                  dataIndex: 'size',
                  options: ['small', 'medium', 'large', 'extra large'],
                  phpMode: true
                }, {
                  type: 'boolean',
                  dataIndex: 'visible'
                }]
          });

      var columns = [{
        dataIndex: 'id',
        header: 'Id',
        sortable: true,
        // instead of specifying filter config just specify filterable=true
        // to use store's field's type property (if type property not
        // explicitly specified in store config it will be 'auto' which
        // GridFilters will assume to be 'StringFilter'
        filterable: true
          // ,filter: {type: 'numeric'}
        }, {
        dataIndex: 'company',
        header: 'Company',
        id: 'company',
        sortable: true,
        filter: {
          type: 'string'
          // specify disabled to disable the filter menu
          // , disabled: true
        }
      }, {
        dataIndex: 'price',
        header: 'Price',
        sortable: true,
        filter: {
        // type: 'numeric' // specify type here or in store fields config
        }
      }, {
        dataIndex: 'size',
        header: 'Size',
        sortable: true,
        filter: {
          type: 'list',
          options: ['small', 'medium', 'large', 'extra large']
          // ,phpMode: true
        }
      }, {
        dataIndex: 'date',
        header: 'Date',
        sortable: true,
        renderer: Ext.util.Format.dateRenderer('m/d/Y'),
        filter: {
        // type: 'date' // specify type here or in store fields config
        }
      }, {
        dataIndex: 'visible',
        header: 'Visible',
        sortable: true,
        filter: {
        // type: 'boolean' // specify type here or in store fields config
        }
      }];

      var grid = new Ext.grid.GridPanel({
            border: false,
            store: directStore,
            columns: columns,
            loadMask: true,
            plugins: [filters],
            autoExpandColumn: 'company',
            bbar: new Ext.PagingToolbar({
                  store: directStore,
                  pageSize: 50,
                  plugins: [filters]
                }),
            listeners: {
              render: {
                fn: function() {
                  directStore.load({
                        params: {
                          start: 0,
                          limit: 50
                        }
                      });
                }
              }
            }

          });

      // add some buttons to bottom toolbar just for demonstration purposes
      grid.getBottomToolbar().add(['->', {
            text: 'All Filter Data',
            tooltip: 'Get Filter Data for Grid',
            handler: function() {
              var data = Ext.encode(grid.filters.getFilterData());
              Ext.Msg.alert('All Filter Data', data);
            }
          }, {
            text: 'Clear Filter Data',
            handler: function() {
              grid.filters.clearFilters();
            }
          }]);

      var win = new Ext.Window({
            title: 'Grid Filters Example',
            height: 400,
            width: 700,
            layout: 'fit',
            items: grid
          }).show();

    });