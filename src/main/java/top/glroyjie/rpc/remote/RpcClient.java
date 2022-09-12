package top.glroyjie.rpc.remote;

import top.glroyjie.rpc.config.ClusterInvokeConfig;
import top.glroyjie.rpc.invoke.Invocation;
import top.glroyjie.rpc.invoke.RpcResult;

/**
 * @author jie-r
 * @since 2022/9/4
 */
public interface RpcClient {

    void init();

    void start();

    /**
     * register interface
     * @param serviceName
     * @param interfaceClass
     * @return interface proxy object
     */
    <T> T generateInterfaceRef(String serviceName, Class<T> interfaceClass, ClusterInvokeConfig invokeConfig);


    void rmServerConfig(String serviceName, String interfaceName, String addr);


    RpcResult invoke(Invocation invocation);
}
