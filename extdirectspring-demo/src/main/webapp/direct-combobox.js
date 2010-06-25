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
      {name: 'fullName'},
      {name: 'id'},
      {name: 'street'},
      {name: 'city'},
      {name: 'state'},
      {name: 'zip'}
    ];
  
  var directStoreWOPaging = new Ext.data.DirectStore({
    paramsAsHash:true,
    root : '',
    directFn: personAction.load,       
    fields : storeFields
  });
  
  var directStoreWithPaging = new Ext.data.DirectStore({
    paramsAsHash:true,
    root : 'records',
    totalProperty: 'total',
    directFn: personAction.loadWithPaging,       
    fields : storeFields
  });  

  var tpl = new Ext.XTemplate('<tpl for=".">',
      '<div class="combo-result-item">',
      '<div class="combo-name">{fullName}</div>',
      '<div class="combo-full-address">{street}</div>',
      '<div class="combo-full-address">{city} {state} {zip}</div>',
      '</div></tpl>');


  var combo = {
    xtype : 'combo',
    fieldLabel : 'Search by name',
    forceSelection : true,
    store : directStoreWOPaging,
    displayField : 'fullName',
    valueField : 'id',
    loadingText : 'Querying...',
    minChars : 1,
    tpl : tpl,
    itemSelector : 'div.combo-result-item',
    triggerAction : 'all'
  };
  
  var comboWithPaging = {
    xtype : 'combo',
    fieldLabel : 'Search by name',
    forceSelection : true,
    store : directStoreWithPaging,
    displayField : 'fullName',
    valueField : 'id',
    pageSize : 20,
    loadingText : 'Querying...',
    minChars : 1,
    tpl : tpl,
    itemSelector : 'div.combo-result-item',
    triggerAction : 'all'
  };  

  new Ext.form.FormPanel({
    renderTo : Ext.getBody(),
    width : 400,
    title : 'Combo Test w/o paging',
    frame : true,
    defaults: {anchor: '100%'},
    items : [combo]
  });   
  
  new Ext.form.FormPanel({
    renderTo : Ext.getBody(),
    width : 400,
    title : 'Combo Test with paging',
    frame : true,
    defaults: {anchor: '100%'},
    items : [comboWithPaging]
  });    
});
