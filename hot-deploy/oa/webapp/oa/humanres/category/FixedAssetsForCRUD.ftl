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
<#--<@treeInline id="category" onselect="onSelectOrg" edit=true/>-->

<script type="text/javascript">
function createFixedAssets(){
    $.ajax({
        type: 'GET',
        url: 'EditFixedAssets',
        async: true,
        dataType: 'html',
        success:function (content) {
            displayInTab("fixedAssetsTab", "新增固定资产", content, {width:500, height:250});
        }
    });
}
function saveFixedAssets(){
    alert("12345");
    /*  var options = {
          beforeSubmit: function () {
              //return $("#EditOrganization").validationEngine("validate");
              return true;
          }, // pre-submit callback
          dataType: "json",
          success: function (data) {
              closeCurrentTab();
              *//* showInfo(data.data.msg);
                 if(callback){
                 var callbackObj = typeof(callback) == 'function' ? callback : eval(callback);
                 if (typeof(callbackObj) == 'function') {
                 callbackObj.call(window, data);
                 }
                 }*//*
            },

            url: "saveFixedAssets", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#EditFixedAssets").ajaxSubmit(options);*/
}

</script>
<div id="dialog" title="Basic dialog">
</div>
