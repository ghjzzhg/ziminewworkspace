<#macro dropDownList dropDownValueList enumTypeId name="dropDown" width="200" onchange="" inputSize="10" selectedValue="" required=true aloneFunction="" preference="--请选择--" usingPreference=true callback="" url="saveEnumeration">
<!--    dropDownValueList:下拉框的值
        name：下拉框name
        selectedValue：默认值
        callback：回调函数
        enumTypeId：类型
        aloneFunction：单独定义的方法，不使用该页面保存方法
        preference:首选项名称
        usingPreference：启用首选项
        required：验证是否为空
        width:下拉框宽度
        inputSize：输入长度
        onchange:下拉框点击事件
 -->
<script type="text/javascript">
    $(function() {
        <#if selectedValue?has_content>
            $("select[id='${name}Id'] option").each(function () {
                if('${selectedValue}' == $(this).val()){
                    $(this).attr('selected',"true");
                    $("#${name}hidden").val($(this).text());
                }
            });
        </#if>
        $('#${name}Id').comboSelect();
        $('#${name}Id').parent().find("input").keyup(function(){
            if($(this).val().trim() != "" && $("#${name}Id").val() == ""){
                $("#${name}A").css("display","");
            }else{
                $("#${name}A").css("display","none");
            }
        });
        $('#${name}Id').parent().find("input").blur(function(){
            $("#${name}hidden").val( $('#${name}Id').parent().find("input").val());
            if($('#${name}Id').parent().find("input").val() != ""){
                $("#${name}hidden").validationEngine("validate");
            }
        });
    });

    function selectChange${name}(){
        $("#${name}A").css("display","none");
        $("#${name}hidden").val($('#${name}Id').parent().find("input").val());
        $("#${name}hidden").validationEngine("validate");
        <#if onchange?has_content>
            ${onchange}${Name}();
        </#if>
    }

    function saveTypeInfo${name}(){
        var value = $('#${name}Id').parent().find("input").val();
        <#if aloneFunction?has_content>
            ${aloneFunction}(value);
        <#else>
        var validate = $("#${name}hidden").validationEngine("validate");
            if(!validate){
                $.ajax({
                    type: 'GET',
                    url: "${url}",
                    async: true,
                    dataType: 'json',
                    data:{enumTypeId:'${enumTypeId}',enumName:value},
                    success: function (data) {
                        if(data.data.flag = "1"){
                            $("#${name}Id").append('<option value="'+data.data.enumId+'" selected>'+data.data.description+'</option>')
                            $("#${name}A").css("display","none");
                            $("${name}hidden").val(data.data.enumId);
                            <#if callback?has_content>
                                ${callback}(data.data.enumId,data.data.description);
                            </#if>
                            $('#${name}Id').comboSelect();
                            $('#${name}Id').parent().find("input").keyup(function(){
                                if($(this).val().trim() != "" && $("#${name}Id").val() == ""){
                                    $("#${name}A").css("display","");
                                }else{
                                    $("#${name}A").css("display","none");
                                }
                            });
                            $('#${name}Id').parent().find("input").blur(function(){
                                $("#${name}hidden").val( $('#${name}Id').parent().find("input").val());
                                $("#${name}hidden").validationEngine("validate");
                            });
                        }else{
                            showInfo(data.data.message);
                        }

                    }
                });
            }
        </#if>
    }
</script>

<div class="jqv">
    <input type="hidden" id="${name}hidden" name="${name}hidden" class="validate[required,maxSize[${inputSize}]]" value="">
    <div style="float: left;width: ${width}px">
        <select name="${name}" id="${name}Id" onchange="selectChange${name}()">
            <#if usingPreference>
                <option value="">${preference}</option>
            </#if>
            <#if dropDownValueList?has_content>
                <#list dropDownValueList as list>
                    <option value="${list.enumId}">${list.description}</option>
                </#list>
            </#if>
        </select>
    </div>
    <div style="width:80px;float: left">
        <a href="#" style="display: none" id="${name}A" onclick="saveTypeInfo${name}()" class="smallSubmit">保存</a>
    </div>
</div>
</#macro>