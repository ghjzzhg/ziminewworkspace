<div class="portlet box blue-hoki">
    <div class="portlet-title">
        <div class="caption">
            <i class="fa fa-gift"></i>${enumType.description?default('')}
        </div>
        <div class="actions">
        </div>
    </div>
    <div class="portlet-body" style="height: 400px;overflow-y: auto">
        <table class="table table-hover table-light">
            <thead>
            <tr class="uppercase">
                <th> 名称</th>
                <th> 操作</th>
            </tr>
            </thead>
        <#if enums?has_content>
            <#list enums as enum>
                <tr>
                    <td>
                        <span>${enum.description}</span>
                        <input type="text" name="description" class="form-control" value="${enum.description}"
                               style="display: none"/>
                        <input type="hidden" name="roleTypeId" value="${enum.roleTypeId}"/>
                        <input type="hidden" name="parentTypeId" value="${enum.parentTypeId}"/>
                    </td>
                    <td style="white-space: nowrap">
                        <a class="btn btn-md green btn-outline" href="#nowhere" onclick="saveEnum(this)" title="保存"
                           style="display:none"> <i
                                class="fa fa-save"></i> </a>
                        <#if enum.roleTypeId=="CASE_PERSON_ROLE_MANAGER" || enum.roleTypeId=="CASE_PERSON_ROLE_STAFF">
                        <#else>
                            <a class="btn btn-md green btn-outline" href="#nowhere" onclick="javascript:editEnum(this);"
                               title="修改"> <i
                                    class="fa fa-pencil"></i> </a>
                            <a class="btn btn-md red btn-outline" href="#nowhere" onclick="javascript:deleteEnum(this);"
                               title="删除"> <i
                                    class="fa fa-remove"></i> </a>
                        </#if>
                    </td>
                </tr>
            </#list>
        </#if>
            <tr>
                <td>
                    <span></span>
                    <input type="hidden" name="enumId"/>
                    <input type="hidden" name="enumTypeId" value="${enumType.roleTypeId}"/>
                    <input type="hidden" name="seq"/>
                    <input type="text" class="form-control" name="description"/>
                </td>
                <td style="white-space: nowrap">
                    <a class="btn btn-md green btn-outline" href="#nowhere" onclick="saveEnum(this, true)" title="保存">
                        <i
                                class="fa fa-save"></i> </a>
                    <a class="btn btn-md green btn-outline edit-btn" href="#nowhere"
                       onclick="javascript:editEnum(this);" title="修改" style="display:none"> <i
                            class="fa fa-pencil"></i> </a>
                    <a class="btn btn-md red btn-outline delete-btn" href="#nowhere"
                       onclick="javascript:deleteEnum(this);" title="删除" style="display:none"> <i
                            class="fa fa-remove"></i> </a>
                </td>
            </tr>
        </table>
    </div>
</div>