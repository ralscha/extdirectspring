Ext.require('Ext.chart.*');

Ext.onReady(function () {
	Ext.direct.Manager.addProvider(Ext.app.REMOTING_API);
	
	Ext.define('SiteInfo', {
		extend : 'Ext.data.Model',
		fields : [ 'dateInMillis', 'users', 'views', 'visits' ]
	});
	
	var store = Ext.create('Ext.data.Store', {
        model: 'SiteInfo',
        proxy: {
            type: 'direct',
			directFn : chartService.getSiteInfo,
			reader : {
				root : 'records'
			}
        }
    });		
	
    var chart;
    
    var intr = setInterval(function() {
//        var gs = generateData();
//        var toDate = timeAxis.toDate,
//            lastDate = gs[gs.length - 1].date,
//            markerIndex = chart.markerIndex || 0;
//        if (+toDate < +lastDate) {
//            markerIndex = 1;
//            timeAxis.toDate = lastDate;
//            timeAxis.fromDate = Ext.Date.add(Ext.Date.clone(timeAxis.fromDate), Ext.Date.DAY, 1);
//            chart.markerIndex = markerIndex;
//        }
//        store.loadData(gs);
    	store.load(function(records, operation, success) {
    	    console.log('loaded records');
    	});
    }, 1000);

    Ext.create('Ext.Window', {
        width: 800,
        height: 600,
        hidden: false,
        maximizable: true,
        title: 'Live Animated Chart',
        renderTo: Ext.getBody(),
        layout: 'fit',
        items: [{
            xtype: 'chart',
            id: 'chartCmp',
            store: store,
            animate: true,
            axes: [{
                type: 'Numeric',
                grid: true,
                minimum: 0,
                maximum: 100,
                position: 'left',
                fields: ['views', 'visits', 'users'],
                title: 'Number of Hits',
                grid: {
                    odd: {
                        fill: '#dedede',
                        stroke: '#ddd', 'stroke-width': 0.5
                    }
                }
            }, {
                type: 'Time',
                position: 'bottom',
                fields: 'dateInMillis',
                title: 'Day',
                dateFormat: 'M d',
                groupBy: 'year,month,day',
                aggregateOp: 'sum',

                constrain: true,
                fromDate: new Date(2011, 1, 1),
                toDate: new Date(2011, 1, 7),
                grid: true
            }],
            series: [{
                type: 'line',
                axis: 'left',
                xField: 'dateInMillis',
                yField: 'visits',
                label: {
                    display: 'none',
                    field: 'visits',
                    renderer: function(v) { return v >> 0; },
                    'text-anchor': 'middle'
                },
                markerCfg: {
                    radius: 5,
                    size: 5
                }
            },{
                type: 'line',
                axis: 'left',
                xField: 'dateInMillis',
                yField: 'views',
                label: {
                    display: 'none',
                    field: 'visits',
                    renderer: function(v) { return v >> 0; },
                    'text-anchor': 'middle'
                },
                markerCfg: {
                    radius: 5,
                    size: 5
                }
            },{
                type: 'line',
                axis: 'left',
                xField: 'dateInMillis',
                yField: 'users',
                label: {
                    display: 'none',
                    field: 'visits',
                    renderer: function(v) { return v >> 0; },
                    'text-anchor': 'middle'
                },
                markerCfg: {
                    radius: 5,
                    size: 5
                }
            }]
        }]
    });
    
    chart = Ext.getCmp('chartCmp');
    var timeAxis = chart.axes.get(1);
    store.load();
});