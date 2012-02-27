Ext.define('Sencha.view.PresidentList', {
    extend: 'Ext.dataview.List',
    xtype: 'presidentlist',
    requires: ['Sencha.store.Presidents'],
    
    config: {
        title: 'American Presidents',
        grouped: true,
        itemTpl: '{firstName} <tpl if="middleInitial">{middleInitial}. </tpl>{lastName}',
        store: 'Presidents',
        onItemDisclosure: true
    }
});
