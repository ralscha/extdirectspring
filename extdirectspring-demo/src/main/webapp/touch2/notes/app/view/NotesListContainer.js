Ext.define("Notes.view.NotesListContainer", {
	extend: 'Ext.Container',
	alias: 'widget.noteslistcontainer',

	initialize: function() {
		this.callParent(arguments);

		var newButton = {
			xtype: 'button',
			text: 'New',
			ui: 'action',
			handler: this.onNewButtonTap,
			scope: this
		};

		var topToolbar = {
			xtype: 'toolbar',
			title: 'My Notes',
			docked: 'top',
			items: [ {
				xtype: 'spacer'
			}, newButton ]
		};

		var notesList = {
			xtype: 'noteslist',
			store: Ext.getStore('Notes'),
			listeners: {
				disclose: this.onNotesListDisclose
			}
		};

		this.add([ topToolbar, notesList ]);
	},

	onNewButtonTap: function() {
		notesService.log('newNoteCommand');
		this.fireEvent('newNoteCommand', this);
	},

	onNotesListDisclose: function(list, record, target, index, evt, options) {
		console.log(this);
		notesService.log('editNoteCommand');
		this.fireEvent('editNoteCommand', this, record);
	},

	config: {
		layout: {
			type: 'fit'
		}
	}

});