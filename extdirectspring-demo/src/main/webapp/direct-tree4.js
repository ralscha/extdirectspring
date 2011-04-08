Ext.require([
    'Ext.direct.*',
    'Ext.data.*',
    'Ext.tree.*',
    'Ext.grid.Scroller'
]);

Ext.onReady(function() {    
    Ext.direct.Manager.addProvider(Ext.app.REMOTING_API);

    var store = new Ext.data.TreeStore({
        root: {
            expanded: true
        },
        nodeParam: 'id',
        proxy: {
            type: 'direct',
            directFn: treeProvider.getTree,
            paramsAsHash: true,
            extraParams : {
    			foo: new Date()
    	    }
        }
    });
    
    
    // create the Tree
    var tree = new Ext.tree.TreePanel({
        store: store,
        height: 350,
        width: 600,
        title: 'Tree Sample',
        rootVisible: false,
        renderTo: Ext.getBody()
    });
    
    store.on("beforeload", function(store, operation) {
    	store.proxy.extraParams.foo = new Date();
    }, this);
    
});