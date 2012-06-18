Ext.define("Notes.store.Notes", {
	extend: 'Ext.data.Store',
	config: {
		model: 'Notes.model.Note',
		sorters: [ {
			property: 'dateCreated',
			direction: 'DESC'
		} ],
		grouper: {
			sortProperty: "dateCreated",
			direction: "DESC",
			groupFn: function(record) {
				if (record && record.data.dateCreated) {
					return record.data.dateCreated.toDateString();
				} else {
					return '';
				}
			}
		}
	}
});