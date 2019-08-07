<script language="javascript">
    var templateForWorkContactList;
    $(function () {
        templateForWorkContactList = KindEditor.create('textarea[name="content"]', {
            allowFileManager: true
        });
    });
    $("#ifApprove").click(function(){
        if(document.getElementById("ifApprove").checked){
            document.getElementById("auditor").style.display="";
            document.getElementById("auditorPersonLabel").style.display="";
        }else{
            document.getElementById("auditor").style.display="none";
            document.getElementById("auditorPersonLabel").style.display="none";
        }
    });

    $("#ifResponse").click(function(){
        if(document.getElementById("ifResponse").checked){
            document.getElementById("hopeData").style.display="";
            document.getElementById("hopeDataLabel").style.display="";
        }else{
            document.getElementById("hopeData").style.display="none";
            document.getElementById("hopeDataLabel").style.display="none";
        }
    });
</script>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<div>
    <div>
        <table align="center" cellpadding="0" cellspacing="0" border="0" width="100%" height="50px">
            <tbody>
            <tr>
                <td valign="top">
                    <img src="/images/rextec_logo.png" width="140px" height="45px" alt="瑞克斯信息科技有限公司">
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <form name="perfExamEditForm" id="perfExamEditForm" class="basic-form">
        <div>
            <div style="text-align: center; letter-spacing: 20px; margin:0 auto; font-weight: bold;font-size: 3em;margin-bottom: 0.5em;">
                工作联络单
            </div>
            <div>

                <table class="basic-table">
                    <tbody>
                    <tr>
                        <td colspan="6">
                            <table cellpadding="0" cellspacing="0" border="1" width="100%"
                                   style="border-collapse: collapse">
                                <tbody>
                                <tr>
                                    <td class="label">
                                        <label for="Liaison_a"
                                               id="Liaison_a_title"><b class="requiredAsterisk">*</b>联络单主送至:</label>
                                    </td>
                                    <td width="220px" class="jqv">
                                    <@selectStaff id="mainPerson" name="mainPerson" value="" multiple=true required=true/>
                                    </td>
                                    <td class="label">
                                        <label for="Liaison_b"
                                               id="Liaison_b_title" style="">联络单抄送至:</label></td>
                                    <td width="220px" class="jqv">
                                    <@selectStaff id="copyPerson" name="copyPerson" value="" multiple=true/>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">
                                        <label for="Liaison_c"
                                               id="Liaison_c_title" ><b class="requiredAsterisk">*</b>联络单标题：</label></td>
                                    <td colspan="3" class="jqv">
                                        <input type="text" id="title" name="title" style="width: 100%" maxlength="20"
                                               class="validate[required,custom[onlyLetterNumberChinese]]"/>
                                    </td>
                                </tr>

                                <tr>
                                    <td class="label">
                                        <label for="Liaison_d"
                                               id="Liaison_d_title"><b class="requiredAsterisk">*</b>联络单类型：</label></td>
                                    <td class="jqv">
                                        <select id="contactListType" name="contactListType" class="validate[required]">
                                        <#list TypeManagementList as typeList>
                                            <option value="${typeList.typeManagementListId}">${typeList.typeManagement}</option>
                                        </#list>
                                        </select>
                                        <a class="smallSubmit" href="#" onclick="showTypeManagement()" title="类型管理">类型管理</a>
                                    </td>

                                    <td class="label">
                                        <label for="Liaison_e"
                                               id="Liaison_e_title"><b class="requiredAsterisk">*</b>联络单编号：</label></td>
                                    <td class="jqv">
                                        <label>${number}</label>
                                        <input type="hidden" name="number" value="${number}"/>
                                    </td>
                                </tr>

                                <tr>
                                    <td class="label">
                                        <label for="Liaison_f"
                                               id="Liaison_f_title">是否需要审核：</label></td>
                                    <td>
                                        需要：<input type="checkbox" name="ifApprove" id="ifApprove" onclick="ifApprove">
                                    </td>

                                    <td class="label" id="auditor" style="display:none">
                                        <label for="Liaison_a"
                                               id="auditor" style="color: red"><b class="requiredAsterisk">*</b>审核人:</label>
                                    </td>
                                    <td id="auditorPersonLabel" style="display:none">
                                    <@selectStaff id="auditorPerson" name="auditorPerson" value="" multiple=true/>
                                    </td>
                                <tr>
                                <tr>
                                    <td class="label">
                                        <label for="Liaison_g"
                                               id="Liaison_g_title">是否需要回复：</label></td>
                                    <td>
                                        需要：<input type="checkbox" id="ifResponse" name="ifResponse" onclick="ifResponse">
                                    </td>

                                    <td class="label" id="hopeData" style="display:none">
                                        <label for="Liaison_g"
                                               id="Liaison_g_title"><b class="requiredAsterisk">*</b>希望回复日期：</label></td>
                                    <td id="hopeDataLabel" style="display:none">
                                    <@htmlTemplate.renderDateTimeField name="responseTime" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS"
                                    value="" size="25" maxlength="30" id="responseTime" dateType="" shortDateInput=false timeDropdownParamName=""
                                    defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                                    timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">
                                        <label for="Liaison_h"
                                               id="Liaison_h_title">回复邮件通知：</label></td>
                                    <td colspan="3">
                                        <span class="tooltip">是否邮件通知主送、抄送及发送人
                                        </span>
                                        <input type="radio" name="ifEmail" value="yes">邮件通知
                                        <input type="radio" name="ifEmail" value="no" checked="true">不通知
                                    </td>
                                </tr>
                                <tr>
                                    <td class="label">
                                        <label for="Liaison_i"
                                               id="Liaison_i_title"><b class="requiredAsterisk">*</b>联络单内容：</label></td>
                                    <td colspan="3" class="jqv">
                                        <textarea id="Liaison_i" name="content" style="width:100%;" class="validate[required]" ></textarea>
                                    </td>
                                </tr>

                                </tbody>
                            </table>
                        </td>
                    </tr>
                    </tbody>
                    <tr align="center">
                        <td>
                            <a href="#" class="smallSubmit" onclick="$.workContactList.saveWorkContactList(templateForWorkContactList)">发送</a>
                        </td>
                    </tr>
                </table>

            </div>
        </div>
    </form>
</div>