/**
 * HighchartsAdapter
 * Updated for Highcharts release 1.2.5
 *
 * grep function added in 1.2.5
 */

HighchartsAdapter = {

    each: Ext.each,

    map: function(arr, fn){
            var results = [];
            if (arr)
            for (var i = 0, len = arr.length; i < len; i++)
                    results[i] = fn.call(arr[i], arr[i], i, arr);
            return results;
    },

    grep: function( elems, callback, inv ) {
            var ret = [];

            // Go through the array, only saving the items
            // that pass the validator function
            for ( var i = 0, length = elems.length; i < length; i++ )
                    if ( !inv != !callback( elems[ i ], i ) )
                            ret.push( elems[ i ] );

            return ret;
    },

    merge: function(){
        var args = arguments;
        /**
         * jQuery extend function
         */
        var jqextend = function() {
            // copy reference to target object
            var target = arguments[0] || {}, i = 1, length = arguments.length, deep = false, options;

            // Handle a deep copy situation
            if ( typeof target === "boolean" ) {
                deep = target;
                target = arguments[1] || {};
                // skip the boolean and the target
                i = 2;
            }

            // Handle case when target is a string or something (possible in deep copy)
            if ( typeof target !== "object" && !Ext.isFunction(target) )
                target = {};

            // extend jQuery itself if only one argument is passed
            if ( length == i ) {
                target = this;
                --i;
            }

            for ( ; i < length; i++ )
                // Only deal with non-null/undefined values
                if ( (options = arguments[ i ]) != null )
                        // Extend the base object
                        for ( var name in options ) {
                                var src = target[ name ], copy = options[ name ];

                                // Prevent never-ending loop
                                if ( target === copy )
                                        continue;

                                // Recurse if we're merging object values
                                if ( deep && copy && typeof copy === "object" && !copy.nodeType )
                                        target[ name ] = jqextend( deep,
                                                // Never move original objects, clone them
                                                src || ( copy.length != null ? [ ] : { } )
                                        , copy );

                                // Don't bring in undefined values
                                else if ( copy !== undefined )
                                        target[ name ] = copy;

                        }

            // Return the modified object
            return target;
        };
        return jqextend(true, null, args[0], args[1], args[2], args[3]);
    },

    hyphenate: function (str){
        return str.replace(/([A-Z])/g, function(a, b){ return '-'+ b.toLowerCase() });
    },

    addEvent: function (el, event, fn) {
        var xel = Ext.get(el);
        if (xel) {
            xel.addListener(event, fn)
        } else {
            if (!el.addListener){
                Ext.apply(el, new Ext.util.Observable());
            }
            el.addListener(event, fn)
        }

    },

    fireEvent: function(el, event, eventArguments, defaultFunction) {
        var o = {
            type: event,
            target: el
        }
        Ext.apply(o, eventArguments)

        // if fireEvent is not available on the object, there hasn't been added
        // any events to it above
        if (el.fireEvent) {
            el.fireEvent(event, o);
        }
        
        // fire the default if it is passed and it is not prevented above
        if (defaultFunction) defaultFunction(o);
    },

    animate: function (el, params, options){
    	if(options){
    		if(options.duration==undefined || options.duration==0){
        		options.duration=1;
        	}
        	else{
        		options.duration=options.duration/1000;
        	}
    	}

        if (params.width!==undefined) {
            Ext.get(el).setWidth(parseInt(params.width), {duration:options.duration});
        }
        else if (params.height!==undefined) {
            Ext.get(el).setHeight(parseInt(params.height), {duration:options.duration});
        }
        else if (params.left!==undefined) {
            Ext.get(el).setLeft(parseInt(params.left));
        }
        else if (params.opacity!==undefined) {
            Ext.get(el).setOpacity(parseInt(params.opacity),{duration:options.duration,callback:options.complete});
        } else {
            Ext.get(el).setTop(parseInt(params.top));
        }
    },

    getAjax: function (url, callback) {
        Ext.Ajax.request({
           url: url,
           success: function(response){
               callback(response.responseText);
           }
        });
    }
}