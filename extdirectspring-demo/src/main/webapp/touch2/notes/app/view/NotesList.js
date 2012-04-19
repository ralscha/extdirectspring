Ext.define('Notes.view.NotesList', {
    extend: 'Ext.dataview.List',
    alias: 'widget.noteslist',
    config: {
        loadingText: 'Loading Notes...',
        emptyText: '<div class=\"notes-list-empty-text\">No notes found.</div>',
        onItemDisclosure: true,
        grouped: true,
        itemTpl: '<div class=\"list-item-title\">{id}: {title}</div><div class=\"list-item-narrative\">{narrative}</div>'     
    }
});