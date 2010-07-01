/**
 * @author Daniel Kloosterman
 * @email buz.i286@gmail.com
 * @version 0.3.1
 * @examples {@link http://www.i286.org/examples/}
 * @requires HighChart 1.2.5+, HighCharts-ExtJS-adapter
 *
 * Todo:
 * setSerieStyle, setStyle, Multiple datastores (0.4.0)
 *
 * 08-05-2010
 * + Small fix in refresh function
 *
 * 28-04-2010
 * + Added class: Ext.ux.HighChart.Serie, Ext.ux.HighChart.LineSerie, Ext.ux.HighChart.PieSerie, etc... :)
 * - Fixed: Wait for the panel to be resized to its correct size (Instead of 0,0)
 * after that create the chart.
 *
 * Updates:
 * 24-04-2010
 * - Fixed: setTitle/setSubTitle
 * + removeSerie, removeAllSeries
 * + loadMask
 *
 * 23-04-2010
 * - Fixed: Chart rendering (bindComponent was binding to itself not the parent)
 * + update: Added delay for rendering/updating the chart.
 *
 * 20-04-2010
 * - Fixed: Chart without store/series
 * - Fixed: addSeries (thx to buergi)
 *
 * 19-04-2010
 * - Fixed: Series keep hidden after resize.
 * + bindComponent - Fixes the issue with the fitlayout.
 * + setTitle, setSubTitle - Change the title/subtitle of the chart
 * + addSeries - Add new series to the chart
 */
/**
 * @class Ext.ux.HighChart
 * @extends Ext.BoxComponent
 * The Ext.chart package provides the capability to visualize data with javascript based charting.
 * Each chart binds directly to an Ext.data.Store enabling automatic updates of the chart.
 * To change the look and feel of a chart, see the {@link http://www.highcharts.com/ref/#chart} config options.
 * @constructor
 * @xtype highchart
 */
