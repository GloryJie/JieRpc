package top.glroyjie.rpc.config;

import lombok.Getter;
import lombok.Setter;
import top.gloryjie.remote.serializer.InnerSerializer;

/**
 * @author jie-r
 * @since 2022/9/14
 */
@Setter
@Getter
public class InvokeConfig {

    private int invokeTimeout = 10000;
    private boolean lazyConnect = false;
    private int serializeType = InnerSerializer.HESSIAN2.getCode();

    private String loadBalanceWay = JieRpcConstant.SIMPLE_ROUND_ROBIN_LB;
}
