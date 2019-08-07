<script type="text/javascript">
    function borrowRegister(){
        displayInTab3("SubOrganizationTab", "借用预登记", {requestUrl: "borrowRegister", data:{}, width: "900px"});
       /* $.ajax({
            type: 'POST',
            url: 'borrowRegister',
            async: true,
            dataType: 'html',
            success: function (content) {
                displayInTab("OrganizationTab", "借用预登记", content, {});
            }
        });*/
    }

    function borrowRegisterConfirm(){
        displayInTab3("SubOrganizationTab", "借用预登记", {requestUrl: "borrowRegisterConfirm", data:{}, width: "900px"});
       /* $.ajax({
            type: 'POST',
            url: 'borrowRegisterConfirm',
            async: true,
            dataType: 'html',
            success: function (content) {
                displayInTab("OrganizationTab", "借用预登记", content, {});
            }
        });*/
    }
</script>

<table cellspacing="0" class="basic-table hover-bar">
    <tr class="header-row-2">
        <td>
            <label for="FixedAssetsBorrowList_a" id="FixedAssetsBorrowList_a_title">资产编码</label>  </td>
        <td>
            <label for="FixedAssetsBorrowList_b" id="FixedAssetsBorrowList_b_title">资产名称</label>  </td>
        <td>
            <label for="FixedAssetsBorrowList_c" id="FixedAssetsBorrowList_c_title">资产类别</label>  </td>
        <td>
            <label for="FixedAssetsBorrowList_d" id="FixedAssetsBorrowList_d_title">规格</label>  </td>
        <td>
            <label for="FixedAssetsBorrowList_e" id="FixedAssetsBorrowList_e_title">数量</label>  </td>
        <td>
            <label for="FixedAssetsBorrowList_f" id="FixedAssetsBorrowList_f_title">借出次数</label>  </td>
        <td>
            <label for="FixedAssetsBorrowList_g" id="FixedAssetsBorrowList_g_title">借用状态</label>  </td>
        <td>
            <label for="FixedAssetsBorrowList_h" id="FixedAssetsBorrowList_h_title">借出时间</label>  </td>
        <td>
            <label for="FixedAssetsBorrowList_i" id="FixedAssetsBorrowList_i_title">借出天数</label>  </td>
        <td>
            <label for="FixedAssetsBorrowList_j" id="FixedAssetsBorrowList_j_title">备注</label>  </td>
    </tr>

<#if fixedAssetsBorrowList?has_content>
    <#list fixedAssetsBorrowList as list>
        <tr>
            <td>${list.a}</td>
            <td>${list.b}</td>
            <td>${list.c}</td>
            <td>${list.d}</td>
            <td>${list.e}</td>
            <td>${list.f}</td>
            <td>${list.g}</td>
            <td>


                <#if list.h?contains("a")>
                    <a href="#" onclick="javascript:borrowRegister()" class="smallSubmit">我要借用</a>
                <#else >
                    <a href="#" onclick="javascript:borrowRegisterConfirm()" class="smallSubmit">借用确认</a>
                </#if>


            </td>
            <td>${list.i}</td>
            <td>${list.j}</td>
        </tr>
    </#list>
</#if>
</table>




