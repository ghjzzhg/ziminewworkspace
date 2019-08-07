mailbox = [[id:'compose', name:'写邮件', iconSkin: 'icon05'],
           [id:'folders', name:'文件夹', iconSkin: 'pIcon01'],
           [id:'inbox', name:'收件箱(10/200)', pId:'folders', iconSkin: 'icon05'],
           [id:'draft', name:'草稿箱(1)', pId:'folders', iconSkin: 'icon05'],
           [id:'sent', name:'发件箱(10)', pId:'folders', iconSkin: 'icon05'],
           [id:'trash', name:'垃圾箱(0)', pId:'folders', iconSkin: 'icon05'],
           [id:'abc', name:'自定义(0)', pId:'folders', iconSkin: 'icon05'],
           [id:'contacts', name:'联系人', iconSkin: 'pIcon01'],
           [id:'groups', name:'分组', pId:'contacts', iconSkin: 'icon01'],
           [id:'internal', name:'内部联系人', pId:'contacts', iconSkin: 'icon01'],
           [id:'external', name:'外部联系人', pId:'contacts', iconSkin: 'icon01'],
           [id:'reject', name:'邮件拒收', iconSkin: 'icon03'],
           [id:'filter', name:'邮件过滤', iconSkin: 'icon03'],
           [id:'search', name:'邮件搜索', iconSkin: 'icon03'],
           [id:'signature', name:'个人签名', iconSkin: 'icon07'],
           [id:'settings', name:'邮箱设置', iconSkin: 'icon07'],
           [id:'autoReply', name:'自动回复', iconSkin: 'icon07']
];
request.setAttribute("data", mailbox);