Ext.define('Starter.controller.PollChart', {
	extend: 'Ext.app.Controller',

	stores: [ 'PollChart' ],
	models: [ 'PollChart' ],
	views: [ 'poll.PollChart' ],

	refs: [ {
		ref: 'pollchart',
		selector: 'pollchart'
	}, {
		ref: 'pollchartCmp',
		selector: 'pollchart chart'
	}, {
		ref: 'controlButton',
		selector: 'pollchart button[action=control]'
	} ],

	init: function() {
		this.control({
			'pollchart': {
				beforerender: this.onBeforeRender,
				destroy: this.onDestroy,
				beforeactivate: this.onBeforeActivate,
				beforedeactivate: this.onBeforeDeActivate
			},
			'pollchart button[action=control]': {
				click: this.controlPolling
			}
		});
	},

	onBeforeRender: function(cmp) {
		var store = this.getPollChartStore(), model = this.getPollChartModel();

		this.provider = Ext.direct.Manager.getProvider('chartdatapoller');
		this.provider.addListener('data', function(provider, event) {
			if (store.getCount() > 20) {
				store.removeAt(0);
			}

			var record = model.create({
				time: event.data.date,
				points: event.data.value
			});

			store.add(record);
		});
	},

	controlPolling: function(button, event) {
		if (button.getText() == 'Start') {
			button.setText(i18n.chart_stop);
			button.setIconCls('icon-stop');
			this.provider.connect();
		} else {
			button.setText(i18n.chart_start);
			button.setIconCls('icon-start');
			this.provider.disconnect();
		}
	},

	onBeforeActivate: function() {
		if (this.getControlButton().getText() !== i18n.chart_start) {
			this.provider.connect();
		}
	},

	onBeforeDeActivate: function() {
		this.provider.disconnect();
	},

	onDestroy: function() {
		this.provider.disconnect();
	}

});
