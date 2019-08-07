<div>
    <table class="basic-table hover-bar">
        <tr>
            <td class="label">
                关键字
            </td>
            <td>
                <select>
                    <option value="1" label="发件人"></option>
                    <option value="2" label="收件人"></option>
                    <option value="3" label="抄送人"></option>
                    <option value="4" label="主题"></option>
                    <option value="5" label="正文"></option>
                    <option value="6" label="大小"></option>
                </select>
                <select>
                    <option value="1" label="包含"></option>
                    <option value="2" label="不包含"></option>
                    <option value="3" label="等于"></option>
                    <option value="4" label="不等于"></option>
                    <option value="5" label="大于"></option>
                    <option value="6" label="小于"></option>
                    <option value="7" label="通配符等于"></option>
                </select>
                <input type="text" style="width: 100px">
            </td>
        </tr>
        <tr>
            <td class="label">
                时间
            </td>
            <td>
            <@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss" value="${requestParameters.fromDate!}" size="25" maxlength="30" id="fromDate1" dateType="" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                <select>
                    <option value="1" label="之前"></option>
                    <option value="2" label="之后"></option>
                    <option value="3" label="等于"></option>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                大小
            </td>
            <td>
                <select>
                    <option value="1" label="小于"></option>
                    <option value="2" label="大于"></option>
                    <option value="3" label="等于"></option>
                </select>
                <input type="text" style="width: 100px">
            </td>
        </tr>
        <tr>
            <td class="label">
                状态
            </td>
            <td>
                <select>
                    <option value="1" label="全部"></option>
                    <option value="2" label="未读"></option>
                    <option value="3" label="已读"></option>
                </select>
            </td>
        </tr>
        <tr>
            <td class="label">
                搜索范围
            </td>
            <td>
                <label>
                    <input type="checkbox">收件箱
                </label>
                <label>
                    <input type="checkbox">草稿箱
                </label>
                <label>
                    <input type="checkbox">发件箱
                </label>
                <label>
                    <input type="checkbox">垃圾箱
                </label>
                <label>
                    <input type="checkbox">自定义
                </label>
            </td>
        </tr>
        <tr>
            <td>
            </td>
            <td>
                <a href="#" class="smallSubmit">查询</a>
            </td>
        </tr>
    </table>
</div>