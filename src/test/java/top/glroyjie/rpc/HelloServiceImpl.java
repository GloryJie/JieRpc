package top.glroyjie.rpc;

/**
 * @author jie-r
 * @since 2022/9/4
 */
public class HelloServiceImpl implements HelloService{
    @Override
    public String sayHello(String name) {
        return "hello, " + name;
    }
}
