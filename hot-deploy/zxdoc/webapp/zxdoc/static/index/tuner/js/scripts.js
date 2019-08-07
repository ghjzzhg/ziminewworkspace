/**/
/* on load event */
/**/
"use strict";
jQuery(document).ready( function (){
	if ( !is_mobile_device () ) {
		$('body').append('<div id="tuner" class="tuner"><div class="layout-style"><p>布局样式</p><div class="page-style wide active">全屏</div><div class="page-style boxed">居中</div></div><i id="tuner-switcher" class="flaticon-gear"></i> </div> <div id="tuner-style-1" class="tuner-style" style="display: none;"> /* Colors for: main color */   p a, ins , ins.alt-3, ul.icon-style li .list-icon, h2 span, h3 span, .menu-contacts .menu-contacts-item .cws-icon, .main-nav .search li .close-button:hover, .main-nav.transparent .search li .close-button:hover, .mn-sub li:not(.mn-sub-multi):hover > .button_open, .mn-sub li.active > .button_open, .mobile_nav ul li a:not(.cws-button):hover, .mobile_nav ul.mn-sub li a:not(.cws-button):hover, .site-top-panel .top-left-wrap .lang-wrap ul li a i, .site-top-panel .top-left-wrap .social-wrap a.cws_social_link, .site-top-panel .top-left-wrap .social-wrap .cws_social_links > *, .site-top-panel .top-left-wrap .social-wrap .share-toggle-button, .site-top-panel .top-right-wrap i, .breadcrumbs .breadcrumbs-item a:hover, .breadcrumbs .breadcrumbs-item a:last-child , .tp-caption .sl-title span, .cws-button, .cws-button.alt:hover, .cws-button.white:hover, .cws-button.with-icon, .cws-button.with-icon.alt:hover, .cws-icon.main-color, .cws-icon.border-icon.alt:hover, .service-item a:hover, .service-item a:hover:after, .cws-icon.type-3:hover:before, .cws-icon.type-3.alt:before, .hover-effect-2:hover .opacity:hover h3, .cws-social:hover, .service-item.color-icon .cws-icon, .service-item.on-dark:hover .cws-icon, .service-center-icon .cws-icon, .accordion .content-title.active, .toggle.style-2 .content-title.active, .counter-block .counter-icon, .counter-block .counter, .counter-block .counter-name, .pricing-tables .pricing-list li .list-icon.flaticon-mark, .pricing-tables.active .header-pt h3, .pricing-tables:hover .header-pt h3, .pic .links .link-icon.alt, nav .showing span, .carousel-container .carousel-nav .prev:hover, .carousel-container .carousel-nav .next:hover, .comments .comment-body .comment_info_section .button-content.reply, .testimonial .quote:after, .testimonial .author span, footer.footer .copyright p span, footer.footer .copyright a.footer-nav:hover, .widget-footer address strong, .widget-footer li a:hover, .blog-item .blog-item-data .blog-title a, .nav-blog .prev:hover, .nav-blog .next:hover, .blog-nav-tags a:hover, .quote.alt-3 h4 span, .widget-image .img-title p, .widget-search form .search-submit, .widget-subscribe form .submit, .widget-categories ul li:hover, .widget-archive ul li:hover, .widget-items ul li:hover, .widget-custom-filter ul li:hover, .widget-categories ul li:hover:before, .widget-categories ul li:hover a, .widget-archive ul li:hover:before, .widget-archive ul li:hover a, .widget-items ul li:hover:before, .widget-items ul li:hover a, .widget-custom-filter ul li:hover:before, .widget-custom-filter ul li:hover a, .widget-items ul li:hover a span, .widget-categories.alt .accordion.style-2 .content-title.active, .widget-categories.alt .accordion.style-2 .content ul li:hover a, .widget-categories.alt .accordion.style-2 .content ul li.active a, .widget-post .item-recent .title a:hover, .widget-post .item-top-sellers .title a:hover, .widget-top-sellers .item-recent .title a:hover, .widget-top-sellers .item-top-sellers .title a:hover, .widget-post .item-recent .date-recent a:hover, .widget-post .item-top-sellers .date-recent a:hover, .widget-top-sellers .item-recent .date-recent a:hover, .widget-top-sellers .item-top-sellers .date-recent a:hover, .widget-post .item-recent .price, .widget-post .item-top-sellers .price, .widget-top-sellers .item-recent .price, .widget-top-sellers .item-top-sellers .price, .top-shop .widget-top-sellers p span, .top-shop .widget-top-sellers .item-top-sellers .title .shop-close i:hover, .top-shop .widget-top-sellers .item-top-sellers .price > span:first-child, .top-shop .widget-top-sellers .total .sub-total span, .widget-testimonials .carousel-nav .prev:hover, .widget-testimonials .carousel-nav .next:hover, .widget-tes-item .author-info .name, .widget-about h3.title, .ui-datepicker-calendar thead th, .selection-box.color-3:before, .admin-about .admin-name a, .portfolio-item .pic .item-content.alt .categories a, .work-filter a.active, .project-details .description .tag:hover, .project-details .description .link, .project-details .description .social:hover, #list-or-grid .switch-button.active, .shop-data .result-count span, .product.hot .price, .product.sale .price, .product .price, .product .price-review .button-groups, .color-filter ul .cat-item.main-color.active, .color-filter ul .cat-item.main-color:hover, .color-filter ul .cat-item.main-color.active a, .color-filter ul .cat-item.main-color:hover a, .brand-filter ul .cat-item.main-color.active, .brand-filter ul .cat-item.main-color:hover, .brand-filter ul .cat-item.main-color.active a, .brand-filter ul .cat-item.main-color:hover a, .size-filter .size:hover, .size-filter .size.active, .single-product .price, .single-product .post-number a:hover, .single-product .category-line a:hover, .single-product .tags-line a:hover, .review-status .status-product span, input[type="checkbox"]:before, .shipping .amount, .inner-nav li.menu-shop-card .mn-sub li .shop-cart-menu a.cws-button, .inner-nav.mobile_nav .shop-cart-menu a.cws-button, .woocommerce-shipping-fields #ship-to-different-address label, abbr, .woocommerce table tbody .cart_item:hover .product-name, .contact-address p span, #scroll-top:hover i, .login-popup .login-popup-wrap .login-content .remember a, .dropcap, .back-home, .tuner .page-style.active, .tuner .page-style.active.boxed:before, .service-item .cws-icon, .service-item h3, .blog-item .blog-item-data .post-info span, .service-item.on-dark .cws-icon, .service-item.on-dark h3, .profile-cat, .twitter-1 .twitt-icon, .project-details .description .tag { color: #<span>cws_theme_main#</span>; }    .inner-nav ul li > a:hover, .inner-nav ul li a.active, .mn-sub li:not(.mn-sub-multi):hover > a, .mn-sub li.active > a, .mobile_nav li:hover a:not(.cws-button), .mn-sub li:not(.mn-sub-multi):hover > a, .mobile_nav li.active > a, .mn-sub li:not(.mn-sub-multi).active > a   { color: #<span>cws_theme_main#</span> !important; }    input[type="number"]:focus, ins.alt-3, ul li:before, ul.style-3 li:before, .checkbox input[type=checkbox]:checked, .radio label:hover, .radio.radio1 label, .radio input[type=radio]:checked + label, .cws-button, .cws-button.with-icon, .cws-button.with-icon:hover, .cws-button.with-icon.alt, .cws-button.with-icon.alt:hover, .cws-icon.only-border, .cws-icon.border-icon:hover, .cws-icon.border-icon.alt,  .cws-icon.type-3, .cws-icon.type-3:after, .cws-icon.type-3.alt:hover, .cws-icon.type-3.alt:hover:after, .service-item.border:hover, .service-item.icon-right.alt:hover .cws-icon, .service-item.icon-left.alt:hover .cws-icon, .service-item.icon-right.alt:hover .cws-icon:after, .service-item.icon-left.alt:hover .cws-icon:after, .accordion img, .toggle .content-title.active span, .toggle .content, .alert.alert-notice.alt, .counter-block, .pricing-tables.active, .pricing-tables:hover, .pricing-tables.active a.cws-button, .tabs .tabs-btn.active, .pagination li a:hover, .pagination li a.active, .pagination li a:focus, .carousel-container .carousel-nav .prev:hover, .carousel-container .carousel-nav .next:hover, .blog-item a.cws-button.alt.gray:hover, .widget-tags .tag:hover, .widget-testimonials .carousel-nav .prev:hover, .widget-testimonials .carousel-nav .next:hover, .carousel-pag .owl-pagination .owl-page.active, .pagiation-carousel .owl-pagination .owl-page.active, .carousel-pag.main-color .owl-page.active, .pagiation-carousel.main-color .owl-page.active, .contact-form input:not(.cws-button):focus, .contact-form textarea:focus, .contact-form .cws-button, .contact-form .cws-button:hover, .selection-box select:focus, .selection-box.color-2 select:focus, .selection-box.color-3 select,  .message-form-subject input:focus, .message-form-author input:focus, .message-form-website input:focus, .message-form-email input:focus, .message-form-message textarea:focus, .border-t, .border-b, .work-filter a:before, .filter-button.active, .filter-button:hover, #list-or-grid .switch-button.active, .shop-post img, .shop-data #list-grid > div.active, .price_slider .ui-slider-handle:before, .color-filter ul .cat-item.main-color.active:before, .color-filter ul .cat-item.main-color:hover:before, .brand-filter ul .cat-item.main-color.active:before, .brand-filter ul .cat-item.main-color:hover:before, .size-filter .size:hover, .size-filter .size.active, .inner-nav li.menu-shop-card .mn-sub li .shop-cart-menu a.cws-button, .inner-nav.mobile_nav .shop-cart-menu a.cws-button, .map-full-width.border-t, .login-popup .login-popup-wrap .login-content input.form-row:focus, .dropcap, .tuner, .tuner .page-style.active, .tuner .page-style.active.boxed:before, .service-item.icon-left.alt .cws-icon, .service-item.icon-left.alt .cws-icon:after, .service-item.icon-right.alt .cws-icon, .service-item.icon-right.alt .cws-icon:after, .twitter-1 .twitt-icon   { border-color: #<span>cws_theme_main#</span>; }  .pricing-tables a.cws-button:hover  { border-color: #<span>cws_theme_main#</span> !important; }    ins.alt-2, ul.style-3 li:before, .bg-main:before, .radio label:before, .cws_divider.with-plus:before, .cws_divider.with-plus:after, hr:before, hr:after, .inner-nav .mobile_menu_switcher, .main-nav:not(.transparent) .inner-nav.desktop-nav.switch-menu .menu-bar .ham, .main-nav:not(.transparent) .inner-nav.desktop-nav.switch-menu .menu-bar .ham:before, .main-nav:not(.transparent) .inner-nav.desktop-nav.switch-menu .menu-bar .ham:after, .cws-button:hover, .cws-button.alt, .cws-button.with-icon:hover, .cws-button.with-icon.alt, .cws-icon.border-icon:hover, .cws-icon.border-icon.alt, .cws-icon.type-3:after, .cws-icon.type-3.alt:hover:after, .service-bg-icon:before, .service-center-icon:before, .accordion.style-2 .content-title.active span, .toggle .content-title i.toggle-icon:before, .toggle .content-title i.toggle-icon:after, .toggle .content-title.active span, .toggle.style-2 .content-title.active i.toggle-icon:before, .alert.alert-notice.alt, .counter-block.alt, .pricing-tables .price-pt:before, .pricing-tables.active a.cws-button, .skill-bar .bar span, .skill-bar.st-color-3 .bar span, .tabs .tabs-btn.active, .profile-item .pic .hover-effect, .divider, .pic .hover-effect, .pic .links .link-icon.alt:hover, .pagination li a:hover, .pagination li a.active, .pagination li a:focus, .blog-item a.cws-button.alt.gray:hover, .blog-date .date:before, .quote.alt-2, aside.sb-right .cws-widget + .cws-widget:before, aside.sb-right .cws-widget + .cws-widget:after, aside.sb-left .cws-widget + .cws-widget:before, aside.sb-left .cws-widget + .cws-widget:after, .widget-tags .tag:hover, .widget-social .social-icon-wrap .social-icon:before, .carousel-pag .owl-pagination .owl-page.active, .pagiation-carousel .owl-pagination .owl-page.active, .carousel-pag.main-color .owl-page.active, .pagiation-carousel.main-color .owl-page.active, .ui-datepicker-header, td.ui-datepicker-today:before, .contact-form .cws-button:not(.alt):hover, .filter-button.active, .filter-button:hover, .shop-data #list-grid > div.active, .product .action, .price_slider .ui-slider-range, .price_slider .ui-slider-handle:before, .color-filter ul .cat-item.main-color.active:before, .color-filter ul .cat-item.main-color:hover:before, .brand-filter ul .cat-item.main-color.active:before, .brand-filter ul .cat-item.main-color:hover:before, table.table thead tr, form input[type*="radio"]:checked:before, .contact-address .contact-icon-wrap .contact-icon:before, .login-popup .login-popup-wrap .title-wrap, .login-popup .login-popup-wrap .login-bot, .dropcap.alt-2, .dropcap.alt-4, .portfolio-item .pic .item-content, .blog-item .blog-media .pic .item-content, .blog-masonry .blog-item .blog-media .pic .item-content, .tuner i  { background: #<span>cws_theme_main#</span>; }   .pricing-tables a.cws-button:hover   { background: #<span>cws_theme_main#</span> !important; }   .blog-date .date .date-cont > span.day:before, .tabs .tabs-btn:before  { border-color: #<span>cws_theme_main#</span> transparent transparent transparent; }   .pic .links .link-icon.alt, .cws-social:hover   { box-shadow: 0 0 0 1px #<span>cws_theme_main#</span>; }   </div> <div id="tuner-style-2" class="tuner-style" style="display: none;">   <style id="cws-cp-1"></style>')

		jQuery('#tuner-switcher').on('click', function()
		{
			jQuery('#tuner').toggleClass('tuner-visible');
		});

		/*jQuery('.color-picker').each( function(){
			var el = jQuery(this);
			var def_color = el.data( 'color' );
			var id = el.attr('id');
			var matches = /color-(\d+)/.exec( id );
			if ( matches != null ){
				var index = matches[1];
				var tuner_id = 'tuner-style-' + index;
				var style_id = 'cws-cp-' + index;
				var tuner_el = jQuery( '#' + tuner_id );
				var style_el = jQuery( '#' + style_id );
				if ( tuner_el.length && style_el.length ){
					
					el.ColorPicker({
						color: def_color,
						onShow: function(colpkr)
						{
							jQuery(colpkr).fadeIn(300);
							return false;
						},
						onHide: function(colpkr)
						{
							jQuery(colpkr).fadeOut(300);
							return false;
						},
						onChange: function (hsb, hex, rgb) {
							el.css('background-color', '#' + hex);
							tuner_el.find('span').text(hex);
							tuner_el.find('span.darknest').text(cws_Hex2RGBwithdark(hex,1.14));
							style_el.text(tuner_el.text());
						}
					});
				}

			}
		});*/
		$("html").addClass("t-pattern-1");
		jQuery('#tuner').on('click', '.patterns li', function()
		{
			jQuery(".tuner .patterns li").removeClass('active');
			jQuery(this).addClass("active");
			var body_el, body_cls, matches, old_pattern, new_pattern_index, new_pattern;
			body_el = jQuery('html');
			body_cls = body_el.attr('class');
			matches = /t-pattern-(\d+)/.exec( body_cls );
			if ( matches != null ){
				old_pattern = matches[0];
				body_el.removeClass(old_pattern);
			}
			new_pattern_index = jQuery(this).data('pattern');
			new_pattern = "t-pattern-" + new_pattern_index;
			body_el.addClass(new_pattern);
		});
		jQuery('#tuner').on('click', '.page-style', function() {
			$('.tuner .page-style').removeClass('active');
			$(this).addClass("active");
			if ( $(this).hasClass("boxed") ) {
				$("body").addClass("boxed");
			} else {
				$("body").removeClass("boxed");
			}
			width_sticky ();
			$(window).resize(function(){
				width_sticky();
			})
		})
	}
});
function width_sticky () {
	if ( $("body").hasClass("boxed") ) {
		var width_body = $("body").innerWidth();
		$("body.boxed .main-nav").css({"width":width_body+"px"});
	} else {
		$("body .main-nav").css({"width":"100%","left":"0"});
	}
}
function cws_Hex2RGBwithdark(hex,coef_color) {

  var coef_color = coef_color == undefined ? 1 : coef_color;
  var hex = hex.replace("#", "");

  var color = '';

  if (hex.length == 3) {
   color = Math.round(hexdec(hex.substr(0,1))/coef_color)+',';
   color = color + Math.round(hexdec(hex.substr(1,1))/coef_color)+',';
   color = color + Math.round(hexdec(hex.substr(2,1))/coef_color);
  }else if(hex.length == 6){
   color = Math.round(hexdec(hex.substr(0,2))/coef_color)+',';
   color = color + Math.round(hexdec(hex.substr(2,2))/coef_color)+',';
   color = color + Math.round(hexdec(hex.substr(4,2))/coef_color);
  }
  return color;
 }
 function hexdec(hex_string) {
  hex_string = (hex_string + '')
  .replace(/[^a-f0-9]/gi, '');
  return parseInt(hex_string, 16);
 }