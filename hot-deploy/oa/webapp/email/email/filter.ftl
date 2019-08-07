<div>
    <style type="text/css">
        #filterTable td{
            vertical-align: top;
        }
    </style>
    <table id="filterTable" class="basic-table hover-bar">
        <tr class="header-row-2">
            <td>序号</td>
            <td>目标</td>
            <td>规则</td>
            <td>值</td>
            <td>操作</td>
            <td></td>
        </tr>
        <#list filters as filter>
            <tr class="<#if filter_index % 2 == 1>alternate-row</#if>">
                <td>
                ${filter_index + 1}
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
                </td>
                <td>
                    <select>
                        <option value="1" label="包含"></option>
                        <option value="2" label="不包含"></option>
                        <option value="3" label="等于"></option>
                        <option value="4" label="不等于"></option>
                        <option value="5" label="大于"></option>
                        <option value="6" label="小于"></option>
                        <option value="7" label="通配符等于"></option>
                    </select>
                </td>
                <td>
                    <input type="text" value="${filter.fieldValue}" style="width:100%">
                </td>
                <td>
                    <div style="display: inline-block;">
                        <select>
                            <option value="1" label="拒收" <#if filter.action == 1>selected</#if>></option>
                            <option value="2" label="转发至" <#if filter.action == 2>selected</#if>></option>
                            <option value="3" label="回复" <#if filter.action == 3>selected</#if>></option>
                            <option value="4" label="移动到" <#if filter.action == 4>selected</#if>></option>
                            <option value="5" label="停止规则处理" <#if filter.action == 5>selected</#if>></option>
                        </select>
                    </div>
                    <div class="<#if filter.action != 2>hide</#if>" style="display: inline-block">
                        <input type="text"/>
                    </div>
                    <div class="<#if filter.action != 3>hide</#if>" style="margin-top: 10px">
                        <div>
                            <label>主题:
                                <input type="text">
                            </label>
                        </div>
                        <div>
                            <label>内容:
                                <textarea style="width: 70%" rows="3"></textarea>
                            </label>
                        </div>
                    </div>
                    <div class="<#if filter.action != 4>hide</#if>">
                        移动至:
                        <select>
                            <option value="1" label="草稿箱"></option>
                            <option value="2" label="发件箱"></option>
                            <option value="3" label="垃圾箱"></option>
                            <option value="4" label="自定义"></option>
                        </select>
                    </div>
                </td>
                <td>
                    <a href="#" class="icosg-arrow-up"></a>
                    <a href="#" class="icosg-arrow-down"></a>
                    <a href="#" class="icosg-database"></a>
                    <a href="#" class="icosg-remove"></a>
                </td>
            </tr>
        </#list>
    </table>
</div>