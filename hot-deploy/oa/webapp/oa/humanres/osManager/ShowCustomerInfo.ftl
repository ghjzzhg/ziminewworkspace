<#include "component://widget/templates/dropDownList.ftl"/>
<script language="javascript">
    var template;
    $(function () {
        template = KindEditor.create('textarea[name="note"]', {
            allowFileManager: true
        });
        $("#note").html(unescapeHtmlText('${data.list.note?default('')}'));
    });

    function individualFeedback(id,workreportId){
        displayInTab3("individualFeedbackTbl", "个人反馈信息", {
            requestUrl: "individualFeedback",
            data: {workReportId:workreportId,personId:id},
            width: "600px",
            position: "center"
        });
    }
</script>

<form method="post" action="" id="EditCustomer" class="basic-form" name="EditCustomer">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>客户名称</label>
            </td>
            <td class="jqv">
                <#if data.list?has_content>
                    <label>${data.list.customerName?default('')}</label>
                </#if>
            </td>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>客户类型</label>
            </td>
            <td class="jqv">
                <label></label>
                <#if data.typeList?has_content && data.list?has_content>
                    <#list data.typeList as list>
                        <#if list.enumId?has_content && data.list.customerType?has_content  && (data.list.customerType?default("")) == (list.enumId?default(""))>
                            <label>${list.description?default("")}</label>
                        </#if>
                    </#list>
                </#if>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>联系人</label>
            </td>
            <td class="jqv">
            <#if data.list?has_content>
                <label>${data.list.linkman?default('')}</label>
            </#if>
            </td>
            <td class="label">
                <label>所在地址</label>
            </td>
            <td class="jqv">
            <#if data.list?has_content>
                <label>${data.list.address?default('')}</label>
            </#if>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label>联系电话</label>
            </td>
            <td class="jqv">
            <#if data.list?has_content>
                <label>${data.list.telephone?default('')}</label>
            </#if>
            </td>
            <td class="label">
                <label>联系传真</label>
            </td>
            <td class="jqv">
            <#if data.list?has_content>
                <label>${data.list.linkFax?default('')}</label>
            </#if>
            </td>
        </tr>
        <tr>
            <td class="label">
                <label><b class="requiredAsterisk">*</b>联系手机</label>
            </td>
            <td class="jqv">
            <#if data.list?has_content>
                <label>${data.list.phone?default('')}</label>
            </#if>
            </td>
        </tr>

        <tr>
            <td class="label" >　
                <label>备注：</label>
            </td>
            <td colspan="2"></td>
        </tr>
        <tr>
            <td colspan="4" class="jqv">
           <label id="note"/>
            </td>
        </tr>
        </tbody>
    </table>
</form>
