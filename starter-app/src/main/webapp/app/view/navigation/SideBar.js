Ext.define('Starter.view.navigation.SideBar', {
	alias: 'widget.sidebar',
	extend: 'Ext.panel.Panel',
	stateId: 'sidebar',

	title: i18n.navigation,
	collapsible: true,
	layout: 'fit',
	minWidth: 100,
	maxWidth: 200,		
		
	initComponent: function() {
		var me = this;
		me.items = [ {
			xtype: 'treepanel',
			border: 0,
	        store: 'Navigation',
	        rootVisible: false,
	        animate: false
		} ];

		me.callParent(arguments);

	}
});