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
                deleteNoteCommand: 'onDeleteNoteCommand',
                backToHomeCommand: 'onBackToHomeCommand'
			}
		}
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
	
	onEditNoteCommand: function(list, record) {
		notesService.log('onEditNoteCommand');
		this.activateNoteEditor(record);	
	},
		
	onSaveNoteCommand: function() {
		notesService.log('onSaveNoteCommand');
		var noteEditor = this.getNoteEditor();
		var currentNote = noteEditor.getRecord();
		var newValues = noteEditor.getValues();
		
		currentNote.set('title', newValues.title);
		currentNote.set('narrative', newValues.narrative);
		currentNote.set('dateCreated', newValues.dateCreated);
		
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
	
    onDeleteNoteCommand: function () {
    	notesService.log("onDeleteNoteCommand");

        var noteEditor = this.getNoteEditor();
        var currentNote = noteEditor.getRecord();
        
        var notesStore = Ext.getStore('Notes');
               
        notesStore.remove(currentNote);
        notesStore.sync();

        this.activateNotesList();
    }, 

    onBackToHomeCommand: function () {
    	notesService.log("onBackToHomeCommand");
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