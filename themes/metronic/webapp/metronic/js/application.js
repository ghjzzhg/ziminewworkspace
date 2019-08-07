/***********************************************
APACHE OFBiz
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
***********************************************/

/*********************
JQuery Columns
*********************/
var j = 1;
 
(function(jQuery) {
	jQuery.fn.columns = function(options) {
	
	var defaults = {			
		colNumber: 2,
		direction: 'vertical'
	};
			
	this.each(function() {
		
		var obj = jQuery(this);
		var settings = jQuery.extend(defaults, options);
		var totalListElements = jQuery(this).children('li').size();
		var baseColItems = Math.ceil(totalListElements / settings.colNumber);
		var listClass = jQuery(this).attr('class');
		
		for (i=1;i<=settings.colNumber;i++) {	
			if(i==1){
				jQuery(this).addClass('listCol1').wrap('<div class="listContainer'+j+'"></div>');
			} 
			else if(jQuery(this).is('ul')) {
				jQuery(this).parents('.listContainer'+j).append('<ul class="listCol'+i+'"></ul>');
			} 
			else {
				jQuery(this).parents('.listContainer'+j).append('<ol class="listCol'+i+'"></ol>');
			}
				jQuery('.listContainer'+j+' > ul,.listContainer'+j+' > ol').addClass(listClass);
		}
		
		var listItem = 0;
		var k = 1;
		var l = 0;	
		
		if(settings.direction == 'vertical') {			
			jQuery(this).children('li').each(function() {
				listItem = listItem+1;
				if (listItem > baseColItems*(settings.colNumber-1) ) {
					jQuery(this).parents('.listContainer'+j).find('.listCol'+settings.colNumber).append(this);
				} 
				else {
					if(listItem<=(baseColItems*k)) {
						jQuery(this).parents('.listContainer'+j).find('.listCol'+k).append(this);
					} 
					else {
						jQuery(this).parents('.listContainer'+j).find('.listCol'+(k+1)).append(this);
						k = k+1;
					}
				}
			});
			
			jQuery('.listContainer'+j).find('ol,ul').each(function(){
				if(jQuery(this).children().size() == 0) {
				jQuery(this).remove();
				}
			});	
			
		}
		
		else {
			jQuery(this).children('li').each(function(){
				l = l+1;
				if(l <= settings.colNumber) {
					jQuery(this).parents('.listContainer'+j).find('.listCol'+l).append(this);
				} 
				else {
					l = 1;
					jQuery(this).parents('.listContainer'+j).find('.listCol'+l).append(this);
				}				
			});
		}
		
		jQuery('.listContainer'+j).find('ol:last,ul:last').addClass('last');
		j = j+1;
		
	});
    };
})(jQuery);

