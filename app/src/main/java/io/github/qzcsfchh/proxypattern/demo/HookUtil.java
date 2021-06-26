package io.github.qzcsfchh.proxypattern.demo;

import android.content.Context;

import com.android.dx.stock.ProxyBuilder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

import io.github.qzcsfchh.proxypattern.demo.cglib.CGLibProxy;
import io.github.qzcsfchh.proxypattern.demo.proxy.InterfaceProxyImpl;
import leo.android.cglib.proxy.Enhancer;
import leo.android.cglib.proxy.MethodInterceptor;

/**
 * 基于动态代理封装的hook工具
 */
public class HookUtil {
    private static final String TAG = "HookUtil";

    public static Field getField(Class clz, String fieldName) {
        try {
            return clz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getFieldValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean setFieldValue(Object obj, String fieldName, Object fieldValue){
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj,fieldValue);
            return true;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }


    public Method getMethod(Object obj, String methodName, Class<?>... parameterTypes){
        Method declaredMethod = null;
        try {
            declaredMethod = obj.getClass().getDeclaredMethod(methodName,parameterTypes);
            declaredMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return declaredMethod;
    }


    /**
     * JDK动态代理：限制是被代理对象必须实现一个接口或多个接口
     *
     * @param obj    被代理的对象，必须实现一个接口
     * @param action 行为监控
     * @return 产生的代理
     */
    public static Object proxyInterface(Object obj, InterceptAction action){
        return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), new InterceptHandler(obj,action));
    }


    /**
     * CGLib动态代理：限制是不能代理final的class、field、method.
     *
     * @param context     the context
     * @param objClass    the obj class
     * @param interceptor the interceptor
     * @return the object
     */
    public static Object proxyClass(Context context, Class<?> objClass, MethodInterceptor interceptor){
        Enhancer enhancer = new Enhancer(context);
        enhancer.setSuperclass(objClass);
        enhancer.setInterceptor(interceptor);
        return enhancer.create();
    }

    /**
     * DexMaker动态代理：限制是不能代理final的class、field、method
     *
     * @param context 传入上下文用于获取dex目录
     * @param obj     被代理的对象
     * @param action  行为监控
     * @return 产生的代理
     * @throws IOException the io exception
     */
    public static Object proxyAny(Context context, Object obj, InterceptAction action) throws IOException {
        return ProxyBuilder.forClass(obj.getClass()).dexCache(context.getDir("dx", Context.MODE_PRIVATE))
                .handler(new InterceptHandler(obj, action))
                .build();
    }

    public static Object proxyAny(Context context,Object obj, InvocationHandler action) throws IOException {
        return ProxyBuilder.forClass(obj.getClass()).dexCache(context.getDir("dx",Context.MODE_PRIVATE))
                .handler(action)
                .build();
    }


    /**
     * 是否是可代理的对象
     *
     * @param clz the clz
     * @return the boolean
     */
    public static boolean isClassProxable(Class<?> clz){
        return !Modifier.isFinal(clz.getModifiers()) && !ProxyBuilder.isProxyClass(clz);
    }

    private static final class InterceptHandler implements InvocationHandler {
        private final Object hooked;
        private final InterceptAction interceptAction;

        public InterceptHandler(Object hooked, InterceptAction interceptAction) {
            this.hooked = hooked;
            this.interceptAction = interceptAction;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 增加我们自己的操作
            method.setAccessible(true);
            if (interceptAction != null) {
                interceptAction.onIntercept(method, args);
            }
            // 为了不影响正常的执行结果，返回原始对象的执行结果
            return method.invoke(hooked,args);
        }

    }


    public interface InterceptAction {

        /**
         *
         * @param method
         * @param args
         * @return true if we have done something, or false when nothing happened
         */
        boolean onIntercept(Method method, Object[] args) throws Throwable;
    }
}
