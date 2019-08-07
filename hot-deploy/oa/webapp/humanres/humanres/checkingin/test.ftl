<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<#include "component://common/webcommon/includes/uiWidgets/zTreeMacros.ftl"/>
<#assign departmentId = delegator.findOne("TblStaff", {"partyId" : userLoginId}, true).department>
<select onclick="showSelect()" id="testSelect" style="width: 100px">
    <option></option>
</select>
<@treeInline id="category" url="GetOrganizationTree" onselect="onSelectOrg" width="100px" height="200px" edit=false/>

<script type="text/javascript">
    $("#categorysearch").css("display","none");
    function showSelect(){
        $.treeInline['category'].initTreeInline(rawdata);
        //默认选中第一个
        var categoryTree = $.treeInline['category'].tree;
        var node = categoryTree.getNodeByParam("code", '${departmentId}', null);
        categoryTree.selectNode(node);
        onSelectOrg(node);
    }


    function onSelectOrg(node){
        alert(node.name);
        //统计默认部门一天考勤状况（饼状图）
        var selectOption = $($("#testSelect").find("option")[0]);
        selectOption.text(node.name);
        selectOption.attr("value",node.id);

        //刷新岗位
        refreshOccupationChart(node.id);
    }

    function refreshOccupationChart(partyId){
        $.ajax({
            type: 'GET',
            url: "Occupations?partyId=" + partyId,
            async: true,
            dataType: 'html',
            success: function (content) {
               // $("#occupations").html(content);
            }
        });
        $.organization.refreshOccupationDetail(partyId);
    }
    //jQuery(window).load(createTree());

    <#-- creating the JSON Data -->
    var rawdata = [
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
</script>
<#--<div id="dialog" title="Basic dialog">
</div>-->