/*********************
JQuery Formalize
*********************/
var FORMALIZE = (function($, window, document, undefined) {
	var PLACEHOLDER_SUPPORTED = 'placeholder' in document.createElement('input');
	var AUTOFOCUS_SUPPORTED = 'autofocus' in document.createElement('input');
	var WEBKIT = 'webkitAppearance' in document.createElement('select').style;
	var IE6 = !!($.browser.msie && parseInt($.browser.version, 10) === 6);
	var IE7 = !!($.browser.msie && parseInt($.browser.version, 10) === 7);
	return {
		go: function() {
			for (var i in FORMALIZE.init) {
				FORMALIZE.init[i]();
			}
		},
		init: {
			detect_webkit: function() {			
				if (!WEBKIT) {
					return;
				}
				jQuery('html').addClass('is_webkit');
			},
			full_input_size: function() {
				if (!IE7 || !jQuery('textarea, input.input_full').length) {
					return;
				}
				jQuery('textarea, input.input_full').wrap('<span class="input_full_wrap"></span>');
			},
			ie6_skin_inputs: function() {
				if (!IE6 || !jQuery('input, select, textarea').length) {
					return;
				}
				var button_regex = /button|submit|reset/;
				var type_regex = /date|datetime|datetime-local|email|month|number|password|range|search|tel|text|time|url|week/;
				jQuery('input').each(function() {
					var el = jQuery(this);
					if (this.getAttribute('type').match(button_regex)) {
						el.addClass('ie6_button');
						if (this.disabled) {
							el.addClass('ie6_button_disabled');
						}
					}
					else if (this.getAttribute('type').match(type_regex)) {
						el.addClass('ie6_input');
						if (this.disabled) {
							el.addClass('ie6_input_disabled');
						}
					}
				});
				jQuery('textarea, select').each(function() {
					if (this.disabled) {
						jQuery(this).addClass('ie6_input_disabled');
					}
				});
			},
			placeholder: function() {
				if (PLACEHOLDER_SUPPORTED || !jQuery(':input[placeholder]').length) {
					return;
				}
				jQuery(':input[placeholder]').each(function() {
					var el = jQuery(this);
					var text = el.attr('placeholder');
					function add_placeholder() {
						if (!el.val() || el.val() === text) {
							el.val(text).addClass('placeholder_text');
						}
					}
					add_placeholder();
					el.focus(function() {
						if (el.val() === text) {
							el.val('').removeClass('placeholder_text');;
						}
					}).blur(function() {
						add_placeholder();
					});
					el.closest('form').submit(function() {
						if (el.val() === text) {
							el.val('');
						}
					}).bind('reset', function() {
						setTimeout(add_placeholder, 50);
					});
				});
			},
			autofocus: function() {
				if (AUTOFOCUS_SUPPORTED || !jQuery(':input[autofocus]').length) {
					return;
				}
				jQuery(':input[autofocus]:visible:first').select();
			}
		}
	};
})(jQuery, this, this.document);

