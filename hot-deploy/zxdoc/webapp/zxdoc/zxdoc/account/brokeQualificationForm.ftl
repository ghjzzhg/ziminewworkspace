<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="/images/lib/bootstrap-fileinput/bootstrap-fileinput.js"></script>
<div class="portlet light">
    <div class="portlet-title">
        <div class="caption font-red-sunglo">
            <i class="icon-settings font-red-sunglo"></i>
            <span class="caption-subject bold uppercase"> 实名认证</span>
            <span class="label label-sm label-success">券商</span>
        </div>
    </div>
    <div class="portlet-body">
        <div class="col-xs-3"></div>
        <div class="col-xs-6">
            <form role="form" action="#" style="width: 400px">
                <div class="form-group">
                    <label class="control-label">姓名</label>
                    <input type="text" placeholder="姓名" class="form-control"/></div>
                <div class="form-group">
                    <label class="control-label">身份证</label>
                    <input type="text" placeholder="身份证号" class="form-control"/></div>
                <div class="form-group">
                    <label class="control-label">职业资格证号</label>
                    <input type="text" placeholder="职业资格证号" class="form-control"/></div>
                <div class="form-group">
                    <label class="control-label">职业资格证扫描件</label>
                    <div>
                        <div class="fileinput fileinput-new" data-provides="fileinput">
                            <div class="fileinput-preview thumbnail" data-trigger="fileinput" style="width: 200px; height: 150px;"> </div>
                            <div>
                                <span class="btn red btn-outline btn-file">
                                    <span class="fileinput-new"> 选择文件 </span>
                                    <span class="fileinput-exists"> 重新选择 </span>
                                    <input type="file" name="...">
                                </span>
                                <a href="javascript:;" class="btn red fileinput-exists" data-dismiss="fileinput"> 删除 </a>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="margiv-top-10">
                    <a href="javascript:;" class="btn green"> 提交 </a>
                </div>
            </form>
        </div>
    </div>
</div>