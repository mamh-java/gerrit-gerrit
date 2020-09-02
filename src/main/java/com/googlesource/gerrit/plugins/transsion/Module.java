// Copyright (C) 2013 The Android Open Source Project
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

package com.googlesource.gerrit.plugins.transsion;

import com.google.gerrit.extensions.config.FactoryModule;
import com.google.gerrit.extensions.events.LifecycleListener;
import com.google.inject.Scopes;
import com.google.inject.internal.UniqueAnnotations;
import com.googlesource.gerrit.plugins.transsion.commands.HookArgs;
import com.googlesource.gerrit.plugins.transsion.commands.HookExecutor;
import com.googlesource.gerrit.plugins.transsion.commands.HookQueue;


class Module extends FactoryModule {
  @Override
  protected void configure() {
    bind(HookQueue.class).in(Scopes.SINGLETON);
    bind(LifecycleListener.class).annotatedWith(UniqueAnnotations.create()).to(HookQueue.class);
    bind(HookExecutor.class).in(Scopes.SINGLETON);
    bind(LifecycleListener.class).annotatedWith(UniqueAnnotations.create()).to(HookExecutor.class);

    factory(HookArgs.Factory.class);
  }

}

