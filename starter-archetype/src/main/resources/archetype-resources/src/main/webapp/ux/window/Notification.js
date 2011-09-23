
/* 
 *	Notification / Toastwindow extension for Ext JS 4.x
 *
 *	Copyright (c) 2011 Eirik Lorentsen (http://www.eirik.net/)
 *
 *	Examples and documentation at: http://www.eirik.net/Ext/ux/window/Notification.html
 *
 *	Dual licensed under the MIT (http://www.opensource.org/licenses/mit-license.php) 
 *	and GPL (http://www.opensource.org/licenses/gpl-license.php) licenses.
 *
 *	Version: 1.3
 *	Last changed date: 2011-09-13
 */

Ext.define('Ext.ux.window.Notification', {
	extend: 'Ext.window.Window',
	alias: 'widget.uxNotification',

	title: 'Notification',

	cls: 'ux-notification-window',
	autoDestroy: true,
	autoHeight: true,
	plain: false,
	draggable: false,
	shadow: false,
	focus: Ext.emptyFn,

	// For alignment and to store array of rendered notifications. Defaults to document if not set.
	manager: null,

	useXAxis: false,

	// Options: br, bl, tr, tl
	corner: 'br',

	// Pixels between each notification
	spacing: 6,

	// Pixels from the managers borders to start the first notification
	paddingX: 30,
	paddingY: 10,

	slideInAnimation: 'easeIn',
	slideDownAnimation: 'bounceOut',
	autoDestroyDelay: 7000,
	slideInDelay: 1500,
	slideDownDelay: 1000,
	fadeDelay: 500,
	stickOnClick: true,
	stickWhileHover: true,

	// Private. Do not override!
	underDestruction: false,
	readyToDestroy: false,
	// Caching position coordinate to avoid windows overlapping when fading in simultaneously
	xPos: 0,
	yPos: 0,

	statics: {
		defaultManager: {
			notifications: [],
			el: null
		},
		info: function(title, text) {
			this.notification(title, text, false);
		},
		error: function(title, text) {
			this.notification(title, text, true);
		},
		notification: function(title, text, error) {
			Ext.create('widget.uxNotification', {
				corner: 'br',
				title: title,
				manager: 'notification',
				iconCls: error ? 'ux-notification-icon-error' : 'ux-notification-icon-information',
				autoDestroyDelay: 4000,
				slideInDelay: 800,
				slideDownDelay: 1500,
				slideInAnimation: 'elasticIn',
				slideDownAnimation: 'elasticIn',				
				html: text,
				bodyStyle: {
						'background-color': error ? '#F76541' : '#5FFB17',
						'color': error ? 'white' : 'black',
						'font-weight': 'bold'
				}
			}).show();
			
		}
	},

	initComponent: function() {
		var me = this;

		me.callParent(arguments);

		switch (me.corner) {
			case 'br':
				me.paddingFactorX = -1;
				me.paddingFactorY = -1;
				me.siblingAlignment = "br-br";
				if (me.useXAxis) {
					me.managerAlignment = "bl-br";
				} else {
					me.managerAlignment = "tr-br";
				}
				break;
			case 'bl':
				me.paddingFactorX = 1;
				me.paddingFactorY = -1;
				me.siblingAlignment = "bl-bl";
				if (me.useXAxis) {
					me.managerAlignment = "br-bl";
				} else {
					me.managerAlignment = "tl-bl";
				}
				break;
			case 'tr':
				me.paddingFactorX = -1;
				me.paddingFactorY = 1;
				me.siblingAlignment = "tr-tr";
				if (me.useXAxis) {
					me.managerAlignment = "tl-tr";
				} else {
					me.managerAlignment = "br-tr";
				}
				break;
			case 'tl':
				me.paddingFactorX = 1;
				me.paddingFactorY = 1;
				me.siblingAlignment = "tl-tl";
				if (me.useXAxis) {
					me.managerAlignment = "tr-tl";
				} else {
					me.managerAlignment = "bl-tl";
				}
				break;
		}

		if (typeof me.manager == 'string') {
			me.manager = Ext.getCmp(me.manager);
		}

		// If no manager is provided or found, then the static object is used and the el property pointed to the body document.
		if (!me.manager) {
			me.manager = me.statics().defaultManager;

			if (!me.manager.el) {
				me.manager.el = Ext.getBody();
			}
		}
		
		if (typeof me.manager.notifications == 'undefined') {
			me.manager.notifications = [];
		}

	},

	onRender: function() {
		var me = this;

        	me.callParent(arguments);

		if (me.stickOnClick) {
		if (me.body && me.body.dom) {
			Ext.fly(me.body.dom).on('click', me.cancelAutoDestroy, me);
		}
		}

		if (me.autoDestroy) {
			me.task = new Ext.util.DelayedTask(me.doAutoDestroy, me);
	 		me.task.delay(me.autoDestroyDelay);
		}

		me.el.hover(
			function () {
				me.mouseIsOver = true;
			},
			function () {
				me.mouseIsOver = false;
	},
			me
		);

	},

	getXposAlignedToManager: function () {
		var me = this;

		var xPos = 0;

		if (me.corner == 'br' || me.corner == 'tr') {
			xPos += me.manager.el.getRight();
			xPos -= (me.el.getWidth() + me.paddingX);
		} else {
			xPos += me.manager.el.getLeft();
			xPos += me.paddingX;
		}

		return xPos;
	},

	getYposAlignedToManager: function () {
		var me = this;

		var yPos = 0;

		if (me.corner == 'br' || me.corner == 'bl') {
			yPos += me.manager.el.getBottom();
			yPos -= (me.el.getHeight() + me.paddingY);
		} else {
			yPos += me.manager.el.getTop();
			yPos += me.paddingY;
		}

		return yPos;
	},

	getXposAlignedToSibling: function (sibling) {
		var me = this;

		if (me.useXAxis) {
			if (me.corner == 'tl' || me.corner == 'bl') {
				// Using sibling's width when adding
				return (sibling.xPos + sibling.el.getWidth() + sibling.spacing);
			} else {
				// Using own width when subtracting
				return (sibling.xPos - me.el.getWidth() - me.spacing);
			}
		} else {
			return me.el.getLeft();
		}

	},

	getYposAlignedToSibling: function (sibling) {
		var me = this;

		if (me.useXAxis) {
			return me.el.getTop();
		} else {
			if (me.corner == 'tr' || me.corner == 'tl') {
				// Using sibling's width when adding
				return (sibling.yPos + sibling.el.getHeight() + sibling.spacing);				
			} else {
				// Using own width when subtracting
				return (sibling.yPos - me.el.getHeight() - sibling.spacing);
			}
		}
	},

	beforeShow: function () {
		var me = this;

		if (me.manager.notifications.length) {
			me.el.alignTo(me.manager.notifications[me.manager.notifications.length - 1].el, me.siblingAlignment, [0, 0]);
			me.xPos = me.getXposAlignedToSibling(me.manager.notifications[me.manager.notifications.length - 1]);
			me.yPos = me.getYposAlignedToSibling(me.manager.notifications[me.manager.notifications.length - 1]);
		} else {
			me.el.alignTo(me.manager.el, me.managerAlignment, [(me.paddingX * me.paddingFactorX), (me.paddingY * me.paddingFactorY)]);
			me.xPos = me.getXposAlignedToManager();
			me.yPos = me.getYposAlignedToManager();
		}

		Ext.Array.include(me.manager.notifications, me);

		me.el.animate({
			to: {
				x: me.xPos,
				y: me.yPos
			},
			easing: me.slideInAnimation,
			duration: me.slideInDelay,
			dynamic: true
		});

	},

	slideDown: function () {
		var me = this;

		var index = Ext.Array.indexOf(me.manager.notifications, me)

		// Not animating the element if it already started to destroy itself
		if (!me.underDestruction && me.el) {

			if (index) {
				me.xPos = me.getXposAlignedToSibling(me.manager.notifications[index - 1]);
				me.yPos = me.getYposAlignedToSibling(me.manager.notifications[index - 1]);
			} else {
				me.xPos = me.getXposAlignedToManager();
				me.yPos = me.getYposAlignedToManager();
			}

			me.el.animate({
				to: {
					x: me.xPos,
					y: me.yPos
				},
				easing: me.slideDownAnimation,
				duration: me.slideDownDelay,
				dynamic: true
			});
		}
	},

	cancelAutoDestroy: function() {
		var me = this;

		me.addClass('notification-fixed');
		if (me.autoDestroy) {
			me.task.cancel();
			me.autoDestroy = false;
		}
	},

	doAutoDestroy: function () {
		var me = this;

		/* Delayed destruction when mouse leaves the component.
		   Doing this before me.mouseIsOver is checked below to avoid a race condition while resetting event handlers */
		me.el.hover(
			function () {
			},
			function () {
				me.destroy();
			},
			me
		);
		
		if (!(me.stickWhileHover && me.mouseIsOver)) {
			// Destroy immediately
			me.destroy();
			}
		},

	listeners: {
		'beforehide': function (me, eOpts) {
				if (!me.underDestruction) {
				// Force window to animate and destroy, instead of hiding
				me.destroy();
				return false;
			}
		}
	},

	destroy: function () {
		var me = this;

		// Avoids starting the last animation on an element already underway with its destruction
		if (!me.underDestruction) {

		me.underDestruction = true;

		me.cancelAutoDestroy();
		me.stopAnimation();

		me.el.animate({
			to: {
				opacity: 0
			},
			easing: 'easeIn',
			duration: me.fadeDelay,
			dynamic: true,
			listeners: {
				afteranimate: function () {

					var index = Ext.Array.indexOf(me.manager.notifications, me);
					if (index != -1) {
						Ext.Array.erase(me.manager.notifications, index, 1);

						// Slide "down" all notifications "above" the destroyed one
						for (;index < me.manager.notifications.length; index++) {
							me.manager.notifications[index].slideDown();
						}
					}
					me.readyToDestroy = true;
					me.destroy();
				}
			}
		});
			}

		// After animation is complete the component may be destroyed
		if (me.readyToDestroy) {
			this.callParent(arguments);
		}
	}

});


/*	Changelog:
 *
 *	2011-09-01 - 1.1: Bugfix. Array.indexOf not universally implemented, causing errors in IE<=8. Replaced with Ext.Array.indexOf.
 *	2011-09-12 - 1.2: Added config options: stickOnClick and stickWhileHover
 *	2011-09-13 - 1.3: Cleaned up component destruction
 *
 */