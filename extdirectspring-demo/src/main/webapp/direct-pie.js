Ext.onReady(function() {
  Ext.QuickTips.init();
  Ext.Direct.addProvider(Ext.app.REMOTING_API);

  var directStore = new Ext.data.DirectStore({
    paramsAsHash: true,
    autoLoad: true,
    root: '',
    directFn: reportService.getSeasonData,
    fields: ['season', 'total']
  });

  var chart = new Ext.ux.HighChart({
    store: directStore,
    series: [
      {
        type: 'pie',
        name: 'Views',
        categorieField: 'season',
        dataField: 'total'
      }],
    chartConfig: {
      chart: {
        margin: [50, 150, 60, 80]
      },
      title: {
        text: 'ExtJs Pie',
        style: {
          margin: '10px 100px 0 0' // center it
        }
      },
      subtitle: {
        text: 'Source: sencha.com',
        style: {
          margin: '0 100px 0 0' // center it
        }
      },
      legend: {
        layout: 'vertical',
        style: {
          left: 'auto',
          bottom: 'auto',
          right: '10px',
          top: '100px'
        }
      }
    }
  });


  var graphWin = new Ext.Window({
      title: 'Resizeable Graph Window',
      resizeable: true,
      width: 500,
      height: 350,
      layout: 'fit',
      tbar: [
        {
          xtype: 'label',
          text: 'Min. value:'
        },
        {
          xtype: 'numberfield',
          id: 'minValue',
          value: 50,
          width: 40,
          allowBlank: false      
        },  
        '-',
        {
          xtype: 'label',
          text: 'Max. value:'
        },
        {
          xtype: 'numberfield',
          id: 'maxValue',
          width: 40,
          value: 250,
          allowBlank: false      
        },         
        '->',
        {
          text: 'Refresh Data',
          handler: function() {
            directStore.load({
              params: {
                minRandomValue: Ext.ComponentMgr.get('minValue').value,
                maxRandomValue: Ext.ComponentMgr.get('maxValue').value
              }
            });
          }
        }
      ],
      items: [chart]
  });
  graphWin.show();

});
