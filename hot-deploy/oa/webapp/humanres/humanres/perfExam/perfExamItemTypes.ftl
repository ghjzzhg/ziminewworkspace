<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<@treeInline id="perfExamItemTypes" onselect="onPerfExamItemType" edit=true url="getPerfExamItemTypes" addFun="addPerfExamItemType" updateFun="updatePerfExamItemType" deleteFun="deletePerfExamItemType" nodesLevel=2/>

<script type="text/javascript">
    $(function(){
        $.treeInline['perfExamItemTypes'].initTreeInline();
    })

    function onPerfExamItemType(id){
        var selectedNode = $.treeInline["perfExamItemTypes"].tree.getNodeByParam("id", id, null);
        var parentNode = selectedNode? selectedNode.getParentNode() : "";

        $.ajax({
            type: 'GET',
            url: "GetExamItems?type=" + id + (parentNode ? "&parentType=" + parentNode.id : ""),
            async: true,
            dataType: 'html',
            success: function (content) {
                $(".right-content").html(content);
            }
        });
    }
    function addPerfExamItemType(data){
        $.ajax({
            type: 'POST',
            url: "createPerfExamItemType",
            async: true,
            data:{orderStr: data.orderString, parentTypeId: data.parentId, description: data.name},
            dataType: 'json',
            success: function (content) {
                if(content && content.typeId){
                    data.updateNodeCode(content.typeId);
                }
            }
        });
    }
    function updatePerfExamItemType(data){
        $.ajax({
            type: 'POST',
            url: "updatePerfExamItemType",
            async: true,
            data:{orderStr: data.orderString, parentTypeId: data.parentId, typeId: data.id, description: data.name},
            dataType: 'json',
            success: function (content) {
                showInfo("更新成功");
            }
        });
    }
    function deletePerfExamItemType(data){
        $.ajax({
            type: 'POST',
            url: "deletePerfExamItemType",
            async: true,
            data:{typeId: data.code},
            dataType: 'json',
            success: function (content) {
                var zTree = $.fn.zTree.getZTreeObj(data.treeId);
                zTree.removeNode(zTree.getNodeByParam("id", data.code, null));
                showInfo("删除成功");
            }
        });
    }
</script>
