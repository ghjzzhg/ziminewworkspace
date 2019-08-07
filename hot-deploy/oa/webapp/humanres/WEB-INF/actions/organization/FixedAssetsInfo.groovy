import javolution.util.FastList
import javolution.util.FastMap

fixedAssetsInfoForm = FastMap.newInstance();
fixedAssetsInfoForm.put("a","test");
fixedAssetsInfoForm.put("w","test");
fixedAssetsInfoForm.put("e","test");
fixedAssetsInfoForm.put("r","test");
fixedAssetsInfoForm.put("t","test");
fixedAssetsInfoForm.put("y","test");
fixedAssetsInfoForm.put("u","test");
fixedAssetsInfoForm.put("i","test");
fixedAssetsInfoForm.put("o","test");
fixedAssetsInfoForm.put("p","test");
fixedAssetsInfoForm.put("a","test");
fixedAssetsInfoForm.put("s","test");
fixedAssetsInfoForm.put("d","test");
fixedAssetsInfoForm.put("f","test");
fixedAssetsInfoForm.put("g","test");
fixedAssetsInfoForm.put("h","test");
fixedAssetsInfoForm.put("a","test");
context.fixedAssetsInfoForm = fixedAssetsInfoForm;

fixedAssetsPartsList = FastList.newInstance();
fixedAssetsPartsMap = FastMap.newInstance();
fixedAssetsPartsMap.put("q","test");
fixedAssetsPartsMap.put("w","test");
fixedAssetsPartsMap.put("e","test");
fixedAssetsPartsMap.put("r","test");
fixedAssetsPartsMap.put("t","test");
fixedAssetsPartsMap.put("y","test");
fixedAssetsPartsMap.put("u","test");
fixedAssetsPartsMap.put("i","test");
fixedAssetsPartsList.add(fixedAssetsPartsMap);
context.fixedAssetsPartsList = fixedAssetsPartsList;

useFixedAssetsList = FastList.newInstance();
for(int i=0;i<5;i++){
    useFixedAssetsMap = FastMap.newInstance();
    useFixedAssetsMap.put("q","test");
    useFixedAssetsMap.put("w","test");
    useFixedAssetsMap.put("r","test");
    useFixedAssetsMap.put("t","test");
    useFixedAssetsMap.put("y","test");
    useFixedAssetsList.add(useFixedAssetsMap);
}
context.useFixedAssetsList = useFixedAssetsList;
reconditionFixedAssetsList = FastList.newInstance()j;
for(int i=0;i<5;i++){
    reconditionFixedAssetsMap = FastMap.newInstance();
    reconditionFixedAssetsMap.put("q","test");
    reconditionFixedAssetsMap.put("w","test");
    reconditionFixedAssetsMap.put("r","test");
    reconditionFixedAssetsMap.put("t","test");
    reconditionFixedAssetsMap.put("y","test");
    reconditionFixedAssetsList.add(reconditionFixedAssetsMap);
}
context.reconditionFixedAssetsList = reconditionFixedAssetsList;