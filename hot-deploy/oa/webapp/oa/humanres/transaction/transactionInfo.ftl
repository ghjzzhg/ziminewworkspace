<script language="javascript">
    $(function () {
        template = KindEditor.create('textarea[name="template"]', {
            allowFileManager: true
        });

        $('a[name="confirmFeedback"]').click(function () {
            var strValue = template.text();
            $('#textContext').append('<div style="text-align: left;background: gainsboro;font-size: 8px">' +
            '所在部门：研发中心，反馈人：测试员，反馈时间：2015-6-4 10:44:45，IP：192.168.101</div>' +
            '<div style="height: 100px;">' +
            '<div style="color: green">[进度反馈：2015-6-4:本周未开始]：</div>' +
            '<div>' + strValue + '</div>' +
            '</div>');
        });
    });
</script>

<div class="yui3-skin-sam">
    <form name="perfExamEditForm" id="perfExamEditForm" class="basic-form">
        <div class="yui3-skin-audio-light">
            <div>
                <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
                    <tbody>
                    <tr>
                        <td>事务编号</td>
                        <td><input type="text" value="GSLKJ80980" disabled="disabled"></td>
                        <td>事务类别</td>
                        <td><input type="text" value="电脑维护（按年重复)" disabled="disabled"></td>
                        <td>事务状况</td>
                        <td><input type="text" value="运作中" disabled="disabled"></td>
                    </tr>
                    <tr>
                        <td>事务名称</td>
                        <td colspan="3"><input type="text" value="显卡维修" disabled="disabled"></td>
                        <td>事务进度</td>
                        <td><input type="text" value="（1/3）开始" disabled="disabled"></td>

                    </tr>
                    <tr>
                        <td>跟 进 人</td>
                        <td><input type="text" value="程晨" disabled="disabled"></td>
                        <td>生效时间</td>
                        <td><input type="text" value="2015-06-01" disabled="disabled"></td>
                        <td>项目主管</td>
                        <td><input type="text" value="顾静" disabled="disabled"></td>
                    </tr>
                    <tr>
                        <td>绑定设备</td>
                        <td><input type="text" value="联想笔记本" disabled="disabled"></td>
                        <td>简要要求</td>
                        <td><input type="text" value="工程师" disabled="disabled"></td>
                        <td>录 入 人</td>
                        <td><input type="text" value="赵品" disabled="disabled"></td>
                    </tr>
                    <tr>
                        <td>内容说明</td>
                        <td colspan="5"><textarea value="" disabled="disabled">尽快修复</textarea></td>
                    </tr>
                    </tbody>
                </table>
                <div style="margin-left: 20px;margin-right: 20px">
                    <div style="margin-top: 20px;height: 200px">
                        <div style="text-align: center;font-size:large"><b style="color: #0000CC">录入</b>反馈内容</div>
                        <div style="color: green;font-size: 10px;text-align: center">
                            (欢迎提出你的建议和意见，重要事项请点击下面的“□Email通知”，系统会自动将反馈Email给收件人)
                        </div>
                        <div style="margin-top: 5px">
                            <span><b style="font-weight: bold;color:gray">反馈内容：</b><b
                                    style="color: #0000CC;font-size: 8px;font-weight: 100">(提示：选择图标可以上传附件！)</b></span>
                        <span style="margin-left: 80px"><b style="font-weight: bold;color:gray">事务进度：</b>
                        <select>
                            <option value="1">(1/3)开始</option>
                            <option value="2">(2/3)进行中</option>
                            <option value="3">(3/3)完成</option>
                        </select>
                        </span>
                            <span><b style="font-weight: bold;color:gray">发生时间：<input type="date"/></b></span>
                        </div>
                        <textarea name="template" style="height: 200px"></textarea>

                        <div>
                            <div style="float: left;">
                                <input type="checkbox"><b style="font-weight: 100;font-size: 8px">Email通知</b>
                            </div>
                            <div style="float: right;margin-right: 100px">
                                <a href="#" class="smallSubmit" name="confirmFeedback">确认反馈</a>&nbsp
                                <a href="#" class="smallSubmit" onclick="$.transaction.visitLog('电脑维护（每年）')">浏览日志</a>
                            </div>
                        </div>
                        <div style="clear:both;"></div>
                    </div>

                    <div style="margin-top: 150px;border: 1px solid #DDD;height: 500px;overflow: scroll">
                        <div class="screenlet-title-bar">
                            <div style="color: black;font-weight: bold;font-size: large">最新反馈</div>
                        </div>
                        <div id="textContext" style="margin-top: 20px;font-size: 12px">
                            <div style="text-align: left;background: gainsboro;font-size: 8px">
                                所在部门：研发中心，反馈人：测试员，反馈时间：2015-6-4 10:44:45，IP：192.168.101
                            </div>
                            <div style="height: 100px;">
                                <div style="color: green">[进度反馈：2015-6-4:本周未开始]：</div>
                                <div>正在进行中</div>
                            </div>
                            <div style="text-align: left;background: gainsboro;font-size: 8px">
                                所在部门：研发中心，反馈人：测试员，反馈时间：2015-6-5 11:44:45，IP：192.168.101
                            </div>
                            <div style="height: 100px;">
                                <div style="color: green">[进度反馈：2015-6-4:本周未开始]：</div>
                                <div>已完成</div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </form>
</div>