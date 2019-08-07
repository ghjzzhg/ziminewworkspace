<link rel='stylesheet' href='/images/z-tree/css/zTreeStyle/zTreeStyle.css' />
<script type="text/javascript" src='/images/z-tree/3.5/jquery.ztree.all-3.5.min.js'></script>
<script type="text/javascript" src='/images/z-tree/3.5/jquery.ztree.exhide-3.5.min.js'></script>
<#macro treeInline id lazy=true data="" title="" url="" asyncUrl="" dataFilter="" defaultExpand=true checked=false extId="" notAllowSelectRoot=false notAllowSelectParent=false cssStyle="" disabled=false nodesLevel=100 category="" width="" height="" hide=false onselect="" edit=false addFun="" onAddFun="" updateFun="" onUpdateFun="" deleteFun="" onDeleteFun="" onloadFun="" nameHtml=false>
<div id="${id}TreeInlineContainer" style="display:${hide?string('none', '')}">
    <div id="${id}search">
        <input type="text" style="display: inline-block;" class="empty search-key tipb" placeholder="关键字过滤" title="关键字过滤" id="${id}key" name="${id}key" maxlength="50">
        <#--<#if test="${not empty treeConentType}">
            <tags:treeContentEdit id="${id}TypeEdit" cssStyle="display:inline-block;${empty treeConentIcon ? 'width:45%;margin:4px 6px':'vertical-align:middle'}" width="${treeContentWidth}" height="${treeContentHeight}" treeConentType="${treeConentType}" onselect="${id}initTreeInline" icon="${treeConentIcon}" cssClass="tip" title="${empty treeConentTitle ? title : treeConentTitle}维护"/>
        </#if>-->
    </div>
    <div id="${id}tree" class="ztree" style="padding:15px 0;${width?has_content ?string("width:" + width, "")};${height?has_content ?string("height:" + height, "")};overflow: auto"></div>
</div>

<style type="text/css">
    .ztree .hightlight{
        font-weight: bold;
    }

    .diy-btn{
        display: none;
    }
    .ztree li a:hover .diy-btn{
        display:inline-block;
    }
    .ztree .button{
        padding: 0;
    }
