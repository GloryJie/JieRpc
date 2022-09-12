package top.glroyjie.rpc.invoke;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author jie-r
 * @since 2022/9/3
 */
@Setter
@Getter
@ToString
public class InvokeRequest implements Serializable {

    private String serviceName;

    private String interfaceName;

    private String methodName;

    private String methodKey;

    /**
     * 参数列表, key-> paramType, value -> param
     */
    private List<ParamPair> paramList;

    /**
     * 传递的线程上下文信息
     */
    private Map<String, String> context;

}
