/*
Ext.Loader.setPath({
    'Ext': '../../touch/src'
});
*/

Ext.require(['Ext.direct.Manager','Ext.direct.RemotingProvider', 'Ext.data.proxy.Direct'], function() {
	Ext.direct.Manager.addProvider(Ext.app.REMOTING_API);
});


Ext.application({
    name: 'Notes',

    requires: [
        'Ext.MessageBox'
    ],

    models: ['Note'],
    stores: ['Notes'],
    controllers: ['Notes'],
    views: ['NotesList', 'NotesListContainer'],
    
    icon: {
        57: 'resources/icons/Icon.png',
        72: 'resources/icons/Icon~ipad.png',
        114: 'resources/icons/Icon@2x.png',
        144: 'resources/icons/Icon~ipad@2x.png'
    },
    
    phoneStartupScreen: 'resources/loading/Homescreen.jpg',
    tabletStartupScreen: 'resources/loading/Homescreen~ipad.jpg',

    launch: function() {
        // Destroy the #appLoadingIndicator element
        Ext.fly('appLoadingIndicator').destroy();

        notesService.log('App Launch');
        Ext.Viewport.add({xtype: 'noteslistcontainer'});
    },

    onUpdated: function() {
        Ext.Msg.confirm(
            "Application Update",
            "This application has just successfully been updated to the latest version. Reload now?",
            function() {
                window.location.reload();
            }
        );
    }
});
