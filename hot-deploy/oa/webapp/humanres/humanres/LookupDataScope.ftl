<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<style type="text/css">
    .ms-container ul.ms-list{
        height: 350px;
    }
    .yui3-tabview-panel{
        float:left;
        width: 100%;
    }
</style>
<link type="text/css" rel="stylesheet" href="/hr/static/orgTree.css"/>
<script type="text/javascript" src="/images/lib/yui/yui-min.js"></script>
<div class="yui3-skin-sam">
    <div class="col-xs-8">
        <div id="dataScopeTypes">
            <ul>
            <#if dept>
                <li><a href="#dept">按部门</a></li>
            </#if>
            <#if position>
                <li><a href="#position">按岗位</a></li>
            </#if>
            <#if level>
                <li><a href="#level">按职级</a></li>
            </#if>
            <#if user>
                <li><a href="#user">按人员</a></li>
            </#if>
            </ul>
            <div>
            <#if dept>
                <div id="dept">
                    <div>
                        <div class="col-xs-4" style="padding: 0">
                            <@treeInline id="dataScopeDeptTree" url="GetOrganizationTree?dummyRoot=true" onselect="onDataScopeDept" height="330px" edit=false/>
                        </div>
                        <div class="col-xs-8">
                            <select id="dataScopeDeptSelect" multiple="multiple">

                            </select>
                        </div>
                    </div>
                    <div>
                        <#if like>
                            <label>
                                <input type="radio" name="deptType" value="only" checked>仅该部门
                            </label>
                            <label>
                                <input type="radio" name="deptType" value="like">包括下属部门
                            </label>
                        </#if>
                    </div>
                </div>
            </#if>
            <#if position>
                <div id="position">
                    <div>
                        <div class="col-xs-4" style="padding: 0">
                            <@treeInline id="dataScopePositionTree" url="LookupOccupationTree?includePartyId=false" onselect="onDataScopePosition" height="330px" edit=false/>
                        </div>
                        <div class="col-xs-8">
                            <select id="dataScopePositionSelect" multiple="multiple">

                            </select>
                        </div>
                    </div>
                    <div>
                        <#if like>
                            <label>
                                <input type="radio" name="positionType" value="only" checked>仅该岗位
                            </label>
                            <label>
                                <input type="radio" name="positionType" value="like">包括下属岗位
                            </label>
                        </#if>
                    </div>
                </div>
            </#if>
            <#if level>
                <div id="level">
                    <div>
                        <select id="dataScopeLevelSelect" multiple="multiple">
                            <#if levelData?has_content>
                                <#list levelData as level>
                                    <option value="${level.enumId}">${level.description}</option>
                                </#list>
                            </#if>
                        </select>
                    </div>
                    <div>
                        <#if like>
                            <label>
                                <input type="radio" name="levelType" value="only" checked>仅该职级
                            </label>
                            <label>
                                <input type="radio" name="levelType" value="like">包括更高职级
                            </label>
                        </#if>
                    </div>
                </div>
            </#if>
            <#if user>
                <div id="user">
                    <div>
                        <div class="col-xs-4">
                            <@treeInline id="dataScopeDeptTree2" url="GetOrganizationTree" onselect="onDataScopeDept2" height="330px" edit=false/>
                        </div>
                        <div class="col-xs-8">
                            <select id="dataScopeUserSelect" multiple="multiple">

                            </select>
                        </div>
                    </div>
                </div>
            </#if>
            </div>
        </div>
    </div>
    <div class="col-xs-4" style="padding: 0">
        <div id="dataScopeSelected">
            <ul>
                <li><a href="#dataScopeSelectedTab">已选范围</a></li>
            </ul>
            <div>
                <div id="dataScopeSelectedTab" class="ms-container">
                    <div class="ms-selection" style="width:100%">
                        <ul class="ms-list">
                        <#if dept>
                            <#if deptOnly?has_content>
                                <#list deptOnly?split(",", "r") as deptId>
                                    <#assign deptEntity = delegator.findOne("PartyGroup", {"partyId" : deptId}, true)/>
                                    <#if deptEntity?has_content>
                                        <li value_id='${deptId}' value_type='dept' onclick="removeSelectedItem(this)"  value_like='false' class='ms-elem-selection ms-selected' style='display: list-item;'>
                                            <span>${deptEntity.get("groupName")}</span>
                                        </li>
                                    </#if>
                                </#list>
                            </#if>
                            <#if deptLike?has_content>
                                <#list deptLike?split(",", "r") as deptId>
                                    <#assign deptEntity = delegator.findOne("PartyGroup", {"partyId" : deptId}, true)/>
                                    <#if deptEntity?has_content>
                                        <li value_id='${deptId}' value_type='dept' onclick="removeSelectedItem(this)" value_like='true' class='ms-elem-selection ms-selected' style='display: list-item;'>
                                            <span>${deptEntity.get("groupName")}<i style='color:deepskyblue'>及其下属部门</i></span>
                                        </li>
                                    </#if>
                                </#list>
                            </#if>
                        </#if>
                        <#if position && positionOnly?has_content>
                            <#if positionOnly?has_content>
                                <#list positionOnly?split(",", "r") as positionId>
                                    <#assign pos = delegator.findOne("RoleType", {"roleTypeId" : positionId}, true)/>
                                    <#if pos?has_content>
                                        <li value_id='${positionId}' value_type='position' onclick="removeSelectedItem(this)" value_like='false' class='ms-elem-selection ms-selected' style='display: list-item;'>
                                            <span>${pos.get("description")}</span>
                                        </li>
                                    </#if>
                                </#list>
                            </#if>
                            <#if positionLike?has_content>
                                <#list positionLike?split(",", "r") as positionId>
                                    <#assign pos = delegator.findOne("RoleType", {"roleTypeId" : positionId}, true)/>
                                    <#if pos?has_content>
                                        <li value_id='${positionId}' value_type='position' onclick="removeSelectedItem(this)" value_like='true' class='ms-elem-selection ms-selected' style='display: list-item;'>
                                            <span>${pos.get("description")}<i style='color:deepskyblue'>及其下属岗位</i></span>
                                        </li>
                                    </#if>
                                </#list>
                            </#if>
                        </#if>
                        <#if level && levelOnly?has_content>
                            <#if levelOnly?has_content>
                                <#list levelOnly?split(",", "r") as levelId>
                                    <#assign levelEntity = delegator.findOne("Enumeration", {"enumId" : levelId}, true)/>
                                    <#if levelEntity?has_content>
                                        <li value_id='${levelId}' value_type='level' onclick="removeSelectedItem(this)" value_like='false' class='ms-elem-selection ms-selected' style='display: list-item;'>
                                            <span>${levelEntity.get("description")}</span>
                                        </li>
                                    </#if>
                                </#list>
                            </#if>
                            <#if levelLike?has_content>
                                <#list levelLike?split(",", "r") as levelId>
                                    <#assign levelEntity = delegator.findOne("Enumeration", {"enumId" : levelId}, true)/>
                                    <#if levelEntity?has_content>
                                        <li value_id='${levelId}' value_type='level' onclick="removeSelectedItem(this)"  value_like='false' class='ms-elem-selection ms-selected' style='display: list-item;'>
                                            <span>${levelEntity.get("description")}<i style='color:deepskyblue'>及更高岗位</i></span>
                                        </li>
                                    </#if>
                                </#list>
                            </#if>
                        </#if>
                        <#if user && userValue?has_content>
                            <#list userValue?split(",", "r") as partyId>
                                <#assign userEntity = delegator.findOne("Person", {"partyId" : partyId}, true)/>
                                <#if user?has_content>
                                    <li value_id='${partyId}' value_type='user' onclick="removeSelectedItem(this)" value_like='false' class='ms-elem-selection ms-selected' style='display: list-item;'>
                                        <span><#if userEntity.get("fullName")?has_content>${userEntity.get("fullName")}<#else>${userEntity.get("lastName")}${userEntity.get("firstName")}</#if></span>
                                    </li>
                                </#if>
                            </#list>
                        </#if>
                        </ul>
                    </div>
                </div>
                <label>&nbsp;</label>
            </div>
        </div>
    </div>
    <div class="col-xs-12" style="margin-top: 5px">
        <a href="#nowhere" class="smallSubmit btn btn-primary" onclick="confirmScopes()">确定</a>
        <a href="#nowhere" class="smallSubmit btn btn-danger" onclick="closeCurrentTab()">取消</a>
    </div>
