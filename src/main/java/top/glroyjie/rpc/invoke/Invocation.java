package top.glroyjie.rpc.invoke;

import lombok.Getter;
import lombok.Setter;

/**
 * desc one rpc invoke
 * @author jie-r
 * @since 2022/9/3
 */
@Setter
@Getter
public class Invocation {

    private String serviceName;
    private String interfaceName;
    private String methodName;
    private Object[] arguments;
    private String methodKey;

}
