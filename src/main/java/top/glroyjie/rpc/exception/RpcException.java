package top.glroyjie.rpc.exception;

/**
 * @author jie-r
 * @since 2022/9/3
 */
public class RpcException extends RuntimeException{

    public RpcException() {
    }

    public RpcException( String message) {
        super(message);
    }

    public RpcException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public RpcException(Throwable rootCause) {
        super(rootCause);
    }

}
