<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#include "component://content/webapp/content/showUploadFileView.ftl"/>

<table cellspacing="0" class="basic-table hover-bar">
    <tbody>
    <tr class="header-row-2">
        <td>
            <label>版本号</label>
        </td>
        <td>
            <label>文档类别</label>
        </td>
        <td>
            <label>文档子类别</label>
        </td>
        <td>
            <label>文档标题</label>
        </td>
        <td>
            <label>发布部门</label>
        </td>
        <td>
            <label>发布人</label>
        </td>
        <td>
            <label>创建日期</label>
        </td>
    </tr>
    <#if data.list?has_content>
        <#list data.list as line>
        <tr id="ss">
            <td>
                <a href="#" class="hyperLinkStyle" onclick="$.FileData.showFileData('${line.fileDataId}','查看档案')">
                    ${line.versionNumber?default('')}</label>
                </a>
            </td>
            <td>
                <label>${line.parentType?default('')}</label>
            </td>
            <td>
                <label>${line.sonType?default('')}</label>
            </td>
            <td>
                <label>${line.documentTitle?default('')}</label>
            </td>
            <td>
                <label>${line.releaseDepartment?default('')}</label>
            </td>
            <td>
                <label>${line.issuer?default('')}</label>
            </td>
            <td>
                <label>${line.createdStamp?default('')}</label>
            </td>
        </tr>
        </#list>
    </#if>
    </tbody>
</table>
</html>