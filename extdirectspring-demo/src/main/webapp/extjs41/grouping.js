Ext.require([ 'Ext.data.*', 'Ext.grid.*' ]);
Ext.onReady(function() {

	Ext.direct.Manager.addProvider(Ext.app.REMOTING_API);

	Ext.define('Restaurant', {
		extend: 'Ext.data.Model',
		fields: [ 'name', 'cuisine' ],
		proxy: {
			type: 'direct',
			directFn: restaurantService.getRestaurants
		}
	});

	var Restaurants = Ext.create('Ext.data.Store', {
		storeId: 'restaraunts',
		model: 'Restaurant',
		sorters: [ 'cuisine', 'name' ],
		groupField: 'cuisine',
		autoLoad: true,
		remoteGroup: true,
		remoteSort: true
	});

	var groupingFeature = Ext.create('Ext.grid.feature.Grouping', {
		groupHeaderTpl: 'Cuisine: {name} ({rows.length} Item{[values.rows.length > 1 ? "s" : ""]})'
	});

	var grid = Ext.create('Ext.grid.Panel', {
		renderTo: Ext.getBody(),
		collapsible: true,
		iconCls: 'icon-grid',
		frame: true,
		store: Restaurants,
		width: 600,
		height: 400,
		title: 'Restaurants',
		features: [ groupingFeature ],
		columns: [ {
			text: 'Name',
			flex: 1,
			dataIndex: 'name'
		}, {
			text: 'Cuisine',
			flex: 1,
			dataIndex: 'cuisine'
		} ],
		fbar: [ '->', {
			text: 'Clear Grouping',
			iconCls: 'icon-clear-group',
			handler: function() {
				groupingFeature.disable();
			}
		} ]
	});
});