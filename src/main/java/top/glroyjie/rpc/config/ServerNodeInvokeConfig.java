package top.glroyjie.rpc.config;

import lombok.Getter;
import lombok.Setter;
import top.gloryjie.remote.serializer.InnerSerializer;

/**
 * @author jie-r
 * @since 2022/9/4
 */
@Getter
@Setter
public class ServerNodeInvokeConfig {

    private String serverIp;
    private int port;
    private int weight = 1000;

    /**
     * this config copy from cluster config
     */
    private int invokeTimeout = 10000;
    private boolean lazyConnect = false;
    private int serializeType = InnerSerializer.HESSIAN2.getCode();

    public ServerNodeInvokeConfig(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
    }

}
