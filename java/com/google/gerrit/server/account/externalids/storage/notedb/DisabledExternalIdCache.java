// Copyright (C) 2017 The Android Open Source Project
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

package com.google.gerrit.server.account.externalids.storage.notedb;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.gerrit.entities.Account;
import com.google.gerrit.server.account.externalids.ExternalId;
import com.google.gerrit.server.account.externalids.ExternalIdCache;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.io.IOException;
import java.util.Optional;

public class DisabledExternalIdCache implements ExternalIdCache {
  public static Module module() {
    return new AbstractModule() {

      @Override
      protected void configure() {
        bind(ExternalIdCache.class).to(DisabledExternalIdCache.class);
      }

      @Provides
      @Singleton
      Optional<ExternalIdCacheImpl> provideNoteDbExternalIdCacheImpl() {
        return Optional.empty();
      }
    };
  }

  @Override
  public Optional<ExternalId> byKey(ExternalId.Key key) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public ImmutableSet<ExternalId> byAccount(Account.Id accountId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ImmutableSetMultimap<Account.Id, ExternalId> allByAccount() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public ImmutableSetMultimap<String, ExternalId> byEmails(String... emails) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public ImmutableSetMultimap<String, ExternalId> allByEmail() throws IOException {
    throw new UnsupportedOperationException();
  }
}
