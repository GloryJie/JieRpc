package top.glroyjie.rpc.invoke.client;

import top.gloryjie.remote.connection.Connection;
import top.gloryjie.remote.endpoint.RemoteClient;
import top.glroyjie.rpc.config.ClusterInvokeConfig;
import top.glroyjie.rpc.invoke.Invocation;
import top.glroyjie.rpc.invoke.Invoker;
import top.glroyjie.rpc.invoke.RpcResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jie-r
 * @since 2022/9/4
 */
public class ClusterInvoker implements Invoker {

    private final String serviceName;

    private Class<?> interfaceClass;

    private ClusterInvokeConfig invokeConfig;

    private List<Connection> connectionList;

    private RemoteClient remoteClient;

    public ClusterInvoker(String serviceName, Class<?> interfaceClass, ClusterInvokeConfig invokeConfig, RemoteClient remoteClient) {
        this.serviceName = serviceName;
        this.interfaceClass = interfaceClass;
        this.invokeConfig = invokeConfig;
        this.remoteClient = remoteClient;

        connectionList = new ArrayList<>();
    }

    @Override
    public Class<?> getInterface() {
        return interfaceClass;
    }

    @Override
    public String getInterfaceName() {
        return interfaceClass.getName();
    }

    @Override
    public RpcResult invoke(Invocation invocation) {


        return null;
    }
}
