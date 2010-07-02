Ext.onReady(function() {

  Ext.Direct.addProvider(Ext.app.REMOTING_API);

  var directStore = new Ext.data.DirectStore( {
    paramsAsHash : true,
    autoLoad : true,
    root : '',
    directFn : bookService.getBooks,
    fields : [ {
      name : 'id'
    }, {
      name : 'title'
    }, {
      name : 'publisher'
    }, {
      name : 'isbn10'
    }, {
      name : 'isbn13'
    }, {
      name : 'link'
    }, {
      name : 'description'
    } ]
  });

  var expander = new Ext.ux.grid.RowExpander( {
    tpl : new Ext.Template('<p><b>ISBN10:</b> {isbn10}</p>', 
        '<p><b>ISBN13:</b> {isbn13}</p>',
        '<p><b>Link:</b> <a href="{link}" target="_blank">{link}</a></p>', 
        '<p><b>Description:</b> {description}</p>')
  });

  var gridBooks = new Ext.grid.GridPanel( {
    store : directStore,
    cm : new Ext.grid.ColumnModel( {
      defaults : {
        sortable : true,
        width : 200
      },
      columns : [ expander, {
        header : "Title",
        dataIndex : 'title'
      }, {
        header : "Publisher",
        dataIndex : 'publisher'
      } ]
    }),
    width : 430,
    height : 270,
    plugins : expander,
    title : 'ExtJS Books',
    renderTo : Ext.getBody()
  });

});
