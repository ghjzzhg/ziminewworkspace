<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css" />
<link href="/metronic-web/layout/css/light.css" rel="stylesheet" type="text/css" />
<link href="/metronic-web/layout/layout.min.css" rel="stylesheet" type="text/css" />
<link href="/ckfinder/static/file-manage-layout.css" rel="stylesheet" type="text/css" />
<style type="text/css">
    .screenlet{
        border-bottom:0px;
    }
    body{
        background: transparent;
    }
</style>
<script src="/images/lib/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/images/lib/bootstrap-fileinput/bootstrap-fileinput.js"></script>
<div class="portlet-body">
    <ul class="nav nav-tabs">
    <#if parameters.allowLocalUpload?boolean>
        <li class="active">
            <a href="#tab_1_1" data-toggle="tab"> 文件库 </a>
        </li>

        <li>
            <a href="#tab_1_2" data-toggle="tab"> 本地上传 </a>
        </li>
    </#if>
    </ul>
    <div class="tab-content">
        <div class="tab-pane fade active in" id="tab_1_1">
            <div style="padding: 5px;float:left">
                <#--<input type="text" class="form-control" placeholder="关键字" name="query" style="display:inline-block;border:1px solid lightcyan;width:150px">
                <a href="javascript:;" title="搜索文件" class="btn submit">
                    <i class="icon-magnifier"></i>
                </a>-->
            </div>
