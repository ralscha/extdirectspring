Ext.define("NotesApp.view.NotesListContainer", {
    extend: "Ext.Container",   
    xtype: 'notesListContainer',
    layout: 'fit',
    config: {
        items: [{
            xtype: "toolbar",
            docked: "top",
            title: "My Notes",
            items: [{
                xtype: "spacer"
            }, {
                xtype: "button",
                text: "New",
                ui: "action",
                id:"new-note-btn"
            }]
        }, { xtype: 'notesList' } ]
    }
});