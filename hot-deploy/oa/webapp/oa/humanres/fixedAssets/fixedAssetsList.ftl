<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if data.list?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "searchFixedAssets"/>
    <#assign sortParam = "&fixedAssetsCode="+data.fixedAssetsCode?default("")+"&fixedAssetsName="+data.fixedAssetsName?default("")
    +"&assetType="+data.assetType?default("")+"&department="+data.department?default("")
    +"&usePerson="+data.usePerson?default("")+"&useDepartment="+data.useDepartment?default("")
    +"&assetStatus="+data.assetStatus?default("")+"&canLendOut="+data.canLendOut?default("")/>
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
            <label>资产编码</label>
        </td>
        <td>
            <label>资产名称</label>
        </td>
        <td>
            <label>资产类别</label>
        </td>
        <td>
            <label>规格</label>
        </td>
        <td>
            <label>使用年限</label>
        </td>
        <td>
            <label>数量</label>
        </td>
        <td>
            <label>使用部门</label>
        </td>
        <td>
            <label>使用人</label>
        </td>
        <td>
            <label>状态</label>
        </td>
        <td>
            <label>存放地点</label>
        </td>
        <td>
            <label>备注</label>
        </td>
        <td>
        </td>
        <td>
        </td>
    </tr>
    <#if data.list?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.list as line>
        <tr<#if alt_row> class="alternate-row"</#if> id="ss">
            <td>
                <a name="fixedAssetsCode" href="#" class="hyperLinkStyle" onclick="$.fixedAssets.showFixedAssetsInfoForm('${line.fixedAssetsId}')" title="资产编码">
                ${line.fixedAssetsCode?default("")}
                </a>
            </td>
            <td>
                <label>${line.fixedAssetsName?default("")}</label>
            </td>
            <td>
               <label>${line.assetTypeName?default("")}</label>
            </td>
            <td>
                <label>${line.assetStandard?default("")}</label>
            </td>
            <td>
                <label>${line.limitYear?default("")}</label>
            </td>
            <td>
                <label>${line.assetCount?default("")}</label>
            </td>
            <td>
                <label>${line.useDepartmentName?default("")}</label>
                <input type="hidden" name="userDepartment" value="${line.useDepartment?default("")}">
            </td>
            <td>
                <label>${line.usePersonName?default("")}</label>
                <input type="hidden" name="usePerson" value="${line.usePerson?default("")}">
            </td>
            <td>
                <label>${line.assetStatusName?default("")}</label>
                <input type="hidden" name="assetStatus" value="${line.assetStatus?default("")}">
            </td>
            <td>
                <label>${line.keepPlace?default("")}</label>
            </td>
            <td>
                <label>${line.remarks?default("")}</label>
            </td>
            <td>
                <a name="updateLink" class="icon-edit" href="#" onclick="$.fixedAssets.addFixedAssets('${line.fixedAssetsId}')" title="更新"></a>
            </td>
            <td>
                <a name="deleteLink" class="icon-trash" href="#" onclick="$.fixedAssets.deleteFixedsAssets('${line.fixedAssetsId}')" title="删除"></a>
            </td>
        </tr>
            <#assign rowCount = rowCount + 1>
            <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>
<#if data.list?has_content>
    <@nextPrevAjax targetId="ListFixedAssetsOrg" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>