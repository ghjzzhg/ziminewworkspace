<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<style type="text/css">
    .ms-container ul.ms-list{
        height: 350px;
    }

    .ztree li {
        line-height: 20px;
    }
    .ztree li span.button{
        top: 1px;
        font-family: 'Glyphicons Halflings';
        font-style: normal;
        font-weight: 400;
        line-height: 1em;
        -webkit-font-smoothing:antialiased;
        -moz-osx-font-smoothing:grayscale;
        background-image: none;
    }

    .ztree li span.button.pIcon01_ico_open:before{
        content: "\e021";
    }
    .ztree li span.button.pIcon01_ico_close:before{
        content: "\e021";
    }
    .ztree li span.button.pIcon01_ico_open{
        color: #0a6ebd;
    }
    .ztree li span.button.pIcon01_ico_close{
        color: darkgray;
    }

    .ztree li span.button.ico_open:before{
        content: "\e008";
    }
    .ztree li span.button.ico_close:before{
        content: "\e008";
    }
    .ztree li span.button.ico_open{
        color: #0a6ebd;
        vertical-align: middle;
    }
    .ztree li span.button.ico_close{
        color: darkgray;
        vertical-align: middle;
    }
    .ztree li span.button.icon01_ico_docu:before {
        content: "\e008";
    }
    .ztree li span.button.icon01_ico_docu {
        color: #0a6ebd;
    }

    .ztree li span.button.switch.root_open,.ztree li span.button.switch.center_open,.ztree li span.button.switch.bottom_open{
        color: #0a6ebd;
    }

    .ztree li span.button.switch.root_open:before,.ztree li span.button.switch.center_open:before,.ztree li span.button.switch.bottom_open:before{
        content: "\2212";
    }

    .ztree li span.button.switch.root_close,.ztree li span.button.switch.center_close,.ztree li span.button.switch.bottom_close{
        color: darkgray;
    }

    .ztree li span.button.switch.root_close:before,.ztree li span.button.switch.center_close:before,.ztree li span.button.switch.bottom_close:before{
        content: "\2b";
    }

</style>
<div>
    <div class="col-xs-8">
        <div id="dept">
            <div>
                <div class="col-xs-4" style="padding: 0">
                <@treeInline id="${lookupId}_StaffSelectDeptTree" url="GetOrganizationTree?includeStaff=true" onselect="${lookupId}_SelectDept" height="330px" edit=false/>
                </div>
                <div class="col-xs-8">
                    <select id="${lookupId}_DeptSelect" multiple="multiple">

                    </select>
                </div>
            </div>
        </div>
    </div>
    <div class="col-xs-4">
        <div id="${lookupId}_SelectedTab" class="ms-container">
            <div class="ms-selection" style="width:100%">
                <ul class="ms-list">
                <#if partyIds?has_content>
                    <#list partyIds?split(",", "r") as partyId>
                        <#assign person = delegator.findOne("Person", {"partyId" : partyId}, true)/>
                        <#if person?has_content>
                            <#assign fullName = person.get("fullName")?default('')/>
                            <#if !fullName?has_content>
                                <#assign fullName = person.get("lastName")?default('') + person.get("firstName")?default('')/>
                            </#if>
                            <li value_id="${person.get('partyId')}" onclick='removeSelectedItem(this)' class='ms-elem-selection ms-selected' style='display: list-item;'>
                                <span>${fullName}</span>
                            </li>
                        </#if>
                    </#list>
                </#if>
                </ul>
            </div>
        </div>
    </div>
    <div class="col-xs-12" style="margin-top: 5px">
        <a href="#nowhere" class="smallSubmit" onclick="${lookupId}_confirmStaffs()">确定</a>
        <a href="#nowhere" class="smallSubmit" onclick="closeCurrentTab()">取消</a>
    </div>
</div>

