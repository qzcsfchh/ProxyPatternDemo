package io.github.qzcsfchh.proxypattern.demo.cglib;

import java.util.Arrays;

import io.github.qzcsfchh.proxypattern.demo.Logger;
import leo.android.cglib.proxy.MethodInterceptor;
import leo.android.cglib.proxy.MethodProxy;

/**
 * <p>CGLib-for-Android是第三方开发者开发的基于CGLib和DexMaker的安卓开发库></p>
 * 1. 不支持final类以及含final方法的类，这是个很大的限制；
 *
 * @author huanghao6
 * @version V1.0
 * @since 2021/6/26 22:34
 * @see <a href='https://github.com/leo-ouyang/CGLib-for-Android'>CGLib-for-Android</a>
 * @see <a href='https://github.com/cglib/cglib'>cglib</a>
 */
public class CGLibProxy implements MethodInterceptor {
    private final Logger mLogger;

    public CGLibProxy(Logger logger) {
        mLogger = logger;
    }

    @Override
    public Object intercept(Object proxy, Object[] args, MethodProxy method) throws Exception {
        switch (method.getMethodName()) {
            case "sayHi":
                mLogger.print("hello from CGLibProxy.");
                return null;
            case "doWork":
                mLogger.print("The pass-in param is: " + Arrays.toString(args));
                return "I'm CGLibProxy.";
        }

        return method.invokeSuper(proxy, args);
    }
}
