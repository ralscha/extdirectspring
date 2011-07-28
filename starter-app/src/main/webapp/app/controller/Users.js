Ext.define('Starter.controller.Users', {
	extend: 'Ext.app.Controller',

	views: [ 'user.List', 'user.Edit' ],
	stores: [ 'Users', 'Roles' ],
	models: [ 'User', 'Role' ],
	refs: [ {
		ref: 'userList',
		selector: 'userlist'
	}, {
		ref: 'userEditForm',
		selector: 'useredit form'
	}, {
		ref: 'userEditWindow',
		selector: 'useredit'
	} ],

	init: function() {
		this.control({
			'userlist': {
				itemdblclick: this.editUserFromDblClick,
				itemclick: this.enableActions,
				beforeactivate: this.onBeforeActivate,
			},
			'useredit button[action=save]': {
				click: this.updateUser
			},
			'userlist button[action=add]': {
				click: this.createUser
			},
			'userlist button[action=edit]': {
				click: this.editUserFromButton
			},
			'userlist button[action=delete]': {
				click: this.deleteUser
			},
			'userlist textfield': {
				filter: this.handleFilter,
			}
		});
	},

	handleFilter: function(field, newValue) {
		var myStore = this.getUsersStore();
		if (newValue) {
			myStore.remoteFilter = false;
			myStore.clearFilter(true);
			myStore.remoteFilter = true;
			myStore.filter('filter', newValue);
		} else {
			myStore.clearFilter();
		}
	},

	editUserFromDblClick: function(grid, record) {
		this.editUser(record);
	},

	editUserFromButton: function() {
		this.editUser(this.getUserList().getSelectionModel().getSelection()[0]);
	},

	editUser: function(record) {
		Ext.widget('useredit');

		var form = this.getUserEditForm().getForm();
		form.loadRecord(record);
		form.setValues({
			'roleIds': Ext.Array.map(record.raw.roles, function(item) {
				return item.id;
			})
		});
	},

	createUser: function() {
		Ext.widget('useredit');
		this.getUserEditForm().getForm().isValid();
	},

	deleteUser: function(button) {
		var record = this.getUserList().getSelectionModel().getSelection()[0];
		if (record) {
			Ext.Msg.confirm(i18n.user_delete+'?', i18n.delete_confirm + ' ' + record.data.name,
					this.afterConfirmDeleteUser, this);
		}
	},

	afterConfirmDeleteUser: function(btn) {
		if (btn === 'yes') {
			var record = this.getUserList().getSelectionModel().getSelection()[0];
			if (record) {
				this.getUsersStore().remove(record);
				this.getUsersStore().sync();
				this.doGridRefresh();
				this.toggleDeleteButton(false);
				this.toggleEditButton(false);
				Starter.component.Notification.info(i18n.successful, i18n.user_deleted);
			}
		}
	},

	enableActions: function(button, record) {
		this.toggleDeleteButton(true);
		this.toggleEditButton(true);
	},

	toggleDeleteButton: function(enable) {
		var button = this.getUserList().down('button[action=delete]');
		if (enable) {
			button.enable();
		} else {
			button.disable();
		}
	},

	toggleEditButton: function(enable) {
		var button = this.getUserList().down('button[action=edit]');
		if (enable) {
			button.enable();
		} else {
			button.disable();
		}
	},

	updateUser: function(button) {
		var form = this.getUserEditForm(), record = form.getRecord();

		form.getForm().submit({
			params: {
				id: record ? record.data.id : ''
			},
			scope: this,
			success: function(form, action) {
				this.doGridRefresh();
				this.getUserEditWindow().close();
				Starter.component.Notification.info(i18n.successful, i18n.user_saved);
			}
		});
	},

	onBeforeActivate: function(cmp, options) {
		if (options) {
			this.doGridRefresh();
		}
	},

	doGridRefresh: function() {
		this.getUserList().down('pagingtoolbar').doRefresh();
	}

});