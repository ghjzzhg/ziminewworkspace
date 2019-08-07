<#macro chooser chooserType="LookupEmployee" name="chooser" strTab="员工选择器" importability=false chooserValue="" chooserValueName="" dropDown=false dropDownType="LookupEmployee" aloneFunction="" callback="" chooserValue=""  required=true secondary="false" dropValue="" fp="">
<!--
chooserType:选择器类型：部门(LookupDepartment)，岗位(LookupOccupation)，职级，人员(LookupEmployee),班次(WorkNumAjax)
chooserValue:选择器的值
dropDown:是否启用下拉框
dropDownType：下拉框选择类型: 负责人(LookupManager)
callback：回调函数
aloneFunction:单独的方法，不使用本页面的查询方法
chooserValue:部门，岗位，职级，人员的编号
dropValue：下拉框的值
importability ： 是否可输入
 -->
<script type="text/javascript">
    function lookupInfo${name}(){
        var secon = '${chooserType}';
        if(secon == 'LookupEmployee'){
            displayInTab3("secondaryTbl", "${strTab}", {requestUrl: "${chooserType}",data:{name:'${name}'}, width: "600px"});
        }else{
            displayInTab3("LookupTbl", "${strTab}", {requestUrl: "${chooserType}",data:{name:'${name}'}, width: "600px"});
        }
    }

    <#if chooserValue?has_content && !chooserValueName?has_content>
        $(function () {
            setLookUpInfo${name}("${chooserValue}",false);
        })
    <#elseif chooserValue?has_content && chooserValueName?has_content>
        $("#showId${name}").val(unescapeHtmlText('${chooserValueName}'));
        $("#${name}id").val(${chooserValue});
    </#if>

    function setLookUpInfo${name}(id,flag){
        <#if aloneFunction?has_content>
            ${aloneFunction}();
        <#else>
            $.ajax({
                type: 'POST',
                url: "lookUpChooserInfo",
                async: true,
                dataType: 'json',
                data:{chooserType:'${chooserType}',chooserId:id,dropDown:'${dropDown?c}',dropDownType:'${dropDownType}'},
                success: function (data) {
                    if(data.data.flag != 0){
                        $("#showId${name}").val(data.data.name);
                        $("#${name}id").val(data.data.id);
                        if(${dropDown?c}){
                            var list = data.data.valueList;
                            $("#${name}SelectId").empty();
                            $("#${name}SelectId").append("<option>--请选择--</option>")
                            for(var i = 0; i < list.length; i++){
                                <#if dropValue?has_content>
                                    if(list[i].id == '${dropValue}'){
                                        $("#${name}SelectId").append("<option value='"+ list[i].id + "' selected>"+ list[i].name+ "</option>")
                                    }else{
                                        $("#${name}SelectId").append("<option value='"+ list[i].id + "'>"+ list[i].name+ "</option>")
                                    }
                                <#else>
                                    $("#${name}SelectId").append("<option value='"+ list[i].id + "'>"+ list[i].name+ "</option>")
                                </#if>

                            }
                        }
                        if(flag){
                            closeCurrentTab();
                        }
                        ${callback}(id);
                    }else{
                        showInfo(data.data.msg);
                        if(flag) {
                            closeCurrentTab();
                        }
                    }

                }
            });
        </#if>
    }

    function clearHiddenValue${name}(){
        var showId = $("#showId${name}").val();
        if(showId == ""){
            $("#${name}id").val("")
        }
    }
</script>
<div class="jqv">
    <input type="text" id="showId${name}" size="12" onblur="clearHiddenValue${name}()" name="<#if fp?has_content>fp_</#if>show${name}" <#if !importability> readonly</#if>/>
    <a href="#" class="smallSubmit" onclick="lookupInfo${name}()">选择</a>
    <input name="<#if fp?has_content>fp_</#if>${name}" id="${name}id" type="hidden" <#if required> class="validate[required]"</#if>>
</div>
<#if dropDown>
    <div style="float: left;">
        <select name="${name}Select" id="${name}SelectId" <#if required> class="validate[required]"</#if>>
            <option value="">--请选择--</option>
        </select>
    </div>
</#if>
</#macro>