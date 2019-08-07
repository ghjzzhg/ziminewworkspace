<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if recordList?has_content>
    <#assign viewIndex = viewIndex>
    <#assign highIndex = highIndex>
    <#assign totalCount = totalCount>
    <#assign viewSize = viewSize>
    <#assign lowIndex = lowIndex>
    <#assign commonUrl = "ListEmployeeRecord"/>
    <#assign sortParam = "&fullName="+parameters.fullName?default("")+"&workerSn="+parameters.workerSn?default("")
                        +"&gender="+parameters.gender?default("")+"&startDate="+parameters.startDate?default("")
                        +"&endDate="+parameters.endDate?default("")+"&jobState="+parameters.jobState?default("")/>
    <#assign param = sortParam + "&sortField=" + sortField?default("")/>
    <#assign viewIndexFirst = 0/>
    <#assign viewIndexPrevious = viewIndex - 1/>
    <#assign viewIndexNext = viewIndex + 1/>
    <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
</#if>
<table cellspacing="0" class="basic-table hover-bar">
    <tbody>
    <tr class="header-row-2">
        <td>
            <label>序号</label>
        </td>
        <td>
            <label>工号</label>
        </td>
        <td>
            <label>姓名</label>
        </td>
        <td>
            <label>性别</label>
        </td>
        <td>
            <label>部门</label>
        </td>
        <td>
            <label>学历</label>
        </td>
        <td>
            <label>学位</label>
        </td>
        <td>
            <label>身份证号码</label>
        </td>
        <td>
            <label>出生日期</label>
        </td>
        <td>
            <label>入职日期</label>
        </td>
        <td>
            <label>在职状态</label>
        </td>
        <td>
            <label>登录账户</label>
        </td>
        <td>
            <label>修改</label>
        </td>
        <td>
            <label>添加账户</label>
        </td>
    </tr>
    <#if recordList?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list recordList as line>
        <tr<#if alt_row> class="alternate-row"</#if> id="ss">
            <td>
                ${rowCount + 1}
                <input type="hidden" name="partyId" value="${line.partyId?default('')}">
            </td>
            <td>
                <label>${line.workerSn?default('')}</label>
            </td>
            <td>
                <label>${line.fullName?default('')}</label>
            </td>
            <td>
                <label>${line.genderName?default('')}</label>
            </td>
            <td>
                <label>${line.groupName?default('')}</label>
            </td>
            <td>
                <label>${line.diploma?default('')}</label>
            </td>
            <td>
                <label>${line.degree?default('无')}</label>
            </td>
            <td>
                <label>${line.cardId?default('')}</label>
            </td>
            <td>
                <label>${line.birthDay?default('')}</label>
            </td>
            <td>
                <label>${line.workDate?default('')}</label>
            </td>
            <td>
                <label>${line.jobStateName?default('')}</label>
            </td>
            <#if oaAdminPermission>
                <td>
                    <#--<#assign partyId = line.partyId>-->
                    <#assign userLogins = delegator.findByAnd("UserLogin", {"partyId": line.partyId})>
                    <#if userLogins?has_content>
                        <#list userLogins as userLogins1>
                        <div <#if userLogins1.getString("enabled") == "N"></#if>><a href="#nowhere" class="hyperLinkStyle" onclick="$.recordManagement.editUserLogin('${userLogins1.userLoginId}')">${userLogins1.userLoginId}</a></div>
                        </#list>
                    </#if>
                </td>
            </#if>
                <td>
                    <a href="#nowhere" onclick="javascript:$.recordManagement.editEmployee('${line.partyId?default('')}');" title="修改" class="icon-edit"></a>
                </td>
            <#if oaAdminPermission>
                <td>
                    <a href="#nowhere" onclick="javascript:$.recordManagement.createUserLogin('${line.partyId?default('')}');" title="添加" class="icon-user"></a>
                </td>
            </#if>
        </tr>
        <#assign rowCount = rowCount + 1>
        <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>

<#if recordList?has_content>
    <@nextPrevAjax targetId="screenlet_3_col" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>