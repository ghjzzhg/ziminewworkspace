<#if form?has_content>
    <#assign formId = form.formId?default("")>
    <#assign formType = form.formType?default("")>
    <#assign formName = form.formName?default("")>
    <#assign formKey = form.formKey?default("")>
    <#assign servicePrefix = form.servicePrefix?default("")>
    <#assign formUrl = form.formUrl?default("")>
    <#assign category = form.category?default("")>
    <#assign description = form.description?default("")>
    <#assign content = form.content?default("")>
</#if>

<script type="text/javascript">
    $(function () {
        formId = "pf_formId0"
        setPagecreateAct();
    })
     function saveActForm(id) {
         $("#content").val(leipiEditor.getContent().trim());
//        if(window["actFormContent"]){
//            window["actFormContent"].sync();
//        }
        var options = {
            beforeSubmit: function () {
                return $("#ActForm").validationEngine("validate");
            }, // pre-submit callback
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                displayJsonResponse(data);
                ajaxUpdateAreas('ActFormList,ActFormListOnly,sortField=sn')
            },

            url: id ? "updateActForm" : "createActForm", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
        };
        $("#ActForm").ajaxSubmit(options);
    }

    function setPagecreateAct(){
        if($("#formType").val() == "USER_SCREEN"){
            $("#actExtPath").css('display','none');
            $("#actFormContent").css('display','');
        }else{
            $("#actExtPath").css('display','');
            $("#actFormContent").css('display','none');
        }
    }
</script>

<form method="post" action="/workflow/control/updateActForm" id="ActForm" class="basic-form"
      onsubmit="javascript:submitFormDisableSubmits(this)" name="ActForm">
    <input type="hidden" name="formId" value="${formId?default("")}" id="ActForm_formId">
    <input type="hidden" name="saveLink" id="ActForm_saveLink" onclick="javascript:$.workflow.saveActForm('${formId?default("")}')">
    <table cellspacing="0" class="basic-table hover-bar">
        <tbody>
        <tr>
            <td class="label">
                <label>类型</label></td>
            <td>
                <span class="ui-widget">
                    <select name="formType" class="required" id="formType" onchange="setPagecreateAct()" size="1">
                        <#if formTypeList?has_content>
                            <#list formTypeList as list>
                                <#if list.enumId?has_content && list.enumId == formType?default("")>
                                <option value="${list.enumId?default("")}" selected>${list.description?default("")}</option>
                                <#else>
                                    <option value="${list.enumId?default("")}">${list.description?default("")}</option>
                                </#if>
                            </#list>
                        </#if>
                    </select>
              </span>
            </td>
        </tr>
        <tr class="alternate-row">
            <td class="label">
                <label>名称</label></td>
            <td>
                <input type="text" name="formName" value="${formName?default("")}" size="20" id="ActForm_formName"
                       onblur="getPinYin(this.value, ['ActForm_formKey','ActForm_servicePrefix'])">
                <script language="JavaScript" type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>


            </td>
        </tr>
        <tr>
            <td class="label">
                <label>key</label></td>
            <td>
                <input type="text" name="formKey" class="required" value="${formKey?default("")}" size="20">
            </td>
        </tr>
        <tr class="alternate-row">
            <td class="label">
                <label for="ActForm_servicePrefix" id="ActForm_servicePrefix_title">服务前缀</label></td>
            <td>
                <input type="text" name="servicePrefix" value="${servicePrefix?default("")}" size="20" id="ActForm_servicePrefix">
                <span class="tooltip">流程流转时用到业务对应的服务时约定按此前缀获取对应的服务名称, 其中保存服务必须返回业务主键bizKey</span>
            </td>
        </tr>
        <tr id="actExtPath">
            <td class="label">
                <label for="ActForm_formUrl" id="ActForm_formUrl_title">路径</label></td>
            <td>
                <input type="text" name="formUrl" value="${formUrl?default("")}" style="width:100%" id="ActForm_formUrl">
            </td>
        </tr>
        <tr class="alternate-row">
            <td class="label">
                <label for="ActForm_category" id="ActForm_category_title">分类</label></td>
            <td>
        <span class="ui-widget">
            <select name="category" id="ActForm_category" size="1">
                <option value="">--请选择--</option>
                <#if formTypeList?has_content>
                    <#list categoryList as list>
                        <#if list.enumId?has_content && list.enumId == category?default("")>
                            <option value="${list.enumId?default("")}" selected>${list.description?default("")}</option>
                        <#else>
                            <option value="${list.enumId?default("")}">${list.description?default("")}</option>
                        </#if>

                    </#list>
                </#if>
            </select>
          </span>


            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="ActForm_description" id="ActForm_description_title">描述</label></td>
            <td>
                <textarea name="description" cols="60" rows="3" id="ActForm_description">${description?default('')}</textarea>
            </td>
        </tr>
        <tr id="actFormContent" class="alternate-row">
            <td class="label">
                <label>表单设计</label></td>
            <td>
                <input type="hidden" id="content" name="content">
                        <script id="myFormDesign" type="text/plain"></script>
                    <script type="text/javascript">
                        var leipiEditor;
                        $(function () {
                            leipiEditor = UE.getEditor('myFormDesign',{
                                allowDivTransToP: false,//阻止转换div 为p
                                toolleipi:true,//是否显示，设计器的 toolbars
                                textarea: 'design_content',
                                //这里可以选择自己需要的工具按钮名称,此处仅选择如下五个
                                toolbars:[[
                                    'fullscreen', 'source', '|', 'undo', 'redo', '|','bold', 'italic', 'underline', 'fontborder', 'strikethrough',  'removeformat', '|', 'forecolor', 'backcolor', 'insertorderedlist', 'insertunorderedlist','|', 'fontfamily', 'fontsize', '|', 'indent', '|', 'justifyleft', 'justifycenter', 'justifyright', 'justifyjustify', '|',  'link', 'unlink',  '|',  'horizontal',  'spechars',  'wordimage', '|', 'inserttable', 'deletetable',  'mergecells',  'splittocells']],
                                //focus时自动清空初始化时的内容
                                //autoClearinitialContent:true,
                                //关闭字数统计
                                wordCount:false,
                                //关闭elementPath
                                elementPathEnabled:false,
                                //默认的编辑区域高度
                                initialFrameHeight:300
                                //,iframeCssUrl:"css/bootstrap/css/bootstrap.css" //引入自身 css使编辑器兼容你网站css
                                //更多其他参数，请参考ueditor.config.js中的配置项
                            })
                            $("div[id^='edui']").each(function () {
                                UE.dom.domUtils.setStyle(this, 'z-index','0');
                                UE.dom.domUtils.removeStyle(this, 'width');
                            })
                            leipiEditor.ready(function() {
                                //this是当前创建的编辑器实例
                                var content = unescapeHtmlText('${content?default("")}');
                                this.setContent(content);
                            });
                        })
                    </script>

            </td>
        </tr>
        <tr>
            <td class="label">
                <label for="ActForm_saveLink" class="hide" id="ActForm_saveLink_title">保存</label></td>
            <td colspan="4">
                <input type="hidden" name="hiddenList" id="hiddenList" value="${parameters.chooserInfo?default("")}">
                <input type="hidden" name="valueList" id="valueList" value="${parameters.chooserValue?default("")}">
                <a class="smallSubmit" href="#nowhere" onclick="javascript:saveActForm('${formId?default("")}')" title="保存">
                    保存</a>

            </td>
        </tr>
        </tbody>
    </table>
</form>
