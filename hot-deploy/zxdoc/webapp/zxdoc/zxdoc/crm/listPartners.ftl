<#--<script src="/images/lib/datatables/datatable.js" type="text/javascript"></script>-->
<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css"/>
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/dist-picker/distpicker.css" rel="stylesheet" type="text/css"/>
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.min.js" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<script type="text/javascript" src="/images/lib/dist-picker/distpicker.min.js"></script>
<script src="${request.contextPath}/static/crm.js?t=20170123" type="text/javascript"></script>

<style>
    .spanArea{
        display:block!important;/*内联对象需加*/
        word-break:keep-all;/* 不换行 */
        white-space:nowrap;/* 不换行 */
        overflow:hidden;/* 内容超出宽度时隐藏超出部分的内容 */
        text-overflow:ellipsis;
    }
</style>

<script type="text/javascript">
    var templateTable;
    $(function () {
        templateTable = initDatatables("#sample_1", {
            buttons: [
                {
                    text: '新建',
                    action: function (e, dt, node, config) {
                        displayInLayer('新建客户', {
                            requestUrl: 'NewPartner?partnersFlag=returnPage', height: '70%', width: '700px', end: function () {
                                templateTable.ajax.reload();
                            }
                        })
                    }, className: 'btn green btn-outline'
                },
                /*{
                    text: '导入',
                    action: function (e, dt, node, config) {
                        displayInLayer('导入文件', {
                            requestUrl: 'LoadFile', height: '50%', width: 500, end: function () {
                                templateTable.ajax.reload(null, false);
                            }
                        })
                    }, className: 'btn green btn-outline'
                },*/
                /*{extend: 'print', text: '打印', className: 'btn dark btn-outline'},
                {extend: 'pdf', className: 'btn green btn-outline'},
                {extend: 'csv', className: 'btn purple btn-outline '}*/],
            "serverSide": true,
            "ajax": {
                url: "NewPartnerJson",
                type: "POST",
                data: function (d) {
                    d.SearchCustomerName = $("#customerName").val();
                    var searchArea = "";
                    $("#distpicker option:selected").each(function(){
                        var optionVal = $(this).val();
                        if(optionVal && (!searchArea || parseInt(optionVal) > parseInt(searchArea))){
                            searchArea = optionVal;
                        }
                    })
                    d.SearchArea = searchArea;
                    console.log(searchArea);
                    d.SearchCustomerLevel = $("#customerLevel").val();
                    d.SearchCustomerStatus = $("#customerStatus").val();
                }
            },
            "order": [
                [1, 'asc'],
            ],
            "columns": [
                {"data": null,"targets": 0, orderable: false},
//                {"data": "customerName"},
                {
                    "data": null, "name": "customerName", "render": function (data, type, row, meta) {
                    var customerName = row.customerName == null ?"":row.customerName;
                    var name = row.customerName == null ?"":row.customerName;
//                    if(customerName.length>20)
//                    {
//                        name = name.substring(0,20) + "...";
//                    }
                    return Mustache.render($("#customerNameTpl").html(), {
                        partyId: row.partyId,
                        id: row.id,
                        customerName: customerName,
                        name:name,
                    });
                }
                },
                {"data": "geoName"},
                {"data": null,"orderable":false,"render": function (data, type, row) {
                        var address = row.address1== null ?"":row.address1;
                        var address1 = row.address1 == null ?"":row.address1;
//                        if(address.length>20)
//                        {
//                            address = address.substring(0,20) + "...";
//                        }
                        return "<span title='"+address1+"'>"+address+"</span>";
                    }
                },
                {"data": "infoString","render": function (data, type, row) {
                    var infoString = row.infoString==null?"":row.infoString;
                    var infoString1 = row.infoString==null?"":row.infoString;
//                    if(infoString!=null&&infoString.length>20)
//                    {
//                        infoString = infoString.substring(0,20) + "...";
//                    }
                    return "<a href='"+infoString1+"' target='_blank'>"+infoString+"</a>";
                }
                },
                {"data": "descriptionLevel"},
                {"data": "descriptionStatus"},
                {
                    orderable: false, "data": null, "render": function (data, type, row) {
                    return Mustache.render($("#operatorCol").html(), {partyId: row.partyId,id: row.id});
                }
                }
            ],
            "fnDrawCallback": function(){
                var api = this.api();
                var startIndex= api.context[0]._iDisplayStart;//获取到本页开始的条数
                api.column(0).nodes().each(function(cell, i) {
                    //此处 startIndex + i + 1;会出现翻页序号不连续，主要是因为startIndex 的原因,去掉即可。
                    cell.innerHTML = startIndex + i + 1;
                });
            }
        });
        $("#distpicker").distpicker({valueType: "code"});
    })
