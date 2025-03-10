// Copyright (C) 2012 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.gerrit.extensions.registration;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gerrit.extensions.events.LifecycleListener;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** <b>DO NOT USE</b> */
public class PrivateInternals_DynamicTypes {
  public static Map<TypeLiteral<?>, DynamicItem<?>> dynamicItemsOf(Injector src) {
    Map<TypeLiteral<?>, DynamicItem<?>> m = new HashMap<>();
    for (Map.Entry<Key<?>, Binding<?>> e : src.getBindings().entrySet()) {
      TypeLiteral<?> type = e.getKey().getTypeLiteral();
      if (type.getRawType() == DynamicItem.class) {
        ParameterizedType p = (ParameterizedType) type.getType();
        m.put(
            TypeLiteral.get(p.getActualTypeArguments()[0]),
            (DynamicItem<?>) e.getValue().getProvider().get());
      }
    }
    if (m.isEmpty()) {
      return Collections.emptyMap();
    }
    return Collections.unmodifiableMap(m);
  }

  public static Map<TypeLiteral<?>, DynamicSet<?>> dynamicSetsOf(Injector src) {
    Map<TypeLiteral<?>, DynamicSet<?>> m = new HashMap<>();
    for (Map.Entry<Key<?>, Binding<?>> e : src.getBindings().entrySet()) {
      TypeLiteral<?> type = e.getKey().getTypeLiteral();
      if (type.getRawType() == DynamicSet.class) {
        ParameterizedType p = (ParameterizedType) type.getType();
        m.put(
            TypeLiteral.get(p.getActualTypeArguments()[0]),
            (DynamicSet<?>) e.getValue().getProvider().get());
      }
    }
    if (m.isEmpty()) {
      return Collections.emptyMap();
    }
    return Collections.unmodifiableMap(m);
  }

  public static Map<TypeLiteral<?>, DynamicMap<?>> dynamicMapsOf(Injector src) {
    Map<TypeLiteral<?>, DynamicMap<?>> m = new HashMap<>();
    for (Map.Entry<Key<?>, Binding<?>> e : src.getBindings().entrySet()) {
      TypeLiteral<?> type = e.getKey().getTypeLiteral();
      if (type.getRawType() == DynamicMap.class) {
        ParameterizedType p = (ParameterizedType) type.getType();
        m.put(
            TypeLiteral.get(p.getActualTypeArguments()[0]),
            (DynamicMap<?>) e.getValue().getProvider().get());
      }
    }
    if (m.isEmpty()) {
      return Collections.emptyMap();
    }
    return Collections.unmodifiableMap(m);
  }

  public static ImmutableList<RegistrationHandle> attachItems(
      Injector src, String pluginName, Map<TypeLiteral<?>, DynamicItem<?>> items) {
    if (src == null || items == null || items.isEmpty()) {
      return ImmutableList.of();
    }

    List<RegistrationHandle> handles = new ArrayList<>(4);
    try {
      for (Map.Entry<TypeLiteral<?>, DynamicItem<?>> e : items.entrySet()) {
        @SuppressWarnings("unchecked")
        TypeLiteral<Object> type = (TypeLiteral<Object>) e.getKey();

        @SuppressWarnings("unchecked")
        DynamicItem<Object> item = (DynamicItem<Object>) e.getValue();

        for (Binding<Object> b : bindings(src, type)) {
          Class<? super Object> rawType = type.getRawType();
          DynamicItem.Final annotation = rawType.getAnnotation(DynamicItem.Final.class);
          if (annotation != null) {
            Object existingBinding = item.get();
            if (existingBinding != null) {
              throw new ProvisionException(
                  String.format(
                      "Attempting to bind a @DynamicItem.Final %s twice: it was already bound to %s"
                          + " and tried to bind again to %s",
                      rawType.getName(), existingBinding, b));
            }

            String implementedByPlugin = annotation.implementedByPlugin();
            if (!Strings.isNullOrEmpty(implementedByPlugin)
                && !implementedByPlugin.equals(pluginName)) {
              throw new ProvisionException(
                  String.format(
                      "Attempting to bind a @DynamicItem.Final %s to unexpected plugin: it was"
                          + " supposed to be bound to %s plugin but tried bind to %s plugin",
                      rawType.getName(), implementedByPlugin, pluginName));
            }
          }
          handles.add(item.set(b.getKey(), b.getProvider(), pluginName));
        }
      }
    } catch (RuntimeException | Error e) {
      remove(handles);
      throw e;
    }
    return ImmutableList.copyOf(handles);
  }

