<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css"/>
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css"/>
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.min.js" type="text/javascript"></script>
<style type="text/css">
    #accounts .select-row td{
        background: lightblue!important;
    }
</style>
<script type="text/javascript">
    var accountTable;

    $(function () {
        accountTable = initDatatables("#accounts", {
            buttons: [
            ],
            "serverSide": true,
            "ajax": {
                url: "PartyPersonChooserJson",
                type: "POST",
                data: function (d) {
                    d.name = $("#name").val();
                    d.type = '${parameters.type}';
                }
            },
            "order": [
                [2, 'asc'],
            ],
            "pageLength": 5,
            "columns": [
                {
                    orderable: false,
                    "className": "dt-center",
                    "data": null,
                    "render": function (data, type, row, meta) {
                        return meta.row + 1 + "<input type='hidden' name='partyId' value='" + row.partyId + "'>" + "<input type='hidden' name='groupName' value='" + row.groupName + "'>" + "<input type='hidden' name='fullName' value='" + row.fullName + "'>";
                    }
                },
                {
                    orderable: false,
                    "name": "groupName", "data": null, "render": function(data, type, row, meta){
                        var partnerType = row.partnerType, partnerGroupName = row.partnerGroupName;
                        if(partnerGroupName){
                            return partnerGroupName;
                        }else if(partnerType){
                            return row.groupName + "<span style='color:lightskyblue'> [合伙人]</span>";
                        }else{
                            return row.groupName
                        }
                    }
                },
                {"data": "fullName"}
            ],initComplete: function(){
                var openLayer = getLayer();
                openLayer.iframeAuto(openLayer.getFrameIndex(window.name));
            }
        });
        bindClickEvent();
    })
    //账户查询
    function queryAccount() {
        accountTable.ajax.reload();
    }

    var selectedId = '', selectedName = '', selectedPersonName = '';
    function bindClickEvent(){
        $("#accounts").on("click", "td", function(){
            $(this).closest("table").find(".select-row").removeClass("select-row");
            var $tr = $(this).closest("tr");
            $tr.addClass("select-row");
            selectedId = $tr.find("input[name=partyId]").val();
            selectedName = $tr.find("input[name=groupName]").val();
            selectedPersonName = $tr.find("input[name=fullName]").val();
        }).on("dblclick", "td", function(){
            $(this).closest("table").find(".select-row").removeClass("select-row");
            var $tr = $(this).closest("tr");
            $tr.addClass("select-row");
            selectedId = $tr.find("input[name=partyId]").val();
            selectedName = $tr.find("input[name=groupName]").val();
            selectedPersonName = $tr.find("input[name=fullName]").val();
            confirmSelect();
        })
    }

    function confirmSelect(){
        if(selectedId){
            closeCurrentTab({"id" : selectedId, "name": selectedName, "person": selectedPersonName});
        }else{
            showError("请选择一条记录");
        }
    }
</script>
<div class="portlet light ">
    <div class="portlet-body">
        <div class="form-group col-sm-2 col-xs-6">
            <label class="control-label">关键字</label>
            <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-group"></i>
                        </span>
                <input type="text" name="name" class="form-control" id="name" maxlength="20"/>
            </div>
        </div>
        <div class="form-group col-sm-2 col-xs-6">
            <label class="control-label">&nbsp;</label>
            <div class="actions">
                <a href="#nowhere" onclick="queryAccount()" class="btn btn-circle green">
                    <i class="fa fa-search"></i> 查询 </a>
                <a href="#nowhere" onclick="confirmSelect()" class="btn btn-circle btn-info">
                    <i class="fa fa-check"></i> 选择 </a>
                <input hidden id="typeRemark" value="group">
            </div>
        </div>
    </div>
    <div class="portlet-body">
        <table class="table table-striped table-bordered table-hover order-column" id="accounts">
            <thead>
            <tr>
                <th>
                    序号
                </th>
                <th>企业/机构</th>
                <th>姓名</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>