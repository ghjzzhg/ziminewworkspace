parentId = parameters.get("id");
contacts = [];
if(parentId){
    switch (parentId){
        case '1': contacts = [
                [id: "11", name: '董事长', pId:"1"],
                [id: "12", name: '董事长助理', pId:"1"]
        ];break;
        case '2': contacts = [
                [id: "21", name: '行政主管', pId:"2"],
                [id: "22", name: '行政助理', pId:"2"],
                [id: "23", name: '人事部门', pId:"2", isParent: true]
        ];break;
        case '3': contacts = [
                [id: "31", name: '研发小组1', pId:"3", isParent: true],
                [id: "32", name: '研发小组2', pId:"3", isParent: true],
                [id: "33", name: '研发小组3', pId:"3", isParent: true]
        ];break;
        case '31': contacts = [
                [id: "311", name: '刘学晓', pId:"31"],
                [id: "312", name: '何维涛', pId:"31"],
                [id: "313", name: '潘永霆', pId:"31"]
        ];break;
    }
}else{
    contacts = [[id: '1', name: '供应商', isParent: true],
                    [id: '2', name: '大客户', isParent: true],
                    [id: '3', name: '小客户', isParent: true],
                    [id: '4', name: '临时', isParent: true]
    ];
}

request.setAttribute("data", contacts);