<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://widget/templates/chooser.ftl"/>
<style type="text/css">
    /* The rail and end cap images are shared in a sprite.
     * Include the id of the slider container to increase css specificity */
    .yui3-skin-sam .yui3-slider-x .yui3-slider-rail,
    .yui3-skin-sam .yui3-slider-x .yui3-slider-rail-cap-left,
    .yui3-skin-sam .yui3-slider-x .yui3-slider-rail-cap-right {
        /*background-image: url(/images/lib/yui/slider-base/assets/skins/rule.png);*/
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
        <#if exam.examId?has_content>
            <#assign scoreList = delegator.findByAnd("TblPerfExamScore", {"examId": exam.examId}, null, false)/>
            <#if scoreList?has_content>
            <#list scoreList as score>
            <#if score_index != 0>,</#if>
            '${score.itemId}': ${score.score}
            </#list>
            </#if>
        </#if>
    };

    $(function() {

        standardSliderInit("add");

        <#if exam.perfExamType?has_content>
        $.perfExam.getPerfExamItems('${exam.perfExamType}');
        </#if>
        editor1 = KindEditor.create('textarea[name="previousAdvice"]', {
            allowFileManager : true
        });
        editor2 = KindEditor.create('textarea[name="nextAdvice"]', {
            allowFileManager : true
        });
    });


    function sumTotalValue()
    {
        var totalValue = 0, totaldegree = "";
        $("input.slider_value").each(function(){
            var v = $(this).val();
            if(v){
                totalValue += parseFloat(v);
            }
        })
        totalValue = totalValue.toFixed(2);
        $("#totalscore").val(totalValue);
        if (totalValue >= 0) {totaldegree="C-";}
        if (totalValue >= 50) {totaldegree="C";}
        if (totalValue >= 60) {totaldegree="B";}
        if (totalValue >= 70) {totaldegree="B+";}
        if (totalValue >= 80) {totaldegree="A";}
        if (totalValue >= 95) {totaldegree="A+";}
        $("#totaldegree").val(totaldegree);
    }


    function standardSliderInit(sliderId) {

        YUI({
            base:'/images/lib/yui/',
            skin: {
                overrides: {
                    'slider-base': [
                        'sam',          // The default skin
                        'sam-dark',     // Suited for dark backgrounds
                        'audio-light',
                        'audio'
                    ]
                }
            }
        }).use('slider', function (Y) {
            // Slider is available and ready for use. Add implementation
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
                <td valign="top"><img
                        src="/images/rextec_logo.png" width="140px" height="45px" alt="瑞克斯信息科技有限公司">
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <form name="perfExamEditForm" id="perfExamEditForm">
    <div class="yui3-skin-audio-light">
        <div style="text-align: center; letter-spacing: 20px; margin:0 auto; font-weight: bold;font-size: 3em;margin-bottom: 0.5em;">
            员工绩效考核表
        </div>
        <div>
            <table border="0" cellspacing="0" cellpadding="0" align="center" style="margin: 0.5em auto">
                <tbody><tr>
                    <td align="center">
                        <div style="display: inline-block;"><b>考评部门:</b></div>
                        <div style="display: inline-block;">
                        <@chooser chooserType="LookupDepartment" name="department" importability=true chooserValue="${exam.department?default('')}"/>
                        <#--<@htmlTemplate.lookupField value="${exam.department?default('')}" formName="perfExamEditForm" name="department" id="department" fieldFormName="LookupDepartment" position="center"/>-->
                        </div>
                        <b style="margin-left: 1em;">考评类别：</b>
                        <select name="perfExamType" onchange="$.perfExam.getPerfExamItems(this.value)">
                        <option value="">请选择</option>
                            <#if perfExamTypes?has_content>
                                <#list perfExamTypes as perfExamType>
                                    <option value="${perfExamType.typeId}" <#if exam.perfExamType?default('') == perfExamType.typeId>selected</#if>>${perfExamType.description}</option>
                                </#list>
                            </#if>
                    </select>
                    </td>
                </tr>
                </tbody>
            </table>
            <div style="border:1px solid; height: 500px;overflow-y: auto">
                <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
                <tbody>
                <tr>
                    <td colspan="6">
                        <table cellpadding="0" cellspacing="0" border="1" width="100%"
                               style="border-collapse: collapse">
                            <tbody>
                            <tr>
                                <td width="90" bgcolor="#E0E0E0">　<b>员工姓名：</b></td>
                                <td width="210">
                                    <@htmlTemplate.lookupField value="${exam.staff?default('')}" formName="perfExamEditForm" name="staff" id="partyId" fieldFormName="LookupStaff" position="center"/>
                                </td>
                                <td width="90" bgcolor="#E0E0E0">　<b>员工职务：</b></td>
                                <td width="210">&nbsp;
                                </td>
                            </tr>
                            <tr>
                                <td bgcolor="#E0E0E0">　<b>员工部门：</b></td>
                                <td>&nbsp;</td>
                                <td bgcolor="#E0E0E0">　<b>考&nbsp;评&nbsp;人：</b></td>
                                <td>
                                    <#if exam.evaluator?has_content>
                                        <#assign evaluator=delegator.findOne("Person", {"partyId": exam.evaluator},true)/>
                                        ${evaluator.get("fullName")?default(evaluator.get("lastName")?default('') + evaluator.get("firstName")?default(''))}
                                        <input type="hidden" name="evaluator" value="${exam.evaluator}">
                                    <#else>
                                        ${evaluatorName}
                                        <input type="hidden" name="evaluator" value="${userLogin.partyId}">
                                    </#if>
                                </td>
                            </tr>
                            <tr>
                                <td bgcolor="#E0E0E0">　<b>考核月份：</b></td>
                                <td>
                                    &nbsp;<select name="evaluateYear">
                                    <#list years as year>
                                        <option value="${year}" <#if exam.evaluateYear?default('') == year>selected</#if>>${year}</option>
                                    </#list>
                                </select>年
                                    <select name="evaluateMonth">
                                        <#assign evaluateMonth = monthNow/>
                                            <#if exam.evaluateMonth?has_content>
                                                <#assign evaluateMonth = exam.evaluateMonth/>
                                            </#if>
                                        <#list 1..12 as month>
                                            <option value="${month-1}" <#if evaluateMonth == month - 1>selected</#if>>${month}</option>
                                        </#list>
                                    </select>月

                                </td>
                                <td bgcolor="#E0E0E0">　<b>考评日期：</b></td>
                                <td>&nbsp;${exam.evaluateDate?default(evaluateDate)}<input type="hidden" name="evaluateDate" value="${exam.evaluateDate?default(evaluateDate)}"></td>
                            </tr>
                            <tr>
                                <td>
                                    测试范围
                                </td>
                                <td>
                                    <#if exam?has_content>
                                        <#assign viewScopeDataId = exam.examId?default('')/>
                                    <#else>
                                        <#assign viewScopeDataId = ''/>
                                    </#if>
                                    <@dataScope id="viewScope" name="viewScope" dataId= "${viewScopeDataId}"/>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr>
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
                                    <input name="addScore" value="${exam.addScore?default('')}" class="slider_value" id="add_slider" type="text" value="0" size="3" maxlength="3" readonly=""
                                           onclick="alert('请通过左边滑块改变分值');">
                                </td>
                                <td>
                                </td>
                            </tr>
                            <tr height="21">
                                <td align="center"><b>项目<br>临时工作</b></td>
                                <td>本月工作计划中纳入的考核内容的完成情况</td>
                                <td>20</td>
                                <td>&nbsp;</td>

                                <td><input name="planScore" class="slider_value" id="plan_score" type="text" size="3" value="${exam.planScore?default(0)}"
                                           onchange="javascript:if(this.value==''){this.value=0;};if(this.value-0>20){alert('最多只允许加分20分！');this.value=20;};if(0-(this.value)>20){alert('最多只允许扣分20分！');this.value='-20';};sumTotalValue();"
                                           style="ime-mode:disabled;" maxlength="3"
                                           onkeypress="if (event.keyCode!=46 &amp;&amp; event.keyCode!=45 &amp;&amp; (event.keyCode<48 || event.keyCode>57)) event.returnValue=false"
                                           onpaste="return false;" ondragenter="return false"></td>
                                <td><a href="javascript:alert('进入工作计划模块')" target="_blank">点击进入为工作计划内容打分!</a></td>

                            </tr>
                            <tr>
                                <td align="center" colspan="6">合计：
                                    <input name="totalscore" id="totalscore" value="${exam.score1?default('')}" readonly="" type="text" size="3"
                                           style="width:80px; text-align:center;color:red;margin-right: 1em">
                                    <input name="result" id="totaldegree" value="${exam.result?default('')}" readonly=""
                                                                                                type="text" size="3"
                                                                                                style="width:50px; text-align:center;color:red">
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>



                <tr>
                <#if exam.examId?has_content>
                    <#assign advice = delegator.findOne("TblPerfExamAdvice", {"examId": exam.examId}, true)/>
                </#if>
                    <td colspan="6">
                        <b>前期工作计划及建设性建议：</b>(<font color="blue">提示：你可以选择以下图标中的
                        <span style="display: inline-block" class="ke-toolbar-icon ke-toolbar-icon-url ke-icon-insertfile"></span>直接上传附件！</font>)<span style="font-size: 2pt"><br></span>
                        <textarea name="previousAdvice" id="content1" style="width:90%" rows="6">
                            <#if advice?has_content>
                                ${advice.previousAdvice?default('')}
                            </#if>
                        </textarea>
                    </td>
                </tr>
                <tr>
                    <td colspan="6">
                        <b>下期工作计划及建设性建议：</b>(<font color="blue">提示：你可以选择以下图标中的
                        <span style="display: inline-block" class="ke-toolbar-icon ke-toolbar-icon-url ke-icon-insertfile"></span>直接上传附件！</font>)<span style="font-size: 2pt"><br>
    </span>
                        <textarea name="nextAdvice" id="content2" style="width:90%" rows="6">
                            <#if advice?has_content>
                                ${advice.nextAdvice?default('')}
                            </#if>
                        </textarea>
                    </td>
                </tr>

                <tr>
                    <td colspan="6"><b>评语</b><br/><input type="hidden" name="num" value="0">
                        <textarea name="remark"
                                                                                                        rows="4"
                                                                                                        style="width:90%;"
                                                                                                        id="kpremark">
                            ${exam.remark?default('')}
                        </textarea>
                    </td>
                </tr>
                <tr>
                    <td colspan="7" align="center"><span style="font-size: 3pt"><br></span>
                        <#assign submitName="提交考评"/>
                        <#assign submitType=""/>
                        <#if exam.evaluateStatus?has_content>
                        <#if exam.evaluateStatus == 'PERF_EXAM_PENDING_REVIEW'>
                            <#assign submitName="提交初审"/>
                            <#assign submitType="review"/>
                         <#elseif exam.evaluateStatus == 'PERF_EXAM_PENDING_APPROVE' >
                             <#assign submitName="提交终审"/>
                             <#assign submitType="approve"/>
                         <#elseif exam.evaluateStatus == 'PERF_EXAM_PENDING_RECORD' >
                            <#assign submitName="归档"/>
                            <#assign submitType="record"/>
                        </#if>
                        </#if>
                        <input type="button" onclick="$.perfExam.savePerfExam('${exam.examId?default('')}', '${submitType}')" value="${submitName}" name="button1">&nbsp;&nbsp;
                        <input type="button" value="打印绩效">&nbsp;&nbsp;
                        <input type="button" onclick="closeCurrentTab()" value="关闭" >
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