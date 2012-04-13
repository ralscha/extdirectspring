Ext.define("Notes.controller.Notes", {
	extend: 'Ext.app.Controller',
	config: {
		refs: {
			notesListContainer: 'noteslistcontainer',
			noteEditor: 'noteeditor'
		},
		control: {
			notesListContainer: {
				newNoteCommand: 'onNewNoteCommand',
				editNoteCommand: 'onEditNoteCommand'
			},
			noteEditor: {
				saveNoteCommand: 'onSaveNoteCommand',
				homeCommand: 'onHomeCommand'
			}
		}
	},

	onNewNoteCommand: function() {		
		notesService.log('onNewNoteCommand');
		var newNote = Ext.create('Notes.model.Note', {
			id: -1,
			dateCreated: new Date(),
			title: '',
			narrative: ''
		});
		
		this.activateNoteEditor(newNote);		
	},
	
	slideLeftTransition: {
		type: 'slide',
		direction: 'left'
	},
	
	slideRightTransition: {
		type: 'slide',
		direction: 'right'
	},	
	
	activateNoteEditor: function(record) {
		var noteEditor = this.getNoteEditor();
		noteEditor.setRecord(record);
		Ext.Viewport.animateActiveItem(noteEditor, this.slideLeftTransition);
	},
	
	activateNotesList: function(record) {		
		Ext.Viewport.animateActiveItem(this.getNotesListContainer(), this.slideRightTransition);
	},	
	
	onEditNoteCommand: function(list, record) {
		notesService.log('onEditNoteCommand');
		this.activateNoteEditor(record);	
	},
	
	onHomeCommand: function() {
		this.activateNotesList();	
	},
	
	onSaveNoteCommand: function() {
		notesService.log('onSaveNoteCommand');
		var noteEditor = this.getNoteEditor();
		var currentNote = noteEditor.getRecord();
		var newValues = noteEditor.getValues();
		
		currentNote.set('title', newValues.title);
		currentNote.set('narrative', newValues.narrative);
		
		var errors = currentNote.validate();
		if (!errors.isValid()) {
			Ext.Msg.alert('Wait!', errors.getByField('title')[0].getMessage(), Ext.emptyFn);
			currentNote.reject();
			return;
		}
		
		var notesStore = Ext.getStore('Notes');

		if (currentNote.data.id === -1) {
			notesStore.add(currentNote);
		}
		
		notesStore.sync();
		notesStore.sort([{property: 'dateCreated', direction: 'DESC'}]);
		
		this.activateNotesList();
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