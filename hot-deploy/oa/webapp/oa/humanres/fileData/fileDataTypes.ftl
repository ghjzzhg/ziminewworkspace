<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<@treeInline id="fileDataItemTypes" onselect="onFileDataItemType" edit=false  url="getFileDataTypes" nodesLevel=2/>
<script type="text/javascript">
    $(function(){
        $.treeInline['fileDataItemTypes'].initTreeInline();
    })

    function onFileDataItemType(id){
        var parentNode = $.treeInline["fileDataItemTypes"].tree.getNodeByParam("id", id, null).getParentNode();
        var son = $.treeInline["fileDataItemTypes"].tree.getNodeByParam("id", id, null);
        if(parentNode!=null){
            if(parentNode.name!='文档类别'){
                var parentTypeName=parentNode.name;
                var sonTypeName=son.name;
                var verifyId=parentNode.id;/*//校验用的ID*/
                var fileDataSonId=id;
                var fileDataParentId=parentNode.id;
            }else{
                var parentTypeName=son.name;
                var sonTypeName="";
                var verifyId=parentNode.id;
                var fileDataSonId="";
                var fileDataParentId=id;
            }
        }else{
            var parentTypeName="";
            var sonTypeName="";
            var fileDataSonId="";
            var fileDataParentId="";
            var verifyId=id;
        }
        var parentId=id;
        var typeName=son.name;
        $("#parentTName").val(parentTypeName);
        $("#sonTName").val(sonTypeName);
        $("#Id").val(id);
        $("#verifyId").val(verifyId);
        $("#parentId").val(parentId);
        $("#typeName").val(typeName);
        $("#fileDataSonId").val(fileDataSonId);
        $("#fileDataParentId").val(fileDataParentId);
        $.FileData.searchFileData();
        $.FileData.searchFileType();
    }
</script>
<input type="hidden" id="parentTName" value="">
<input type="hidden" id="sonTName" value="">
<input type="hidden" id="Id" name="Id" value="">
<input type="hidden" id="verifyId" name="verifyId" value="1000">
<input type="hidden" id="parentId" name="parentId" value="1000">
<input type="hidden" id="typeName" name="typeName" value="文档类别">
<input type="hidden" id="fileDataParentId" value="">
<input type="hidden" id="fileDataSonId" value="">