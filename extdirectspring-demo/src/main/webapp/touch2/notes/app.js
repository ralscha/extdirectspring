Ext.require(['Ext.direct.Manager','Ext.direct.RemotingProvider', 'Ext.data.proxy.Direct'], function() {
	Ext.direct.Manager.addProvider(Ext.app.REMOTING_API);
});

Ext.application({
    name: 'NotesApp',
    views: ['Main', 'NotesList'],
    stores: ['Notes'],
    models: ['Note'],
    controllers: ['Notes'],
    
    launch: function() {
        Ext.Viewport.add({
            xtype: 'mainpanel'
        });
    }
});