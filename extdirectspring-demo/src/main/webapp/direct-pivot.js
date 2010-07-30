Ext.onReady(function() {

      Ext.Direct.addProvider(Ext.app.REMOTING_API);

      var saleFields = [{
            name: 'person',
            type: 'string'
          }, {
            name: 'product',
            type: 'string'
          }, {
            name: 'city',
            type: 'string'
          }, {
            name: 'month',
            type: 'int'
          }, {
            name: 'quarter',
            type: 'int'
          }, {
            name: 'year',
            type: 'int'
          }, {
            name: 'quantity',
            type: 'int'
          }, {
            name: 'value',
            type: 'int'
          }];

      var directStore = new Ext.data.DirectStore({
            paramsAsHash: true,
            root: '',
            autoLoad: true,
            directFn: salesAction.load,
            fields: saleFields
          });

      var limit = 220;

      var switchHandler = function(b, e) {
        if (pivotGrid.measure === 'quantity') {
          b.setText('Show Quantity');
          limit = 22000;
          pivotGrid.setMeasure('value');
          pivotGrid.view.refresh(true);
          pivotGrid.view.setTitle('Sales Performance Value');
        } else {
          b.setText('Show Value');
          limit = 220;
          pivotGrid.setMeasure('quantity');
          pivotGrid.view.refresh(true);
          pivotGrid.view.setTitle('Sales Performance Quantity');
        }
      };

      var pivotGrid = new Ext.grid.PivotGrid({
            title: 'PivotGrid Example',
            width: 1000,
            height: 286,
            renderTo: 'docbody',
            store: directStore,
            aggregator: 'sum',
            tbar: ['->', {
                  text: 'Show Value',
                  handler: switchHandler
                }],
            measure: 'quantity',

            viewConfig: {
              title: 'Sales Performance Quantity',
              getCellCls: function(value) {
                if (value === 0) {
                  return 'sales-zero';
                } else if (value > limit) {
                  return 'sales-high';
                } else {
                  return '';
                }
              }
            },

            leftAxis: [{
                  width: 80,
                  dataIndex: 'person'
                }, {
                  width: 90,
                  dataIndex: 'product'
                }],

            topAxis: [{
                  dataIndex: 'year'
                }, {
                  dataIndex: 'city'
                }]
          });

    });
