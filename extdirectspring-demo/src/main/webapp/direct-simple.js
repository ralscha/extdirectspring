Ext.onReady(function(){
  Ext.Direct.addProvider(Ext.app.REMOTING_API);
  
  var callback = function(result, e) {
 
    console.log(result);
    console.dir(e);
 
    if(true === e.status) {
      Ext.Msg.show({
        title : 'Response from server',
        msg: '<b>Success</b><br/>' + result,
        width : 300,
        buttons : Ext.MessageBox.OK,
        icon : Ext.MessageBox.INFO
      });            
    } else {
      Ext.Msg.show({
        title : 'Response from server',
        msg: '<b>FAILURE</b><br/>' + e.message,
        width : 300,
        buttons : Ext.MessageBox.OK,
        icon : Ext.MessageBox.ERROR
      });
    }
    
    
  };
  
  
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
    items: [
      { 
        text: 'Call no argument method',
        handler: Example.simple.method1.createDelegate(this, [callback])
      },
      {         
        text: 'Call one argument method',
        handler: Example.simple.method2.createDelegate(this, ['ping', callback])
      },     
      { 
        text: 'Call method with HttpServletRequest argument',
        handler: Example.simple.method3.createDelegate(this, ['ping', callback])
      },     
      { 
        text: 'Call a method with an error',
        handler: Example.simple.method4.createDelegate(this, [callback])
      }   
    ]
  }).show();  

});