Ext.ux.HighChart = Ext.extend(Ext.BoxComponent, {

    /**
     * @cfg {Object} defaultSerieType
     * Sets styles for this chart. This contains default styling, so modifying this property will <b>override</b>
     * the built in styles of the chart. Use {@link #extraStyle} to add customizations to the default styling.
     */
    defaultSerieType: null,

    /**
     * @cfg {Boolean} resizable
     * True to allow resizing, false to disable resizing (defaults to true).
     */
    resizable: true,

    /**
     * @cfg {Integer} updateDelay
     * (defaults to 0)
     */
    updateDelay: 0,

    /**
     * @cfg {Object} loadMask An {@link Ext.LoadMask} config or true to mask the chart while
     * loading. Defaults to false.
     */
    loadMask: false,
     
    /**
     * Add one or more series to the chart
     * @param {Array} series An array of series
     * @param {Boolean} append the serie. Defaults to true
     */
    addSeries: function(series, append){
        append = (append == null) ? true : false;
        var n = new Array(), c = new Array(), cls, serieObject;
        // Add empty data to the serie or just leave it normal. Bug in HighCharts?
        for (var i = 0; i < series.length; i++){
            var serie = series[i];
            if (!serie.serieCls){
                if (serie.type!=null || this.defaultSerieType!=null){
                    cls = Ext.ux.HighChart.Series.get(serie.type!=null ? serie.type : this.defaultSerieType)
                } else {
                    cls = Ext.ux.HighChart.Serie;
                }
                serieObject = new cls(serie)
            } else {
                serieObject = serie;
            }
            c.push(serieObject.config);
            n.push(serieObject);
        }

        // Show in chart
        if (this.chart){
            if (!append){
                this.removeAllSeries();
                this.series = n;
                this.chartConfig.series = c;
            } else {
                this.chartConfig.series = this.chartConfig.series ? this.chartConfig.series.concat(c) : c;
                this.series = this.series ? this.series.concat(n) : n;
            }
            for (var i = 0; i < c.length;i++){
                this.chart.addSeries(c[i],true);
            }
            this.refresh();

        // Set the data in the config.
        } else {
            if (append){
                this.chartConfig.series = this.chartConfig.series ? this.chartConfig.series.concat(c) : c;
                this.series = this.series ? this.series.concat(n) : n;

            } else {
                this.chartConfig.series = c;
                this.series = n;
            }
        }
    },

    /**
     *
     */
    removeSerie: function(id, redraw){
        redraw = redraw || true;
        if (this.chart){
            this.chart.series[id].remove(redraw);
            this.chartConfig.series.splice(id, 1);
        }
        this.series.splice(id, 1);
    },

    /**
     * Remove all series
     */
    removeAllSeries: function(){
        var sc=this.chartConfig.series.length;
        for (var i=0;i<sc;i++){
            this.removeSerie(0);
        }
    },

    /**
     * Set the title of the chart
     * @param {String} title Text to set the subtitle
     */
    setTitle: function(title) {
        if (this.chartConfig.title)
            this.chartConfig.title.text = title;
        else
            this.chartConfig.title = { 
                text: title
            }

        if (this.chart && this.chart.titleLayer)
            this.chart.titleLayer.div.firstChild.innerHTML = title;
    },

    /**
     * Set the subtitle of the chart
     * @param {String} title Text to set the subtitle
     */
    setSubTitle: function(title){
        if (this.chartConfig.subtitle)
            this.chartConfig.subtitle.text = title;
        else
            this.chartConfig.subtitle = { 
                text: title
            }

        if (this.chart && this.chart.titleLayer)
            this.chart.titleLayer.div.lastChild.innerHTML = title;
    },

    initComponent: function(){
        if (this.store)
            this.store = Ext.StoreMgr.lookup(this.store);
        
        Ext.ux.HighChart.superclass.initComponent.call(this);
    },

    initEvents: function(){
        if(this.loadMask){
            this.loadMask = new Ext.LoadMask(this.el,
                Ext.apply({
                    store:this.store
                    }, this.loadMask));
        }
    },

    afterRender: function(){

        if (this.store)
            this.bindStore(this.store, true);

        Ext.ux.HighChart.superclass.afterRender.call(this);
        
        this.bindComponent(true);
        
        Ext.applyIf(this.chartConfig.chart, {
            renderTo: this.el.dom,
            events: {
                load: function(){
                    this.updatePosition();
                }
            }
        });

        Ext.applyIf(this.chartConfig, {
            xAxis: [{}]
        });

        if (this.xField && this.store){
            this.updatexAxisData();
        }

        if (this.series){
            this.addSeries(this.series, false);
        } else
            this.series = [];
   
        this.initEvents();
        // Make a delayed call to update the chart.
        this.update(500);
    },

    onMove: function(){
        if (this.chart)
            this.chart.updatePosition();
    },

    draw: function(){
        /**
         * Redraw the chart
         */
        if(this.chart && this.rendered) {
            if (this.resizable){
                for (var i = 0; i < this.series.length;i++){
                    this.series[i].visible = this.chart.series[i].visible;
                }

                // Destroy
                this.chart.destroy();
                delete this.chart;

                // Create a new chart
                this.chart = new Highcharts.Chart(this.chartConfig);
            }

        /**
         * Create the chart
         */
        } else if (this.rendered){
            // Create the chart
            this.chart = new Highcharts.Chart(this.chartConfig);
        }

        for (i=0; i < this.series.length; i++){
            if (!this.series[i].visible)
                this.chart.series[i].hide();
        }

        // Refresh the data
        this.refresh();
        this.chart.updatePosition();
    },

    //@deprecated
    onContainerResize: function(){
        this.draw();
    },

    //private
    updatexAxisData: function(){
        var data = [], items = this.store.data.items;

        if (this.xField && this.store){
            for (var i=0; i < items.length; i++){
                data.push(items[i].data[this.xField])
            }
            if (this.chart)
                this.chart.xAxis[0].setCategories(data, true);
            else
                this.chartConfig.xAxis[0].categories = data;
        }
    },

    bindComponent: function(bind){
        /**
         * Make the chart update the positions
         * positions are based on the window object and not on the
         * owner object.
         */
        var getWindow = function(parent){
            if (parent.ownerCt)
                return getWindow(parent.ownerCt)
            else
                return parent;
        }
        var w = getWindow(this);

        if (bind) {
            w.on('move', this.onMove, this);

            if (this.ownerCt)
                this.ownerCt.on('render', this.update, this);
        }
        else {
            if (this.ownerCt)
                this.ownerCt.un('render', this.update, this);
            w.un('move', this.onMove, this)
        }
    },

    /**
     * Changes the data store bound to this chart and refreshes it.
     * @param {Store} store The store to bind to this chart
     */
    bindStore : function(store, initial){

        if(!initial && this.store){
            if(store !== this.store && this.store.autoDestroy){
                this.store.destroy();
            } else {
                this.store.un("datachanged", this.onDataChange, this);
                this.store.un("load", this.onLoad, this);
                this.store.un("add", this.onAdd, this);
                this.store.un("remove", this.onRemove, this);
                this.store.un("update", this.onUpdate, this);
                this.store.un("clear", this.onClear, this);
            }
        }

        if(store){
            store = Ext.StoreMgr.lookup(store);
            store.on({
                scope: this,
                load: this.onLoad,
                datachanged: this.onDataChange,
                add: this.onAdd,
                remove: this.onRemove,
                update: this.onUpdate,
                clear: this.onClear
            });
        }

        this.store = store;
        if(store && !initial){
            this.refresh();
        }
    },

    /**
     * Complete refresh of the chart
     */
    refresh: function(){
        if (this.store&&this.chart){
            var data = new Array(), seriesCount = this.chart.series.length, i;

            for (i=0;i<seriesCount;i++)
                data.push(new Array());

            // We only want to go true the data once.
            // So we need to have all columns that we use in line.
            // But we need to create a point.
            var items = this.store.data.items;
            var xFieldData = [];

            for (var x = 0; x < items.length;x++){
                var record = items[x];
                if (this.xField){
                    xFieldData.push(record.data[this.xField]);
                }
                for (i = 0; i < seriesCount; i++){
                    var serie = this.series[i], point;
                    if ((serie.type == 'pie' && serie.useTotals)){
                        if (x == 0)
                            serie.clear();
                        point = serie.getData(record, x);
                    } else {
                        point = serie.getData(record, x);
                        data[i].push(point);
                    }
                }
            }

            // Update the series
            for (i = 0; i < seriesCount; i++) {
                if (this.series[i].useTotals) {
                    this.chart.series[i].setData(this.series[i].getTotals())
                }
                else if (data[i].length > 0)
                    this.chart.series[i].setData(data[i], true); // true == redraw.
            }

            if (this.xField){
                this.updatexAxisData();
            }
        }
    },

    /**
     * Update a selected row.
     */
    refreshRow: function(record){
        var index = this.store.indexOf(record);
        if (this.chart){
        for (var i = 0; i < this.chart.series.length; i++){
            var serie = this.chart.series[i];
            var point = this.series[i].getData(record, index);
            if (this.series[i].type == 'pie' && this.series[i].useTotals){
                this.series[i].update(record);
                this.chart.series[i].setData(this.series[i].getTotals());
            } else
                serie.data[index].update(point);
        }

        if (this.xField){
            this.updatexAxisData();
        }
        }
    },

    /**
     * A function to delay the updates
     * @param {Integer} delay Set a custom delay
     */
    update: function(delay){
        var cdelay = delay||this.updateDelay;
        if(!this.updateTask){
            this.updateTask = new Ext.util.DelayedTask(this.draw, this);
        }
        this.updateTask.delay(cdelay);
    },

    // private
    onDataChange : function(){
        this.refresh();
    },

    // private
    onClear : function(){
        this.refresh();
    },

    // private
    onUpdate : function(ds, record){
        this.refreshRow(record);
    },

    // private
    onAdd : function(ds, records, index){
        var redraw = false, xFieldData = [];

        for (var i = 0; i < records.length; i++){
            var record = records[i];
            if (i==records.length-1) redraw = true;
            if (this.xField){
                xFieldData.push(record.data[this.xField]);
            }

            for (var x = 0; x < this.chart.series.length; x++){
                var serie = this.chart.series[x], s = this.series[x];
                var point = s.getData(record, index+i);
                if (!(s.type == 'pie' && s.useTotals)){
                    serie.addPoint(point, redraw);
                }
            }
        }
        if (this.xField){
            this.chart.xAxis[0].setCategories(xFieldData, true);
        }
 
    },


    //private
    onResize: function(){
        Ext.ux.HighChart.superclass.onResize.call(this);
        this.update();
    },

    // private
    onRemove : function(ds, record, index, isUpdate){
        for (var i = 0; i < this.series.length; i++){
            var s = this.series[i];
            if (s.type == 'pie' && s.useTotals){
                s.removeData(record, index);
                this.chart.series[i].setData(s.getTotals())
            } else {
                this.chart.series[i].data[index].remove(true)
            }
        }
        Ext.each(this.chart.series, function(serie){
            serie.data[index].remove(true);
        })
        if (this.xField){
            this.updatexAxisData();
        }
    },

    // private
    onLoad : function(){
        this.refresh();
    },

    destroy: function(){
        delete this.series;
        if(this.chart) {
            this.chart.destroy();
            delete this.chart;
        }
        
        this.bindStore(null);
        this.bindComponent(null);

        Ext.ux.HighChart.superclass.destroy.call(this);
    }
});
Ext.reg('highchart', Ext.ux.HighChart);

