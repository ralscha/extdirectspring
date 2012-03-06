/**
 * Grabs the APOD RSS feed from Google's Feed API, passes the data to our Model to decode
 */
Ext.define('apod.store.Pictures', {
    extend: 'Ext.data.Store',
    
    config: {
        model: 'apod.model.Picture',
        proxy: {
			type: 'direct',
			directFn: carouselService.readPictures
        }
    }
});