jQuery(document).ready(function() {
	FORMALIZE.go();
	/*jQuery(".left-bar .submenu > a").click(function(){
		jQuery(this).parent().toggleClass('active');
	});*/

	jQuery(".left-bar").prepend(
		'<div class="admin-logo">' +
		'<div class="logo-holder pull-left">' +
		jQuery("#app-navigation h2").text() +
		'</div>' +
		'<a href="#" class="menu-bar" style="position:absolute;left:0;"><i class="glyphicon glyphicon-align-justify"></i></a>' +
		'</div>'
	);

	jQuery('ul.menu-parent').accordion();

/*
	setTimeout(function(){
		jQuery(".normal-bar .admin-logo div, .admin-logo a").show();
	}, 1000)*/

	var pageContainer = jQuery(".page-container");

	jQuery('.menu-bar').click(function(){
        if(pageContainer.hasClass("normal-bar") || jQuery("body").width() >= 1024){//窄屏时不放大
			pageContainer.toggleClass('mini-bar');
			pageContainer.toggleClass('normal-bar');
			jQuery(".left-bar .submenu").removeClass("active");
			jQuery(".left-bar .submenu ul").hide();
			jQuery.cookie('mini-bar', pageContainer.hasClass("mini-bar") ? 'mini-bar' : '', { expires: 365, path: '/' });
			jQuery(".normal-bar .admin-logo div").show();
		}
/*
		jQuery(".left-bar").getNiceScroll().remove();
		setTimeout(function() {
			jQuery(".left-bar").niceScroll();
		}, 200);*/
	});
	jQuery('.menu-switch').click(function(){
		jQuery(this).toggleClass("active");
		jQuery(".left-bar").toggleClass("mini-show");
	});
	//jQuery("html").niceScroll();
	/*jQuery("html").click(function(){
		if(jQuery(".main-navigation").is(":visible")){
			jQuery(".main-navigation").hide('slide');
		}
	});*/
	jQuery('.header-menu-switch').mouseenter(function(event){
		mainMenuTimeout = setTimeout(function(){
			jQuery(".main-navigation").show('fade', 500);
		}, 500);
	}).mouseleave(function(){
		if(mainMenuTimeout){
			clearTimeout(mainMenuTimeout);
		}
		mainMenuTimeout = setTimeout(function(){
			jQuery(".main-navigation").hide();
		}, 500);
	});

	/*jQuery(".main-navigation").mouseenter(function(){
		if(mainMenuTimeout){
			clearTimeout(mainMenuTimeout);
		}
	}).mouseleave(function(){
		jQuery(this).hide('fade', 500);
	})*/

	jQuery(".submenu ul li").click(function(){
		var $this = jQuery(this);
        if($this.width() > $this.parents(".submenu").width()){
			$this.parent().hide();
		}
	});
	jQuery(".left-bar li.submenu, .left-bar .menu-parent > li").mouseenter(function(){
		if(jQuery(".menu-parent").width() < 100) {//mini-bar或者窄屏幕时收回
			jQuery(this).siblings().find("ul").hide();
			jQuery(this).children("ul").show();
		}
	})
	jQuery(".left-bar li.submenu > ul").mouseleave(function(){
		if(jQuery(".menu-parent").width() < 100){//mini-bar或者窄屏幕时收回
			jQuery(this).hide();
		}
	});
	jQuery(".left-bar").mouseleave(function(){
		if(jQuery(".menu-switch").is(":visible")){//窄屏幕
			jQuery(".left-bar").removeClass("mini-show");
			jQuery(".menu-switch").addClass("active");
		}
		setTimeout(hideTempClickedMenu, 500);
	});

	function hideTempClickedMenu(){
		jQuery(".left-bar li.submenu.active:not(.selected) > ul").slideUp('fast');
		jQuery(".left-bar li.submenu.active").removeClass("active");
	}

	jQuery(".left-bar").on("click", "a:not([href^=#])", function(event){
		var $this = jQuery(this), href = $this.attr("href");
		if(href.indexOf('showSubMenuAjax') < 0){
			event.stopPropagation();
			$this.attr("href", "javascript:showSubMenuAjax('" + href + "')");
			showSubMenuAjax(href);
		}
		jQuery(".left-bar .selected").removeClass("selected")
		$this.parent().addClass("selected");
		$this.parents("li.submenu").removeClass("active").addClass("selected");
	})

	/*jQuery(".contentarea").mouseover(function(){
		var $activeSubMenu = jQuery(".submenu.active > ul");
        if($activeSubMenu.width() > $activeSubMenu.parent().width()){
			$activeSubMenu.hide();
		}
	});*/

	showSystemMsg();
});

function showSystemMsg(){
	var currentMsg = jQuery(".last-system-msg > div > div:visible");
	if(!currentMsg.length){
		jQuery(".last-system-msg > div > div:eq(0)").show();
	}else{
		currentMsg.hide('show');
		var nextMsg = currentMsg.next();
		if(nextMsg.length){
			nextMsg.show();
		}else{
			jQuery(".last-system-msg > div > div:eq(0)").show();
		}
	}
	setTimeout(showSystemMsg, 2000);
}

function initSlimScroll(el, options) {
	$(el).each(function() {
		if ($(this).attr("data-initialized")) {
			return; // exit
		}

		var height;

		if ($(this).attr("data-height")) {
			height = $(this).attr("data-height");
		} else {
			height = $(this).css('height');
		}

		$(this).slimScroll($.extend({
			allowPageScroll: true, // allow page scroll when the element scroll is ended
			size: '7px',
			color: ($(this).attr("data-handle-color") ? $(this).attr("data-handle-color") : '#bbb'),
			wrapperClass: ($(this).attr("data-wrapper-class") ? $(this).attr("data-wrapper-class") : 'slimScrollDiv'),
			railColor: ($(this).attr("data-rail-color") ? $(this).attr("data-rail-color") : '#eaeaea'),
			position: 'right',
			height: height,
			alwaysVisible: ($(this).attr("data-always-visible") == "1" ? true : false),
			railVisible: ($(this).attr("data-rail-visible") == "1" ? true : false),
			disableFadeOut: true
		}, options));

		$(this).attr("data-initialized", "1");
	});
}