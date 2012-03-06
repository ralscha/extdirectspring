/**
 * Our app is pretty simple - it just grabs the latest images from NASA's Astronomy Picture Of the Day 
 * (http://apod.nasa.gov/apod/astropix.html) and displays them in a Carousel. This file drives most of
 * the application, but there's also:
 * 
 * * A Store - app/store/Pictures.js - that fetches the data from the APOD RSS feed
 * * A Model - app/model/Picture.js - that represents a single image from the feed
 * * A View - app/view/Picture.js - that displays each image
 * 
 * Our application's launch function is called automatically when everything is loaded.
 */
Ext.Loader.setConfig({
    enabled: true,
    paths: { 'Ext': 'http://www.ralscha.ch/sencha-touch-2.0.0-gpl/src' }
});

Ext.require(['Ext.direct.Manager','Ext.direct.RemotingProvider', 'Ext.data.proxy.Direct'], function() {
	Ext.direct.Manager.addProvider(Ext.app.REMOTING_API);
});

Ext.application({
    name: 'apod',
    
    models: ['Picture'],
    stores: ['Pictures'],
    views: ['Picture'],
    
    requires: [
        'Ext.carousel.Carousel'
    ],
    
    launch: function() {
    	
        var titleVisible = false,
            info, carousel;
        
        /**
         * The main carousel that drives our app. We're just telling it to use the Pictures store and
         * to update the info bar whenever a new image is swiped to
         */
        carousel = Ext.create('Ext.Carousel', {
            store: 'Pictures',
            direction: 'horizontal',
            
            listeners: {
                activeitemchange: function(carousel, item) {
                    info.setHtml(item.getPicture().get('title'));
                }
            }
        });
        
        /**
         * This is just a reusable Component that we pin to the top of the page. This is hidden by default
         * and appears when the user taps on the screen. The activeitemchange listener above updates the 
         * content of this Component whenever a new image is swiped to
         */
        info = Ext.create('Ext.Component', {
            cls: 'apod-title',
            top: 0,
            left: 0,
            right: 0
        });
        
        //add both of our views to the Viewport so they're rendered and visible
        Ext.Viewport.add(carousel);
        Ext.Viewport.add(info);

        /**
         * The Pictures store (see app/store/Pictures.js) is set to not load automatically, so we load it 
         * manually now. This loads data from the APOD RSS feed and calls our callback function once it's
         * loaded.
         * 
         * All we do here is iterate over all of the data, creating an apodimage Component for each item. 
         * Then we just add those items to the Carousel and set the first item active.
         */
        Ext.getStore('Pictures').load(function(pictures) {
            var items = [];
            
            Ext.each(pictures, function(picture) {
                if (!picture.get('image')) {
                    return;
                }
                
                items.push({
                    xtype: 'apodimage',
                    picture: picture
                });
            });
            
            carousel.setItems(items);
            carousel.setActiveItem(0);
        });
        
        /**
         * The final thing is to add a tap listener that is called whenever the user taps on the screen.
         * We do a quick check to make sure they're not tapping on the carousel indicators (tapping on
         * those indicators moves you between items so we don't want to override that), then either hide 
         * or show the info Component.
         * 
         * Note that to hide or show this Component we're adding or removing the apod-title-visible class.
         * If you look at index.html you'll see the CSS rules style the info bar and also cause it to fade
         * in and out when you tap.
         */
        Ext.Viewport.element.on('tap', function(e) {
            if (!e.getTarget('.x-carousel-indicator')) {
                if (titleVisible) {
                    info.element.removeCls('apod-title-visible');
                    titleVisible = false;
                } else {
                    info.element.addCls('apod-title-visible');
                    titleVisible = true;
                }
            }
        });
    }
});