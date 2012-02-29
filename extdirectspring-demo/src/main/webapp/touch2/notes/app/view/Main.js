Ext.define('NotesApp.view.Main', {
    extend: 'Ext.navigation.View',
    xtype: 'mainpanel',
    requires: [
        'NotesApp.view.NotesList'
    ],

    config: {
        items: [{
            xtype: 'notesList'
        }]
    }
});