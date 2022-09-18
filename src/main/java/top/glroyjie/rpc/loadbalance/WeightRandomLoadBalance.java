package top.glroyjie.rpc.loadbalance;

import top.glroyjie.rpc.invoke.Invocation;
import top.glroyjie.rpc.invoke.client.ClientNodeInvoker;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * random weight，like dubbo
 * @author jie-r
 * @since 2022/9/16
 */
public class WeightRandomLoadBalance implements ClientLoadBalance{

    @Override
    public ClientNodeInvoker select(List<ClientNodeInvoker> invokers, Invocation invocation) {
        // weights[i] = sum(weight[0]...weight[i])
        int[] weights = new int[invokers.size()];
        boolean allSame = true;
        int firstWeight = invokers.get(0).getWeight();
        int totalWeight = weights[0] = firstWeight;
        for (int i = 1; i < invokers.size(); i++) {
            int weight = invokers.get(i).getWeight();
            totalWeight += weight;
            weights[i] = totalWeight;
            if (allSame && firstWeight != weight){
                allSame = false;
            }
        }

        // not all same, random weight
        if (!allSame && totalWeight > 0){
            int randomWeight = ThreadLocalRandom.current().nextInt(totalWeight);
            for (int i = 0; i < weights.length; i++) {
                if (randomWeight < weights[i]){
                    return invokers.get(i);
                }
            }
        }

        // if all same, random select
        return invokers.get(ThreadLocalRandom.current().nextInt(invokers.size()));
    }

}
