/*

This file is part of Ext JS 4

Copyright (c) 2011 Sencha Inc

Contact:  http://www.sencha.com/contact

Commercial Usage
Licensees holding valid commercial licenses may use this file in accordance with the Commercial Software License Agreement provided with the Software or, alternatively, in accordance with the terms contained in a written agreement between you and Sencha.

If you are unsure which license is appropriate for your use, please contact the sales department at http://www.sencha.com/contact.

*/
/**
 * @private
 * @class Ext.ux.layout.component.form.ItemSelector
 * @extends Ext.layout.component.field.Field
 * Layout class for {@link Ext.ux.form.ItemSelector} fields.
 * @private
 */
Ext.define('Ext.ux.layout.component.form.ItemSelector', {
    extend: 'Ext.layout.component.field.Field',
    alias: ['layout.itemselectorfield'],

    type: 'itemselectorfield',

    /**
     * @cfg {Number} height The height of the field. Defaults to 200.
     */
    defaultHeight: 200,

    sizeBodyContents: function(width, height) {
        var me = this;

        if (!Ext.isNumber(height)) {
            height = me.defaultHeight;
        }

        me.owner.innerCt.setSize(width, height);
    }
});
