Ext.define('Starter.controller.Navigation', {
	extend: 'Ext.app.Controller',

	stores: [ 'Navigation' ],
	views: [ 'navigation.SideBar', 'navigation.Header', 'navigation.UserOptions' ],
	models: [ 'User' ],

	refs: [ {
		ref: 'tabpanel',
		selector: 'viewport tabpanel'
	}, {
		ref: 'navigationTree',
		selector: 'sidebar treepanel'
	}, {
		ref: 'loggedOnLabel',
		selector: 'viewport navigationheader label'
	}, {
		ref: 'userOptionsWindow',
		selector: 'useroptions'
	}, {
		ref: 'userOptionsForm',
		selector: 'useroptions form'
	} ],

	init: function() {
		this.control({
			'sidebar treepanel': {
				itemclick: this.onTreeItemClick
			},
			'tabpanel': {
				tabchange: this.syncNavigation
			},
			'navigationheader button[action=options]': {
				click: this.getUser
			},
			'useroptions button[action=save]': {
				click: this.updateUser
			}

		});
		securityService.getLoggedOnUser(this.showLoggedOnUser, this);
	},

	showLoggedOnUser: function(fullname) {
		this.getLoggedOnLabel().setText(fullname);
	},

	getPath: function(node) {
		return node.parentNode ? this.getPath(node.parentNode) + "/" + node.getId() : "/" + node.getId();
	},

	getUser: function() {
		userService.getLoggedOnUserObject(this.openOptionsWindow, this);
	},

	openOptionsWindow: function(result) {
		if (result) {
			Ext.widget('useroptions');
			var form = this.getUserOptionsForm().getForm();
			var model = this.getUserModel();
			var record = new model(result);
			form.loadRecord(record);
		}
	},

	updateUser: function(button) {
		var form = this.getUserOptionsForm(), record = form.getRecord();

		form.getForm().submit({
			params: {
				id: record ? record.data.id : ''
			},
			scope: this,
			success: function(form, action) {
				this.getUserOptionsWindow().close();
				Ext.ux.window.Notification.info(i18n.successful, i18n.user_saved);
			}
		});
	},

	onTreeItemClick: function(treeview, record, item, index, event, options) {
		var view = record.raw.view, tab = this.getTabpanel().child(view);
		if (view) {
			if (!tab) {
				tab = this.getTabpanel().add({
					xtype: view,
					iconCls: record.raw.iconCls,
					treePath: this.getPath(record),
					navigationId: record.raw.id
				});
			}
			this.getTabpanel().setActiveTab(tab);
		}
	},

	syncNavigation: function() {
		var activeTab = this.getTabpanel().getActiveTab();
		var selectionModel = this.getNavigationTree().getSelectionModel();
		this.getNavigationTree().expandPath(activeTab.treePath);

		var activeTabId = activeTab.navigationId;
		var selection = selectionModel.getLastSelected();
		var currentId = selection && selection.raw.id;

		if (activeTabId !== currentId) {
			selectionModel.select(this.getNavigationTree().getStore().getNodeById(activeTabId));
		}
	}

});
