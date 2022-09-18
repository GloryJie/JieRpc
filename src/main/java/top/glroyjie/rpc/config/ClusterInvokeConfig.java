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

    private String serviceName;

    private InvokeConfig invokeConfig;

    private List<ServerNodeInvokeConfig> nodeInvokeConfigs;

}
