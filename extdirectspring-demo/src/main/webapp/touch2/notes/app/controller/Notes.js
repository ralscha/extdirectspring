Ext.define("Notes.controller.Notes", {
	extend: 'Ext.app.Controller',
	config: {
		refs: {
			notesListView: "noteslistview",
			noteEditorView: "noteeditorview",
			notesList: "#notesList"
		},
		control: {
			notesListView: {
				newNoteCommand: 'onNewNoteCommand',
				editNoteCommand: 'onEditNoteCommand'
			},
			noteEditorView: {
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
		var noteEditorView = this.getNoteEditorView();
		noteEditorView.setRecord(record);
		Ext.Viewport.animateActiveItem(noteEditorView, this.slideLeftTransition);
	},
	activateNotesList: function() {
		Ext.Viewport.animateActiveItem(this.getNotesListView(), this.slideRightTransition);
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
		var noteEditorView = this.getNoteEditorView();

		var currentNote = noteEditorView.getRecord();
		var newValues = noteEditorView.getValues();

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

		notesStore.sort([ {
			property: 'dateCreated',
			direction: 'DESC'
		} ]);

		this.activateNotesList();
	},
	onDeleteNoteCommand: function() {
		notesService.log("onDeleteNoteCommand");

		var noteEditorView = this.getNoteEditorView();
		var currentNote = noteEditorView.getRecord();
		var notesStore = Ext.getStore("Notes");

		notesStore.remove(currentNote);
		notesStore.sync();

		this.activateNotesList();
	},
	onBackToHomeCommand: function() {
		notesService.log("onBackToHomeCommand");
		this.activateNotesList();
	},

	launch: function() {
		this.callParent(arguments);
		Ext.getStore('Notes').load();
		notesService.log('launch');
	},
	init: function() {
		this.callParent(arguments);
		notesService.log('init');
	}
});