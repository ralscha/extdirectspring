Ext.Loader.setConfig({
	enabled : true
});

Ext.Loader.setPath('Ext.ux', '../ux/');
Ext.require([ 'Ext.grid.*', 
              'Ext.data.*', 
              'Ext.util.*',
		      'Ext.grid.PagingScroller' ]);

Ext.onReady(function() {

	Ext.direct.Manager.addProvider(Ext.app.REMOTING_API);

	Ext.define('Address', {
		extend : 'Ext.data.Model',
		fields : [ 'lastName', 'firstName', 'id', 'street', 'city', 'state', 'zip' ]
	});

	// create the Data Store
	var store = Ext.create('Ext.data.Store', {
		id : 'store',
		pageSize : 200,
		model : 'Address',
		remoteSort : true,
        // allow the grid to interact with the paging scroller by buffering
        buffered: true,
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

	var grid = Ext.create('Ext.grid.Panel', {
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
		columns : [ {
			xtype : 'rownumberer',
			width : 50,
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

    // Load a maximum of 200 records into the prefetch buffer (which is NOT mapped to the UI)
    // When that has completed, instruct the Store to load the first page from prefetch into the live, mapped record cache
    store.prefetch({
        start: 0,
        limit: 199,
        callback: function() {
            store.guaranteeRange(0, 199);
        }
    });
});
