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
<@treeInline id="category" onselect="onSelectOrg" edit=true/>

<script type="text/javascript">

    $(function(){
        $.treeInline['category'].initTreeInline(rawdata);
    })

    function onSelectOrg(id){
        $.ajax({
            type: 'GET',
            url: "GetExamPersons?department=" + id,
            async: true,
            dataType: 'html',
            success: function (content) {
                $(".right-content").html(content);
            }
        });
    }

<#-- some labels are not unescaped in the JSON object so we have to do this manualy -->
function unescapeHtmlText(text) {
    return jQuery('<div />').html(text).text()
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
                <#if root.pId??>
                    pId: "${root.pId}",
                </#if>
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
