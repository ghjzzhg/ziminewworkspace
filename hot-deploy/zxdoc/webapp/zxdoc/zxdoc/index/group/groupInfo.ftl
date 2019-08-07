<#--<link REL="stylesheet" HREF="/zxdoc/static/companyInfo/css/style.css" TYPE="text/css">-->
<script type="text/javascript" src="/zxdoc/static/index/js/jquery.min.js"></script>
<#--<script type="text/javascript" src="/zxdoc/static/companyInfo/js/jquery.js"></script>
<script type="text/javascript" src="/zxdoc/static/companyInfo/js/script.js"></script>-->
<#--<link href="/images/lib/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css"/>-->
<link href="/metronic-web/component/components.min2.css" rel="stylesheet" type="text/css"/>
<#--<script type="text/javascript" src="/images/lib/bootstrap/js/bootstrap.js"></script>-->
<style type="text/css">
</style>
<script type="text/javascript">
    $(function(){
        /*$(".content-body").height($(window).height()).css("background", "transparent");
        $(".content-body").backstretch([
                    "/metronic-web/images/login-bg/1.jpg",
                    "/metronic-web/images/login-bg/2.jpg",
                    "/metronic-web/images/login-bg/3.jpg",
                    "/metronic-web/images/login-bg/4.jpg"
                ], {
                    fade: 1000,
                    duration: 8000
                }
        );*/
        var minHeight = $(window).height() - 160;
        if($("#company").height() < minHeight){
            $("#company").height(minHeight);
        }
        $("#company").css("background", "transparent").backstretch([
                    "/metronic-web/images/login-bg/1.jpg",
                    "/metronic-web/images/login-bg/2.jpg",
                    "/metronic-web/images/login-bg/3.jpg",
                    "/metronic-web/images/login-bg/4.jpg"
                ], {
                    fade: 1000,
                    duration: 8000
                }
        );
    })
</script>
<div id="company">
<section class="page-section pt-80 pb-0">
    <div class="container">
        <div class="row pb-100">

            <div class="col-md-12 col-sm-12" style="color: black">
                <div class="portlet blue-crusta box">
                    <div class="portlet-title" style="background-color: #00aba5;line-height: 35px;">
                        <div class="caption" style="margin-left: 5px;">
                            <i class="fa fa-cogs"></i>${partyGroup.groupName!} </div>
                        <div class="actions">
                        </div>
                    </div>
                    <div class="portlet-body" style="border: #00aba5 solid 1px;background-color: #FFFFFF">
                        <div class="row static-info">
                            <div class="col-md-5 name" style="margin-left: 5px;"> 类型： </div>
                            <div class="col-md-6 value">
                                <span class="label label-info label-sm"> ${partyGroup.description?default('')} </span>
                            </div>
                        </div>
                        <div class="row static-info">
                            <div class="col-md-5 name" style="margin-left: 5px;">注册时间： </div>
                            <div class="col-md-6 value">
                                <span class="label label-info label-sm"> ${partyGroup.createdStamp?string("yyyy-MM-dd HH:mm:ss")} </span>
                            </div>
                        </div>
                        <div class="row static-info">
                            <div class="col-md-5 name" style="margin-left: 5px;"> 联系人： </div>
                            <div class="col-md-6 value">
                                <span class="label label-info label-sm"> ${partyGroup.fullName?default('')} </span>
                            </div>
                        </div>
                        <div class="row static-info">
                            <div class="col-md-5 name" style="margin-left: 5px;"> 联系电话： </div>
                            <div class="col-md-6 value">
                                <span class="label label-info label-sm"> ${partyGroup.contactNumber?default('')} </span>
                            </div>
                        </div>
                        <div class="row static-info">
                            <div class="col-md-5 name" style="margin-left: 5px;"> 邮箱： </div>
                            <div class="col-md-6 value">
                                <span class="label label-info label-sm"> ${partyGroup.email?default('')} </span>
                            </div>
                        </div>
                        <div class="row static-info">
                            <div class="col-md-5 name" style="margin-left: 5px;"> 地址： </div>
                            <div class="col-md-6 value">
                                <span class="label label-info label-sm"> ${partyGroup.area?default("")} ${partyGroup.address1?default('')} </span>
                            </div>
                        </div>
                        <div class="row static-info">
                            <div class="col-md-5 name" style="margin-left: 5px;"> 官网： </div>
                            <div class="col-md-6 value">
                            <#if partyGroup.infoString?has_content><span class="label label-info label-sm"> <a
                                        href="${partyGroup.infoString}">${partyGroup.infoString}</a> </span></#if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
                </div>
    </div>
</section>
</div>