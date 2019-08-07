<html><head>
    <title>HTML编辑器-文件上传</title>
    <meta http-equiv="Content-Type" content="text/html; charset=gb2312">

    <style type="text/css">
        <!--
        td {  font-size: 9pt}
        -->
    </style>
    <script language="javascript">
        function showFileList(folderType,id,fileId){
            displayInTab3("fileListTab", "文件上传", {requestUrl: "fileList",data:{folderType:folderType,inputId:id,fileId:fileId,textId:'${textId}',hiddenId:'${hiddenId}'},width: "400px",height:500,position:"center"});
        }
        function saveId(){
            var fileName = "";
            var fileId = "";
            var flag = false;
            if($("#oneselfRadio").attr("checked")=="checked"){
                if(null != $("#oneselfFileId").val() && $("#oneselfFileId").val() != ""){
                    fileName = $("#oneself").val();
                    fileId = $("#oneselfFileId").val();
                    flag = true;
                }else{
                    flag = false
                }
            }else if($("#teamRadio").attr("checked")=="checked"){
                if(null != $("#teamworkFileId").val() && $("#teamworkFileId").val() != ""){
                    fileName = $("#teamwork").val();
                    fileId = $("#teamworkFileId").val();
                    flag = true;
                }else{
                    flag = false
                }
            }else{
                var fileId = '${fileUploadId}';
                if(null != $("#attachment_"+fileId+"_value").val() && $("#attachment_"+fileId+"_value").val() != ""){
                    fileName = $("#attachment_name_"+fileId+"_value").val();
                    fileId = $("#attachment_"+fileId+"_value").val();
                    flag = true;
                }else{
                    flag = false
                }
            }
            if(flag){
                var fileIds = fileId.split(",");
                for(var i = 0; i < fileIds.length; i ++){
                    $.ajax({
                        type: 'post',
                        url: "searchDataResourceById",
                        async: true,
                        data:{fileId:fileIds[i]},
                        dataType: "json",
                        success: function (content) {
                            var dataName = content.data.dataResourceName;
                            var type = dataName.substr(dataName.lastIndexOf(".") + 1 , dataName.length);
                            if( type == "jpg" || type == "png" || type == "jpeg" || type == "gif" || type == "bmp" || type == "pdf"){
                                $("#${textId}").append('<span id="file_id_' + content.data.dataResourceId + '" style="background-color: yellow;border: 1px solid #a5d24a;margin-right: 5px;padding: 5px;float: left;background: #cde69c"><a class="attachment-image" title="' + content.data.dataResourceName + '" style="color: #55a3e5;margin-left:5px" target="_blank" href="' + content.data.objectInfo + '">' + content.data.dataResourceName + '</a><span class="tipl icon-remove 10159" onclick="remove_upload(' + content.data.dataResourceId + ')" title="删除"></span></span>');
                            }else{
                                $("#${textId}").append('<span id="file_id_' + content.data.dataResourceId + '" style="background-color: yellow;border: 1px solid #a5d24a;margin-right: 5px;padding: 5px;float: left;background: #cde69c"><a class="attachment-image" title="' + content.data.dataResourceName + '" style="color: #55a3e5;margin-left:5px" href="javascript:downloadServerAttachment(' + "'" + content.data.dataResourceId + "'" + ')">' + content.data.dataResourceName + '</a><span class="tipl icon-remove 10159" onclick="remove_upload(' + content.data.dataResourceId + ')" title="删除"></span></span>');
                            }
                        }
                    });
                }
                var hifileId = $("#${hiddenId}").val();
                if(null != hifileId && hifileId != ""){
                    $("#${hiddenId}").val(hifileId + ',' + fileId);
                }else{
                    $("#${hiddenId}").val(fileId);
                }
                closeCurrentTab2();
            }else{
                closeCurrentTab2();
            }
        }
    </script>
</head>
<body onkeydown="javascript:if(event.keyCode == 8){press_backspace();}" bgcolor="#ffffff" leftmargin="0" topmargin="0" rightmargin="0" bottommargin="0" marginwidth="0" marginheight="0" scroll="no" style="OVERFLOW-Y: hidden; OVERFLOW-X: hidden;" oncontextmenu="return true"><br>
<table width="94%" border="1" cellspacing="0" cellpadding="5" align="center" bordercolordark="#CCCCCC" bordercolorlight="#000000" style="border-collapse: collapse;" id="adv0">
    <tbody>
    <tr align="left" valign="middle" bgcolor="#DEDEDE">
        <td bgcolor="#DEDEDE" height="58">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tbody><tr>
                    <td colspan="2"><strong>文件来源：</strong></td>
                    <td width="75%">&nbsp;</td>
                </tr>
                <tr>
                    <td width="5%"><input type="radio" id="oneselfRadio" name="radiobutton" value="0" onclick="javascript:document.getElementById('oneself').disabled=false;document.getElementById('file_Submit3').disabled=false;document.getElementById('teamwork').disabled=true;document.getElementById('file_Submit4').disabled=true;document.getElementById('Submit_file').style.display='none';" checked="checked"></td>
                    <td width="20%" nowrap="nowrap">个人文档：</td>
                    <td><input type="text" name="oneself" id="oneself" size="40" style="width:200" value="个人文档://" readonly="">
                        <input type="button" name="Submit3" id="file_Submit3" style="width:73" value="浏览..." onclick="javascript:showFileList('个人文档','oneself','oneselfFileId');">
                        <input type="hidden" name="oneselfFileId" id="oneselfFileId" value="">
                </tr>
                <tr>
                    <td width="5%"><input type="radio" id="teamRadio" name="radiobutton" value="0" onclick="javascript:document.getElementById('oneself').disabled=true;document.getElementById('file_Submit3').disabled=true;document.getElementById('teamwork').disabled=false;document.getElementById('file_Submit4').disabled=false;document.getElementById('Submit_file').style.display='none';"></td>
                    <td width="20%" nowrap="nowrap">协作空间：</td>
                    <td><input type="text" name="teamwork" id="teamwork" size="40" style="width:200" value="协作空间://" readonly="" disabled="disabled">
                        <input type="button" name="Submit4" id="file_Submit4" style="width:73" value="浏览..." onclick="javascript:showFileList('协作空间','teamwork','teamworkFileId');" disabled="disabled">
                        <input type="hidden" name="teamworkFileId" id="teamworkFileId">
                </tr>
                <tr>
                    <td><input type="radio" name="radiobutton" id="fileRadio" value="1" onclick="javascript:document.getElementById('oneself').disabled=true;document.getElementById('file_Submit3').disabled=true;document.getElementById('teamwork').disabled=true;document.getElementById('file_Submit4').disabled=true;document.getElementById('Submit_file').style.display='';"></td>
                    <td>我要立即上传：</td>
                    <td>
                    </td>
                </tr>
                <tr id="Submit_file" style="display:none">
                    <td colspan="3">
                    <#include "component://content/webapp/content/uploadFileList.ftl"/>
                    <@fileUpload id="${fileUploadId}" name="fileListH" onComplete="fileRetruen" showLink=true readonly=false showTip=false simple="上传" download=true/>
                    </td>
                </tr>
                </tbody></table>
        </td>
    </tr>
    </tbody></table>

<div align="center">
    <a href="#" onclick="saveId()" class="smallSubmit">确认</a>
</div>
</body></html>