Ext.override(Ext.draw.engine.Svg, {

    createSpriteElement: function(sprite) {
        // Create svg element and append to the DOM.
        var el = this.createSvgElement(sprite.type);
        el.id = sprite.id;
        if (el.style) {
            el.style.webkitTapHighlightColor = "rgba(0,0,0,0)";
        }
        sprite.el = Ext.get(el);
        this.applyZIndex(sprite); //performs the insertion
        sprite.matrix = new Ext.draw.Matrix();
        sprite.bbox = {
            plain: 0,
            transform: 0
        };
        this.applyAttrs(sprite);
        this.applyTransformations(sprite);
        sprite.fireEvent("render", sprite);
        return el;
    },
    
    transform: function(sprite, matrixOnly) {
        var me = this,
            matrix = new Ext.draw.Matrix(),
            transforms = sprite.transformations,
            transformsLength = transforms.length,
            i = 0,
            transform, type;
            
        for (; i < transformsLength; i++) {
            transform = transforms[i];
            type = transform.type;
            if (type == "translate") {
                matrix.translate(transform.x, transform.y);
            }
            else if (type == "rotate") {
                matrix.rotate(transform.degrees, transform.x, transform.y);
            }
            else if (type == "scale") {
                matrix.scale(transform.x, transform.y, transform.centerX, transform.centerY);
            }
        }
        sprite.matrix = matrix;
        if (!matrixOnly) {
            sprite.el.set({transform: matrix.toSvg()});
        }
    },


    setSize: function(width, height) {
        var me = this,
            el = me.el;
        
        width = +width || me.width;
        height = +height || me.height;
        me.width = width;
        me.height = height;


        el.setSize(width, height);
        el.set({
            width: width,
            height: height
        });
        me.callParent([width, height]);
    },


    render: function (container) {
        var me = this,
            width,
            height,
            el,
            defs,
            bgRect,
            webkitRect;
        if (!me.el) {
            width = me.width || 0;
            height = me.height || 0;
            el = me.createSvgElement('svg', {
                xmlns: "http:/" + "/www.w3.org/2000/svg",
                version: 1.1,
                width: width,
                height: height
            });
            defs = me.getDefs();


            // Create a rect that is always the same size as the svg root; this serves 2 purposes:
            // (1) It allows mouse events to be fired over empty areas in Webkit, and (2) we can
            // use it rather than the svg element for retrieving the correct client rect of the
            // surface in Mozilla (see https://bugzilla.mozilla.org/show_bug.cgi?id=530985)
            bgRect = me.createSvgElement("rect", {
                width: "100%",
                height: "100%",
                fill: "#000",
                stroke: "none",
                opacity: 0
            });
            
            if (Ext.isSafari3) {
                // Rect that we will show/hide to fix old WebKit bug with rendering issues.
                webkitRect = me.createSvgElement("rect", {
                    x: -10,
                    y: -10,
                    width: "110%",
                    height: "110%",
                    fill: "none",
                    stroke: "#000"
                });
            }
            el.appendChild(defs);
            if (Ext.isSafari3) {
                el.appendChild(webkitRect);
            }
            el.appendChild(bgRect);
            container.appendChild(el);
            me.el = Ext.get(el);
            me.bgRect = Ext.get(bgRect);
            if (Ext.isSafari3) {
                me.webkitRect = Ext.get(webkitRect);
                me.webkitRect.hide();
            }
            me.el.on({
                scope: me,
                mouseup: me.onMouseUp,
                mousedown: me.onMouseDown,
                mouseover: me.onMouseOver,
                mouseout: me.onMouseOut,
                mousemove: me.onMouseMove,
                mouseenter: me.onMouseEnter,
                mouseleave: me.onMouseLeave,
                click: me.onClick
            });
        }
        me.renderAll();
    },


    tuneText: function (sprite, attrs) {
        var el = sprite.el.dom,
            tspans = [],
            height, tspan, text, i, ln, texts, factor, x;


        if (attrs.hasOwnProperty("text")) {
            //only create new tspans for text lines if the text has been 
            //updated or if it's the first time we're setting the text
            //into the sprite.


            //get the actual rendered text.
            text = sprite.tspans && Ext.Array.map(sprite.tspans, function(t) { return t.textContent; }).join('');


            if (!sprite.tspans || attrs.text != text) {
                tspans = this.setText(sprite, attrs.text);
                sprite.tspans = tspans;
            //for all other cases reuse the tspans previously created.
            } else {
                tspans = sprite.tspans || [];
            }
        }
        // Normalize baseline via a DY shift of first tspan. Shift other rows by height * line height (1.2)
        if (tspans.length) {
            height = this.getBBoxText(sprite).height;
            x = sprite.el.dom.getAttribute("x");
            for (i = 0, ln = tspans.length; i < ln; i++) {
                // The text baseline for FireFox 3.0 and 3.5 is different than other SVG implementations
                // so we are going to normalize that here
                factor = (Ext.isFF3_0 || Ext.isFF3_5) ? 2 : 4;
                tspans[i].setAttribute("x", x);
                tspans[i].setAttribute("dy", i ? height * 1.2 : height / factor);
            }
            sprite.dirty = true;
        }
    },


    setText: function(sprite, textString) {
         var me = this,
             el = sprite.el.dom,
             tspans = [],
             height, tspan, text, i, ln, texts;
        
        while (el.firstChild) {
            el.removeChild(el.firstChild);
        }
        // Wrap each row into tspan to emulate rows
        texts = String(textString).split("\n");
        for (i = 0, ln = texts.length; i < ln; i++) {
            text = texts[i];
            if (text) {
                tspan = me.createSvgElement("tspan");
                tspan.appendChild(document.createTextNode(Ext.htmlDecode(text)));
                el.appendChild(tspan);
                tspans[i] = tspan;
            }
        }
        return tspans;
    },
    
    renderItem: function (sprite) {
        if (!this.el) {
            return;
        }
        if (!sprite.el) {
            this.createSpriteElement(sprite);
        }
        if (sprite.zIndexDirty) {
            this.applyZIndex(sprite);
        }
        if (sprite.dirty) {
            this.applyAttrs(sprite);
            if (sprite.dirtyTransform) {
                this.applyTransformations(sprite);
            }
        }
    },    


    applyAttrs: function (sprite) {
        var me = this,
            el = sprite.el,
            group = sprite.group,
            sattr = sprite.attr,
            parsers = me.parsers,
            //Safari does not handle linear gradients correctly in quirksmode
            //ref: https://bugs.webkit.org/show_bug.cgi?id=41952
            //ref: EXTJSIV-1472
            gradientsMap = me.gradientsMap || {},
            safariFix = Ext.isSafari && !Ext.isStrict,
            groups, i, ln, attrs, font, key, style, name, rect;


        if (group) {
            groups = [].concat(group);
            ln = groups.length;
            for (i = 0; i < ln; i++) {
                group = groups[i];
                me.getGroup(group).add(sprite);
            }
            delete sprite.group;
        }
        attrs = me.scrubAttrs(sprite) || {};


        // if (sprite.dirtyPath) {
            sprite.bbox.plain = 0;
            sprite.bbox.transform = 0;
            if (sprite.type == "circle" || sprite.type == "ellipse") {
                attrs.cx = attrs.cx || attrs.x;
                attrs.cy = attrs.cy || attrs.y;
            }
            else if (sprite.type == "rect") {
                attrs.rx = attrs.ry = attrs.r;
            }
            else if (sprite.type == "path" && attrs.d) {
                attrs.d = Ext.draw.Draw.pathToString(Ext.draw.Draw.pathToAbsolute(attrs.d));
            }
            sprite.dirtyPath = false;
        // }
        // else {
        //     delete attrs.d;
        // }


        if (attrs['clip-rect']) {
            me.setClip(sprite, attrs);
            delete attrs['clip-rect'];
        }
        if (sprite.type == 'text' && attrs.font && sprite.dirtyFont) {
            el.set({ style: "font: " + attrs.font});
        }
        if (sprite.type == "image") {
            el.dom.setAttributeNS(me.xlink, "href", attrs.src);
        }
        Ext.applyIf(attrs, me.minDefaults[sprite.type]);


        if (sprite.dirtyHidden) {
            (sattr.hidden) ? me.hidePrim(sprite) : me.showPrim(sprite);
            sprite.dirtyHidden = false;
        }
        for (key in attrs) {
            if (attrs.hasOwnProperty(key) && attrs[key] != null) {
                //Safari does not handle linear gradients correctly in quirksmode
                //ref: https://bugs.webkit.org/show_bug.cgi?id=41952
                //ref: EXTJSIV-1472
                //if we're Safari in QuirksMode and we're applying some color attribute and the value of that
                //attribute is a reference to a gradient then assign a plain color to that value instead of the gradient.
                if (safariFix && ('color|stroke|fill'.indexOf(key) > -1) && (attrs[key] in gradientsMap)) {
                    attrs[key] = gradientsMap[attrs[key]];
                }
                if (key in parsers) {
                    el.dom.setAttribute(key, parsers[key](attrs[key], sprite, me));
                } else {
                    el.dom.setAttribute(key, attrs[key]);
                }
            }
        }
        
        if (sprite.type == 'text') {
            me.tuneText(sprite, attrs);
        }
        sprite.dirtyFont = false;


        //set styles
        style = sattr.style;
        if (style) {
            el.setStyle(style);
        }


        sprite.dirty = false;


        if (Ext.isSafari3) {
            // Refreshing the view to fix bug EXTJSIV-1: rendering issue in old Safari 3
            me.webkitRect.show();
            setTimeout(function () {
                me.webkitRect.hide();
            });
        }
    }
});           