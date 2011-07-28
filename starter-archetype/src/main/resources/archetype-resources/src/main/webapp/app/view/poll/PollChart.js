Ext.define('Starter.view.poll.PollChart', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.pollchart',

	title: i18n.chart_title,

	layout: 'fit',
	closable: true,

	requires: [ 'Ext.chart.*' ],

	initComponent: function() {

		var store = Ext.StoreManager.get('PollChart');
		if (!store.getCount()) {
			for ( var i = 0; i < 20; i++) {
				store.add([ {
					"time": "00:00:00",
					"points": 0
				} ]);
			}
		}

		this.dockedItems = [ {
			xtype: 'toolbar',
			items: [ {
				text: i18n.chart_stop,
				iconCls: 'icon-stop',
				action: 'control'
			} ]
		} ];

		this.items = [ {
			xtype: 'chart',
			animate: true,
			shadow: true,
			store: store,
			height: 600,
			axes: [ {
				type: 'Numeric',
				position: 'left',
				fields: [ 'points' ],
				maximum: 2000,
				minimum: 0,
				// majorTickSteps: 500,
				// minorTickSteps: 100,
				title: i18n.chart_points,
				label: {
					renderer: Ext.util.Format.numberRenderer('0')
				},
				grid: {
					odd: {
						opacity: 0.5,
						fill: '#eee',
						stroke: '#bbb',
						'stroke-width': 0.5
					}
				}
			}, {
				type: 'Category',
				position: 'bottom',
				fields: [ 'time' ],
				title: i18n.chart_pollingtime
			} ],
			series: [ {
				type: 'line',
				highlight: {
					size: 7,
					radius: 7
				},
				tips: {
					width: 130,
					renderer: function(storeItem, item) {
						this.setTitle(i18n.chart_received + ' ' + storeItem.get('points') + ' '+i18n.chart_pointsat+' ' + storeItem.get('time'));
					}
				},
				axis: 'left',
				xField: 'time',
				yField: 'points',
				markerConfig: {
					type: 'circle',
					size: 4,
					radius: 4,
					'stroke-width': 0,
					fill: 'red'
				}
			} ]
		} ];

		this.callParent(arguments);
	}
});
