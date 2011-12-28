Ext.define('Simple.model.User', {
    extend: 'Ext.data.Model',
    fields: ['id', 'firstName', 'lastName', 'email', 'city'],
    
    proxy : {
		type: 'direct',
		api : {
			read : userService.load,
			create : userService.create,
			update : userService.update,
			destroy : userService.destroy
		},
		reader : {
			root : 'records'
		}
	}
});