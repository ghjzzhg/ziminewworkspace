personSalaryItems = [[id:'SI001', title: '基本工资', active: 1, inOut: '<span style="color:green">应发</span>', basedOnSalary: '', remark:'来源于员工合同工资', value: '2000'],
                   [id:'SI001', title: '社保基数', active: 1, inOut: '显示', basedOnSalary: '', remark:'社保基数', value: '2000'],
                   [id:'SI001', title: '公积金基数', active: 1, inOut: '显示', basedOnSalary: '', remark:'公积金基数', value: '2000'],
                   [id:'SI002', title: '养老保险', active: 1, inOut: '<span style="color:red">应扣</span>', basedOnSalary: '社保基数', remark:'养老保险(个人)2014年标准', value: 8],
                   [id:'SI003', title: '失业保险', active: 1, inOut: '<span style="color:red">应扣</span>', basedOnSalary: '社保基数', remark:'失业保险(个人)2014年标准', value: 1],
                   [id:'SI004', title: '生育保险', active: 1, inOut: '<span style="color:red">应扣</span>', basedOnSalary: '社保基数', remark:'生育保险(个人)2014年标准', value: 1],
                   [id:'SI005', title: '公积金', active: 1, inOut: '<span style="color:red">应扣</span>', basedOnSalary: '公积金基数', remark:'公积金(个人)2014年标准', value: 2],
                   [id:'SI002', title: '养老保险', active: 1, inOut: '显示', basedOnSalary: '社保基数', remark:'养老保险(公司)2014年标准', value: 10],
                   [id:'SI003', title: '失业保险', active: 1, inOut: '显示', basedOnSalary: '社保基数', remark:'失业保险(公司)2014年标准', value: 5],
                   [id:'SI004', title: '生育保险', active: 1, inOut: '显示', basedOnSalary: '社保基数', remark:'生育保险(公司)2014年标准', value: 3],
                   [id:'SI005', title: '公积金', active: 1, inOut: '显示', basedOnSalary: '公积金基数', remark:'公积金(公司)2014年标准', value: 2],
                   [id:'SI006', title: '奖金', active: 1, inOut: '<span style="color:green">应发</span>', basedOnSalary: '', remark:'奖金来源于奖惩模块', value: ''],
                   [id:'SI007', title: '罚金', active: 1, inOut: '<span style="color:red">应扣</span>', basedOnSalary: '', remark:'罚金来源于奖惩模块', value: ''],
                   [id:'SI008', title: '实到天数', active: 1, inOut: '<span style="color:green">应发</span>', basedOnSalary: '', remark:'来源于考勤模块', value: ''],
                   [id:'SI009', title: '应发工资', active: 1, inOut: '<span style="color:green">应发</span>', basedOnSalary: '', remark:'应发工资', value: '']
];
context.personSalaryItems = personSalaryItems;