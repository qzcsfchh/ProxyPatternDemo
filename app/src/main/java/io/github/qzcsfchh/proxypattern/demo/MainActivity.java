package io.github.qzcsfchh.proxypattern.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;

import com.android.dx.stock.ProxyBuilder;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Random;

import io.github.qzcsfchh.proxypattern.demo.cglib.CGLibProxy;
import io.github.qzcsfchh.proxypattern.demo.databinding.ActivityMainBinding;
import io.github.qzcsfchh.proxypattern.demo.dexmaker.DexMakerProxy;
import io.github.qzcsfchh.proxypattern.demo.jdk.JdkProxy;
import io.github.qzcsfchh.proxypattern.demo.proxy.InterfaceProxy;
import io.github.qzcsfchh.proxypattern.demo.proxy.InterfaceProxyImpl;
import leo.android.cglib.proxy.Enhancer;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;

    private final Logger mLogger = new Logger() {
        @Override
        public void print(String log) {
            appendLine(log);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.tvConsole.setMovementMethod(ScrollingMovementMethod.getInstance());

        mBinding.btnJdk.setOnClickListener(v -> testJdkProxy());
        mBinding.btnCglib.setOnClickListener(v -> testCGLibProxy());
        mBinding.btnDexmaker.setOnClickListener(v -> testDexMakerProxy());
    }

    private void testDexMakerProxy() {
        try {
            InterfaceProxy proxy = ProxyBuilder.forClass(InterfaceProxyImpl.class)
                    .constructorArgTypes(Logger.class)
                    .constructorArgValues(mLogger)
                    .dexCache(getDir("dx",Context.MODE_PRIVATE))
                    .handler(new DexMakerProxy(mLogger,new InterfaceProxyImpl(mLogger)))
                    .build();
            proxy.sayHi();
            proxy.doWork(new Random().nextInt());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void testCGLibProxy() {
        Enhancer enhancer = new Enhancer(this);
        enhancer.setSuperclass(InterfaceProxyImpl.class);
        enhancer.setInterceptor(new CGLibProxy(mLogger));

        InterfaceProxyImpl proxy = (InterfaceProxyImpl) enhancer.create();
        proxy.sayHi();
        proxy.doWork(new Random().nextInt());
    }


    private void testJdkProxy() {
        InterfaceProxy proxy = null;
        // 简化方法
//        proxy = (InterfaceProxy) Proxy.newProxyInstance(getClassLoader(),
//                new Class[]{InterfaceProxy.class},
//                new JdkProxy(mLogger, new InterfaceProxyImpl(mLogger)));

        // 繁琐方法
        try {
            // 1、生成$Proxy0的class文件
            System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
            // 2、获取动态代理类
            Class<?> proxyClazz = Proxy.getProxyClass(getClassLoader(),InterfaceProxy.class);
            // 3、获得代理类的构造函数，并传入参数类型InvocationHandler.class
            Constructor<InterfaceProxy> constructor = (Constructor<InterfaceProxy>) proxyClazz.getConstructor(InvocationHandler.class);
            // 4、通过构造函数来创建动态代理对象，将自定义的InvocationHandler实例传入
            proxy = (InterfaceProxy) constructor.newInstance(new JdkProxy(mLogger,new InterfaceProxyImpl(mLogger)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        proxy.sayHi();
        proxy.doWork(new Random().nextInt());
    }


    private void appendLine(CharSequence line) {
        if (!TextUtils.isEmpty(line)) {
            mBinding.tvConsole.append(line + "\n");
            int textHeight = (int) (mBinding.tvConsole.getLineCount() * (mBinding.tvConsole.getLineHeight() + mBinding.tvConsole.getLineSpacingExtra()) + 0.5f);
            if (textHeight > mBinding.tvConsole.getHeight()) {
                mBinding.tvConsole.scrollTo(0, textHeight - mBinding.tvConsole.getHeight());
            }
        }
    }


}