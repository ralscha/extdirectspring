/**
 * Simple Model that represents an image from NASA's Astronomy Picture Of the Day. The only remarkable
 * thing about this model is the 'image' field, which uses a regular expression to pull its value out 
 * of the main content of the RSS feed. Ideally the image url would have been presented in its own field
 * in the RSS response, but as it wasn't we had to use this approach to parse it out
 */
Ext.define('apod.model.Picture', {
    extend: 'Ext.data.Model',
    
    config: {
        fields: [
            'id', 'title', 'link', 'author', 'content', 'image'
        ]
    }
});