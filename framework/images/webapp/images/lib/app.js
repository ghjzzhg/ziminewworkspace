function confirmx(title, confirmFunc, param) {
    if (confirm(title)) {
        if (typeof(param) == 'string') {
            try {
                param = JSON.parse(param);
            } catch (e) {
            }
        }
        var confirmFuncObj = typeof(confirmFunc) == 'function' ? confirmFunc : eval(confirmFunc);
        if (typeof(confirmFuncObj) == 'function') {
            confirmFuncObj.call(window, param);
        }
    }
}

$.zTreeTools = {
    removeUnsavedNode: function (param) {
        var zTree = $.fn.zTree.getZTreeObj(param.treeId);
        zTree.removeNode(zTree.getNodeByParam("id", param.nodeId, null));
    },
    addDiyDom: function (treeId, treeNode) {
        var zTree = $.fn.zTree.getZTreeObj(treeId);
        var sObj = $("#" + treeNode.tId + "_span");
        if ($("#addBtn_" + treeNode.code).length > 0) return;
        var addStr = "<span class='diy-btn icon-remove tip' id='removeBtn_" + treeNode.code
            + "' title='删除' onfocus='this.blur();'></span>";
        sObj.after(addStr);
        addStr = "<span class='diy-btn icon-plus tip' id='addBtn_" + treeNode.code
            + "' title='添加子项' onfocus='this.blur();'></span>";
        sObj.after(addStr);
        addStr = "<span class='diy-btn icon-pencil tip' id='editBtn_" + treeNode.code
            + "' title='修改项' onfocus='this.blur();'></span>";
        sObj.after(addStr);
        var btn = $("#addBtn_" + treeNode.code);
        if (btn) btn.off("click").on("click", function (event) {
            event.stopPropagation();
            if (!treeNode.code) {
                showError("请保存当前内容节点后再添加新项");
                return false;
            }
            if(zTree.addFun){
                var addFunObj = typeof(zTree.addFun) == 'function' ? zTree.addFun : eval(zTree.addFun);
                addFunObj.call(window, {parentCode: treeNode.code});
            }else{

                var nodes = zTree.transformToArray(zTree.getNodes());
                var newCount = 1;
                if (nodes) {
                    newCount = nodes.length;
                }
                var newNodes = zTree.addNodes(treeNode, {id: 100 + newCount, pId: treeNode.code, name: "请录入.."});
                zTree.editName(newNodes[0]);
            }
            return false;
        });
        btn = $("#removeBtn_" + treeNode.code);
        if (btn) btn.off("click").on("click", function () {
            if (!treeNode.code) {
                confirmx("确认删除该项吗？", $.zTreeTools.removeUnsavedNode, {treeId: treeId, nodeId: treeNode.id});
                return false;
            }
            confirmx("确认删除该项吗？", zTree.deleteFun, {treeId: treeId, code: treeNode.code});
            return false;
        });
        btn = $("#editBtn_" + treeNode.code);
        if (btn) btn.off("click").on("click", function () {
            if(zTree.updateFun){
                var updateFunObj = typeof(zTree.updateFun) == 'function' ? zTree.updateFun : eval(zTree.updateFun);
                zTree.updateFunObj.call(window, {code: treeNode.code});
            }else{
                zTree.editName(treeNode);
            }
            return false;
        });
    },
    updateNodeName: function (treeObj, treeNode, onUpdateFun) {
        var pId = treeNode.pId, name = treeNode.name, id = treeNode.id;
        var nodes = treeObj.transformToArray(treeObj.getNodes());
        var orderStr = "";
        for (var i = 0; i < nodes.length; i++) {
            var node = nodes[i];
            orderStr += (node.code ? node.code : "") + ":" + i + ";"
        }

        var updateFunObj = typeof(onUpdateFun) == 'function' ? onUpdateFun : eval(onUpdateFun);
        if (typeof(updateFunObj) == 'function') {
            updateFunObj.call(window, {parentId: pId, id: id, name: name, orderString: orderStr});
        }
    },
    addNodeName: function (treeObj, treeNode, onAddFun) {
        var pId = treeNode.pId, name = treeNode.name;
        var nodes = treeObj.transformToArray(treeObj.getNodes());
        var orderStr = "";
        for (var i = 0; i < nodes.length; i++) {
            var node = nodes[i];
            orderStr += (node.code ? node.code : "") + ":" + i + ";"
        }

        var addFunObj = typeof(onAddFun) == 'function' ? onAddFun : eval(onAddFun);
        if (typeof(addFunObj) == 'function') {
            addFunObj.call(window, {parentId: pId, name: name, orderString: orderStr, updateNodeCode: function(id){treeNode.id = id; treeNode.code = id}});
        }
    }

};