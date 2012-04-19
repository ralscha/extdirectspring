Ext.define('Notes.view.NoteEditor', {
	extend: 'Ext.form.Panel',
	requires: 'Ext.form.FieldSet',
	alias: 'widget.noteeditor',
	config: {
		scrollable: 'vertical'
	},

	initialize: function() {
		this.callParent(arguments);

		var backButton = {
			xtype: "button",
			ui: "back",
			text: "Home",
			handler: this.onBackButtonTap,
			scope: this
		};

		var saveButton = {
			xtype: "button",
			ui: "action",
			text: "Save",
			handler: this.onSaveButtonTap,
			scope: this
		};

		var topToolbar = {
			xtype: "toolbar",
			docked: "top",
			title: "Edit Note",
			items: [ backButton, {
				xtype: "spacer"
			}, saveButton ]
		};

		var deleteButton = {
			xtype: "button",
			iconCls: "trash",
			iconMask: true,
			handler: this.onDeleteButtonTap,
			scope: this
		};

		var bottomToolbar = {
			xtype: "toolbar",
			docked: "bottom",
			items: [ deleteButton ]
		};

		this.add([ topToolbar, {
			xtype: "fieldset",
			items: [ {
				xtype: 'datepickerfield',
				name: 'dateCreated',
				dateFormat: 'd.m.Y',
				label: 'Date',
				required: true
			}, {
				xtype: 'textfield',
				name: 'title',
				label: 'Title',
				required: true
			}, {
				xtype: 'textareafield',
				name: 'narrative',
				label: 'Narrative'
			} ]
		}, bottomToolbar ]);
	},

	onSaveButtonTap: function() {
		notesService.log('saveNoteCommand');
		this.fireEvent("saveNoteCommand", this);
	},

	onDeleteButtonTap: function() {
		notesService.log("deleteNoteCommand");
		this.fireEvent("deleteNoteCommand", this);
	},
	onBackButtonTap: function() {
		notesService.log("backToHomeCommand");
		this.fireEvent("backToHomeCommand", this);
	}
});