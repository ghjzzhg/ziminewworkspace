<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#assign selectedForm=""/>
<#if taskForm?has_content>
    <#assign selectedForm="${taskForm['formId']}"/>
</#if>
<form>
    <div class='panel panel-default'>
        <div class='panel-heading'>任务表单</div>
        <div class='panel-body'>
            <div class='form-group'>
                <select class='form-control' id='taskForm' procDefId='${procDefId}' taskId='${taskId}' formId='${selectedForm}'>
                    <option value=''>无</option>
                    <#if forms?has_content>
                        <#list forms as form>
                            <option value='${form["formId"]?default("")}' <#if form["formId"]?default("") == selectedForm?default("")>selected</#if>>${form["formName"]?default("")}(${form["formUrl"]?default("自定义表单")})</option>
                        </#list>
                    </#if>
                </select>
            </div>
            <div class='form-group'><input type='button' class='btn btn-primary' value='保存'
                                           onclick='ProcessDiagramGenerator.setTaskForm()'/><input type='button'
                                                                                                   style='margin-left:10px'
                                                                                                   class='btn btn-cancel'
                                                                                                   value='清除'
                                                                                                   onclick='ProcessDiagramGenerator.cancelTaskForm()'/>
            </div>
        </div>
    </div>
    <div class='panel panel-default'>
        <div class='panel-heading'>任务责任人</div>
        <div class='panel-body'>
            <@dataScope id="taskUser${taskId}" name="taskUser" dataId="${userId}" entityName="ActTaskExtUser" appCtx="/workflow/control"/>
                <input type='button' class='btn btn-primary' value='保存'
                       onclick='ProcessDiagramGenerator.saveTaskUser()'/>
        </div>
    </div>

    <div class='panel panel-default'>
        <div class='panel-heading'>权限设置<a style='float:right' class='icosg-plus1' href='#nowhere'
                                          onclick='ProcessDiagramGenerator.setTaskAssignee()'></a></div>
        <div class='panel-body'></div>
    </div>
</form>