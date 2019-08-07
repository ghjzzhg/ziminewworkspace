<script language="javascript">
    $(function () {
        template = KindEditor.create('textarea[name="template"]', {
            allowFileManager: true
        });
    });

    function selectChange(obj) {
        var value = $('select[name="proposalType"] option:selected').text();
        $('input[name="proposalType"]').attr("value", value);
    }

    function feedbackSubmit(){
        $("#feedbackContext").replaceWith('');
        $("#tbody_feedbackContext").append('<tr><td><b>反馈人：</b>佚名（测试员/180.116.53.4）</td>'+
                                            '<td><b>反馈时间：</b>2015-6-5 9:24:00</td></tr>'+
                                            '<tr><td colspan="2" ">'+template.text()+'</td></tr>');
    }
</script>

<div>
    <form name="perfExamEditForm" id="perfExamEditForm" class="basic-form">
        <div>
            <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
                <tbody>
                <tr>
                    <td colspan="6">
                        <table cellpadding="0" cellspacing="0" border="1" width="100%"
                               style="border-collapse: collapse">
                            <tbody>
                            <tr>
                                <td class="label">　
                                    <label>提案编号</label>
                                </td>
                                <td>
                                    GHT2015062
                                </td>
                                <td class="label">　
                                    <label>提案类别</label>
                                </td>
                                <td>
                                    生产效率提案
                                </td>
                            </tr>
                            <tr>
                                <td class="label">　
                                   <label>提 交 人</label>
                                </td>
                                <td>
                                    宋涛
                                </td>
                                <td class="label">　
                                    <label>提交日期</label>
                                </td>
                                <td>
                                    2015-06-01 17:30:25
                                </td>
                            </tr>
                            <tr>
                                <td class="label">　
                                    <label>提案标题</label>
                                </td>
                                <td>
                                    aaaaaa
                                </td>
                                <td class="label">
                                    <label>编辑日期</label>
                                </td>
                                <td>
                                    2015-06-02 17:30:25
                                </td>
                            </tr>
                            <tr>
                                <td class="label">　
                                    <label>浏览权限</label>
                                </td>
                                <td></td>
                            </tr>
                            <tr>
                                <td class="label">　
                                    <label>现状描述</label>
                                </td>
                                <td colspan="3"></td>
                            </tr>
                            <tr>
                                <td class="label">　
                                    <label>提案内容</label>
                                </td>
                                <td colspan="3"></td>
                            </tr>
                            <tr>
                                <td class="label">　
                                    <label>预期效果</label>
                                </td>
                                <td colspan="3"></td>
                            </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td colspan="6">
                        <table cellpadding="0" cellspacing="0" border="1" width="100%"
                               style="border-collapse: collapse">
                            <tbody>
                                <tr style="background:#b3d4ff;font-weight: bold">
                                    <td >评定人</td>
                                    <td >评定时间</td>
                                    <td >投稿奖金</td>
                                    <td >提案奖金</td>
                                    <td >提案等级</td>
                                    <td >提案评价</td>
                                </tr>
                                <tr>
                                    <td colspan="6" style="text-align: center;font-size: 18px;color: red">没有评语</td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td colspan="6">
                        <table cellpadding="0" cellspacing="0" border="1" width="100%"
                               style="border-collapse: collapse">
                            <tbody>
                            <tr>
                                <td class="label" colspan="4"><p style="font-size: 18px;color: #0000CC;text-align: center">提案评定</p></td>
                            </tr>
                            <tr>
                                <td class="label">
                                    <label for="proposalAssess_a" id="proposalAssess_a_title">投稿奖金</label>
                                </td>
                                <td>
                                    <select id="proposalAssess_a">
                                        <option value="1">0元</option>
                                        <option value="2">2元</option>
                                        <option value="3">4元</option>
                                    </select>
                                </td>
                                <td class="label">
                                    <label for="proposalAssess_b" id="proposalAssess_b_title">提案奖金</label>
                                </td>
                                <td>
                                    <select id="proposalAssess_b">
                                        <option value="1">请输入</option>
                                        <option value="2">不奖励</option>
                                        <option value="3">待定</option>
                                    </select>
                                </td>

                            </tr>
                            <tr>
                                <td class="label">
                                    <label for="proposalAssess_c" id="proposalAssess_c_title">提案等级</label>
                                </td>
                                <td>
                                    <select id="proposalAssess_c">
                                        <option value="1">aaaa</option>
                                        <option value="1">aaaa</option>
                                        <option value="1">aaaa</option>
                                    </select>
                                </td>

                            </tr>
                            <tr>
                                <td class="label">
                                    <label for="proposalAssess_d" id="proposalAssess_d_title">提案评价</label>
                                </td>
                                <td colspan="3">
                                    <textarea id="proposalAssess_d"></textarea>
                                </td>
                            </tr>

                            <tr align="center">
                                <td colspan="4"><input type="submit" value="确定评定"
                                           onclick="javascript:document.lookupparty.submit();"/>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td colspan="6">
                        <table cellpadding="0" cellspacing="0" border="1" width="100%"
                               style="border-collapse: collapse">
                            <tbody>
                            <tr>
                                <td colspan="4"><p style="font-size: 18px;color: #0000CC;text-align: center">安排提案跟进人/部门</p></td>
                            </tr>
                            <tr>
                                <td class="label">
                                    <label for="proposalFollowUp_a" id="proposalFollowUp_a_title">跟进部门</label>
                                </td>
                                <td>
                                    <textarea></textarea>
                                </td>
                                <td >
                                    <label for="proposalFollowUp_b" id="proposalFollowUp_b_title">跟进人</label>
                                </td>
                                <td>
                                    <textarea></textarea>
                                </td>
                            </tr>
                            <tr>
                                <td class="label">
                                    <label for="proposalFollowUp_c" id="proposalFollowUp_c_title">执行情况</label>
                                </td>
                                <td>
                                    <select id="proposalFollowUp_c">
                                        <option value="1">未启动</option>
                                        <option value="1">执行中</option>
                                        <option value="1">已完成</option>
                                    </select>
                                </td>
                                <td class="label">
                                    <label for="proposalFollowUp_d" id="proposalFollowUp_d_title">预计完成时间</label>
                                </td>
                                <td>
                                   <input type="date" id="proposalFollowUp_d"/>
                                </td>

                            </tr>
                            <tr>
                                <td class="label">
                                    <label>填 报 人</label>
                                </td>
                                <td ></td>
                                <td class="label">
                                    <label>填报时间</label>
                                </td>
                                <td ></td>
                            </tr>

                            <tr align="center">
                                <td colspan="4"><input type="submit" value="确定安排"
                                                       onclick=""/>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td colspan="6">
                        <table cellpadding="0" cellspacing="0" border="1" width="100%"
                               style="border-collapse: collapse">
                            <tbody id="tbody_feedbackContext">
                            <tr>
                                <td colspan="2"><p style="font-size: 18px;color: #0000CC;text-align: center">用户意见/反馈</p></td>
                            </tr>
                            <tr>

                            </tr>
                            <tr id="feedbackContext">
                                <td colspan="2" style="text-align: center;font-size: 18px;color: red">没有反馈</td>
                            </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td colspan="6">
                        <table cellpadding="0" cellspacing="0" border="1" width="100%"
                               style="border-collapse: collapse">
                            <tbody>
                            <tr >
                                <td class="label">
                                    <label><b style="color: red">用户意见建议反馈</b></label>
                                </td>
                                <td ><textarea name="template" style="width:100%;"></textarea></td>
                            </tr>
                            <tr>
                                <td class="label">
                                    <label for="opinion_name">你的昵称</label>
                                </td>
                                <td ><input type="text" id="opinion_name"></td>
                            </tr>
                            <tr align="center">
                                <td colspan="2">
                                    <a href="#" class="smallSubmit" onclick="feedbackSubmit()">提交反馈</a>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </form>
</div>

