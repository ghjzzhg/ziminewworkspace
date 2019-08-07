import javolution.util.FastList
import javolution.util.FastMap

liaisonCheckList = FastList.newInstance();
liaisonCheckMap = FastMap.newInstance();
liaisonCheckMap.put("q","新项目进展");
liaisonCheckMap.put("w","项目跟进联络单");
liaisonCheckMap.put("e","王杰");
liaisonCheckMap.put("r","");
liaisonCheckMap.put("t","开发部");
liaisonCheckMap.put("y","宋涛");
liaisonCheckMap.put("u","审核中");
liaisonCheckMap.put("i","2015-6-6 10:43:20");
liaisonCheckList.add(liaisonCheckMap);

liaisonCheckMap = FastMap.newInstance();
liaisonCheckMap.put("q","新器材更换");
liaisonCheckMap.put("w","更换进度联络");
liaisonCheckMap.put("e","宋佳乐");
liaisonCheckMap.put("r","");
liaisonCheckMap.put("t","采购部");
liaisonCheckMap.put("y","季修愧");
liaisonCheckMap.put("u","待审核");
liaisonCheckMap.put("i","2015-6-4 10:43:20");
liaisonCheckList.add(liaisonCheckMap);

context.liaisonCheckList = liaisonCheckList;


