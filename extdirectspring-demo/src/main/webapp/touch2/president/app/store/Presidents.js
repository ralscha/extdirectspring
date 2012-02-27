Ext.define('Sencha.store.Presidents', {
    extend: 'Ext.data.Store',
    
    config: {
        model: 'Sencha.model.President',
        autoLoad: true,
        sorters: 'lastName',
        grouper : function(record) {
            return record.data.lastName;
        }
    }
});
