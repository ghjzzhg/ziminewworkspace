<link href="/images/lib/dist-picker/distpicker.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/zxdoc/static/index/js/jquery.min.js"></script>
<script type="text/javascript" src="/images/lib/dist-picker/distpicker.min.js"></script>
<style type="text/css">
    table.altrowstable {
        font-family: verdana, arial, sans-serif;
        font-size: 11px;
        color: #333333;
        border-width: 1px;
        border-color: #a9c6c9;
        border-collapse: collapse;
    }

    table.altrowstable th {
        border-width: 1px;
        padding: 8px;
        border-style: solid;
        border-color: #a9c6c9;
    }

    table.altrowstable td {
        border-width: 1px;
        padding: 8px;
        border-style: solid;
        border-color: #a9c6c9;
    }
    .page-section{
        padding-top: 30px;
        margin:0;
    }
    a.btn{
        border-radius: 0;
    }

    #distpicker {
        float:right;
    }
</style>
<script type="application/javascript">
    function openGroupInfo(partyId) {
        $.ajax({
            type: 'GET',
            url: "groupInfo",
            async: true,
            dataType: 'html',
            data: {partyId: partyId},
            success: function (content) {
                $("#groupInfo").html($(content));
            }
        });
    }
    function groupList(partyId) {
        $.ajax({
            type: 'GET',
            url: "groupList",
            async: true,
            dataType: 'html',
            data: {groupType: '${parameters.groupType}'},
            success: function (content) {
                $("#groupInfo").html($(content));
            }
        });
    }
    $(function () {
//        $("#search").height($(window).height() - 160).css("background", "transparent");
//        $("#groupInfo").height($("#search").height() - 200);
        var companyList;
        $("#distpicker").distpicker({valueType: "code"});
        searchCompanyByAddress();

        var minHeight = $("#companyTable").height() + 900;
        if($("#search").height() < minHeight){
            $("#search").height(minHeight);
        }
        $("#search").css("background", "transparent").backstretch([
                    "/metronic-web/images/login-bg/1.jpg",
                    "/metronic-web/images/login-bg/2.jpg",
                    "/metronic-web/images/login-bg/3.jpg",
                    "/metronic-web/images/login-bg/4.jpg"
                ], {
                    fade: 1000,
                    duration: 8000
                }
        );
    })

    //清空搜索条件
    function emptySearchCondition() {
        $("#area").val(null).trigger('change');
        $("[name=area]").val("");
        companyList.ajax.reload();
    }

    function searchCompanyByAddress() {
        //使用dataTable进行分页 0109
        companyList = initDatatables("#companyTable", {
            buttons: [],
            "serverSide": true,
            "ajax": {
                url: "searchCompanyByAddressJson",
                type: "POST",
                dataType:"json",
                data: function (d) {
                    var searchArea = "";
                    $("#distpicker option:selected").each(function(){
                        var optionVal = $(this).val();
                        if(optionVal && (!searchArea || parseInt(optionVal) > parseInt(searchArea))){
                            searchArea = optionVal;
                        }
                    })
                    d.areaCode = searchArea;
                    d.groupType = $("#groupType").val();
                }
            },
            ordering: false,
            "columns": [
                {"data": null,"targets": 0, orderable: false},
                {
                    orderable: false,
                    "className": "dt-left",
                    "data": null, "render": function (data, type, row) {
                    var groupName = row.groupName;
                    var hasPartner = row.hasPartner;
                    if(hasPartner=="N") {
                        return Mustache.render($("#groupName").html(), {groupName: groupName});
                    }else {
                        return Mustache.render($("#groupNames").html(), {groupName: groupName,partyId:row.partyId});
                    }
                }
                },
                {
                    orderable: false,
                    "className": "dt-left",
                    "data": null, "render": function (data, type, row) {
                    var isPartner = row.isPartner;
                    if(isPartner=="N") {
                        return Mustache.render($("#fullName").html(), {fullName: row.fullName});
                    }else{
                        return "";
                    }
                }
                },
                {
                    orderable: false,
                    "className": "dt-left",
                    "data": null, "render": function (data, type, row) {
                    var isPartner = row.isPartner;
                    if(isPartner=="N") {
                        return Mustache.render($("#contactNum").html(), {contactNumber:row.contactNumber});
                    }else{
                        return "";
                    }
                }
                },
                {
                    orderable: false,
                    "className": "dt-left",
                    "data": null, "render": function (data, type, row) {
                    var isPartner = row.isPartner;
                    if(isPartner=="N") {
                        return Mustache.render($("#more").html(), {partyId:row.partyId});
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
        })
    }

</script>
<script type="text/html" id="groupName">
    <span><i class="glyphicon glyphicon-lock" style="color: #00a0e9" title="公司"></i> {{groupName}}</span>
</script>
<script type="text/html" id="groupNames">
    <i class="glyphicon glyphicon-lock" style="color: #00a0e9" title="公司"></i> {{groupName}}(<a href="${request.contextPath}/control/showPartners?partyId={{partyId}}&groupName={{groupName}}&groupType=${groupType}" style="color: #00a0e9">合伙人</a>)
</script>
<script type="text/html" id="fullName">
    <span><i class="glyphicon glyphicon-lock" style="color: red" title="联系人"></i> {{fullName}}</span>
</script>
<script type="text/html" id="contactNum">
    <span><i class="glyphicon glyphicon-lock" style="color: green" title="联系方式"></i> {{contactNumber}}</span>
</script>
<script type="text/html" id="more">
    <a href="${request.contextPath}/control/groupInfo?partyId={{partyId}}"
       class="btn btn-outline btn-circle btn-sm purple" title="更多">
        <i class="glyphicon glyphicon-list-alt"></i> 更多 </a>
</script>
<div id="search" style="height: 840px">
    <section class="page-section pt-80 pb-0">
        <div class="container">
            <div class="row pb-10">
                <div class="col-xs-8 col-xs-offset-2">
                    <input type="hidden" id="groupType" value="${groupType}"/>
                    <h2 class="title-section mb0 mt-0 text-center" style="color: #ffffff;">${role.description!}</h2>
                    <div class="cws_divider with-plus short-3 center mb-20 mt-10"></div>
                    <p class="text-center mb-30"></p>
                    <div class="col-xs-8 col-xs-offset-2 mb-20">
                        <div class="input-group">

                            <div id="distpicker" class="distpicker spanArea">
                                <select id="province" class="form-control"></select>
                                <select id="city" class="form-control"></select>
                                <select id="district" class="form-control"></select>
                            </div>
                            <input type="text" id="employee" name="employee" style="display: none" class="form-control">
                            <span class="input-group-btn">
                                <button class="btn green" type="button" onclick="companyList.ajax.reload()">查询</button>
                            </span>
                        </div>
                    </div>
                </div>
                <div>
                    <table id="companyTable" style="color: #1b1919;width:100%" class="table table-striped table-bordered table-hover order-column">
                        <thead>
                        <tr>
                            <th>序号</th>
                            <th>
                                公司名称
                            </th>
                            <th>
                                联系人
                            </th>
                            <th>
                                联系方式
                            </th>
                            <th>
                                查看详情
                            </th>
                        </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                </div>
                <div class="col-xs-12" style="margin-top:10px;">
                <#if groupType == "CASE_ROLE_STOCK">
    <#--券商-->
        <#include "component://zxdoc/webapp/zxdoc/zxdoc/index/group/security/securityOffice.html"/>
    <#elseif groupType == "CASE_ROLE_LAW">
                <#--律师-->
                    <#include "component://zxdoc/webapp/zxdoc/zxdoc/index/group/lawyer/lawyerOffice.html"/>
                </#if>
                </div>
            </div>
        </div>
    </section>
</div>