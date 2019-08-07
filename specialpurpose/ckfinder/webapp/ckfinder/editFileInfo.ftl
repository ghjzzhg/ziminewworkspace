<#if fileFlag =="add">
<script type="application/javascript">
    function saveNewFilename(){
        var folderName = $("#folderName").val();
        var foldeRemarks = $("#foldeRemarks").val();
        if(folderName.trim() == ""){
            showError("请输入文件夹名称");
            return;
        }
        if(/[\\*"|:<>@#\$%\^&\：\*\?]+/g.test(folderName))
        {
            showError("文件名不能包含特殊字符");
            return;
        }
        if(/[\\*"|:<>@#\$%\^&\：\*\?]+/g.test(foldeRemarks))
        {
            showError("文件备注不能包含特殊字符");
            return;
        }
        closeCurrentTab(folderName + "|" + foldeRemarks);
    }
</script>
<#--提示框-->
<div id="addFolder" title="新建文件夹">
    <fieldset>
        <label for="name">名称：</label>
        <input type="text" name="folderName" id="folderName" maxlength="20" class="form-control">
        </br>
        <label for="name">备注：</label>
        <input type="text" name="foldeRemarks" id="foldeRemarks" maxlength="50"  class="form-control">
    </fieldset>
</div>
<#elseif fileFlag =="edit">
<script type="application/javascript">
    function saveNewFilename(){
        var fileName = $("#folderName").val();
        if(fileName.trim() == ""){
            showError("请输入名称");
            return;
        }
        if(/[\\*"|:<>@#\$%\^&\：\*\?]+/g.test(fileName))
        {
            showError("文件不能包含特殊字符");
            return;
        }
        <#if fileType != "file">
            var newfoldeRemark = $("#newfoldeRemark").val();
            if(/[\\*"|:<>@#\$%\^&\：\*\?]+/g.test(newfoldeRemark))
            {
                showError("文件备注不能包含特殊字符");
                return;
            }
            fileName = fileName + "|" + newfoldeRemark;
        </#if>
        closeCurrentTab(fileName);
    }
</script>
<div id="editFile" title="重命名">
    <fieldset>

        <label for="name">请输入新名称：</label>
        <table style="width: 100%">
            <tr>
                <td style="width: 80%">
                    <input type="text" name="folderName" id="folderName" maxlength="20" value="${fileName?default("")}"  class="form-control">
                </td>
                <td style="width:20%">
                    <#if fileType == "file">
                        <label id="fileSuffix">${fileSuffix?default("")}</label>
                    </#if>
                </td>
            </tr>
        </table>
        <#if fileType != "file">
        <label for="name">请输入新备注：</label>
        <table style="width: 100%">
            <tr>
                <td style="width: 80%">
                    <input type="text" name="newfoldeRemark" id="newfoldeRemark" maxlength="20" value="${folderRemarks?default("")}"  class="form-control">
                </td>
            </tr>
        </table>
        </#if>
    </fieldset>
</div>
</#if>
<div class="form-group col-md-12" style="text-align: center;margin-top: 20px">
    <div class="margiv-top-10">
        <a href="javascript:saveNewFilename()" class="btn green"> 保存 </a>
    </div>
</div>