/**
 * @class Ext.ux.HighChart.Series
 * This class registers all available series, and provide backward compatibility
 * @constructor
 */
Ext.ux.HighChart.Series = function(){
    var items = new Array(),
    values = new Array();

    return {
        reg: function(id, cls){
            items.push(cls); values.push(id);
        },

        get: function(id){
            return items[values.indexOf(id)];
        }
    }
}();

/**
 * @class Ext.ux.HighChart.Serie
 * Series class for the highcharts widget.
 * @constructor
 */
Ext.ux.HighChart.Serie = function(config){
    config.type = this.type;
    if (!config.data){
        config.data = [];
    }
    Ext.apply(this, config); this.config = config;
}
Ext.ux.HighChart.Serie.prototype = {

    type: null,

    /**
     * The field used to access the x-axis value from the items from the data source.
     *
     * @property xField
     * @type String
     */
    xField: null,

    /**
     * The field used to access the y-axis value from the items from the data source.
     *
     * @property yField
     * @type String
     */
    yField: null,

    /**
     * The field used to hide the serie initial. Defaults to true.
     *
     * @property visible
     * @type boolean
     */
    visible: true,

    clear: Ext.emptyFn,
    
    getData: function(record, index){
        var yField = this.yField || this.dataIndex, xField = this.xField,
        point = {
            data: record.data,
            y: record.data[yField]
        };
        if (xField) point.x = record.data[xField];
        return point;
    },

    serieCls: true
};

