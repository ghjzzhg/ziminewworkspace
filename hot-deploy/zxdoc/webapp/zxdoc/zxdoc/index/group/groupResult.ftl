<div class="table-scrollable">
    <table class="table table-striped table-bordered table-advance table-hover" style="color: #000000;margin:0 auto;">
        <thead>
        <tr>
            <th>
                <i class="fa fa-briefcase"></i> 公司
            </th>
            <th style="width: 20%">
                <i class="fa fa-user"></i> 联系人
            </th>
            <th style="width: 20%">
                <i class="glyphicon glyphicon-earphone"></i> 联系方式
            </th>
            <th style="width: 10%"></th>
        </tr>
        </thead>
        <tbody>
        <#if parameters.groupList?has_content>
            <#list parameters.groupList as group>
            <tr style="width: 100%">
                <td class="highlight" style="line-height: 28px;width: 50%">
                    <div style="text-align: left;float: left">
                        <i class="glyphicon glyphicon-lock" style="color: #00a0e9" title="公司"></i>
                    </div>
                    <span style="float: left;font-size: 14px;margin-left: 10px;">
                    ${group.groupName?default('')} </span>
                </td>
                <td class="hidden-xs" style="width: 20%;line-height: 28px;">
                    <div style="float: left">
                        <i class="glyphicon glyphicon-user" style="color: red" title="联系人"></i>
                    </div>
                    <span style="float: left;font-size: 14px;margin-left: 10px;">
                    ${group.fullName?default('')}
                    </span>
                </td>
                <td style="width: 20%;line-height: 28px;">
                    <div style="float: left">
                        <i class="fa fa-mobile" style="font-size: 24px;color: green" title="联系方式"></i>
                    </div>
                    <span style="float: left;font-size: 14px;margin-left: 10px;">
                    ${group.contactNumber?default('')}
                    </span>
                </td>
                <td style="width: 10%">
                    <a href="${request.contextPath}/control/groupInfo?partyId=${group.partyId}"
                       class="btn btn-outline btn-circle btn-sm purple" title="更多">
                        <i class="glyphicon glyphicon-list-alt"></i> 更多 </a>
                </td>
            </tr>
            </#list>
        </#if>

        </tbody>
    </table>
</div>
