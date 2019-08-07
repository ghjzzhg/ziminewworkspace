package org.ofbiz.entity.util;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by galaxypan on 16.7.2.
 */
public class UtilPagination {

    public static PaginationResult queryPage(EntityQuery query, Map<String, Object> pageParameterMap) {
        EntityListIterator eli = null;
        PaginationResult result = null;
        boolean beganTrans = false;
        try {
            beganTrans = TransactionUtil.begin();
            int viewIndex = 0;
            try {
                viewIndex = Integer.parseInt((String) pageParameterMap.get("VIEW_INDEX"));
            } catch (Exception e) {
                viewIndex = 0;
            }

            int viewSize = 10;
            try {
                viewSize = Integer.parseInt((String) pageParameterMap.get("VIEW_SIZE"));
            } catch (Exception e) {
                viewSize = 10;
            }

            String sortField = (String) pageParameterMap.get("sortField");

            // 计算当前显示页的最小、最大索引(可能会超出实际条数)
            int lowIndex = viewIndex * viewSize + 1;
            int highIndex = (viewIndex + 1) * viewSize;
            //--------------------------------------------------

            eli = query
                    .orderBy(sortField)
                    .cursorScrollInsensitive()
                    .fetchSize(highIndex)
                    .queryIterator();
            // 获取结果片段
            List<GenericValue> resultList = eli.getPartialList(lowIndex, viewSize);
            // 获取实际总条数
            int totalCount = eli.getResultsSizeAfterPartialList();
            if (highIndex > totalCount) {
                highIndex = totalCount;
            }

            result = new PaginationResult();
            result.setViewIndex(viewIndex);
            result.setViewSize(viewSize);
            result.setSortField(sortField);
            result.setLowIndex(lowIndex);
            result.setHighIndex(highIndex);
            result.setTotalCount(totalCount);
            result.setResultList(resultList);

        } catch (Throwable t) {
            try {
                TransactionUtil.rollback(beganTrans, t.getMessage(), t);
            } catch (GenericTransactionException te) {
                Debug.logError(te, "Cannot rollback transaction", UtilPagination.class.getName());
            }
        } finally {
            if (eli != null) {
                try {
                    eli.close();
                } catch (GenericEntityException e) {
                    e.printStackTrace();
                }
            }
            if (beganTrans) {//如果是本查询开启的事务在此需关闭
                try {
                    TransactionUtil.commit(beganTrans);
                } catch (GenericTransactionException e) {
                    throw new RuntimeException("提交事务失败");
                }
            }
        }

        return result;
    }

