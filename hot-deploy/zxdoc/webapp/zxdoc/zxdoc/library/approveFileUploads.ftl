<div class="portlet light">
    <div class="portlet-body">

        <div class="table-scrollable table-scrollable-borderless">
            <table class="table table-hover table-light">
            <#if parameters.data?has_content>
                <#list parameters.data as list >
                    <tr>
                        <td>
                            <a href="#nowhere" onclick="displayInside('${request.contextPath}/control/ApproveFileUpload?fileId=${list.dataResourceId?default('')}')">${list.title?default('')}</a>
                            <span class="label label-sm label-info "> ${list.description?default('')} </span>
                        </td>
                        <td style="white-space: nowrap"> ${list.fullName?default('')} </td>
                        <td style="white-space: nowrap"> ${list.createdStamp?default('')} </td>
                    </tr>
                </#list>
            </#if>
            </table>
        </div>
    </div>
</div>