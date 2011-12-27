Ext.require([ 'Ext.direct.*', 'Ext.form.*', 'Ext.tip.QuickTipManager', 'Ext.layout.container.Accordion' ]);

Ext.onReady(function() {

	Ext.app.REMOTING_API.enableBuffer = 0;
	Ext.direct.Manager.addProvider(Ext.app.REMOTING_API);

	Ext.tip.QuickTipManager.init();

	Ext.direct.Manager.on('exception', function(e) {		
		Ext.Msg.alert('Failure', 'Something went wrong');
	});	
	
	Ext.create('Ext.form.Panel', {
		renderTo: Ext.getBody(),
		frame: true,
		fileUpload: true,
		width: 500,
		height: 400,
		defaults: {
			width: 400
		},
		defaultType: 'textfield',
		items: [ {
			xtype: 'filefield',
			buttonOnly: false,
			fieldLabel: 'File1',
			name: 'fileUpload1',
			buttonText: 'Select a File...'
		}, {
			xtype: 'filefield',
			buttonOnly: false,
			fieldLabel: 'File2',
			name: 'fileUpload2',
			buttonText: 'Select a Textfile...'
		}, textArea1 = new Ext.form.TextArea({
			name: 'textArea',
			fieldLabel: "File contents",
			height: 300,
	        anchor: '100%'
		}) ],
		api: {
			submit: uploadController.uploadTest4
		},
		buttons: [ {
			text: "Upload",
			handler: function() {
				
				var form = this.up('form').getForm();
	            if(form.isValid()){
	                form.submit({
	                    waitMsg: 'Uploading your files...',
						success: function(form, action) {
							textArea1.setValue(action.result.fileContents);
						}
	                });
	            }				

			}
		}, {
			text: "Reset",
			handler: function() {
				this.up('form').getForm().reset();
			}
		} ]
	});

});