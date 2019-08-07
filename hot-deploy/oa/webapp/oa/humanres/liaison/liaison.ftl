<script language="javascript">
    template = KindEditor.create('textarea[name="template"]', {
        allowFileManager: true
    });

    function liaisonForwardSelect() {
        displayInTab3("LiaisonForwardTab", "转发人选择", {
            requestUrl: "checkPerson",
            data: {param: "range"},
            width: "600px",
            position: 'top'
        });
    }
</script>

<div>
    <div>
        <table align="center" cellpadding="0" cellspacing="0" border="0" width="100%" height="50px">
            <tbody>
            <tr>
                <td valign="top"><img
                        src="/images/rextec_logo.png" width="140px" height="45px" alt="瑞克斯信息科技有限公司">
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <form name="" id="" class="basic-form">
        <div>
            <div style="text-align: center; letter-spacing: 20px; margin:0 auto; font-weight: bold;font-size: 3em;margin-bottom: 0.5em;">
                工作联络单
            </div>
            <div>
                <div style="float: left">
                    <span class="label">发文单位：<b style="color:blue">研发中心</b></span>
                </div>
                <div style="float: right">
                    <span class="label">编号：<b style="color:blue">MIS-YFZX-15-06-002</b></span><br>
                    <span class="label">日期：<b style="color:blue">2015-6-6 10:43:20</b></span>
                </div>
            </div>
            <div>
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
                                            <label>主　　送:</label>
                                        </td>
                                        <td>
                                            ${liaisonMap.q}
                                        </td>
                                        <td class="label">
                                            <label>抄　　送:</label>
                                        </td>
                                        <td>
                                            ${liaisonMap.w}
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="label">
                                            <label>事　　由:</label>
                                        </td>
                                        <td colspan="3">
                                            ${liaisonMap.e}
                                        </td>

                                    </tr>
                                    <tr>
                                        <td class="label">
                                            <label>发文意图:</label>
                                        </td>
                                        <td>
                                            ${liaisonMap.r}
                                        </td>
                                        <td class="label">
                                            <label>审核类型:</label>
                                        </td>
                                        <td>
                                            ${liaisonMap.t}
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="label">
                                            <label>具体事项:</label>
                                        </td>
                                        <td colspan="3">
                                            ${liaisonMap.y}
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="label">
                                            <label>审核意见:</label>
                                        </td>
                                        <td colspan="3">
                                        ${liaisonMap.u}
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="label">
                                            <label>发文部门:</label>
                                        </td>
                                        <td>
                                        </td>
                                        <td class="label">
                                            <label>经办人:</label>
                                        </td>
                                        <td>
                                        ${liaisonMap.i}
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="label">
                                            <label>是否回复:</label>
                                        </td>
                                        <td>
                                        ${liaisonMap.o}
                                        </td>
                                        <td class="label">
                                            <label>希望回复日期:</label>
                                        </td>
                                        <td>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="label">
                                            <label>回复意见:</label>
                                        </td>
                                        <td colspan="3">
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="label">
                                            <label>回复部门:</label>
                                        </td>
                                        <td>
                                        </td>
                                        <td class="label">
                                            <label>经办人:</label>
                                        </td>
                                        <td>
                                        </td>
                                    </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </form>
</div>
<div style="margin-top: 20px">
    <div align="center">
        <a href="#" class="smallSubmit">打印本联络单</a>
        <a href="#" class="smallSubmit">浏览日志</a>
        <a href="#" class="smallSubmit">导出为word文件</a>
    </div>
</div>

<div style="margin-top: 20px">
    <div class="screenlet-title-bar">
        <ul>
            <li class="h3">信息回复</li>

        </ul>
        <br class="clear">
    </div>
    <form name="" id="" class="basic-form">
        <table class="basic-table" cellspacing="0" cellpadding="0" align="center">
            <tbody>
            <tr>
                <td colspan="6">
                    <table cellpadding="0" cellspacing="0" border="1" width="100%"
                           style="border-collapse: collapse">
                        <tbody>
                        <tr>
                            <td class="label">
                                <label>接收人:</label>
                            </td>
                            <td>
                                <table>
                                    <tr>
                                        <td class="label">
                                            <label for="Liaison_a"
                                                   id="Liaison_a_title">主送:</label></td>
                                        <td colspan="2">
                                        ${liaisonMap.q}
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="label">
                                            <label for="Liaison_b"
                                                   id="Liaison_b_title">抄送:</label></td>
                                        <td colspan="2">

                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="label">
                                            <label for="Liaison_c"
                                                   id="Liaison_c_title">转发:</label></td>
                                        <td>
                                            <textarea id="Liaison_c" style="cursor:pointer" name="liaison_forward"
                                                      onclick="liaisonForwardSelect()"></textarea>
                                            <script language="JavaScript"
                                                    type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>

                                        </td>
                                        <td>
                                            <a href="#" class="smallSubmit" onclick="liaisonForwardSelect()">选择转发人</a>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td class="label">
                                <label for="Liaison_d"
                                       id="Liaison_d_title">回复意见:</label></td>
                            <td>
                                <textarea id="Liaison_d" name="template" style="width:100%;"></textarea>
                                <script language="JavaScript"
                                        type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                            </td>
                        </tr>
                        <tr>
                            <td class="label">
                                <label for="Liaison_e"
                                       id="Liaison_e_title">邮件通知:</label></td>
                            <td>
                                <input type="checkbox" id="Liaison_e">邮件通知主送人
                                <input type="checkbox" >邮件通知抄送人
                                <input type="checkbox" >邮件通知发送人
                                <script language="JavaScript"
                                        type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
            </tbody>
        </table>
    </form>
    <div style="margin-top: 20px">
        <div align="center">
            <a href="#" class="smallSubmit">回复</a>
        </div>
    </div>
</div>