<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#if perfExamMap?has_content>
    <#assign examId = perfExamMap.examId?default('')>
    <#assign planId = perfExamMap.planId?default('')>
    <#assign type = perfExamMap.type?default('')>
    <#assign perfExamItemsList = perfExamMap.perfExamItemsList?default('')>
    <#assign evaluator = perfExamMap.evaluator?default('')>
    <#assign reviewer = perfExamMap.reviewer?default('')>
    <#assign approver = perfExamMap.approver?default('')>
    <#assign evaluateYear = perfExamMap.evaluateYear?default('')>
    <#assign evaluateMonth = perfExamMap.evaluateMonth?default('')>
    <#assign fullName = perfExamMap.fullName?default('')>
    <#assign workerSn = perfExamMap.workerSn?default('')>
    <#assign departmentName = perfExamMap.departmentName?default('')>
    <#assign occupationName = perfExamMap.occupationName?default('')>
    <#assign description = perfExamMap.description?default('')>
    <#assign typeCountMap = perfExamMap.typeCountMap?default('')>
    <#assign department = perfExamMap.department?default('')>
    <#assign evaluaterAddScore = perfExamMap.evaluaterAddScore?default('')>
    <#assign evaluaterPlanScore = perfExamMap.evaluaterPlanScore?default('')>
    <#assign previousAdvice = perfExamMap.previousAdvice?default('')>
    <#assign nextAdvice = perfExamMap.nextAdvice?default('')>
    <#assign reviewerAddScore = perfExamMap.reviewerAddScore?default('')>
    <#assign reviewerPlanScore = perfExamMap.reviewerPlanScore?default('')>
    <#assign reviewerRemark = perfExamMap.reviewerRemark?default('')>
    <#assign finalizer = perfExamMap.finalizer?default('')>
    <#assign evaluaterDate = perfExamMap.evaluaterDate?default('')>
    <#assign reviewerDate = perfExamMap.reviewerDate?default('')>
    <#assign approverDate = perfExamMap.approverDate?default('')>
    <#assign approverAddScore = perfExamMap.approverAddScore?default('')>
    <#assign approverPlanScore = perfExamMap.approverPlanScore?default('')>
    <#assign approverRemark = perfExamMap.approverRemark?default('')>
    <#assign evaluaterTotalScore = perfExamMap.evaluaterTotalScore?default('')>
    <#assign reviewerTotalScore = perfExamMap.reviewerTotalScore?default('')>
    <#assign approverTotalScore = perfExamMap.approverTotalScore?default('')>
    <#assign evaluaterResult = perfExamMap.evaluaterResult?default('')>
    <#assign reviewerResult = perfExamMap.reviewerResult?default('')>
    <#assign approverResult = perfExamMap.approverResult?default('')>
    <#assign typeTotalScore = perfExamMap.typeTotalScore?default('')>
</#if>
<style type="text/css">
    .yui3-skin-sam .yui3-slider-x .yui3-slider-rail,
    .yui3-skin-sam .yui3-slider-x .yui3-slider-rail-cap-left,
    .yui3-skin-sam .yui3-slider-x .yui3-slider-rail-cap-right {
        background:transparent;
    }
    .score-ruler{
        background: url(/images/lib/yui/slider-base/assets/skins/rule.png) no-repeat;
        border-top: 1px solid;
    }
    .score-table td{
        border: 1px solid lightgrey;
    }
</style>
<script language="javascript">
    var scoreMap = {
    };
