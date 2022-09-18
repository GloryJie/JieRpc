package top.glroyjie.rpc.invoke;

/**
 * client&server invoker abstract
 * @author jie-r
 * @since 2022/9/3
 */
public interface Invoker {

    RpcResult invoke(Invocation invocation);

}
