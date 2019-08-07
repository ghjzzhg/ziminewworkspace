package org.ofbiz.content.data;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by galaxypan on 16.8.27.
 */
public class FileManagerFactory {

    private static Map<String, FileTypeManager> cache = new HashMap<>();

    public static FileTypeManager getFileManager(String type, GenericDelegator delegator){
        FileTypeManager manager = cache.get(type);
        if(manager == null){
            synchronized (FileManagerFactory.class){
                if(manager == null){
                    switch (type){
                        case "LOCAL_FILE" : manager = new LocalFileManager(delegator);break;
                        case "ZXDOC_FILE" : manager = new AbsoluteFileManager(delegator);break;
                    }
                    cache.put(type, manager);
                }
            }
        }
        return manager;
    }
}
