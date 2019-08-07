<#macro SignForTableList workSheetSignForInformationList liaisonMap >
<div>
    <div>
        <table class="basic-table">
            <tr class="header-row-2">
                <td>签收部门</td>
                <td>签收者</td>
                <td>签收时间</td>
                <td>签收状态</td>
                <td>签收意见</td>
               <#-- <#if (map.reviewTheStatus == 'PERSON_ONE')&&(liaisonMap.reviewTheStatus == 'LIAISON_STATUS_ONE')>-->
                    <td>签收操作</td>
                <#--</#if>-->
            </tr>
            <#assign userLoginId = parameters.userLogin.partyId>
            <#if workSheetSignForInformationList?has_content>
                <#list workSheetSignForInformationList as list>
                    <#assign person = delegator.findOne("Person",{"partyId":list.fullName},false)>
                    <tr>
                        <td>${list.departmentName?default('')}</td>
                        <td>${person.fullName?default('')}</td>
                        <td> ${list.SignforTime?default('')}</td>
                        <td>
                            <#assign reviewTheStatus = delegator.findOne("Enumeration",{"enumId":list.reviewTheStatus},false)>
                            ${reviewTheStatus.description}
                        </td>
                        <td>                                                                                           <#--是否是当前人-->                        <#--是当前用户并且签收人状态处于未签收状态-->                                         <#--当前工作联系单是否处于已送达状态-->
                            <textarea name="auditOpinion" id="auditOpinion_${list.workSheetSignForId}" <#if (!(userLoginId == list.fullName))||((userLoginId == list.fullName)&&!(list.reviewTheStatus == 'SIGN_FOR_NO')||!(liaisonMap.reviewTheStatus == 'LIAISON_STATUS_TWO'))>disabled</#if>>${list.SignForContent?default('')}</textarea>
                        </td>
                    <#if (list.reviewTheStatus == 'SIGN_FOR_NO')&&(liaisonMap.reviewTheStatus == 'LIAISON_STATUS_TWO')>
                        <td>
                            <#if userLoginId == list.fullName>
                                    <a class="smallSubmit" onclick="$.liaison.signed('${list.contactListId}','${list.fullName}','${list.workSheetSignForId}','SIGN_FOR_YES')">签收</a>
                                <#--<#else>
                                    <#assign reviewTheStatus = delegator.findOne("Enumeration",{"enumId":list.reviewTheStatus},false)>
                                ${reviewTheStatus.description}-->
                           <#-- <#else >
                                <#assign reviewTheStatus = delegator.findOne("Enumeration",{"enumId":list.reviewTheStatus},false)>
                            ${reviewTheStatus.description}-->
                            </#if>
                        </td>
                    </#if>
                    </tr>
                </#list>
            </#if>

        </table>
    </div>
</div>
</#macro>
