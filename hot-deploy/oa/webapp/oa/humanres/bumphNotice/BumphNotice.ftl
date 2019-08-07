<script type="text/javascript">
    var template;
    $(function () {
        template = KindEditor.create('textarea[name="noticeFeedback"]', {
            allowFileManager: true
        });
        $("#feedbackSubmit").click(function(){
            console.log(template.html());
            if(template.html() == null || template.html() == ""){
                showInfo("请填写反馈后再提交！");
                return;
            }
            $.ajax({
                type: 'post',
                url: "saveNoticeFeedback",
                async: true,
                dataType: 'json',
                data:{noticeId:'${noticeId}',feedbackContext:template.html()},
                success: function (data) {
                    $.ajax({
                        type: 'POST',
                        url: "getFeedback",
                        async: true,
                        dataType: 'html',
                        data:{feedbackMiddleId:'${noticeId}',sortField:'-feedbackTime',feedbackPerson:'',childFeedbackId:'',feedbackMiddleType:'TblNotice'},
                        success: function (data) {
                            $("#feedbackList").html(data);
                        }
                    });
                    KindEditor.remove('textarea[name="noticeFeedback"]');
                    $('textarea[name="noticeFeedback"]').val("");
                    template = KindEditor.create('textarea[name="noticeFeedback"]', {
                        allowFileManager: true
                    });
                    showInfo(data.msg);
                }
            });
        });
    });
</script>
<style type="text/css">
    .text-sm{
        letter-spacing: 2px;
        font-size: 10px;
        font-weight: 100;
    }
    .text-lg{
        letter-spacing: 2px;
        font-size: 10px;
        font-weight: 300;
    }
    .text-md{
        letter-spacing: 2px;
        font-size: 10px;
        font-weight: 200;
    }
</style>
<#include "component://oa/webapp/oa/humanres/workPlan/feedbackListCommonCopy.ftl"/>
<#assign scopeData = dispatcher.runSync("getDataScope", Static["org.ofbiz.base.util.UtilMisc"].toMap("entityName", "TblNotice", "dataId", "${noticeId}", "dataAttr", "", "userLogin", userLogin))/>
<#if scopeData?has_content>
    <#assign description=scopeData.description?default("")/>
</#if>
<div>
    <form name="" id="" class="basic-form">
    <table align="center" cellpadding="0" cellspacing="0" border="0" width="100%" height="50px">
        <tbody>
        <tr>
            <td valign="top"><img
                    src="/images/rextec_logo.png" width="140px" height="45px" alt="瑞克斯信息科技有限公司">
            </td>
        </tr>
        </tbody>
    </table>
    <div style="border-bottom:1px solid;text-align: center; letter-spacing: 20px; margin:0 auto; font-weight: bold;font-size: 3em;margin-bottom: 0.5em;">
        <div>
            <span class="h3" style="width: 100%">${noticeInfo.noticeHeadDesc?default("")}</span>
        </div>
        <span class="text-md"><span class="tooltip">RECTEC</span>${noticeInfo.noticeYear?default('')}<span class="tooltip">CWB</span><span class="tooltip">第</span>${noticeInfo.noticeNumber?default('')}<span class="tooltip">号</span></span>
        <div>
            <span class="text-sm"><b style="font-size: 12px">[发起时间：${noticeInfo.releaseTime?default('')}]</b><b style="font-size: 12px">[发起部门：${noticeInfo.groupName?default('')}]</b></span>
        </div>
    </div>

    <div style="text-align: center; letter-spacing: 20px; margin:0 auto; overflow:auto;  font-weight: bold;font-size: 3em;border-bottom: 1px solid;height: 300px">
        <div class="h3">${noticeInfo.title}</div>
        <div id="notice_content_display" style="font-size: 16px;font-weight: 100;letter-spacing: 2px;child-align:left;text-align: left;">
        </div>
        <script>
            $("#notice_content_display").append(unescapeHtmlText('${noticeInfo.content?default('')}'));
        </script>
    </div>

    <div style="border-bottom: 1px;font-size: 16px;font-weight: 100;letter-spacing: 2px">
        <div>
            [发布范围:
        <#if description?has_content>
            <#list description as desc>
                <span>${desc.name}
                    <#if desc.like?has_content>
                        <i style="color: lightskyblue">${desc.like}</i>
                    </#if>
                                                </span>
            </#list>
        </#if>]
        </div>
        <div align="center">
            <a href="#" class="smallSubmit" onclick="">打印</a>
            <a href="#" class="smallSubmit" onclick="">导出</a>
            <a href="#" class="smallSubmit" onclick="">浏览日志</a>
        </div>
    </div>
    <div style="text-align: center;margin-top: 10px;margin-bottom: 10px">
        说明：导为Word文件时图片无法导出，请在本页面中的图片上点击鼠标右键，选择“复制”，然后粘贴到Word文件中的相应位置。
    </div>
    <div>
        <#if noticeInfo.hasFeedback?has_content&&noticeInfo.hasFeedback='Y'>
            <table width="100%">
                <tbody>
                <tr>
                    <td align="center">
                        <span style="font-size: 20px">文档反馈</span>
                        <p class="tooltip">本文档已经设置为允许反馈，你可以在下面框中录入反馈内容；本次反馈人：测试员</p>
                    </td>
                </tr>
                <tr>
                    <td id="feedbackList">
                        <@feedbackList  feedbackMiddleId='${noticeId}' feedbackPerson='' childFeedbackId="" value='' type="TblNotice" targetId='feedbackList'></@feedbackList>
                    </td>
                </tr>
                <tr>
                    <td>
                        <textarea name="noticeFeedback" style="width: 100%"></textarea>
                    </td>
                </tr>
                <tr>
                    <td align="center">
                        <a class="smallSubmit" id="feedbackSubmit">反馈</a>
                    </td>
                </tr>
                </tbody>
            </table>
        </#if>
    </div>
    </form>
</div>