/**
 * @class Ext.ux.HighChart.SplineSerie
 * @extends Ext.ux.HighChart.Serie
 * SplineSerie class for the charts widget.
 * @constructor
 */
Ext.ux.HighChart.SplineSerie = Ext.extend(Ext.ux.HighChart.Serie, {
    type: 'spline'
});
Ext.ux.HighChart.Series.reg('spline', Ext.ux.HighChart.SplineSerie);

/**
 * @class Ext.ux.HighChart.ColumnSerie
 * @extends Ext.ux.HighChart.Serie
 * ColumnSerie class for the charts widget.
 * @constructor
 */
Ext.ux.HighChart.ColumnSerie = Ext.extend(Ext.ux.HighChart.Serie, {
    type: 'column'
});
Ext.ux.HighChart.Series.reg('column', Ext.ux.HighChart.ColumnSerie);

/**
 * @class Ext.ux.HighChart.BarSerie
 * @extends Ext.ux.HighChart.Serie
 * BarSerie class for the charts widget.
 * @constructor
 */
Ext.ux.HighChart.BarSerie = Ext.extend(Ext.ux.HighChart.Serie, {
    type: 'bar'
});
Ext.ux.HighChart.Series.reg('bar', Ext.ux.HighChart.BarSerie);

/**
 * @class Ext.ux.HighChart.SplineSerie
 * @extends Ext.ux.HighChart.Serie
 * LineSerie class for the charts widget.
 * @constructor
 */
