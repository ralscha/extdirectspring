Ext.onReady(function() {
	if (Ext.grid.RowEditor) {
		Ext.grid.RowEditor.prototype.saveBtnText = "Speichern";
		Ext.grid.RowEditor.prototype.cancelBtnText = "Abbrechen";
		Ext.grid.RowEditor.prototype.errorsText = "Fehler";
		Ext.grid.RowEditor.prototype.dirtyText = "Änderungen müssen abgebrochen oder gespeichert werden";
	}

	if (Ext.ux.form.ItemSelector) {
		Ext.ux.form.ItemSelector.prototype.buttonsText = {
			top: "An den Anfang verschieben",
			up: "Nach oben verschieben",
			add: "Zu den Selektierten hinzufügen",
			remove: "Von den Selektierten entfernen",
			down: "Nach unten verschieben",
			bottom: "An das Ende verschieben"
		};
		
		Ext.ux.form.ItemSelector.prototype.listTitle = {
	    	from: 'Verfügbar',
	    	to: 'Selektiert'
	    };
	}
});
