package top.glroyjie.rpc.util;

import top.glroyjie.rpc.invoke.Invocation;

import java.lang.reflect.Method;

/**
 * @author jie-r
 * @since 2022/9/4
 */
public class RpcHelperUtil {

    public static String getMethodKey(Method method){
        String name = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        StringBuilder builder = new StringBuilder(name).append("(");
        for (int i = 0; i < parameterTypes.length; i++) {
            builder.append(parameterTypes[i].getCanonicalName());
            if (i < parameterTypes.length - 1) {
                builder.append(",");
            }
        }
        builder.append(")");
        return builder.toString();
    }

    public static String getInvokeClusterKey(Invocation invocation){
        return invocation.getServiceName() + "#" + invocation.getInterfaceName();
    }
}
