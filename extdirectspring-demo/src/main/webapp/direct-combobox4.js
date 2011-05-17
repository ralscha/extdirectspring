Ext.require([ 'Ext.data.*', 
              'Ext.form.*', 
              ]);

Ext.onReady(function() {
	Ext.direct.Manager.addProvider(Ext.app.REMOTING_API);

	Ext.define('DeliveryTime', {
		extend : 'Ext.data.Model',
		fields : [ 'label', 'value' ],
		proxy : {
			type: 'direct',
			directFn: deliveryTimeService.getDeliveryTimes,
			reader : {
				root: 'records'
			}
		}
	});

	var store = Ext.create('Ext.data.Store', {
		autoLoad: true,
		model : 'DeliveryTime'	
	});

	var simpleCombo = Ext.create('Ext.form.field.ComboBox', {
	    fieldLabel: 'Select a delivery method',
	    renderTo: Ext.getBody(),
	    displayField: 'label',
	    valueField: 'value',
	    width: 500,
	    labelWidth: 130,
	    store: store,
	    queryMode: 'local',
	    typeAhead: true
	});
	
});