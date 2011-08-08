Ext.require('Starter.component.Notification');

Ext.application({
	name: 'Starter',
	appFolder: 'app',
	controllers: [ 'Users', 'Navigation', 'PollChart' ],
	autoCreateViewport: true,
	launch: function() {

		if (this.hasLocalstorage()) {
			Ext.state.Manager.setProvider(Ext.create('Ext.state.LocalStorageProvider'));
		} else {
			Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));
		}

		Ext.direct.Manager.on('event', function(e) {
			//todo: need a better method to handle session timeouts
			if (e.code && e.code === 'parse') {
				window.location.reload();
			}
		});
		
		Ext.direct.Manager.on('exception', function(e) {	
			if (e.message === 'accessdenied') {
				Starter.component.Notification.error(i18n.error, i18n.error_accessdenied);
			} else {
				Starter.component.Notification.error(i18n.error, e.message);
			}
		});		
		
		Ext.apply(Ext.form.field.VTypes, {
			password: function(val, field) {
				if (field.initialPassField) {
					var pwd = field.up('form').down('#' + field.initialPassField);
					return (val == pwd.getValue());
				}
				return true;
			},

			passwordText: i18n.user_passworddonotmatch
		});

	},
	hasLocalstorage: function() {
		try {
			return !!localStorage.getItem;
		} catch (e) {
			return false;
		}
	}
});
