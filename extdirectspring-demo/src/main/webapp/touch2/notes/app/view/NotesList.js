Ext.define('Notes.view.NotesList', {
	extend: 'Ext.Container',
	alias: 'widget.noteslistview',
	config: {
		layout: {
			type: 'fit'
		},
		items: [ {
			xtype: "toolbar",
			title: "My Notes",
			docked: "top",
			items: [ {
				xtype: 'spacer'
			}, {
				xtype: "button",
				text: 'New',
				ui: 'action',
				itemId: "newButton"
			} ]
		}, {
			xtype: "list",
			store: "Notes",
			itemId: "notesList",
			loadingText: "Loading Notes...",
			emptyText: "<div class=\"notes-list-empty-text\">No notes found.</div>",
			onItemDisclosure: true,
			grouped: true,
			plugins: [ {
				xclass: 'Ext.plugin.PullRefresh'
			} ],
			itemTpl: "<div class=\"list-item-title\">{id}: {title}</div><div class=\"list-item-narrative\">{narrative}</div>"
		} ],
		listeners: [ {
			delegate: "#newButton",
			event: "tap",
			fn: "onNewButtonTap"
		}, {
			delegate: "#notesList",
			event: "disclose",
			fn: "onNotesListDisclose"
		} ]
	},
	onNewButtonTap: function() {
		notesService.log("newNoteCommand");
		this.fireEvent("newNoteCommand", this);
	},
	onNotesListDisclose: function(list, record, target, index, evt, options) {
		notesService.log("editNoteCommand");
		this.fireEvent('editNoteCommand', this, record);
	}
});