Ext.onReady(function() {
      Ext.Direct.addProvider(Ext.app.REMOTING_API);

      var callback = function(result, e) {

        // console.log(result);
        // console.dir(e);

        var out = Ext.ComponentMgr.get('out');

        if (true === e.status) {
          out.append('<p><b>Success</b>: ' + result + '</p>');
        } else {
          out.append('<p><b>Failure</b>: ' + e.message + '</p>');
        }
        out.el.scroll('down', 100000, true);

      };

      var win = new Ext.Window({
            title: 'Simple Examples',
            width: 600,
            height: 250,
            layout: 'border',
            closable: false,
            items: [{
                  xtype: 'panel',
                  region: 'west',
                  layout: 'vbox',
                  minWidth: 250,
                  width: 250,
                  split: true,
                  layoutConfig: {
                    padding: '10px',
                    align: 'left',
                    pack: 'start'
                  },
                  defaultType: 'button',
                  defaults: {
                    style: {
                      paddingTop: '10px'
                    }
                  },
                  items: [{
                        text: 'Call no argument method',
                        handler: Example.simple.method1.createDelegate(this, [callback])
                      }, {
                        text: 'Call one argument method',
                        handler: Example.simple.method2.createDelegate(this, ['ping', callback])
                      }, {
                        text: 'Call method with HttpServletRequest argument',
                        handler: Example.simple.method3.createDelegate(this, ['ping', callback])
                      }, {
                        text: 'Call method with an error',
                        handler: Example.simple.method4.createDelegate(this, [callback])
                      }, {
                        text: 'Call all methods',
                        handler: function() {
                          Example.simple.method1(callback);
                          Example.simple.method2('ping', callback);
                          Example.simple.method3('ping', callback);
                          Example.simple.method4(callback);
                        }
                      }]
                }, {
                  xtype: 'panel',
                  region: 'center',
                  layout: 'fit',
                  tbar: ['->', {
                        text: 'Clear',
                        handler: function() {
                          Ext.ComponentMgr.get('out').update('');
                        }
                      }],
                  items: [{
                        xtype: 'displayfield',
                        cls: 'x-form-text',
                        id: 'out'
                      }]
                }

            ]

          });

      win.show();

      new Ext.Window({
            layout: 'vbox',
            width: 270,
            height: 160,
            closable: false,
            resizable: false,
            title: 'Simple calls to remote methods',
            defaultType: 'button',
            defaults: {
              style: {
                padding: '5px'
              }
            },
            items: [{
                  text: 'Call no argument method',
                  handler: Example.simple.method1.createDelegate(this, [callback])
                }, {
                  text: 'Call one argument method',
                  handler: Example.simple.method2.createDelegate(this, ['ping', callback])
                }, {
                  text: 'Call method with HttpServletRequest argument',
                  handler: Example.simple.method3.createDelegate(this, ['ping', callback])
                }, {
                  text: 'Call a method with an error',
                  handler: Example.simple.method4.createDelegate(this, [callback])
                }]
          });

    });
