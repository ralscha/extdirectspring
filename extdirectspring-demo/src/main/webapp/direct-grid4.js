Ext.require([
    'Ext.direct.*',
    'Ext.data.*',
    'Ext.grid.*',
    'Ext.util.Format'
]);

Ext.define('Company', {
    extend: 'Ext.data.Model',
    fields: ['name', 'turnover']
});

Ext.onReady(function() {    
    Ext.direct.Manager.addProvider(Ext.app.REMOTING_API);
        
    // create the Tree
    Ext.create('Ext.grid.GridPanel', {
        store: {
            model: 'Company',
            remoteSort: true,
            autoLoad: true,
            sorters: [{
                property: 'name',
                direction: 'ASC'
            },
            {
            	property: 'turnover',
            	direction: 'DESC'
            }],
            proxy: {
                type: 'direct',
                directFn: turnoverService.getTurnovers,
                reader: {
                    root: 'records'
                }
            }
        },
        columns: [{
            dataIndex: 'name',
            flex: 1,
            text: 'Name'
        }, {
            dataIndex: 'turnover',
            width: 120,
            text: 'Turnover pa.',
            renderer: Ext.util.Format.usMoney
        }],
        height: 350,
        width: 600,
        title: 'Company Grid',
        renderTo: Ext.getBody()
    });
});