</script>
<div class="yui3-skin-sam">
    <div>
        <table align="center" cellpadding="0" cellspacing="0" border="0" width="100%" height="50px">
            <tbody>
            <tr>
                <td valign="top"><img src="/images/rextec_logo.png" width="140px" height="45px" alt="瑞克斯信息科技有限公司">
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <form name="perfExamEditForm" id="perfExamEditForm">
        <div class="yui3-skin-audio-light">
            <div style="text-align: center; letter-spacing: 20px; margin:0 auto; font-weight: bold;font-size: 3em;margin-bottom: 0.5em;">员工绩效考核表</div>
            <div>
                <table border="0" cellspacing="0" cellpadding="0" align="center" style="margin: 0.5em auto">
                    <tbody>
                    <tr>
                        <td align="center">
                            <b style="margin-left: 1em;">考评类别：</b>
                            <label>${description?default('')}</label>
                        </td>
                    </tr>
                    </tbody>
                </table>
                <div style="border:1px solid; height: 500px;overflow-y: auto">
                    <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
                        <tbody>
                        <tr>
                            <td colspan="6">
                                <table cellpadding="0" cellspacing="0" border="1" width="100%" style="border-collapse: collapse">
                                    <tbody>
                                    <tr>
                                        <td class="label">
                                            <label for="fullNameTitle">员工姓名：</label>
                                        </td>
                                        <td colspan="2">
                                            <label>${fullName?default('')}</label>
                                        </td>
                                        <td class="label">
                                            <label for="workerSnTitle">员工工号：</label>
                                        </td>
                                        <td colspan="2">
                                            <label>${workerSn?default('')}</label>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="label">
                                            <label for="documentTitle">员工部门：</label>
                                        </td>
                                        <td colspan="2">
                                            <label>${departmentName?default('')}</label>
                                        </td>
                                        <td class="label">
                                            <label for="occupationTitle">员工岗位：</label>
                                        </td>
                                        <td colspan="2">
                                            <label>${occupationName?default('')}</label>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="label">
                                            <label for="evaluateMonthTitle">考核月份：</label>
                                        </td>
                                        <td colspan="2">
                                            <label>${evaluateYear?default('')}年${evaluateMonth?default('')}月</label>
                                        </td>
                                        <td class="label">
                                            <label for="evaluateDocumentTitle">考评部门：</label>
                                        </td>
                                        <td colspan="2">
                                            <label>${department?default('')}</label>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="label">
                                            <label for="evaluatorTitle">考评人：</label>
                                        </td>
                                        <td colspan="2">
                                            <label>${evaluator?default('')}</label>
                                        </td>、
                                        <td class="label">
                                            <label for="evaluaterDateTitle">考评日期：</label>
                                        </td>
                                        <td>
                                            <label>${evaluaterDate?default('')}</label>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="label">
                                            <label for="reviewerTitle">初审人：</label>
                                        </td>
                                        <td colspan="2">
                                            <label>${reviewer?default('')}</label>
                                        </td>
                                        <td class="label">
                                            <label for="reviewerDateTitle">初审日期：</label>
                                        </td>
                                        <td colspan="2">
                                            <label>${reviewerDate?default('')}</label>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="label">
                                            <label for="approverTitle">终审人：</label>
                                        </td>
                                        <td colspan="2">
                                            <label>${approver?default('')}</label>
                                        </td>
                                        <td class="label">
                                            <label for="approverDateTitle">终审日期：</label>
                                        </td>
                                        <td colspan="2">
                                            <label>${approverDate?default('')}</label>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="label">
                                            <label for="finalizerTitle">归档人：</label>
                                        </td>
                                        <td colspan="2">
                                            <label>${finalizer?default('')}</label>
                                        </td>、
                                        <td class="label">
                                            <label for="finalizerDate">归档日期：</label>
                                        </td>
                                        <td>
                                            <label>${finalizerDate?default('')}</label>
                                        </td>
                                    </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="8">
                                <table class="score-table" cellpadding="0" cellspacing="0" border="1" width="100%" style="border-collapse: collapse">
                                    <tbody>
                                    <tr align="center" bgcolor="#E0E0E0" class="header-row-2">
                                        <td width="10%"><strong>类别</strong></td>
                                        <td width="50%"><strong>考评项目</strong></td>
                                        <td width="10%"><strong>权数</strong></td>
                                        <td width="10%"><strong>考评人评分</strong></td>
                                        <td width="10%"><strong>初审人评分</strong></td>
                                        <td width="10%"><strong>终审人评分</strong></td>
                                    </tr>
                                    </tbody>
                                    <tbody id="perfExamItemsContainer">
                                        <#if perfExamItemsList?has_content>
                                            <#assign lastType=""/>
                                            <#list perfExamItemsList as item>
                                                <#assign rowSpan = typeCountMap[item.typeId]?default("1")/>
                                            <tr>
                                                <#if !lastType?has_content || item.typeId != lastType>
                                                    <td align="center" rowspan="${rowSpan}">
                                                        <b>${delegator.findOne("TblPerfExamItemType", {"typeId" : item.typeId}, true).get("description")}</b>
                                                    </td>
                                                </#if>
                                                <td>${item.title}</td>
                                                <td align="center">
                                                ${item.score}
                                                </td>
                                                <td align="center">
                                                    <label>${item.evaluateScore?default('')}</label>
                                                </td>
                                                <td align="center">
                                                    <label>${item.reviewerScore?default('')}</label>
                                                </td>
                                                <td align="center">
                                                    <label>${item.approverScore?default('')}</label>
                                                </td>
                                            </tr>
                                                <#assign lastType=item.typeId/>
                                            </#list>
                                        </#if>
                                    </tbody>
                                    <tbody>
                                    <tr height="21">
                                        <td align="center">
                                            <b>加分项目</b>
                                        </td>
                                        <td>公司奖励/挽救公司损失或财产/其它</td>
                                        <td align="center">5
                                        </td>
                                        <td align="center">
                                            <label>${evaluaterAddScore?default('')}</label>
                                        </td>
                                        <td align="center">
                                            <label>${reviewerAddScore?default('')}</label>
                                        </td>
                                        <td align="center">
                                            <label>${approverAddScore?default('')}</label>
                                        </td>
                                    </tr>
                                    <tr height="21">
                                        <td align="center"><b>项目<br>临时工作</b></td>
                                        <td>本月工作计划中纳入的考核内容的完成情况</td>
                                        <td align="center" >20</td>
                                        <td align="center">
                                            <label>${evaluaterPlanScore?default('')}</label>
                                        </td>
                                        <td align="center">
                                            <label>${reviewerPlanScore?default('')}</label>
                                        </td>
                                        <td align="center">
                                            <label>${approverPlanScore?default('')}</label>
                                        </td>

                                    </tr>
                                    <tr height="21">
                                        <td align="center"><b>总分</b></td>
                                        <td>以上项目的总分</td>
                                        <td align="center" >${typeTotalScore?default('')}</td>
                                        <td align="center">
                                            <label>${evaluaterTotalScore?default('')}</label>
                                        </td>
                                        <td align="center">
                                            <label>${reviewerTotalScore?default('')}</label>
                                        </td>
                                        <td align="center">
                                            <label>${approverTotalScore?default('')}</label>
                                        </td>

                                    </tr>
                                    <tr height="21">
                                        <td align="center"><b>等级</b></td>
                                        <td>绩效考核的等级</td>
                                        <td align="center">C&nbsp;&nbsp;C-<br>B&nbsp;&nbsp;B+<br>A&nbsp;&nbsp;A+<br>
                                        </td>
                                        <td align="center">
                                            <label>${evaluaterResult?default('')}</label>
                                        </td>
                                        <td align="center">
                                            <label>${reviewerResult?default('')}</label>
                                        </td>
                                        <td align="center">
                                            <label>${approverResult?default('')}</label>
                                        </td>

                                    </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="6">
                                <b>前期工作计划及建设性建议：</b>(<font color="blue">提示：你可以选择以下图标中的
                                <span style="display: inline-block" class="ke-toolbar-icon ke-toolbar-icon-url ke-icon-insertfile"></span>直接上传附件！</font>)<span style="font-size: 2pt"><br></span>
                                <div id="previousAdvice1" ></div>
                                <script>
                                    $("#previousAdvice1").append("<label>"+ unescapeHtmlText('${previousAdvice?default('')}') +"</label>");
                                </script>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="6">
                                <b>下期工作计划及建设性建议：</b>(<font color="blue">提示：你可以选择以下图标中的<span style="display: inline-block" class="ke-toolbar-icon ke-toolbar-icon-url ke-icon-insertfile"></span>直接上传附件！</font>)<span style="font-size: 2pt"><br></span>
                                <div id="nextAdvice1" ></div>
                                <script>
                                    $("#nextAdvice1").append("<label>"+ unescapeHtmlText('${nextAdvice?default('')}') +"</label>");
                                </script>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="6"><b>初审评语:</b><br/>
                                <div id="reviewerRemark" ></div>
                                <script>
                                    $("#reviewerRemark").append("<label>"+ unescapeHtmlText('${reviewerRemark?default('')}') +"</label>");
                                </script>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="6"><b>终审评语:</b><br/>
                                <div id="approverRemark" ></div>
                                <script>
                                    $("#approverRemark").append("<label>"+ unescapeHtmlText('${approverRemark?default('')}') +"</label>");
                                </script>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </form>
    <div>
        注：等级采用比例分配法，等级为"优"的原则上不超过部门员工总数的10%，按部门评分顺序依次确定。
    </div>
</div>
