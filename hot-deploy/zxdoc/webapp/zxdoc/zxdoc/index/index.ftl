<script type="text/javascript" src="/images/jquery/jquery-1.11.0.min.js"></script>
<script type="text/javascript" src="/images/lib/layer/layer.js?t=20170909"></script>

<style type="text/css">
    .backNoColor{
        background-color:#000000;/* IE6和部分IE7内核的浏览器(如QQ浏览器)下颜色被覆盖 */
        background-color:rgba(0,0,0,0.2); /* IE6和部分IE7内核的浏览器(如QQ浏览器)会读懂，但解析为透明 */
    }
    #filter-grid .file-title a{
        font-size:12px;
    }

    #filter-grid .file-date {
        text-align: right;
        font-size:12px;
        display:none;
        float:right;
        position:absolute;
        /*bottom: 2px;*/
        right:2px;
        top:-10px;
    }
    #filter-grid .file-name-wrapper{
        position:relative;
    }
    #filter-grid .file-name-wrapper:hover .file-date{
        display: inline-block;
    }

    #filter-grid .portfolio-item{
        background-color: white;
        color:#666666;
    }
</style>
<script type="text/javascript">
    $(function() {
        setTimeout(function(){
            $.ajax({
                type: "post",
                url: "/zxdoc/control/getAdvert",
                async: true,
                success: function (str) {
                    if(str.data != null && str.data.length > 0) {
                        indexs = layer.open({
                            type: 1,
                            zIndex:10,
                            maxmin:true,
                            title: "",
                            closeBtn: 0, //不显示关闭按钮
                            shade: 0,
                            move:false,
                            area: ['340px', '215px'],
                            offset: 'l', //左下角弹出
                            shift: 2,
                            content: str.data[0].description,
                            success : function(layero, index) {// 弹出成功之后，隐藏最大化按钮
                                layero.find('.layui-layer-max').hide();
                            },
                            min:function(layero){
                                $("#layui-layer2").hidden;
                                showMsg();
                            },
                        });
                        layer.style(indexs, {
                            color: "#000000"
                        });
                    }
                }
            })
        }, 5000);
    });

    function showMsg()
    {
        mindex =layer.open({
            skin:"backNoColor",
            type: 1,
            zIndex:10,
            title: "",
            closeBtn: 0, //不显示关闭按钮
            shade: 0,
            move:false,
            area: ['80px', '40px'],
            offset: 'l', //左下角弹出
            time: 99999999, //不自动关闭
            btn:['口'],
            yes:function(index, layero){
                layero.hide();
                $("#layui-layer3").css("zIndex",3);
                $("#layui-layer2").css("zIndex",30);
                layer.restore(indexs);
            },
        })
    }
    /*$.post('getAdvert', {}, function(data){
        console.log(data);
        layer.open({
            type: 1,
            title: data[0].ruleName,
            closeBtn: 1, //不显示关闭按钮
            shade: [0],
            area: ['340px', '215px'],
            offset: ['380px','0px'], //右下角弹出
            time: 99999999, //2秒后自动关闭
            shift: 2,
            content:data[0].description,
        });
    });*/
