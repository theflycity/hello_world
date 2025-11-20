package org.example.exercise03;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Map;


public class ValidateUtils {

    /**
     * 校验非空字段
     * 支持结构体内字段校验：aaa.bbb.ccc
     * 暂不支持数组：aaa[n].bbb
     */
    public static ArrayList<String> validateParams(JSONObject req, ArrayList<String> params) {
        ArrayList<String> emptyParams = new ArrayList<>();
        for (String param : params) {
            String[] paramSet = param.split("\\.");
            if (paramSet.length == 1) {
                if (StringUtils.isEmpty(req.getString(param))) {
                    emptyParams.add(param);
                }
            } else if (paramSet.length > 1) {
                JSONObject newReq = req;
                for (int i = 0; i < paramSet.length - 1; i++) {
                    if (null == newReq) {
                        break;
                    }
                    newReq = newReq.getJSONObject(paramSet[i]);
                }
                if (null != newReq) {
                    String value = newReq.getString(paramSet[paramSet.length - 1]);
                    if (StringUtils.isEmpty(value)) {
                        emptyParams.add(param);
                    }
                } else {
                    emptyParams.add(param);
                }
            }
        }
        return emptyParams;
    }


    public static ArrayList validateAllParams(JSONObject checkedParams) {
        ArrayList<String> emptyParams = new ArrayList<String>();
        for (Map.Entry entry : checkedParams.entrySet()) {
            if (StringUtils.isEmpty((String) entry.getValue())) {
                emptyParams.add((String) entry.getKey());
            }
        }
        return emptyParams;
    }

    /**
     * 重载方法：支持对JSON格式内含单层数组的指定元素的空值校验
     * @param req 请求参数JSONObject
     * @param params 需要校验的参数列表
     * @param arrayParam 数组参数名
     * @param validateKey 需要校验的键名
     * @param conditionKey 条件键名（可选）
     * @param conditionValue 条件值（可选）
     * @return 空值参数列表
     */
    public static ArrayList<String> validateParams(JSONObject req, ArrayList<String> params, 
                                                   String arrayParam, String validateKey,
                                                   String conditionKey, String conditionValue) {
        // 先执行原有的校验逻辑
        ArrayList<String> emptyParams = validateParams(req, params);
        
        // 处理数组中的元素校验
        // 获取指定的数组
        com.alibaba.fastjson.JSONArray array = req.getJSONArray(arrayParam);
        if (array != null && !array.isEmpty()) {
            // 遍历数组中的每个元素
            for (int i = 0; i < array.size(); i++) {
                JSONObject item = array.getJSONObject(i);
                if (item != null) {
                    // 判断是否需要条件过滤
                    boolean conditionMet = true;
                    if (conditionKey != null && conditionValue != null) {
                        // 检查是否满足特定条件
                        conditionMet = conditionValue.equals(item.getString(conditionKey));
                    }
                    
                    // 如果满足条件（或不需要条件），则校验指定键的值是否为空
                    if (conditionMet && StringUtils.isEmpty(item.getString(validateKey))) {
                        emptyParams.add(arrayParam + "[" + i + "]." + validateKey);
                    }
                }
            }
        }
        
        return emptyParams;
    }


    /**
     * 通用校验方法：支持复杂嵌套结构的空值校验
     * 路径语法：
     * - 使用 . 分隔对象层级
     * - 使用 [] 表示数组
     * - 示例：
     *   - school.classes[].students[].name （校验所有学生的名字）
     *   - school.classes[name=ClassA].students[].name （只校验ClassA班学生的名字）
     * 
     * @param req 请求参数JSONObject
     * @param validationPaths 需要校验的路径列表
     * @return 空值参数列表
     */
    public static ArrayList<String> validateComplexPaths(JSONObject req, ArrayList<String> validationPaths) {
        ArrayList<String> emptyParams = new ArrayList<>();
        
        for (String path : validationPaths) {
            validatePath(req, path, "", emptyParams);
        }
        
        return emptyParams;
    }
    