<script type="text/javascript">
    function removeSelectedItem(obj){
        if(confirm('确定要删除此选项吗？删除后点击确定按钮生效。')){
            $(obj).remove();
        }

    }
    var $staffSelectDeptSelect = $("#${lookupId}_DeptSelect");
    function ${lookupId}_SelectDept(id){
        $staffSelectDeptSelect.multiSelect('deselect_all');
        $staffSelectDeptSelect.multiSelect('destroy');
        $staffSelectDeptSelect.find("option").remove();

        var deptTree = $.treeInline['${lookupId}_StaffSelectDeptTree'].tree;
        var nodes = deptTree.getNodesByParam("pId", id, null);
        if(nodes.length){
            for(var i in nodes){
                var node = nodes[i];
                if(node.isHidden){
                    $staffSelectDeptSelect.append("<option value='" + node.code + "' label='" + node.name + "'>" + node.name + "</option>");
                }
            }
        }

        $("#${lookupId}_DeptSelect").multiSelect({keepOrder: true, afterSelect:${lookupId}_afterStaffSelect, afterDeselect: ${lookupId}_afterStaffDeselect});
    }

    function ${lookupId}_afterStaffSelect(value){
        if(!${multiple}){
            var selected = $staffSelectDeptSelect.val();
            if(selected){
                for(var i in selected){
                    var v = selected[i];
                    if(v != value){
                        $staffSelectDeptSelect.multiSelect("deselect", v);
                    }
                }
            }
            //start
            //Author:dengbeibei
            //选择员工时不能多选，如果已经有员工了，则将已经有的员工移除
            $("#${lookupId}_SelectedTab .ms-list li").each(function(){
                var $this = $(this), v = $this.attr("value_id");
                if(v!=value){
                    $(this).remove();
                }
            });
            //end
        }

        if($("#${lookupId}_SelectedTab li[value_id='" + value + "']").length){
            ${lookupId}_afterStaffDeselect(value);
        }
        var text = $("#${lookupId}_DeptSelect option[value='" + value + "']").text();
        $("#${lookupId}_SelectedTab .ms-list").append("<li value_id='" + value + "' onclick='removeSelectedItem(this)' class='ms-elem-selection ms-selected' style='display: list-item;'>"
                + "<span>" + text + "</span>"
                + "</li>");
    }

    function ${lookupId}_afterStaffDeselect(value){
        $("#${lookupId}_SelectedTab li[value_id='" + value + "']").remove();
    }

    function ${lookupId}_confirmStaffs(){
        var values = [];
        var selectionText = [];
        $("#${lookupId}_SelectedTab .ms-list li").each(function(){
            var $this = $(this), value = $this.attr("value_id");
            values.push(value);
            selectionText.push($this.html());
        });
        $("input[name=${lookupName}]").val(values.join(","));
        $("#${lookupId}").html(selectionText.join(" "));
    <#if onchange?has_content>
        var callbackObj = eval('${onchange}');
        if (typeof(callbackObj) == 'function') {
            callbackObj.call(window, values, selectionText);
        }
    </#if>
        closeCurrentTab();
    }


    <#-- creating the JSON Data -->
    var deptTreeData = [
    <#if (completedTreeContext?has_content)>
        <@fillTree rootCat = completedTreeContext/>
    </#if>
    <#if (staffData?has_content)>
        ,
        <@fillTree rootCat = staffData/>
    </#if>

    <#macro fillTree rootCat>
        <#if (rootCat?has_content)>
            <#list rootCat as root>
            {
                id: "${root.id}",
                code: "${root.code}",
                pId: "${root.pId}",
                isHidden: <#if root.isHidden?has_content>${root.isHidden?c}<#else>false</#if>,
                iconSkin: "${root.iconSkin?default('')}",
                name: unescapeHtmlText("<#if root.name??>${root.name?js_string}<#else>${root.id?js_string}</#if>")
                <#if root_has_next>
                },
                <#else>
                }
                </#if>
            </#list>
        </#if>
    </#macro>
    ];

    $.treeInline['${lookupId}_StaffSelectDeptTree'].initTreeInline(deptTreeData);
    $staffSelectDeptSelect.multiSelect({keepOrder: true});


</script>
