

<#include "component://content/webapp/content/showUploadFileView.ftl"/>
<script type="text/javascript">
    var mailContent;
    $(function(){
        mailContent = KindEditor.create('textarea[name="content"]', {
            allowFileManager : true
        });

       /* var autoheight=mailContent.edit.doc.body.scrollHeight;
        mailContent.edit.setHeight(autoheight);

        $(".mailReceiver").tagsInput({'width': '100%',
            'height': 'auto',
            'delimiter': ';',
            'defaultText':'',
            autocomplete_url: "AutoGetEmailAddress",
            autocomplete: {minLength: 2, selectFirst: true, width: '100px', autoFill: true}});
        $(".mailReceiver").importTags('');
        attachmentVar = $(".attachmentTags");
        attachmentVar.tagsInput({'width': '100%',
            'height': 'auto',
            'defaultText':''});

        new qq.FineUploader({
            element: document.getElementById('mailAttachementUpload'),
            request: {
                endpoint: '/content/control/FineUploader',
                forceMultipart:true,
                customHeaders:{'Accept' : 'application/json, text/javascript, *!/!*; q=0.01'},
                params:{'CSRFToken' : getCSRFToken()}                 },
            validation: {
                sizeLimit: 104857600 // 50 kB = 50 * 1024 bytes
            },
            text: {
                uploadButton: '上传',
                cancelButton: '取消',
                failUpload:'上传失败'
            },
            failedUploadTextDisplay:{
                mode:'none'
            },
            callbacks: {
                onComplete: function(id, fileName, responseJSON) {
                    if(responseJSON.status){
                        showInfo("上传成功");
                        attachmentVar.addTag(responseJSON.data.name);
                    }else{
                        showError("上传失败,请重试");
                    }
                }
            },
            messages: {
                typeError: "不支持的文件格式，仅支持 'jpg', 'jpeg', 'png', 'gif'"
            },
            showMessage: function(message) {
                showInfo(message);
            }
        });*/
    })
</script>
<style type="text/css">
    .file .tagsinput{
        border: 0;
        width: 90%!important;
        display:inline-block;
        vertical-align: bottom;
    }
    .file .tagsinput input{
        display: none;
    }
</style>
<div>
    <form method="post" action="" id="sendEmail" class="basic-form" name="sendEmail">
    <table class="basic-table" style="width: 100%">
        <tr>
            <td class="label"><span onclick="$.email.chooseContacts('address')" style="color: #2a586f; cursor: pointer">收件人</span></td>
            <td><input type="text" style="width:100%" name="toString"></td>
        </tr>
        <tr>
            <td class="label"><span onclick="$.email.chooseContacts('ccAddress')" style="color: #2a586f; cursor: pointer">抄送</span></td>
            <td><input  type="text" style="width:100%" name="ccString"></td>
        </tr>
        <tr>
            <td class="label"><span onclick="$.email.chooseContacts('bccAddress')" style="color: #2a586f; cursor: pointer">密送</span></td>
            <td><input  type="text" style="width:100%" name="bccString"></td>
        </tr>
        <tr>
            <td class="label">主题</td>
            <td><input type="text" name="subject" style="width:100%"></td>
        </tr>
        <tr>
            <td class="label">附件</td>
            <td>
                <div>
                    <@showFileList id="emailFile" hiddenId="fileId" fileList=""></@showFileList>
                </div>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <textarea name="content" style="width: 100%; height: 300px"></textarea>
            </td>
        </tr>
    </table>
        </form>
</div>