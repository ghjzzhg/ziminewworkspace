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
<#--<style>
    .screenlet-body.vehicleClass div {
        margin: 0,0;
        border: 0;
        padding: 0;
    }
</style>-->
<script type="text/javascript">
    function changeDivBackgroundColorForTime(id) {
        var d = document.getElementById(id);
        d.style.backgroundColor = "#FF7F24";
    }
    //已审核
    function changeDivBackgroundColorForThrough(id, start, end) {
        var end = parseInt(end);
        var start = parseInt(start);
        for (start; start <= end; start++) {
            var idVar = id + start;
            var d = document.getElementById(idVar);
            d.style.backgroundColor = "#CDCD00";

        }
    }
    //已安排
    function changeDivBackgroundColorForArrange(id, start, end) {
        var end = parseInt(end);
        var start = parseInt(start);
        for (start; start <= end; start++) {
            var idVar = id + start;
            var d = document.getElementById(idVar);
            d.style.backgroundColor = "#66CD00";
        }
    }
    //已预约
    function changeDivBackgroundColorResource(id, start, end) {
        var j = parseInt(end);
        var i = parseInt(start);
        for (i; i <= j; i++) {
            var idVar = id + i;
            var d = document.getElementById(idVar);
            d.style.backgroundColor = "#FF0000";
        }
    }
  $(function () {
    <#list occupyTime as timeList>
        <#if timeList?has_content>
            changeDivBackgroundColorResource('resource1_${timeList.resourceId}_', '${timeList.startDate}', '${timeList.endDate}');
        </#if>
    </#list>
    <#list occupyTimeForThrough as timeListForThrough>
        <#if timeListForThrough?has_content>
            changeDivBackgroundColorForThrough('resource1_${timeListForThrough.resourceId}_', '${timeListForThrough.startDate}', '${timeListForThrough.endDate}');
        </#if>
    </#list>
    <#list occupyTimeForArrange as timeListForArrange>
        <#if timeListForArrange?has_content>
            changeDivBackgroundColorForArrange('resource1_${timeListForArrange.resourceId}_', '${timeListForArrange.startDate}', '${timeListForArrange.endDate}');
        </#if>
    </#list>
    })
</script>
<#assign extInfo = parameters.extInfo?default("N")>
<form name="resourceList">
    <table border="5" width="100%" style="background-color: #FFFFFF;border-color: #FF0000 ">
        <tr style="height: 45px">
            <td style=" font-size: 15px;width: 150px;white-space: nowrap;font-weight:bold">资源名称</td>
            <td style="font-size: 15px;white-space: nowrap;font-weight:bold">资源占用图例</td>
        </tr>
    <#assign time=0>
    <#list resourceListShow as list>
        <tr style="height: 40px">
            <td>${list.resourceName}</td>
            <td>
                <div>
                    <div>
                        <#list 0..48 as divList>
                            <div id="resource1_${list.resourceId}_${divList}"
                                 style="background: rgba(0, 0, 0, 0.22);float:left;margin-right: 0px;padding: 0px;border: 0px;width: 15px;height: 15px;vertical-align: top">
                                <#if divList==15>
                                    <div style="position: relative; top: -15px; margin-top: 0px;">08:00</div>
                                </#if>
                                <#if divList==0>
                                    <div style="position: relative; top: -15px; margin-top: 0px;">00:00</div>
                                </#if>
                                <#if divList==47>
                                    <div style="position: relative; top: -15px; margin-top: 0px;">24:00</div>
                                </#if>
                                <#if divList==24>
                                    <div style="position: relative; top: -15px; margin-top: 0px;">12:00</div>
                                </#if>
                                <#if divList==33>
                                    <div style="position: relative; top: -15px; margin-top: 0px;">17:00</div>
                                </#if>
                            </div>
                        </#list>
                    </div>
                </br>
                    <div style="width:800px;">
                        <#list 0..48 as divList>
                            <div id="resource1_time_${list.resourceId}_${divList}"
                                 style="background: rgba(0, 0, 0, 0.51);float:left;margin-right: 0px;padding: 0px;border: 0px;width: 15px;height: 2px;vertical-align: top">
                            </div>
                        </#list>
                    </div>
                </div>
            </td>
        </tr>
        <#list 16..23 as changeDivList>
            <script>
                changeDivBackgroundColorForTime('resource1_time_${list.resourceId}_${changeDivList}');
            </script>
        </#list>
        <#list 26..33 as changeDivList>
            <script>
                changeDivBackgroundColorForTime('resource1_time_${list.resourceId}_${changeDivList}');
            </script>
        </#list>
    </#list>
    </table>
</form>
<!-- end findEmployees.ftl -->
