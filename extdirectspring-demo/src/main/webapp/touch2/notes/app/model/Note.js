Ext.define('NotesApp.model.Note', {
    extend: 'Ext.data.Model',
    config: {
        fields: [
           {name: 'id', type: 'int'},
           {name: 'date', type: 'date', dateFormat: 'd.m.Y'},
           {name: 'title', type: 'string'},
           {name: 'narrative', type: 'string'},
        ],
        validations: [
           { type: 'presence', field: 'id' },
           { type: 'presence', field: 'title' }
        ],
		proxy : {
			type: 'direct',
			directFn: notesService.readNotes
		}
    }
});
