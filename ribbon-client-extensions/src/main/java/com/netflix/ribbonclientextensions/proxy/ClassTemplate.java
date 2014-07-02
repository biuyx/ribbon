/*
 * Copyright 2014 Netflix, Inc.
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
 */
package com.netflix.ribbonclientextensions.proxy;

import com.netflix.ribbonclientextensions.http.HttpResourceGroup;
import com.netflix.ribbonclientextensions.proxy.annotation.ResourceGroup;

import static com.netflix.ribbonclientextensions.proxy.annotation.ResourceGroup.*;

/**
 * @author Tomasz Bak
 */
class ClassTemplate<T> {
    private final Class<T> clientInterface;
    private final String resourceGroupName;
    private final Class<? extends HttpResourceGroup> resourceGroupClass;

    ClassTemplate(Class<T> clientInterface) {
        this.clientInterface = clientInterface;

        ResourceGroup annotation = clientInterface.getAnnotation(ResourceGroup.class);
        if (annotation != null) {
            String name = annotation.name().trim();
            resourceGroupName = name.isEmpty() ? null : annotation.name();
            if (UndefHttpResourceGroup.class.equals(annotation.resourceGroupClass())) {
                resourceGroupClass = null;
            } else {
                resourceGroupClass = annotation.resourceGroupClass();
            }
            verify();
        } else {
            resourceGroupName = null;
            resourceGroupClass = null;
        }
    }

    public Class<T> getClientInterface() {
        return clientInterface;
    }

    public String getResourceGroupName() {
        return resourceGroupName;
    }

    public Class<? extends HttpResourceGroup> getResourceGroupClass() {
        return resourceGroupClass;
    }

    public static <T> ClassTemplate<T> from(Class<T> clientInterface) {
        return new ClassTemplate<T>(clientInterface);
    }

    private void verify() {
        if (resourceGroupName != null && resourceGroupClass != null) {
            throw new RibbonProxyException("Both resource group name and class defined with @ResourceGroupSpec");
        }
    }
}
