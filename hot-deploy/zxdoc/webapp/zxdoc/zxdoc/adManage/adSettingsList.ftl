<#--<script src="/images/lib/datatables/datatable.js" type="text/javascript"></script>-->
<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css" />
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css" />
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.min.js" type="text/javascript"></script>
<script src="${request.contextPath}/static/advertment.js?t=20170602" type="text/javascript"></script>

<script type="text/javascript">
    var oTable;
    $(function(){
        /*var table = $('#sample_1');*/
        oTable = initDatatables("#sample_1",{

            // Internationalisation. For more info refer to http://datatables.net/manual/i18n
           /* "language": {
                "aria": {
                    "sortAscending": ": activate to sort column ascending",
                    "sortDescending": ": activate to sort column descending"
                },
                "emptyTable": "无记录",
                "info": "当前 _START_ - _END_ 总共 _TOTAL_ 条",
                "infoEmpty": "无记录",
                "infoFiltered": "(filtered1 from _MAX_ total entries)",
                "zeroRecords": "无记录"
            },*/

            // Or you can use remote translation file
            //"language": {
            //   url: '//cdn.datatables.net/plug-ins/3cfcc339e89/i18n/Portuguese.json'
            //},

            // setup buttons extension: http://datatables.net/extensions/buttons/
            buttons: [
                {
                    text: '新建',
                    action: function ( e, dt, node, config ) {
                        displayInLayer('添加广告', {requestUrl: 'AdSettingsHeader', width:800, height:'80%'});
                    }, className: 'btn green btn-outline'
                }
            ],
            "serverSide": true,
            "ajax": {
                url: "FindAdvert",
                type: "POST"
            },
            "order": [
                [1, 'asc'],
            ],
            "columns": [
                {"data": null,"targets": 0, orderable: false},
                {"data":"score", "render": function(data, type, row, meta){
                    if(row.lockAdvert != '1') {
                        var score = data;
                        var begin = row.maxTimes;
                        var now = new Date;
                        var year = now.getFullYear();
                        var month = (now.getMonth() + 1).toString();
                        var day = (now.getDate()).toString();
                        if (month.length == 1) {
                            month = "0" + month;
                        }
                        if (day.length == 1) {
                            day = "0" + day;
                        }
                        var dateTime = year + "-" + month + "-" + day;
                        if (dateTime >= begin && score >= dateTime) {
                            return "<span style='color:green'>可用</span>";
                        } else {
                            return "<span style='color:red'>不可用</span>";
                        }
                    }else{
                        return "<span style='color:red'>不可用</span>";
                    }
                }},
                {"data":"ruleName"},
                {"data":"maxTimes"},
                {"data":"score"},
                {"data":"ranges", "render": function(data, type, row, meta){
                    var ranges = data;
                    ranges = ranges.substr(0,ranges.length-1);
                    return ranges;
                    }
                },
                {
                    orderable: false, "data": null, "render": function (data, type, row) {
                        var html;
                        if(row.lockAdvert == '1'){
                            html = Mustache.render($("#operatorCol").html(), {AdvertId: row.AdvertId}) + Mustache.render($("#unLockAdvert").html(), {AdvertId: row.AdvertId});
                        }else{
                            html = Mustache.render($("#operatorCol").html(), {AdvertId: row.AdvertId}) + Mustache.render($("#lockAdvert").html(), {AdvertId: row.AdvertId});
                        }
                        return html;
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
            // scroller extension: http://datatables.net/extensions/scroller/
//            scrollY:        300,
            /*deferRender:    true,
            scroller:       false,
            paging: true,
            stateSave:      true,
            "searching": false,
            "lengthChange": false,*/

            // set the initial value
            /*"pageLength": 10,

            "dom": "<'row' <'col-md-12'B>><'row'<'col-md-6 col-sm-12'l><'col-md-6 col-sm-12'f>r><'table-scrollable't><'row'<'col-md-5 col-sm-12'i><'col-md-7 col-sm-12'p>>", // horizobtal scrollable datatable
*/
            // Uncomment below line("dom" parameter) to fix the dropdown overflow issue in the datatable cells. The default datatable layout
            // setup uses scrollable div(table-scrollable) with overflow:auto to enable vertical scroll(see: assets/global/plugins/datatables/plugins/bootstrap/dataTables.bootstrap.js).
            // So when dropdowns used the scrollable div should be removed.
            //"dom": "<'row' <'col-md-12'T>><'row'<'col-md-6 col-sm-12'l><'col-md-6 col-sm-12'f>r>t<'row'<'col-md-5 col-sm-12'i><'col-md-7 col-sm-12'p>>",
        });
    })
</script>
<script id="operatorCol" type="text/html">
    <a class="btn btn-md green btn-outline" href="javascript:$.Advertment.editAdvertment('{{AdvertId}}');" title="修改"> <i class="fa fa-pencil"></i> </a>
    <a class="btn btn-md red btn-outline" href="javascript:$.Advertment.deleteAdvertment('{{AdvertId}}');" title="删除"> <i class="fa fa-remove"></i> </a>
</script>
<script id="lockAdvert" type="text/html">
    <a class="btn btn-md red btn-outline" href="javascript:$.Advertment.updateAdvertmentSatus('{{AdvertId}}','1');" title="禁用"> <i class="fa fa-lock"></i> </a>
</script>
<script id="unLockAdvert" type="text/html">
    <a class="btn btn-md green btn-outline" href="javascript:$.Advertment.updateAdvertmentSatus('{{AdvertId}}','0');" title="启用"> <i class="fa fa-unlock"></i> </a>
</script>
<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">页头通栏广告</span>
        </div>
        <div class="tools"></div>
    </div>
    <div class="portlet-body">
        <table class="table table-striped table-bordered table-hover order-column" id="sample_1">
            <thead>
            <tr>
                <th>序号</th>
                <#--<th>显示顺序</th>-->
                <th>可用</th>
                <th>标题</th>
               <#-- <th>样式</th>-->
                <th>起始时间</th>
                <th>终止时间</th>
                <th>投放范围</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
            <#--<tbody>
            <tr>
                <td>1</td>
                <td><input class="form-control" type="text" value="0" size="2"/> </td>
                <td><input type="checkbox" checked> </td>
                <td>广告标题</td>
                <td>文字</td>
                <td>2016-07-21 10:10:10</td>
                <td>2016-07-29 10:10:10</td>
                <td>首页, 工作台</td>
                <td>
                    <a class="btn btn-md green btn-outline" href="javascript:;" title="修改"> <i
                            class="fa fa-pencil"></i> </a>
                </td>
            </tr>
            <tr>
                <td>2</td>
                <td><input class="form-control" type="text" value="1" size="2"/> </td>
                <td><input type="checkbox" checked> </td>
                <td>广告标题2</td>
                <td>图片</td>
                <td>2016-07-21 10:10:10</td>
                <td>2016-07-29 10:10:10</td>
                <td>首页, 工作台</td>
                <td>
                    <a class="btn btn-md green btn-outline" href="javascript:;" title="修改"> <i
                            class="fa fa-pencil"></i> </a>
                </td>
            </tr>
            </tbody>-->
        </table>
    </div>
</div>