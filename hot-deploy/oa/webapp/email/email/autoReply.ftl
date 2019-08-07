<script type="text/javascript">
    $(function() {
        mailContent = KindEditor.create('textarea[name="content"]', {
            allowFileManager: false
        });

        var autoheight = mailContent.edit.doc.body.scrollHeight;
        mailContent.edit.setHeight(autoheight);
    });
</script>
<div>
    <table class="basic-table" style="width: 100%">
        <tr>
            <td colspan="2" style="font-weight: bold;">
                <div>
                    <label>
                        <input type="checkbox">启用自动回复功能
                    </label>
                </div>
                <div>
                    <label>
                        <input type="checkbox">启用自动回复过期功能
                    </label><@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss" value="${requestParameters.fromDate!}" size="25" maxlength="30" id="fromDate1" dateType="" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </div>

            </td>
        </tr>
        <tr>
            <td class="label">
                主题
            </td>
            <td>
                <input type="text" style="width: 100%">
            </td>
        </tr>
        <tr>
            <td class="label">内容</td>
            <td>
                <textarea name="content" style="width: 100%; height: 300px">
                    <pre>




◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇
测试员
Tel: 0519-81888556
Mobile: 18661234562
E_mail: support@rextec.com.cn
常州瑞克斯信息科技有限公司
公司地址：江苏省常州市新北区太湖东路9-4号D417
','''╭⌒╮⌒╮.',''',,',.'',,','',.
╱◥███◣''o┈ ；工作与生活和谐发展 ┄o.'',,'
︱田︱田 田| ','''',.o┈ 中小企业信息化管理专家 ┄o'
╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬╬
                        </pre>
                </textarea>
            </td>
        </tr>
    </table>
</div>