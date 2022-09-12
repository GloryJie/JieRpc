package top.glroyjie.rpc.config;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jie-r
 * @since 2022/9/4
 */
@Setter
@Getter
public class RpcClientConfig {
    private int connectTimeout = 500;
    private int ioThreads = 10;
    private int queueSize = 1024;

}
