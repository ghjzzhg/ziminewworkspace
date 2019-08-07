<link href="/images/lib/bootstrap-fileinput/bootstrap-fileinput.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/bootstrap-tagsinput/bootstrap-tagsinput.css" rel="stylesheet" type="text/css"/>
<link href="/images/lib/three-level-picker/css/three-level-picker.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/images/lib/bootstrap-fileinput/bootstrap-fileinput.js"></script>
<script type="text/javascript" src="/images/lib/three-level-picker/js/three-level-picker.js"></script>
<script type="text/javascript" src="/images/lib/bootstrap-tagsinput/bootstrap-tagsinput.min.js"></script>
<script type="text/javascript" src="/zxdoc/static/docTypes.js"></script>
<script type="text/javascript">
    function gotoNextStep() {
        var fileUpload = $("#uploadedFile").val();
        if(fileUpload != ""){
            var filesub = fileUpload.substring(fileUpload.lastIndexOf('\\') + 1, fileUpload.length);
            if(filesub.length > 50){
                showError("文件名称长度请小于50个字符");
                return;
            }
            var activeStep = $(".mt-step-col.active");
            var nextStep = activeStep.next(".mt-step-col");
            if (nextStep.length) {
                nextStep.addClass("active");
                activeStep.removeClass("active");
                $("#" + activeStep.data("stepId")).hide();
                $("#" + nextStep.data("stepId")).show();
            }
        }else{
            showInfo("选择文件！")
        }
    }
    function gotoPreviousStep() {
        var activeStep = $(".mt-step-col.active");
        var prevStep = activeStep.prev(".mt-step-col");
        if (prevStep.length) {
            prevStep.addClass("active");
            activeStep.removeClass("active");
            $("#" + activeStep.data("stepId")).hide();
            $("#" + prevStep.data("stepId")).show();
        }
    }
    $(function () {
        $("#newLibrary").validationEngine("attach", {promptPosition: "topLeft"});
        $("#type").threeLevelPicker({source: 'DocTypes'});
        $('#tags').tagsinput();
    });
    //    补充信息
    //        function addLibrary() {
    //            var data = {
    //                messageTitle: $("#messageTitle").val(),
    //                types: $("#types").val(),
    //                tags: $("#tags").val(),
    //                introduce: $("#introduce").val(),
    //                messageType: $("#messageType").val(),
    //            }
    //            $.ajax({
    //                type: 'post',
    //                dataType: 'json',
    //                url: 'Library',
    //                data: data,
    //                success:function(content){
    //                    showInfo(content.msg);
    //                    gotoNextStep();
    //                }
    //            })
    //        }
    function addLibrary() {
        var options = {
            beforeSubmit: function () {
                return $("#newLibrary").validationEngine("validate");
            },
            dataType: "html",
            success: function (data) {
                var msg = $(data).find("#msg").html();
                if(msg == "success") {
                    showInfo("上传成功");
                    gotoNextStep();
                }else {
                    if(msg=="file") {
                        showError("请选择文件");
                    }else if(msg=="fileSize"){
                        showError("请控制文件大小在${parameters.fileSize?default('50')}M之内");
                    }
                }
            },
            url: "addLibrary",
            type: 'post'
        };
        $("#newLibrary").ajaxSubmit(options);
    }

    function showScoreInput(v){
        var value = $(v).val();
        if(value == "1"){
            $("#accessdiv").show();
        }else{
            $("#accessdiv").hide();
        }
    }
