package top.glroyjie.rpc.remote;

/**
 * @author jie-r
 * @since 2022/9/3
 */
public interface RpcServer {

    void start();


    <T> void registerInvoker(Class<T> interfaceClass, T implRef);


}
