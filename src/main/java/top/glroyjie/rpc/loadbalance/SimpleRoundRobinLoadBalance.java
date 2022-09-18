package top.glroyjie.rpc.loadbalance;

import top.glroyjie.rpc.invoke.Invocation;
import top.glroyjie.rpc.invoke.client.ClientNodeInvoker;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jie-r
 * @since 2022/9/16
 */
public class SimpleRoundRobinLoadBalance implements ClientLoadBalance{

    private final AtomicInteger atomicIndex = new AtomicInteger(0);

    @Override
    public ClientNodeInvoker select(List<ClientNodeInvoker> invokers, Invocation invocation) {
        int index = atomicIndex.getAndIncrement() % invokers.size();
        return invokers.get(index);
    }

}
