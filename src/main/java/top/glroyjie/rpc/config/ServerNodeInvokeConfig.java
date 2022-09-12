package top.glroyjie.rpc.config;

import lombok.Getter;
import lombok.Setter;

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

    public ServerNodeInvokeConfig(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
    }

}
