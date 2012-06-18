Ext.define('Notes.view.NoteEditor', {
	extend: 'Ext.form.Panel',
	requires: 'Ext.form.FieldSet',
	alias: 'widget.noteeditorview',
	config: {
		scrollable: 'vertical',
		items: [ {
			xtype: "toolbar",
			docked: "top",
			title: "Edit Note",
			items: [ {
				xtype: "button",
				ui: "back",
				text: "Home",
				itemId: "backButton"
			}, {
				xtype: "spacer"
			}, {
				xtype: "button",
				ui: "action",
				text: "Save",
				itemId: "saveButton"
			} ]
		}, {
			xtype: "toolbar",
			docked: "bottom",
			items: [ {
				xtype: "button",
				iconCls: "trash",
				iconMask: true,
				itemId: "deleteButton"
			} ]
		}, {
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
		} ],
		listeners: [ {
			delegate: "#backButton",
			event: "tap",
			fn: "onBackButtonTap"
		}, {
			delegate: "#saveButton",
			event: "tap",
			fn: "onSaveButtonTap"
		}, {
			delegate: "#deleteButton",
			event: "tap",
			fn: "onDeleteButtonTap"
		} ]
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