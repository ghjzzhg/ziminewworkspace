parentId = parameters.get("id");
contacts = [];
if(parentId){
    switch (parentId){
        case '1': contacts = [
                [id: "11", name: '小张', pId:"1"],
                [id: "12", name: '小李', pId:"1"]
        ];break;
        case '2': contacts = [
                [id: "21", name: '张三', pId:"2"],
                [id: "22", name: '李四', pId:"2"],
                [id: "23", name: '王五', pId:"2"]
        ];break;
    }
}else{
    contacts = [[id: '1', name: '技术骨干', isParent: true],
                    [id: '2', name: '行政策划', isParent: true]
    ];
}

request.setAttribute("data", contacts);