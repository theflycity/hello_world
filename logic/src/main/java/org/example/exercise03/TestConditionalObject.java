package org.example.exercise03;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import java.util.ArrayList;

public class TestConditionalObject {
    public static void main(String[] args) {
        // 创建测试数据
        JSONObject testData = new JSONObject();
        testData.put("schoolName", "Test School");
        
        // 创建学校信息对象
        JSONObject schoolInfo = new JSONObject();
        schoolInfo.put("type", "primary");
        schoolInfo.put("address", "北京市朝阳区");
        schoolInfo.put("establishedYear", "2000");
        testData.put("info", schoolInfo);
        
        // 创建班级数组
        JSONArray classes = new JSONArray();
        
        JSONObject class1 = new JSONObject();
        class1.put("className", "ClassA");
        class1.put("grade", "一年级");
        
        JSONArray students1 = new JSONArray();
        JSONObject student1 = new JSONObject();
        JSONObject student2 = new JSONObject();
        student1.put("name", "张三");
        student1.put("age", "10");
        student2.put("name", "");
        student2.put("age", "10");
        students1.add(student1);
        students1.add(student2);

        class1.put("students", students1);
        classes.add(class1);
        
        testData.put("classes", classes);
        
        // 测试新的校验方法 - 带条件的普通对象
        ArrayList<String> validationPaths = new ArrayList<>();
        validationPaths.add("info(type=primary).address");  // 校验type为primary的info对象的address字段
        validationPaths.add("info(type=secondary).address");  // 校验type为secondary的info对象的address字段
        
        System.out.println("测试数据: " + testData.toJSONString());
        System.out.println("校验结果: " + ValidateUtils.validateComplexPaths(testData, validationPaths));
        
        // 测试混合路径
        ArrayList<String> mixedPaths = new ArrayList<>();
        mixedPaths.add("info(type=primary).address");
        mixedPaths.add("classes[].className");
        mixedPaths.add("classes(className=ClassA).students[].name");
        
        System.out.println("混合路径校验结果: " + ValidateUtils.validateComplexPaths(testData, mixedPaths));
    }
}