package org.ofbiz.content.data;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;

/**
 * Created by REXTEC-15-2 on 2016-09-09.
 */
public abstract class FileTypeAbstractImpl implements FileTypeManager{
    private GenericDelegator delegator;

    private String module = FileTypeAbstractImpl.class.getName();

    public FileTypeAbstractImpl(GenericDelegator delegator){
        this.delegator = delegator;
    }

    @Override
    public void delFile(String dataResourceId) {
        delRealityFile(dataResourceId);
        delFileHistoryList(dataResourceId);
        delFileInfo(dataResourceId);
    }

    public void delFileInfo(String dataResourceId){
        try {
            delegator.removeByAnd("TblFileOwnership", UtilMisc.toMap("fileId", dataResourceId));
            delegator.removeByAnd("DataResourceRole", UtilMisc.toMap("dataResourceId", dataResourceId));
            delegator.removeByAnd("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    public abstract void delRealityFile(String dataResourceId);
}