    public static PaginationResultDatatables queryPageDatatables(EntityQuery query, Map<String, Object> pageParameterMap) {
        EntityListIterator eli = null;
        PaginationResultDatatables result = null;
        boolean beganTrans = false;
        try {
            beganTrans = TransactionUtil.begin();

            int lowIndex = UtilValidate.isEmpty(pageParameterMap.get("start")) ? 0 : Integer.parseInt((String) pageParameterMap.get("start")) + 1;
            int viewSize = UtilValidate.isEmpty(pageParameterMap.get("length")) ? 10 : Integer.parseInt((String) pageParameterMap.get("length"));

            Set<String> parameterNames = pageParameterMap.keySet();
            Map<Integer, Map<String, String>> orderData = new HashMap<>();
            Map<Integer, String> columnData = new HashMap<>();
            Map<Integer, String> columnNames = new HashMap<>();
            Pattern columnDataPattern = Pattern.compile("(columns\\[)(\\d+)(\\]\\[)(data)(\\])");
            Pattern columnNamePattern = Pattern.compile("(columns\\[)(\\d+)(\\]\\[)(name)(\\])");
            Pattern orderColumnPattern = Pattern.compile("(order\\[)(\\d+)(\\]\\[)([a-z]+)(\\])");
            for (String name: parameterNames) {
                Matcher columnM = columnDataPattern.matcher(name);
                Matcher columnN = columnNamePattern.matcher(name);
                if(columnM.find()){
                    int index = Integer.parseInt(columnM.group(2));
                    columnData.put(index, (String) pageParameterMap.get(name));
                }else if(columnN.find()){
                    int index = Integer.parseInt(columnN.group(2));
                    columnNames.put(index, (String) pageParameterMap.get(name));
                }else{
                    Matcher matcher = orderColumnPattern.matcher(name);
                    if(matcher.find()){
                        int index = Integer.parseInt(matcher.group(2));
                        String type = matcher.group(4);
                        Map<String, String> data = orderData.get(index);
                        if(data == null){
                            data = new HashMap<>();
                            orderData.put(index, data);
                        }
                        data.put(type, (String) pageParameterMap.get(name));
                    }
                }
            }

            List<String> orderString = new ArrayList<>();

            List<Integer> orderIndex = new ArrayList<>(orderData.keySet());
            Collections.sort(orderIndex);
            for (Integer index : orderIndex) {
                Map<String, String> data = orderData.get(index);
                String dir = data.get("dir");
                String column = data.get("column");
                String columnName = columnData.get(Integer.parseInt(column));
                if (UtilValidate.isEmpty(columnName)) {
                    columnName = columnNames.get(Integer.parseInt(column));
                }
                if (UtilValidate.isEmpty(columnName)) {
                    throw new RuntimeException("datatable字段定义中排序字段[" + index + "]未指定data或name属性");
                }
                String orderStr = "asc".equals(dir) ? columnName : ("-" + columnName);
                orderString.add(orderStr);
            }
            if(UtilValidate.isNotEmpty(orderString)){
                query = query.orderBy(orderString);
            }
            eli = query.queryIterator();
            int totalCount = eli.getResultsSizeAfterPartialList();


            result = new PaginationResultDatatables();
            List<GenericValue> data = eli.getPartialList(lowIndex, viewSize);
            result.setData(data);
            result.setDraw(UtilValidate.isEmpty(pageParameterMap.get("draw")) ? 1 : Integer.parseInt((String) pageParameterMap.get("draw")));
            result.setRecordsTotal(totalCount);
            result.setRecordsFiltered(totalCount);

        } catch (Throwable t){
            Debug.logError(t, "查询出错", UtilPagination.class.getName());
            try {
                TransactionUtil.rollback(beganTrans, t.getMessage(), t);
            } catch (GenericTransactionException te) {
                Debug.logError(te, "Cannot rollback transaction", UtilPagination.class.getName());
            }
        }finally {
            if (eli != null) {
                try {
                    eli.close();
                } catch (GenericEntityException e) {
                    e.printStackTrace();
                }
            }
            if (beganTrans) {//如果是本查询开启的事务在此需关闭
                try {
                    TransactionUtil.commit(beganTrans);
                } catch (GenericTransactionException e) {
                    throw new RuntimeException("提交事务失败");
                }
            }
        }

        return result;
    }

    public static class PaginationResult {
        private int viewIndex = 0;

        private int viewSize = 10;

        private String sortField;

        private List<GenericValue> resultList = new ArrayList<>();

        private int totalCount;

        private int lowIndex;

        private int highIndex;

        public int getViewIndex() {
            return viewIndex;
        }

        public void setViewIndex(int viewIndex) {
            this.viewIndex = viewIndex;
        }

        public int getViewSize() {
            return viewSize;
        }

        public void setViewSize(int viewSize) {
            this.viewSize = viewSize;
        }

        public String getSortField() {
            return sortField;
        }

        public void setSortField(String sortField) {
            this.sortField = sortField;
        }

        public List<GenericValue> getResultList() {
            return resultList;
        }

        public void setResultList(List<GenericValue> resultList) {
            this.resultList = resultList;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public int getLowIndex() {
            return lowIndex;
        }

        public void setLowIndex(int lowIndex) {
            this.lowIndex = lowIndex;
        }

        public int getHighIndex() {
            return highIndex;
        }

        public void setHighIndex(int highIndex) {
            this.highIndex = highIndex;
        }
    }

    public static class PaginationResultDatatables {
        private int draw = 1;

        private int recordsTotal = 10;
        private List<Map> data = new ArrayList<>();

        private int recordsFiltered;

        public int getDraw() {
            return draw;
        }

        public void setDraw(int draw) {
            this.draw = draw;
        }

        public int getRecordsTotal() {
            return recordsTotal;
        }

        public void setRecordsTotal(int recordsTotal) {
            this.recordsTotal = recordsTotal;
        }

        public List<Map> getData() {
            return data;
        }

        public void setData(List<GenericValue> param) {
            for (GenericValue item : param) {
                data.add(item);
            }
        }

        public void setDataMap(List<Map> data) {
            this.data = data;
        }

        public int getRecordsFiltered() {
            return recordsFiltered;
        }

        public void setRecordsFiltered(int recordsFiltered) {
            this.recordsFiltered = recordsFiltered;
        }
    }



}
