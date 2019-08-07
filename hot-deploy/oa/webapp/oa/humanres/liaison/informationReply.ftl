<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#macro informationReply liaisonMap mainPerson='' copyPerson='' >
    <script language="javascript">
        var content1;
        $(function () {
            content1 = KindEditor.create('textarea[name="content1"]', {
                allowFileManager: true
            });
        });
        $("#replyInformation_liaison").click(function () {
            var content = content1.html();
            $.liaison.replyInformation('${liaisonMap.contactListId}',content);
        });
    </script>

    <div>
        <div style="border: 1px solid #ccc;">
            <table class="basic-form">
                <tbody>
                <tr>
                    <td class="label">接收人:</td>
                    <td>

                        <table class="basic-form">
                            <tr>
                                <td class="label">主送:</td>
                                <td>${mainPerson}</td>
                            </tr>
                            <tr>
                                <td class="label">抄送:</td>
                                <td>${copyPerson}</td>
                            </tr>
                            <tr>
                                <td class="label">转发:</td>
                                <td width="300px" >
                                    <@selectStaff id="aaaa" name="aaaa" value="" multiple=true/>
                                </td>
                            </tr>
                        </table>

                    </td>
                </tr>
                </tbody>
                <tr>
                    <td class="label">回复意见:</td>
                    <td>
                        <textarea name="content1" id="content1" style="width:100%;"></textarea>
                    </td>
                </tr>
                <tr>
                    <td class="label">邮件通知:</td>
                    <td>
                        <input type="checkbox" id="a" name="a">
                        <label>邮件通知主送人</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <input type="checkbox" id="b" name="b">
                        <label>邮件通知抄送人</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <input type="checkbox" id="c" name="c">
                        <label>邮件通知发送人</label>
                    </td>
                </tr>
            </table>
        </div>
        <div style="margin-top: 20px">
            <div align="center">
                <a href="#" class="smallSubmit" id="replyInformation_liaison">回复</a>
            </div>
        </div>
    </div>
</#macro>
