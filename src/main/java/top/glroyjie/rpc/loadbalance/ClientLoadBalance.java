package top.glroyjie.rpc.loadbalance;

import top.glroyjie.rpc.invoke.Invocation;
import top.glroyjie.rpc.invoke.client.ClientNodeInvoker;

import java.util.List;

/**
 * @author jie-r
 * @since 2022/9/16
 */
public interface ClientLoadBalance {

    ClientNodeInvoker select(List<ClientNodeInvoker> invokers, Invocation invocation);

}
