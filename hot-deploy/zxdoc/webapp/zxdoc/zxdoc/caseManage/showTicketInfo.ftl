<script>
    $(function () {
        $("#descriptionLab").html(unescapeHtmlText('${description?default('')}'));
    })
</script>
<tr class="portlet light">
    <tr class="portlet-body">
        <table class="table table-hover table-striped table-bordered">
            <tr>
                <td class="form-group col-xs-4"><label class="control-label">标题：</label></td>
                <td class="form-group col-xs-8"><div>${title!}</div></td>
            </tr>
            <tr>
                <td class="form-group col-xs-4"><label class="control-label">开始时间：</label></td>
                <td class="form-group col-xs-8"><div>${startTime!}</div></td>
            </tr>
            <tr>
                <td class="form-group col-xs-4"><label class="control-label">结束时间：</label></td>
                <td class="form-group col-xs-8"><div>${endTime!}</div></td>
            </tr>
            <tr>
                <td class="form-group col-xs-4"><label class="control-label">模板类型：</label></td>
                <td class="form-group col-xs-8"><div>${type!}</div></td>
            </tr>
            <tr>
                <td class="form-group col-xs-4"><label class="control-label">模板名称：</label></td>
                <td class="form-group col-xs-8"><div>${version!}</div></td>
            </tr>
            <tr>
                <td class="form-group col-xs-4"><label class="control-label">参与方：</label></td>
                <td class="form-group col-xs-8"><div>
                <#if member?has_content>
                    <#list member as mem>
                        <span>${mem.type}：${mem.groupName}&nbsp;&nbsp;&nbsp;</span>
                    </#list>
                </#if>
                </div>
                </td>
            </tr>
            <tr>
                <td class="form-group col-xs-4"><label class="control-label">内容描述：</label></td>
                <td class="form-group col-xs-8"><div id="descriptionLab"></div></td>
            </tr>
        </table>
    </div>
</div>