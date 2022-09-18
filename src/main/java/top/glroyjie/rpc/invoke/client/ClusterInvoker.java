package top.glroyjie.rpc.invoke.client;

import top.glroyjie.rpc.config.ClusterInvokeConfig;
import top.glroyjie.rpc.invoke.Invoker;

/**
 * @author jie-r
 * @since 2022/9/4
 */
public interface ClusterInvoker extends Invoker {

    String serviceName();

    ClusterInvokeConfig getInvokeConfig();

}
