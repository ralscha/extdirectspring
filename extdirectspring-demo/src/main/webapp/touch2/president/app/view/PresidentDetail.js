Ext.define('Sencha.view.PresidentDetail', {
    extend: 'Ext.Panel',
    xtype: 'presidentdetail',

    config: {
        title: 'Details',
        styleHtmlContent: true,
        scrollable: 'vertical',
        tpl: [
            '<p>Hello {firstName}!</p>',
            '<img src="http://www.whitehouse.gov/sites/default/files/first-family/masthead_image/{imageUrl}" />'
        ]
    }
});
