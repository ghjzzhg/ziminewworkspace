<!DOCTYPE html>
<#include "component://common/webcommon/render360.ftl"/>
<html class="<#if isIE?has_content && isIE>ie ie${ieVersion?c}</#if>">
<head>
    <title>资协网</title>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=EDGE">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0">
    <link rel="stylesheet" href="/zxdoc/static/index/css/index.min.css">
    <link rel="stylesheet" href="/zxdoc/static/index/css/index2.min.css?t=20170123">
    <#--<link rel="stylesheet" href="/zxdoc/static/index/css/reset.css">-->
    <#--<link rel="stylesheet" href="/zxdoc/static/index/css/bootstrap.css">-->
    <#--<link rel="stylesheet" href="/zxdoc/static/index/css/font-awesome.css">-->
    <#--<link rel="stylesheet" href="/zxdoc/static/index/css/owl.carousel.css">-->
    <#--<link rel="stylesheet" href="/zxdoc/static/index/css/jquery.fancybox.css">-->
    <link rel="stylesheet" href="/zxdoc/static/index/fonts/fi/flaticon.css">
    <#--<link rel="stylesheet" href="/zxdoc/static/index/css/main.css">-->
    <#--<link rel="stylesheet" href="/zxdoc/static/index/css/indent.css">-->
    <link rel="stylesheet" href="/zxdoc/static/index/rs-plugin/css/settings.css">
    <link rel="stylesheet" href="/zxdoc/static/index/rs-plugin/css/layers.css">
    <link rel="stylesheet" href="/zxdoc/static/index/rs-plugin/css/navigation.css">
    <#--<link rel="stylesheet" href="/zxdoc/static/index/tuner/css/colorpicker.css">-->
    <#--<link rel="stylesheet" href="/zxdoc/static/index/tuner/css/styles.css">-->
    <link rel="stylesheet" href="/images/lib/qtip/jquery.qtip.min.css">
    <link rel="stylesheet" href="/images/lib/validationEngine/validationEngine.jquery.css">
    <#--<script language="javascript" src="/zxdoc/static/jsjaccore/jquery-1.6.1.min.js" type="text/javascript"></script>-->
    <#--<![endif]&ndash;&gt;-->
    <#--<script language="javascript" src="/images/jquery/ui/js/jquery.cookie-1.4.0.js" type="text/javascript"></script>-->
