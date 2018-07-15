package com.example.demo.configuration;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Parse {
    public List<Map<String,Object>> parseBean(Class clazz){
        Field[] fields = clazz.getDeclaredFields();
        List<Map<String,Object>> list=new ArrayList<>();
        for (Field field:fields){
            Column annotation = field.getAnnotation(Column.class);
            if (annotation!=null){

                Map<String,Object> map=new HashMap<>();
                String column = annotation.name();
                map.put("column",column);
                String type = field.getType().getName();
                map.put("type",type);
                String name = field.getName();
                map.put("name",name);
                try {
                    PropertyDescriptor pd=new PropertyDescriptor(name,clazz);
                    Method readMethod = pd.getReadMethod();
                    try {
                        Object invoke = readMethod.invoke(clazz.newInstance());
                        map.put("value",invoke);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                } catch (IntrospectionException e) {
                    e.printStackTrace();
                }
                list.add(map);

            }
        }
        return list;

    }
}
