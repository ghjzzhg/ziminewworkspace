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
<style type="text/css">
    .ztree li {
        line-height: 20px;
    }
    .ztree li span.button{
        top: 1px;
        font-family: 'Glyphicons Halflings';
        font-style: normal;
        font-weight: 400;
        line-height: 1em;
        -webkit-font-smoothing:antialiased;
        -moz-osx-font-smoothing:grayscale;
        background-image: none;
    }

    .ztree li span.button.pIcon01_ico_open:before{
        content: "\e021";
    }
    .ztree li span.button.pIcon01_ico_close:before{
        content: "\e021";
    }
    .ztree li span.button.pIcon01_ico_open{
        color: #0a6ebd;
    }
    .ztree li span.button.pIcon01_ico_close{
        color: darkgray;
    }

    .ztree li span.button.ico_open:before{
        content: "\e008";
    }
    .ztree li span.button.ico_close:before{
        content: "\e008";
    }
    .ztree li span.button.ico_open{
        color: #0a6ebd;
        vertical-align: middle;
    }
    .ztree li span.button.ico_close{
        color: darkgray;
        vertical-align: middle;
    }
    .ztree li span.button.icon01_ico_docu:before {
        content: "\e008";
    }
    .ztree li span.button.icon01_ico_docu {
        color: #0a6ebd;
    }

    .ztree li span.button.switch.root_open,.ztree li span.button.switch.center_open,.ztree li span.button.switch.bottom_open{
        color: #0a6ebd;
    }

    .ztree li span.button.switch.root_open:before,.ztree li span.button.switch.center_open:before,.ztree li span.button.switch.bottom_open:before{
        content: "\2212";
    }

    .ztree li span.button.switch.root_close,.ztree li span.button.switch.center_close,.ztree li span.button.switch.bottom_close{
        color: darkgray;
    }

    .ztree li span.button.switch.root_close:before,.ztree li span.button.switch.center_close:before,.ztree li span.button.switch.bottom_close:before{
        content: "\2b";
    }

</style>

<@treeInline id="${lookupId?default('')}_lookupDepartment" url="LookupOccupationTree" onselect="selectOccupation" edit=false/>
<script type="text/javascript">

    $(function(){
        var old = $.treeInline['${lookupId?default('')}_lookupDepartment'];
        if(old && old.tree){
            old.tree.destroy();
        }
        $.treeInline['${lookupId?default('')}_lookupDepartment'].initTreeInline();
    })

    function selectOccupation(id){
        <#if parameters.name?has_content>
            setLookUpInfo${parameters.name}(id,true);
        <#else>
            set_value(id,"${parameters.name}");
        </#if>
    }
</script>
