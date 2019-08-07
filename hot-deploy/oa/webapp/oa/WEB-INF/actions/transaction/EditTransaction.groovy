import javolution.util.FastMap
param = parameters.get("param");
if(param!=null&&!param.equals(" ")){
    transactionMap = FastMap.newInstance();
    transactionMap.put("a","GTA89088");//事务编号
    transactionMap.put("b","2");//事务类别
    transactionMap.put("c",null);//开始日期
    transactionMap.put("d","运作中");//事务状况
    transactionMap.put("e","测试员2015-4-15 13:09:15");//跟进人
    transactionMap.put("f","电脑维护（每年）");//事务名称
    transactionMap.put("j","");//结束日期
    transactionMap.put("h","联系笔记本");//绑定设备
    transactionMap.put("i","葛木");//项目主管
    transactionMap.put("j","工程师");//简要要求
    transactionMap.put("k","1");//提前提醒
    transactionMap.put("l","无");//新反馈通知
    transactionMap.put("template","无");//内容
    context.transactionMap = transactionMap;
}



