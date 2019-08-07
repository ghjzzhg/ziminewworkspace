<#assign formContent = delegator.findOne("ActExtFormContent", {"formId": parameters.view?default("")}, true)/>
<#assign form = delegator.findOne("ActExtForm", {"formId": parameters.view?default("")}, true)/>
<#assign formContentStr = formContent.content?default("")/>
<style type="text/css">
    .shadow{
        -webkit-box-shadow: #666 0px 0px 10px;
        -moz-box-shadow: #666 0px 0px 10px;
        box-shadow: #666 0px 0px 10px;
    }
</style>
<script type="text/javascript">
    <#--var formDataJson = '${formDataJsonStr?default("")}'';-->
    $(function(){
//        $("[name^='fp_']").each(function () {
            $("#formInfo").validationEngine("attach", {promptPosition: "topLeft"});
//        });
        var content = unescapeHtmlText('${formContentStr?default("")}');
        //TODO:将数据回填至表单中
        <#if allTaskHistoryList?has_content>
            <#list allTaskHistoryList as line>
                <#assign oldFormContent = delegator.findOne("ActExtFormContent", {"formId": line.viewId}, true)/>
                <#assign oldForm = delegator.findOne("ActExtForm", {"formId": line.viewId}, true)/>
                <#assign oldformContentStr = oldFormContent.content?default("无表单记录")/>
                var dev = "historyFormInfo"+ ${line_index}
                $("#historyFormInfo").append("<div class='screenlet-title-bar' style='; margin:5px;border:0;padding:0;'>" +
                        "<ul><li class='h3'>${oldForm.formName}</li></ul></div><div style='overflow:auto;margin:5px;border:0;padding:1;' id='" + dev + "'>" +
                        "</div>")
                $("#"+dev).html(unescapeHtmlText('${oldformContentStr?default("无表单记录")}'));
                $("#"+dev + " *").each(function(){
                    if($(this).is("a")){
                        $(this).remove();
                    }else{
                        $(this).attr('disabled', 'true');
                    }
                })
                <#list line.historyList as line>
                var sjvalue = unescapeHtmlText('${line.textValue?default("")}');
                var hName = unescapeHtmlText('${line.formName?default("")}');
                    $("#"+dev).find("[name^='fp_']").each(function(){
                        var name = $(this).attr("name");
                        if(name == hName){
                            if( $("#"+dev).find(this).is("input")){
                                if( $("#"+dev).find(this)[0].type == "radio"){
                                    $("#"+dev).find("input[name='" + name + "']").each(function () {
                                        $("#"+dev).find(this).attr("name",name+dev);
                                        var value = $("#"+dev).find(this).val();
                                        if(value == sjvalue){
                                            $("#"+dev).find(this).attr("checked","checked");
                                        }
                                    })
                                }else if ( $("#"+dev).find(this)[0].type == "text"){
                                    $("#"+dev).find(this).val(sjvalue);
                                }else if( $("#"+dev).find(this)[0].type == "checkbox"){
                                    var valueList = sjvalue.substring(0,sjvalue.length - 1);
                                    var values = valueList.split(",");
                                    $("#"+dev).find("input[name='" + name + "']").each(function () {
                                        var value =  $("#"+dev).find(this).val();
                                        for(var i = 0; i < values.length; i++){
                                            if(value == values[i]){
                                                $("#"+dev).find(this).attr("checked","checked");
                                            }
                                        }
                                    })
                                }
                            }else if ( $("#"+dev).find(this).is("select")){
                                $("#"+dev).find("select[name='" + name + "']  option[value='"+ sjvalue +"'] ").attr("selected",true)
                            }else if( $("#"+dev).find(this).is("textarea")){
                                $("#"+dev).find(this).html(sjvalue);
                            }
                        }
                    })
                </#list>
            </#list>
        </#if>
        $("#formInfo").html(content);
    })
</script>
<input type="hidden" name="viewFormId" id="viewFormId" value="${parameters.view?default("")}">
<#if allTaskHistoryList?has_content>
<div id="historyFormInfo">

</div>
</#if>
<div class="screenlet-title-bar"  style="; margin:5px;border:0;padding:0;">
    <ul>
        <li class="h3">${form.formName?default("未命名")}</li>
    </ul>
    <br class="clear">
</div>
    <div id="formInfo" class="validationEngineContainer">

    </div>
<#--${parameters.viewType}-->