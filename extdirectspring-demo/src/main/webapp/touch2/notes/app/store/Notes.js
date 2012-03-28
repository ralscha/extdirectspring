Ext.define("Notes.store.Notes", {
	extend: 'Ext.data.Store',
	config: {
		model: 'Notes.model.Note',
		sorters: [ {
			property: 'dateCreated',
			direction: 'DESC'
		} ]
	}
});