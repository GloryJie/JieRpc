package top.glroyjie.rpc.remote.server;

import top.gloryjie.remote.endpoint.RemoteServer;
import top.gloryjie.remote.endpoint.server.NettyRemoteServer;
import top.gloryjie.remote.endpoint.server.ServerConfig;
import top.gloryjie.remote.protocol.msg.RemoteMsg;
import top.gloryjie.remote.protocol.msg.RemoteMsgContext;
import top.gloryjie.remote.protocol.msg.RemoteMsgHandler;
import top.glroyjie.rpc.config.JieRpcConstant;
import top.glroyjie.rpc.exception.RpcException;
import top.glroyjie.rpc.invoke.Invocation;
import top.glroyjie.rpc.invoke.InvokeRequest;
import top.glroyjie.rpc.invoke.RpcResult;
import top.glroyjie.rpc.invoke.server.ServerInvoker;
import top.glroyjie.rpc.remote.RpcServer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author jie-r
 * @since 2022/9/4
 */
public class JieRpcServer implements RpcServer {

    private final ServerConfig serverConfig;

    private final RemoteServer remoteServer;

    private final RemoteMsgHandler invokeRequestHandler;

    private final Map<String, ServerInvoker<?>> serverInvokerMap = new ConcurrentHashMap<>();


    public JieRpcServer(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        this.remoteServer = new NettyRemoteServer(serverConfig);
        this.remoteServer.init();
        this.invokeRequestHandler = new ServerInvokeRequestHandler();
        this.remoteServer.registerMsgTypeAndHandler(JieRpcConstant.INVOKE_REQUEST_MSG_TYPE, InvokeRequest.class, invokeRequestHandler);
    }

    @Override
    public void start() {
        this.remoteServer.start();
    }

    @Override
    public <T> void registerInvoker(Class<T> interfaceClass, T implRef) {
        ServerInvoker<T> invoker = new ServerInvoker<>(interfaceClass, implRef);
        this.serverInvokerMap.put(invoker.getInterfaceName(), invoker);
    }

    class ServerInvokeRequestHandler implements RemoteMsgHandler {

        @Override
        public RemoteMsg<?> handleMsg(RemoteMsgContext remoteMsgContext) {
            RemoteMsg<?> reqMsg = remoteMsgContext.getMsg();
            InvokeRequest request = (InvokeRequest) reqMsg.getBody();

            ServerInvoker<?> serverInvoker = serverInvokerMap.get(request.getInterfaceName());
            if (serverInvoker == null) {
                RemoteMsg<RpcResult> response = (RemoteMsg<RpcResult>) RemoteMsg.createResponse(reqMsg);
                RpcResult rpcResult = new RpcResult();
                rpcResult.setException(new RpcException(String.format("server invoker=%s not found", request.getInterfaceName())));
                response.setBody(rpcResult);
                return response;
            }
            Invocation invocation = new Invocation();
            invocation.setInterfaceName(request.getInterfaceName());
            invocation.setMethodName(request.getMethodName());
            invocation.setMethodKey(request.getMethodKey());

            if (request.getParamList() != null && !request.getParamList().isEmpty()) {
                Object[] args = new Object[request.getParamList().size()];
                for (int i = 0; i < request.getParamList().size(); i++) {
                    args[i] = request.getParamList().get(i).getValue();
                }
                invocation.setArguments(args);
            }

            RpcResult result = serverInvoker.invoke(invocation);
            RemoteMsg<RpcResult> response = (RemoteMsg<RpcResult>) RemoteMsg.createResponse(reqMsg);
            response.setBody(result);
            return response;
        }
    }


}
