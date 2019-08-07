<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<@treeInline id="workflowTypes" onselect="onWorkflowType" edit=true url="getWorkflowTypes"/>

<script type="text/javascript">
    $(function(){
        $.treeInline['workflowTypes'].initTreeInline();
    })

    function onWorkflowType(id){
        var selectedNode = $.treeInline["workflowTypes"].tree.getNodeByParam("id", id, null);
        var parentNode = selectedNode? selectedNode.getParentNode() : "";

        $.ajax({
            type: 'GET',
            url: "GetWorkflowDefs?type=" + id + (parentNode ? "&parentType=" + parentNode.id : ""),
            async: true,
            dataType: 'html',
            success: function (content) {
                $(".right-content").html(content);
            }
        });
    }
</script>
