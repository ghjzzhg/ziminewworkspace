<link rel='stylesheet' href='/images/z-tree/css/zTreeStyle/zTreeStyle.css' />
<script type="text/javascript" src='/images/z-tree/3.5/jquery.ztree.all-3.5.min.js'></script>
<script type="text/javascript" src='/images/z-tree/3.5/jquery.ztree.exhide-3.5.min.js'></script>
<script type="text/javascript">
    var setting = {
        view: {
            selectedMulti: false
        },
        edit: {
            enable: true,
            showRemoveBtn: false,
            showRenameBtn: false
        },
        data: {
            keep: {
                parent:true,
                leaf:true
            },
            simpleData: {
                enable: true
            }
        },
        callback: {
            beforeDrag: beforeDrag,
            beforeRemove: beforeRemove,
            beforeRename: beforeRename,
            onRemove: onRemove
        }
    };

    var zNodes =[

    ];
    var log, className = "dark";
    function beforeDrag(treeId, treeNodes) {
        return false;
    }
    function beforeRemove(treeId, treeNode) {
        className = (className === "dark" ? "":"dark");
        showLog("[ "+getTime()+" beforeRemove ]&nbsp;&nbsp;&nbsp;&nbsp; " + treeNode.name);
        return confirm("确认删除 节点 -- " + treeNode.name + " 吗？");
    }
    function onRemove(e, treeId, treeNode) {
        showLog("[ "+getTime()+" onRemove ]&nbsp;&nbsp;&nbsp;&nbsp; " + treeNode.name);
    }
    function beforeRename(treeId, treeNode, newName) {
        if (newName.length == 0) {
            alert("节点名称不能为空.");
            var zTree = $.fn.zTree.getZTreeObj("treeDemo");
            setTimeout(function(){zTree.editName(treeNode)}, 10);
            return false;
        }
        return true;
    }
    function showLog(str) {
        if (!log) log = $("#log");
        log.append("<li class='"+className+"'>"+str+"</li>");
        if(log.children("li").length > 8) {
            log.get(0).removeChild(log.children("li")[0]);
        }
    }
    function getTime() {
        var now= new Date(),
                h=now.getHours(),
                m=now.getMinutes(),
                s=now.getSeconds(),
                ms=now.getMilliseconds();
        return (h+":"+m+":"+s+ " " +ms);
    }

    var newCount = 1;
    function add(e) {
        var zTree = $.fn.zTree.getZTreeObj("treeDemo"),
                isParent = e.data.isParent,
                nodes = zTree.getSelectedNodes(),
                treeNode = nodes[0];
        if (treeNode) {
            treeNode = zTree.addNodes(treeNode, {id:(100 + newCount), pId:treeNode.id, isParent:isParent, name:"new node" + (newCount++)});
        } else {
            treeNode = zTree.addNodes(null, {id:(100 + newCount), pId:0, isParent:isParent, name:"new node" + (newCount++)});
        }
        if (treeNode) {
            zTree.editName(treeNode[0]);
        } else {
            alert("叶子节点被锁定，无法增加子节点");
        }
    };
    function edit() {
        var zTree = $.fn.zTree.getZTreeObj("treeDemo"),
                nodes = zTree.getSelectedNodes(),
                treeNode = nodes[0];
        if (nodes.length == 0) {
            alert("请先选择一个节点");
            return;
        }
        zTree.editName(treeNode);
    };
    function remove(e) {
        var zTree = $.fn.zTree.getZTreeObj("treeDemo"),
                nodes = zTree.getSelectedNodes(),
                treeNode = nodes[0];
        if (nodes.length == 0) {
            alert("请先选择一个节点");
            return;
        }
        var callbackFlag = $("#callbackTrigger").attr("checked");
        zTree.removeNode(treeNode, callbackFlag);
    };
    function clearChildren(e) {
        var zTree = $.fn.zTree.getZTreeObj("treeDemo"),
                nodes = zTree.getSelectedNodes(),
                treeNode = nodes[0];
        if (nodes.length == 0 || !nodes[0].isParent) {
            alert("请先选择一个父节点");
            return;
        }
        zTree.removeChildNodes(treeNode);
    };
    $(function(){
        $.fn.zTree.init($("#treeDemo"), setting, zNodes);
        $("#addParent").bind("click", {isParent:true}, add);
        $("#addLeaf").bind("click", {isParent:false}, add);
        $("#edit").bind("click", edit);
        $("#remove").bind("click", remove);
    })
</script>
<div class="portlet light ">
    <div class="portlet-body">
        <div style="width: 500px;margin:0 auto">
            <form id="caseTemplateForm">
                <input type="hidden" id="templateId" name="id" value="${template.id?default('')}">
                <table class="table table-hover table-striped table-bordered">
                    <tbody>
                    <tr>
                        <td> <label class="control-label">CASE类型<span class="required"> * </span></label></td>
                        <td>
                        <#assign enumerations = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "CASE_CATEGORY"), null, false)>
                            <select class="form-control select" name="templateKey">
                            <#list enumerations as category>
                                <option value="${category.enumId}" <#if template.templateKey?has_content && template.templateKey == category.enumId>selected</#if>>${category.description}</option>
                            </#list>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td> <label class="control-label">所有者<span class="required"> * </span></label></td>
                        <td>
                        <#assign enumerations = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "CASE_CATEGORY"), null, false)>
                            <select class="form-control select" name="templateKey">
                            <#list enumerations as category>
                                <option value="${category.enumId}" <#if template.templateKey?has_content && template.templateKey == category.enumId>selected</#if>>${category.description}</option>
                            </#list>
                            </select>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </form>
            <div class="form-group" style="text-align: left;float: left">
                <div class="margiv-top-10">
                    <a href="javascript:void(0);" id="addParent" class="btn green"> 新建目录 </a>
                </div>
            </div>
            <#--<div class="form-group" style="text-align: left;float: left">-->
                <#--<div class="margiv-top-10">-->
                    <#--<a href="javascript:void(0);" id="addLeaf" class="btn green"> 新建子目录 </a>-->
                <#--</div>-->
            <#--</div>-->
            <div class="form-group" style="text-align: left;float: left">
                <div class="margiv-top-10">
                    <a href="javascript:void(0);" id="edit" class="btn green"> 修改目录名称 </a>
                </div>
            </div>
            <div class="form-group" style="text-align: left;">
                <div class="margiv-top-10">
                    <a href="javascript:void(0);" id="remove" class="btn green"> 删除目录 </a>
                </div>
            </div>
            <br>
            <div class="zTreeDemoBackground left">
                <ul id="treeDemo" class="ztree"></ul>
            </div>
        </div>
    </div>
</div>