<script type="text/javascript" src="/zxdoc/static/index/js/jquery.min.js"></script>
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

    .page-section {
        padding-top: 30px;
        margin: 0;
    }

    a.btn {
        border-radius: 0;
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
        var companyList;

        var minHeight = $("#companyTable").height() + 900;
        if ($("#search").height() < minHeight) {
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

        showPartners();
    })
    function showPartners() {
        //使用dataTable进行分页 0109
        companyList = initDatatables("#companyTable", {
            buttons: [],
            "serverSide": true,
            "ajax": {
                url: "showPartnersJson",
                type: "POST",
                dataType: "json",
                data: function (d) {
                    d.groupType = $("#groupType").val();
                    d.groupName = $("#groupNames").val();
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
                    return Mustache.render($("#groupName").html(), {groupName: groupName});
                }
                },
                {
                    orderable: false,
                    "className": "dt-left",
                    "data": null, "render": function (data, type, row) {
                        var parterName = row.partnerGroupName ? row.partnerGroupName : (row.partnerCategory == "P" ?row.fullName : row.groupName);
                    return Mustache.render($("#fullName").html(), {fullName: parterName});
                }
                },
                {
                    orderable: false,
                    "className": "dt-left",
                    "data": null, "render": function (data, type, row) {
                    return Mustache.render($("#contactNum").html(), {contactNumber: row.contactNumber});
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
<script type="text/html" id="fullName">
    <span><i class="glyphicon glyphicon-lock" style="color: red" title="联系人"></i> {{fullName}}</span>
</script>
<script type="text/html" id="contactNum">
    <span><i class="glyphicon glyphicon-lock" style="color: green" title="联系方式"></i> {{contactNumber}}</span>
</script>
<div id="search" style="height: 840px">
    <section class="page-section pt-80 pb-0">
        <div class="container">
            <div class="row pb-10">
                <div class="col-xs-8 col-xs-offset-2">
                    <input type="hidden" id="partyId" value="${partyId}"/>
                    <input type="hidden" id="groupNames" value="${groupName}"/>
                    <input type="hidden" id="groupType" value="${groupType}"/>
                    <h2 class="title-section mb0 mt-0 text-center" style="color: #ffffff;">${groupName}的合伙人</h2>
                    <div class="cws_divider with-plus short-3 center mb-20 mt-10"></div>
                    <p class="text-center mb-30"></p>
                </div>
                <div>
                    <table id="companyTable" style="color: #1b1919;width:100%"
                           class="table table-striped table-bordered table-hover order-column">
                        <thead>
                        <tr>
                            <th>序号</th>
                            <th>
                                公司名称
                            </th>
                            <th>
                                合伙人名称
                            </th>
                            <th>
                                合伙人联系方式
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