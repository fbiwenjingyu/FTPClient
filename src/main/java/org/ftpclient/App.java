package org.ftpclient;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException {
        System.out.println( "Hello World!" );
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> clazz = classLoader.loadClass("java.lang.String");
        Constructor<?> constructor = clazz.getConstructor(new Class[]{String.class});
        Object instance = null;
        try {
            instance = constructor.newInstance("hello world");
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        System.out.println(instance);

    }


}
