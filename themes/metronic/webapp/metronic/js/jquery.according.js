(function (j) {
    j.fn.extend({
        accordion: function () {
            return this.each(function () {
                function b(c, b) {
                    var $c = jQuery(c);
                    $c.parent(d).siblings().removeClass(e).children(f).slideUp(g);
                    //$(c).parent(d).siblings().removeClass(e).find('.fa-chevron-right').addClass('fa-chevron-down');
                    // $(c).parent(d).find('.fa-chevron-right').addClass('fa-chevron-down');
                    $c.siblings(f)[b || h](b == "show" ? g : !1, function () {
                        var $$c = jQuery(c);
                        $$c.siblings(f).is(":visible") ? $$c.parents(d).not(a.parents()).addClass(e) : $$c.parent(d).removeClass(e);
                        b == "show" && $$c.parents(d).not(a.parents()).addClass(e);
                        $$c.parents().show()
                    })
                }
                var a = jQuery(this),
                    e = "active",
                    h = "slideToggle",
                    f = "ul, div",
                    g = "fast",
                    d = "li";
                if (a.data("accordiated")) return !1;
                jQuery.each(a.find("ul, li>div"),
                    function () {
                        jQuery(this).data("accordiated", !0);
                        jQuery(this).hide()

                    });
                jQuery.each(a.find("a"), function () {
                    jQuery(this).click(function (e) {

                        if (!String.prototype.startsWith) {
                            Object.defineProperty(String.prototype, 'startsWith', {
                                enumerable: false,
                                configurable: false,
                                writable: false,
                                value: function (searchString, position) {
                                    position = position || 0;
                                    return this.indexOf(searchString, position) === position

                                }
                            });
                        }

                        var $this = jQuery(this), str = $this.attr('href');

                        if(str.startsWith("#"))
                        {
                            e.preventDefault();
                        }



                        /*if($(this).find('.chevron').hasClass('ti-angle-right'))
                        {
                            $(this).find('.chevron').toggleClass('ti-angle-right')
                                .addClass('ti-angle-down');
                        }
                        else
                        {
                            $(this).find('.chevron').toggleClass('ti-angle-right')
                                .removeClass('ti-angle-down');
                        }*/
                        b(this, h);
                        var parLi = jQuery(this).parent();
                        if(!parLi.hasClass("submenu")){
                            //parLi.siblings("li.selected").removeClass("selected");
                            parLi.addClass("selected");
                        }
                    });
                    jQuery(this).bind("activate-node", function () {
                        a.find(f).not(jQuery(this).parents()).not(jQuery(this).siblings()).slideUp(g);
                        b(this, "slideDown");
                        //$(this).find('.ti-angle-down').removeClass('ti-angle-down').addClass('ti-angle-right');

                    })
                });
                var i = location.hash ? a.find("a[href=" + location.hash + "]")[0] : a.find("li.selected a")[0];
                i && jQuery(i).parents(".normal-bar").length && b(i, !1)
            })
        }
    })
})(jQuery);