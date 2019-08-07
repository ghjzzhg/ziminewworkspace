<#include "component://common/webcommon/includes/uiWidgets/kindeditor.ftl"/>
<link href="/images/lib/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/images/lib/bootstrap/js/bootstrap.js"></script>
<script type="text/javascript">
    var s;
    $(function(){
        s = KindEditor.create('textarea[name="ticketContent"]');
    });
    function saveAdvert()
    {
        var c = s.html();
        $("#ticketContent").val(c)
        var options = {
            url: "saveAdvert",
            type: "post",
            dataType: "json",
            success: function(content) {
                showInfo("编辑成功");
                closeCurrentTab();
            }
        }
        $("#form1").ajaxSubmit(options);
    }
</script>
<div class="portlet light">
    <div class="portlet-title">
        <div class="caption font-red-sunglo">
            <i class="icon-settings font-red-sunglo"></i>
            <span class="caption-subject bold uppercase"> 广告设置 / 页头通栏广告</span>
        </div>
    </div>
    <div class="portlet-body">
    <#--<div class="note note-info">
        展现方式: 页头通栏广告显示于页面上方，通常使用 960x60 图片或 Flash 的形式。当前页面有多个页头通栏广告时，系统会随机选取其中之一显示。<br/>
        价值分析: 由于能够在页面打开的第一时间将广告内容展现于最醒目的位置，因此成为了网页中价位最高、最适合进行商业宣传或品牌推广的广告类型之一。
    </div>-->
        <div class="row">
            <form id="form1">
                <table class="table table-hover table-striped table-bordered">
                    <tbody>
                    <input type="hidden" name="AdvertId" value="${adver.AdvertId}"/>
                    <tr>
                        <td style="width: 100px"> <label class="control-label">广告标题<span class="required"> * </span></label></td>
                        <td>
                            <input type="text" name="ruleName" class="form-control" value="${adver.ruleName}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label class="control-label">投放范围<span class="required"> * </span></label>
                        </td>
                        <td>
                            <div>
                                <label><input type="checkbox" checked="checked" name="range" value="首页" onclick="return false;">首页</label>
                                <#--<label><input type="checkbox" name="range" value="工作台">工作台</label>
                                <label><input type="checkbox" name="range" value="文档协作">文档协作</label>-->
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label class="control-label">起始时间<span class="required"> * </span></label>
                        </td>
                        <td>
                            <input type="text" name="maxTimes" class="form-control" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})" value="${adver.maxTimes}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label class="control-label">终止时间<span class="required"> * </span></label>
                        </td>
                        <td>
                            <input type="text" name="score" class="form-control" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})" value="${adver.score}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label class="control-label">广告内容<span class="required"> * </span></label>
                        </td>
                        <td>
                            <div>
                                <textarea name="ticketContent" id="ticketContent" style="height: 300px; width: 100%" class="form-control" rows="20">${adver.description!}</textarea>
                            </div>
                        </td>
                    </tr>
                    <#-- <tr>
                         <td>
                             <label class="control-label">额外设置</label>
                         </td>
                         <td>
                             根据展现方式确定更多的设置
                         </td>
                     </tr>-->
                    </tbody>

                </table>

                <div class="form-group" align="center">
                    <div class="margiv-top-10">
                        <a onclick="saveAdvert()" class="btn green"> 提交 </a>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>