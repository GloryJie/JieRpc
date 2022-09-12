package top.glroyjie.rpc.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author jie-r
 * @since 2022/9/4
 */
@Getter
@Setter
public class ClusterInvokeConfig {
    private int connectTimeout = 1000;
    private int invokeTimeout = 10000;
    private boolean lazyConnect = false;

    private List<ServerNodeInvokeConfig> nodeInvokeConfigs;

}
