<script type="text/javascript">
    function saveCaseShareFile(){
        var sharedPartyIds = "";
        $("input:checked").each(function(){
            sharedPartyIds += $(this).val() + ",";
        })
        $.ajax({
            url: "saveShareFilePrivate",
            type: "POST",
            dataType: "json",
            data:{fileId: '${parameters.fileId}', sharedPartyIds: sharedPartyIds.substring(0,sharedPartyIds.length-1)},
            success: function(data){
                showInfo("分享成功！");
                closeCurrentTab();
            }
        })
    }
</script>
<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">分享设置</span>
        </div>
    </div>
    <div class="portlet-body">
        <form id="templateFoldersForm" style="overflow-y: auto;overflow-x: hidden;">
        <table class="table table-striped table-bordered table-hover order-column">
            <thead>
            <tr>
                <th>序号</th>
                <th>机构名</th>
                <th>名称</th>
            </tr>
            </thead>
            <tbody>
            <#list dataList as data>
            <tr>
                <td>
                ${data?index + 1}
                </td>
                <td>
                    <label>${data.groupName}</label>
                </td>
                <td>
                    <label>${data.fullName}</label>
                </td>
                <td>
                    <input type="checkbox" name="shareFolder" id="${data.partyId}" value="${data.partyId}"/>
                </td>
            </tr>
            </#list>
            <#if dataList?size == 0>
                <tr>
                    <td colspan="3" style="text-align: center">当前无子账户</td>
                </tr>
            </#if>
            </tbody>
        </table>
        </form>
        <div class="form-group" align="center">
            <div class="margiv-top-10">
                <a href="javascript:saveCaseShareFile();" class="btn green"> 分享 </a>
            </div>
        </div>
    </div>
</div>