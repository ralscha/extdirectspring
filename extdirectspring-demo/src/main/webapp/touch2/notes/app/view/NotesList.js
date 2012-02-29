Ext.define('NotesApp.view.NotesList', {
    extend: 'Ext.dataview.List',
    xtype: 'notesList',
    requires: ['NotesApp.store.Notes'],
    
    config: {
        title: 'Notes',
        grouped: true,
        itemTpl: [
           '<div>{title}</div>',
           '<div>{narrative}</div>'
        ],
        store: 'Notes',
        onItemDisclosure: true,
        items: {
			xtype: "toolbar",
			docked: "top",
			title: "My Notes",
			items: [ {
				xtype: "spacer"
			}, {
				xtype: "button",
				text: "New",
				ui: "action",
				id: "new-note-btn"
			} ]
		}
    }
});
