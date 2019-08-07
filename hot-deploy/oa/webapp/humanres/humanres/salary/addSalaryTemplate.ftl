<script language="javascript">
    var mouldContent;
    $(function() {
        mouldContent = KindEditor.create('textarea[name="mouldContent"]', {
            allowFileManager : true
        });
        $("#mouldName").data("promptPosition", "bottomLeft");
    });
</script>
<#assign salaryEntryList = delegator.findAll("TblSalaryEntry", false)>
<#if salaryBillMould?has_content>
    <#assign mouldId = salaryBillMould.mouldId>
    <#assign mouldName = salaryBillMould.mouldName>
    <#assign mouldContent = salaryBillMould.mouldContent>

</#if>
<form name="addSalaryBillMouldForm" id="addSalaryBillMouldForm" class="basic-form">
    <input type="hidden" name="mouldId" id="mouldId" value="${mouldId?default('')}">
    <table>
        <tbody>
        <tr>
            <td class="label">
                <label for="mouldName" id="mouldName_title"><b class="requiredAsterisk">*</b>模板名称：</label>
            </td>
            <td colspan="4" class="jqv">
                <input type="text" id="mouldName" name="mouldName" value="${mouldName?default('')}" class="validate[required]" >
            </td>
        </tr>
        <tr>
            <td class="label">　
                <label for="salaryBillMould_template" id="salaryBillMould_template_title"><b class="requiredAsterisk">*</b>模板内容：</label>
            </td>
            <td colspan="4"></td>
        </tr>
        <tr>
            <td colspan="4" class="jqv">
                <textarea name="mouldContent" id="mouldContent" style="width: 90%;" rows="10" class="validate[required]">${mouldContent?default('')}</textarea>
            </td>
        </tr>
        </tbody>
        <tr align="left">
            <td colspan="4">
                <a href="#" class="smallSubmit" onclick="$.salary.saveSalaryTemplate()">保存模板</a>
            </td>
        </tr>
    </table>
</form>
<div style="padding-left: 10px;font-size: 10pt">
    <div style="font-size: 11pt;color: red">
        说明：员工工资条模板维护，请复制以下${r'${*****}'}内容到上面模板中即可。
    </div>
    <table cellspacing="0">
        <tbody>
        <tr>
            <th style="font-size: 11pt;">员工基本信息项：</th>
        </tr>
        <tr class="header-row-2">
            <td>
                所在部门：
                <label>${r'${user_dept}'}</label>
            </td>
            <td>
                员工工号：
                <label>${r'${user_code}'}</label>
            </td>
            <td>
                员工姓名：
                <label>${r'${user_name}'}</label>
            </td>
        </tr>
        <tr>
            <td>
                员工岗位：
                <label>${r'${user_position}'}</label>
            </td>
            <td>
                基本工资：
                <label>${r'${salary}'}</label>
            </td>
            <td>
                工资年份：
                <label>${r'${s_year}'}</label>
            </td>
        </tr>
        <tr>
            <td>
                工资月份：
                <label>${r'${s_month}'}</label>
            </td>
            <td>
                应出勤天数：
                <label>${r'${attendanceDays}'}</label>
            </td>
            <td>
                实际出勤天数：
                <label>${r'${actualDays}'}</label>
            </td>
        </tr>
        <tr>
            <td>
                实发工资：
                <label>${r'${actualSalary}'}</label>
            </td>
        </tr>
        <tr>
            <th style="font-size: 11pt;">薪资条目可选项：</th>
        </tr>
        <#if salaryEntryList?has_content>
            <#list salaryEntryList as list>
                <#if list_index % 3 == 0>
                <tr>
                </#if>
                    <td>
                       ${list.title?default("")}：
                           <#--<input type="text" value="${r'${'}${list.entryId?default("")}${r'}'}" style="border:none;" readonly="">-->
                           <label>${r'${'}${list.entryId?default("")}${r'}'}</label>
                    </td>
                <#if (list_index-2) % 3 == 0>
                <tr>
                </#if>
            </#list>
        </#if>
        </tbody>
    </table>
</div>



<#--所在部门：<input type="text" value="${r'${user_dept}'}" style="border:none;" readonly=""><br>-->
<#--员工工号：<input type="text" value="${r'${user_code}'}" style="border:none;" readonly=""><br>-->
<#--员工姓名：<input type="text" value="${r'${user_name}'}" style="border:none;" readonly=""><br>-->
<#--员工岗位：<input type="text" value="${r'${user_position}'}" style="border:none;" readonly=""><br>-->
<#--工资年份：<input type="text" value="${r'${s_year}'}" style="border:none;" readonly=""><br>-->
<#--工资月份：<input type="text" value="${r'${s_month}'}" style="border:none;" readonly=""><br>-->
<#--基本工资：<input type="text" value="${r'${salary}'}" style="border:none;" readonly=""><br>-->
<#--奖金：<input type="text" value="${r'${bonus}'}" style="border:none;" readonly=""><br>-->
<#--应出勤天数：<input type="text" value="${r'${attendanceDays}'}" style="border:none;" readonly=""><br>-->
<#--实际出勤天数：<input type="text" value="${r'${actualDays}'}" style="border:none;" readonly=""><br>-->
<#--餐费补贴：<input type="text" value="${r'${neal}'}" style="border:none;" readonly=""><br>-->
<#--住房补贴：<input type="text" value="${r'${housing}'}" style="border:none;" readonly=""><br>-->
<#--话费补贴：<input type="text" value="${r'${calling}'}" style="border:none;" readonly=""><br>-->
<#--交通补贴：<input type="text" value="${r'${traffic}'}" style="border:none;" readonly=""><br>-->
<#--应发工资合计：<input type="text" value="${r'${paid}'}" style="border:none;" readonly=""><br>-->
<#--应扣代缴个人社保金：<input type="text" value="${r'${social}'}" style="border:none;" readonly=""><br>-->
<#--实发工资：<input type="text" value="${r'${actualSalary}'}" style="border:none;" readonly=""><br>-->