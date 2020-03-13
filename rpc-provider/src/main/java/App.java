import com.jean.rpc.RpcConfig;
import com.jean.rpc.RpcSocketServer;
import com.jean.service.UserService;
import com.jean.service.UserServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
//        UserService userService = new UserServiceImpl();
//        RpcSocketServer rpcSocketServer = new RpcSocketServer();
//        rpcSocketServer.send(userService,8080);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RpcConfig.class);
        context.start();
    }
}
