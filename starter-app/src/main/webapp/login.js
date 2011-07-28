Ext.onReady(function() {
	Ext.QuickTips.init();

	var login = Ext.create('Ext.form.Panel', {
		frame: true,
		title: i18n.login_title,
		url: 'j_spring_security_check',
		width: 320,
		margin: '60px, 0, 0, 100px',
		renderTo: Ext.getBody(),

		standardSubmit: true,

		defaults: {
			anchor: '100%'
		},
		
		defaultType: 'textfield',
		
		fieldDefaults: {
			msgTarget: 'side'
		},
		
		items: [ {
			fieldLabel: i18n.user_username,
			name: 'j_username',
			allowBlank: false
		}, {
			fieldLabel: i18n.user_password,
			name: 'j_password',
			inputType: 'password',
			allowBlank: false
		} ],

		buttons: [ {
			text: i18n.login_withuser,
			handler: function() {
				var form = this.up('form').getForm();
				form.setValues({
					j_username: 'user',
					j_password: 'user'
				});
				form.submit();
			}
		}, {
			text: i18n.login_withadmin,
			handler: function() {
				var form = this.up('form').getForm();
				form.setValues({
					j_username: 'admin',
					j_password: 'admin'
				});
				form.submit();
			}
		}, {
			text: i18n.login,
			handler: function() {
				var form = this.up('form').getForm();
				if (form.isValid()) {
					form.submit();
				}
			}
		} ]
	});

	login.show();
	login.getForm().findField('j_username').focus();

});