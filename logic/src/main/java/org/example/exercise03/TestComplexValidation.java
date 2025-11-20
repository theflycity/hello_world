package org.example.exercise03;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import java.util.ArrayList;

public class TestComplexValidation {
    public static void main(String[] args) {
        // 创建测试数据
        JSONObject testData = new JSONObject();
        testData.put("name", "学校A");
        
        JSONArray classes = new JSONArray();
        
        JSONObject class1 = new JSONObject();
        class1.put("className", "ClassA");
        class1.put("grade", "一年级");
        
        JSONArray students1 = new JSONArray();
        JSONObject student1 = new JSONObject();
        student1.put("name", "张三");
        student1.put("age", "10");
        students1.add(student1);
        
        JSONObject student2 = new JSONObject();
        student2.put("name", ""); // 空名字
        student2.put("age", "11");
        students1.add(student2);
        
        class1.put("students", students1);
        
        JSONObject class2 = new JSONObject();
        class2.put("className", "ClassB");
        class2.put("grade", "");
        
        JSONArray students2 = new JSONArray();
        JSONObject student3 = new JSONObject();
        student3.put("name", "李四");
        student3.put("age", ""); // 空年龄
        students2.add(student3);
        class2.put("students", students2);
        
        classes.add(class1);
        classes.add(class2);
        testData.put("classes", classes);
        
        // 测试新的校验方法
        ArrayList<String> validationPaths = new ArrayList<>();
        validationPaths.add("name");
        validationPaths.add("classes[].className");
        validationPaths.add("classes[].grade");
        validationPaths.add("classes[].students[].name");
        validationPaths.add("classes[].students[].age");
        
        System.out.println("测试数据: " + testData.toJSONString());
        System.out.println("校验结果: " + ValidateUtils.validateComplexPaths(testData, validationPaths));
        
        // 测试带条件的路径
        ArrayList<String> conditionalPaths = new ArrayList<>();
        conditionalPaths.add("classes[className=ClassA].students[].name");
        System.out.println("条件校验结果: " + ValidateUtils.validateComplexPaths(testData, conditionalPaths));
    }
}