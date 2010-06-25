// This example illustrates how to load a FormPanel or BasicForm through Ext.Direct.

Ext.onReady(function(){
    // Notice that Direct requests will batch together if they occur
    // within the enableBuffer delay period (in milliseconds).
    // Slow the buffering down from the default of 10ms to 100ms
    Ext.app.REMOTING_API.enableBuffer = 100;
    Ext.Direct.addProvider(Ext.app.REMOTING_API);

    // provide feedback for any errors
    Ext.QuickTips.init();

    var basicInfo = new Ext.form.FormPanel({
        // configs for FormPanel
        title: 'Basic Information',
        border: false,
        padding: 10,
        buttons:[{
            text: 'Submit',
            handler: function(){
                basicInfo.getForm().submit({
                    params: {
                        foo: 'bar',
                        uid: 34
                    }
                });
            }
        }],

        // configs apply to child items
        defaults: {anchor: '-20', msgTarget: 'side'}, // provide some room on right for validation errors
        defaultType: 'textfield',
        items: [{
            fieldLabel: 'Name',
            name: 'name'
        },{
            fieldLabel: 'Email',
            name: 'email'
        },{
            fieldLabel: 'Company',
            name: 'company'
        }],

        // configs for BasicForm
        api: {
            // The server-side method to call for load() requests
            load: profile.getBasicInfo,
            // The server-side must mark the submit handler as a 'formHandler'
            submit: profile.updateBasicInfo
        },
        // specify the order for the passed params
        paramOrder: ['uid', 'foo']
    });

    var phoneInfo = new Ext.form.FormPanel({
        title: 'Phone Numbers',
        border: false,
        api: {
            load: profile.getPhoneInfo
        },
        padding: 10,
        paramOrder: ['uid'],
        defaultType: 'textfield',
        defaults: {anchor: '100%'},
        items: [{
            fieldLabel: 'Office',
            name: 'office'
        },{
            fieldLabel: 'Cell',
            name: 'cell'
        },{
            fieldLabel: 'Home',
            name: 'home'
        }]
    });

     var locationInfo = new Ext.form.FormPanel({
        title: 'Location Information',
        border: false,
        padding: 10,
        api: {
            load: profile.getLocationInfo
        },
        paramOrder: ['uid'],
        defaultType: 'textfield',
        defaults: {anchor: '100%'},
        items: [{
            fieldLabel: 'Street',
            name: 'street'
        },{
            fieldLabel: 'City',
            name: 'city'
        },{
            fieldLabel: 'State',
            name: 'state'
        },{
            fieldLabel: 'Zip',
            name: 'zip'
        }]
    });

     var accordion = new Ext.Panel({
        layout: 'accordion',
        layoutConfig: {
            autoWidth : false
        },
        renderTo: Ext.getBody(),
        title: 'My Profile',
        width: 300,
        height: 240,
        items: [basicInfo, phoneInfo, locationInfo]
    });

    // load the forms (notice the load requests will get batched together)
    basicInfo.getForm().load({
        // pass 2 arguments to server side getBasicInfo method (len=2)
        params: {
            foo: 'bar',
            uid: 34
        }
    });

    phoneInfo.getForm().load({
        params: {
            uid: 5
        }
    });

    // defer this request just to simulate the request not getting batched
    // since it exceeds to configured buffer
    (function(){
        locationInfo.getForm().load({
            params: {
                uid: 5
            }
        });
    }).defer(200);


});