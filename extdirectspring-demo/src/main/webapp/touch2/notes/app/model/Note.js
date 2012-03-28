Ext.define("Notes.model.Note", {
	extend: 'Ext.data.Model',
	config: {
		idProperty: 'id',
		fields: [ {
			name: 'id',
			type: 'int'
		}, {
			name: 'dateCreated',
			type: 'date',
			dateFormat: 'd.m.Y'
		}, {
			name: 'title',
			type: 'string'
		}, {
			name: 'narrative',
			type: 'string'
		} ],
		validations: [ {
			type: 'presence',
			field: 'id'
		}, {
			type: 'presence',
			field: 'dateCreated'
		}, {
			type: 'presence',
			field: 'title',
			message: 'Please enter a title for this note.'
		} ],
		proxy : {
			type: 'direct',
			api: {
			    create  : undefined,
			    read    : notesService.readNotes,
			    update  : undefined,
			    destroy : undefined
			}
		}
	}
});