<#macro auditInformationTableList workSheetAuditInformationList liaisonMap>
    <table class="basic-table">
        <tr class="header-row-2">
            <td>审核部门</td>
            <td>审核者</td>
            <td>审核时间</td>
            <td>审核状态</td>
            <td>审核意见</td>
           <#-- <#if (map.reviewTheStatus == 'PERSON_ONE')&&(liaisonMap.reviewTheStatus == 'LIAISON_STATUS_ONE')>-->
                <td>审核操作</td>
            <#--</#if>-->
        </tr>
        <#assign userLoginId = parameters.userLogin.partyId>
        <#if workSheetAuditInformationList?has_content>
            <#list workSheetAuditInformationList as list>
                <#assign person = delegator.findOne("Person",{"partyId":list.fullName},false)>
                <tr>
                    <td>${list.departmentName?default('')}</td>
                    <td>${person.fullName?default('')}</td>
                    <td> ${list.responseTime?default('')}</td>
                    <td>
                        <#assign reviewTheStatus = delegator.findOne("Enumeration",{"enumId":list.reviewTheStatus},false)>
                        ${reviewTheStatus.description}
                    </td>
                    <td>                                                                                           <#--是否是当前人-->                        <#--是当前用户并且审核人状态处于待审核状态-->                                         <#--当前工作联系单是否处于审核中状态-->
                        <textarea name="auditOpinion" id="auditOpinion_${list.fullName}" <#if (!(userLoginId == list.fullName))||((userLoginId == list.fullName)&&!(list.reviewTheStatus == 'PERSON_ONE')||!(liaisonMap.reviewTheStatus == 'LIAISON_STATUS_ONE'))>disabled</#if>>${list.content?default('')}</textarea>
                    </td>
                <#if (list.reviewTheStatus == 'PERSON_ONE')&&(liaisonMap.reviewTheStatus == 'LIAISON_STATUS_ONE')>
                    <td>
                        <#if userLoginId == list.fullName>
                                <a class="smallSubmit" onclick="$.liaison.approved('${list.contactListId}','${list.workSheetAuditInforId}','${list.fullName}','PERSON_TWO')">通过</a>
                                <a class="smallSubmit" onclick="$.liaison.approved('${list.contactListId}','${list.workSheetAuditInforId}','${list.fullName}','PERSON_THREE')">驳回</a>
                        </#if>
                    </td>
                </#if>
                </tr>
            </#list>
        </#if>

    </table>
</#macro>
