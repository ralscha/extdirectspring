Ext.application({
	name: 'Sencha',

	launch: function() {
		Ext.direct.Manager.addProvider(Ext.app.REMOTING_API);

		Ext.define('Contact', {
			extend: 'Ext.data.Model',
			fields: [ 'id', 'name', 'email', 'message' ],

			proxy: {
				type: 'direct',
				api: {
					read: contactService.read,
					update: contactService.update,
					create: contactService.create,
					destroy: contactService.destroy,
					
				}
			}
		});

		Ext.create('Ext.TabPanel', {
			fullscreen: true,
			tabBarPosition: 'bottom',

			items: [
					{
						title: 'Home',
						iconCls: 'home',
						cls: 'home',
						html: [ '<img height=260 src="http://staging.sencha.com/img/sencha.png" />',
								'<h1>Welcome to Sencha Touch</h1>', "<p>Building the Getting Started app</p>",
								'<h2>Sencha Touch (2.0.0pr1)</h2>' ].join("")
					}, {
						xtype: 'nestedlist',
						title: 'Blog',
						iconCls: 'star',
						cls: 'blog',
						displayField: 'title',

						store: Ext.create('Ext.data.TreeStore', {
							fields: [ 'title', 'content' ],
							proxy: {
								type: 'direct',
								directFn: blogService.getBlogPosts
							}
						}),

						getDetailCard: function(node) {
							if (node) {
								return {
									xtype: 'panel',
									scrollable: true,
									html: node.get('content')
								};
							}
						}
					},
					{
						xtype: 'formpanel',
						title: 'Contact Us',
						iconCls: 'user',
						layout: 'vbox',

						listeners: {
							show: function() {
								Contact.load(1, {
									scope: this,
									success: function(record, operation) {
										this.setRecord(record);
									},

								});
							}
						},

						items: [ {
							xtype: 'fieldset',
							title: 'Contact Us',
							instructions: 'Email address is optional',

							items: [ {
								xtype: 'textfield',
								label: 'Name',
								name: 'name'
							}, {
								xtype: 'emailfield',
								label: 'Email',
								name: 'email'
							}, {
								xtype: 'textareafield',
								label: 'Message',
								name: 'message',
								height: 90
							} ]
						}, {
							xtype: 'button',
							text: 'Send',
							ui: 'confirm',

							handler: function() {
								var form = this.up('formpanel');
								var record = form.getRecord();
								record.set(form.getValues());
								form.getRecord().save({
									success: function() {
										Ext.Msg.alert('Thank You', 'Your message has been received', function() {
											form.reset();
										});
									},
									scope:this
								});

							}
						} ]
					} ]
		});
	}
});