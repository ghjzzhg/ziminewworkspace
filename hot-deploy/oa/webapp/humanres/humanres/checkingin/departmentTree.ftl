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
<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#assign departmentId = delegator.findOne("TblStaff", {"partyId" : userLoginId}, false).department>
<#assign yestedayDate = yestedayDate?string>
<label class="label">时间</label>
<@htmlTemplate.renderDateTimeField name="checkingInStatistics_date" event="onchange" action="dataChange()" className="" alert="" title="Format: yyyy-MM-dd" value="${yestedayDate}"
size="25" maxlength="30" id="fromDate1" dateType="dateFmt:'yyyy-MM-dd'" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown=""
timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>


<@treeInline id="category" url="GetOrganizationTree" onselect="onSelectOrg" edit=false/>

<script type="text/javascript">
    var searchDate = '${yestedayDate}';
    function dataChange(){
      /*  //alert($("input[name='checkingInStatistics_date']").val());
        alert(searchDate);*/
    }

    $(function(){

        $.treeInline['category'].initTreeInline(rawdata);
        //默认选中第一个
        var categoryTree = $.treeInline['category'].tree;
        var node = categoryTree.getNodeByParam("code", '${departmentId}', null);
        categoryTree.selectNode(node);
        onSelectOrg(node.id,node);
    })

    function onSelectOrg(id,node){
        //统计默认部门一天考勤状况（饼状图）
        var date = $("input[name='checkingInStatistics_date']").val();
        $.ajax({
            type: 'GET',
            url: "searcheCheckingInStatistics?department=" + id + "&" + "date=" + date,
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#CheckingInStatisticsResult").html(content);
            }
        });
        //刷新岗位
        refreshOccupationChart(id);
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
<div id="dialog" title="Basic dialog">
</div>
