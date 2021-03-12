package com.github.simplerpc.rpc.client;

import com.github.simplerpc.rpc.client.stubs.AbstractStub;
import com.github.simplerpc.rpc.client.stubs.RpcRequest;
import com.github.simplerpc.rpc.serialize.SerializeSupport;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CGLibDynamicProxy extends AbstractStub implements MethodInterceptor {

    private Class clz;

    public CGLibDynamicProxy(Class clz) {
        this.clz = clz;
    }

    private Enhancer enhancer = new Enhancer();

    /*生成代理类的常规代码
        Enhancer enhancer=new Enhancer();

        enhancer.setSuperclass(ConcreteClassNoInterface.class);

        enhancer.setCallback(new ConcreteClassInterceptor());

        ConcreteClassNoInterface ccni=(ConcreteClassNoInterface)enhancer.create();*/
    public Object getProxy() {
        //设置需要创建子类的类
        enhancer.setSuperclass(clz);
        enhancer.setCallback(this);
        //通过字节码技术动态创建子类实例
        return enhancer.create();
    }

    //    这个函数就是重点
//    定义一个拦截器。在调用目标方法时，CGLib会回调MethodInterceptor接口方法拦截，
//    来实现你自己的代理逻辑，类似于JDK中的InvocationHandler接口。
//    参数：Object为由CGLib动态生成的代理类实例，Method为上文中实体类所调用的被代理的方法引用
//    ，Object[]为参数值列表，MethodProxy为生成的代理类对方法的代理引用。
//返回：从代理实例的方法调用返回的值。
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return SerializeSupport.parse(invokeRemote(new RpcRequest(clz.getCanonicalName(), method.getName(), SerializeSupport.serialize(objects))));
    }
}