Ext.ux.HighChart.LineSerie = Ext.extend(Ext.ux.HighChart.Serie, {
    type: 'line'
});
Ext.ux.HighChart.Series.reg('line', Ext.ux.HighChart.LineSerie);

/**
 * @class Ext.ux.HighChart.SplineSerie
 * @extends Ext.ux.HighChart.Serie
 * AreaSerie class for the charts widget.
 * @constructor
 */
Ext.ux.HighChart.AreaSerie = Ext.extend(Ext.ux.HighChart.Serie, {
    type: 'area'
});
Ext.ux.HighChart.Series.reg('area', Ext.ux.HighChart.AreaSerie);

/**
 * @class Ext.ux.HighChart.SplineSerie
 * @extends Ext.ux.HighChart.Serie
 * AreasplineSerie class for the charts widget.
 * @constructor
 */
Ext.ux.HighChart.AreaSplineSerie = Ext.extend(Ext.ux.HighChart.Serie, {
    type: 'areaspline'
});
Ext.ux.HighChart.Series.reg('areaspline', Ext.ux.HighChart.AreaSplineSerie);

/**
 * @class Ext.ux.HighChart.ScatterSerie
 * @extends Ext.ux.HighChart.Serie
 * ScatterSerie class for the charts widget.
 * @constructor
 */
Ext.ux.HighChart.ScatterSerie = Ext.extend(Ext.ux.HighChart.Serie, {
    type: 'scatter'
});
Ext.ux.HighChart.Series.reg('scatter', Ext.ux.HighChart.ScatterSerie);

/**
 * @class Ext.ux.HighChart.PieSerie
 * @extends Ext.ux.HighChart.Serie
 * PieSerie class for the charts widget.
 * @constructor
 */
Ext.ux.HighChart.PieSerie = Ext.extend(Ext.ux.HighChart.Serie, {

    type: 'pie',

    /**
     * Categoriefield
     */
    categorieField: null,

    /**
     * Datafield
     */
    dataField: null,

    /**
     *
     */
    useTotals: false,
    
    /**
     * Columns
     */    
    columns: [],
    
    constructor: function(config){
        Ext.ux.HighChart.PieSerie.superclass.constructor.apply(this, arguments);
        if (this.useTotals){
            this.columnData = {};
            var length = this.columns.length;
            for (var i = 0; i<length;i++){
                this.columnData[this.columns[i]] = 100 / length;
            }
        }
    },
    
    //private
    addData: function(record){
        for (var i = 0; i<this.columns.length;i++){
            var c = this.columns[i];
            this.columnData[c] = this.columnData[c] + record.data[c];
        }
    },

    //private
    update: function(record){
        for (var i = 0; i<this.columns.length;i++){
            var c = this.columns[i];
            if (record.modified[c])
                this.columnData[c] = this.columnData[c] + record.data[c] - record.modified[c];
        }
    },

    //private
    removeData: function(record, index){
        for (var i = 0; i<this.columns.length;i++){
            var c = this.columns[i];
            this.columnData[c] = this.columnData[c] - record.data[c];
        }
    },

    //private
    clear: function(){
        for (var i = 0; i<this.columns.length;i++){
            var c = this.columns[i];
            this.columnData[c] = 0;
        }
    },

    //private
    getData: function(record, index){
        if (this.useTotals){
            this.addData(record);
            return [];
        }
        return [record.data[this.categorieField], record.data[this.dataField]];
    },


    getTotals: function(){
        var a = new Array();
        for (var i = 0; i<this.columns.length;i++){
            var c = this.columns[i];
            a.push([c, this.columnData[c]]);
        }
        return a;
    }
});
Ext.ux.HighChart.Series.reg('pie', Ext.ux.HighChart.PieSerie);