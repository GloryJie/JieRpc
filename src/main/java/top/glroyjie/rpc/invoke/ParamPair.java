package top.glroyjie.rpc.invoke;

import lombok.*;

import java.io.Serializable;

/**
 * desc method single param
 * @author jie-r
 * @since 2022/9/3
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
public class ParamPair implements Serializable {

    /**
     * 参数类型
     */
    private String type;

    /**
     * 参数值
     */
    private Object value;

}
