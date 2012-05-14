Ext.require([
    'Ext.direct.*'
]);

Ext.application({
    name: 'Direct',
    launch: function() {
        Ext.direct.Manager.addProvider(Ext.app.REMOTING_API);

        var form = Ext.create('Ext.form.Panel', {
            fullscreen: true,
            items: [{
                xtype: 'fieldset',
                items: [{
                    xtype: 'textfield',
                    label: 'First Name',
                    name: 'firstName',
                    value: 'Evan'
                }, {
                    xtype: 'textfield',
                    label: 'Last Name',
                    name: 'lastName',
                    value: 'Trimboli'
                }, {
                    xtype: 'numberfield',
                    label: 'Age',
                    name: 'age',
                    value: 25
                }]
            }, {
                docked: 'bottom',
                xtype: 'toolbar',
                items: [{xtype: 'spacer'}, {
                    text: 'Send',
                    handler: function(){
                        var values = form.getValues();
                        touchTestAction.showDetails(values, function(value) {
                            Ext.Msg.alert('Server Response: ', value);
                        });
                    }
                }]
            }]
        });
    }
});