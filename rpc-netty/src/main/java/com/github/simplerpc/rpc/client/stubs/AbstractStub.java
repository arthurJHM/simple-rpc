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
package com.github.simplerpc.rpc.client.stubs;

import com.github.simplerpc.rpc.client.RequestIdSupport;
import com.github.simplerpc.rpc.client.ServiceStub;
import com.github.simplerpc.rpc.client.ServiceTypes;
import com.github.simplerpc.rpc.serialize.SerializeSupport;
import com.github.simplerpc.rpc.transport.Transport;
import com.github.simplerpc.rpc.transport.command.Code;
import com.github.simplerpc.rpc.transport.command.Command;
import com.github.simplerpc.rpc.transport.command.Header;
import com.github.simplerpc.rpc.transport.command.ResponseHeader;

import java.util.concurrent.ExecutionException;

/**
 * @author LiYue
 * Date: 2019/9/27
 */
public abstract class AbstractStub implements ServiceStub {
    protected Transport transport;

    protected byte [] invokeRemote(RpcRequest request) { //构造出来的代理类Stub实际调用的函数
        Header header = new Header(ServiceTypes.TYPE_RPC_REQUEST, 1, RequestIdSupport.next());
        byte [] payload = SerializeSupport.serialize(request);
        Command requestCommand = new Command(header, payload);//构造requestCommand  payload是实际的经过序列化的请求  head包括rpc请求类型，版本号和请求的序号
        try {
            Command responseCommand = transport.send(requestCommand).get();//通过transport请求发送 获得结果
            ResponseHeader responseHeader = (ResponseHeader) responseCommand.getHeader();//解析header
            if(responseHeader.getCode() == Code.SUCCESS.getCode()) {//解析headCode是不是success
                return responseCommand.getPayload();
            } else {
                throw new Exception(responseHeader.getError());
            }

        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setTransport(Transport transport) {
        this.transport = transport;
    }
}
