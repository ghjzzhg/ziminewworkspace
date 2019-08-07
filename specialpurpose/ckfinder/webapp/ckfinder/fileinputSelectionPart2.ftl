<#include "component://common/webcommon/includes/commonMacros.ftl"/>
            <#--<table id="example" class="display dataTable no-footer" cellspacing="0" width="100%" role="grid" style="width: 100%;">
                <thead>
                <tr style="background: #f7f7f7;border-radius: 2px;border: 1px solid #d2d2d2;color: #888;overflow: hidden;"
                    role="row">
                    <td width="10%" class="sorting_disabled" rowspan="1" colspan="1" style="width: 55px;"></td>
                    <td width="10%" class="sorting_disabled" rowspan="1" colspan="1" style="width: 55px;">序号</td>
                    <td width="70%" class="sorting_disabled" rowspan="1" colspan="1" style="width: 601px;">文件名</td>
                    <td width="20%" class="sorting_disabled" rowspan="1" colspan="1" style="width: 145px;">修改日期</td>
                </tr>
                </thead>
                <tbody id="fileTable">
                <tr role="row" class="odd">
                    <td><input type="checkbox" value="10102" fileName="文件名.docx"/> </td>
                    <td>0</td>
                    <td><i
                            class="fa fa-file-o"></i> <a
                            href="javascript:showFileList('/CASE010101/','10102','CASE010101')">/根目录/子目录/目录1/文件名.docx</a>
                    </td>
                    <td>201608221231</td>
                </tr>
                <tr role="row" class="even">
                    <td><input type="checkbox" value="10103" fileName="文件名.txt"/> </td>
                    <td>1</td>
                    <td><i
                            class="fa fa-file-o"></i> <a href="javascript:showFileList('/其他CASE/','10103','其他CASE')">/根目录/文件名.txt</a>

                    </td>
                    <td>201608221233</td>
                </tr>
                </tbody>
            </table>-->
        </div>
    <#if parameters.allowLocalUpload?boolean>
        <div class="tab-pane fade" id="tab_1_2">
            <span style="color: red"></span>
        <@fileinput name="${parameters.inputName?default('atta')}" selectText="<i class='fa fa-upload'></i>"/>
        </div>
    </#if>
    </div>
</div>
<#--<div class="note note-info">-->
<#--<pre>-->
    <#--温馨提示-->
<#--<#assign fileSize = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("content.properties", "fileSize")>-->
    <#--1.<span style="color: red">上传所有文件总大小请不要超过${fileSize?default("50")}兆。</span>-->
    <#--2.<span style="color: red">文件名称不要超过50个字符。</span>-->
<#--</pre>-->
<#--</div>-->