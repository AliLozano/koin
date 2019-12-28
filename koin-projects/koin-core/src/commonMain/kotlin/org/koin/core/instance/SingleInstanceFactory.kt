/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.koin.core.instance

import org.koin.core.Koin
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.ThreadScope
import org.koin.core.state.CallerThreadContext
import org.koin.core.state.assertMainThread
import org.koin.core.state.currentCallerThreadContext

/**
 * Single instance holder
 * @author Arnaud Giuliani
 */
class SingleInstanceFactory<T>(koin: Koin, beanDefinition: BeanDefinition<T>) :
    InstanceFactory<T>(koin, beanDefinition) {

    private var value: T? = null

    override fun isCreated(): Boolean = (value != null)

    override fun drop() {
        beanDefinition.callbacks.onClose?.invoke(value)
        value = null
    }

    override fun create(context: InstanceContext): T = if (value == null) {
        super.create(context)
    } else value ?: error("Single instance created couldn't return value")

    @Suppress("UNCHECKED_CAST")
    override fun get(context: InstanceContext): T {
        assertMainThread()
        val threadContext = currentCallerThreadContext

        if(currentCallerThreadContext == CallerThreadContext.None)
            throw IllegalStateException("Thread context not set")

        if (!isCreated()) {
            value = create(context)
        }

        if(threadContext != CallerThreadContext.Main && beanDefinition.threadScope == ThreadScope.Main){
            error("Cannot access from main thread")
        }

        return value ?: error("Single instance created couldn't return value")
    }
}