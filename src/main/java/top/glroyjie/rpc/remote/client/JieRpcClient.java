package top.glroyjie.rpc.remote.client;

import top.gloryjie.remote.connection.Connection;
import top.gloryjie.remote.endpoint.RemoteClient;
import top.gloryjie.remote.endpoint.client.NettyRemoteClient;
import top.gloryjie.remote.endpoint.client.RemoteClientConfig;
import top.gloryjie.remote.protocol.msg.RemoteMsg;
import top.gloryjie.remote.serializer.InnerSerializer;
import top.glroyjie.rpc.config.ClusterInvokeConfig;
import top.glroyjie.rpc.config.JieRpcConstant;
import top.glroyjie.rpc.config.RpcClientConfig;
import top.glroyjie.rpc.config.ServerNodeInvokeConfig;
import top.glroyjie.rpc.exception.RpcException;
import top.glroyjie.rpc.invoke.*;
import top.glroyjie.rpc.invoke.client.ClusterInvoker;
import top.glroyjie.rpc.proxy.InvokeProxyHandler;
import top.glroyjie.rpc.remote.RpcClient;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jie-r
 * @since 2022/9/4
 */
public class JieRpcClient implements RpcClient {

    private final RpcClientConfig rpcClientConfig;

    private final RemoteClient remoteClient;

    private Map<String, Object> proxyObjectMap = new ConcurrentHashMap<>();

    private Map<String, ClusterInvoker> clusterInvokerMap = new ConcurrentHashMap();

    Map<String, List<Connection>> serviceConnectionMap = new ConcurrentHashMap<>();

    Map<String, ClusterInvokeConfig> serviceInvokeConfigMap = new ConcurrentHashMap<>();


    public JieRpcClient(RpcClientConfig rpcClientConfig) {
        this.rpcClientConfig = rpcClientConfig;
        RemoteClientConfig clientConfig = new RemoteClientConfig();
        clientConfig.setConnectTimeout(rpcClientConfig.getConnectTimeout());
        clientConfig.setIoThreads(rpcClientConfig.getIoThreads());
        clientConfig.setQueueSize(rpcClientConfig.getQueueSize());
        this.remoteClient = new NettyRemoteClient(clientConfig);
    }

    @Override
    public void init() {
        remoteClient.init();
    }

    @Override
    public void start() {
        remoteClient.start();
        ;
    }

    @Override
    public <T> T generateInterfaceRef(String serviceName, Class<T> interfaceClass, ClusterInvokeConfig invokeConfig) {
        String clusterKey = serviceName + "#" + interfaceClass.getName();
        if (proxyObjectMap.get(clusterKey) != null) {
            return (T) proxyObjectMap.get(clusterKey);
        }

        synchronized (this) {
            if (proxyObjectMap.get(clusterKey) != null) {
                return (T) proxyObjectMap.get(clusterKey);
            }
            InvokeProxyHandler proxyHandler = new InvokeProxyHandler(serviceName, this);

            // connect
            List<ServerNodeInvokeConfig> nodeInvokeConfigs = invokeConfig.getNodeInvokeConfigs();
            List<Connection> connectionList = new ArrayList<>();
            for (ServerNodeInvokeConfig nodeInvokeConfig : nodeInvokeConfigs) {
                Connection connection = remoteClient.connect(nodeInvokeConfig.getServerIp() + ":" + nodeInvokeConfig.getPort(), invokeConfig.getConnectTimeout());
                connectionList.add(connection);
            }
            serviceConnectionMap.put(clusterKey, connectionList);
            serviceInvokeConfigMap.put(clusterKey, invokeConfig);

            Object proxyInstance = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{interfaceClass}, proxyHandler);
            proxyObjectMap.put(clusterKey, proxyInstance);
        }

        return (T) proxyObjectMap.get(clusterKey);
    }

    @Override
    public void rmServerConfig(String serviceName, String interfaceName, String addr) {

    }

    @Override
    public RpcResult invoke(Invocation invocation) {
        InvokeRequest invokeRequest = new InvokeRequest();
        invokeRequest.setServiceName(invocation.getServiceName());
        invokeRequest.setMethodName(invocation.getMethodName());
        invokeRequest.setInterfaceName(invocation.getInterfaceName());
        invokeRequest.setMethodKey(invocation.getMethodKey());
        if (invocation.getArguments() != null && invocation.getArguments().length > 0) {
            List<ParamPair> list = new ArrayList<>(invocation.getArguments().length);
            for (Object argument : invocation.getArguments()) {
                ParamPair pair = new ParamPair();
                pair.setValue(argument);
                pair.setType(argument.getClass().getSimpleName());
                list.add(pair);
            }
            invokeRequest.setParamList(list);
        }
        // TODO load balance
        String clusterKey = invocation.getServiceName() + "#" + invocation.getInterfaceName();
        List<Connection> connectionList = serviceConnectionMap.get(clusterKey);
        if (connectionList == null || connectionList.isEmpty()) {
            throw new RpcException("invoker not found");
        }
        Connection connection = connectionList.get(0);
        long timeoutMills = serviceInvokeConfigMap.get(clusterKey).getInvokeTimeout();

        RemoteMsg<InvokeRequest> reqMsg = (RemoteMsg<InvokeRequest>) RemoteMsg.createRequest();
        reqMsg.setMsgType(JieRpcConstant.INVOKE_REQUEST_MSG_TYPE);
        reqMsg.setBody(invokeRequest);
        reqMsg.setSerializeType(InnerSerializer.HESSIAN2.getCode());
        RemoteMsg<RpcResult> resultRemoteMsg = (RemoteMsg<RpcResult>) remoteClient.send(connection, reqMsg, timeoutMills);
        return resultRemoteMsg.getBody();
    }


}
