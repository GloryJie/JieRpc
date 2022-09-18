package top.glroyjie.rpc.invoke.client;

import top.glroyjie.rpc.config.ClusterInvokeConfig;
import top.glroyjie.rpc.config.InvokeConfig;
import top.glroyjie.rpc.config.ServerNodeInvokeConfig;
import top.glroyjie.rpc.exception.RpcException;
import top.glroyjie.rpc.invoke.Invocation;
import top.glroyjie.rpc.invoke.RpcResult;
import top.glroyjie.rpc.loadbalance.ClientLoadBalance;
import top.glroyjie.rpc.remote.RpcClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jie-r
 * @since 2022/9/14
 */
public class JieClusterInvoker implements ClusterInvoker{

    private final RpcClient rpcClient;

    private final List<ClientNodeInvoker> nodeInvokerList = new ArrayList<>();

    private final ClusterInvokeConfig clusterInvokeConfig;

    private boolean initFlag = false;

    private ClientLoadBalance loadBalance;


    public JieClusterInvoker(RpcClient rpcClient, ClusterInvokeConfig clusterInvokeConfig) {
        this.rpcClient = rpcClient;
        this.clusterInvokeConfig = clusterInvokeConfig;
        if (!clusterInvokeConfig.getInvokeConfig().isLazyConnect()){
            this.initInvoker();
        }

       this.loadBalance =  rpcClient.createLoadBalance(clusterInvokeConfig.getInvokeConfig().getLoadBalanceWay());
    }

    @Override
    public RpcResult invoke(Invocation invocation) {
        invocation.setServiceName(this.serviceName());
        if (!initFlag){
            this.initInvoker();
        }
        // TODO router、load balance
        //throw new RpcException("random weight load balance strategy could not select one invoker");
        List<ClientNodeInvoker> nodeInvokers = nodeInvokerList.stream().filter(ClientNodeInvoker::isAvailable).collect(Collectors.toList());
        if (nodeInvokers.size() == 0){
            throw new RpcException("no invoker");
        }
        return loadBalance.select(nodeInvokers, invocation).invoke(invocation);
    }

    @Override
    public String serviceName() {
        return this.clusterInvokeConfig.getServiceName();
    }

    @Override
    public ClusterInvokeConfig getInvokeConfig() {
        return clusterInvokeConfig;
    }


    private synchronized void initInvoker(){
        if (initFlag){
            return;
        }
        InvokeConfig invokeConfig = clusterInvokeConfig.getInvokeConfig();
        for (ServerNodeInvokeConfig nodeInvokeConfig : clusterInvokeConfig.getNodeInvokeConfigs()) {
            // copy config to nodeInvokeConfig
            nodeInvokeConfig.setInvokeTimeout(invokeConfig.getInvokeTimeout());
            nodeInvokeConfig.setSerializeType(invokeConfig.getSerializeType());
            ClientNodeInvoker nodeInvoker = rpcClient.createNodeInvoker(nodeInvokeConfig);
            nodeInvokerList.add(nodeInvoker);
        }
        initFlag = true;
    }
}
