Ext.define('Sencha.view.Main', {
    extend: 'Ext.navigation.View',
    xtype: 'mainpanel',
    requires: [
        'Sencha.view.PresidentList',
        'Sencha.view.PresidentDetail'
    ],

    config: {
        items: [{
            xtype: 'presidentlist'
        }]
    }
});