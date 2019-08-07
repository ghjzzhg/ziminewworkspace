<div>
    <#if parameters.msg?has_content>
        <label>${parameters.msg}</label>
    <#else>
        <#assign coValue = parameters.chooserMap.value?default("")>
        <script language="JavaScript">
            $(function () {
                showRuleInfo();
                <#if parameters.chooserMap.operatorType?has_content>
                    $("select[id = 'operator'] option").each(function(){
                        var operatorType = "${parameters.chooserMap.operatorType?default('')}";
                        var value = $(this).val();
                        if(value == operatorType){
                            $(this).attr("selected", true)
                        }
                    });
                </#if>
            })

            function showRuleInfo(){
                var opvalue = $("#operatorParameter").val();
                var opid = opvalue.substring(0,opvalue.lastIndexOf("_"));
                var type = opvalue.substring(opvalue.lastIndexOf("_") + 1, opvalue.length);
                $("#operatorValue").removeClass();
                $("#operator").empty();
                $("#operatorValueTd").html("");
                var html = "";
                if(type == "int" || type == "float"){
                    html += "<option value='equals'> 等于 </option>" +
                            "<option value='greater'> 大于 </option>" +
                            "<option value='less'> 小于 </option>" +
                            "<option value='notEquals'> 不等于 </option>"
                }else if(type == "select" || type == "radio"){
                    $.ajax({
                        type: 'POST',
                        url: "/workflow/control/searchChooseValueList",
                        async: true,
                        dataType: 'json',
                        data:{chooserId:opid},
                        success: function (content) {
                            console.log(content)
                            var chooserList = content.chooserValueList;
                            $("#operatorValueTd").append("<select id='operatorValue' name='operatorValue'></select>")
                            for(var i = 0; i < chooserList.length; i++){
                                if('${coValue?default("")}' == chooserList[i].chooserKey){
                                    $("#operatorValue").append("<option value='" + chooserList[i].chooserKey + "' selected>" +
                                            "" + chooserList[i].chooserValue + "</option>")
                                }else {
                                    $("#operatorValue").append("<option value='" + chooserList[i].chooserKey + "'>" +
                                            "" + chooserList[i].chooserValue + "</option>")
                                }

                            }
                        }
                    });
                    html += "<option value='equals'> 等于 </option>";
                } else {
                    html += "<option value='equals'> 等于 </option>" +
                            "<option value='contain'> 包含 </option>" +
                            "<option value='notEquals'> 不等于 </option>"
                }
                if(type != "select" && type != "radio"){
                    $("#operatorValueTd").append('<input type="text" size="10" id="operatorValue" value="${parameters.chooserMap.value?default("")}" maxlength="40" name="operatorValue"/>');
                }
                $("#operator").append(html)
            }

            function saveValue(id){
                var opvalue = $("#operatorParameter").val();
                var opid = opvalue.substring(0,opvalue.lastIndexOf("_"));
                var type = opvalue.substring(opvalue.lastIndexOf("_")+1, opvalue.length);
                var parametersValue = $("#operatorValue").val();

                if(parametersValue == ""){
                    alert("请输入值！");
                    return;
                }else{
                    if(type == "int"){
                        if(!/^[0-9]*$/.test(parametersValue)){
                            alert("请输入数字!");
                            return;
                        }
                    }else if(type == "float"){
                        if(!/^(-?\d+)(\.\d+)?$/.test(parametersValue)){
                            alert("请输入小数!");
                            return;
                        }
                    }else if (type == "email"){
                        if(!/^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/.test(parametersValue)){
                            alert("请输入正确的邮箱!");
                            return;
                        }
                    }else if (type == "idcard"){
                        if(!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(parametersValue)){
                            alert("请输入有效的身份证号码!");
                            return;
                        }
                    }
                }
                var processDefinitionId = $("#processDefinitionId").val();
                var sourceId = $("#sourceId").val();
                var targetId = $("#targetId").val();
                var operator = $("#operator").val();
                $.ajax({
                    type: 'POST',
                    url: "/workflow/control/saveHiddenChooserValue",
                    async: true,
                    dataType: 'json',
                    data:{id:id,chooserId: opid, parametersValue: parametersValue,sourceId:sourceId,targetId:targetId,operator:operator,processDefinitionId:processDefinitionId},
                    success: function (content) {
                        console.log(content);
                        showInfo(content.msg);
                    }
                });
            }
        </script>
    <form name="setAllotForm" id="setAllotForm" class="basic-form">
        <input type="hidden" name="processDefinitionId" id="processDefinitionId" value="${parameters.processDefinitionId?default("")}">
        <input type="hidden" name="sourceId" id="sourceId" value="${parameters.sourceId?default("")}">
        <input type="hidden" name="targetId" id="targetId" value="${parameters.targetId?default("")}">
        <table class="basic-table hover-bar">
            <tr>
                <td>
                    <select onchange="showRuleInfo()" id="operatorParameter">
                        <option>请选择</option>
                        <#list parameters.chooserList as line>
                            <option value="${line.id}_${line.type}" <#if parameters.chooserMap.chooserId?has_content && parameters.chooserMap.chooserId == line.id> selected </#if>>${line.name}</option>
                        </#list>
                    </select>
                </td>
                <td id="operatorTd">
                    <select id='operator' name='operator'>
                    </select>
                </td>
                <td id="operatorValueTd">
                </td>
            </tr>
        </table>
    </form>
    <div class='form-group'><input type='button' class='btn btn-primary' value='保存'
                                   onclick='saveValue("${parameters.chooserMap.id?default('')}")'/></div>
    </#if>
</div>