</script>
<div class="portlet light">
    <div class="portlet-title">
        <div class="caption font-red-sunglo">
            <i class="icon-settings font-red-sunglo"></i>
            <span class="caption-subject bold uppercase"> 发布资料</span>
        </div>
    </div>
    <div class="portlet-body">
        <div class="mt-element-step">
            <div class="row step-thin">
                <div class="mt-step-desc">
                    <div class="caption-desc font-grey-cascade">请按向导填写每一步骤所需信息
                    </div>
                    <br/></div>
                <div class="col-md-4 bg-grey mt-step-col active" data-step-id="step1">
                    <div class="mt-step-number bg-white font-grey">1</div>
                    <div class="mt-step-title uppercase font-grey-cascade">上传文档</div>
                    <div class="mt-step-content font-grey-cascade">选择需要发布的文档</div>
                </div>
                <div class="col-md-4 bg-grey mt-step-col" data-step-id="step2">
                    <div class="mt-step-number bg-white font-grey">2</div>
                    <div class="mt-step-title uppercase font-grey-cascade">补充信息</div>
                    <div class="mt-step-content font-grey-cascade">明确标题、分类等信息</div>
                </div>
                <div class="col-md-4 bg-grey mt-step-col" data-step-id="step3">
                    <div class="mt-step-number bg-white font-grey">3</div>
                    <div class="mt-step-title uppercase font-grey-cascade">发布结果</div>
                    <div class="mt-step-content font-grey-cascade">发布结果信息</div>
                </div>
            </div>
        </div>
        <form id="newLibrary">
            <div>
                <div class="portlet box blue step-form" id="step1" name="step1">
                    <div class="portlet-body form">
                        <div style="text-align: center;padding: 10px">
                            <div class="fileinput fileinput-new" data-provides="fileinput">
                                <div class="fileinput-preview thumbnail" data-trigger="fileinput"
                                     style="width: 200px; height: 150px;"></div>
                                <div>
                                <span class="btn red btn-outline btn-file">
                                    <span class="fileinput-new"> 选择文件 </span>
                                    <span class="fileinput-exists"> 重新选择 </span>
                                    <input type="file" name="uploadedFile" id="uploadedFile">
                                </span>
                                    <a href="javascript:;" class="btn red fileinput-exists" data-dismiss="fileinput">
                                        删除 </a>
                                </div>
                            </div>
                        </div>
                        <div class="form-actions" style="padding: 20px;text-align: center">
                            <button type="button" class="btn blue" onclick="gotoNextStep()">
                                <i class="fa fa-check"></i> 下一步
                            </button>
                        </div>
                    </div>
                </div>
                <div class="portlet box blue step-form" id="step2" style="display: none;">
                    <div class="portlet-body">

                        <table class="table table-hover table-striped table-bordered">
                            <tbody>
                            <tr>
                                <td style="width: 200px"> 标题<span class="required" style="color: red"> * </span></td>
                                <td>
                                    <input type="text" placeholder="标题" class="form-control validate[required,custom[noSpecial]]"
                                           name="title" maxlength="20"/>
                                </td>
                            </tr>
                            <tr>
                                <td> 分类<span class="required" style="color: red"> * </span></td>
                                <td>
                                    <select class="form-control  validate[required]"  style="width: 200px" name="type">
                                        <option value="">-请选择-</option>
                                    <#if parameters.data?has_content>
                                        <#list parameters.data as list>
                                            <option value="${list.enumId?default('')}">${list.description?default('')}</option>
                                        </#list>
                                    </#if>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td> 标签<span class="required" style="color: red"> * </span></td>
                                <td>
                                    <input type="text" id="tags" name="tags" class="form-control  validate[required,custom[noSpecial]]"/>
                                    <span id="s2id_tags"></span>
                                </td>
                            </tr>
                            <tr>
                                <td> 简介</td>
                                <td>
                                    <textarea class="form-control validate[custom[noSpecial]]" rows="3" name="introduce" maxlength="1000" placeholder="简介信息不超过1000个字符"></textarea>
                                </td>
                            </tr>
                            <tr>
                                <td> 类型</td>
                                <td>
                                    <div><label><input type="radio" name="accessType" onclick="showScoreInput(this)" value="2" checked>普通文档</label>
                                    </div>
                                    <div><label><input type="radio" id="accessTypeScore" name="accessType" onclick="showScoreInput(this)" value="1">付费文档 <span id="accessdiv" style="display: none"><input type="text" name="score" id="accessTypeScoreInput" style="width: 50px" class=" validate[condRequired[accessTypeScore],custom[onlyNumberSp]]" maxlength="8" >分</span></label>
                                    </div>
                                </td>
                            </tr>
                            </tbody>
                        </table>

                        <div class="col-md-12" style="text-align: center;padding:20px">
                            <button type="button" class="btn grey" onclick="gotoPreviousStep();">
                                <i class="fa fa-check"></i> 上一步
                            </button>
                            <button type="button" class="btn blue" onclick="addLibrary()">
                                <i class="fa fa-check"></i> 发布
                            </button>
                        </div>
                    </div>
                </div>
                <div class="portlet box blue step-form" id="step3" style="display: none;">
                    <div class="portlet-body">
                        <div style="text-align: center;padding: 20px">
                            <i class="bg-green circle font-yellow fa fa-check" style="padding: 10px"></i>
                            文档上传成功
                        </div>
                    </div>
                </div>
            </div>
        </form>
        <div class="note note-info">
            <pre>
                温馨提示
            <#assign fileSize = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("content.properties", "fileSize")>
                1.您可以上传日常积累和撰写的文档资料，如模板、总结等。<span style="color: red">上传文件大小请不要超过${fileSize?default("50")}兆</span>支持多种文档类型：<i class="fa fa-file-word-o"></i>&nbsp;<i
                    class="fa fa-file-excel-o"></i>&nbsp;<i class="fa fa-file-powerpoint-o"></i>&nbsp;<i
                    class="fa fa-file-pdf-o"></i>
                2.<span style="color: red">文件名称不要超过50个字符。</span>
                3.上传涉及侵权内容的文档将会被移除。
                4.上传有问题需要帮助？请点击客服进行咨询。
                5.为营造绿色网络环境，严禁上传含有淫秽色情及低俗信息等文档，让我们一起携手共同打造健康的资料库。
            </pre>
        </div>
    </div>
</div>