</div>

<script type="text/javascript">
    function removeSelectedItem(obj){
        if(confirm('确定要删除此选项吗？删除后点击确定按钮生效。')){
            $(obj).remove();
        }

    }
    <#if dept>
    var $dataScopeDeptSelect = $("#dataScopeDeptSelect");
    function onDataScopeDept(id, node){
        $dataScopeDeptSelect.multiSelect('deselect_all');
        $dataScopeDeptSelect.multiSelect('destroy');
        $dataScopeDeptSelect.find("option").remove();

        var deptTree = $.treeInline['dataScopeDeptTree'].tree;
        var nodes = deptTree.getNodesByParam("pId", id, null);
        if(nodes.length){
            for(var i in nodes){
                $dataScopeDeptSelect.append("<option value='" + nodes[i].id + "' label='" + nodes[i].name + "'>" + nodes[i].name + "</option>");
            }
        }else{
            $dataScopeDeptSelect.append("<option value='" + node.id + "' label='" + node.name + "'>" + node.name + "</option>");
        }
        $("#dataScopeDeptSelect").multiSelect({keepOrder: true, afterSelect:afterDataScopeDeptSelect, afterDeselect: afterDataScopeDeptDeselect});
    }

    function afterDataScopeDeptSelect(value){
        if($("#dataScopeSelectedTab li[value_type='dept'][value_id='" + value + "']").length){
            afterDataScopeDeptDeselect(value);
        }
        //start Author:dengbeibei
        var isInsert = true;
        $("#dataScopeSelectedTab .ms-list li").each(function(){
            var $this = $(this), v = $this.attr("value_id");
           var pId;
            //如果已经选择的部门有现在选择的部门的子部门，则将其子部门移除，保留该部门
            $(deptTreeData).each(function(){
                if(v==$(this).get(0).id){
                    pId = $(this).get(0).pId;
                }
            });
            if(pId==value){
                $(this).remove();
            }
            //如果已经选择的部门有现在选择的部门的直接父部门，则该部门不能添加成功
            $(deptTreeData).each(function(){
                if(value==$(this).get(0).id){
                    pId = $(this).get(0).pId;
                }
            });
            if(pId==v){
                isInsert = false;
            }
        });
        if(isInsert){//判断是否可以添加新选择的项
            var text = $("#dataScopeDeptSelect option[value='" + value + "']").text();
            var isLike = $("input[name=deptType]:checked").val() === 'like';
            if(isLike){
                text += "<i style='color:deepskyblue'>及其下属部门</i>";
            }
            $("#dataScopeSelectedTab .ms-list").append("<li onclick='removeSelectedItem(this);' value_id='" + value + "' value_type='dept' value_like='" + isLike + "' class='ms-elem-selection ms-selected' style='display: list-item;'>"
                    + "<span>" + text + "</span>"
                    + "</li>");
        }
        //end
    }

    function afterDataScopeDeptDeselect(value){
        $("#dataScopeSelectedTab li[value_type='dept'][value_id='" + value + "']").remove();
    }
    </#if>
    <#if position>
    var $dataScopePositionSelect = $("#dataScopePositionSelect");
    function onDataScopePosition(id, node){
        $dataScopePositionSelect.multiSelect('deselect_all');
        $dataScopePositionSelect.multiSelect('destroy');
        $dataScopePositionSelect.find("option").remove();

        var deptTree = $.treeInline['dataScopePositionTree'].tree;
        var nodes = deptTree.getNodesByParam("pId", id, null);
        if(nodes.length){
            for(var i in nodes){
                $dataScopePositionSelect.append("<option value='" + nodes[i].id + "' label='" + nodes[i].name + "'>" + nodes[i].name + "</option>");
            }
        }else{
            $dataScopePositionSelect.append("<option value='" + node.id + "' label='" + node.name + "'>" + node.name + "</option>");
        }
        $("#dataScopePositionSelect").multiSelect({keepOrder: true, afterSelect:afterDataScopePositionSelect, afterDeselect: afterDataScopePositionDeselect});
    }

    function afterDataScopePositionSelect(value){
        if($("#dataScopeSelectedTab li[value_type='position'][value_id='" + value + "']").length){
            afterDataScopePositionDeselect(value);
        }
        var text = $("#dataScopePositionSelect option[value='" + value + "']").text();
        var isLike = $("input[name=positionType]:checked").val() === 'like';
        if(isLike){
            text += "<i style='color:deepskyblue'>及其下属岗位</i>";
        }
        $("#dataScopeSelectedTab .ms-list").append("<li onclick='removeSelectedItem(this);' value_id='" + value + "' value_type='position' value_like='" + isLike + "' class='ms-elem-selection ms-selected' style='display: list-item;'>"
                + "<span>" + text + "</span>"
                + "</li>");
    }

    function afterDataScopePositionDeselect(value){
        $("#dataScopeSelectedTab li[value_type='position'][value_id='" + value + "']").remove();
    }

    </#if>
    <#if level>
    var $dataScopeLevelSelect = $("#dataScopeLevelSelect");


    function afterDataScopeLevelSelect(value){
        if($("#dataScopeSelectedTab li[value_type='level'][value_id='" + value + "']").length){
            afterDataScopeLevelDeselect(value);
        }
        var text = $("#dataScopeLevelSelect option[value='" + value + "']").text();
        var isLike = $("input[name=levelType]:checked").val() === 'like';
        if(isLike){
            text += "<i style='color:deepskyblue'>及更高职级</i>";
        }
        $("#dataScopeSelectedTab .ms-list").append("<li onclick='removeSelectedItem(this);' value_id='" + value + "' value_type='level' value_like='" + isLike + "' class='ms-elem-selection ms-selected' style='display: list-item;'>"
                + "<span>" + text + "</span>"
                + "</li>");
    }

    function afterDataScopeLevelDeselect(value){
        $("#dataScopeSelectedTab li[value_type='level'][value_id='" + value + "']").remove();
    }
    </#if>
    <#if user>
    var $dataScopeUserSelect = $("#dataScopeUserSelect");

    function onDataScopeDept2(id){
        $dataScopeUserSelect.multiSelect('deselect_all');
        $dataScopeUserSelect.multiSelect('destroy');
        $dataScopeUserSelect.find("option").remove();

        var deptTree = $.treeInline['dataScopeDeptTree2'].tree;
        var nodes = deptTree.getNodesByParam("pId", id, null);
        if(nodes.length){
            for(var i in nodes){
                var node = nodes[i];
                if(node.isHidden){
                    $dataScopeUserSelect.append("<option value='" + node.code + "' label='" + node.name + "'>" + node.name + "</option>");
                }
            }
        }

        $dataScopeUserSelect.multiSelect({keepOrder: true, afterSelect:afterStaffSelect, afterDeselect: afterStaffDeselect});
    }

    function afterStaffSelect(value){
        if($("#dataScopeSelectedTab li[value_id='" + value + "']").length){
            afterStaffDeselect(value);
        }
        var text = $("#dataScopeUserSelect option[value='" + value + "']").text();
        $("#dataScopeSelectedTab .ms-list").append("<li onclick='removeSelectedItem(this);' value_id='" + value + "' value_type='user' class='ms-elem-selection ms-selected' style='display: list-item;'>"
                + "<span>" + text + "</span>"
                + "</li>");
    }

    function afterStaffDeselect(value){
        $("#dataScopeSelectedTab li[value_id='" + value + "']").remove();
    }
    </#if>
    function confirmScopes(){
        var values = {
            dept_only:[],
            dept_like:[],
            level_only:[],
            level_like:[],
            position_only:[],
            position_like:[],
            user_value:[]
        };
        var selectionText = [];
        $("#dataScopeSelectedTab .ms-list li").each(function(){
            var $this = $(this), isLike = $this.attr("value_like"), value = $this.attr("value_id"), type = $this.attr("value_type");
            if(type === "user"){
                values.user_value.push(value);
            }else{
                values[type + (isLike === "true" ? '_like' : '_only')].push(value);
            }
            selectionText.push($this.html());
        });
        for(var key in values){
            if(key == "user_value"){
                $("input[name=${lookupName}_user]").val(values[key].join(","));
            }else{
                $("input[name=${lookupName}_" + key + "]").val(values[key].join(","));
            }
        }
        $("#${lookupId}").html(selectionText.join(" "));
        $("input[name^=${lookupName}][class]").trigger("blur");
        closeCurrentTab();
    }

    YUI({
        base:'/images/lib/yui/'
    }).use('tabview', function (Y) {
        var tabview = new Y.TabView({srcNode:'#dataScopeTypes'});
        tabview.render();
        tabview = new Y.TabView({srcNode:'#dataScopeSelected'});
        tabview.render();
    });


    <#-- creating the JSON Data -->
    var deptTreeData = [
    <#if (completedTreeContext?has_content)>
        <@fillTree rootCat = completedTreeContext/>
    </#if>

    <#macro fillTree rootCat>
        <#if (rootCat?has_content)>
            <#list rootCat as root>
            {
                id: "${root.id}",
                code: "${root.code}",
                pId: "${root.pId}",
                iconSkin: "${root.iconSkin?default('')}",
            isHidden: <#if root.isHidden?has_content>${root.isHidden?c}<#else>false</#if>,
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

    var staffData = [
    <#if (staffData?has_content)>
        <@fillTree rootCat = staffData/>
    </#if>
    ]

    var occupationData = [
    <#if (occupations?has_content)>
        <@fillTree rootCat = occupations/>
    </#if>
    ]

    <#if dept>
    $.treeInline['dataScopeDeptTree'].initTreeInline(deptTreeData);
    $dataScopeDeptSelect.multiSelect({keepOrder: true});
    </#if>
    <#if position>
    $.treeInline['dataScopePositionTree'].initTreeInline(occupationData);
    $dataScopePositionSelect.multiSelect({keepOrder: true});
    </#if>
    <#if user>
    $.treeInline['dataScopeDeptTree2'].initTreeInline(deptTreeData.concat(staffData));
    $dataScopeUserSelect.multiSelect({keepOrder: true});
    </#if>


    <#if level>
    $dataScopeLevelSelect.multiSelect({keepOrder: true, afterSelect:afterDataScopeLevelSelect, afterDeselect: afterDataScopeLevelDeselect});
    </#if>

</script>
