Ext.define('Starter.view.user.List', {
	extend: 'Ext.grid.Panel',
	alias: 'widget.userlist',
	store: 'Users',

	title: i18n.user_users,
	closable: true,
	
	requires: ['Starter.component.FilterField'],

	columns: [ {
		header: 'ID',
		dataIndex: 'id',
		width: 50,
		sortable: false
	}, {
		header: i18n.user_username,
		dataIndex: 'userName',
		flex: 1
	}, {
		header: i18n.user_firstname,
		dataIndex: 'firstName',
		flex: 1
	}, {
		header: i18n.user_lastname,
		dataIndex: 'name',
		flex: 1
	}, {
		header: i18n.user_email,
		dataIndex: 'email',
		flex: 1
	}, {
		header: i18n.user_enabled,
		dataIndex: 'enabled',
		width: 70,
		renderer: function(value) {
			if (value === true) {
				return i18n.yes;
			}
			return '';
		}
	} ],

	initComponent: function() {

		this.dockedItems = [ {
			xtype: 'toolbar',
			dock: 'top',
			items: [ {
				text: i18n.user_new,
				disabled: false,
				action: 'add',
				iconCls: 'icon-user-add'
			}, {
				text: i18n.user_edit,
				disabled: true,
				action: 'edit',
				iconCls: 'icon-user-edit'
			}, {
				text: i18n.user_delete,
				disabled: true,
				action: 'delete',
				iconCls: 'icon-user-delete'
			}, '-', {
				text: i18n.user_excelexport,
				action: 'export',
				iconCls: 'icon-excel',
				href: 'usersExport.xls',				
				target: '_self',
			}, '->', {
				fieldLabel: i18n.filter,
				labelWidth: 40,
				xtype: 'filterfield'				
			} ]
		}, {
			xtype: 'pagingtoolbar',
			dock: 'bottom',
			store: 'Users',
			displayInfo: true,
			displayMsg: i18n.user_display,
			emptyMsg: i18n.user_no
		} ];

		this.callParent(arguments);

	}

});