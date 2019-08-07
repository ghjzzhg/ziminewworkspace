<#macro dataScope id name dataId="" dataAttr="" entityName="" readonly=false dept=true level=true position=true user=true like=true appCtx="" required=false>
    <#local value={}/>
    <#local description=[]/>
    <#if dataId?has_content && entityName?has_content>
        <#assign scopeData = dispatcher.runSync("getDataScope", Static["org.ofbiz.base.util.UtilMisc"].toMap("entityName", entityName, "dataId", dataId, "dataAttr", dataAttr, "userLogin", userLogin))/>
        <#if scopeData?has_content>
            <#local value=scopeData.value?default({})/>
            <#local description=scopeData.description?default("")/>
        </#if>
    </#if>
<div>
    <script src="/hr/static/hr.js" type="text/javascript" charset="utf-8"></script>
    <#if !readonly>
        <div style="float:right;display: inline-block;margin-left: 5px;"><a href="#" onclick="$.hrMacro.emptyScopes('${id}', '${name}','${entityName}')" class="smallSubmit btn btn-danger">清除</a></div>
    <div id="${id}" style="cursor:pointer; padding: 5px; border:1px solid lightskyblue; height: 2.5em;overflow-y: auto" onclick="$.hrMacro.openDataScope({id:'${id}', name: '${name}', appCtx: '${appCtx}', dept: ${dept?c}, level: ${level?c}, position: ${position?c}, user: ${user?c}, like: ${like?c}})">
    <#else>
    <div id="${id}" style="cursor:pointer; border:1px solid lightskyblue; height: 4em;overflow-y: auto">
    </#if>
    <#if description?has_content>
        <#list description as desc>
            <span>${desc.name}<#if desc.like?has_content><i style="color: lightskyblue">${desc.like}</i></#if></span>
        </#list>
    </#if>
</div>
    <input type="hidden" name="${name}_entity_name" id="${name}_entity_name" value="${entityName}">
    <input type="hidden" name="${name}_data_id" id="${name}_data_id" value="${dataId}">
    <input type="hidden" data-jqv-prompt-at="#${id}" class="<#if required>validate[groupRequired[${name}_scope]]</#if>" name="${name}_dept_only" id="${name}_dept_only" value="${value.deptOnly?default('')}">
    <input type="hidden" data-jqv-prompt-at="#${id}" class="<#if required>validate[groupRequired[${name}_scope]]</#if>" name="${name}_dept_like" id="${name}_dept_like" value="${value.deptLike?default('')}">
    <input type="hidden" data-jqv-prompt-at="#${id}" class="<#if required>validate[groupRequired[${name}_scope]]</#if>" name="${name}_level_only" id="${name}_level_only" value="${value.levelOnly?default('')}">
    <input type="hidden" data-jqv-prompt-at="#${id}" class="<#if required>validate[groupRequired[${name}_scope]]</#if>" name="${name}_level_like" id="${name}_level_like" value="${value.levelLike?default('')}">
    <input type="hidden" data-jqv-prompt-at="#${id}" class="<#if required>validate[groupRequired[${name}_scope]]</#if>" name="${name}_position_only" id="${name}_position_only" value="${value.positionOnly?default('')}">
    <input type="hidden" data-jqv-prompt-at="#${id}" class="<#if required>validate[groupRequired[${name}_scope]]</#if>" name="${name}_position_like" id="${name}_position_like" value="${value.positionLike?default('')}">
    <input type="hidden" data-jqv-prompt-at="#${id}" class="<#if required>validate[groupRequired[${name}_scope]]</#if>" name="${name}_user" id="${name}_user" value="${value.user?default('')}">
</div>
</#macro>

<#macro selectStaff id name value="" multiple=true onchange="" required=false>
    <#local description=""/>
    <#local result={}/>
    <#if value?has_content>
        <#local result = dispatcher.runSync("getStaffNames", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyIds", value, "userLogin", userLogin))/>
    </#if>
    <div class="field-lookup">
        <div style="float:right;display: inline-block;margin-left: 5px;"><button  type="button" onclick="${id}_emptyStaffs('${id}', '${name}')" class="smallSubmit">清除</button></div>
        <div id="${id}" style="cursor:pointer; border:1px solid lightskyblue; height: 4em;overflow-y: auto" onclick="${id}_openStaffSelect()">
            <#if result.nameList?has_content>
                <#list result.nameList as name>
                    <span>${name}</span>
                </#list>
            </#if>
        </div>
        <input id="${id}_${name}" type="hidden" data-jqv-prompt-at="#${id}" class="<#if required>validate[required]</#if>" name="${name}" value="${value?default('')}">
        <script type="text/javascript">

            function ${id}_emptyStaffs(id, name){
                document.getElementById(id).innerHTML = " ";
                $("input[name=" + name + "]").val("");
                $("#${id}_${name}").trigger("blur");
                return false;
            }
            function ${id}_openStaffSelect(){
                displayInTab3('${id}StaffSelectTab', '选择员工', {requestUrl: 'LookupStaffSelect', width: '1000', data: {
                    lookupId: '${id}',
                    lookupName: '${name}',
                    partyIds:$("input[name=${name}]").val(),
                    multiple: ${multiple?c},
                    onchange: '${id}_onchange'
                }});
            }

            function ${id}_onchange(values, selectionText){
                $("#${id}_${name}").trigger("blur");
                ${onchange}(values, selectionText);
            }
        </script>
    </div>
</#macro>