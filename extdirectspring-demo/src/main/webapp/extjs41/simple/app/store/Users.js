Ext.define('Simple.store.Users', {
    extend: 'Ext.data.Store',
    model: 'Simple.model.User',
    autoLoad: true,
    remoteSort: true,
    pageSize : 30,
    autoSync : true,
    sorters: [ {
        property: 'lastName',
        direction: 'ASC'
    }]
});