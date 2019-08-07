<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
<#include "component://content/webapp/content/showUploadFileView.ftl"/>
<#if data.perfExamList?has_content>
    <#assign viewIndex = data.viewIndex>
    <#assign highIndex = data.highIndex>
    <#assign totalCount = data.totalCount>
    <#assign viewSize = data.viewSize>
    <#assign commonUrl = "searchPerfExam"/>
    <#assign param = "name="+data.name?default("")+"&department="+data.department?default("")+"&post="+data.post?default("")+"&evaluateYear="+data.evaluateYear?default("")+"&evaluateMonth="+data.evaluateMonth?default("")/>
    <#assign viewIndexFirst = 0/>
    <#assign viewIndexPrevious = viewIndex - 1/>
    <#assign viewIndexNext = viewIndex + 1/>
    <#assign viewIndexLast = Static["org.ofbiz.base.util.UtilMisc"].getViewLastIndex(totalCount, viewSize) />
    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount",lowIndex, "highCount",highIndex, "total",totalCount)/>
    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
</#if>
<table cellspacing="0" class="basic-table hover-bar" align="center">
    <tbody>
    <tr class="header-row-2">
        <td>
            <label>序号</label>
        </td>
        <td>
            <label>员工姓名</label>
        </td>
        <td>
            <label>部门</label>
        </td>
        <td>
            <label>岗位</label>
        </td>
        <td>
            <label>考核月份</label>
        </td>
        <td>
            <label>考评类别</label>
        </td>
        <td>
            <label>考评周期</label>
        </td>
        <td>
            <label>考评人评分</label>
        </td>
        <td>
            <label>初审评分</label>
        </td>
        <td>
            <label>终审评分</label>
        </td>
        <td>
            <label>考评人</label><br/>
            <label>考评日期</label>
        </td>
        <td>
            <label>初审人</label><br/>
            <label>初审日期</label>
        </td>
        <td>
            <label>终审人</label><br/>
            <label>终审日期</label>
        </td>
        <td>
            <label>归档人</label><br/>
            <label>归档日期</label>
        </td>
        <td>

        </td>
        <td>

        </td>
    </tr>
    <#if data.perfExamList?has_content>
        <#assign alt_row = false>
        <#assign rowCount = viewIndex * viewSize>
        <#list data.perfExamList as line>
        <tr<#if alt_row> class="alternate-row"</#if> id="ss">
            <td>
            ${rowCount + 1}
            </td>
            <td>
                <a href="#" class="hyperLinkStyle"  onclick="$.perfExam.showPerfExam('${line.examId}','查看考核')">
                ${line.fullName?default('')}
                 </a>
            </td>
            <td>
                <label>${line.departmentName?default('')}</label>
            </td>
            <td>
                <label>${line.occupationName?default('')}</label>
            </td>
            <td>
                <label>${line.evaluateYear?default('')}年${line.evaluateMonth?default('')}月</label>
            </td>
            <td>
                <label>${line.description?default('')}</label>
            </td>
            <td>
                <label>${line.perfExamCycle?default('')}</label>
            </td>
            <td>
                <label>${line.evaluatorScore?default('')}</label>
            </td>
            <td>
                <label>${line.reviewerScore?default('')}</label>
            </td>
            <td>
                <label>${line.approverScore?default('')}</label>
            </td>
            <td>
                <label>${line.evaluatorName?default('')}</label><br/>
                <label>${line.evaluateDate?default('')}</label>
            </td>
            <td>
                <label>${line.reviewerName?default('')}</label><br/>
                <label>${line.reviewerDate?default('')}</label>
            </td>
            <td>
                <label>${line.approverName?default('')}</label><br/>
                <label>${line.approverDate?default('')}</label>
            </td>

            <td>
                <label>${line.finalizerName?default('')}</label><br/>
                <label>${line.finalizerDate?default('')}</label>
            </td>
            <td >
                <#if line.evaluatePower.equals("1")>
                    <a href="#" onclick="javascript:$.perfExam.editPerfExam('${line.examId?default('')}','${line.planId?default('')}','PERF_EXAM_TYPE_1','${line.evaluateYear?default('')}','${line.evaluateMonth?default('')}')" title="考评" class="glyphicon glyphicon-screenshot" style=""/>
                <#elseif line.reviewePower.equals("1")>
                    <a href="#" onclick="javascript:$.perfExam.editPerfExam('${line.examId?default('')}','${line.planId?default('')}','PERF_EXAM_TYPE_2')" title="初审" class="glyphicon glyphicon-screenshot"/>
                <#elseif line.approvePower.equals("1")>
                    <a href="#" onclick="javascript:$.perfExam.editPerfExam('${line.examId?default('')}','${line.planId?default('')}','PERF_EXAM_TYPE_3')" title="终审" class="glyphicon glyphicon-screenshot"/>
                <#elseif line.finalizePower.equals("1")>
                    <a href="#" onclick="javascript:$.perfExam.archivePerfExam('${line.examId?default('')}','PERF_EXAM_TYPE_4')" title="归档" class="glyphicon glyphicon-screenshot"/>
                <#else>

                </#if>

            </td>
            <td>
                <#if line.evaluateDeletePower.equals("1")>
                    <a href="#" onclick="javascript:$.perfExam.deletePerfExam('${line.examId?default('')}','PERF_EXAM_TYPE_1')" title="删除考评信息" class="icon-trash"/>
                <#elseif line.revieweDeletePower.equals("1")>
                    <a href="#" onclick="javascript:$.perfExam.deletePerfExam('${line.examId?default('')}','PERF_EXAM_TYPE_2')" title="删除初审信息" class="icon-trash"/>
                <#elseif line.approveDeletePower.equals("1")>
                    <a href="#" onclick="javascript:$.perfExam.deletePerfExam('${line.examId?default('')}','PERF_EXAM_TYPE_3')" title="删除终审信息" class="icon-trash"/>
                <#else>

                </#if>
            </td>
        </tr>
            <#assign rowCount = rowCount + 1>
            <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </tbody>
</table>
<#if data.perfExamList?has_content>
    <@nextPrevAjax targetId="perfExamStats" commonUrl=commonUrl param=param paginateStyle="nav-pager" paginateFirstStyle="nav-first"
    viewIndex=viewIndex highIndex=highIndex listSize=totalCount viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel=""
    paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl=""
    ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel=""
    paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
</#if>
