/*
 * (C) Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *   ohun@live.cn (夜色)
 */

package com.mpush.bootstrap;

import com.mpush.tools.log.Logs;

public class Main {

    /**
     * 源码启动请不要直接运行此方法，否则不能正确加载配置文件
     * (因为配置文件的加载问题 CC.java会读取application.conf,但boot里没有这个文件,导致部分配置参数load不进来)
     * 可以把test模块里的application.conf挪到此处,但是不确定有没有其他影响,慎用,理解why不能这样启动就好了
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        Logs.init();
        Logs.Console.info("launch mpush server...");
        ServerLauncher launcher = new ServerLauncher();
        launcher.init();
        launcher.start();
        addHook(launcher);
    }

    /**
     * 注意点
     * 1.不要ShutdownHook Thread 里调用System.exit()方法，否则会造成死循环。
     * 2.如果有非守护线程，只有所有的非守护线程都结束了才会执行hook
     * 3.Thread默认都是非守护线程，创建的时候要注意
     * 4.注意线程抛出的异常，如果没有被捕获都会跑到Thread.dispatchUncaughtException
     *
     * @param launcher
     */
    private static void addHook(ServerLauncher launcher) {
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {

                    try {
                        launcher.stop();
                    } catch (Exception e) {
                        Logs.Console.error("mpush server stop ex", e);
                    }
                    Logs.Console.info("jvm exit, all service stopped.");

                }, "mpush-shutdown-hook-thread")
        );
    }
}
