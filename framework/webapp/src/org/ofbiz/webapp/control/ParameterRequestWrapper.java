package org.ofbiz.webapp.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lxx on 2016/3/31.
 */
public class ParameterRequestWrapper extends HttpServletRequestWrapper {
    private Map<String, String[]> params;

    public ParameterRequestWrapper(HttpServletRequest request,
                                   Map<String, String[]> newParams) {
        super(request);

       this.params = newParams;
    }

    @Override
    public String getParameter(String name) {
        String result = "";

        Object v = params.get(name);
        if (v == null) {
            result = null;
        } else if (v instanceof String[]) {
            String[] strArr = (String[]) v;
            if (strArr.length > 0) {
                result =  strArr[0].trim();
            } else {
                result = null;
            }
        } else if (v instanceof String) {
            result = ((String) v).trim();
        } else {
            result =  v.toString().trim();
        }

        return result;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String,String[]> map = params;
        Map<String,String[]> maps = new HashMap<String,String[]>();
        for (Map.Entry<String,String[]> entry : map.entrySet()) {
            String key = entry.getKey();
            String[] values= entry.getValue();
            for(int i=0; i<values.length;i++){
                String value= values[i];
                values[i]=value.trim();
            }
            maps.put(key,values);
        }
        return maps;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] result = null;
        Object v = params.get(name);
        if (v == null) {
            result =  null;
        } else if (v instanceof String[]) {
            String[] strArr = (String[]) v;
            for(int i=0; i<strArr.length;i++){
                String value= strArr[i];
                strArr[i]=value.trim();
            }
            result = strArr;
        } else if (v instanceof String) {
            result =  new String[] {((String) v) .trim()};
        } else {
            result =  new String[] { v.toString().trim() };
        }

        return result;
    }

}
