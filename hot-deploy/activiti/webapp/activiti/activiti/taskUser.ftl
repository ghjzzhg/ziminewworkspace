<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<div class="col-xs-3">
    <select name="userType">
        <option value="dataScope">按用户</option>
        <option value="starter">发起人</option>
        <option value="oldUser">与已执行任务相同执行人</option>
        <option value="oldDepartment">与已执行任务相同部门</option>
        <option value="oldManager">已执行任务执行人的领导</option>
        <option value="script">脚本</option>
    </select>
</div>
<div class="col-xs-9">
    <div id="userValueDiv">
        <@dataScope id="userValue" appCtx="/workflow/control" name="userValue" dataId="${userId}" dataAttr="" entityName="ActTaskExtUser" readonly=false dept=true level=true position=true user=true/>
    </div>
</div>