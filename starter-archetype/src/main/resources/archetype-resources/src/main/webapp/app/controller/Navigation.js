Ext.define('Starter.controller.Navigation', {
	extend: 'Ext.app.Controller',

	stores: [ 'Navigation' ],
	views: [ 'navigation.SideBar', 'navigation.Header' ],

	refs: [ {
		ref: 'tabpanel',
		selector: 'viewport tabpanel'
	}, {
		ref: 'navigationTree',
		selector: 'sidebar treepanel'
	}, {
		ref: 'loggedOnLabel',
		selector: 'viewport navigationheader label'
	} ],

	init: function() {
		this.control({
			'sidebar treepanel': {
				itemclick: this.onTreeItemClick
			},
			'tabpanel': {
				tabchange: this.syncNavigation
			}
		});
		securityService.getLoggedOnUser(this.showLoggedOnUser, this);
	},

	showLoggedOnUser: function(fullname) {
		this.getLoggedOnLabel().setText(i18n.login_loggedon + ': ' + fullname);
	},

	getPath: function(node) {
		return node.parentNode ? this.getPath(node.parentNode) + "/" + node.getId() : "/" + node.getId();
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
