Ext.Loader.setConfig({
	enabled : true
});
Ext.Loader.setPath('Ext.ux', 'http://cdn.sencha.io/ext-4.0.7-gpl/examples/ux');
Ext.require([ 'Ext.grid.*', 'Ext.data.*', 'Ext.ux.grid.FiltersFeature', 'Ext.toolbar.Paging' ]);

Ext.direct.Manager.addProvider(Ext.app.REMOTING_API);

Ext.define('Product', {
	extend : 'Ext.data.Model',
	fields : [ {
		name : 'id',
		type : 'int'
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
	} ],
	proxy : {
		type : 'direct',
		directFn : filterActionImplementation.load,
		reader : {
			root : 'records'
		}
	}
});

Ext.onReady(function() {

	Ext.QuickTips.init();

	var store = Ext.create('Ext.data.Store', {
		autoDestroy : true,
		model : 'Product',
		autoLoad : false,
		pageSize : 50
	});

	var filters = {
		ftype : 'filters',
		encode : true,
		local : false,
		filters : [ {
			type : 'boolean',
			dataIndex : 'visible'
		} ]
	};

	var createColumns = function() {

		var columns = [ {
			dataIndex : 'id',
			text : 'Id',
			filterable : true,
			width : 50
		}, {
			dataIndex : 'company',
			text : 'Company',
			id : 'company',
			flex : 1,
			filter : {
				type : 'string'
			}
		}, {
			dataIndex : 'price',
			text : 'Price',
			width : 70
		}, {
			dataIndex : 'size',
			text : 'Size',
			filter : {
				type : 'list',
				options : [ 'small', 'medium', 'large', 'extra large' ]
			}
		}, {
			dataIndex : 'date',
			text : 'Date',
			filter : true,
			renderer : Ext.util.Format.dateRenderer('m/d/Y')
		}, {
			dataIndex : 'visible',
			text : 'Visible'
		} ];

		return columns;
	};

	var grid = Ext.create('Ext.grid.Panel', {
		border : false,
		store : store,
		columns : createColumns(),
		loadMask : true,
		features : [ filters ],
		dockedItems : [ Ext.create('Ext.toolbar.Paging', {
			dock : 'bottom',
			store : store
		}) ]
	});

	grid.child('[dock=bottom]').add([ '->', {
		text : 'All Filter Data',
		tooltip : 'Get Filter Data for Grid',
		handler : function() {
			var data = Ext.encode(grid.filters.getFilterData());
			Ext.Msg.alert('All Filter Data', data);
		}
	}, {
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

	store.load({
		params : {
			dRif : '01/07/2011'
		},
		scope : this,
		callback : function(records, operation, success) {
			//console.log(records);
		}
	});
});