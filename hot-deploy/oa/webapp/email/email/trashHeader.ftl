
<div>
    <div class="col-xs-6" style="margin: 5px 0;">
        <#--<a href="#" class="smallSubmit" onclick="$.email.deleteEmail('Trash')">删除</a>-->
        <a href="#" class="smallSubmit" onclick="$.email.removeEmail('Trash')">彻底删除</a>
        <select>
            <option value="0" label="标记为..."></option>
            <option value="1" label="已读"></option>
            <option value="2" label="未读"></option>
        </select>
        <select>
            <option value="0" label="移动到..."></option>
            <option value="1" label="草稿箱"></option>
            <option value="2" label="发件箱"></option>
            <option value="3" label="垃圾箱"></option>
            <option value="4" label="自定义"></option>
        </select>
    </div>
    <div class="col-xs-6">
        <div style="float:right">
            <input type="text">
            <a href="#" class="smallSubmit">搜索</a>
        </div>
    </div>
</div>