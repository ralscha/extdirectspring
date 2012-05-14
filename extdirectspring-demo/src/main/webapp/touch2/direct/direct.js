Ext.application({
    name: 'Direct',
    launch: function() {
        function doEcho(field) {
        	touchTestAction.doEcho(field.getValue(), function(result, event) {
                var transaction = event.getTransaction(),
                    content = Ext.String.format('<b>Successful call to {0}.{1} with response:</b><pre>{2}</pre>',
                        transaction.getAction(), transaction.getMethod(), Ext.encode(result));

                updateMain(content);
                field.reset();
            });
        }

        function doMultiply(field){
        	touchTestAction.multiply(field.getValue(), function(result, event) {
                var transaction = event.getTransaction(),
                    content;

                if (event.getStatus()) {
                    content = Ext.String.format('<b>Successful call to {0}.{1} with response:</b><pre>{2}</pre>',
                        transaction.getAction(), transaction.getMethod(), Ext.encode(result));
                } else {
                    content = Ext.String.format('<b>Call to {0}.{1} failed with message:</b><pre>{2}</pre>',
                        transaction.getAction(), transaction.getMethod(), event.getMessage());
                }
                updateMain(content);
                field.reset();
            });
        }

        function updateMain(content){
            main.setData({
                data: content
            });
        }

        Ext.direct.Manager.addProvider(Ext.app.REMOTING_API, {
            type:'polling',
            url: Ext.app.POLLING_URLS.message,
            listeners: {
                data: function(provider, event) {
                    updateMain('<i>' + event.getData() + '</i>');
                }
            }
        });

        var main = Ext.create('Ext.Container', {
            fullscreen: true,
            id: 'logger',
            tpl: '<p>{data}</p>',
            tplWriteMode: 'append',
            styleHtmlContent: true,
            scrollable: true,
            items: [{
                docked: 'top',
                xtype: 'toolbar',
                title: 'Remote Call Log'
            }, {
                docked: 'bottom',
                xtype: 'toolbar',
                items: [{
                    itemId: 'echoText',
                    xtype: 'textfield',
                    width: 300,
                    emptyText: 'Echo input'
                }, {
                    itemId: 'echo',
                    text: 'Echo',
                    handler: function(){
                        doEcho(main.down('#echoText'));
                    }
                }, {xtype: 'spacer'}, {
                    itemId: 'multiplyText',
                    xtype: 'textfield',
                    width: 80,
                    emptyText: 'Multiply x 8'
                }, {
                    itemId: 'multiply',
                    text: 'Multiply',
                    handler: function() {
                        doMultiply(main.down('#multiplyText'));
                    }
                }]
            }]
        });
    }
});