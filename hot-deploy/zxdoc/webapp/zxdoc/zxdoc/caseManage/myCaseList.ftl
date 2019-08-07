<div class="portlet light">
    <div class="portlet-body">
        <div class="scroller" style="height: 200px;" data-always-visible="1" data-rail-visible="0">
            <ul class="feeds">
                <#list caseList as line>
                <li>
                    <div class="col1">
                        <div class="cont">
                            <div class="cont-col1">
                                <div class="label label-sm label-info">
                                    <i class="fa fa-bar-chart-o"></i>
                                </div>
                            </div>
                            <div class="cont-col2">
                                <div class="desc"> <#if line.dataStatus != "0"><a href="#" onclick="displayInside('${request.contextPath}/control/CaseHome?caseId=${line.caseId}')"></#if><#if line.subCaseTitle?has_content>
                                    <span title="${line.title?default('')}">${line.subCaseTitle?default('')}</span> <#else>${line.title?default('')}</#if><#if line.dataStatus != "0"></a></#if>
                                    <#if line.dataStatus?has_content  && line.dataStatus != "1">
                                    <span class="label label-sm<#if line.dataStatus?has_content && line.dataStatus == "2"> label-danger"> 即将到期<#elseif line.dataStatus == "0"> label-warning "> 过期<#else> label-info "></#if> </span>
                                    </#if>
                                </div>
                            </div>
                        </div>
                    </div>
                </li>
                </#list>
            </ul>
        </div>
    </div>
</div>