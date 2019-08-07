<script src="${request.contextPath}/static/caseManage.js?t=20171023" type="text/javascript"></script>
<div class="portlet light">
    <div class="portlet-body">
        <form role="form" action="#">
            <div class="form-group col-md-12">
                <label class="control-label">备注内容</label>
                <textarea class="form-control" maxlength="1000" placeholder="请不要超过1000个字符" rows="3" id="description"></textarea>
            </div>
            <#--<div class="form-group col-md-12">
                <label class="control-label">相关附件</label>
                <div class="fileinput fileinput-new" data-provides="fileinput">
                    <span class="btn green btn-file">
                        <span class="fileinput-new"> 上传 </span>
                        <span class="fileinput-exists"> 重新上传 </span>
                        <input type="file" name="...">
                    </span>
                    <span class="fileinput-filename"> </span> &nbsp;
                    <a href="javascript:;" class="close fileinput-exists" data-dismiss="fileinput"> </a>
                </div>
            </div>-->
            <div class="form-group col-md-12" style="text-align: center">
                <div class="margiv-top-10">
                    <a href="javascript:$.caseManage.completeCaseStep('${progressId}', false);" class="btn green"> 完成 </a>
                </div>
            </div>
        </form>
    </div>
</div>