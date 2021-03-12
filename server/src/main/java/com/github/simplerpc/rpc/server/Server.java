/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.simplerpc.rpc.server;

import com.github.simplerpc.rpc.NameService;
import com.github.simplerpc.rpc.RpcAccessPoint;
import com.github.simplerpc.rpc.hello.HelloService;
import com.github.simplerpc.rpc.spi.ServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.net.URI;

/**
 * @author LiYue
 * Date: 2019/9/20
 */
public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    public static void main(String [] args) throws Exception {

        String serviceName = HelloService.class.getCanonicalName(); //获得类的全名，之后用来之后做注册用  com.github.simplerpc.rpc.hello.HelloService
        File tmpDirFile = new File(System.getProperty("java.io.tmpdir"));//获得一个临时文件夹，之后做注册中心使用 C:\Users\Arthur\AppData\Local\Temp
        File file = new File(tmpDirFile, "simple_rpc_name_service.data");//创建临时文件，用来做注册中心 C:\Users\Arthur\AppData\Local\Temp\simple_rpc_name_service.data
        HelloService helloService = new HelloServiceImpl();//创建服务端的真正的执行代码的实例
//
        logger.info("创建并启动RpcAccessPoint...");
        try(RpcAccessPoint rpcAccessPoint = ServiceSupport.load(RpcAccessPoint.class);//通过SPI获得当前项目中的PpcAccessPointRPC框架对外提供的服务接口
            // 相当于是使用rpc的服务接口 功能包括有启动rpc框架进行监听  服务端注册服务  获得注册中心 客户端获得远程服务  实现类是NettyRpcAccessPoint
            Closeable ignored = rpcAccessPoint.startServer()) {
//            NameService nameService = rpcAccessPoint.getNameService(file.toURI());
            NameService nameService = rpcAccessPoint.getNameService(getMysqlURI());
            assert nameService != null;
            logger.info("向RpcAccessPoint注册{}服务...", serviceName);
            URI uri = rpcAccessPoint.addServiceProvider(helloService, HelloService.class);//这里的向RPCaccessPoint注册，都只是放在了本地的hashmap中
            logger.info("服务名: {}, 向NameService注册...", serviceName);
            nameService.registerService(serviceName, uri);
            logger.info("开始提供服务，按任何键退出.");
            //noinspection ResultOfMethodCallIgnored
            System.in.read();
            logger.info("Bye!");
        }
    }

    public static URI getMysqlURI(){
        URI uri = URI.create("mysql:jdbc:mysql://127.0.0.1:3306/rpc?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT");
        return uri;
    }

    public static URI getOracleURI(){
        URI uri = URI.create("oracle:jdbc:oracle:thin:@127.0.0.1:1521:orcl");
        return uri;
    }

}
