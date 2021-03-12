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
package com.github.simplerpc.rpc.client;

import com.github.simplerpc.rpc.NameService;
import com.github.simplerpc.rpc.RpcAccessPoint;
import com.github.simplerpc.rpc.hello.HelloService;
import com.github.simplerpc.rpc.hello.entity.HelloRequest;
import com.github.simplerpc.rpc.hello.entity.HelloResult;
import com.github.simplerpc.rpc.spi.ServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;

/**
 * @author LiYue
 * Date: 2019/9/20
 */
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    public static void main(String [] args) throws IOException, InterruptedException {
        String serviceName = HelloService.class.getCanonicalName();
        File tmpDirFile = new File(System.getProperty("java.io.tmpdir"));
        File file = new File(tmpDirFile, "simple_rpc_name_service.data");
        String name = "Master MQ";
        try(RpcAccessPoint rpcAccessPoint = ServiceSupport.load(RpcAccessPoint.class)) {
//            NameService nameService = rpcAccessPoint.getNameService(file.toURI());
            NameService nameService = rpcAccessPoint.getNameService(getMysqlURI());
            assert nameService != null;
            URI uri = nameService.lookupService(serviceName);
            assert uri != null;
            logger.info("找到服务{}，提供者: {}.", serviceName, uri);
            HelloService helloService = rpcAccessPoint.getRemoteService(uri, HelloService.class);
            logger.info("请求服务, name: {}...", name);
            /*String response;
            while (true) {
                response = helloService.hello(name + "" + new Date().getTime());
                Thread.sleep(1000);
                logger.info("收到响应: {}.", response);
            }*/

            HelloRequest request = new HelloRequest();
            request.setParam(Arrays.asList("hello","world"));
            request.setTimestamp(new Date().toString());
            HelloResult response = helloService.helloMoreResult(request);
            logger.info("收到响应: {}.", response.toString());
        }

    }
    public static URI getMysqlURI(){
        URI uri = URI.create("mysql:jdbc:mysql://139.224.220.127:3306/rpc?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT");
        return uri;
    }


    public static URI getOracleURI(){
        URI uri = URI.create("oracle:jdbc:oracle:thin:@127.0.0.1:1521:orcl");
        return uri;
    }
}
