package top.glroyjie.rpc.proxy;

import top.glroyjie.rpc.exception.RpcException;
import top.glroyjie.rpc.invoke.Invocation;
import top.glroyjie.rpc.invoke.RpcResult;
import top.glroyjie.rpc.invoke.client.ClusterInvoker;
import top.glroyjie.rpc.util.RpcHelperUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author jie-r
 * @since 2022/9/3
 */
public class InvokeProxyHandler implements InvocationHandler {

    private final String serviceName;

    private ClusterInvoker clusterInvoker;

    public InvokeProxyHandler(String serviceName, ClusterInvoker clusterInvoker) {
        this.serviceName = serviceName;
        this.clusterInvoker = clusterInvoker;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(proxy, args);
        }
        if (Objects.equals("toString", methodName) && parameterTypes.length == 0) {
            return proxy.toString();
        }
        if (Objects.equals("hashCode", methodName) && parameterTypes.length == 0) {
            return proxy.hashCode();
        }
        if (Objects.equals("equals", methodName) && parameterTypes.length == 1) {
            return proxy.equals(args[0]);
        }

        // create invocation to desc this invoke
        Invocation invocation = new Invocation();
        invocation.setServiceName(serviceName);
        invocation.setArguments(args);
        invocation.setMethodName(methodName);
        invocation.setInterfaceName(method.getDeclaringClass().getName());
        invocation.setMethodKey(RpcHelperUtil.getMethodKey(method));

        RpcResult rpcResult = clusterInvoker.invoke(invocation);
        if (rpcResult.getException() != null) {
            if (rpcResult.getException() instanceof RpcException){
                throw rpcResult.getException();
            }
            throw new RpcException(rpcResult.getException());
        }
        return rpcResult.getResult();
    }
}
