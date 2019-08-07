List transactionRecordList = new ArrayList();
List contractRecordList = new ArrayList();
List rewardsAndPunishmentRecordList = new ArrayList();
transactionMap = [:];
transactionMap.put("employeeName","张雨晴")
transactionMap.put("employeeNumber","S0414")
transactionMap.put("lastDepartment","策划部")
transactionMap.put("newDepartment","销售部")
transactionMap.put("newPost","职员")
transactionMap.put("transactionTime","2015-04-03")
transactionMap.put("transactionType","试用期转正")
transactionRecordList.add(transactionMap);
transactionMap1 = [:];
transactionMap1.put("employeeName","李明")
transactionMap1.put("employeeNumber","S0418")
transactionMap1.put("lastDepartment","开发部")
transactionMap1.put("newDepartment","销售部")
transactionMap1.put("newPost","职员")
transactionMap1.put("transactionTime","2015-04-03")
transactionMap1.put("transactionType","试用期转正")
transactionRecordList.add(transactionMap1);




contractMap = [:];
contractMap.put("employeeName","张雨晴")
contractMap.put("employeeNumber","S0414")
contractMap.put("contractNumber","DF15611DDFGOIE")
contractMap.put("contractName","试用期合同")
contractMap.put("contractType","劳动合同")
contractMap.put("valid","2015-04-03")
contractMap.put("Wage","1000")
contractMap.put("signTime","2015-02-03")
contractRecordList.add(contractMap);

contractMap1 = [:];
contractMap1.put("employeeName","张雨晴")
contractMap1.put("employeeNumber","S0414")
contractMap1.put("contractNumber","DF15665452")
contractMap1.put("contractName","正式合同")
contractMap1.put("contractType","劳动合同")
contractMap1.put("valid","2016-04-04")
contractMap1.put("Wage","2500")
contractMap1.put("signTime","2015-04-05")
contractRecordList.add(contractMap1);

contractMap4 = [:];
contractMap4.put("employeeName","李明")
contractMap4.put("employeeNumber","S0418")
contractMap4.put("contractNumber","DF15611DDFGOGE")
contractMap4.put("contractName","试用期合同")
contractMap4.put("contractType","劳动合同")
contractMap4.put("valid","2015-04-03")
contractMap4.put("Wage","1000")
contractMap4.put("signTime","2015-02-03")
contractRecordList.add(contractMap4);

contractMap5 = [:];
contractMap5.put("employeeName","李明")
contractMap5.put("employeeNumber","S0418")
contractMap5.put("contractNumber","DF15999452")
contractMap5.put("contractName","正式合同")
contractMap5.put("contractType","劳动合同")
contractMap5.put("valid","2016-04-018")
contractMap5.put("Wage","2500")
contractMap5.put("signTime","2015-04-18")
contractRecordList.add(contractMap5);

contractMap6 = [:];
contractMap6.put("employeeName","蔡金龙")
contractMap6.put("employeeNumber","S0413")
contractMap6.put("contractNumber","DFHH9452")
contractMap6.put("contractName","正式合同")
contractMap6.put("contractType","劳动合同")
contractMap6.put("valid","2016-05-04")
contractMap6.put("Wage","2500")
contractMap6.put("signTime","2015-05-05")
contractRecordList.add(contractMap6);





contractMap2 = [:];
contractMap2.put("employeeName","张雨晴")
contractMap2.put("employeeNumber","S0414")
contractMap2.put("rewardsAndPunishmentNumber","DF15611DDFGOIE")
contractMap2.put("rewardsAndPunishmentName","销售奖励")
contractMap2.put("rewardsAndPunishmentType","通报表扬")
contractMap2.put("rewardsAndPunishmentTime","2015-04-03")
contractMap2.put("rewardsAndPunishmentLevel","公司级")
contractMap2.put("rewardsAndPunishmentAmount","￥200")
rewardsAndPunishmentRecordList.add(contractMap2);

contractMap3 = [:];
contractMap3.put("employeeName","张雨晴")
contractMap3.put("employeeNumber","S0414")
contractMap3.put("rewardsAndPunishmentNumber","DF1SDSD2323")
contractMap3.put("rewardsAndPunishmentName","销售奖励")
contractMap3.put("rewardsAndPunishmentType","通报表扬")
contractMap3.put("rewardsAndPunishmentTime","2015-06-01")
contractMap3.put("rewardsAndPunishmentLevel","公司级")
contractMap3.put("rewardsAndPunishmentAmount","￥200")
rewardsAndPunishmentRecordList.add(contractMap3);

contractMap7 = [:];
contractMap7.put("employeeName","李明")
contractMap7.put("employeeNumber","S0418")
contractMap7.put("rewardsAndPunishmentNumber","DF1SD855553")
contractMap7.put("rewardsAndPunishmentName","违反公司章程惩罚")
contractMap7.put("rewardsAndPunishmentType","小过")
contractMap7.put("rewardsAndPunishmentTime","2015-06-01")
contractMap7.put("rewardsAndPunishmentLevel","公司级")
contractMap7.put("rewardsAndPunishmentAmount","￥-200")
rewardsAndPunishmentRecordList.add(contractMap7);



List trainManagementList = new ArrayList();
trainMap = [:];
trainMap.put("employeeName","李明")
trainMap.put("employeeNumber","S0418")
trainMap.put("trainNumber","FHJDJI584661")
trainMap.put("trainName","销售培训")
trainMap.put("trainType","内部培训")
trainMap.put("trainDate","2015-02-03")
trainMap.put("trainLecturer","肖敏")
trainMap.put("trainAddress","销售部1201")
trainManagementList.add(trainMap)

trainMap1 = [:];
trainMap1.put("employeeName","李明")
trainMap1.put("employeeNumber","S0418")
trainMap1.put("trainNumber","FHJHHHDJIE1")
trainMap1.put("trainName","开发培训")
trainMap1.put("trainType","国内培训")
trainMap1.put("trainDate","2015-03-03")
trainMap1.put("trainLecturer","著名软件书作人")
trainMap1.put("trainAddress","北京朝阳区江丽软件培训中心")
trainManagementList.add(trainMap1)
context.trainManagementList = trainManagementList;
context.transactionRecordList = transactionRecordList;
context.contractRecordList = contractRecordList;
context.rewardsAndPunishmentRecordList = rewardsAndPunishmentRecordList;

