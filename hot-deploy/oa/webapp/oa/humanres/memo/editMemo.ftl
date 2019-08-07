<script language="javascript">
    $(function () {
        template = KindEditor.create('textarea[name="template"]', {
            allowFileManager: true
        });

    });
</script>

<div>
    <form name="" id="" class="basic-form">
        <div>
            <table class="basic-table">
                <tbody>
                <tr>
                    <td class="label">
                        <label for="MemoAddForm_a" id="MemoAddForm_a_title">备忘录类别:</label>
                    </td>
                    <td>
                        <select name="b" id="MemoAddForm_a" size="1">
                            <option value="1">全部</option>
                            <option value="2">个人日记</option>
                            <option value="3">工作提醒</option>
                        </select>
                    </td>

                    <td class="label">
                        <label for="MemoAddForm_b"
                               id="MemoAddForm_b_title">文档类别:</label>
                    </td>
                    <td>
                        备忘<input type="radio" size="25" id="MemoAddForm_b">、日记<input type="radio">
                        <script language="JavaScript"
                                type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                    </td>
                </tr>

                <tr>
                    <td class="label">
                        <label for="MemoAddForm_c" id="MemoAddForm_c_title"><b style="color: red">*</b>备忘录标题：</label>
                    </td>
                    <td>
                        <input type="text" size="25" id="MemoAddForm_c">
                        <script language="JavaScript"
                                type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                    </td>

                    <td class="label">
                        <label for="MemoAddForm_d"
                               id="MemoAddForm_d_title">待办事宜:</label>
                    </td>
                    <td>
                        待办<input type="radio" size="25" id="MemoAddForm_d">、已办<input type="radio">
                        <script language="JavaScript"
                                type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                    </td>
                </tr>

                <tr>
                    <td class="label">
                        <label for="MemoAddForm_e" id="MemoAddForm_e_title"><b style="color: red">*</b>拟办日期：</label>
                    </td>
                    <td>
                        <input type="text" size="25" id="MemoAddForm_e">
                        <script language="JavaScript"
                                type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                    </td>

                    <td class="label">
                        <label for="MemoAddForm_f"
                               id="MemoAddForm_f_title">访问口令:</label>
                    </td>
                    <td>
                        <input type="text" size="25" id="MemoAddForm_f">
                        <script language="JavaScript"
                                type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                    </td>
                </tr>

                <tr>

                    <td class="label">
                        <label for="MemoAddForm_g"
                               id="MemoAddForm_g_title"><b style="color: red">*</b>备忘录内容:</label>
                    </td>
                    <td>
                    <td colspan="3">
                        <span class="tooltip">(提示：可选择以下图标直接上传附件！)</span>
                    </td>
                </tr>
                <tr>
                    <td colspan="4">
                        <textarea name="template" id="MemoAddForm_g" style="width:100%;"></textarea>
                        <script language="JavaScript"
                                type="text/javascript">ajaxAutoCompleter('', false, 2, 300);</script>
                    </td>
                </tr>

                </tbody>
            </table>
            <div>
                "*"号的表示必须填写； 特别说明：1、备忘录仅本人可浏览，其他人均无权限浏览！2、有口令查看的文档正文和口令都采用了高强度变形加密，即使管理员也无法查看，请务必记住添加的访问口令！
            </div>
            <div align="center">
                <input type="submit" value="提交备忘录">
            </div>
        </div>
    </form>
</div>
