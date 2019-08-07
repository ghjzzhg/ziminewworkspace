<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
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
    <#assign evaluateAddScore = perfExamMap.evaluateAddScore?default('')>
    <#assign evaluatePlanScore = perfExamMap.evaluatePlanScore?default('')>
    <#assign previousAdvice = perfExamMap.previousAdvice?default('')>
    <#assign nextAdvice = perfExamMap.nextAdvice?default('')>
    <#assign reviewerAddScore = perfExamMap.reviewerAddScore?default('')>
    <#assign reviewerPlanScore = perfExamMap.reviewerPlanScore?default('')>
    <#assign reviewerRemark = perfExamMap.reviewerRemark?default('')>
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
    $(function() {
        standardSliderInit("add");
        previousAdvice = KindEditor.create('textarea[name="previousAdvice"]', {
            allowFileManager : true
        });
        nextAdvice = KindEditor.create('textarea[name="nextAdvice"]', {
            allowFileManager : true
        });
        remark = KindEditor.create('textarea[name="remark"]', {
            allowFileManager : true
        });
    });
    function sumTotalValue()
    {
        var totalValue = 0, totaldegree = "";
        var totalScore = 20+parseFloat($("#add_score").val());
        $("input.slider_value").each(function(){
            var v = $(this).val();
            if(v){
                totalValue += parseFloat(v);
            }
        })
        $("input.item_score").each(function(){
            var v = $(this).val();
            if(v){
                totalScore += parseFloat(v);
            }
        })
        totalValue = totalValue.toFixed(2);
        $("#totalScore").val(totalValue);
        var a = totalValue/totalScore *100;
        if (a>= 0) {totaldegree="C-";}
        if (a>= 50) {totaldegree="C";}
        if (a>= 60) {totaldegree="B";}
        if (a>= 70) {totaldegree="B+";}
        if (a>= 80) {totaldegree="A";}
        if (a>= 95) {totaldegree="A+";}
        $("#totalDegree").val(totaldegree);
    }
    function standardSliderInit(sliderId) {
        YUI({
            base:'/images/lib/yui/',
            skin: {
                overrides: {
                    'slider-base': [
                        'sam',
                        'sam-dark',
                        'audio-light',
                        'audio'
                    ]
                }
            }
        }).use('slider', function (Y) {
            var initScore = Y.one("#" + sliderId + "_score").get("value"), oldScore = scoreMap[sliderId];
            if(oldScore === undefined){
                oldScore = 0;
            }
            Y.one("#" + sliderId + "_slider").set("value", oldScore);
            var value = initScore? oldScore*100/initScore: 0;
            var slider = new Y.Slider({
                axis: 'x',
                min: 0,
                max: 100,
                value: value ,
                length: '217px'
            });
            slider.render('#' + sliderId);

            slider.after("valueChange", function(e){
                var per = e.newVal/100;
                this.set( "value", per * Y.one("#" + sliderId + "_score").get("value"));
                var remark = Y.one("#" + sliderId + "_remark");
                if(remark){
                    if(per >=0 && per < 0.4){
                        remark.set("value", $('#' + sliderId + '_lv4').val());
                    }else if(per >= 0.4 && per <= 0.6){
                        remark.set("value", $('#' + sliderId + '_lv3').val());
                    }else if(per >= 0.6 && per <= 0.8){
                        remark.set("value", $('#' + sliderId + '_lv2').val());
                    }else if(per >= 0.8){
                        remark.set("value", $('#' + sliderId + '_lv1').val());
                    }
                }
                sumTotalValue();
            }, Y.one( "#" + sliderId + "_slider"));
        });

    }
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
        <input type="hidden" name="examId" value="${examId?default('')}">
        <input type="hidden" name="planId" value="${planId?default('')}">
        <input type="hidden" name="type" value="${type?default('')}">
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
                                            <#if type.equals("PERF_EXAM_TYPE_2")>
                                                <td class="label">
                                                    <label for="reviewerTitle">初审人：</label>
                                                </td>
                                                <td colspan="2">
                                                    <label>${reviewer?default('')}</label>
                                                </td>
                                            <#elseif type.equals("PERF_EXAM_TYPE_3")>
                                                <td class="label">
                                                    <label for="approverTitle">终审人：</label>
                                                </td>
                                                <td colspan="2">
                                                    <label>${approver?default('')}</label>
                                                </td>
                                            <#else >
                                                <td class="label">
                                                    <label for="evaluatorTitle">考评人：</label>
                                                </td>
                                                <td colspan="2">
                                                    <label>${evaluator?default('')}</label>
                                                </td>
                                            </#if>
                                            <td class="label">
                                                <label for="evaluateDocumentTitle"><b class="requiredAsterisk">*</b>考评部门:</label>
                                            </td>
                                            <td colspan="2">
                                                <#if department?has_content>
                                                    <label>${department?default('')}</label>
                                                <#else>
                                                    <@chooser chooserType="LookupDepartment" name="department" importability=true chooserValue="${department?default('')}"/>
                                                    <#--<@htmlTemplate.lookupField value="" formName="perfExamEditForm" name="department" id="department" fieldFormName="LookupDepartment" position="center" className="validate[required]"/>-->
                                                </#if>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="label">
                                                <label for="evaluateMonthTitle">考核月份：</label>
                                            </td>
                                            <td colspan="2">
                                                <label>${evaluateYear?default('')}年${evaluateMonth?default('')}月</label>
                                                <input type="hidden" name="evaluateYear" value="${evaluateYear?default('')}">
                                                <input type="hidden" name="evaluateMonth" value="${evaluateMonth?default('')}">
                                            </td>
                                            <td class="label">
                                                <label for="dateTitle"><b class="requiredAsterisk">*</b>考评日期：</label>
                                            </td>
                                            <td colspan="2" style="margin-right: 28px">
                                                <@htmlTemplate.renderDateTimeField name="date" event="" action="" className="validate[required]" alert="" title="Format: yyyy-MM-dd"
                                                value="${date?default(.now)}" size="25" maxlength="30" id="date" dateType="" shortDateInput=true timeDropdownParamName=""
                                                defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2=""
                                                timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                        <tr>
                        <#if type.equals("PERF_EXAM_TYPE_2")>
                            <td colspan="6">
                                <table class="score-table" cellpadding="0" cellspacing="0" border="1" width="100%" style="border-collapse: collapse">
                                    <tbody>
                                        <tr align="center" bgcolor="#E0E0E0" class="header-row-2">
                                            <td width="8%"><strong>类别</strong></td>
                                            <td width="17%"><strong>考评项目</strong></td>
                                            <td width="5%"><strong>权数</strong></td>
                                            <td width="30%"><strong>满意程度</strong></td>
                                            <td width="8%"><strong>考评人评分</strong></td>
                                            <td width="5%"><strong>分值</strong></td>
                                            <td width="27%"><strong>说明</strong></td>
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
                                                    <input type="hidden" id="${item.itemId}_score" class="item_score" value="${item.score}">
                                                </td>
                                                <td>
                                                    <div id="${item.itemId}" class="score-ruler" name="${item.itemId}" title="${item.title}"></div>
                                                </td>
                                                <input id="${item.itemId}_lv1" type="hidden" value="${item.score1}">
                                                <input id="${item.itemId}_lv2" type="hidden" value="${item.score2}">
                                                <input id="${item.itemId}_lv3" type="hidden" value="${item.score3}">
                                                <input id="${item.itemId}_lv4" type="hidden" value="${item.score4}">
                                                <td>
                                                    <label>${item.evaluateScore?default('')}</label>
                                                </td>
                                                <td>
                                                    <input id="${item.itemId}_slider" class="slider_value" name="value_${item.itemId}" type="text" value="0" size="3" maxlength="3" readonly="" onclick="alert('请通过左边滑块改变分值');">
                                                </td>
                                                <td>
                                                    <input type="text" style="width:100%; border: none; background-color:transparent;" id="${item.itemId}_remark" value="${item.score1}" readonly>
                                                </td>
                                            </tr>
                                            <script type="text/javascript">
                                                standardSliderInit("${item.itemId}");
                                            </script>
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
                                            <input type="hidden" id="add_score" value="5">
                                        </td>
                                        <td>
                                            <div id="add" class="score-ruler" name="add" title="公司奖励/挽救公司损失或财产/其它"></div>
                                        </td>
                                        <td>
                                            <label>${evaluateAddScore?default('')}</label>
                                        </td>
                                        <td>
                                            <input name="addScore" value="" class="slider_value" id="add_slider" type="text" value="0" size="3" maxlength="3" readonly="" onclick="alert('请通过左边滑块改变分值');">
                                        </td>

                                        <td>
                                        </td>
                                    </tr>
                                    <tr height="21">
                                        <td align="center"><b>项目<br>临时工作</b></td>
                                        <td>本月工作计划中纳入的考核内容的完成情况</td>
                                        <td>20</td>
                                        <td></td>
                                        <td>
                                            <label>${evaluatePlanScore?default('')}</label>
                                        </td>
                                        <td><input name="planScore" class="slider_value" id="plan_score" type="text" size="3" value="0" class="validate[required,onlyNumberSp]]"
                                                   onchange="javascript:if(this.value==''){this.value=0;};if(this.value-0>20){alert('最多只允许加分20分！');this.value=20;};if(0-(this.value)>20){alert('最多只允许扣分20分！');this.value='-20';};sumTotalValue();"
                                                   style="ime-mode:disabled;" maxlength="3"
                                                   onkeypress="if (event.keyCode!=46 &amp;&amp; event.keyCode!=45 &amp;&amp; (event.keyCode<48 || event.keyCode>57)) event.returnValue=false"
                                                   onpaste="return false;" ondragenter="return false"></td>
                                        <td><a href="javascript:alert('进入工作计划模块')" target="_blank">点击进入为工作计划内容打分!</a></td>

                                    </tr>
                                    <tr>
                                        <td align="center" colspan="7">合计：
                                            <input name="totalScore" id="totalScore" value="" readonly="" type="text" size="3" style="width:80px; text-align:center;color:red;margin-right: 1em">
                                            <input name="result" id="totalDegree" value="" readonly="" type="text" size="3" style="width:50px; text-align:center;color:red">
                                        </td>
                                    </tr>
                                    </tbody>
                                </table>
                            </td>

                        <#elseif type.equals("PERF_EXAM_TYPE_3")>
                            <td colspan="6">
                                <table class="score-table" cellpadding="0" cellspacing="0" border="1" width="100%" style="border-collapse: collapse">
                                    <tbody>
                                    <tr align="center" bgcolor="#E0E0E0" class="header-row-2">
                                        <td width="7%"><strong>类别</strong></td>
                                        <td width="16%"><strong>考评项目</strong></td>
                                        <td width="5%"><strong>权数</strong></td>
                                        <td width="30%"><strong>满意程度</strong></td>
                                        <td width="5%"><strong>考评人评分</strong></td>
                                        <td width="5%"><strong>初审人评分</strong></td>
                                        <td width="5%"><strong>分值</strong></td>
                                        <td width="27%"><strong>说明</strong></td>
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
                                                    <input type="hidden" id="${item.itemId}_score" class="item_score" value="${item.score}">
                                                </td>
                                                <td>
                                                    <div id="${item.itemId}" class="score-ruler" name="${item.itemId}" title="${item.title}"></div>
                                                </td>
                                                <input id="${item.itemId}_lv1" type="hidden" value="${item.score1}">
                                                <input id="${item.itemId}_lv2" type="hidden" value="${item.score2}">
                                                <input id="${item.itemId}_lv3" type="hidden" value="${item.score3}">
                                                <input id="${item.itemId}_lv4" type="hidden" value="${item.score4}">
                                                <td>
                                                    <label>${item.evaluateScore?default('')}</label>
                                                </td>
                                                <td>
                                                    <label>${item.reviewerScore?default('')}</label>
                                                </td>
                                                <td>
                                                    <input id="${item.itemId}_slider" class="slider_value" name="value_${item.itemId}" type="text" value="0" size="3" maxlength="3" readonly="" onclick="alert('请通过左边滑块改变分值');">
                                                </td>
                                                <td>
                                                    <input type="text" style="width:100%; border: none; background-color:transparent;" id="${item.itemId}_remark" value="${item.score1}" readonly>
                                                </td>
                                            </tr>
                                            <script type="text/javascript">
                                                standardSliderInit("${item.itemId}");
                                            </script>
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
                                            <input type="hidden" id="add_score" value="5">
                                        </td>
                                        <td>
                                            <div id="add" class="score-ruler" name="add" title="公司奖励/挽救公司损失或财产/其它"></div>
                                        </td>
                                        <td>
                                            <label>${evaluateAddScore?default('')}</label>
                                        </td>
                                        <td>
                                            <label>${reviewerAddScore?default('')}</label>
                                        </td>
                                        <td>
                                            <input name="addScore" value="" class="slider_value" id="add_slider" type="text" value="0" size="3" maxlength="3" readonly="" onclick="alert('请通过左边滑块改变分值');">
                                        </td>

                                        <td>
                                        </td>
                                    </tr>
                                    <tr height="21">
                                        <td align="center"><b>项目<br>临时工作</b></td>
                                        <td>本月工作计划中纳入的考核内容的完成情况</td>
                                        <td>20</td>
                                        <td>&nbsp;</td>
                                        <td>
                                            <label>${evaluatePlanScore?default('')}</label>
                                        </td>
                                        <td>
                                            <label>${reviewerPlanScore?default('')}</label>
                                        </td>
                                        <td><input name="planScore" class="slider_value" id="plan_score" type="text" size="3" value="0" class="validate[required,onlyNumberSp]]"
                                                   onchange="javascript:if(this.value==''){this.value=0;};if(this.value-0>20){alert('最多只允许加分20分！');this.value=20;};if(0-(this.value)>20){alert('最多只允许扣分20分！');this.value='-20';};sumTotalValue();"
                                                   style="ime-mode:disabled;" maxlength="3"
                                                   onkeypress="if (event.keyCode!=46 &amp;&amp; event.keyCode!=45 &amp;&amp; (event.keyCode<48 || event.keyCode>57)) event.returnValue=false"
                                                   onpaste="return false;" ondragenter="return false"></td>
                                        <td><a href="javascript:alert('进入工作计划模块')" target="_blank">点击进入为工作计划内容打分!</a></td>

                                    </tr>
                                    <tr>
                                        <td align="center" colspan="7">合计：
                                            <input name="totalScore" id="totalScore" value="" readonly="" type="text" size="3" style="width:80px; text-align:center;color:red;margin-right: 1em">
                                            <input name="result" id="totalDegree" value="" readonly="" type="text" size="3" style="width:50px; text-align:center;color:red">
                                        </td>
                                    </tr>
                                    </tbody>
                                </table>
                            </td>
                        <#else >
                            <td colspan="6">
                                <table class="score-table" cellpadding="0" cellspacing="0" border="1" width="100%"
                                       style="border-collapse: collapse">
                                    <tbody>
                                    <tr align="center" bgcolor="#E0E0E0" class="header-row-2">
                                        <td width="10%"><strong>类别</strong></td>
                                        <td width="20%"><strong>考评项目</strong></td>
                                        <td width="5%"><strong>权数</strong></td>
                                        <td width="30%"><strong>满意程度</strong></td>
                                        <td width="5%"><strong>分值</strong></td>
                                        <td width="27%"><strong>说明</strong></td>
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
                                                    <input type="hidden" id="${item.itemId}_score" class="item_score" value="${item.score}">
                                                </td>
                                                <td>
                                                    <div id="${item.itemId}" class="score-ruler" name="${item.itemId}" title="${item.title}"></div>
                                                </td>
                                                <input id="${item.itemId}_lv1" type="hidden" value="${item.score1}">
                                                <input id="${item.itemId}_lv2" type="hidden" value="${item.score2}">
                                                <input id="${item.itemId}_lv3" type="hidden" value="${item.score3}">
                                                <input id="${item.itemId}_lv4" type="hidden" value="${item.score4}">
                                                <td>
                                                    <input id="${item.itemId}_slider" class="slider_value" name="value_${item.itemId}" type="text" value="0" size="3" maxlength="3" readonly="" onclick="alert('请通过左边滑块改变分值');">
                                                </td>
                                                <td>
                                                    <input type="text" style="width:100%; border: none; background-color:transparent;" id="${item.itemId}_remark" value="${item.score1}" readonly>
                                                </td>
                                            </tr>
                                            <script type="text/javascript">
                                                standardSliderInit("${item.itemId}");
                                            </script>
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
                                            <input type="hidden" id="add_score" value="5">
                                        </td>
                                        <td>
                                            <div id="add" class="score-ruler" name="add" title="公司奖励/挽救公司损失或财产/其它"></div>
                                        </td>
                                        <td>
                                            <input name="addScore" value="" class="slider_value" id="add_slider" type="text" value="0" size="3" maxlength="3" readonly="" onclick="alert('请通过左边滑块改变分值');">
                                        </td>
                                        <td>
                                        </td>
                                    </tr>
                                    <tr height="21">
                                        <td align="center"><b>项目<br>临时工作</b></td>
                                        <td>本月工作计划中纳入的考核内容的完成情况</td>
                                        <td>20</td>
                                        <td></td>
                                        <td><input name="planScore" class="slider_value" id="plan_score" type="text" size="3" value="0" class="validate[required,onlyNumberSp]]"
                                                   onchange="javascript:if(this.value==''){this.value=0;};if(this.value-0>20){alert('最多只允许加分20分！');this.value=20;};if(0-(this.value)>20){alert('最多只允许扣分20分！');this.value='-20';};sumTotalValue();"
                                                   style="ime-mode:disabled;" maxlength="3"
                                                   onkeypress="if (event.keyCode!=46 &amp;&amp; event.keyCode!=45 &amp;&amp; (event.keyCode<48 || event.keyCode>57)) event.returnValue=false"
                                                   onpaste="return false;" ondragenter="return false"></td>
                                        <td><a href="javascript:alert('进入工作计划模块')" target="_blank">点击进入为工作计划内容打分!</a></td>

                                    </tr>
                                    <tr>
                                        <td align="center" colspan="6">合计：
                                            <input name="totalScore" id="totalScore" value="" readonly="" type="text" size="3" style="width:80px; text-align:center;color:red;margin-right: 1em">
                                            <input name="result" id="totalDegree" value="" readonly="" type="text" size="3" style="width:50px; text-align:center;color:red">
                                        </td>
                                    </tr>
                                    </tbody>
                                </table>
                            </td>
                        </#if>

                        </tr>
                        <tr>
                            <td colspan="6">
                                <b><b class="requiredAsterisk">*</b>前期工作计划及建设性建议：</b>(<font color="blue">提示：你可以选择以下图标中的
                                <span style="display: inline-block" class="ke-toolbar-icon ke-toolbar-icon-url ke-icon-insertfile"></span>直接上传附件！</font>)<span style="font-size: 2pt"><br></span>
                                <#if type.equals("PERF_EXAM_TYPE_1")>
                                    <textarea name="previousAdvice" id="previousAdvice_title" style="width:90%" rows="6"></textarea>
                                    <#else >
                                        <div id="previousAdvice1" ></div>
                                        <script>
                                            $("#previousAdvice1").append("<label>"+ unescapeHtmlText('${previousAdvice?default('')}') +"</label>");
                                        </script>
                                </#if>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="6">
                                <b><b class="requiredAsterisk">*</b>下期工作计划及建设性建议：</b>(<font color="blue">提示：你可以选择以下图标中的
                                <span style="display: inline-block" class="ke-toolbar-icon ke-toolbar-icon-url ke-icon-insertfile"></span>直接上传附件！</font>)<span style="font-size: 2pt"><br></span>
                            <#if type.equals("PERF_EXAM_TYPE_1")>
                                <textarea name="nextAdvice" id="nextAdvice_title" style="width:90%" rows="6"></textarea>
                            <#else >
                                <div id="nextAdvice1" ></div>
                                <script>
                                    $("#nextAdvice1").append("<label>"+ unescapeHtmlText('${nextAdvice?default('')}') +"</label>");
                                </script>
                            </#if>
                            </td>
                        </tr>
                        <#if type.equals("PERF_EXAM_TYPE_2")>
                        <tr>
                            <td colspan="6"><b>初审评语:</b><br/>
                                <textarea name="remark" style="width:90%" id="remarkTitle"></textarea>
                            </td>
                        </tr>
                        <#elseif type.equals("PERF_EXAM_TYPE_3")>
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
                                <textarea name="remark" style="width:90%" id="remarkTitle"></textarea>
                            </td>
                        </tr>
                        </#if>
                        <tr>
                            <td colspan="7" align="center"><span style="font-size: 3pt"><br></span>
                                <#if type.equals("PERF_EXAM_TYPE_2")>
                                    <input type="button" onclick="$.perfExam.savePerfExam('${examId?default('')}','PERF_EXAM_STATE_2')" value="审核通过" name="button1">&nbsp;&nbsp;
                                    <input type="button" onclick="$.perfExam.rejectPerfExam('${examId?default('')}','PERF_EXAM_STATE_3')" value="驳回" name="button2">&nbsp;&nbsp;
                                <#elseif type.equals("PERF_EXAM_TYPE_3")>
                                    <input type="button" onclick="$.perfExam.savePerfExam('${examId?default('')}','PERF_EXAM_STATE_4')" value="审核通过" name="button1">&nbsp;&nbsp;
                                    <input type="button" onclick="$.perfExam.rejectPerfExam('${examId?default('')}','PERF_EXAM_STATE_5')" value="驳回" name="button2">&nbsp;&nbsp;
                                <#else>
                                    <input type="button" onclick="$.perfExam.savePerfExams('${examId?default('')}')" value="提交考评" name="button1">&nbsp;&nbsp;
                                    <input type="button" onclick="closeCurrentTab()" value="关闭" >
                                </#if>

                                <br><br></td>
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
