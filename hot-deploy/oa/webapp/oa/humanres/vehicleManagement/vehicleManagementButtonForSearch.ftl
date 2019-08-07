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

<#assign extInfo = parameters.extInfo?default("N")>
<div style="overflow:visible;text-align:center">
    <table style="margin:auto">
        <tr>
            <td width="120">
            <@htmlTemplate.renderDateTimeField name="dateForOrderSearch" event="" action="" className="" alert="" title="Format: yyyy-MM-dd"
            value="${.now}" size="25" maxlength="30" id="dateForOrderSearch"dateType="dateFmt:'yyyy-MM-dd HH:mm:ss'" shortDateInput=true timeDropdownParamName=""
            defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName=""
            classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td width="56">
                <button href="#"  onclick="javascript:$.vehicleManagement.searchVehicleSituationForOrderByDate();" title=" ">查询</button>
            </td>
        </tr>
    </table>
</div>
<!-- end findEmployees.ftl -->
