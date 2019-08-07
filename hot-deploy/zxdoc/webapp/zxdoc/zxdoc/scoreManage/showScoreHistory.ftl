<#--<script src="/images/lib/datatables/datatable.js" type="text/javascript"></script>-->
<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css" />
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css" />
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<script src="/images/lib/jquery-easypiechart/jquery.easypiechart.min.js" type="text/javascript"></script>

<script type="text/javascript">
    var oTable;
    $(function(){
        oTable = initDatatables("#sample_1", {
            "serverSide": true,
            "ajax":{
                url: "showUserScoresHistoryJson",
                type: "POST",
                data: function (d) {
                    d.partyId = $("#partyId").val();
                    d.type = $("#type").val();
                }
            },
            "order": [
                [3, 'desc']
            ],
            "columns": [
                {"data": null,"targets": 0, orderable: false},
                {"data": "scoreChange"},
                {"data": "description"},
                {"data": "createdStamp"}
            ],
            "fnDrawCallback": function(){
                var api = this.api();
                var startIndex= api.context[0]._iDisplayStart;//获取到本页开始的条数
                api.column(0).nodes().each(function(cell, i) {
                    //此处 startIndex + i + 1;会出现翻页序号不连续，主要是因为startIndex 的原因,去掉即可。
                    cell.innerHTML = startIndex + i + 1;
                });
            },
            initComplete: function(){
                var openLayer = getLayer();
                openLayer.iframeAuto(openLayer.getFrameIndex(window.name));
            }
        });
        showRule();
    })

    function searchUserScoreHistory(type) {
        $("#type").val(type);
        oTable.ajax.reload();
    }

    function showRule() {
        $("#row").empty();
        $.ajax({
            type:"post",
            url:"showRule",
            dataType:"json",
            data:{partyId:$("#partyId").val()},
            success:function (data) {
                var html = "";
                if(data.rules!=null)
                {
                    var rules = data.rules;
                    for (var i=0;i<rules.length;i++)
                    {
                        html += "<div class='col-xs-3'>";
                        html += "<div class='easy-pie-chart'>";
                        html += "<div class='number transactions' data-percent='"+ rules[i].percentage +"'>";
                        html += "<span>"+ rules[i].percentage +"</span>% </div>";
                        html += "<a class='title' href='javascript:searchUserScoreHistory(&quot;" + rules[i].enumCode + "&quot;);'> " + rules[i].description;
                        html += "<i class='icon-arrow-right'></i></a></div></div>";
                        html += "<div class='margin-bottom-10 visible-sm'> </div>";
                    }
                    $("#row").append(html);
                    $('.easy-pie-chart .number').easyPieChart({
                        animate: 1000,
                        size: 75,
                        lineWidth: 3
                    });
                    var openLayer = getLayer();
                    openLayer.iframeAuto(openLayer.getFrameIndex(window.name));
                }
            }
        })
    }
</script>
<input type="hidden" value="" id="type" name="type">
<input type="hidden" value="${userLoginId}" id="partyId">
<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">积分分布</span>
        </div>
        <div class="tools"></div>
    </div>
    <div class="portlet-body">
        <div class="row" id="row">
            <div class="col-xs-3">
                <div class="easy-pie-chart">
                    <div class="number transactions" data-percent="55">
                        <span>+55</span>% </div>
                    <a class="title" href="javascript:searchUserScoreHistory('SCORE_RULE_1');"> 登录系统
                        <i class="icon-arrow-right"></i>
                    </a>
                </div>
            </div>
            <div class="margin-bottom-10 visible-sm"> </div>
            <div class="col-xs-3">
                <div class="easy-pie-chart">
                    <div class="number visits" data-percent="85">
                        <span>+85</span>% </div>
                    <a class="title" href="javascript:searchUserScoreHistory('SCORE_RULE_2');"> 回复咨询
                        <i class="icon-arrow-right"></i>
                    </a>
                </div>
            </div>
            <div class="margin-bottom-10 visible-sm"> </div>
            <div class="col-xs-3">
                <div class="easy-pie-chart">
                    <div class="number bounce" data-percent="46">
                        <span>-46</span>% </div>
                    <a class="title" href="javascript:searchUserScoreHistory('SCORE_RULE_3');"> 上传文档
                        <i class="icon-arrow-right"></i>
                    </a>
                </div>
            </div>
            <div class="margin-bottom-10 visible-sm"> </div>
            <div class="col-xs-3">
                <div class="easy-pie-chart">
                    <div class="number bounce" data-percent="10">
                        <span>-10</span>% </div>
                    <a class="title" href="javascript:searchUserScoreHistory('SCORE_RULE_REWARD');"> 注册
                        <i class="icon-arrow-right"></i>
                    </a>
                </div>
            </div>
        </div>
    </div>
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase">积分日志</span>
        </div>
        <div class="tools"></div>
    </div>
    <div class="portlet-body">
        <table class="table table-striped table-bordered table-hover order-column" id="sample_1">
            <thead>
            <tr>
                <th>序号</th>
                <th>积分变更</th>
                <th>规则</th>
                <th>变更时间</th>
            </tr>
            </thead>
            <tbody><#--
            <#if parameters.data?has_content>
                <#list parameters.data as list>
                <tr>
                    <td>${list_index+1}</td>
                    <td>${list.scoreChange?default('')}</td>
                    <td>${list.description?default('')}</td>
                    <td>${list.createdStamp?default('')}</td>
                </tr>
                </#list>
            </#if>-->
            </tbody>
        </table>
    </div>
</div>