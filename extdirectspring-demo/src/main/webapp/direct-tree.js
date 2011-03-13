Ext.onReady(function() {
  Ext.Direct.addProvider(Ext.app.REMOTING_API);

  myTreeLoader = new Ext.tree.TreeLoader( {
      directFn: treeProvider.getTree,
      //paramOrder: ['node','foo'],
      nodeParameter: 'id',
      paramsAsHash: true,
      baseAttrs : {
			foo: 'empty'
	  }
    });
    
  var tree = new Ext.tree.TreePanel( {
    width: 400,
    height: 400,
    autoScroll: true,
    renderTo: document.body,
    root: {
      id: 'root',
      text: 'Root'
    },
    loader: myTreeLoader,
    fbar: [ {
      text: 'Reload root',
      handler: function() {
        tree.getRootNode().reload();
      }
    } ]
  });
  
  
  myTreeLoader.on("beforeload", function(treeLoader, node) {
	        treeLoader.baseParams.foo = new Date().getSeconds();
  }, this);

});
