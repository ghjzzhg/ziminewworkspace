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
    <div class="page-title">
        <span>车辆管理</span>
    </div>
    <table>
        <tr>
            <td style=" font-size: 10px;font-weight:bold" width="70">使用日期：</td>
            <td width="120">
                <#--<input name="searchDate" class="searchDate" id="searchDate" type="date"/>-->
                <@htmlTemplate.renderDateTimeField name="searchDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd"
                value="" size="25" maxlength="30" id="searchDate" dateType="" shortDateInput=true timeDropdownParamName=""
                defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName=""
                classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
            </td>
            <td>
                <button href="#"  onclick="javascript:$.vehicleManagement.searchVehicleSituationByDate();" title=" ">查询</button>
            </td>
            <td>
                <button href="#" onclick="javascript:$.vehicleManagement.orderVehicle();" title=" ">预约</button>
            </td>
            <td>
                <button href="#" onclick="javascript:$.vehicleManagement.vehicleForCost();" title=" ">费用管理</button>
            </td>
            <td>
                <button href="#" onclick="javascript:$.vehicleManagement.createVehicle();" title=" ">增加车辆</button>
            </td>
            <td>
                <button href="#" onclick="javascript:$.vehicleManagement.listVehicle()" title=" ">编辑车辆</button>
            </td>
        </tr>
    </table>
<!-- end findEmployees.ftl -->
