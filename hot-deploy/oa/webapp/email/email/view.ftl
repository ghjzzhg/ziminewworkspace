
<script type="text/javascript">
    $(function(){
        mailContent = KindEditor.create('textarea[name="content"]', {
            allowFileManager : false,
            items: [],
            resizeType: 1
        });
        mailContent.readonly(true);
        var autoheight=mailContent.edit.doc.body.scrollHeight;
        mailContent.edit.setHeight(autoheight);
    })
</script>
<style type="text/css">
    .ke-toolbar{
        background: transparent;
        border: 0;
    }
    .ke-container{
        border:0;
    }
</style>
<div>
    <select name="menu1" class="drpdwn" style="width:130px;">
        <option>移动到...</option>
        <option value="1">发件箱</option>
        <option value="2">草稿箱</option>
        <option value="3">垃圾箱</option>
        <option value="4">自定义</option>
    </select>
    <select name="menu1" class="drpdwn" style="width:130px;">
        <option>回复...</option>
        <option value="1">回复发件人(附带原文)</option>
        <option value="2">回复发件人(不带原文)</option>
        <option value="3">回复所有人(附带原文)</option>
        <option value="4">回复所有人(不带原文)</option>
    </select>
    <a href="#" class="smallSubmit" onclick="showInfo('转发')">转发</a>
    <a href="#" class="smallSubmit" onclick="showInfo('删除')">删除</a>
    <a href="#" class="smallSubmit" onclick="showInfo('彻底删除')">彻底删除</a>
    <a href="#" class="smallSubmit" onclick="showInfo('拒收')">拒收</a>
    <a href="#" class="smallSubmit" onclick="$.email.returnView('${data.type?default('')}')">返回</a>
</div>
<div>
    <table class="basic-table" style="width: 100%">
        <tr>
            <td class="label">邮件日期</td>
            <td colspan="2">${data.datetimeStarted?default("")}</td>
        </tr>
        <tr>
            <td class="label">发件人</td>
            <td><a href="#" onclick="showInfo('添加到通讯录')">${data.fromString?default("")}</a></td>
        </tr>
        <tr>
            <td class="label">收件人</td>
            <td colspan="2"><a href="#" onclick="showInfo('添加到通讯录')">${data.receive?default("")}</a>
        </tr>
        <tr>
            <td class="label">抄送</td>
            <td colspan="2"><a href="#" onclick="showInfo('添加到通讯录')">${data.ccString?default("")}</a></td>
        </tr>
        <tr>
            <td class="label">主题</td>
            <td>${data.subject?default("")}</td>
        </tr>
        <tr>
            <td class="label">附件</td>
            <td>
                <#if data.fileList?has_content>
                    <#list data.fileList as line>
                    <a href="#" onclick="$.email.downloadFile(${line.contentId?default('')})">${line.fileName?default('')}</a>&nbsp;&nbsp;
                    </#list>
                </#if>
        </tr>
        <tr>
            <td colspan="2">
                <textarea name="content" style="width: 100%; height: 300px">
                    <pre>
                        ${data.content?default("")}
                        </pre>
                </textarea>
            </td>
        </tr>
    </table>
</div>