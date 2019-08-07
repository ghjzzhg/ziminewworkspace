<script type="text/javascript">
    function appendNode(obj){
        var btn = $(obj), tr = btn.closest("tr"), table = btn.closest("table"), maxSeq = -1;
        table.find("input[name^=seq_]").each(function(){
            var seq = parseInt($(this).attr("name").substring(4));
            if(seq > maxSeq){
                maxSeq = seq;
            }
        });
        tr.after(Mustache.render($("#enumRow").html(), {seq: maxSeq + 1}));
        //重置排序
        table.find("input[name^=seq_]").each(function(index){
            $(this).val(index);
        });
    }
    function editEnum(obj){
        var btn = $(obj), td = btn.closest("td"), target = td.prev(), table = btn.closest("table");
        target.find("span").hide();
        target.find("input[name=description]").show();
        btn.prev().show();
        btn.hide();
    }
    function saveEnum(obj, add){
        var btn = $(obj), td = btn.closest("td"), target = td.prev(), table = btn.closest("table");
        var input = target.find("input[name=description]");
        $.ajax({
            type: 'POST',
            url: add ? "addEnum" : "updateEnum",
            async: true,
            dataType: 'json',
            data:target.find("input").serializeObject(),
            success: function (data) {
                showInfo("修改成功");
                if(!target.find("input[name=enumId]").val()){
                    var newRow = td.parent().clone();
                    newRow.appendTo(table);
                    newRow.find("input[name=description]").val("");
                }
                target.find("span").html(input.val()).show();
                input.hide();
                btn.next().show();
                if(add){
                    target.find("input[name=enumId]").val(data.enumId);
                    btn.next().next().show();
                }
                btn.hide();
            }
        });
    }

    function guid() {
        function S4() {
            return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
        }
        return (S4()+S4()+S4()+S4()+S4()+S4()+S4()+S4());
    }

    function saveRoleType(obj, add){
        var btn = $(obj), td = btn.closest("td"), target = td.prev(), table = btn.closest("table");
        var input = target.find("input[name=description]");
        var enumTypeId = target.find("input[name=enumTypeId]").val();
        target.find("input[name=enumCode]").val(guid());
        $.ajax({
            type: 'POST',
            url: add ? "addEnum" : "updateEnum",
            async: true,
            dataType: 'json',
            data:target.find("input").serializeObject(),
            success: function (data) {
                showInfo("修改成功");
                if(!target.find("input[name=enumId]").val()){
                    var newRow = td.parent().clone();
                    newRow.appendTo(table);
                    newRow.find("input[name=description]").val("");
                }
                target.find("span").html(input.val()).show();
                input.hide();
                btn.next().show();
                if(add){
                    target.find("input[name=enumId]").val(data.enumId);
                    btn.next().next().show();
                }
                btn.hide();
                $("#enumCode"+enumTypeId).val();
            }
        });
    }
    function deleteEnum(obj){
        var btn = $(obj), td = btn.closest("td"), target = td.prev();
        var input = target.find("input[name=enumId]");
        $.ajax({
            type: 'POST',
            url: "deleteEnum",
            async: true,
            dataType: 'json',
            data: {"enumId": input.val()},
            success: function (data) {
                showInfo("删除成功");
                td.parent().remove();
            }
        });
    }
    function removeNode(obj){
        var btn = $(obj), tr = btn.closest("tr"), table = btn.closest("table");
        tr.remove();
        //重置排序
        table.find("input[name^=seq_]").each(function(index){
            $(this).val(index);
        });
    }
    function moveNode(obj, dir){
        var btn = $(obj), tr = btn.closest("tr"), table = btn.closest("table");
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
        table.find("input[name^=seq_]").each(function(index){
            $(this).val(index);
        });
    }
</script>
<script id="enumRow" type="text/html">
    <tr>
        <td><input type="hidden" name="id_{{seq}}"/><input type="hidden" name="seq_{{seq}}"/></td>
        <td><input type="text" class="form-control" name="name_{{seq}}"/></td>
        <td>
            <a class="btn btn-md green btn-outline" href="#nowhere" onclick="appendNode(this);" title="添加"> <i class="fa fa-level-down"></i> </a>
            <a class="btn btn-md red btn-outline" href="#nowhere" onclick="removeNode(this);" title="删除"> <i class="fa fa-remove"></i> </a>
            <a class="btn btn-md green btn-outline" href="#nowhere" onclick="moveNode(this, 'up');" title="向上移动"> <i class="fa fa-arrow-up"></i> </a>
            <a class="btn btn-md green btn-outline" href="#nowhere" onclick="moveNode(this, 'down');" title="向下移动"> <i class="fa fa-arrow-down"></i> </a>
        </td>
    </tr>
</script>


<div class="portlet light">
    <div class="portlet-title">
        <div class="caption font-red-sunglo">
            <i class="icon-settings font-red-sunglo"></i>
            <span class="caption-subject bold uppercase"> 系统设置</span>
        </div>
    </div>
    <div class="portlet-body">