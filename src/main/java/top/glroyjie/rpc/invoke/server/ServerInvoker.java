package top.glroyjie.rpc.invoke.server;

import top.glroyjie.rpc.exception.RpcException;
import top.glroyjie.rpc.invoke.Invocation;
import top.glroyjie.rpc.invoke.Invoker;
import top.glroyjie.rpc.invoke.RpcResult;
import top.glroyjie.rpc.util.RpcHelperUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * desc one proxy interface class
 *
 * @author jie-r
 * @since 2022/9/4
 */
public class ServerInvoker<T> implements Invoker {

    private final Class<T> interfaceClass;

    private final T interfaceImplRef;

    /**
     * key: methodName(paramTypeName,paramTypeName)
     * value: method
     */
    private final Map<String, Method> interfaceMethodMap = new HashMap<>();


    public ServerInvoker(Class<T> interfaceClass, T interfaceImplRef) {
        this.interfaceClass = interfaceClass;
        this.interfaceImplRef = interfaceImplRef;

        // init interfaceMethodMap
        Method[] methods = interfaceClass.getMethods();
        for (Method method : methods) {
            method.setAccessible(true);
            String methodKey = RpcHelperUtil.getMethodKey(method);
            Method prev = interfaceMethodMap.put(methodKey, method);
            if (prev != null) {
                throw new RpcException("duplicate method key:" + methodKey);
            }
        }
    }


    public Class<?> getInterface() {
        return interfaceClass;
    }

    public String getInterfaceName() {
        return interfaceClass.getName();
    }

    @Override
    public RpcResult invoke(Invocation invocation) {
        RpcResult rpcResult = new RpcResult();

        String targetMethodKey = invocation.getMethodKey();
        Method method = interfaceMethodMap.get(targetMethodKey);
        if (method == null) {
            rpcResult.setException(new RpcException("server can't find method=" + targetMethodKey));
        } else {
            try {
                Object result = method.invoke(interfaceImplRef, invocation.getArguments());
                rpcResult.setResult(result);
            } catch (InvocationTargetException e) {
                rpcResult.setException(e.getTargetException());
            } catch (Throwable e) {
                rpcResult.setException(e);
            }
        }

        return rpcResult;
    }

}
