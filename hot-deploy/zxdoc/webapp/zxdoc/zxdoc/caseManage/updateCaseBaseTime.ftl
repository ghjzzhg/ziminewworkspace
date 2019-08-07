<script src="${request.contextPath}/static/caseManage.js?t=20171023" type="text/javascript"></script>
<div class="portlet light ">
    <div class="portlet-body">
        <form id="caseBaseTimeForm">
            <input type="hidden" name="caseId" value="${parameters.caseId}">
            <table class="table table-hover table-striped table-bordered">
                <tbody>
                <#if baseTimes?has_content>
                    <#list baseTimes as baseTime>
                    <tr>
                        <td><label class="control-label">${baseTime.name}</label></td>
                        <td>
                            <#assign oldTime = oldTimes[baseTime.id]!>
                            <input type="text" class="form-control" name="baseTime_${baseTime.id}" value="${oldTime!}"
                                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})"/>
                        </td>
                    </tr>
                    </#list>
                </#if>
                </tbody>
            </table>
        </form>
        <div class="form-group">
            <div class="margiv-top-10">
                <a href="javascript:$.caseManage.saveCaseBaseTime();" class="btn green"> 保存 </a>
            </div>
        </div>
    </div>
</div>