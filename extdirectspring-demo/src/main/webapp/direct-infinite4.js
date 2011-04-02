Ext.Loader.setConfig({
	enabled : true
});

Ext.Loader.setPath('Ext.ux', '../ux/');
Ext.require([ 'Ext.grid.*', 
              'Ext.data.*', 
              'Ext.data.BufferStore', 
              'Ext.util.*',
		      'Ext.grid.PagingScroller' ]);

Ext.onReady(function() {

	Ext.direct.Manager.addProvider(Ext.app.REMOTING_API);

	Ext.define('Address', {
		extend : 'Ext.data.Model',
		fields : [ 'lastName', 'firstName', 'id', 'street', 'city', 'state', 'zip' ]
	});

	// create the Data Store
	var store = new Ext.data.BufferStore({
		id : 'store',
		pageSize : 200,
		model : 'Address',
		remoteSort : true,
		proxy : {
			type : 'direct',
			directFn : personAction.loadWithPaging,
			reader : {
				root : 'records',
				totalProperty : 'total'
			}
		},
		sorters : [ {
			property : 'lastName',
			direction : 'ASC'
		} ]
	});

	var grid = new Ext.grid.GridPanel({
		width : 700,
		height : 500,
		title : 'Some random data',
		store : store,
		verticalScrollerType : 'paginggridscroller',
		loadMask : true,
		disableSelection : true,
		invalidateScrollerOnRefresh : false,
		viewConfig : {
			trackOver : false
		},
		// grid columns
		headers : [ {
			xtype : 'rownumberer',
			width : 40,
			sortable : false
		}, {
			text : "Last Name",
			dataIndex : 'lastName',
			sortable : true
		}, {
			text : "First Name",
			dataIndex : 'firstName',
			sortable : true			
		}, {
			text : "Street Address",
			dataIndex : 'street',
			sortable : true			
		}, {
			text : "City",
			dataIndex : 'city',
			sortable : true			
		}, {
			text : "State",
			dataIndex : 'state',
			sortable : true			
		}, {
			text : "Zip Code",
			dataIndex : 'zip',
			sortable : true			
		}],
		renderTo : Ext.getBody()
	});

	// trigger the data store load
	store.guaranteeRange(0, 199);
});
