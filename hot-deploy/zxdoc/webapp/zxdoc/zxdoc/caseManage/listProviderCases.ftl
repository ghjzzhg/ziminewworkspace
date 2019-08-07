<#--<script src="/images/lib/datatables/datatable.js" type="text/javascript"></script>-->
<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css"/>
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css"/>
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.min.js" type="text/javascript"></script>
<script src="${request.contextPath}/static/caseManage.js?t=20171023" type="text/javascript"></script>
<script type="text/javascript">
    var caseTable;
    $(function () {
        caseTable = initDatatables("#sample_1",{
            buttons: [
                {
                    text: '新建',
                    action: function (e, dt, node, config) {
                        displayInLayer('创建CASE', {
                            requestUrl: 'ChooseCaseTemplate', height: '80%', width: '80%', layer: {
                                end: function () {
                                    caseTable.ajax.reload()
                                }
                            }
                        })
                    },
                    className: 'btn green btn-outline'
                }/*,
                {extend: 'print', text: '打印', className: 'btn dark btn-outline'},
                {extend: 'pdf', className: 'btn green btn-outline'},
                {extend: 'csv', className: 'btn purple btn-outline '}*/
            ],
            ajax: {
                url: "getProviderCases",
                type: "POST",
                data: function(d){
                    d.customerName = $("#customerName").val();
                    return d;
                }
            },
            "order": [
                [5, 'desc'],
            ],
            "columns": [
                {"data": null,"targets": 0, orderable: false},
                {"data": null, "render": function (data, type, row) {
                    var groupName = data.groupName ==null?"":row.groupName;
                    var title = data.groupName ==null?"":row.groupName;
//                    if(groupName.length>10)
//                    {
//                        groupName = groupName.substring(0,10) + "...";
//                    }
                    return "<span title='"+title+"'>" + groupName + "</span>";
                    }
                },
                {
                    "data": null, "render": function (data, type, row) {
                        var caseTitle = data.title ==null?"":row.title;
                        var title = data.title ==null?"":row.title;
//                        if(caseTitle.length>18)
//                        {
//                            caseTitle = caseTitle.substring(0,18) + "...";
//                        }
                        if(data.status != "CASE_STATUS_ARCHIVED"){
                            return Mustache.render($("#toCaseHome").html(), {caseId: data.caseId, caseTitle: caseTitle,title:title});
                        }else{
                            return "<span title='"+title+"'>" + caseTitle + "</span>";
                        }

                    }
                },
                {
                    "data": null, "render": function (data, type, row) {
                    var joinGroups = data.joinGroups  ==null?"":row.joinGroups;
                    var title = data.joinGroups  ==null?"":row.joinGroups;
//                    if(joinGroups.length>18)
//                    {
//                        joinGroups = joinGroups.substring(0,18) + "...";
//                    }
                    return "<span title='"+title+"'>" + joinGroups + "</span>";
                }
                },
                {"data": "fullName"},
                {"data": "startDate"},
                {"data": "dueDate"},
                {"data": "completeDate"},
                {
                    "data": null, "render": function (data, type, row) {
                    //TODO 状态
                    if(data.status != null && data.status == "CASE_STATUS_FINISH"){
                        return "<div style='display: none'>已完成</div><i class='font-green fa fa-circle-o' title='已完成'></i>"
                    }else if(data.status != null && data.status == "CASE_STATUS_ARCHIVED"){
                        return "<div style='display: none'>已归档</div><i class='font-green fa fa-dot-circle-o' title='已归档'></i>"
                    }else{
                        return "<div style='display: none'>进行中</div><i class='font-green fa fa-hourglass-half' title='进行中'></i>"
                    }

                }
                },
                {
                orderable: false, "data": null, "render": function (data, type, row) {
                        if(data.status != "CASE_STATUS_ARCHIVED"){
                            if(data.caseOwner){
                                var html = $("#operations").html() + $("#operations1").html()
                                return Mustache.render(html, {caseId: data.caseId});
                            }else if (data.caseAscription){
                                return Mustache.render($("#operations").html(), {caseId: data.caseId});
                            }
                        }else{
                            return "";
                        }
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
    })

    function searchProviderCase(){
        caseTable.ajax.reload();
    }
</script>
<script id="toCaseHome" type="text/html">
    <a href="#nowhere" onclick="displayInside('${request.contextPath}/control/CaseHome?caseId={{caseId}}')" title="{{title}}">{{caseTitle}}</a>
</script>
<script id="operations1" type="text/html">
    <span style="white-space: nowrap">
    <a class="btn btn-md red btn-outline" href="javascript:$.caseManage.updateCaseFiledStatus('{{caseId}}');" name="{{caseId}}" title="归档"> <i class="fa fa-ban"></i>
    </a>
        </span>
</script>
<script id="operations" type="text/html">
    <span style="white-space: nowrap">
    <a class="btn btn-md green btn-outline" href="#nowhere" onclick="$.caseManage.updateCaseBasicInfo('{{caseId}}')" title="修改"> <i
            class="fa fa-pencil"></i> </a>
        </span>
</script>
<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">我的CASE</span>
        </div>
        <div class="tools"></div>
        <div class="actions">
            <a href="#nowhere" onclick="javascript:searchProviderCase();" class="btn btn-circle green">
                <i class="fa fa-search"></i> 查询 </a>
        </div>
    </div>
    <div class="portlet-body">
        <div class="row">
            <div class="form-group col-sm-2">
                <label class="control-label">企业名称</label>
                <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-group"></i>
                        </span>
                    <input type="text" id="customerName" name="customerName" class="form-control" maxlength=20/>
                </div>
            </div>
            <#--<div class="form-group col-sm-2">
                <label class="control-label">等级</label>
                <div class="input-group">
                    <span class="input-group-addon">
                        <i class="fa fa-user"></i>
                    </span>
                    <select class="form-control" name="customerLevel" id="customerLevel">
                        <option>-请选择-</option>
                    </select>
                </div>
            </div>-->
        </div>
    </div>
    <div class="portlet-body">
        <table class="table table-striped table-bordered table-hover order-column" id="sample_1">
            <thead>
            <tr>
                <th>序号</th>
                <th>企业名称</th>
                <th>CASE标题</th>
                <th>参与方</th>
                <th>创建人</th>
                <th>开始时间</th>
                <th>预计结束</th>
                <th>结束时间</th>
                <th>状态</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <#--<tr>-->
                <#--<td>1</td>-->
                <#--<td>苏宁环球</td>-->
                <#--<td><a href="#nowhere" onclick="displayInside('${request.contextPath}/control/CaseHome?caseId=')">2016年度年度公告</a></td>-->
                <#--<td>XX律师事务所、XX券商</td>-->
                <#--<td>2016-07-18 10:10</td>-->
                <#--<td>2016-07-28 10:10</td>-->
                <#--<td>无</td>-->
                <#--<td><i class="font-red fa fa-unlock" title="缺少服务提供商"></i></td>-->
                <#--<td>-->
                    <#--<a class="btn btn-md green btn-outline" href="javascript:;" title="开始协作"> <i class="fa fa-comments"></i> </a>-->
                    <#--<a class="btn btn-md red btn-outline" href="javascript:;" title="归档"> <i class="fa fa-ban"></i> </a>-->
                <#--</td>-->
            <#--</tr>-->
            </tbody>
        </table>
    </div>
</div>