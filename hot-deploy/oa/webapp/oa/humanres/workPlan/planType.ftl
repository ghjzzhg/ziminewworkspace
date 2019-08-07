<#macro selectPlanType name optionEnumeTypeId defaultVale="">
    <script type="text/javascript">
        function changeSetInput(obj){
            var option = $(obj).find("option:selected");
            $("input[name= " + "${name}" + "_input]").val($(option).text());
            $("#"+"${name}_hidden").val($(option).val());
        }

        function setInput(desc){
            $("#" + "${name}" + "_select").find("option").each(function(){
                if(desc.trim() == $(this).text()){
                    $(this).attr("selected","true");
                    $("#"+"${name}_hidden").val($(this).val());
                }else{
                    $("#"+"${name}_hidden").val("");
                }
            });
        }
    </script>
    <#if optionEnumeTypeId?has_content>
        <#assign optionList = delegator.findByAnd("Enumeration",{'enumTypeId':'${optionEnumeTypeId}'})>
    </#if>
    <input type="text" name="${name}_input" value="" onchange="setInput($(this).val())">
    <input type="hidden" name="${name}" id="${name}_hidden" value="">
    <select name="${name}_select" id="${name}_select" onchange="changeSetInput($(this))">
        <option value="">--请选择--</option>
        <#list optionList as list>
            <#if defaultVale == list.enumId>
                <option value="${list.enumId}" selected>${list.description}</option>
            <#else >
                <option value="${list.enumId}">${list.description}</option>
            </#if>
        </#list>
    </select>
    <script>
        if('${defaultVale}' != ''){
            var option = $("#"+"${name}_select").find("option:selected");
            $("input[name= " + "${name}" + "_input]").val($(option).text());
            $("#"+"${name}_hidden").val($(option).val());
        }
    </script>
</#macro>