    /**
     * 递归校验路径
     */
    private static void validatePath(Object currentObj, String remainingPath, String fullPath, 
                                  ArrayList<String> emptyParams) {
        // 如果没有剩余路径需要处理
        if (remainingPath == null || remainingPath.isEmpty()) {
            // 检查当前对象是否为空
            if (currentObj == null || 
                (currentObj instanceof String && StringUtils.isEmpty((String) currentObj))) {
                emptyParams.add(fullPath);
            }
            return;
        }
        
        // 解析路径的第一部分
        int dotIndex = remainingPath.indexOf('.');
        String currentSegment = (dotIndex == -1) ? remainingPath : remainingPath.substring(0, dotIndex);
        String nextPath = (dotIndex == -1) ? "" : remainingPath.substring(dotIndex + 1);
        
        // 处理数组情况
        if (currentSegment.endsWith("[]")) {
            String arrayName = currentSegment.substring(0, currentSegment.length() - 2);
            
            if (currentObj instanceof JSONObject) {
                com.alibaba.fastjson.JSONArray array = ((JSONObject) currentObj).getJSONArray(arrayName);
                
                if (array == null || array.isEmpty()) {
                    // 整个数组为空，记录路径
                    emptyParams.add(fullPath.isEmpty() ? arrayName + "[]" : fullPath + "." + arrayName + "[]");
                } else {
                    // 遍历数组元素
                    for (int i = 0; i < array.size(); i++) {
                        String newFullPath = fullPath.isEmpty() ? arrayName + "[" + i + "]" : fullPath + "." + arrayName + "[" + i + "]";
                        validatePath(array.get(i), nextPath, newFullPath, emptyParams);
                    }
                }
            } else {
                // 当前期望是一个数组但不是，记录错误
                String path = fullPath.isEmpty() ? arrayName : fullPath + "." + arrayName;
                emptyParams.add(path);
            }
        }
        // 处理带条件的数组情况
        else if (currentSegment.contains("[") && currentSegment.contains("]")) {
            int bracketStart = currentSegment.indexOf('[');
            int bracketEnd = currentSegment.indexOf(']');
            String arrayName = currentSegment.substring(0, bracketStart);
            String condition = currentSegment.substring(bracketStart + 1, bracketEnd);
            
            if (currentObj instanceof JSONObject) {
                com.alibaba.fastjson.JSONArray array = ((JSONObject) currentObj).getJSONArray(arrayName);
                
                if (array == null || array.isEmpty()) {
                    emptyParams.add(fullPath.isEmpty() ? arrayName + "[]" : fullPath + "." + arrayName + "[]");
                } else {
                    // 解析条件
                    String[] parts = condition.split("=");
                    if (parts.length == 2) {
                        String conditionKey = parts[0];
                        String conditionValue = parts[1];
                        
                        // 查找符合条件的元素
                        for (int i = 0; i < array.size(); i++) {
                            Object item = array.get(i);
                            if (item instanceof JSONObject) {
                                JSONObject itemObj = (JSONObject) item;
                                String value = itemObj.getString(conditionKey);
                                if (conditionValue.equals(value)) {
                                    String newFullPath = fullPath.isEmpty() ? arrayName + "[" + i + "]" : fullPath + "." + arrayName + "[" + i + "]";
                                    validatePath(itemObj, nextPath, newFullPath, emptyParams);
                                }
                            }
                        }
                    }
                }
            } else {
                String path = fullPath.isEmpty() ? arrayName : fullPath + "." + arrayName;
                emptyParams.add(path);
            }
        }
        // 处理带条件的普通对象情况 (使用圆括号)
        else if (currentSegment.contains("(") && currentSegment.contains(")")) {
            int parenStart = currentSegment.indexOf('(');
            int parenEnd = currentSegment.indexOf(')');
            String objName = currentSegment.substring(0, parenStart);
            String condition = currentSegment.substring(parenStart + 1, parenEnd);
            
            if (currentObj instanceof JSONObject) {
                // 先获取对象，再检查类型
                Object obj = ((JSONObject) currentObj).get(objName);
                
                if (obj != null && obj instanceof JSONObject) {
                    // 解析条件
                    String[] parts = condition.split("=");
                    if (parts.length == 2) {
                        String conditionKey = parts[0];
                        String conditionValue = parts[1];
                        
                        // 检查对象是否满足条件
                        String value = ((JSONObject) obj).getString(conditionKey);
                        if (conditionValue.equals(value)) {
                            String newFullPath = fullPath.isEmpty() ? objName : fullPath + "." + objName;
                            validatePath(obj, nextPath, newFullPath, emptyParams);
                        }
                    }
                } else if (obj == null) {
                    // 对象不存在，记录路径
                    String path = fullPath.isEmpty() ? objName : fullPath + "." + objName;
                    emptyParams.add(path);
                }
            } else {
                String path = fullPath.isEmpty() ? objName : fullPath + "." + objName;
                emptyParams.add(path);
            }
        }
        // 处理普通对象属性
        else {
            if (currentObj instanceof JSONObject) {
                Object value = ((JSONObject) currentObj).get(currentSegment);
                String newFullPath = fullPath.isEmpty() ? currentSegment : fullPath + "." + currentSegment;
                validatePath(value, nextPath, newFullPath, emptyParams);
            } else {
                // 当前期望是一个对象但不是
                String path = fullPath.isEmpty() ? currentSegment : fullPath + "." + currentSegment;
                emptyParams.add(path);
            }
        }
    }

//    public static void main(String[] args) {
//        JSONObject json = new JSONObject();
//        json.put("a", "aaa");
//        JSONObject b = new JSONObject();
//        b.put("bb", "bbb");
//        json.put("b", b);
//        ArrayList<String> params = new ArrayList<>();
//        params.add("a");
//        params.add("c");
//        params.add("d");
//        params.add("b.bb");
//        params.add("b.c");
//        params.add("b.bbb.d");
//        System.out.println(validateParams(json, params));
//    }

}
