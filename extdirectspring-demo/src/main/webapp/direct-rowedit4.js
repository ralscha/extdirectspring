Ext.Loader.setConfig({
	enabled : true
});

Ext.Loader.setPath('Ext.ux', 'http://www.ralscha.ch/ext-4.0.0/examples/ux');

Ext.require([ 'Ext.grid.*', 
              'Ext.data.*', 
              'Ext.util.*', 
              'Ext.state.*', 
              'Ext.form.*', 
              'Ext.ux.CheckColumn' ]);

Ext.onReady(function() {
	Ext.direct.Manager.addProvider(Ext.app.REMOTING_API);

	Ext.define('Person', {
		extend : 'Ext.data.Model',
		fields : [ 'lastName', 'firstName', 'id', 'street', 'city', 'state', 'zip' ],
		proxy: {
			type: 'direct',
			api : {
				read : personAction.loadWithPaging,
				create : personAction.create,
				update : personAction.update,
				destroy : personAction.destroy
			},
			reader : {
				root : 'records'
			},
			writer : {
				root : 'records',
				allowSingle : false
			}
		}
	});

	var store2 = Ext.create('Ext.data.DirectStore', {
		autoDestroy : true,
		model : 'Person',
		root : 'records',
		idProperty : 'id',
		totalProperty : 'total',
		remoteSort : true,
		pageSize : 50,
		autoLoad : {
			start : 0,
			limit : 50
		},
		autoSync : false,
		api : {
			read : personAction.loadWithPaging,
			create : personAction.create,
			update : personAction.update,
			destroy : personAction.destroy
		}
	});

	store = Ext.create('Ext.data.Store', {
		autoDestroy : true,
		model : 'Person',
		pageSize : 50,
		autoLoad : {
			start : 0,
			limit : 50
		}/*,
		proxy : {
			type : 'direct',
			api : {
				read : personAction.loadWithPaging,
				create : personAction.create,
				update : personAction.update,
				destroy : personAction.destroy
			},
			reader : {
				root : 'records'
			},
			writer : {
				root : 'records',
				allowSingle : false
			}
		}*/
	});
	console.dir(store2);
	console.dir(store);

	var rowEditing = Ext.create('Ext.grid.plugin.RowEditing', {
		clicksToMoveEditor : 1,
		autoCancel : false
	});

	var pagingToolbar = Ext.create('Ext.toolbar.Paging', {
		store : store,
		displayInfo : true,
		displayMsg : 'Displaying persons {0} - {1} of {2}',
		emptyMsg : "No persons to display"
	});

	// create the grid and specify what field you want
	// to use for the editor at each column.
	var grid = Ext.create('Ext.grid.Panel', {
		store : store,
		columns : [ {
			header : 'Last Name',
			dataIndex : 'lastName',
			flex : 1,
			editor : {
				allowBlank : false
			}
		}, {
			header : 'First Name',
			dataIndex : 'firstName',
			editor : {
				allowBlank : false
			}
		}, {
			header : 'Street Address',
			dataIndex : 'street',
			editor : {
				allowBlank : true
			}
		}, {
			header : 'City',
			dataIndex : 'city',
			editor : {
				allowBlank : true
			}
		}, {
			header : 'State',
			dataIndex : 'state',
			editor : {
				allowBlank : true
			}
		}, {
			header : 'Zip Code',
			dataIndex : 'zip',
			editor : {
				allowBlank : true
			}
		} ],
		renderTo : Ext.getBody(),
		width : 700,
		height : 400,
		title : 'Persons',
		frame : true,
		bbar : pagingToolbar,
		tbar : [ {
			text : 'Add Person',
			iconCls : 'employee-add',
			handler : function() {
				rowEditing.cancelEdit();

				// Create a record instance through the ModelManager
				var r = Ext.ModelManager.create({
					lastName : 'New',
					firstName : 'Person',
					street : 'Street',
					city : 'City',
					state : 'State',
					zip : 'Zip'
				}, 'Person');

				store.insert(0, r);
				rowEditing.startEdit(0, 0);
			}
		}, {
			itemId : 'removePerson',
			text : 'Remove Person',
			iconCls : 'employee-remove',
			handler : function() {
				var sm = grid.getSelectionModel();
				rowEditing.cancelEdit();
				store.remove(sm.getSelection());
				sm.select(0);
			},
			disabled : true
		}, '->', {
			text : 'Sync',
			handler : function() {
				store.sync();
			}
		} ],
		plugins : [ rowEditing ],
		listeners : {
			'selectionchange' : function(view, records) {
				grid.down('#removePerson').setDisabled(!records.length);
			}
		}
	});
});