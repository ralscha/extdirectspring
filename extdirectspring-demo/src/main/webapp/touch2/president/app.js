Ext.require(['Ext.direct.Manager','Ext.direct.RemotingProvider', 'Ext.data.proxy.Direct'], function() {
	Ext.direct.Manager.addProvider(Ext.app.REMOTING_API);
});

Ext.application({
    name: 'Sencha',

    controllers: ['Main'],
    views: ['Main'],
    stores: ['Presidents'],
    models: ['President'],
       
    launch: function() {
        Ext.Viewport.add({
            xtype: 'mainpanel'
        });
    }
});