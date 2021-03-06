package com.letv.plugin.pluginloader.loadapk.utils;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RefInvoke {
	public static  Object invokeStaticMethod(String class_name, String method_name, Class[] pareTyple, Object[] pareVaules){
		
		try {
			Class obj_class = Class.forName(class_name);
			Method method = obj_class.getMethod(method_name,pareTyple);
			return method.invoke(null, pareVaules);
		} catch (SecurityException e) {
			e.printStackTrace();
		}  catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static  Object invokeMethod(String class_name, String method_name, Object obj ,Class[] pareTyple, Object[] pareVaules){
		
		try {
			Class obj_class = Class.forName(class_name);
			Method method = obj_class.getDeclaredMethod(method_name,pareTyple);
			return method.invoke(obj, pareVaules);
		} catch (SecurityException e) {
			Log.i("clf", "invokeMethod..SecurityException="+Log.getStackTraceString(e));
			e.printStackTrace();
		}  catch (IllegalArgumentException e) {
			Log.i("clf", "invokeMethod..IllegalArgumentException="+Log.getStackTraceString(e));
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Log.i("clf", "invokeMethod..IllegalAccessException="+Log.getStackTraceString(e));
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			Log.i("clf", "invokeMethod..NoSuchMethodException="+Log.getStackTraceString(e));
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			Log.i("clf", "invokeMethod..InvocationTargetException="+Log.getStackTraceString(e));
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			Log.i("clf", "invokeMethod..ClassNotFoundException="+Log.getStackTraceString(e));
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static Object getFieldOjbect(String class_name,Object obj, String filedName){
		try {
			Class obj_class = Class.forName(class_name);
			Field field = obj_class.getDeclaredField(filedName);
			field.setAccessible(true);
			return field.get(obj);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static Object getStaticFieldOjbect(String class_name, String filedName){
		
		try {
			Class obj_class = Class.forName(class_name);
			Field field = obj_class.getDeclaredField(filedName);
			field.setAccessible(true);
			return field.get(null);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static boolean setFieldOjbect(String classname, String filedName, Object obj, Object filedVaule){
		try {
			Class obj_class = Class.forName(classname);
			Field field = obj_class.getDeclaredField(filedName);
			field.setAccessible(true);
			field.set(obj, filedVaule);
			return true;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void setStaticOjbect(String class_name, String filedName, Object filedVaule){
		try {
			Class obj_class = Class.forName(class_name);
			Field field = obj_class.getDeclaredField(filedName);
			field.setAccessible(true);
			field.set(null, filedVaule);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}		
	}

}
