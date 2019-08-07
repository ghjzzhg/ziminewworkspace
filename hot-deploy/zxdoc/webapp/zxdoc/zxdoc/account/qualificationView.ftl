<#if parameters.type=="NoPartner">
<div class="portlet light">
    <div class="portlet-title">
        <div class="caption font-red-sunglo">
            <i class="icon-settings font-red-sunglo"></i>
            <span class="caption-subject bold uppercase"> 实名认证</span>
            <span class="label label-sm label-success">${parameters.data.description?default('')}</span>
            <span class="label label-sm label-success">${parameters.data.qualificationStatus?default('')}</span>
        </div>
    </div>
    <div class="portlet-body">
        <table class="table table-hover table-striped table-bordered">
            <tbody>
            <tr>
                <td> 社会信用代码</td>
                <td>
                    <div style="word-break: break-all">
                    ${parameters.data.info.regNumber?default('')}
                    </div>
                </td>
            </tr>
            <tr>
                <td> 企业地址</td>
                <td>
                    <div style="word-break: break-all">
                        ${parameters.data.info.address1?default('')}
                    </div>
                </td>
            </tr>
            <tr>
                <td> 营业执照扫描件</td>
                <td>
                    <#if parameters.data.attachements?has_content>
                        <#list parameters.data.attachements as attr>
                            <#if attr.dataResourceId?has_content>
                                <img onclick="viewPdfInLayer('${attr.dataResourceId?default('')}')" src="/content/control/imageView?fileName=${attr.dataResourceId?default('')}&externalLoginKey=${externalLoginKey}" width="200px">
                            </#if>
                        </#list>
                    </#if>
                </td>
            </tr>
            <tr>
                <td > 经营范围</td>
                <td width="70%">
                    <#if parameters.data.info.bizScope?has_content>
                        <textarea class="form-control" rows="3" disabled="disabled">${parameters.data.info.bizScope?default('')}</textarea>
                    </#if>

                </td>
            </tr>
            <tr>
                <td> 企业官网 </td>
                <td>
                    <div style="word-break: break-all">
                    <a class="btn btn-outline sbold uppercase" id="demo_5" href="${parameters.data.info.infoString?default('')}" target="_blank">${parameters.data.info.infoString?default('')}</a>
                    </div>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</div>
<#else >
<div class="portlet light">
    <div class="portlet-title">
        <div class="caption font-red-sunglo">
            <i class="icon-settings font-red-sunglo"></i>
            <span class="caption-subject bold uppercase"> 实名认证</span>
            <span class="label label-sm label-success">${parameters.type?default('')}</span>
            <span class="label label-sm label-success">${parameters.data.qualificationStatus?default('')}</span>
        </div>
    </div>
    <div class="portlet-body">
        <table class="table table-hover table-striped table-bordered">
            <tbody>
            <tr>
                <td> 姓名</td>
                <td>
                    <div style="word-break: break-all">
                        ${parameters.data.info.fullName?default('')}
                    </div>
                </td>
            </tr>
            <tr>
                <td> 身份证</td>
                <td>
                ${parameters.data.info.idCard?default('')}
                </td>
            </tr>
            <tr>
                <td> 职业资格证号</td>
                <td>
                    <textarea class="form-control" rows="3" disabled="disabled">${parameters.data.info.qualifiNum?default('')}</textarea>
                </td>
            </tr>
            <tr>
                <td> 职业资格证扫描件</td>
                <td>
                    <#if parameters.data.attachements?has_content>
                        <#list parameters.data.attachements as attr>
                            <img onclick="viewPdfInLayer('${attr.dataResourceId?default('')}')" src="/content/control/imageView?fileName=${attr.dataResourceId?default('')}&externalLoginKey=${externalLoginKey}" width="200px">
                        </#list>
                    </#if>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</div>
</#if>