</script>
<div class="tp-banner-container" style="position:relative;">

        <div class="tp-banner-slider">
            <ul>
                <li data-masterspeed="700" data-slotamount="7" data-transition="fade">
                    <img src="/zxdoc/static/index/pic/slider/main/slide-1.jpg" data-bgfit="cover" data-bgposition="center 70%" data-lazyload="/zxdoc/static/index/pic/slider/main/slide-1.jpg" alt="" data-bgparallax="10">
                    <div data-x="['left','left','center','center']" data-hoffset="0" data-y="center" data-voffset="-5%" data-width="['470px','350px','250px','200px']" data-transform_in="x:-150px;opacity:0;s:1500;e:Power3.easeInOut;" data-transform_out="x:150px;opacity:0;s:1000;e:Power2.easeInOut;s:1000;e:Power2.easeInOut;" data-start="400" class="tp-caption sl-content">
                        <div class="sl-title" style="color: lightseagreen">进入资本的蓝洞</div>
                        <div class="sl-title"></div>
                        <div class="sl-title-top" style="padding: 5px 0;color:lightseagreen">信任我们</div>
                        <div class="sl-title" style="color: lightseagreen">管理 <br /><span style="display:block;margin: 10px 0">您的业务</span></div>
                        <#--<p style="color: white;">以持续的专业培训为后盾，不断完善经营管理机制和业务质量管理制度，谋求事务所与优秀专业人才的共同发展.</p>-->
                    </div>
                </li>
                <li data-masterspeed="700" data-transition="fade" class="without-overlay">
                    <img src="/zxdoc/static/index/pic/slider/main/slide-2-1.jpg" data-lazyload="/zxdoc/static/index/pic/slider/main/slide-2-1.jpg" data-bgposition="center 45%" alt="" data-bgparallax="10">
                    <div data-x="['right','right','center','center']" data-hoffset="0" data-y="center" data-voffset="-5%" data-width="['470px','350px','250px','200px']" data-transform_in="y:-150px;opacity:0;s:1500;e:Power3.easeInOut;" data-transform_out="y:150px;opacity:0;s:1000;e:Power2.easeInOut;s:1000;e:Power2.easeInOut;" data-start="400" class="tp-caption sl-content align-center">
                        <div class="sl-title-top" style="padding: 5px 0;">第三方机构的</div>
                        <div class="sl-title">人性化 <span>服务平台</span></div>
                        <#--<p>说明。。。。。。。。.</p>-->
                    </div>
                </li>
            <#--<li data-masterspeed="700" data-transition="fade" class="without-overlay"><img src="/zxdoc/static/index/rs-plugin/assets/loader.gif" data-lazyload="/zxdoc/static/index/pic/slider/main/slide-3-1.jpg" data-bgposition="center 67%" alt="" data-kenburns="on" data-duration="30000" data-ease="Linear.easeNone" data-scalestart="100" data-scaleend="120" data-rotatestart="0" data-rotateend="0" data-offsetstart="0 0" data-offsetend="0 0" data-bgparallax="10">
                <div data-x="['right','right','center','center']" data-hoffset="0" data-y="center" data-voffset="-5%" data-width="['470px','350px','250px','200px']" data-transform_in="x:150px;opacity:0;s:1500;e:Power3.easeInOut;" data-transform_out="x:-150px;opacity:0;s:1000;e:Power2.easeInOut;s:1000;e:Power2.easeInOut;" data-start="400" class="tp-caption sl-content align-right">
                    <div class="sl-title-top text-white">We think globally</div>
                    <div class="sl-title text-white">World <span>of ideas</span></div>
                    <p class="text-white">Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor.</p><a href="#" class="cws-button white">Learn more</a>
                </div>
            </li>-->
            </ul>
        </div>
    <#--<#if userLogin?has_content>
    <div style="position:absolute;bottom:10px;left:50px;">
        <a href="/zxdoc/control/main" class="cws-button">进入平台&nbsp;<i class="fa fa-angle-double-right"></i><i class="fa fa-angle-double-right"></i></a>
    </div>
    <#else>
    <form class="login-form" method="post" id="loginform" name="loginform" action="${request.contextPath}/control/login">
        <div style="position:absolute;bottom:260px;left:50px;padding: 20px;border-radius: 5px;">
            <h3 style="text-align: center;font-family: \5FAE\8F6F\96C5\9ED1;color:black;padding-bottom:15px;" class="form-title">登录账户</h3>
            <div class="form-group">
                <!--ie8, ie9 does not support html5 placeholder, so we just show field title for that&ndash;&gt;
                <label class="control-label visible-ie8 visible-ie9">用户名</label>
                <div class="input-icon">
                    <i class="fa fa-user"></i>
                    <input class="form-control placeholder-no-fix" type="text" autocomplete="off" placeholder="用户名" id="loginUserName" name="USERNAME" /> </div>
            </div>
            <div class="form-group">
                <label class="control-label visible-ie8 visible-ie9">密码</label>
                <div class="input-icon">
                    <i class="fa fa-lock"></i>
                    <input class="form-control placeholder-no-fix" type="password" autocomplete="off" placeholder="密码" id="loginPassword" name="PASSWORD" /> </div>
            </div>
            <div class="form-actions">
                &lt;#&ndash;<label><input type="checkbox" name="remember" value="1" /> 记住我 </label>&ndash;&gt;
                <button type="submit" class="btn green"> 登录 &nbsp;<i class="fa fa-angle-double-right"></i></button>
                <a href="#nowhere" class="btn green pull-right"> 注册&nbsp;<i class="fa fa-long-arrow-right"></i> </a>
            </div>
        </div>
    </form>
    </#if>-->
    </div>
    <!-- page section-->
   <#-- <section class="page-section pb-0">
        <div class="container">
            <div class="row">
                <div class="col-md-8 col-md-offset-2 mb-20">
                    <h2 class="title-section mb0 mt-0 text-center">特殊普通合伙 </h2>
                    <div class="cws_divider with-plus short-3 center mb-20 mt-10"></div>
                    <p class="text-center mb-30">中喜秉承“至诚、至精、至公“的执业理念，恪守“独立、容观、公正”的执业原则，建立和完善了经营管理机制和业务质量管理制度，视优秀专业人才为发展之本，并以持续的专业培训为后盾，使员T具备良好的执业操守。注重与国内同行、国际著名的会计机构交流与合作，学习国际先进经验，不断提高服务质量与执业水平</p>
                </div>
                <!-- service item&ndash;&gt;
                <div class="col-sm-4 mb-sm-40">
                    <div class="service-item icon-center"><i class="flaticon-computer-1 cws-icon type-1 color-2"></i>
                        <h3>Fully Responsive</h3>
                        <p class="mb-0">Donec quam felis, ultricies nec, pellentesque eu, bulsi pretium quis, sem massa quis enim.  <a href="#" class="color-1 mt-10">Read More<span class="cws_divider short color-1"></span></a></p>
                    </div>
                </div>
                <!-- service item&ndash;&gt;
                <!-- ! service item&ndash;&gt;
                <div class="col-sm-4 mb-sm-40">
                    <div class="service-item icon-center"><i class="flaticon-work cws-icon type-1 color-2"></i>
                        <h3>Awesome Options</h3>
                        <p class="mb-0">In enim justo, rhoncus ut, imperdiet a, venenatis vitae justo moest ransu quis lorem. <a href="#" class="color-1 mt-10">Read More<span class="cws_divider short color-1"></span></a></p>
                    </div>
                </div>
                <!-- service item&ndash;&gt;
                <!-- ! service item&ndash;&gt;
                <div class="col-sm-4">
                    <div class="service-item icon-center"><i class="flaticon-people-1 cws-icon type-1 color-2"></i>
                        <h3>Great Support</h3>
                        <p class="mb-0">Nullam dictum felis eu pede mollis pretium. Integer daser tincidunt. Cras dapibus.  <a href="#" class="color-1 mt-10">Read More<span class="cws_divider short color-1"></span></a></p>
                    </div>
                </div>
                <!-- ! service item&ndash;&gt;
            </div>
            <div class="cws_divider with-plus long color-line-1 mt-80"></div>
        </div>
    </section>-->
    <!-- ! page section -->
    <!-- page section services-->
    <#--<section class="page-section pt-80 pb-0">
        <div class="container">
            <div class="row pb-100">
                <div class="col-md-8 col-md-offset-2 mb-20">
                    <h2 class="title-section mb0 mt-0 text-center">特殊普通合伙 </h2>
                    <div class="cws_divider with-plus short-3 center mb-20 mt-10"></div>
                    <p class="text-center mb-30">中喜秉承“至诚、至精、至公“的执业理念，恪守“独立、容观、公正”的执业原则，建立和完善了经营管理机制和业务质量管理制度，视优秀专业人才为发展之本，并以持续的专业培训为后盾，使员T具备良好的执业操守。注重与国内同行、国际著名的会计机构交流与合作，学习国际先进经验，不断提高服务质量与执业水平</p>
                </div>
            </div>
        </div>
        <div class="page-section pb-0 pt-0 bg-gray">
            <div class="container">
                <div class="row pt-100 pb-0">
                    <div class="col-md-4 mb-md-40">
                        <div class="service-item icon-left mb-60 alt"><i class="flaticon-work cws-icon"></i>
                            <h3>审计</h3>
                            <p class="mb-0">财务报表审计、离任经济责任审计、企业破产、清算审计、外汇年检审计、法律、法规规定的其他审计业务</p>
                        </div>
                        <div class="service-item icon-left alt"><i class="flaticon-school cws-icon"></i>
                            <h3>验资</h3>
                            <p class="mb-0">企业新设验资、企业变更验资</p>
                        </div>
                    </div>
                    <div class="col-md-4 flex-item-end mb-md-40"><img src="/zxdoc/static/index/pic/promo-1.png" alt class="mt-minus-170 mt-md-0"></div>
                    <div class="col-md-4 mb-md-40">
                        <div class="service-item icon-right mb-60 alt"><i class="flaticon-technology-1 cws-icon"></i>
                            <h3>咨询</h3>
                            <p class="mb-0">企业发展战略咨询、资产经营与并购重组咨询、企业经营管理咨询、企业管理信息化咨询. </p>
                        </div>
                        <div class="service-item icon-right alt"><i class="flaticon-technology cws-icon"></i>
                            <h3>其他业务</h3>
                            <p class="mb-0">担任会计司法鉴定人、担任破产清算策划、担任公司破产管理人、工程决算财务审计.</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <!-- page section about&ndash;&gt;
    <!-- call out section&ndash;&gt;
    <section class="page-section pt-0 pb-0 bg-main cws_prlx_section"><img src="/zxdoc/static/index/pic/callout-bg.jpg" alt class="cws_prlx_layer">
        <div class="container">
            <div class="call-out-box clearfix with-icon">
                <div class="callout-wrap"><i class="flaticon-fashion"></i>
                    <div class="callout-content">
                        <p>资协网标语.</p>
                    </div><a href="/zxdoc/control/main" class="cws-button white mt-20">注册试用</a>
                </div>
            </div>
        </div>
    </section>-->
    <!-- ! call out section-->
    <!-- page section about-->
    <#--<section style="background-image:url('/zxdoc/static/index/pic/back-1.jpg');'" class="page-section bt-gray bg-n-rep bg-x-50">
        <div class="container">
            <div class="row">
                <div class="col-md-6 mb-md-50"></div>
                <div class="col-md-6">
                    <!-- section title&ndash;&gt;
                    <h2 class="title-section mt-0 mb-0">About us</h2>
                    <!-- ! section title&ndash;&gt;
                    <div class="cws_divider with-plus short-3 mb-20 mt-10"></div>
                    <p class="mb-30">Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim.</p>
                    <!-- accordion&ndash;&gt;
                    <div class="accordion style-1 mb-40">
                        <div class="content-title"> <span class="active"><i class="flaticon-shapes"></i>Maecenas tempus, tellus eget condimentum rhoncus, sem quam ?<i class="accordion_angle fa fa-angle-down"></i></span></div>
                        <div class="content">
                            <p>Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, aug ue velit cursus nunc, quis gravida magna mi a libero. </p>
                        </div>
                        <div class="content-title"> <span><i class="flaticon-technology-3"></i>Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id lorem ?<i class="accordion_angle fa fa-angle-down"></i></span></div>
                        <div class="content">Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, aug ue velit cursus nunc, quis gravida magna mi a libero. </div>
                        <div class="content-title"> <span><i class="flaticon-favorite"></i>Donec vitae sapien ut libero venenatis faucibus, sit amet orci eget ? <i class="accordion_angle fa fa-angle-down"></i></span></div>
                        <div class="content">Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, aug ue velit cursus nunc, quis gravida magna mi a libero. </div>
                    </div>
                    <!-- ! accordion&ndash;&gt;<a href="#" class="cws-button alt with-icon mb-xs-20 mr-xs-10">Contact Us<i class="flaticon-note-1"></i></a><a href="#" class="cws-button alt with-icon color-3">Online support<i class="flaticon-social-5"></i></a>
                </div>
            </div>
        </div>
        <!-- list&ndash;&gt;
    </section>-->
    <!-- ! page section about-->
    <!-- section team-->
    <section class="page-section pt-70 pb-70 bg-gray" <#--data-original="/zxdoc/static/index/pic/bg-2.jpg"--> style="/*background: url('/zxdoc/static/index/pic/bg-2.jpg');*/background-size: 100% 100%">
        <div class="container">
            <div class="row">
                <div class="col-md-8 col-md-offset-2">
                    <!-- title section-->
                    <h2 class="title-section text-center mt-0 mb-0">第三方机构</h2>
                    <!-- ! title section-->
                    <div class="cws_divider with-plus short-3 center mb-20 mt-10"></div>
                    <p class="text-center mb-30">
                        企业、会计事务所、券商、律师事务所、其他机构
                    </p>
                </div>
            </div>
            <div class="row cws-multi-col mb-50">
                <div class="col-md-2 col-md-offset-1 col-sm-6 mb-md-30">
                    <!-- profile item-->
                    <a href="${request.contextPath}/control/groupList?groupType=CASE_ROLE_OWNER" target="_blank">
                    <div class="profile-item">
                        <div class="pic">
                            <div class="img_cont"><img class="lazy" data-original="/zxdoc/static/index/pic/team/1.jpg" data-at2x="/zxdoc/static/index/pic/team/1.jpg" alt></div>
                            <div class="hover-effect"></div>
                            <div class="ourteam_content">
                                <div class="title_wrap">
                                    <h3 class="title">企业</h3>
                                </div>
                                <div class="desc">
                                    <p>在线发布信息、在第三方机构协作下实现业务流程.</p>
                                </div>
                            <#--<div class="social_links"><a href="#" class="flaticon-social-4"></a><a href="#" class="flaticon-social"></a><a href="#" class="flaticon-social-network"></a><a href="#" class="flaticon-social-1"></a><a href="#" class="flaticon-social-3"></a></div>-->
                            </div>
                        </div>
                    </div>
                    </a>
                    <!-- ! profile item-->
                </div>
                <div class="col-md-2 col-sm-6 mb-md-30">
                    <!-- profile item-->
                    <a href="${request.contextPath}/control/groupList?groupType=CASE_ROLE_ACCOUNTING" target="_blank">
                    <div class="profile-item">
                        <div class="pic">
                            <div class="img_cont"><img class="lazy" data-original="/zxdoc/static/index/pic/team/2.jpg" data-at2x="/zxdoc/static/index/pic/team/2.jpg" alt></div>
                            <div class="hover-effect"></div>
                            <div class="ourteam_content">
                                <div class="title_wrap">
                                    <h3 class="title">会计师事务所</h3>
                                </div>
                                <div class="desc">
                                    <p>提供专业的会计业务支持，协助企业实现业务流程.</p>
                                </div>
                            <#--<div class="social_links"><a href="#" class="flaticon-social-4"></a><a href="#" class="flaticon-social"></a><a href="#" class="flaticon-social-network"></a><a href="#" class="flaticon-social-1"></a><a href="#" class="flaticon-social-3"></a></div>-->
                            </div>
                        </div>
                    </div>
                    </a>
                    <!-- ! profile item-->
                </div>
                <div class="col-md-2 col-sm-6 mb-sm-30">
                    <!-- profile item-->
                    <a href="${request.contextPath}/control/groupList?groupType=CASE_ROLE_STOCK" target="_blank">
                    <div class="profile-item">
                        <div class="pic">
                            <div class="img_cont"><img class="lazy" data-original="/zxdoc/static/index/pic/team/3.jpg" data-at2x="/zxdoc/static/index/pic/team/3.jpg" alt></div>
                            <div class="hover-effect"></div>
                            <div class="ourteam_content">
                                <div class="title_wrap">
                                    <h3 class="title">券商</h3>
                                </div>
                                <div class="desc">
                                    <p>督导业务，协助企业实现业务流程.</p>
                                </div>
                            <#--<div class="social_links"><a href="#" class="flaticon-social-4"></a><a href="#" class="flaticon-social"></a><a href="#" class="flaticon-social-network"></a><a href="#" class="flaticon-social-1"></a><a href="#" class="flaticon-social-3"></a></div>-->
                            </div>
                        </div>
                    </div>
                    </a>
                    <!-- ! profile item-->
                </div>
                <div class="col-md-2 col-sm-6">
                    <!-- profile item-->
                    <a href="${request.contextPath}/control/groupList?groupType=CASE_ROLE_LAW" target="_blank">
                    <div class="profile-item">
                        <div class="pic">
                            <div class="img_cont"><img class="lazy" data-original="/zxdoc/static/index/pic/team/4.jpg" data-at2x="/zxdoc/static/index/pic/team/4.jpg" alt></div>
                            <div class="hover-effect"></div>
                            <div class="ourteam_content">
                                <div class="title_wrap">
                                    <h3 class="title">律师事务所</h3>
                                </div>
                                <div class="desc">
                                    <p>提供专业的法务支持，协助企业实现业务流程.</p>
                                </div>
                            <#--<div class="social_links"><a href="#" class="flaticon-social-4"></a><a href="#" class="flaticon-social"></a><a href="#" class="flaticon-social-network"></a><a href="#" class="flaticon-social-1"></a><a href="#" class="flaticon-social-3"></a></div>-->
                            </div>
                        </div>
                    </div>
                    </a>
                    <!-- ! profile item-->
                </div>
                <div class="col-md-2 col-sm-6">
                    <!-- profile item-->
                    <a href="${request.contextPath}/control/groupList?groupType=CASE_ROLE_INVESTOR" target="_blank">
                    <div class="profile-item">
                        <div class="pic">
                            <div class="img_cont"><img class="lazy" data-original="/zxdoc/static/index/pic/team/5.jpg" data-at2x="/zxdoc/static/index/pic/team/5.jpg" alt></div>
                            <div class="hover-effect"></div>
                            <div class="ourteam_content">
                                <div class="title_wrap">
                                    <h3 class="title">其他机构</h3>
                                </div>
                                <div class="desc">
                                    <p>协助企业实现业务流程.</p>
                                </div>
                            <#--<div class="social_links"><a href="#" class="flaticon-social-4"></a><a href="#" class="flaticon-social"></a><a href="#" class="flaticon-social-network"></a><a href="#" class="flaticon-social-1"></a><a href="#" class="flaticon-social-3"></a></div>-->
                            </div>
                        </div>
                    </div>
                    </a>
                    <!-- ! profile item-->
                </div>
            </div>
        <#--<div class="row">
            <div class="col-md-6">
                <h3 class="trans-uppercase mb-20"><span>About</span> John Doe</h3>
                <p class="mb-40">Integer ante arcu, accumsan a, consectetuer eget, posuere ut, mauris. Praesent adipiscing,  ula lamcorper ipsum rutrum nunc. Nunc nonummy metus. Vestibulum volutpat pretium liberodel enean ut eros et nisl sagittis vestibulum. Nullam nulla eros, ultricies sit amet, nonummy idiime perdiet feugiat, pede. Sed lectus. </p><a href="#" class="cws-button with-icon alt color-2 blog-nav-button">View profile<i class="flaticon-social-5"></i></a>
                <div class="blog-nav-share align-right"><a href="#" class="cws-social flaticon-social-4"></a><a href="#" class="cws-social flaticon-social"></a><a href="#" class="cws-social flaticon-social-3"></a><a href="#" class="cws-social flaticon-social-1"></a><a href="#" class="cws-social flaticon-social-network"></a></div>
            </div>
            <div class="col-md-6">
                <h3 class="trans-uppercase mb-20">Skills</h3>
                <!-- skill bar item&ndash;&gt;
                <div class="skill-bar st-color-1">
                    <div class="name">SEO<span class="skill-bar-perc"></span></div>
                    <div class="bar"><span data-value="80" class="cp-bg-color skill-bar-progress"></span></div>
                </div>
                <!-- ! skill bar item&ndash;&gt;
                <!-- skill bar item&ndash;&gt;
                <div class="skill-bar st-color-2">
                    <div class="name">Developing<span class="skill-bar-perc"></span></div>
                    <div class="bar"><span data-value="90" class="cp-bg-color skill-bar-progress"></span></div>
                </div>
                <!-- ! skill bar item&ndash;&gt;
                <!-- skill bar item&ndash;&gt;
                <div class="skill-bar st-color-3">
                    <div class="name">Design<span class="skill-bar-perc"></span></div>
                    <div class="bar"><span data-value="70" class="cp-bg-color skill-bar-progress"></span></div>
                </div>
                <!-- ! skill bar item&ndash;&gt;
                <!-- skill bar item&ndash;&gt;
                <div class="skill-bar st-color-4">
                    <div class="name">CEO<span class="skill-bar-perc"></span></div>
                    <div class="bar"><span data-value="95" class="cp-bg-color skill-bar-progress"></span></div>
                </div>
                <!-- ! skill bar item&ndash;&gt;
            </div>
        </div>-->
        </div>
    </section>
    <!-- ! section team-->
    <!-- section portfolio filter-->
    <section class="page-section pb-0 pattern-1 relative pt-70 pb-70" <#--data-original="/zxdoc/static/index/pic/bg-4.jpg"--> style="/*background: url('/zxdoc/static/index/pic/bg-4.jpg');*/background-size: 100% 100%">
        <div class="container">
            <div class="row">
                <div class="col-md-8 col-md-offset-2">
                    <!-- title section-->
                    <h2 class="title-section text-center mt-0 mb-0">政策文件&nbsp;<i style="color:lightskyblue;cursor: pointer" class="fa fa-search" onclick="togglePolicySearch(this)"></i></h2>
                    <!-- ! title section-->
                    <div class="cws_divider with-plus short-3 center mb-20 mt-10"></div>
                    <p class="text-center mb-30">
                        提供最新的业务规则、部门规章、法律法规及其他各项服务指南
                    </p>
                    <div id="policySearch" class="col-xs-6 col-xs-offset-3 mb-20" style="display: none;">
                        <div class="input-group">
                            <input type="text" id="searchKey" class="form-control" value="${searchKey!''}" name="searchKey" style="width: 100%;height:34px;" placeholder="请输入查询关键字"/>
                            <span class="input-group-btn">
                            <button class="btn green" type="button" onclick="searchPolicy();">查询</button>
                        </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- filter-->
        <div class="isotop-container">
            <input hidden value="${error?default('')}" id="errors">
            <div class="work-filter">
                <#--<a href="#" data-filter="*" class="filter active">全部文件</a>-->
                <#list topCategories as category>
                    <a href="#" data-filter=".${category.enumId}" class="filter <#if category?index ==0>active</#if>">${category.description}</a>
                    <#if category?index ==0>
                        <#assign activeCategory = category.enumId>
                    </#if>
                </#list>
            </div>
            <div id="filter-grid" class="row cws-multi-col portfolio-grid">
            <#list topCategories as category>
                <#if policyFiles?keys?seq_contains(category.enumId)>
                    <#assign topFiles = policyFiles[category.enumId]>
                    <#assign fileArray = topFiles?chunk(10)>
                <#list fileArray as files>
            <div class="col-lg-3 col-md-4 col-sm-6 all ${category.enumId}">
                <!-- portfolio item-->
                <div class="portfolio-item text-center">
                    <div class="pic" <#--data-original="/zxdoc/static/index/pic/bg-glass.png"--> style="padding:10px;height: 360px;">
                        <table class="altrowstable" width="100%" align="center">
                                <#assign fileSize = 1>
                                <#list files as file>
                                    <#if fileSize = 6>
                                        <#break >
                                    </#if>
                                    <#assign fileSize = fileSize + 1>
                                    <#assign fileData = file.getRelatedOne("DataResource", false)>
                                    <tr>
                                        <td class="file-name-wrapper" style="text-align: left">
                                            <#assign fileName = fileData.dataResourceName[0..<fileData.dataResourceName?last_index_of(".")]>
                                            <#assign nameLength = StringUtil.xmlDecodedLength(fileName!)>
                                            <div class="file-title">
                                                <a onclick="viewPdfInLayer('${file.id}')" href="#nowhere"
                                                   title="${fileName}">
                                                <#--<#if nameLength gt 12>-->
                                                        <#--${StringUtil.xmlDecodedSubstring(fileName!, 0, 12)}...-->
                                                        <#--<#else>-->
                                                ${fileName!}
                                                <#--</#if>-->
                                            </a>
                                                </div>
                                            <span class="label label-danger file-date">${(file.publishDate!file.createdStamp)?string["yyyy-MM-dd"]}</span>
                                        </td>
                                        <#--<td style="text-align: right;white-space: nowrap">
                                        ${(file.publishDate!file.createdStamp)?string["yyyy-MM-dd"]}
                                        </td>-->
                                    </tr>
                                </#list>
                        </table>
                        <!-- item content -->
                        <div class="item-content" style="height: 50px">
                            <h3 class="portfolio-title">${category.description}</h3>
                            <#--<div class="categories">条目如何展示</div>-->
                            <div class="links" style="bottom: 0">
                                <a href="/zxdoc/control/listPolicyFile?category=${category.enumId}" class="link-icon flaticon-tool" title="更多"></a>
                            </div>
                        </div>
                    </div>

                </div>
                <!-- ! portfolio item-->
            </div>
                </#list>
                </#if>
            </#list>
            <#list subCategories as category>
                <div class="col-lg-3 col-md-4 col-sm-6 all ${category.enumTypeId}">
                    <!-- portfolio item-->
                    <div class="portfolio-item text-center">
                        <div class="pic"<#-- data-original="/zxdoc/static/index/pic/bg-glass.png"--> style="padding:10px;height: 360px;">
                            <table class="altrowstable" width="100%" align="center">
                                <#if policyFiles?keys?seq_contains(category.enumId)>
                                    <#assign files = policyFiles[category.enumId]>
                                    <#assign fileSize = 1>
                                    <#list files as file>
                                        <#if fileSize = 6>
                                            <#break >
                                        </#if>
                                        <#assign fileSize = fileSize + 1>
                                        <#assign fileData = file.getRelatedOne("DataResource", false)>
                                        <tr>
                                            <td class="file-name-wrapper" style="text-align: left">
                                                <#assign fileName = fileData.dataResourceName[0..<fileData.dataResourceName?last_index_of(".")]>
                                                <#assign nameLength = StringUtil.xmlDecodedLength(fileName!)>
                                                <div class="file-title">
                                                    <a onclick="viewPdfInLayer('${file.id}')" href="#nowhere"
                                                       title="${fileName}">
                                                        <#--<#if nameLength gt 15>-->
                                                        <#--${StringUtil.xmlDecodedSubstring(fileName!, 0, 15)}...-->
                                                        <#--<#else>-->
                                                        ${fileName!}
                                                        <#--</#if>-->
                                                    </a>
                                                </div>
                                                <span class="label label-danger file-date">${(file.publishDate!file.createdStamp)?string["yyyy-MM-dd"]}</span>
                                            </td>
                                            <#--<td style="text-align: right;white-space: nowrap">
                                            ${(file.publishDate!file.createdStamp)?string["yyyy-MM-dd"]}
                                            </td>-->
                                        </tr>
                                    </#list>
                                </#if>
                            </table>
                            <!-- item content -->
                            <div class="item-content" style="height: 50px">
                                <h3 class="portfolio-title">${category.description}</h3>
                            <#--<div class="categories">条目如何展示</div>-->
                                <div class="links" style="bottom: 0">
                                    <a href="/zxdoc/control/listPolicyFile?category=${category.enumId}"
                                       class="link-icon flaticon-tool" title="更多"></a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </#list>
            </div>
        </div>
        <#--<div class="button-center pb-40 mt-10"><a href="#" class="cws-button alt">Load More</a></div>-->
        <!-- ! filter-->
    </section>
    <!-- ! section portfolio filter-->

    <!-- section news-->
    <section class="page-section pb-70 pt-70" <#--data-original="/zxdoc/static/index/pic/bg-5.jpg" -->style="/*background: url('/zxdoc/static/index/pic/bg-5.jpg');*/background-size: 100% 100%">
        <div class="container">
            <div class="row">
                <div class="col-md-8 col-md-offset-2">
                    <!-- title section-->
                    <h2 class="title-section text-center mt-0 mb-0">平台动态</h2>
                    <!-- ! title section-->
                    <div class="cws_divider with-plus short-3 center mb-20 mt-10"></div>
                    <p class="text-center mb-30">资协网最新动态展示.</p>
                </div>
            </div>
            <div class="row">
                <div class="col-md-4 col-sm-6">
                    <!-- Blog Post -->
                    <div class="blog-item news">
                        <!-- Blog Image-->
                        <div class="blog-media">
                            <div class="pic"><img class="lazy" data-original="/zxdoc/static/index/pic/blog/370x260/1.jpg" data-at2x="/zxdoc/static/index/pic/blog/370x260/1.jpg" alt><#--<a href="blog-single.html" class="hover-effect alt"><span class="plus fa fa-plus"></span></a>--></div>
                            <div class="blog-date alt">
                                <div class="date">
                                    <div class="date-cont"><span class="day">15</span><span title="August" class="month"><span>8月</span></span><span class="year">2016</span><i class="springs"></i></div>
                                </div>
                            </div>
                        </div>
                        <!-- title, author...-->
                        <div class="blog-item-data clearfix">
                            <h4 class="blog-title">资协网立项</h4>
                            <p class="post-info">上海资协信息</p>
                        </div>
                        <!-- Text Intro-->
                        <div class="blog-item-body">
                            <p>资协网立项、调研用户需求，找到用户的痛点，有针对性的为客户解决问题</p>
                        </div>
                        <#--<a href="#" class="cws-button small alt">Read More</a>-->
                    </div>
                    <!-- ! Blog Post-->
                </div>
                <div class="col-md-4 col-sm-6">
                    <!-- ! Blog Post -->
                    <div class="blog-item news">
                        <!-- Blog Image-->
                        <div class="blog-media">
                            <div class="pic"><img class="lazy" data-original="/zxdoc/static/index/pic/blog/370x260/3.jpg" data-at2x="/zxdoc/static/index/pic/blog/370x260/3.jpg" alt></div>
                            <div class="blog-date alt">
                                <div class="date">
                                    <div class="date-cont"><span class="day">31</span><span title="October" class="month"><span>10月</span></span><span class="year">2016</span><i class="springs"></i></div>
                                </div>
                            </div>
                        </div>
                        <!-- title, author...-->
                        <div class="blog-item-data clearfix">
                            <h4 class="blog-title">资协网内部测试</h4>
                            <p class="post-info">上海资协信息</p>
                        </div>
                        <!-- Text Intro-->
                        <div class="blog-item-body">
                            <p>开始内部测试 ...</p>
                        </div>
                        <#--<a href="#" class="cws-button small alt">Read More</a>-->
                    </div>
                    <!-- ! Blog Post -->
                </div>
                <div class="col-md-4 col-sm-6">
                    <!-- ! Blog Post image-->
                    <div class="blog-item news">
                        <!-- Blog Image-->
                        <div class="blog-media">
                            <div class="pic"><img class="lazy" data-original="/zxdoc/static/index/pic/blog/370x260/2.jpg" data-at2x="/zxdoc/static/index/pic/blog/370x260/2.jpg" alt><#--<span class="plus fa fa-plus"></span>--></div>
                            <div class="blog-date alt">
                                <div class="date">
                                    <div class="date-cont"><span class="day">3</span><span title="January" class="month"><span>1月</span></span><span class="year">2017</span><i class="springs"></i></div>
                                </div>
                            </div>
                        </div>
                        <!-- title, author...-->
                        <div class="blog-item-data clearfix">
                            <h4 class="blog-title">试运行</h4>
                            <p class="post-info">上海资协信息</p>
                        </div>
                        <!-- Text Intro-->
                        <div class="blog-item-body">
                            <p>开始试运行 ...</p>
                        </div>
                        <#--<a href="#" class="cws-button small alt">Read More</a>-->
                    </div>
                    <!-- ! Blog Post image-->
                </div>
            </div>
        </div>
    </section>
    
    <!--新增 -->
    <section class="page-section pb-70 pt-70" <#--data-original="/zxdoc/static/index/pic/bg-5.jpg" -->style="/*background: url('/zxdoc/static/index/pic/bg-5.jpg');*/background-size: 100% 100%">
        <div class="container">
            <div class="row">
                <div class="col-md-8 col-md-offset-2">
                    <!-- title section-->
                    <h2 class="title-section text-center mt-0 mb-0">合作机构</h2>
                    <!-- ! title section-->
                    <div class="cws_divider with-plus short-3 center mb-20 mt-10"></div>
                    <p class="text-center mb-30">资协网合作机构展示.</p>
                </div>
            </div>
            <div class="row">
                <div class="col-md-4 col-sm-6">
                    <!-- Blog Post -->
                    <div class="blog-item news">
                        <!-- Blog Image-->
                        <div class="blog-media">
                            <div class="pic">
                            <a href="http://ea.caohejing.com" target="_blank">
                            <img class="lazy" data-original="/zxdoc/static/index/pic/blog/370x260/11.jpg" data-at2x="/zxdoc/static/index/pic/blog/370x260/11.jpg" alt>
                            </a>
                            </div>
                        </div>
                        <!-- title, author...-->
                        <div class="blog-item-data clearfix">
                            <h4 class="blog-title">上海漕河泾新兴技术开发区</h4>
                            <p class="post-info">上海漕河泾新兴技术开发区是国务院批准的全国首批14个国家级经济技术开发区之一</p>
                        </div>
                        <!-- Text Intro-->
                        <div class="blog-item-body">
                            <p>1991年又被批准为国家级高新技术产业开发区。开发区总规划面积14.3平方公里。</p>
                        </div>
                        <#--<a href="#" class="cws-button small alt">Read More</a>-->
                    </div>
                    <!-- ! Blog Post-->
                </div>
                <div class="col-md-4 col-sm-6">
                    <!-- ! Blog Post -->
                    <div class="blog-item news">
                        <!-- Blog Image-->
                        <div class="blog-media">
                            <div class="pic">
                            <a href="http://www.zizhupark.com" target="_blank">
                            <img class="lazy" data-original="/zxdoc/static/index/pic/blog/370x260/22.jpg" data-at2x="/zxdoc/static/index/pic/blog/370x260/22.jpg" alt>
                            </a>
                            </div>
                        </div>
                        <!-- title, author...-->
                        <div class="blog-item-data clearfix">
                            <h4 class="blog-title">上海紫竹高新技术产业开发区</h4>
                            <p class="post-info">上海紫竹高新技术产业开发区是上海市人民政府于2001年9月12日下发沪府[2001]第34号文批准建设</p>
                        </div>
                        <!-- Text Intro-->
                        <div class="blog-item-body">
                            <p>由闵行区人民政府、上海交通大学、紫江集团、上海联和投资有限公司等各方面共同筹划</p>
                        </div>
                        <#--<a href="#" class="cws-button small alt">Read More</a>-->
                    </div>
                    <!-- ! Blog Post -->
                </div>
                <div class="col-md-4 col-sm-6">
                    <!-- ! Blog Post image-->
                    <div class="blog-item news">
                        <!-- Blog Image-->
                        <div class="blog-media">
                            <div class="pic"><img class="lazy" data-original="/zxdoc/static/index/pic/blog/370x260/2.jpg" data-at2x="/zxdoc/static/index/pic/blog/370x260/2.jpg" alt><#--<span class="plus fa fa-plus"></span>--></div>
                        </div>
                        <!-- title, author...-->
                        <div class="blog-item-data clearfix">
                            <h4 class="blog-title">其他</h4>
                            <p class="post-info">其他</p>
                        </div>
                        <!-- Text Intro-->
                        <div class="blog-item-body">
                            <p>其他</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <!-- ! section news-->
    <!-- section parallax counter-->
    <section class="page-section cws_prlx_section pt-70 pb-70"><img src="/zxdoc/static/index/pic/counter-bg-1.jpg" alt class="cws_prlx_layer">
        <div class="container">
            <div class="row cws-multi-col">
                <div class="col-sm-3 col-xs-6 mb-sm-50">
                    <!-- counter block-->
                    <div class="counter-block style-2 st-color-2 full"><i class="counter-icon flaticon-people-2"></i>
                        <div class="counter-name-wrap">
                            <div data-count="${company!}" class="counter">0</div>
                            <div class="counter-name">注册企业</div>
                        </div>
                    </div>
                    <!-- ! counter block-->
                </div>
                <div class="col-sm-3 col-xs-6 mb-sm-50">
                    <!-- counter block-->
                    <div class="counter-block style-2 st-color-2 full"><i class="counter-icon flaticon-fashion"></i>
                        <div class="counter-name-wrap">
                            <div data-count="${server!}" class="counter">0</div>
                            <div class="counter-name">服务机构</div>
                        </div>
                    </div>
                    <!-- counter block-->
                </div>
                <div class="col-sm-3 col-xs-6">
                    <!-- counter block-->
                    <div class="counter-block style-2 st-color-2 full"><i class="counter-icon flaticon-game"></i>
                        <div class="counter-name-wrap">
                            <div data-count="${caseCounts!}" class="counter">0</div>
                            <div class="counter-name">完成项目</div>
                        </div>
                    </div>
                    <!-- ! counter block-->
                </div>
                <div class="col-sm-3 col-xs-6">
                    <!-- counter block-->
                    <div class="counter-block style-2 st-color-2 full"><i class="counter-icon flaticon-sheet"></i>
                        <div class="counter-name-wrap">
                            <div data-count="${library!}" class="counter">0</div>
                            <div class="counter-name">资料分享</div>
                        </div>
                    </div>
                    <!-- ! counter block-->
                </div>
            </div>
        </div>
    </section>
    <!-- ! section parallax counter-->
    <!-- section testimonials-->
    <section class="page-section pt-120 pb-70 pattern-1 relative">
        <div class="container">
            <div class="row mb-20">
                <div class="col-md-8 col-md-offset-2">
                    <!-- title section-->
                    <h2 class="title-section text-center mt-0 mb-0"  style="color: #1b1919;">用户评价</h2>
                    <!-- ! title section-->
                    <div class="cws_divider with-plus short-3 center mb-20 mt-10"></div>
                    <p class="text-center mb-30" style="color: #1b1919;">
                        用户的肯定，是资协网发展的动力！
                    </p>
                </div>
            </div>
            <div class="carousel-container">
                <div class="row">
                    <div class="owl-two-pag pagiation-carousel mb-20">
                        <!-- comment list section-->
                        <div class="widget_wrapper">
                            <div class="testimonial">
                                <div class="quote">
                                    高效的在线协作让我们信息披露更快捷、更准确
                                </div>
                                <div class="author"> <img src="/zxdoc/static/index/pic/blog/60x60/1.jpg" data-at2x="/zxdoc/static/index/pic/blog/60x60/1.jpg" alt>
                                    <figure>
                                        <figcaption>蒋婷<span>[ 财务主管 ]</span>
                                            <div class="stars"></div>
                                        </figcaption>
                                    </figure>
                                </div>
                            </div>
                        </div>
                        <!-- ! comment list section-->
                        <!-- comment list section-->
                        <div class="widget_wrapper">
                            <div class="testimonial">
                                <div class="quote">人性化的平台业务流程，便捷的即时沟通，提高了客户服务质量</div>
                                <div class="author"> <img src="/zxdoc/static/index/pic/blog/60x60/2.jpg" data-at2x="/zxdoc/static/index/pic/blog/60x60/2.jpg" alt>
                                    <figure>
                                        <figcaption>向前<span>[ 注册会计师 ]</span>
                                            <div class="stars"></div>
                                        </figcaption>
                                    </figure>
                                </div>
                            </div>
                        </div>
                        <!-- ! comment list section-->
                        <!-- comment list section-->
                    <#--<div class="widget_wrapper">
                        <div class="testimonial">
                            <div class="quote">Aenean tellus metus, bibendum sed, posuere ac, mattis non, nunc. Vestibur lum frin gilla pede sit amet augue. In turpis. Pellentesque posuere. Praesent turpis. Aenean posu ere, tortor sed cursus feugiat.</div>
                            <div class="author"> <img src="/zxdoc/static/index/pic/blog/60x60/1.jpg" data-at2x="/zxdoc/static/index/pic/blog/60x60/1.jpg" alt>
                                <figure>
                                    <figcaption>Juliana Doe<span>[ Business Manager ]</span>
                                        <div class="stars"></div>
                                    </figcaption>
                                </figure>
                            </div>
                        </div>
                    </div>
                    <!-- ! comment list section&ndash;&gt;
                    <!-- comment list section&ndash;&gt;
                    <div class="widget_wrapper">
                        <div class="testimonial">
                            <div class="quote">Aenean tellus metus, bibendum sed, posuere ac, mattis non, nunc. Vestibur lum frin gilla pede sit amet augue. In turpis. Pellentesque posuere. Praesent turpis. Aenean posu ere, tortor sed cursus feugiat.</div>
                            <div class="author"> <img src="/zxdoc/static/index/pic/blog/60x60/2.jpg" data-at2x="/zxdoc/static/index/pic/blog/60x60/2.jpg" alt>
                                <figure>
                                    <figcaption>Peter Doe<span>[ Web Designer ]</span>
                                        <div class="stars"></div>
                                    </figcaption>
                                </figure>
                            </div>
                        </div>
                    </div>-->
                        <!-- ! comment list section-->
                    </div>
                </div>
            </div>
        </div>
    </section>
    <!-- ! section testimonials-->
    <!---->
    <#--<section class="page-section pb-0">
        <div class="container">
            <!-- section title&ndash;&gt;
            <div class="row">
                <div class="col-md-8 col-md-offset-2">
                    <!-- title section&ndash;&gt;
                    <h2 class="title-section text-center mt-0 mb-0">联系我们</h2>
                    <!-- ! title section&ndash;&gt;
                    <div class="cws_divider with-plus short-3 center mb-20 mt-10"></div>
                    &lt;#&ndash;<p class="text-center mb-10">Curabitur at lacus ac velit ornare lobortis. Curabitur a felis in nunc fringilla tristique. Phasellus gravida semper nisi, ullam vel sem. Pellentesque libero tortor, tincidunt et, tincidunt eget, semper nec, quam.</p>&ndash;&gt;
                </div>
            </div>
            <div class="row">
                <!-- form section&ndash;&gt;
                &lt;#&ndash;<div class="col-md-8 mb-md-50">
                    <form action="php/contacts-process.php" method="post" class="form contact-form alt clearfix">
                        <div class="row">
                            <div class="col-md-6">
                                <textarea name="message" cols="40" rows="4" placeholder="Message" aria-invalid="false" aria-required="true" style="height: 248px"></textarea>
                            </div>
                            <div class="col-md-6">
                                <input type="text" name="name" value="" size="40" placeholder="Name" aria-invalid="false" aria-required="true" class="form-row form-row-first">
                                <input type="text" name="email" value="" size="40" placeholder="Email" aria-required="true" class="form-row form-row-last">
                                <input type="text" name="subject" value="" size="40" placeholder="Subject" aria-required="true" class="form-row form-row-last">
                                <input type="submit" value="Submit now" class="cws-button alt full-width">
                            </div>
                        </div>
                    </form>
                    <div id="feedback-form-errors" role="alert" class="alert alert-danger alt alert-dismissible fade in">
                        <button type="button" data-dismiss="alert" aria-label="Close" class="close"><span aria-hidden="true">×</span></button><i class="alert-icon border fa fa-exclamation-triangle"></i><strong>Error Message!</strong><br>
                        <div class="message"></div>
                    </div>
                    <div class="email_server_responce"></div>
                </div>&ndash;&gt;
                <!-- contact address&ndash;&gt;
                <div class="col-md-2"></div>
                <div class="col-md-8">
                    <address class="contact-address">
                        <div class="contact-icon-wrap"><a href="#" class="contact-icon flaticon-technology-2"> <span>021-64953617 </span></a></div>
                        <div class="contact-icon-wrap"><a href="#" class="contact-icon flaticon-note color-2"> <span>support@zimiwang.com </span></a></div>
                        <div class="contact-icon-wrap"><a href="#" class="contact-icon flaticon-placeholder"> <span>上海市徐汇区宜山路888号新银大厦2208室</span></a></div>
                        <div class="contact-icon-wrap"><a href="#" class="contact-icon flaticon-time color-2"> <span>周一 - 周日: 9:00 - 18:00</span></a></div>
                    </address>
                </div>
            </div>
        </div>
        &lt;#&ndash;<div class="map-full-width map-wrapper mt-60 border-t">
            <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d25295.930156304785!2d16.371063311644324!3d48.208404844730474!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x476d07986fcad78b%3A0x73f5a4d267cc4174!2zTmFnbGVyZ2Fzc2UgMTAsIDEwMTAgV2llbiwg0JDQstGB0YLRgNC40Y8!5e0!3m2!1sru!2sua!4v1453294615596" allowfullscreen=""></iframe>
        </div>&ndash;&gt;
    </section>-->
