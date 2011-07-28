Ext.define('Starter.view.navigation.SideBar', {
	alias: 'widget.sidebar',
	extend: 'Ext.panel.Panel',

	title: i18n.navigation,
	collapsible: true,
	layout: 'fit',
	minWidth: 100,
	maxWidth: 200,		
		
	initComponent: function() {
		this.items = [ {
			xtype: 'treepanel',
			border: 0,
	        store: 'Navigation',
	        rootVisible: false,
	        animate: false
		} ];

		this.callParent(arguments);

	}
});