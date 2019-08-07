<#macro showFileList fileList  id="fileName" hiddenId="fileId" fileUploadId="abc" uploadFlag=true>
<#--id表示输入框id-->
<#--hiddenId-->
<script language="javascript">
    function uploadFileList(){
        displayInTab3("showUploadFileViewTab", "文件上传", {requestUrl: "showUploadFileView",data:{textId:'${id}',hiddenId:'${hiddenId}',fileUploadId:'${fileUploadId}'},width: "550px",height:300,position:"center"});
    }
    $("a.attachment-image").fancybox({
        onClosed: closeFanyBox,
        'overlayShow':false,
        'transitionIn':'elastic',
        'transitionOut':'elastic'
    });

    function closeFanyBox(){
        $(this) && $(this).remove();
    }
    function remove_upload(id,delflag){
        if(confirm("是否确认删除？")){
            var filelength = 0;
            <#if fileList?has_content>
                filelength = '${fileList?size}';
            </#if>
            if(null != filelength && filelength > 0){
                delFileView(id);
            }else{
                $.ajax({
                    type:'POST',
                    url: 'deleteUplodeFile',
                    data:{'fileId' : id},
                    asyn:true,
                    dataType:'json',
                    success:function () {
                        delFileView(id);
                    }
                });
            }
        }
    }

    function delFileView(id){
        var fileId = $("#${hiddenId}").val();
        var fileIds = fileId.split(",");
        var fileList = "";
        for(var i = 0;i < fileIds.length; i++){
            if(id != fileIds[i]){
                fileList = fileList + fileIds[i] + ","
            }
        }
        fileList = fileList.substr(0,fileList.length-1);
        $("#${hiddenId}").val(fileList);
        $("#file_id_"+id).remove();
    }

</script>
<div>

    <div id="${id}">
    <#if fileList?has_content>
        <#list fileList as file>
        <span id="file_id_${file.dataResourceId}" style="background-color: yellow;border: 1px solid #a5d24a;margin-right: 5px;padding: 5px;float: left;background: #cde69c">
            <#if file.type == "jpg" || file.type == "png" || file.type == "jpeg" || file.type == "gif" || file.type == "bmp" || file.type == "pdf">
                <a class="attachment-image" title="${file.dataResourceName}" style="color: #55a3e5;margin-left:5px" target="_blank" href="${file.objectInfo}" id="file_${file.dataResourceId}">${file.dataResourceName}</a>
            </#if>
            <#if file.type != "jpg" && file.type != "png" && file.type != "jpeg" && file.type != "gif" && file.type != "bmp" && file.type != "pdf">
                <a class="attachment-image" title="${file.dataResourceName}" style="color: #55a3e5;margin-left:5px" href="javascript:downloadServerAttachment('${file.dataResourceId}')" id="file_${file.dataResourceId}">${file.dataResourceName}</a>
            </#if>
            <#if uploadFlag>
                <span class="tipl icon-remove 10159" onclick="remove_upload('${file.dataResourceId}')" title="删除"></span>
            </#if>
        </span>
        </#list>
    </#if>
    </div>
    <#if uploadFlag>
        <a href="#" onclick="uploadFileList()" class="smallSubmit">选择</a>
        <input type="hidden" id="${hiddenId}" value="" name="${hiddenId}">
    </#if>
</div>
</#macro>