<#--<link rel="stylesheet" href="/metronic-web/component/components.min.css">-->
    <style type="text/css">
        #filter-grid table td{
            padding: 4px;
        }
        .form-group.form-md-line-input.form-md-floating-label .input-icon > label {
            padding-left: 34px; }
        .form-group.form-md-line-input.form-md-floating-label .input-icon.right > label {
            padding-left: 0;
            padding-right: 34px; }
        .form-group.form-md-line-input.form-md-floating-label .input-group.left-addon label {
            padding-left: 34px; }
        .form-group.form-md-line-input.form-md-floating-label .input-group.right-addon label {
            padding-right: 34px; }
        .form-group.form-md-line-input + .input-icon {
            padding-top: 0; }
        .form-group.form-md-line-input .help-block {
            position: absolute;
            margin: 2px 0 0 0;
            opacity: 0 ;
            filter: alpha(opacity=0) ;
            font-size: 13px; }
        .form-group.form-md-line-input > .input-icon > i {
            left: 0;
            bottom: 0;
            margin: 9px 2px 10px 10px;
            color: #888888; }
        .form-group.form-md-line-input > .input-icon.input-icon-lg > i {
            top: 6px; }
        .form-group.form-md-line-input > .input-icon.input-icon-sm > i {
            top: -1px; }
        .form-group.form-md-line-input > .input-icon .form-control {
            padding-left: 34px; }
        .form-group.form-md-line-input > .input-icon > label {
            margin-top: -20px; }
        .form-group.form-md-line-input > .input-icon.right .form-control {
            padding-left: 0;
            padding-right: 34px; }
        .form-group.form-md-line-input > .input-icon.right > i {
            left: auto;
            right: 8px;
            margin: 11px 2px 10px 10px; }
        .input-icon {
            position: relative;
            left: 0; }
        .input-icon > .form-control {
            padding-left: 33px; }
        .input-group .input-icon > .form-control {
            -webkit-border-radius: 4px 0 0 4px;
            -moz-border-radius: 4px 0 0 4px;
            -ms-border-radius: 4px 0 0 4px;
            -o-border-radius: 4px 0 0 4px;
            border-radius: 4px 0 0 4px; }
        .input-icon > i {
            color: #ccc;
            display: block;
            position: absolute;
            margin: 11px 2px 4px 10px;
            z-index: 3;
            width: 16px;
            font-size: 16px;
            text-align: center;
            left: 0; }
        .modal .input-icon > i {
            z-index: 10055; }
        .has-success .input-icon > i {
            color: #36c6d3; }
        .has-warning .input-icon > i {
            color: #F1C40F; }
        .has-info .input-icon > i {
            color: #659be0; }
        .has-error .input-icon > i {
            color: #ed6b75; }
        .input-icon.right {
            left: auto;
            right: 0; }
        .input-icon.right > .form-control {
            padding-right: 33px;
            padding-left: 12px; }
        .input-group .input-icon.right > .form-control {
            -webkit-border-radius: 0 4px 4px 0;
            -moz-border-radius: 0 4px 4px 0;
            -ms-border-radius: 0 4px 4px 0;
            -o-border-radius: 0 4px 4px 0;
            border-radius: 0 4px 4px 0; }
        .input-icon.right > i {
            right: 8px;
            float: right; }
        .input-icon.input-icon-lg > i {
            margin-top: 16px; }
        .input-icon.input-icon-sm > i {
            margin-top: 8px;
            font-size: 13px; }
        .visible-ie8 {
            display: none; }

        .ie8 .visible-ie8 {
            display: inherit !important; }

        .visible-ie9 {
            display: none; }

        .ie9 .visible-ie9 {
            display: inherit !important; }

        .hidden-ie8 {
            display: inherit; }

        .ie8 .hidden-ie8 {
            display: none !important; }

        .hidden-ie9 {
            display: inherit; }

        .ie9 .hidden-ie9 {
            display: none !important; }

        footer.footer{
            position:fixed;
            z-index: -1;
        }

        .ie footer.footer{
            position: relative;
            z-index: 0;
        }

        .btn.green:not(.btn-outline) {
            color: #FFFFFF;
            background-color: #32c5d2;
            border-color: #32c5d2; }
        .btn.green:not(.btn-outline):focus,
        .btn.green:not(.btn-outline).focus {
            color: #FFFFFF;
            background-color: #26a1ab;
            border-color: #18666d; }
        .btn.green:not(.btn-outline):hover {
            color: #FFFFFF;
            background-color: #26a1ab;
            border-color: #2499a3; }

        .btn.red:not(.btn-outline) {
            color: #ffffff;
            background-color: #e7505a;
            border-color: #e7505a; }
        .btn.red:not(.btn-outline):focus,
        .btn.red:not(.btn-outline).focus {
            color: #ffffff;
            background-color: #e12330;
            border-color: #a1161f; }
        .btn.red:not(.btn-outline):hover {
            color: #ffffff;
            background-color: #e12330;
            border-color: #dc1e2b; }
        .btn.red:not(.btn-outline):active,
        .btn.red:not(.btn-outline).active,
        .open > .btn.red:not(.btn-outline).dropdown-toggle {
            color: #ffffff;
            background-color: #e12330;
            border-color: #dc1e2b; }
        .btn.red:not(.btn-outline):active:hover,
        .btn.red:not(.btn-outline):active:focus,
        .btn.red:not(.btn-outline):active.focus,
        .btn.red:not(.btn-outline).active:hover,
        .btn.red:not(.btn-outline).active:focus,
        .btn.red:not(.btn-outline).active.focus,
        .open > .btn.red:not(.btn-outline).dropdown-toggle:hover,
        .open > .btn.red:not(.btn-outline).dropdown-toggle:focus,
        .open > .btn.red:not(.btn-outline).dropdown-toggle.focus {
            color: #ffffff;
            background-color: #c51b26;
            border-color: #a1161f; }
        .btn.red:not(.btn-outline):active,
        .btn.red:not(.btn-outline).active,
        .open > .btn.red:not(.btn-outline).dropdown-toggle {
            background-image: none; }
        .btn.red:not(.btn-outline).disabled:hover,
        .btn.red:not(.btn-outline).disabled:focus,
        .btn.red:not(.btn-outline).disabled.focus,
        .btn.red:not(.btn-outline)[disabled]:hover,
        .btn.red:not(.btn-outline)[disabled]:focus,
        .btn.red:not(.btn-outline)[disabled].focus,
        fieldset[disabled] .btn.red:not(.btn-outline):hover,
        fieldset[disabled] .btn.red:not(.btn-outline):focus,
        fieldset[disabled] .btn.red:not(.btn-outline).focus {
            background-color: #e7505a;
            border-color: #e7505a; }
        .btn.red:not(.btn-outline) .badge {
            color: #e7505a;
            background-color: #ffffff; }

        .btn.btn-outline.red {
            border-color: #e7505a;
            color: #e7505a;
            background: none; }
        .btn.btn-outline.red:hover,
        .btn.btn-outline.red:active,
        .btn.btn-outline.red:active:hover,
        .btn.btn-outline.red:active:focus,
        .btn.btn-outline.red:focus,
        .btn.btn-outline.red.active {
            border-color: #e7505a;
            color: #ffffff;
            background-color: #e7505a; }


        .widget-footer li a:last-child{
            margin-left:0;
        }
        .page-section{
            margin:5px 0;
        }
        img.sticky-logo{
            height:80px;
        }
        .small-height img.sticky-logo{
            height:50px;
        }
        .menu-contacts{
            top:<#if isIE && ieVersion < 10>30<#else>45</#if>px;
        }
        .small-height .menu-contacts{
            top:40px;
        }
        .full-wrapper{
            /*background: url("/metronic-web/images/logo-bg-big-www.png") no-repeat;*/
            margin: 0 auto;
            max-width:1250px;
        }
        .small-height .full-wrapper{
            background: none;
        }
        /*.small-height .full-wrapper{
            background: url("/metronic-web/images/logo-bg-small.png") no-repeat;
        }*/

        #filter-grid table tr:hover td{
            background-color: whitesmoke;
            color:black;
        }
        .content-body.boxed, .boxed footer{
            margin: 0 auto;
            max-width: 1250px;
        }
        .title-section, .blog-title, .quote{
            color:#404040;
        }
        .page-section p{
            color:#666666;
        }
        .page-section .profile-item p{
            color:white;
        }
        .portfolio-item .pic .item-content .portfolio-title{
            line-height: 50px;
        }
        .fa.fa-search.active{
            color:lightseagreen!important;
        }
    </style>
    <script type="text/javascript">
        <#--if(${isXp?string} &&　${isIE?string}){-->
            <#--alert('Win XP系统仅支持360等双核浏览器的极速模式');-->
        <#--}else if(${isIE?string} && ${ieVersion?string} < 9){-->
            <#--alert('平台部分业务功能需在IE9+中才能正常执行，请使用IE9+浏览器');-->
        <#--}-->
        var isXp = ${isXp?string};
    </script>
    <#--<script type="text/javascript" src="/images/jquery/ui/js/jquery.cookie-1.4.0.js"></script>-->
</head>
<body>
<!-- header page-->
<header style="margin-bottom: 100px;">
    <!-- site top panel-->
   <#-- <div class="site-top-panel">
        <div class="container p-relative">
            <div class="row">
                <div class="col-sm-6">
                    <!-- lang select wrapper&ndash;&gt;
                    <div class="top-left-wrap">
                        <div class="lang-wrap">
                            <div>
                                资协网标语
                            </div>
                        </div>
                    &lt;#&ndash;<div class="social-wrap"><a href="https://plus.google.com/" title="Google +" class="cws_social_link"><i class="share-icon fa fa-google-plus"></i></a><a href="http://twitter.com/" title="Twitter" class="cws_social_link"><i class="share-icon fa fa-twitter"></i></a><a href="http://facebook.com" title="Facebook" class="cws_social_link"><i class="share-icon fa fa-facebook"></i></a><a href="http://dribbble.com" title="Dribbble" class="cws_social_link"><i class="share-icon fa fa-dribbble"></i></a></div>&ndash;&gt;
                    </div>
                    <!-- ! lang select wrapper&ndash;&gt;
                </div>
                <div class="col-sm-6 text-right">
                    <div class="top-right-wrap">
                        <div class="top-login"><i class="flaticon-people-2"></i><a href="/zxdoc/control/main"><#if userLogin?has_content>进入平台<#else>登录|注册</#if></a></div>
                    &lt;#&ndash;<span>|</span>
                    <div class="top-register"><i class="flaticon-mark"></i><a href="/zxdoc/control/main">注册</a></div>&ndash;&gt;
                    </div>
                </div>
                <div class="search_menu_cont">
                    <form role="search" method="get" class="form">
                        <div class="search-wrap">
                            <input type="text" placeholder="Search . . ." class="form-control search-field">
                        </div>
                    </form>
                    <div class="search_back_button"><i class="fa fa-close"></i></div>
                </div>
            </div>
        </div>
    </div>-->
    <!-- ! site top panel-->
    <!-- Navigation panel-->


    <nav class="main-nav js-stick" style="position: fixed">
        <div class="full-wrapper relative clearfix">
            <!-- Logo ( * your text or image into link tag *)-->
            <div style="padding: 10px">
                <a class="logo">
                    <img src="http://www.zhongxi-cpa.com/uploadpic/20188149481756329.gif" alt class="sticky-logo">
                </a>
            </div>
            <!-- Main Menu-->
            <#--<div class="inner-nav desktop-nav switch-menu" >-->
            <div>
            <#--<ul class="clearlist">
                <!-- Item With Sub&ndash;&gt;
                <li><a href="index.html" class="mn-has-sub active">Home <i class="fa fa-angle-down button_open"></i></a>
                    <ul class="mn-sub">
                        <li class="active"><a href="index.html">Standart Slider</a></li>
                        <li><a href="index-slider.html">Full-Screen Slider</a></li>
                        <li><a href="index-video.html">Video Background</a></li>
                    </ul>
                </li>
                <!-- End Item With Sub&ndash;&gt;
                <!-- Item With Sub&ndash;&gt;
                <li class="megamenu"><a href="page-about-us.html" class="mn-has-sub">Pages <i class="fa fa-angle-down button_open"></i></a>
                    <!-- Sub&ndash;&gt;
                    <ul class="mn-sub mn-has-multi">
                        <li class="mn-sub-multi"><a class="mn-group-title">Pages</a>
                            <ul>
                                <li><a href="page-about-us.html">About Us</a></li>
                                <li><a href="page-services.html">Services</a></li>
                                <li><a href="page-procces.html">Our Procces</a></li>
                                <li><a href="page-our-team.html">Our Team</a></li>
                                <li><a href="page-profile.html">Profile</a></li>
                                <li><a href="page-elements.html">Elements</a></li>
                                <li><a href="page-typography.html">Typography</a></li>
                                <li><a href="page-404.html">Page 404</a></li>
                            </ul>
                        </li>
                        <li class="mn-sub-multi"><a class="mn-group-title">Sale</a>
                            <ul>
                                <li>
                                    <div class="sale-wrap mt-15">
                                        <div class="widget-image alt"><img src="/zxdoc/static/index/pic/shop/270x370.jpg" alt>
                                            <div class="img-title"><a href="shop-grid.html" class="cws-button small alt">Check it</a>
                                                <h3>Holiday Sale</h3>
                                                <p>30% Off</p>
                                            </div>
                                        </div>
                                    </div>
                                </li>
                            </ul>
                        </li>
                        <li class="mn-sub-multi"><a class="mn-group-title">Popular</a>
                            <ul>
                                <li>
                                    <div class="product mt-15 align-left">
                                        <!-- picture&ndash;&gt;
                                        <div class="pic"><a href="shop-single.html"><img src="/zxdoc/static/index/pic/shop/220x240.jpg" alt></a></div>
                                        <!-- ! picture&ndash;&gt;
                                        <h3 class="product-title mt-10 mb-5"><a href="shop-single.html">Maecenas nec odio</a></h3>
                                        <div class="price-review">
                                            <div class="star-rating full"><span style="width:100%"></span></div>
                                            <div class="price">$54.<span class="price-mini">99</span></div>
                                        </div>
                                    </div>
                                </li>
                            </ul>
                        </li>
                    </ul>
                    <!-- End Sub&ndash;&gt;
                </li>
                <!-- End Item With Sub&ndash;&gt;
                <!-- Item With Sub&ndash;&gt;
                <li><a href="portfolio-3-col.html" class="mn-has-sub">Portfolio <i class="fa fa-angle-down button_open"></i></a>
                    <!-- Sub&ndash;&gt;
                    <ul class="mn-sub">
                        <li><a href="portfolio-3-col.html">Three Columns</a></li>
                        <li><a href="portfolio-4-col.html">Four Columns</a></li>
                        <li><a href="portfolio-masonry.html">Portfolio Masonry</a></li>
                        <li><a href="portfolio-with-sidebar.html">With Sidebar</a></li>
                        <li><a href="portfolio-gallery.html">Gallery</a></li>
                        <li><a href="page-portfolio-single.html">Portfolio Single</a></li>
                    </ul>
                    <!-- End Sub&ndash;&gt;
                </li>
                <!-- End Item With Sub&ndash;&gt;
                <!-- Item With Sub&ndash;&gt;
                <li><a href="blog-sidebar-right.html" class="mn-has-sub">Blog <i class="fa fa-angle-down button_open"></i></a>
                    <!-- Sub&ndash;&gt;
                    <ul class="mn-sub">
                        <li><a href="blog-2-col.html" class="mn-has-sub">Columns<i class="fa fa-angle-right pull-right button_open"></i></a>
                            <ul class="mn-sub to-left">
                                <li><a href="blog-2-col.html">Two Columns</a></li>
                                <li><a href="blog-2-col-sidebar.html">Two Columns + Sidebar</a></li>
                                <li><a href="blog-3-col.html">Three Columns</a></li>
                            </ul>
                        </li>
                        <li><a href="blog-sidebar-right.html">Blog Right Sidebar</a></li>
                        <li><a href="blog-medium-img.html">Medium Images</a></li>
                        <li><a href="blog-small-img.html">Small Images</a></li>
                        <li><a href="blog-single.html">Blog Single</a></li>
                    </ul>
                    <!-- End Sub&ndash;&gt;
                </li>
                <!-- End Item With Sub&ndash;&gt;
                <!-- Item With Sub&ndash;&gt;
                <li><a href="shop-grid.html" class="mn-has-sub">Shop <i class="fa fa-angle-down button_open"></i></a>
                    <!-- Sub&ndash;&gt;
                    <ul class="mn-sub">
                        <li><a href="shop-grid.html">Shop Grid</a></li>
                        <li><a href="shop-cart.html">Shop Cart</a></li>
                        <li><a href="shop-checkout.html">Shop Checkout</a></li>
                        <li><a href="shop-single.html">Shop Single Product</a></li>
                    </ul>
                    <!-- End Sub&ndash;&gt;
                </li>
                <!-- End Item With Sub&ndash;&gt;
                <!-- Item&ndash;&gt;
                <li><a href="page-contact.html">Contact</a></li>
                <!-- End Item&ndash;&gt;
            </ul>-->
                <div class="menu-contacts" style="text-align: left;margin-left: 100px">
                    <#--<div class="menu-contacts-item tip" title="资协网">
                        <p class="mb-0" style="padding-left: 27px;color:lightseagreen;font-size:30px">带您进入资本的蓝洞</p>
                        <div style="height:25px; background: url('/metronic-web/images/logo-bg-big-www.png?t=20170123') no-repeat;"></div>
                    </div>
                <div class="menu-contacts-item"><i class="flaticon-placeholder cws-icon"></i>
                    <p class="mb-0">上海市徐汇区宜山路888号</p>
                    <p class="mb-0">新银大厦2208室</p>
                </div>
                <div class="menu-contacts-item"><i class="flaticon-time cws-icon"></i>
                    <p class="mb-0">周一 - 周日: 00:00 - 24:00 </p>
                    <p class="mb-0">7x24小时</p>
                </div>-->
                </div>
                <input type="hidden" name="externalLoginKey" value="${externalLoginKey!}">
                    <div style="position: absolute;top:30%;right:20px;color:lightseagreen;max-width: 150px">
                        <a href="${request.contextPath}/static/%E8%B5%84%E7%A7%98%E7%BD%91Client.exe">下载客户端</a>&nbsp;|&nbsp;<a href="${request.contextPath}/control/registerUser">注册</a>
                        <br/><a href="javascript:viewOfficeInLayer('11370')">测试控件</a>
                    </div>
            <#--<a href="#" class="menu-bar"><span class="ham"></span></a>-->
            </div>
            <!-- End Main Menu-->


        </div>
    </nav>
    <!-- End Navigation panel-->
</header>
<!-- ! header page-->
<div class="content-body boxed">