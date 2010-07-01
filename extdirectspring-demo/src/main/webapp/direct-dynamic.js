Ext.onReady(function() {
      Ext.QuickTips.init();

      Ext.Direct.addProvider({
            id: 'messageProvider',
            type: 'polling',
            interval: 1000,
            url: Ext.app.POLLING_URLS.newData
          });

      var chart = new Ext.ux.HighChart({

            chartConfig: {
              chart: {
                defaultSeriesType: 'spline',
                margin: [50, 50, 60, 80]
              }
            },
            title: {
              text: 'Live random data',
              style: {
                margin: '10px 10px 0 0' // center it
              }
            },
            xAxis: {
              type: 'datetime',
              tickPixelInterval: 150
            },
            yAxis: {
              title: {
                text: 'Value'
              },
              plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                  }]
            },
            legend: {
              layout: 'vertical',
              style: {
                left: 'auto',
                bottom: 'auto',
                right: '10px',
                top: '100px'
              }
            },
            series: [{
                  name: 'Random data 1',
                  data: (function() {
                    // generate an array of random data
                    var data = [], time = (new Date()).getTime(), i;
                    for (i = -19; i <= 0; i++) {
                      data.push({
                            x: time + i * 1000,
                            y: Math.random()
                          });
                    }
                    return data;
                  })()
                },
                {
                  name: 'Random data 2',
                  data: (function() {
                    // generate an array of random data
                    var data = [], time = (new Date()).getTime(), i;
                    for (i = -19; i <= 0; i++) {
                      data.push({
                            x: time + i * 1000,
                            y: Math.random()
                          });
                    }
                    return data;
                  })()
                }                
                ]
          });

      var graphWin = new Ext.Window({
            closable: false,
            title: 'Dynamic update',
            resizeable: true,
            width: 600,
            height: 350,
            layout: 'fit',
            items: [chart]
          });
      graphWin.show();

      Ext.Direct.on({
            newData: function(e) {
              var x = (new Date()).getTime();
              chart.chart.series[0].addPoint([x, e.data.x], true, true);
              chart.chart.series[1].addPoint([x, e.data.y], true, true);
            }
          });

    });
