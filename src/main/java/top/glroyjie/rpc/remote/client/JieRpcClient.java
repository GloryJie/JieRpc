package top.glroyjie.rpc.remote.client;

import top.gloryjie.remote.connection.Connection;
import top.gloryjie.remote.endpoint.RemoteClient;
import top.gloryjie.remote.endpoint.client.NettyRemoteClient;
import top.gloryjie.remote.endpoint.client.RemoteClientConfig;
import top.gloryjie.remote.protocol.msg.RemoteMsg;
import top.glroyjie.rpc.config.ClusterInvokeConfig;
import top.glroyjie.rpc.config.JieRpcConstant;
import top.glroyjie.rpc.config.RpcClientConfig;
import top.glroyjie.rpc.config.ServerNodeInvokeConfig;
import top.glroyjie.rpc.exception.RpcException;
import top.glroyjie.rpc.invoke.Invocation;
import top.glroyjie.rpc.invoke.InvokeRequest;
import top.glroyjie.rpc.invoke.ParamPair;
import top.glroyjie.rpc.invoke.RpcResult;
import top.glroyjie.rpc.invoke.client.ClientNodeInvoker;
import top.glroyjie.rpc.invoke.client.ClusterInvoker;
import top.glroyjie.rpc.invoke.client.JieClusterInvoker;
import top.glroyjie.rpc.loadbalance.ClientLoadBalance;
import top.glroyjie.rpc.loadbalance.SimpleRoundRobinLoadBalance;
import top.glroyjie.rpc.loadbalance.WeightRandomLoadBalance;
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

    private final Map<String, Object> proxyObjectMap = new ConcurrentHashMap<>();

    private final Map<String, ClusterInvoker> clusterInvokerMap = new ConcurrentHashMap<>();

    private final Map<String, Class<?>> loadBalanceClassMap = new ConcurrentHashMap<>();


    public JieRpcClient(RpcClientConfig rpcClientConfig) {
        this.rpcClientConfig = rpcClientConfig;
        RemoteClientConfig clientConfig = new RemoteClientConfig();
        clientConfig.setConnectTimeout(rpcClientConfig.getConnectTimeout());
        clientConfig.setIoThreads(rpcClientConfig.getIoThreads());
        clientConfig.setQueueSize(rpcClientConfig.getQueueSize());
        this.remoteClient = new NettyRemoteClient(clientConfig);
        init();
    }


    private void init(){
        this.remoteClient.init();

        // default load balance impl
        loadBalanceClassMap.put(JieRpcConstant.SIMPLE_ROUND_ROBIN_LB, SimpleRoundRobinLoadBalance.class);
        loadBalanceClassMap.put(JieRpcConstant.WEIGHT_RANDOM_LB_NAME, WeightRandomLoadBalance.class);
    }

    @Override
    public void start() {
        remoteClient.start();
    }

    @Override
    public synchronized void registerServerService(String serviceName, ClusterInvokeConfig invokeConfig) {
        if (clusterInvokerMap.containsKey(serviceName)){
            throw new IllegalArgumentException("service already register");
        }
        // TODO router、load balance
        ClusterInvoker clusterInvoker = new JieClusterInvoker(this, invokeConfig);
        clusterInvokerMap.put(serviceName, clusterInvoker);
    }

    @Override
    public <T> T generateInterfaceRef(String serviceName, Class<T> interfaceClass) {
        String interfaceRefKey = serviceName + "#" + interfaceClass.getName();
        if (proxyObjectMap.get(interfaceRefKey) != null) {
            return (T) proxyObjectMap.get(interfaceRefKey);
        }

        synchronized (this) {
            if (proxyObjectMap.get(interfaceRefKey) != null) {
                return (T) proxyObjectMap.get(interfaceRefKey);
            }

            ClusterInvoker clusterInvoker = clusterInvokerMap.get(serviceName);
            if (clusterInvoker == null){
                throw new IllegalArgumentException("server service not register, serviceName=" + serviceName);
            }

            InvokeProxyHandler proxyHandler = new InvokeProxyHandler(serviceName, clusterInvoker);
            Object proxyInstance = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{interfaceClass}, proxyHandler);
            proxyObjectMap.put(interfaceRefKey, proxyInstance);
        }

        return (T) proxyObjectMap.get(interfaceRefKey);
    }

    @Override
    public ClientNodeInvoker createNodeInvoker(ServerNodeInvokeConfig invokeConfig) {
        return new JieClientNodeInvoker(invokeConfig);
    }

    @Override
    public ClientLoadBalance createLoadBalance(String loadBalanceKey) {
        Class<?> lbClass = loadBalanceClassMap.get(loadBalanceKey);
        if (lbClass == null){
            throw new RpcException(loadBalanceKey + " load balance not register");
        }
        Object instance = null;
        try {
            instance = lbClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RpcException("crate load balance instance err,key=" + loadBalanceKey, e);
        }
        return (ClientLoadBalance) instance;
    }

    class JieClientNodeInvoker implements ClientNodeInvoker{

        private final Connection connection;

        private final ServerNodeInvokeConfig invokeConfig;

        public JieClientNodeInvoker(ServerNodeInvokeConfig invokeConfig) {
            this.invokeConfig = invokeConfig;
            this.connection = remoteClient.connect(invokeConfig.getServerIp() + ":" + invokeConfig.getPort(), 0);
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

            RemoteMsg<InvokeRequest> reqMsg = (RemoteMsg<InvokeRequest>) RemoteMsg.createRequest();
            reqMsg.setMsgType(JieRpcConstant.INVOKE_REQUEST_MSG_TYPE);
            reqMsg.setBody(invokeRequest);
            reqMsg.setSerializeType(invokeConfig.getSerializeType());
            RemoteMsg<RpcResult> resultRemoteMsg = (RemoteMsg<RpcResult>) remoteClient.send(connection, reqMsg, invokeConfig.getInvokeTimeout());
            return resultRemoteMsg.getBody();
        }

        @Override
        public boolean isAvailable() {
            return connection.isActive();
        }

        @Override
        public ServerNodeInvokeConfig getInvokeConfig() {
            return invokeConfig;
        }

         @Override
         public int getWeight() {
             return invokeConfig.getWeight();
         }

     }


}
