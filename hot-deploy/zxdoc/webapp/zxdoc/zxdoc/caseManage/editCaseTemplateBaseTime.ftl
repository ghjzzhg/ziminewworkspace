<#include "component://common/webcommon/includes/commonMacros.ftl"/>
<link href="/images/lib/datatables/datatables.min.css?t=20161212" rel="stylesheet" type="text/css" />
<link href="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css" />
<#--<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>-->
<script src="/images/lib/datatables/datatables.min.js?t=20161212" type="text/javascript"></script>
<script src="/images/lib/datatables/plugins/bootstrap/datatables.bootstrap.js" type="text/javascript"></script>
<script src="/images/lib/mustache/mustache.js" type="text/javascript"></script>
<script src="${request.contextPath}/static/caseManage.js?t=20171023" type="text/javascript"></script>

<script type="text/javascript">
    var formData;
    $(function(){
        var oTable = initDatatables("#caseNodes", {paging: false,"info": false, "ordering": false});
        $("#templateNodesForm").validationEngine("attach", {promptPosition: "topLeft"});
        formData = $("#templateNodesForm").serialize();
    });

    function appendNode(obj){
        var btn = $(obj), tr = btn.closest("tr"), table = btn.closest("table"), maxSeq = -1;
        table.find("input[name^=seq_]").each(function(){
            var seq = parseInt($(this).attr("name").substring(4));
            if(seq > maxSeq){
                maxSeq = seq;
            }
        })
        tr.after(Mustache.render($("#nodeHtml").html(), {seq: maxSeq + 1}));
        //重置排序
        resortSeq();
        var openLayer = getLayer();
        openLayer.iframeAuto(openLayer.getFrameIndex(window.name));
    }
    function removeNodeTime(obj){
        var btn = $(obj), tr = btn.closest("tr");
        tr.remove();
        //重置排序
        resortSeq()
        var openLayer = getLayer();
        openLayer.iframeAuto(openLayer.getFrameIndex(window.name));
    }
    function moveNode(obj, dir){
        var btn = $(obj), tr = btn.closest("tr");
        if(dir == "up"){
            var upside = tr.prev();
            if(upside.length){
                upside.before(tr);
            }
        }else if(dir == "down"){
            var downside = tr.next();
            if(downside.length){
                downside.after(tr);
            }
        }
        //重置排序
        resortSeq()

    }

    function removeNodeFile(obj){
        var btn = $(obj)
    }

    function resortSeq(){
        var allTables = $("#templateNodesForm table");
        allTables.each(function(){
            var table = $(this);
            table.find("input[name^=seq_]").each(function(index){
                $(this).val(index);
                $(this).siblings("span").html(index + 1);
            });
        });
    }
</script>
<style type="text/css">
    table.table td, table.table th {
        white-space: nowrap;
    }
</style>

<script id="nodeHtml" type="text/html">
    <tr>
        <td><input type="hidden" name="id_{{seq}}"/><input type="hidden" name="seq_{{seq}}"/><span></span></td>
        <td><input type="text" maxlength="20" class="form-control" name="name_{{seq}}"/></td>
        <td>
            <a class="btn btn-md green btn-outline" href="#nowhere" onclick="appendNode(this);" title="添加"> <i class="fa fa-plus"></i> </a>
            <a class="btn btn-md red btn-outline" href="#nowhere" onclick="removeNodeTime(this);" title="删除"> <i class="fa fa-remove"></i> </a>
            <a class="btn btn-md green btn-outline" href="#nowhere" onclick="moveNode(this, 'up');" title="向上移动"> <i class="fa fa-arrow-up"></i> </a>
            <a class="btn btn-md green btn-outline" href="#nowhere" onclick="moveNode(this, 'down');" title="向下移动"> <i class="fa fa-arrow-down"></i> </a>
        </td>
    </tr>
</script>

<div class="portlet light ">
    <div class="portlet-title">
        <div class="caption font-dark">
            <i class="icon-settings font-green"></i>
            <span class="caption-subject bold uppercase"><#if template?has_content>${template.templateName!}</#if>-基准时间</span>
        </div>
        <div class="tools"></div>
    </div>
    <div class="portlet-body">
        <form id="templateNodesForm" style="overflow-y: auto;overflow-x: hidden;">
        <input type="hidden" id="templateId" name="templateId" value="${parameters.templateId}">
        <table class="table table-striped table-bordered table-hover order-column" id="caseNodes">
            <thead>
            <tr>
                <th>序号</th>
                <th>基准时间</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <#assign newSeq = -1>
            <#list nodes as node>
            <#if node.seq gt newSeq>
                <#assign newSeq = node.seq>
            </#if>
            <tr>
                <td><span>${node.seq + 1}</span><input type="hidden" name="id_${node?index}" value="${node.id}"/> <input type="hidden" name="seq_${node?index}" value="${node.seq}"/></td>
                <td>
                    <input type="text" class="form-control validate[required]" name="name_${node?index}" value="${node.name?default('')}"/>
                </td>
                <td>
                    <a class="btn btn-md green btn-outline" href="#nowhere" onclick="appendNode(this);" title="添加"> <i class="fa fa-plus"></i> </a>
                    <a class="btn btn-md red btn-outline" href="#nowhere" onclick="removeNodeTime(this);" title="删除"> <i class="fa fa-remove"></i> </a>
                    <a class="btn btn-md green btn-outline" href="#nowhere" onclick="moveNode(this, 'up');" title="向上移动"> <i class="fa fa-arrow-up"></i> </a>
                    <a class="btn btn-md green btn-outline" href="#nowhere" onclick="moveNode(this, 'down');" title="向下移动"> <i class="fa fa-arrow-down"></i> </a>
                </td>
            </tr>
            </#list>
            <#assign newSeq = newSeq + 1>
            <tr>
                <td><span>${newSeq + 1}</span><input type="hidden" name="id_${newSeq}"/><input type="hidden" name="seq_${newSeq}" value="${newSeq}"/></td>
                <td><input type="text"  maxlength="20" class="form-control" name="name_${newSeq}"/></td>
                    <td>
                        <a class="btn btn-md green btn-outline" href="#nowhere" onclick="appendNode(this);" title="添加">
                            <i class="fa fa-plus"></i> </a>
                        <a class="btn btn-md red btn-outline" href="#nowhere" onclick="removeNodeTime(this);" title="删除"> <i
                                class="fa fa-remove"></i> </a>
                        <a class="btn btn-md green btn-outline" href="#nowhere" onclick="moveNode(this, 'up');"
                           title="向上移动"> <i class="fa fa-arrow-up"></i> </a>
                        <a class="btn btn-md green btn-outline" href="#nowhere" onclick="moveNode(this, 'down');"
                           title="向下移动"> <i class="fa fa-arrow-down"></i> </a>
                    </td>
                </tr>
                </tbody>
            </table>

        </form>
        <div class="form-group" style="text-align: center">
            <div class="margiv-top-10">
                <a href="javascript:$.caseManage.caseTemplateBack(4);" class="btn green"> 上一步 </a>
                <a href="javascript:$.caseManage.saveCaseTemplateBaseTime();" class="btn green"> 下一步 </a>
            </div>
        </div>
    </div>
</div>