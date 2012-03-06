/**
 * Very simple specialization of Ext.Img, just saves the apod.model.Picture that was assigned to it
 */
Ext.define('apod.view.Picture', {
    extend: 'Ext.Img',
    xtype: 'apodimage',
    
    config: {
        /**
         * @cfg {apod.model.Picture} picture The Picture to show
         */
        picture: null
    },
    
    updatePicture: function(picture) {
        this.setSrc(picture.get('image') + '&width=' + window.innerWidth +'&height=' + window.innerHeight);
    }
});