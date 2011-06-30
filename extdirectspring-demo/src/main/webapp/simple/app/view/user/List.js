Ext.define('Simple.view.user.List', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.userlist',
	store : 'Users',
	
	title : 'Usermanagement',

	columns : [{
		header : 'ID',
		dataIndex : 'id',
		width : 50,
		sortable : false
    }, {
		header : 'First Name',
		dataIndex : 'firstName',
		flex : 1
	}, {
		header : 'Last Name',
		dataIndex : 'lastName',
		flex : 1
	}, {
		header : 'Email',
		dataIndex : 'email',
		flex : 1
	}, {
		header : 'City',
		dataIndex : 'city',
		flex : 1
	} ],

	dockedItems : [ {
		xtype : 'toolbar',
		dock : 'top',
		items : [ {
			text : 'New User',
			disabled : false,
			action : 'add',
			iconCls : 'icon-add'
		}, {
			text : 'Delete User',
			disabled : true,
			action : 'delete',
			iconCls : 'icon-delete'
		} ]
	}, {
		xtype : 'pagingtoolbar',
		dock : 'bottom',
		store : 'Users',
		displayInfo : true,
		displayMsg : 'Displaying Users {0} - {1} of {2}',
		emptyMsg : 'No Users to display'
	} ]

});