</style>
<script type="text/javascript">
    if(!($.treeInline)){
        $.treeInline = {};
    }
    $.treeInline["${id}"] = {
        tree: '',
        setting :{
            view:{
                selectedMulti:false,
                fontCss:function(treeId, treeNode) {
                    return (!!treeNode.highlight) ? {"font-weight":"bold"} : {"font-weight":"normal"};
                },
                showIcon: function(treeId, treeNode){
                    return treeNode.level < ${nodesLevel};
                },
                showLine: function(treeId, treeNode){
                    return treeNode.level < ${nodesLevel};
                },
                expandSpeed: ($.browser.msie && parseInt($.browser.version) <= 6) ? "" : "fast"
                <#if edit>
                ,showTitle: true
                ,addDiyDom: $.zTreeTools.addDiyDom
                </#if>
                <#if nameHtml>
                    ,nameIsHTML: true
                </#if>
            },
            check:{enable: ${checked?c},nocheckInherit:true},
            data:{simpleData:{enable:true}}
        <#if edit>
            ,edit:{enable:true,showRemoveBtn:false,showRenameBtn:false}
        </#if>
        <#if asyncUrl?has_content>
            ,async:{enable:true,url:'${asyncUrl}',autoParam:["id"] <#if dataFilter?has_content>, dataFilter: ${dataFilter}</#if>}
        </#if>
            ,callback:{
                beforeExpand: function (id, node) {
                    return node.level < ${nodesLevel};
                },
                beforeClick:function(id, node){
                    if(${checked?c}){
                        $.treeInline["${id}"].tree.checkNode(node, !node.checked, true, true);
                        return false;
                    }
                <#if notAllowSelectParent>
                    else if(node.isParent){
                        $.treeInline["${id}"].tree.expandNode(node);
                        return false;
                    }
                </#if>
                }
            <#if !checked>
                ,onClick:function(event, treeId, treeNode){
                <#if category?has_content>
                    if (treeNode.category == "" || treeNode.category != "${category}"){
                        return true;
                    }
                </#if>

                 <#if onselect?has_content && !checked>
                    <#if edit>
                    if(treeNode.code){
                    </#if>
                        window["${onselect}"](treeNode.id, treeNode);
                    <#if edit>
                    }
                    </#if>
                </#if>
                }
            </#if>
            <#if checked && onselect?has_content>
                ,onCheck:function(event, treeId, treeNode) {
                <#if edit>
                    if (treeNode.code) {
                </#if>
                        window["${onselect}"]($.treeInline["${id}"].getSelectedIds());
                <#if edit>
                    }
                </#if>
                }
            </#if>
            <#if edit>
                ,beforeRename:function(treeId, treeNode, newName) {
                    treeNode.oriName = treeNode.name;
                    return true;
                }
                ,onRename:function(event, treeId, treeNode){
                    if(treeNode.name != treeNode.oriName){
                        if(treeNode.code){
                            $.zTreeTools.updateNodeName($.fn.zTree.getZTreeObj(treeId), treeNode
                                    <#if onUpdateFun?has_content>
                                        ,"${onUpdateFun}"
                                    </#if>
                                    );
                        }else{
                            $.zTreeTools.addNodeName($.fn.zTree.getZTreeObj(treeId), treeNode
                                <#if onAddFun?has_content>
                                        ,"${onAddFun}"
                                </#if>
                                    );
                        }
                    }
                }
                ,onDrop:function(event, treeId, treeNodes, targetNode, moveType) {
                    var treeNode = treeNodes[0];
                    if(moveType && treeNode.code){
                        updateContentName($.fn.zTree.getZTreeObj(treeId), treeNode.pId, treeNode.code, treeNode.name);
                    }
                }
            </#if>
            }
        },
        getSelectedIds: function(){
            var checkedNodes = $.treeInline["${id}"].tree.getCheckedNodes(true), ids = [];
            for(var i in checkedNodes){
                var checkedNode = checkedNodes[i];

                <#if category?has_content>
                    if (checkedNode.category == "" || checkedNode.category != "${category}"){
                        continue;
                    }
                </#if>

                if(!checkedNode.isParent){
                    ids.push(checkedNode.id);
                }
            }
            return ids.join(",");
        },
        focusKey:function(e) {
            var key = $(e.srcElement || e.currentTarget);
            if (key.hasClass("empty")) {
                key.removeClass("empty");
            }
        },
        blurKey: function (e) {
            var key = $(e.srcElement || e.currentTarget);
            if (key.get(0).value === "") {
                key.addClass("empty");
            }
            $.treeInline["${id}"].searchNode(e);
        },
        searchNode: function(e) {
            // 取得输入的关键字的值
            var key = $(e.srcElement || e.currentTarget), lastValue = key.data("lastValue");
            var value = $.trim(key.get(0).value);

            // 按名字查询
            var keyType = "name";
            if (key.hasClass("empty")) {
                value = "";
            }

            // 如果和上次一次，就退出不查了。
            if (lastValue === value) {
                return;
            }

            // 保存最后一次
            key.data("lastValue", value);
            $(".ztree .hightlight").removeClass("hightlight");
            // 如果要查空字串，就退出不查了。
            if (value === "") {
                return;
            }
            var tree = $.fn.zTree.getZTreeObj(key.data("treeId"));
            var nodeList = tree.getNodesByParamFuzzy(keyType, value);
    //            updateNodes(tree, false, nodeList);

            $.treeInline["${id}"].updateNodes(tree, true, nodeList);
        },
        updateNodes: function(tree, highlight, nodeList) {
            if(nodeList){
                for(var i=0, l=nodeList.length; i<l; i++) {
                    var node = nodeList[i];
                    //node.highlight = highlight;
                    tree.updateNode(node);
                    tree.expandNode(node.getParentNode(), true, false, false);
                    var titleSpan = "#" + node.tId + "_span";
                    $(titleSpan).addClass("hightlight");
                    /*if(i == 0){
                     $("#" + tree.setting.treeId).mCustomScrollbar("scrollTo", titleSpan);
                     }*/
                }
            }
        },
        search: function() {
            $("#${id}search").slideToggle(200);
            if($("#${id}search").is(":visible")){
                $("#${id}search").css("display", "inline-block");
            }
            $("#${id}txt").toggle();
            if($("#${id}txt").is(":visible")){
                $("#${id}txt").css("display", "inline-block");
            }
            $("#${id}key").focus();
        },

        initTreeInline:function(data){
            if($.treeInline["${id}"].tree){
                $.treeInline["${id}"].tree.destroy();
            }
            if(data){
                // 初始化树结构
                $.treeInline["${id}"].tree = $.fn.zTree.init($("#${id}tree"), $.treeInline["${id}"].setting, data);
                <#if defaultExpand>
                    $.treeInline["${id}"].tree.expandAll(true);
                </#if>

                <#if addFun?has_content>//添加节点执行函数
                    $.treeInline["${id}"].tree.addFun = "${addFun}";
                </#if>

                <#if updateFun?has_content>//添加节点执行函数
                    $.treeInline["${id}"].tree.updateFun = "${updateFun}";
                </#if>
                <#if deleteFun?has_content>//删除节点时执行函数
                    $.treeInline["${id}"].tree.deleteFun = "${deleteFun}";
                </#if>

                <#if onloadFun?has_content>//初始化完成执行函数
                    var onloadFun = typeof(${onloadFun}) == 'function' ? ${onloadFun} : eval(${onloadFun});
                    if (typeof(onloadFun) == 'function') {
                        onloadFun.call(window, data);
                    }
                </#if>
            }else{
                $.post("${url}",{extId:"${extId}", t:new Date().getTime()}, function(zNodes){

                    if(Object.prototype.toString.apply(zNodes) !== "[object Array]" && zNodes.data){
                        zNodes = zNodes.data;
                    }
                    // 初始化树结构
                    $.treeInline["${id}"].tree = $.fn.zTree.init($("#${id}tree"), $.treeInline["${id}"].setting, zNodes);
                    <#if defaultExpand>
                        $.treeInline["${id}"].tree.expandAll(true);
                    </#if>
                    <#if addFun?has_content>//添加节点执行函数
                        $.treeInline["${id}"].tree.addFun = "${addFun}";
                    </#if>

                    <#if updateFun?has_content>//添加节点执行函数
                        $.treeInline["${id}"].tree.updateFun = "${updateFun}";
                    </#if>
                    <#if deleteFun?has_content>
                        $.treeInline["${id}"].tree.deleteFun = "${deleteFun}";
                    </#if>
                    <#if onloadFun?has_content>//初始化完成执行函数
                    var onloadFun = typeof(${onloadFun}) == 'function' ? ${onloadFun} : eval(${onloadFun});
                    if (typeof(onloadFun) == 'function') {
                        onloadFun.call(window, data);
                    }
                     </#if>

                }, 'json');
            }


            $("#${id}key").bind("focus", $.treeInline["${id}"].focusKey).bind("blur", $.treeInline["${id}"].blurKey).bind("change keydown cut input propertychange", $.treeInline["${id}"].searchNode);
            $("#${id}key").data("treeId", "${id}tree");
        }
    }
    //lazy为true时需要手动调用初始化
    <#if !lazy>
        $(function(){
            <#if data?has_content>
                $.treeInline["${id}"].initTreeInline(JSON.parse("${data}"));
            <#else>
                $.treeInline["${id}"].initTreeInline();
            </#if>
        })
    </#if>
</script>
</#macro>