</script>
<script id="operatorCol" type="text/html">
    <a class="btn btn-md green btn-outline" href="javascript:$.partner.editPartner('{{partyId}}','{{id}}');" title="编辑"> <i
            class="fa fa-pencil"></i> </a>
    <a class="btn btn-md red btn-outline" href="javascript:$.partner.deletePartner('{{partyId}}','{{id}}');" title="删除"> <i
            class="fa fa-remove"></i> </a>
</script>
<script id="customerNameTpl" type="text/html">
    <a href="javascript:$.partner.showPartner('{{partyId}}','{{id}}');" title="{{customerName}}">{{name}}</a>
</script>
<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">我的客户</span>
        </div>
        <div class="tools"></div>
        <div class="actions">
            <a href="#nowhere" onclick="javascript:$.partner.searchPartner();" class="btn btn-circle green">
                <i class="fa fa-search"></i> 查询 </a>
        </div>
    </div>
    <div class="portlet-body">
        <div class="row">
            <div class="form-group col-sm-2">
                <label class="control-label">客户名称</label>
                <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-group"></i>
                        </span>
                    <input type="text" id="customerName" name="customerName" class="form-control" maxlength=20/>
                </div>
            </div>
            <div class="form-group col-sm-2">
                <label class="control-label">等级</label>
                <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-user"></i>
                        </span>
                    <select class="form-control" name="customerLevel" id="customerLevel">
                        <option value="">-请选择-</option>
                    <#if List2?has_content>
                        <#list List2 as list>
                            <option value="${list.enumId?default('')}">${list.description?default('')}</option>
                        </#list>
                    </#if>
                    </select>
                </div>
            </div>
            <div class="form-group col-sm-2">
                <label class="control-label">状态</label>
                <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-user"></i>
                        </span>
                    <select class="form-control" id="customerStatus">
                        <option value="">-请选择-</option>
                    <#if List3?has_content>
                        <#list List3 as list1>
                            <option value="${list1.enumId?default('')}">${list1.description?default('')}</option>
                        </#list>
                    </#if>
                    </select>
                </div>
            </div>
            <div class="form-group col-sm-6" >
                <label class="control-label">区域</label>
                <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-user"></i>
                        </span>
                    <div><!-- container -->
                        <div id="distpicker" class="distpicker spanArea">
                            <select id="province" class="form-control"></select>
                            <select id="city" class="form-control"></select>
                            <select id="district" class="form-control"></select>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">客户列表</span>
        </div>
        <div class="tools"></div>
    </div>
    <div class="portlet-body">
        <table class="table table-striped table-bordered table-hover order-column" id="sample_1">
            <thead>
            <tr>
                <td style="min-width: 30px">
                    <label>序号</label>
                </td>
                <td style="min-width: 60px">
                    <label>客户名称</label>
                </td>
                <td>
                    <label>省、市、区</label>
                </td>
                <td style="min-width: 60px">
                    <label>详细地址</label>
                </td>
                <td style="min-width: 60px">
                    <label>网站地址</label>
                </td>
                <td style="min-width: 60px">
                    <label>客户等级</label>
                </td>
                <td style="min-width: 60px">
                    <label>客户状态</label>
                </td>
                <td style="min-width: 90px">
                    <label>操作</label>
                </td>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>