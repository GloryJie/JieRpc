package top.glroyjie.rpc.remote;

import top.glroyjie.rpc.config.ClusterInvokeConfig;
import top.glroyjie.rpc.config.ServerNodeInvokeConfig;
import top.glroyjie.rpc.invoke.client.ClientNodeInvoker;
import top.glroyjie.rpc.loadbalance.ClientLoadBalance;

/**
 * @author jie-r
 * @since 2022/9/4
 */
public interface RpcClient {

    void start();

    /**
     * register server service
     * @param serviceName
     * @param invokeConfig service invoke config
     */
    void registerServerService(String serviceName, ClusterInvokeConfig invokeConfig);


    /**
     * register interface
     * @param serviceName
     * @param interfaceClass
     * @return interface proxy object
     */
    <T> T generateInterfaceRef(String serviceName, Class<T> interfaceClass);

    ClientNodeInvoker createNodeInvoker(ServerNodeInvokeConfig invokeConfig);

    ClientLoadBalance createLoadBalance(String loadBalanceKey);
}