  public static ImmutableList<RegistrationHandle> attachSets(
      Injector src, String pluginName, Map<TypeLiteral<?>, DynamicSet<?>> sets) {
    if (src == null || sets == null || sets.isEmpty()) {
      return ImmutableList.of();
    }

    List<RegistrationHandle> handles = new ArrayList<>(4);
    try {
      for (Map.Entry<TypeLiteral<?>, DynamicSet<?>> e : sets.entrySet()) {
        @SuppressWarnings("unchecked")
        TypeLiteral<Object> type = (TypeLiteral<Object>) e.getKey();

        @SuppressWarnings("unchecked")
        DynamicSet<Object> set = (DynamicSet<Object>) e.getValue();

        for (Binding<Object> b : bindings(src, type)) {
          if (b.getKey().getAnnotation() != null) {
            handles.add(set.add(pluginName, b.getKey(), b.getProvider()));
          }
        }
      }
    } catch (RuntimeException | Error e) {
      remove(handles);
      throw e;
    }
    return ImmutableList.copyOf(handles);
  }

  public static ImmutableList<RegistrationHandle> attachMaps(
      Injector src, String pluginName, Map<TypeLiteral<?>, DynamicMap<?>> maps) {
    if (src == null || maps == null || maps.isEmpty()) {
      return ImmutableList.of();
    }

    List<RegistrationHandle> handles = new ArrayList<>(4);
    try {
      for (Map.Entry<TypeLiteral<?>, DynamicMap<?>> e : maps.entrySet()) {
        @SuppressWarnings("unchecked")
        TypeLiteral<Object> type = (TypeLiteral<Object>) e.getKey();

        @SuppressWarnings("unchecked")
        PrivateInternals_DynamicMapImpl<Object> map =
            (PrivateInternals_DynamicMapImpl<Object>) e.getValue();

        for (Binding<Object> b : bindings(src, type)) {
          if (b.getKey().getAnnotation() != null) {
            handles.add(map.put(pluginName, b.getKey(), b.getProvider()));
          }
        }
      }
    } catch (RuntimeException | Error e) {
      remove(handles);
      throw e;
    }
    return ImmutableList.copyOf(handles);
  }

  public static LifecycleListener registerInParentInjectors() {
    return new LifecycleListener() {
      private List<RegistrationHandle> handles;

      @Inject private Injector self;

      @Override
      public void start() {
        handles = new ArrayList<>(4);
        Injector parent = self.getParent();
        while (parent != null) {
          handles.addAll(attachSets(self, PluginName.GERRIT, dynamicSetsOf(parent)));
          handles.addAll(attachMaps(self, PluginName.GERRIT, dynamicMapsOf(parent)));
          parent = parent.getParent();
        }
        if (handles.isEmpty()) {
          handles = null;
        }
      }

      @Override
      public void stop() {
        remove(handles);
        handles = null;
      }
    };
  }

  private static void remove(List<RegistrationHandle> handles) {
    if (handles != null) {
      for (RegistrationHandle handle : handles) {
        handle.remove();
      }
    }
  }

  private static <T> List<Binding<T>> bindings(Injector src, TypeLiteral<T> type) {
    return src.findBindingsByType(type);
  }
}
