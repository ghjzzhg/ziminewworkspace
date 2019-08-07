<#include "component://widget/templates/chooser.ftl"/>
<script language="javascript">
    var editorMap = {};
    $(function () {
        templateForPersonalEditor = KindEditor.create('#templateForPersonal', {
            allowFileManager: true,
            afterBlur:function(){this.sync();}
        });
    });
   /* $(function () {
        templateForUnderlingEditor = KindEditor.create('.templateForUnderling', {
            allowFileManager: true,
            afterBlur:function(){this.sync();}
        });
    });*/
    function loadEditor(templateId){
        var name = templateId;
        name = KindEditor.create('.'+templateId, {
            allowFileManager: true
        });
       editorMap[templateId] = name;
    }
    function showCalendar() {
        displayInTab3("underlingWorkLog", "张三的工作日志", {
            requestUrl: "underlingWorkLogByCalendar",
            width: "800px",
            position: 'top'
        });
    }
    function dateClick() {
        displayInTab3("AddMemo", "我的工作日志", {requestUrl: "showWorkLog", width: "800px", height: 700, position: 'top'});
    }
    function saveLogSet() {
        var options = {
            beforeSubmit: function () {
                return $("#logSetForm").validationEngine('validate');
            },
            dataType: "json",
            success: function (data) {
                $("#logSetId").val(data.data);
                showInfo("保存成功")
            },
            url: "saveLogSet", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#logSetForm").ajaxSubmit(options);
    }
    function editPersonalTemplate(positionId) {
        var template = templateForPersonalEditor.html();
        var a=$("#templateForPersonal").val();
        if(a.length<=0){
            showInfo("请输入内容");
            return false;
        }
        $.ajax({
            type:'POST',
            url:"savePersonalTemplate",
            dataType:'json',
            data:{positionId:positionId,template:template},
            success: function (data) {
                showInfo(data.data);
            }
        });
    }
    function editTemplate(positionId,templateId) {
        var template;
        for(var map in editorMap){
            if(templateId == map ){
                  template = editorMap[map].html();
            }
        }
        if(template.length<=0){
            showInfo("请输入内容");
            return false;
        }
        $.ajax({
            type:'POST',
            url:"saveTemplate",
            dataType:'json',
            data:{positionId:positionId,template:template},
            success: function (data) {
                showInfo(data.data);
            }
        });
    }
    function setInput(){
        var inputList = new Array("planText", "logText", "instructionsText");
        var i=0;
        $("input[type='checkbox']").each(function(){
            if(this.checked == true){
                $("input[name='"+inputList[i]+"']").attr("disabled",false);
            } else {
                $("input[name='"+inputList[i]+"']").attr("disabled",true);
            }
            i = i+1;
        });

    }
</script>
<form name="logSetForm" id="logSetForm" class="basic-form">
<#if resultMap?has_content>
    <input type="hidden" name="logSetId" id="logSetId" class="logSetId" value="${resultMap.logSetId}"/>
<#else>
    <input type="hidden" name="logSetId" id="logSetId" class="logSetId"/>
</#if>
    <div>
        <div>
            <div class="screenlet-title-bar">
                <ul>
                    <li class="h3">参数设置</li>

                </ul>
                <br class="clear">
            </div>
            <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
                <tbody>
                <tr>
                    <td>
                        <label>发布部门</label>
                    </td>
                    <td>
                    <@chooser chooserType="LookupDepartment" name="department" importability=true chooserValue="${department.department?default('')}" required=false/>
                        <#--<@htmlTemplate.lookupField value="${department.department}" formName="logSetForm" name="department"-->
                        <#--fieldFormName="LookupDepartment" position="center"  className="validate[required]"/>-->
                    </td>
                </tr>
                <tr>
                    <td class="label">
                        <label for="LogSet_a"
                               id="LogSet_a_title">工作日程:</label>
                    </td>
                    <td>
                    <#if resultMap.planValue?has_content>
                        <input type="checkbox" name="checkboxForPlan" onchange="setInput();" checked="checked" id="LogSet_a">允许提交今日之前最近
                        <input type="text" name="planText" value="${resultMap.planValue}" class="validate[required,custom[onlyNumberSp]]" maxlength="3">天的工作日程
                    <#else>
                        <input type="checkbox" name="checkboxForPlan" onchange="setInput();" id="LogSet_a">允许提交今日之前最近
                        <input type="text" name="planText" disabled="disabled">天的工作日程
                    </#if>

                        <script language="JavaScript"
                                type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                    </td>
                </tr>
                <tr>
                    <td class="label">
                        <label for="LogSet_b"
                               id="LogSet_b_title">日志汇报:</label>
                    </td>
                    <td>
                    <#if resultMap.logValue?has_content>
                        <input type="checkbox" name="checkboxForLog" onchange="setInput();" checked="checked" id="LogSet_b">允许提交今日之后最近
                        <input type="text" name="logText" value="${resultMap.logValue}" class="validate[required,custom[onlyNumberSp]]" maxlength="3">天内的工作日志
                    <#else>
                        <input type="checkbox" name="checkboxForLog" onchange="setInput();" id="LogSet_b">允许提交今日之前最后
                        <input type="text" name="logText" disabled="disabled">天内的工作日志
                    </#if>

                        <script language="JavaScript"
                                type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                    </td>
                </tr>
                <tr>
                    <td class="label">
                        <label for="LogSet_c"
                               id="LogSet_c_title">领导批示:</label>
                    </td>
                    <td class="jqv">
                    <#if resultMap.instructionsValue?has_content>
                        <input type="checkbox" name="checkboxForInstructions" onchange="setInput();" checked="checked" id="LogSet_c">允许批示今日之后最近
                        <input type="text" name="instructionsText" value="${resultMap.instructionsValue}" class="validate[required,custom[onlyNumberSp]]" maxlength="3">天内的工作日志
                    <#else>
                        <input type="checkbox" name="checkboxForInstructions" onchange="setInput();" id="LogSet_c">允许批示今日之前最后
                        <input type="text" name="instructionsText" disabled="disabled">天内的工作日志
                    </#if>
                        <script language="JavaScript"
                                type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                    </td>
                </tr>
                <tr>
                    <td>操作</td>
                    <td><a href="#" onclick="saveLogSet();" type="submit" class="smallSubmit">保存</a></td>
                </tr>
                </tbody>
            </table>
        </div>


        <div style="margin-top: 20px">
            <div class="screenlet-title-bar">
                <ul>
                    <li class="h3">日志模板设置</li>

                </ul>
                <br class="clear">
            </div>
            <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
                <tbody>
                <tr class="header-row-2">
                    <td>
                        <label>序号</label>
                    </td>
                    <td>
                        <label>岗位名称</label>
                    </td>
                    <td>
                        <label>日志模板</label>
                    </td>
                    <td>
                        <label>维护</label>
                    </td>
                </tr>

                <#--<#list resultListForLogin as resultListForLogin>

                <input type="hidden" value="${resultListForLogin.positionId}"/>
                <tr>
                    <td>
                   1
                    </td>
                    <td>
                       ${resultListForLogin.position}
                    </td>
                &lt;#&ndash; <td>
                     运营总监
                 </td>&ndash;&gt;
                    <td  class="jqv">
                        <textarea name="templateForPersonal" id="templateForPersonal" class="person validate[required]" style="width: 100%">${resultListForLogin.template}</textarea>
                    </td>
                    <td>
                        <a href="#" onclick = "editPersonalTemplate('${resultListForLogin.positionId}')" class="smallSubmit">修改</a>
                    </td>
                </tr>
                </#list>-->
                <#assign item = 2>
                <#list resultListForLogin as resultListForTemplate>

                <input type="hidden" value="${resultListForTemplate.positionId}"/>
                <tr>
                    <td>
                    ${item}
                    </td>
                    <td>
                    ${resultListForTemplate.position}
                    </td>
                    <td>
                        <textarea name="templateForUnderling" class="editor_${item}" style="width: 100%">${resultListForTemplate.template}</textarea>
                    </td>
                    <td>
                        <a href="#" onclick="editTemplate('${resultListForTemplate.positionId}','editor_${item}')" class="smallSubmit">修改</a>
                    </td>
                </tr>
                <script>
                    loadEditor('editor_${item}');
                </script>
                    <#assign item = item + 1>
                </#list>

                </tbody>
            </table>
        </div>
    </div>
</form>