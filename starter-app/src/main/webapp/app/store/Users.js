Ext.define('Starter.store.Users', {
	extend: 'Ext.data.Store',
	model: 'Starter.model.User',
	autoLoad: false,
	remoteSort: true,
	pageSize: 30,
	autoSync: false,
	sorters: [ {
		property: 'name',
		direction: 'ASC'
	} ]
});