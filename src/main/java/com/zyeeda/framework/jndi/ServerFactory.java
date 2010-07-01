/*
 * Copyright 2010 Zyeeda Co. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.zyeeda.framework.jndi;

import java.lang.reflect.Constructor;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 * JNDI server factory.
 *
 * @author		Qi Zhao
 * @version		%I%, %G%
 * @since		1.0
 */
public class ServerFactory implements ObjectFactory {

    private Object obj;

    @Override
    public synchronized Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        if (this.obj != null) {
        	return this.obj;
        }
        
    	if (obj instanceof Reference) {
            Reference ref = (Reference)obj;
            String className = ref.getClassName();
            
            Class<?> clazz = this.getClass().getClassLoader().loadClass(className);
            Constructor<?> ctor = clazz.getConstructor();
            this.obj = ctor.newInstance();
            
            return this.obj;
        }

        return null;
    }
}


