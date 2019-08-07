<script type="text/javascript">
    var formData;
    $(function(){
        var sharedRoles = '${sharedRoles}';
        if(sharedRoles){
            var roles = sharedRoles.split(",");
            for(var i in roles){
                $("#" + roles[i]).prop("checked", "checked");
            }
        }
    });

    function saveCaseShareFile(){
        var sharedRoles = "";
        $("input:checked").each(function(){
            sharedRoles += $(this).val() + ",";
        })
        $.ajax({
            url: "saveCaseShareFile",
            type: "POST",
            dataType: "json",
            data:{fileId: '${parameters.fileId}',fileType: '${parameters.fileType}', moduleType: '${parameters.moduleType}', moduleId: '${parameters.moduleId?default('')}', sharedRoles: sharedRoles},
            success: function(data){
                showInfo("保存成功");
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
                <th>授权</th>
            </tr>
            </thead>
            <tbody>
            <#list roleTypeList as category>
            <tr>
                <td>
                ${category?index + 1}
                </td>
                <td>
                    <label for="${category.roleTypeId}">${category.description}(${category.groupName}) </label>
                </td>
                <td>
                    <input type="checkbox" name="shareFolder" id="${category.roleTypeId}" value="${category.roleTypeId}"/>
                </td>
            </tr>
            </#list>
            <#if roleTypeList?size == 0>
                <tr>
                    <td colspan="3" style="text-align: center">当前无参与方</td>
                </tr>
            </#if>
            </tbody>
        </table>
        </form>
<#if roleTypeList?size &gt; 0>
        <div class="form-group" align="center">
            <div class="margiv-top-10">
                <a href="javascript:saveCaseShareFile();" class="btn green"> 保存 </a>
            </div>
        </div>
</#if>
    </div>
</div>