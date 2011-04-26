Ext.Loader.setConfig({
	enabled : true
});
Ext.Loader.setPath('Ext.ux', 'http://www.ralscha.ch/ext-4.0.0/examples/ux');
Ext.require([ 'Ext.grid.*', 
              'Ext.data.*', 
              'Ext.ux.grid.FiltersFeature',
		      'Ext.toolbar.Paging' ]);

Ext.onReady(function() {
	Ext.direct.Manager.addProvider(Ext.app.REMOTING_API);
	Ext.QuickTips.init();

	var store = Ext.create('Ext.data.DirectStore', {
		autoDestroy : true,

		directFn: filterAction.load,
		root : 'records',
		idProperty : 'id',
		totalProperty : 'total',

		remoteSort : true,
		sortInfo : {
			field : 'company',
			direction : 'ASC'
		},
		pageSize : 50,
		storeId : 'myStore',

		fields : [ {
			name : 'id'
		}, {
			name : 'company'
		}, {
			name : 'price',
			type : 'float'
		}, {
			name : 'date',
			type : 'date',
			dateFormat : 'Y-m-d'
		}, {
			name : 'visible',
			type : 'boolean'
		}, {
			name : 'size'
		} ]
	});

	var filters = {
		ftype : 'filters',
		encode : true, 
		local : false, 
		filters : [ {
			type : 'numeric',
			dataIndex : 'id'
		}, {
			type : 'string',
			dataIndex : 'company'
		}, {
			type : 'numeric',
			dataIndex : 'price'
		}, {
			type : 'date',
			dataIndex : 'date'
		}, {
			type : 'list',
			dataIndex : 'size',
			options : [ 'small', 'medium', 'large', 'extra large' ],
			phpMode : true
		}, {
			type : 'boolean',
			dataIndex : 'visible'
		} ]
	};

	// use a factory method to reduce code while demonstrating
	// that the GridFilter plugin may be configured with or without
	// the filter types (the filters may be specified on the column
	// model
	var createColumns = function() {

		var columns = [ {
			dataIndex : 'id',
			text : 'Id',
			// instead of specifying filter config just specify
			// filterable=true
			// to use store's field's type property (if type property
			// not
			// explicitly specified in store config it will be 'auto'
			// which
			// GridFilters will assume to be 'StringFilter'
			filterable : true
		// ,filter: {type: 'numeric'}
		}, {
			dataIndex : 'company',
			text : 'Company',
			id : 'company',
			flex : 1,
			filter : {
				type : 'string'
			// specify disabled to disable the filter menu
			// , disabled: true
			}
		}, {
			dataIndex : 'price',
			text : 'Price',
			filter : {
			// type: 'numeric' // specify type here or in store fields
			// config
			}
		}, {
			dataIndex : 'size',
			text : 'Size',
			filter : {
				type : 'list',
				options : [ 'small', 'medium', 'large', 'extra large' ]
			// ,phpMode: true
			}
		}, {
			dataIndex : 'date',
			text : 'Date',
			renderer : Ext.util.Format.dateRenderer('m/d/Y'),
			filter : {
			// type: 'date' // specify type here or in store fields
			// config
			}
		}, {
			dataIndex : 'visible',
			text : 'Visible',
			filter : {
			// type: 'boolean' // specify type here or in store fields
			// config
			}
		} ];

		return columns;
	};

	var grid = Ext.create('Ext.grid.Panel', {
		border : false,
		store : store,
		columns : createColumns(),
		loadMask : true,
		features : [ filters ],
		bbar : Ext.create('Ext.toolbar.Paging', {
			store : store
		})
	});

	// add some buttons to bottom toolbar just for demonstration
	// purposes
	grid.child('[dock=bottom]')
		.add([
		'->',
		{
			text : 'All Filter Data',
			tooltip : 'Get Filter Data for Grid',
			handler : function() {
				var data = Ext.encode(grid.filters.getFilterData());
				Ext.Msg.alert('All Filter Data', data);
			}
		},
		{
			text : 'Clear Filter Data',
			handler : function() {
				grid.filters.clearFilters();
			}
		} ]);

	var win = Ext.create('Ext.Window', {
		title : 'Grid Filters Example',
		height : 400,
		width : 700,
		layout : 'fit',
		items : grid
	}).show();

	store.load();
});