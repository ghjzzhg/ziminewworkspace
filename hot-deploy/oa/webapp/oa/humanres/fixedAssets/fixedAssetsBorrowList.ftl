<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#if data.list?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign lowIndex = data.lowIndex>
    <#assign commonUrl = "searchAssetsBorrow"/>
    <#assign sortParam = "&fixedAssetsCode="+data.fixedAssetsCode?default("")+"&fixedAssetsName="+data.fixedAssetsName?default("")
    +"&assetType="+data.assetType?default("")+"&fixedAssetsManager="+data.fixedAssetsManager?default("")
    +"&applyPerson="+data.applyPerson?default("")+"&applyPerson="+data.applyPerson?default("")
    +"&assetStatus="+data.assetStatus?default("")+"&lendStatus="+data.lendStatus?default("")/>
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
            <label>数量</label>
        </td>
        <td>
            <label>借出次数</label>
        </td>
        <td>
            <label>借用状态</label>
        </td>
        <td>
            <label>借出时间</label>
        </td>
        <td>
            <label>借出天数</label>
        </td>
        <td>
            <label>备注</label>
        </td>
    </tr>
    <#if data.list?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.list as line>
        <tr <#if alt_row> class="alternate-row"</#if> id="ss">
            <#if (line.lendStatus?default(""))=="FIXED_ASSETS_LEND_STATUS_2" || (line.lendStatus?default(""))=="FIXED_ASSETS_LEND_STATUS_3">
                <td>
                    <a name="fixedAssetsCode" href="#" class="hyperLinkStyle" onclick="$.fixedAssets.borrowInfo('${line.fixedAssetsId}')" title="资产编码">
                    ${line.fixedAssetsCode?default("")}
                    </a>
                </td>
            <#elseif (line.lendStatus?default(""))=="FIXED_ASSETS_LEND_STATUS_1" || (line.lendStatus?default(""))=="">
                <td>
                    <label>${line.fixedAssetsCode?default("")}</label>
                </td>
            </#if>

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
                <label>${line.assetCount?default("")}</label>
            </td>
            <td>
                <label>${line.lendCount?default("")}</label>
            </td>
            <td>
                <label>${line.lendStatusName?default("")}</label>
            </td>
            <#if (line.lendStatus?default(""))=="FIXED_ASSETS_LEND_STATUS_1" || (line.lendStatus?default(""))=="">
                <td>
                    <a name="lendDate" href="#" class="smallSubmit" onclick="javascript:$.fixedAssets.borrowRegister('${line.fixedAssetsId}')" title="我要借用">
                        <label>我要借用</label>
                    </a>
                </td>
            <#elseif (line.lendStatus?default(""))=="FIXED_ASSETS_LEND_STATUS_2" && ("1" == line.statues)>
                <td>
                    <a name="lendDate" href="#" class="smallSubmit" onclick="$.fixedAssets.borrowAssetsConfirm('${line.fixedAssetsId}')" title="借用确认">
                        <label>借用确认</label>
                    </a>
                </td>
            <#elseif (line.lendStatus?default(""))=="FIXED_ASSETS_LEND_STATUS_2" && (  "1" != line.statues)>
                <td>
                    <label>待管理者确认借出</label>
                </td>
            <#elseif (line.lendStatus?default(""))=="FIXED_ASSETS_LEND_STATUS_3" && ("1" == line.statues)>
                <td>
                    <a name="lendDate" href="#" class="smallSubmit" onclick="$.fixedAssets.returnAssets('${line.fixedAssetsId}')" title="归还确认">
                        <label>归还确认</label>
                    </a>
                </td>
            <#elseif (line.lendStatus?default("")=="FIXED_ASSETS_LEND_STATUS_3") && ("1" == line.statues)>
                <td>
                    <label>待借用者归还</label>
                </td>
            </#if>

            <td>
                <label>${line.lendDayCount?default("")}</label>
            </td>
            <td>
                <label>${line.remarks?default("")}</label>
            </td>
        </tr>
            <#assign rowCount = rowCount + 1>
            <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>
<#if data.list?has_content>
    <@nextPrevAjax targetId="ListFixedAssetsBorrow" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>