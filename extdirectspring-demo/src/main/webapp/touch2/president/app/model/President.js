Ext.define('Sencha.model.President', {
    extend: 'Ext.data.Model',
    config: {
        fields: ['id', 'imageUrl', 'firstName', 'middleInitial', 'lastName'],
		proxy : {
			type: 'direct',
			directFn: presidentsService.getPresidents			
		}
    },

    fullName: function() {    	
    	var d = this.data,        
        names = [
            d.firstName,
            (!d.middleInitial ? "" : d.middleInitial + "."),
            d.lastName
        ];
        
        
        return names.join(" ");
    }
});
