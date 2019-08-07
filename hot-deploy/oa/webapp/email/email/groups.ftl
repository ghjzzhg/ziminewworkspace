<div>
    <table class="basic-table hover-bar">
        <tr class="header-row-2">
            <td>
                序号
            </td>
            <td>
                名称
            </td>
            <td>
                邮件地址
            </td>
            <td>

            </td>
        </tr>
        <#list groups as group>
            <tr>
                <td>
                    ${group_index + 1}
                </td>
                <td>
                    ${group.name}
                </td>
                <td>
                    <#list group.addresses as address>
                        <#if address_index != 0>, </#if>${address}
                    </#list>
                </td>
                <td>
                    <a href="#" class="icon-edit"></a>
                </td>
            </tr>
        </#list>
    </table>
</div>