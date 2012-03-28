Ext.define("Notes.controller.Notes", {
	extend: 'Ext.app.Controller',
	config: {
		refs: {
			notesListContainer: 'noteslistcontainer',
			notesList: "noteslist"
		},
		control: {
			notesListContainer: {
				newNoteCommand: 'onNewNoteCommand'
			},
            notesList: {
            	editNoteCommand: 'onEditNoteCommand'
            }
		}
	},

	onNewNoteCommand: function() {
		notesService.log('onNewNoteCommand');
	},
	
	onEditNoteCommand: function() {
		notesService.log('onEditNoteCommand');
	},

	launch: function() {
		this.callParent();
		Ext.getStore('Notes').load();
		notesService.log('launch');
	},
	init: function() {
		this.callParent();
		notesService.log('init');
	}
});