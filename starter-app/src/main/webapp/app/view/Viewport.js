Ext.define('Starter.view.Viewport', {
	extend: 'Ext.Viewport',
	id: 'viewport',

	layout: {
		type: 'border',
		padding: 5
	},
	defaults: {
		split: true
	},

	requires: [ 'Ext.ux.TabReorderer', 'Ext.ux.TabCloseMenu' ],

	initComponent: function() {
		var me = this;

		var tabCloseMenu = Ext.create('Ext.ux.TabCloseMenu');
		tabCloseMenu.closeTabText = i18n.tabclosemenu_close;
		tabCloseMenu.closeOthersTabsText = i18n.tabclosemenu_closeother;
		tabCloseMenu.closeAllTabsText = i18n.tabclosemenu_closeall;

		me.items = [ {
			region: 'north',
			xtype: 'navigationheader',
			split: false
		}, {
			region: 'center',
			xtype: 'tabpanel',
			plugins: [ Ext.create('Ext.ux.TabReorderer'), tabCloseMenu ],
			plain: true
		}, {
			region: 'west',
			width: 180,
			xtype: 'sidebar'
		} ];

		me.callParent(arguments);
	}

});