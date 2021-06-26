package io.github.qzcsfchh.proxypattern.demo.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

import io.github.qzcsfchh.proxypattern.demo.Logger;
import io.github.qzcsfchh.proxypattern.demo.proxy.InterfaceProxy;

/**
 * <p>JDK仅支持Interface代理</p>
 *
 * @author huanghao6
 * @version V1.0
 * @since 2021/6/26 21:39
 */
public class JdkProxy implements InvocationHandler {

    private final InterfaceProxy mProxy;
    private final Logger mLogger;

    public JdkProxy(Logger logger, InterfaceProxy proxy) {
        mLogger = logger;
        mProxy = proxy;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (method.getName()) {
            case "sayHi":
                mLogger.print("hello from JDK Proxy.");
                return null;
            case "doWork":
                mLogger.print("The pass-in param is: " + Arrays.toString(args));
                return "I'm JdkProxy.";
        }

        return method.invoke(mProxy/*最关键这里不能传proxy，否则会陷入死循环导致OOM*/, args);
    }
}
