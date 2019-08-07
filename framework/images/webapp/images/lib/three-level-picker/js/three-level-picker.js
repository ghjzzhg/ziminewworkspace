(function ($) {

    'use strict';

    var NAMESPACE = 'threeLevelPicker';
    var EVENT_CHANGE = 'change.' + NAMESPACE;
    var LEVEL1 = 'level1';
    var LEVEL2 = 'level2';
    var LEVEL3 = 'level3';

    function ThreeLevelPicker(element, options) {
        this.$element = $(element);
        this.$dropdown = null;
        this.options = $.extend({}, ThreeLevelPicker.DEFAULTS, $.isPlainObject(options) && options);
        this.active = false;
        this.dems = [];
        this.needBlur = false;
        this.init();
    }

    ThreeLevelPicker.prototype = {
        constructor: ThreeLevelPicker,

        init: function () {

            this.defineDems();

            this.render();

            this.bind();

            this.active = true;
        },

        render: function () {
            var p = this.getPosition(),
                placeholder = this.$element.attr('placeholder') || this.options.placeholder,
                textspan = '<span class="three-level-picker-span" style="' +
                    this.getWidthStyle(p.width) + 'height:' +
                    p.height + 'px;line-height:' + (p.height - 1) + 'px;">' +
                    (placeholder ? '<span class="placeholder">' + placeholder + '</span>' : '') +
                    '<span class="title"></span><div class="arrow"></div>' + '</span>',

                dropdown = '<div class="three-level-picker-dropdown" style="left:0px;top:100%;' +
                    this.getWidthStyle(p.width, true) + '">' +
                    '<div class="three-level-select-wrap">' +
                    '<div class="three-level-select-tab">' +
                    '<a class="active" data-count="level1">第一级</a>' +
                    (this.includeDem('level2') ? '<a data-count="level2">第二级</a>' : '') +
                    (this.includeDem('level3') ? '<a data-count="level3">第三级</a>' : '') + '</div>' +
                    '<div class="three-level-select-content">' +
                    '<div class="three-level-select level1" data-count="level1"></div>' +
                    (this.includeDem('level2') ? '<div class="three-level-select level2" data-count="level2"></div>' : '') +
                    (this.includeDem('level3') ? '<div class="three-level-select level3" data-count="level3"></div>' : '') +
                    '</div></div>';

            this.$element.addClass('three-level-picker-input');
            this.$textspan = $(textspan).insertAfter(this.$element);
            this.$dropdown = $(dropdown).insertAfter(this.$textspan);
            var $select = this.$dropdown.find('.three-level-select');

            // setup this.$level1, this.$level2 and/or this.$level3 object
            $.each(this.dems, $.proxy(function (i, type) {
                this['$' + type] = $select.filter('.' + type + '');
            }, this));

            this.refresh();
        },

        refresh: function (force) {
            // clean the data-item for each $select
            var $select = this.$dropdown.find('.three-level-select');
            $select.data('item', null);
            // parse value from value of the target $element
            var val = this.$element.val() || '';
            val = val.split('/');
            $.each(this.dems, $.proxy(function (i, type) {
                if (val[i] && i < val.length) {
                    this.options[type] = val[i];
                } else if (force) {
                    this.options[type] = '';
                }
                this.output(type);
            }, this));
            this.tab(LEVEL1);
            this.feedText();
            this.feedVal();
        },

        defineDems: function () {
            var stop = false;
            $.each([LEVEL1, LEVEL2, LEVEL3], $.proxy(function (i, type) {
                if (!stop) {
                    this.dems.push(type);
                }
                if (type === this.options.level) {
                    stop = true;
                }
            }, this));
        },

        includeDem: function (type) {
            return $.inArray(type, this.dems) !== -1;
        },

        getPosition: function () {
            var p, h, w, s, pw;
            p = this.$element.position();
            s = this.getSize(this.$element);
            h = s.height;
            w = s.width;
            if (this.options.responsive) {
                pw = this.$element.offsetParent().width();
                if (pw) {
                    w = w / pw;
                    if (w > 0.99) {
                        w = 1;
                    }
                    w = w * 100 + '%';
                }
            }

            return {
                top: p.top || 0,
                left: p.left || 0,
                height: h,
                width: w
            };
        },

        getSize: function ($dom) {
            var $wrap, $clone, sizes;
            if (!$dom.is(':visible')) {
                $wrap = $("<div />").appendTo($("body"));
                $wrap.css({
                    "position": "absolute !important",
                    "visibility": "hidden !important",
                    "display": "block !important"
                });

                $clone = $dom.clone().appendTo($wrap);

                sizes = {
                    width: $clone.outerWidth(),
                    height: $clone.outerHeight()
                };

                $wrap.remove();
            } else {
                sizes = {
                    width: $dom.outerWidth(),
                    height: $dom.outerHeight()
                };
            }

            return sizes;
        },

        getWidthStyle: function (w, dropdown) {
            if (this.options.responsive && !$.isNumeric(w)) {
                return 'width:' + w + ';';
            } else {
                return 'width:' + (dropdown ? Math.max(320, w) : w) + 'px;';
            }
        },

        bind: function () {
            var $this = this;

            $(document).on('click', (this._mouteclick = function (e) {
                var $target = $(e.target);
                var $dropdown, $span, $input;
                if ($target.is('.three-level-picker-span')) {
                    $span = $target;
                } else if ($target.is('.three-level-picker-span *')) {
                    $span = $target.parents('.three-level-picker-span');
                }
                if ($target.is('.three-level-picker-input')) {
                    $input = $target;
                }
                if ($target.is('.three-level-picker-dropdown')) {
                    $dropdown = $target;
                } else if ($target.is('.three-level-picker-dropdown *')) {
                    $dropdown = $target.parents('.three-level-picker-dropdown');
                }
                if ((!$input && !$span && !$dropdown) ||
                    ($span && $span.get(0) !== $this.$textspan.get(0)) ||
                    ($input && $input.get(0) !== $this.$element.get(0)) ||
                    ($dropdown && $dropdown.get(0) !== $this.$dropdown.get(0))) {
                    $this.close(true);
                }

            }));

            this.$element.on('change', (this._changeElement = $.proxy(function () {
                this.close(true);
                this.refresh(true);
            }, this))).on('focus', (this._focusElement = $.proxy(function () {
                this.needBlur = true;
                this.open();
            }, this))).on('blur', (this._blurElement = $.proxy(function () {
                if (this.needBlur) {
                    this.needBlur = false;
                    this.close(true);
                }
            }, this)));

            this.$textspan.on('click', function (e) {
                var $target = $(e.target), type;
                $this.needBlur = false;
                if ($target.is('.select-item')) {
                    type = $target.data('count');
                    $this.open(type);
                } else {
                    if ($this.$dropdown.is(':visible')) {
                        $this.close();
                    } else {
                        $this.open();
                    }
                }
            }).on('mousedown', function () {
                $this.needBlur = false;
            });

            this.$dropdown.on('click', '.three-level-select a', function () {
                var $select = $(this).parents('.three-level-select');
                var $active = $select.find('a.active');
                var last = $select.next().length === 0;
                $active.removeClass('active');
                $(this).addClass('active');
                if ($active.data('code') !== $(this).data('code')) {
                    $select.data('item', {
                        name: $(this).attr('title'), code: $(this).data('code')
                    });
                    $(this).trigger(EVENT_CHANGE);
                    $this.feedText();
                    $this.feedVal(true);
                    if (last) {
                        $this.close();
                    }
                }
            }).on('click', '.three-level-select-tab a', function () {
                if (!$(this).hasClass('active')) {
                    var type = $(this).data('count');
                    $this.tab(type);
                }
            }).on('mousedown', function () {
                $this.needBlur = false;
            });

            if (this.$level1) {
                this.$level1.on(EVENT_CHANGE, (this._changeLevel1 = $.proxy(function () {
                    this.output(LEVEL2);
                    this.output(LEVEL3);
                    this.tab(LEVEL2);
                }, this)));
            }

            if (this.$level2) {
                this.$level2.on(EVENT_CHANGE, (this._changeLevel2 = $.proxy(function () {
                    this.output(LEVEL3);
                    this.tab(LEVEL3);
                }, this)));
            }
        },

        open: function (type) {
            type = type || LEVEL1;
            this.$dropdown.show();
            this.$textspan.addClass('open').addClass('focus');
            this.tab(type);
        },

        close: function (blur) {
            this.$dropdown.hide();
            this.$textspan.removeClass('open');
            if (blur) {
                this.$textspan.removeClass('focus');
            }
        },

        unbind: function () {

            $(document).off('click', this._mouteclick);

            this.$element.off('change', this._changeElement);
            this.$element.off('focus', this._focusElement);
            this.$element.off('blur', this._blurElement);

            this.$textspan.off('click');
            this.$textspan.off('mousedown');

            this.$dropdown.off('click');
            this.$dropdown.off('mousedown');

            if (this.$level1) {
                this.$level1.off(EVENT_CHANGE, this._changeLevel1);
            }

            if (this.$level2) {
                this.$level2.off(EVENT_CHANGE, this._changeLevel2);
            }
        },

        getText: function () {
            var text = '';
            this.$dropdown.find('.three-level-select')
                .each(function () {
                    var item = $(this).data('item'),
                        type = $(this).data('count');
                    if (item) {
                        text += ($(this).hasClass('level1') ? '' : '/') + '<span class="select-item" data-count="' +
                            type + '" data-code="' + item.code + '">' + item.name + '</span>';
                    }
                });
            return text;
        },

        getPlaceHolder: function () {
            return this.$element.attr('placeholder') || this.options.placeholder;
        },

        feedText: function () {
            var text = this.getText();
            if (text) {
                this.$textspan.find('>.placeholder').hide();
                this.$textspan.find('>.title').html(this.getText()).show();
            } else {
                this.$textspan.find('>.placeholder').text(this.getPlaceHolder()).show();
                this.$textspan.find('>.title').html('').hide();
            }
        },

        getVal: function () {
            var text = '';
            this.$dropdown.find('.three-level-select')
                .each(function () {
                    var item = $(this).data('item');
                    if (item) {
                        text += ($(this).hasClass('level1') ? '' : '/') + item.name;
                    }
                });
            return text;
        },

        feedVal: function (trigger) {
            this.$element.val(this.getVal());
            if(trigger) {
                this.$element.trigger('cp:updated');
            }
        },

        output: function (type) {
            var options = this.options;
            //var placeholders = this.placeholders;
            var $select = this['$' + type];
            var data = type === LEVEL1 ? {} : [];
            var item;
            var levels;
            var code;
            var matched = null;
            var value;

            if (!$select || !$select.length) {
                return;
            }

            item = $select.data('item');

            value = (item ? item.name : null) || options[type];

            code = (
                type === LEVEL1 ? 1 :
                    type === LEVEL2 ? this.$level1 && this.$level1.find('.active').data('code') :
                        type === LEVEL3 ? this.$level2 && this.$level2.find('.active').data('code') : code
            );
            var levelsData = options.source;
            if(typeof(levelsData) === 'string'){
                levelsData = window[levelsData];
            }
            if(!levelsData){
                throw new Error('没有数据源');
            }
            levels = $.isNumeric(code) ? levelsData[code] : null;

            if ($.isPlainObject(levels)) {
                $.each(levels, function (code, name) {
                    var provs;
                    if (type === LEVEL1) {
                        provs = [];
                        for (var i = 0; i < name.length; i++) {
                            if (name[i].name === value) {
                                matched = {
                                    code: name[i].code,
                                    name: name[i].name
                                };
                            }
                            provs.push({
                                code: name[i].code,
                                name: name[i].name,
                                selected: name[i].name === value
                            });
                        }
                        data[code] = provs;
                    } else {
                        if (name === value) {
                            matched = {
                                code: code,
                                name: name
                            };
                        }
                        data.push({
                            code: code,
                            name: name,
                            selected: name === value
                        });
                    }
                });
            }

            $select.html(type === LEVEL1 ? this.getLevel1List(data) :
                this.getList(data, type));
            $select.data('item', matched);
        },

        getLevel1List: function (data) {
            var list = [],
                $this = this,
                simple = this.options.simple;

            $.each(data, function (i, n) {
                list.push('<dl class="clearfix">');
                list.push('<dt>' + i + '</dt><dd>');
                $.each(n, function (j, m) {
                    list.push(
                        '<a' +
                        ' title="' + (m.name || '') + '"' +
                        ' data-code="' + (m.code || '') + '"' +
                        ' class="' +
                        (m.selected ? ' active' : '') +
                        '">' +
                        ( simple ? $this.simplize(m.name, LEVEL1) : m.name) +
                        '</a>');
                });
                list.push('</dd></dl>');
            });

            return list.join('');
        },

        getList: function (data, type) {
            var list = [],
                $this = this,
                simple = this.options.simple;
            list.push('<dl class="clearfix"><dd>');

            $.each(data, function (i, n) {
                list.push(
                    '<a' +
                    ' title="' + (n.name || '') + '"' +
                    ' data-code="' + (n.code || '') + '"' +
                    ' class="' +
                    (n.selected ? ' active' : '') +
                    '">' +
                    ( simple ? $this.simplize(n.name, type) : n.name) +
                    '</a>');
            });
            list.push('</dd></dl>');

            return list.join('');
        },

        simplize: function (name, type) {
            name = name || '';
            return name;
        },

        tab: function (type) {
            var $selects = this.$dropdown.find('.three-level-select');
            var $tabs = this.$dropdown.find('.three-level-select-tab > a');
            var $select = this['$' + type];
            var $tab = this.$dropdown.find('.three-level-select-tab > a[data-count="' + type + '"]');
            if ($select) {
                $selects.hide();
                $select.show();
                $tabs.removeClass('active');
                $tab.addClass('active');
            }
        },

        reset: function () {
            this.$element.val(null).trigger('change');
        },

        destroy: function () {
            this.unbind();
            this.$element.removeData(NAMESPACE).removeClass('three-level-picker-input');
            this.$textspan.remove();
            this.$dropdown.remove();
        }
    };

    ThreeLevelPicker.DEFAULTS = {
        simple: false,
        responsive: false,
        placeholder: '请选择',
        level: 'level3',
        level1: '',
        level2: '',
        level3: ''
    };

    ThreeLevelPicker.setDefaults = function (options) {
        $.extend(ThreeLevelPicker.DEFAULTS, options);
    };

    // Save the other threeLevelPicker
    ThreeLevelPicker.other = $.fn.threeLevelPicker;

    // Register as jQuery plugin
    $.fn.threeLevelPicker = function (option) {
        var args = [].slice.call(arguments, 1);

        return this.each(function () {
            var $this = $(this);
            var data = $this.data(NAMESPACE);
            var options;
            var fn;

            if (!data) {
                if (/destroy/.test(option)) {
                    return;
                }

                options = $.extend({}, $this.data(), $.isPlainObject(option) && option);
                $this.data(NAMESPACE, (data = new ThreeLevelPicker(this, options)));
            }

            if (typeof option === 'string' && $.isFunction(fn = data[option])) {
                fn.apply(data, args);
            }
        });
    };

    $.fn.threeLevelPicker.Constructor = ThreeLevelPicker;
    $.fn.threeLevelPicker.setDefaults = ThreeLevelPicker.setDefaults;

    // No conflict
    $.fn.threeLevelPicker.noConflict = function () {
        $.fn.threeLevelPicker = ThreeLevelPicker.other;
        return this;
    };
})(jQuery);