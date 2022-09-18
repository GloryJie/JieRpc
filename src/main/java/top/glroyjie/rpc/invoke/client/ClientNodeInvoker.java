package top.glroyjie.rpc.invoke.client;

import top.glroyjie.rpc.config.ServerNodeInvokeConfig;
import top.glroyjie.rpc.invoke.Invoker;

/**
 * single node invoker
 * @author jie-r
 * @since 2022/9/9
 */
public interface ClientNodeInvoker extends Invoker {

    boolean isAvailable();

    ServerNodeInvokeConfig getInvokeConfig();

    int getWeight();

}
