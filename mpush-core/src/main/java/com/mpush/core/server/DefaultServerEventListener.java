/*
 * (C) Copyright 2015-2016 the original author or authors.
 *
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
 *
 * Contributors:
 *     ohun@live.cn (夜色)
 */

package com.mpush.core.server;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mpush.api.common.ServerEventListener;
import com.mpush.api.event.UserOfflineEvent;
import com.mpush.api.event.UserOnlineEvent;
import com.mpush.api.spi.Spi;
import com.mpush.api.spi.core.ServerEventListenerFactory;
import com.mpush.tools.event.EventBus;

/**
 * Created by ohun on 16/10/19.
 *
 * @author ohun@live.cn (夜色)
 */
@Spi(order = 1)
public final class DefaultServerEventListener implements ServerEventListener, ServerEventListenerFactory {

    @Override
    public ServerEventListener get() {
        //一定不能忘了这句 及on方法上的一堆注解
        EventBus.register(this);
        return this;
    }

    @Override
    @Subscribe
    @AllowConcurrentEvents
    public void on(UserOfflineEvent event) {
        System.out.println("用户掉线--->" + event.getUserId());
        ServerEventListener.super.on(event);
    }

    @Override
    @Subscribe
    @AllowConcurrentEvents
    public void on(UserOnlineEvent event) {
        System.out.println("用户在线--->" + event.getUserId());
        ServerEventListener.super.on(event);
    }
}
