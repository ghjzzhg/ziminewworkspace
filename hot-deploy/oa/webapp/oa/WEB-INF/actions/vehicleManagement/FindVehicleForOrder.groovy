List occupyTimeForThrough = new ArrayList();
List vehicle = new ArrayList();
List occupyTime = new ArrayList();
for (int i=6;i<8;i++){
    vehicleForOccupyeTimeForThroughMap = [:];
    vehicleForOccupyeTimeForThroughMap.put("vehicleId",i+"a");
    vehicleForOccupyeTimeForThroughMap.put("startDate","13")
    vehicleForOccupyeTimeForThroughMap.put("endDate","15")
    occupyTimeForThrough.add(vehicleForOccupyeTimeForThroughMap);
}
for (int i=0;i<4;i++){
    vehicleForOccupyeTimeMap = [:];
    vehicleForOccupyeTimeMap.put("vehicleId",i+"a");
    vehicleForOccupyeTimeMap.put("startDate","28")
    vehicleForOccupyeTimeMap.put("endDate","30")
    occupyTime.add(vehicleForOccupyeTimeMap);
}
for (int i=0;i<8;i++){
    vehicleMap = [:];
    if(i==0){
        vehicleMap.put("vehicleName","奥迪Q8")
    }
    if(i==1){
        vehicleMap.put("vehicleName","奔驰c350Li")
    }
    if(i==2){
        vehicleMap.put("vehicleName","玛莎拉蒂")
    }
    if(i==3){
        vehicleMap.put("vehicleName","奇瑞qq")
    }
    if(i==4){
        vehicleMap.put("vehicleName","保时捷SUV")
    }
    if(i==5){
        vehicleMap.put("vehicleName","宝马X6")
    }
    if(i==6){
        vehicleMap.put("vehicleName","奥拓")
    }
    if(i==7){
        vehicleMap.put("vehicleName","马自达C7")
    }
    vehicleMap.put("vehicleId",i+"a");
    vehicle.add(vehicleMap);
}
context.vehicle = vehicle;
context.occupyTimeForThrough = occupyTimeForThrough;
context.occupyTime = occupyTime;
