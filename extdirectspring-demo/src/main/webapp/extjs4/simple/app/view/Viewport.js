Ext.define('Simple.view.Viewport', {
	extend : 'Ext.Viewport',
	id : 'viewport',

	layout : 'fit',

	initComponent : function() {
		this.items = [ {
			xtype : 'userlist'
		} ];

		this.callParent(arguments);
	}

});