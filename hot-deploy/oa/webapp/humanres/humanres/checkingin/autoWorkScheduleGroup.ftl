<select name="workGroups" id="workGroupItem" multiple="multiple" style="display: none">
<#if groupList?has_content>
    <#list groupList as list>
        <option value="${list.partyId}" name="${list.partyId}">${list.groupName}</option>
    </#list>
</#if>
</select>
<a class="smallSubmit" href="#" id="ms_select_ye" title="全选">全选</a>
<a class="smallSubmit" href="#" id="ms_deselect_no" title="全部不选">全部不选</a>