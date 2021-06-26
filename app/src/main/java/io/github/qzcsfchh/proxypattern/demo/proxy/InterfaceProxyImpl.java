package io.github.qzcsfchh.proxypattern.demo.proxy;

import io.github.qzcsfchh.proxypattern.demo.Logger;

/**
 * <p></p>
 *
 * @author huanghao6
 * @version V1.0
 * @since 2021/6/26 21:57
 */
public class InterfaceProxyImpl implements InterfaceProxy{

    private final Logger mLogger;

    public InterfaceProxyImpl(Logger logger) {
        mLogger = logger;
    }

    @Override
    public void sayHi() {
        mLogger.print("I'm InterfaceProxyImpl.");
    }

    @Override
    public String doWork(int var) {
        return "InterfaceProxyImpl";
    }
}
