package top.glroyjie.rpc.invoke;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author jie-r
 * @since 2022/9/3
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
public class RpcResult implements Serializable {

    private Object result;

    private Throwable exception;

    //private String errMsg;


    public RpcResult(Object result) {
        this.result = result;
    }



}
