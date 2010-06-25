/*
 * Copyright 2010 Ralph Schaer <ralphschaer@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

Ext.onReady(function(){
  Ext.QuickTips.init();
  
  Ext.Direct.addProvider(Ext.app.REMOTING_API);

  var storeFields = [
      {name: 'lastName'},
      {name: 'firstName'},
      {name: 'id'},
      {name: 'street'},
      {name: 'city'},
      {name: 'state'},
      {name: 'zip'}
    ];
  
  var writer = new Ext.data.JsonWriter({
    writeAllFields: true,
    listful: true,
    encode: false
  }); 
  
  
  var directStore = new Ext.data.DirectStore({
    id: 'directStore',
    autoDestroy: true,
    paramsAsHash:true,
    root : 'records',
    totalProperty: 'total',
    autoLoad: false,
    autoSave: true,   
    successProperty: 'success',
    fields : storeFields, 
    remoteSort: true,
    idProperty: 'id',
    writer: writer,
    baseParams: {
      no: 1,
      name: 'Ralph'
    },
    api: {
      read: personAction.loadWithPaging,
      create: personAction.create,
      update: personAction.update,
      destroy: personAction.destroy      
    }   
  });  

  var textFieldEditor = new Ext.form.TextField();
  
  var comboEditor = {
    xtype: 'combo',
    triggerAction: 'all',
    displayField: 'state',
    valueField: 'state',
    store: {
      xtype: 'directstore',
      root: '',
      directFn: personAction.getStates,
      fields: ['state']
    }
  };  
  
 
  
  var columnModel = [    
    {
      header: 'Last Name',
      dataIndex: 'lastName',
      sortable: true,
      editor: textFieldEditor
    },
    {
      header: 'First Name',
      dataIndex: 'firstName',
     sortable: true,
      editor: textFieldEditor
    },
    {
      header: 'Street Address',
      dataIndex: 'street',
      sortable: true,
      editor: textFieldEditor
    },
    {
      header: 'City',
      dataIndex: 'city',
      sortable: true,
      editor: textFieldEditor
    },
    {
      header: 'State',
      dataIndex: 'state',
      sortable: true,
      editor: comboEditor
    },  
    {
      header: 'Zip Code',
      dataIndex: 'zip',
      sortable: true,
      editor: textFieldEditor      
    }
  ];  
  
  var onInsertRecord = function() {
    var newRecord = new directStore.recordType();
     var grid = Ext.getCmp('myEditorGrid');
     var selected = grid.getSelectionModel().getSelectedCell();
     var selectedRowIndex = selected[0];
     directStore.insert(selectedRowIndex, newRecord);
     grid.startEditing(selectedRowIndex, 0);
  };
  
  var doCellCtxMenu = function(editorGrid, rowIndex, cellIndex, evtObj) {
    evtObj.stopEvent();
    if (!editorGrid.rowCtxMenu) {
      editorGrid.rowCtxMenu = new Ext.menu.Menu({
        items: [
          {
            text: 'Insert Record',
            handler: onInsertRecord
          },
          {
            text: 'Delete Record',
            handler: onDelete
          }
        ]
      });
    }
    editorGrid.getSelectionModel().select(rowIndex, cellIndex);
    editorGrid.rowCtxMenu.showAt(evtObj.getXY());
  };
  
  var onDelete = function() {
    var grid = Ext.getCmp('myEditorGrid');
    var selected = grid.getSelectionModel().getSelectedCell();
    var recordToDelete = grid.store.getAt(selected[0]);
    grid.store.remove(recordToDelete);    
  };

  var pagingToolbar = {
    xtype: 'paging',
    store: directStore,
    pageSize: 50,
    displayInfo: true    
  };  
  
  var grid = {
    xtype: 'editorgrid',
    columns: columnModel,
    store: directStore,
    loadMask: true,
    id: 'myEditorGrid',
    bbar: pagingToolbar,
    stripeRows: true,
    viewConfig: {
      forceFit: true
    },
    listeners: {
      cellcontextmenu: doCellCtxMenu
    }
  };
  
  new Ext.Panel({
    renderTo: Ext.getBody(),
    height: 350,
    width: 700,    
    border: false,
    frame: false,
    layout: 'fit',
    items: grid
  });
  
  Ext.StoreMgr.get('directStore').load({
    params: { start: 0, limit: 50}
  });  
  
});
