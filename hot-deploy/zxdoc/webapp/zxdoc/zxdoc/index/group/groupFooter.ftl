<script type="text/javascript" src="/zxdoc/static/index/js/jquery.min.js"></script>
<style type="text/css">
    table.altrowstable {
        font-family: verdana, arial, sans-serif;
        font-size: 11px;
        color: #333333;
        border-width: 1px;
        border-color: #a9c6c9;
        border-collapse: collapse;
    }

    table.altrowstable th {
        border-width: 1px;
        padding: 8px;
        border-style: solid;
        border-color: #a9c6c9;
    }

    table.altrowstable td {
        border-width: 1px;
        padding: 8px;
        border-style: solid;
        border-color: #a9c6c9;
    }
</style>
<script type="application/javascript">
    $(function(){
        /*$(".content-body").height($(window).height() - 160).css("background", "transparent");
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
        if($("#business").height() < minHeight){
            $("#business").height(minHeight);
        }
        $("#business").css("background", "transparent").backstretch([
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
<div id="business">
<section class="page-section pt-80 pb-0" style="width: 100%">
    <div class="container">
        <div class="row pb-100">
            <div class="col-md-8 col-md-offset-2 mb-20">
                <div id="groupInfo"  align="center" style="overflow: auto; height:750px">
                    <#include "component://zxdoc/webapp/zxdoc/zxdoc/index/group/security/${parameters.infoId}.ftl">
                </div>
            </div>
        </div>
    </div>
</section>
</div>