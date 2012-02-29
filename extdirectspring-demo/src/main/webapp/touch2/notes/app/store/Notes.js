Ext.define('NotesApp.store.Notes', {
    extend: 'Ext.data.Store',
    
    config: {
        model: 'NotesApp.model.Note',
        autoLoad: true,
        sorters: {property: 'date', direction: 'DESC'},
        grouper : function(record) {
            return Ext.Date.format(record.data.date, 'd.m.Y');
        }
    }
});
