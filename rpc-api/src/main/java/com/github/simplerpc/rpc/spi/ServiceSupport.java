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
package com.github.simplerpc.rpc.spi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * SPI类加载器帮助类
 * @author LiYue
 * Date: 2019-03-26
 */
public class ServiceSupport {
    private final static Map<String, Object> singletonServices = new HashMap<>();
    public synchronized static <S> S load(Class<S> service) {
        return StreamSupport.
                stream(ServiceLoader.load(service).spliterator(), false)  //原理：在ServiceLoader.load的时候，根据传入的接口类，遍历META-INF/services目录下的以该类命名的文件中的所有类，并实例化返回。
                .map(ServiceSupport::singletonFilter)
                .findFirst().orElseThrow(ServiceLoadException::new);
        //ServiceLoader.load 就是SPI的核心
        //创建一个接口文件
        //在resources资源目录下创建META-INF/services文件夹
        //在services文件夹中创建文件，以接口全名命名
        //创建接口实现类
    }
    public synchronized static <S> Collection<S> loadAll(Class<S> service) {
        return StreamSupport.
                stream(ServiceLoader.load(service).spliterator(), false)
                .map(ServiceSupport::singletonFilter).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private static <S>  S singletonFilter(S service) {

        if(service.getClass().isAnnotationPresent(Singleton.class)) {//PpcRequestHandler中的单例模式public @interface Singleton {，在这里处理
            String className = service.getClass().getCanonicalName();
            Object singletonInstance = singletonServices.putIfAbsent(className, service);//如果有过了，则返回第一次放入的这个值service  如果是第一次，则返回null
            return singletonInstance == null ? service : (S) singletonInstance;
        } else {
            return service;
        }
    }
}
