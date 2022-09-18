package top.glroyjie.rpc.invoke;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

/**
 * desc one rpc invoke
 * @author jie-r
 * @since 2022/9/3
 */
@Setter
@Getter
public class Invocation {

    public static final String CLIENT_APPLICATION_NAME = "application.name";
    public static final String REQUEST_TIMEOUT = "_REQUEST_TIMEOUT";

    private String serviceName;
    private String interfaceName;
    private String methodName;
    private Object[] arguments;
    private String methodKey;

    private HashMap<String,String> attachments;

}
