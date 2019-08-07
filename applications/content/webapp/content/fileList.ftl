<script language="javascript">
    function saveFile(fileName,fileId,type){
        var folderType = unescapeHtmlText("${folderType}");
        if('folder' == type){
            $.ajax({
                type: 'post',
                url: "showFileList",
                data: {folderId:fileId,folderType:folderType,inputId:'${inputId}',fileId:'${fileId}',textId:'${textId}',hiddenId:'${hiddenId}'},
                async: true,
                dataType: 'html',
                success: function (content) {
                    $("#fileList").html(content);
                }
            });
        }else{
            $("#${inputId}").val(fileName);
            $("#${fileId}").val(fileId);
            closeCurrentTab2();
        }
    }

    function returnUpPage(path){
        var folderType = unescapeHtmlText("${folderType}");
        $.ajax({
            type: 'post',
            url: "showFileList",
            data: {folderPath:path,folderType:folderType,inputId:'${inputId}',fileId:'${fileId}',textId:'${textId}',hiddenId:'${hiddenId}'},
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#fileList").html(content);
            }
        });
    }
</script>
<table cellspacing="0" class="basic-table hover-bar" id="fileList">
    <tbody>
    <tr class="header-row-2">
        <td>
            <label>名称</label>
        </td>
        <td>
            <label>类型</label>
        </td>
    </tr>
    <#if data.returnList?has_content>
        <tr id="">
            <td colspan="2">
                <label><a href="#" onclick="returnUpPage('${data.returnList}')">返回上级</a></label>
            </td>
        </tr>
    </#if>
    <#list data.fileList as file>
        <tr <#if file.type == 'folder'> class="alternate-row" </#if> id="rowTr">
            <td>
                <a href="#" onclick="saveFile('${file.dataResourceName}','${file.dataResourceId}','${file.type}')" title="${file.dataResourceName}" class="smallSubmit">${file.dataResourceName}</a>
            </td>
            <td>
                <label>${file.typeName?default('')}</label>
            </td>
        </tr>
    </#list>
    </tbody>
</table>
