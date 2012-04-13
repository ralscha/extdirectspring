Ext.require([ 'Ext.window.Window', 'Ext.chart.*' ]);

Ext.onReady(function() {
	Ext.direct.Manager.addProvider(Ext.app.REMOTING_API);

	Ext.define('SiteInfo', {
		extend: 'Ext.data.Model',
		fields: [ {
			name: 'date',
			type: 'date',
			dateFormat: 'Y-m-d'
		}, 'visits', 'views', 'veins' ],
		proxy: {
			type: 'direct',
			directFn: chartService.getSiteInfo
		}
	});

	var store = Ext.create('Ext.data.Store', {
		model: 'SiteInfo'
	});

	Ext.create('Ext.Window', {
		width: 800,
		height: 600,
		minHeight: 400,
		minWidth: 550,
		maximizable: true,
		title: 'Live Updated Chart',
		layout: 'fit',
		items: [ {
			xtype: 'chart',
			style: 'background:#fff',
			store: store,
			animate: true,
			id: 'chartCmp',
			axes: [ {
				type: 'Numeric',
				grid: true,
				minimum: 0,
				maximum: 100,
				position: 'left',
				fields: [ 'views', 'visits', 'veins' ],
				title: 'Number of Hits',
				grid: {
					odd: {
						fill: '#dedede',
						stroke: '#ddd',
						'stroke-width': 0.5
					}
				}
			}, {
				type: 'Time',
				position: 'bottom',
				fields: 'date',
				title: 'Day',
				dateFormat: 'M d',
				groupBy: 'year,month,day',
				aggregateOp: 'sum'
			} ],
			series: [ {
				type: 'line',
				axis: [ 'left', 'bottom' ],
				xField: 'date',
				yField: 'visits',
				label: {
					display: 'none',
					field: 'visits',
					renderer: function(v) {
						return v >> 0;
					},
					'text-anchor': 'middle'
				},
				markerConfig: {
					radius: 5,
					size: 5
				}
			}, {
				type: 'line',
				axis: [ 'left', 'bottom' ],
				xField: 'date',
				yField: 'views',
				label: {
					display: 'none',
					field: 'visits',
					renderer: function(v) {
						return v >> 0;
					},
					'text-anchor': 'middle'
				},
				markerConfig: {
					radius: 5,
					size: 5
				}
			}, {
				type: 'line',
				axis: [ 'left', 'bottom' ],
				xField: 'date',
				yField: 'veins',
				label: {
					display: 'none',
					field: 'visits',
					renderer: function(v) {
						return v >> 0;
					},
					'text-anchor': 'middle'
				},
				markerConfig: {
					radius: 5,
					size: 5
				}
			} ]
		} ]
	}).show();
	chart = Ext.getCmp('chartCmp');
	var timeAxis = chart.axes.get(1);

	store.load(function() {
		updateTimeAxis();
		timeAxis.constrain = true;
		chart.refresh();
	});

	setInterval(function() {
		store.load(function() {
			updateTimeAxis();
			chart.refresh();
		});
	}, 1000);

	var updateTimeAxis = function() {
		timeAxis.fromDate = store.first().data.date;
		if (store.data.getCount() < 7) {
			timeAxis.toDate = Ext.Date.add(Ext.Date.clone(timeAxis.fromDate), Ext.Date.DAY, 6);
		} else {
			timeAxis.toDate = store.last().data.date;
		}
	};

});