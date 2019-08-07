<div>
    <div class="col-xs-12">
        <label>
            <input type="checkbox">启用外部SMTP服务器
        </label>
    </div>
    <div class="col-xs-12">
        <table class="basic-table hover-bar">
            <tr>
                <td class="label">
                    服务器地址:
                </td>
                <td>
                    <input type="text">
                </td>
            </tr>
            <tr>
                <td class="label">
                    端口:
                </td>
                <td>
                    <input type="text">
                </td>
            </tr>
            <tr>
                <td class="label">
                    用户名:
                </td>
                <td>
                    <input type="text">
                </td>
            </tr>
            <tr>
                <td class="label">
                    密码:
                </td>
                <td>
                    <input type="password">
                </td>
            </tr>
            <tr>
                <td class="label">
                    密码确认:
                </td>
                <td>
                    <input type="password">
                </td>
            </tr>
        </table>
    </div>
    <div class="col-xs-12">
说明：1、如果单位没有固定IP以及该IP对应的域名和域名邮箱，那本系统发出的Email将可能被外部手机邮箱及其他收件人邮箱服务商视作垃圾邮件而拒收，本位置的设置就是为解决该问题而设定的。启用了外部SMTP发信后，系统发往外部的Email将自动调用该SMTP服务器来发送（内部Email仍使用系统自带邮件服务器发送），确保通知Email不被拒收；<br/>
说明：2、为了确保外部Email能收到，用户可前往sina、126等免费邮件服务商申请免费电子邮箱，然后将相关参数填入以上位置并启用外部Email采用外部SMTP服务器发送即可。<br/>
说明：3、QQ邮箱目前已经开始强制要求SMTP使用SSL连接，本系统不支持SSL连接方式发送邮件，请改用网易或新浪等邮箱发送邮件。
    </div>
</div>