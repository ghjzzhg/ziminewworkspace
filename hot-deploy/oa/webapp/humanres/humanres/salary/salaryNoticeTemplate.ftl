<script language="javascript">
    var content1;
    $(function() {
        content1 = KindEditor.create('textarea[name="template"]', {
            allowFileManager : true
        });
    });
</script>
<#assign salaryEntryList = delegator.findAll("TblSalaryEntry", false)>
<#if mouldName1?has_content>
    <div id="salaryBillMouldArea">
        <table>
            <tbody>
            <tr>
                <th class="label">
                    当前使用模板名称：
                </th>
                <td>
                    <label id="mouldName1" name="mouldName1">${mouldName1?default('')}</label>
                </td>
            </tr>
            <tr>
                <th class="label">　
                    当前使用模板内容：
                </th>
            </tr>
            <tr>
                <td colspan="10">
                    <textarea name="template" id="salaryBillMouldArea" style="width: 90%;height: 250px">${content1}</textarea>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</#if>
<#--<br>-->
<#--<div>-->
    <#--<a class="smallSubmit" href="#" onclick="$.salary.addTemplateManagement()" title="增加模板">增加模板</a>-->
<#--</div>-->
<#--</form>-->
<#--<div style="padding-left: 10px;">-->
    <#--<div style="font-size: large">-->
        <#--说明：员工工资条模板维护，请复制以下:${r'${*****}'}内容到上面模板中即可。-->
    <#--</div>-->
    <#--<table cellspacing="0">-->
        <#--<tbody>-->
            <#--<tr>-->
                <#--<td style="font-size: large">员工基本信息项：</td>-->
            <#--</tr>-->
            <#--<tr class="header-row-2">-->
                <#--<td>-->
                    <#--所在部门：-->
                    <#--<label>${r'${user_dept}'}</label>-->
                <#--</td>-->
                <#--<td>-->
                    <#--员工工号：-->
                    <#--<label>${r'${user_code}'}</label>-->
                <#--</td>-->
                <#--<td>-->
                    <#--员工姓名：-->
                    <#--<label>${r'${user_name}'}</label>-->
                <#--</td>-->
            <#--</tr>-->
            <#--<tr>-->
                <#--<td>-->
                    <#--员工岗位：-->
                    <#--<label>${r'${user_position}'}</label>-->
                <#--</td>-->
                <#--<td>-->
                    <#--基本工资：-->
                    <#--<label>${r'${salary}'}</label>-->
                <#--</td>-->
                <#--<td>-->
                    <#--工资年份：-->
                    <#--<label>${r'${s_year}'}</label>-->
                <#--</td>-->
            <#--</tr>-->
            <#--<tr>-->
                <#--<td>-->
                    <#--工资月份：-->
                    <#--<label>${r'${s_month}'}</label>-->
                <#--</td>-->
                <#--<td>-->
                    <#--应出勤天数：-->
                    <#--<label>${r'${attendanceDays}'}</label>-->
                <#--</td>-->
                <#--<td>-->
                    <#--实际出勤天数：-->
                    <#--<label>${r'${actualDays}'}</label>-->
                <#--</td>-->
            <#--</tr>-->
            <#--<tr>-->
                <#--<td>-->
                    <#--实发工资：-->
                    <#--<label>${r'${actualSalary}'}</label>-->
                <#--</td>-->
            <#--</tr>-->
            <#--<tr>-->
                <#--<td style="font-size: large">薪资条目可选项：</td>-->
            <#--</tr>-->
            <#--<#if salaryEntryList?has_content>-->
                <#--<#list salaryEntryList as list>-->
                    <#--<#if list_index % 3 == 0>-->
                    <#--<tr>-->
                    <#--</#if>-->
                        <#--<td>-->
                           <#--<label>${list.title?default("")}：${r'${'}${list.entryId?default("")}${r'}'}</label>-->
                        <#--</td>-->
                    <#--<#if (list_index-2) % 3 == 0>-->
                    <#--<tr>-->
                    <#--</#if>-->
                <#--</#list>-->
            <#--</#if>-->
        <#--</tbody>-->
    <#--</table>-->
<#--</div>-->



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