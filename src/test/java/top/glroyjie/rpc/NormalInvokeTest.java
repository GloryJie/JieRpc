package top.glroyjie.rpc;

import org.junit.jupiter.api.Test;
import top.gloryjie.remote.endpoint.server.ServerConfig;
import top.glroyjie.rpc.config.ClusterInvokeConfig;
import top.glroyjie.rpc.config.InvokeConfig;
import top.glroyjie.rpc.config.RpcClientConfig;
import top.glroyjie.rpc.config.ServerNodeInvokeConfig;
import top.glroyjie.rpc.remote.client.JieRpcClient;
import top.glroyjie.rpc.remote.server.JieRpcServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author jie-r
 * @since 2022/9/4
 */
public class NormalInvokeTest {

    @Test
    public void serverTest() throws Exception{
        ServerConfig serverConfig = new ServerConfig("127.0.0.1", 8080);
        serverConfig.setIoThreads(10);
        JieRpcServer rpcServer = new JieRpcServer(serverConfig);
        rpcServer.registerInvoker(HelloService.class, new HelloServiceImpl());
        rpcServer.start();

        TimeUnit.MINUTES.sleep(10);
    }

    @Test
    public void serverBTest() throws Exception{
        ServerConfig serverConfig = new ServerConfig("127.0.0.1", 8081);
        serverConfig.setIoThreads(10);
        JieRpcServer rpcServer = new JieRpcServer(serverConfig);
        rpcServer.registerInvoker(HelloService.class, new HelloServiceImpl());
        rpcServer.start();

        TimeUnit.MINUTES.sleep(10);
    }


    @Test
    public void clientTest() throws Exception{
        RpcClientConfig rpcClientConfig = new RpcClientConfig();
        JieRpcClient rpcClient = new JieRpcClient(rpcClientConfig);

        ClusterInvokeConfig clusterInvokeConfig = new ClusterInvokeConfig();
        List<ServerNodeInvokeConfig> nodeInvokeConfigList = new ArrayList<>();
        nodeInvokeConfigList.add(new ServerNodeInvokeConfig("127.0.0.1", 8080));
        nodeInvokeConfigList.add(new ServerNodeInvokeConfig("127.0.0.1", 8081));

        clusterInvokeConfig.setInvokeConfig(new InvokeConfig());
        clusterInvokeConfig.setNodeInvokeConfigs(nodeInvokeConfigList);

        rpcClient.start();

        rpcClient.registerServerService("test", clusterInvokeConfig);
        HelloService helloService = rpcClient.generateInterfaceRef("test", HelloService.class);

        for (int i = 0; i < 10; i++) {
            String result = helloService.sayHello("JOJO");
            System.out.println(result);
        }

        TimeUnit.SECONDS.sleep(3);


    }



}
