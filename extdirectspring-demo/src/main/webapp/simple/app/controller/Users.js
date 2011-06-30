Ext.define('Simple.controller.Users', {
	extend : 'Ext.app.Controller',

	views : [ 'user.List', 'user.Edit' ],
	stores : [ 'Users' ],
	models : [ 'User' ],
	refs : [ {
		ref : 'userList',
		selector : 'userlist'
	}, {
		ref : 'userEditForm',
		selector : 'useredit form'
	}, {
		ref : 'userEditWindow',
		selector : 'useredit'
	}],

	init : function() {
		this.control({
			'userlist' : {
				itemdblclick : this.editUser,
				itemclick : this.enableDelete
			},
			'useredit button[action=save]' : {
				click : this.updateUser
			},
			'userlist button[action=delete]' : {
				click : this.deleteUser
			}
		});
	},

	editUser : function(grid, record) {
		var view = Ext.widget('useredit');
		view.down('form').loadRecord(record);
	},

	deleteUser : function(button) {
		var record = this.getUserList().getSelectionModel().getSelection()[0];

		if (record) {
			this.getUsersStore().remove(record);
			this.getUserList().down('pagingtoolbar').doRefresh();
			this.toggleDeleteButton(false);
		}
	},

	enableDelete : function(button, record) {
		this.toggleDeleteButton(true);
	},

	toggleDeleteButton : function(enable) {
		var button = this.getUserList().down('button[action=delete]');
		if (enable) {
			button.enable();
		} else {
			button.disable();
		}
	},
	
	updateUser : function(button) {
		var record = this.getUserEditForm().getRecord(); 
		var values = this.getUserEditForm().getValues();

		record.set(values);
		this.getUserEditWindow().close();
	}
});