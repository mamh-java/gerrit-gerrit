// Copyright (C) 2016 The Android Open Source Project
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

package com.googlesource.gerrit.plugins.transsion.commands;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gerrit.common.Nullable;
import com.google.gerrit.extensions.common.AccountInfo;
import com.google.gerrit.extensions.common.ApprovalInfo;
import com.google.gerrit.extensions.common.ChangeInfo;
import com.google.gerrit.reviewdb.client.Account;
import com.google.gerrit.server.IdentifiedUser;
import com.google.gerrit.server.config.CanonicalWebUrl;
import com.google.gerrit.server.config.SitePaths;
import com.google.gerrit.server.git.GitRepositoryManager;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.MoreObjects.firstNonNull;

public class HookArgs {
  public interface Factory {
    HookArgs create();
  }

  final IdentifiedUser.GenericFactory identifiedUserFactory;
  final Provider<String> urlProvider;
  final HookMetrics metrics;
  final GitRepositoryManager gitManager;
  final SitePaths sitePaths;

  private final List<String> args;

  @Inject
  HookArgs(
      IdentifiedUser.GenericFactory identifiedUserFactory,
      @CanonicalWebUrl @Nullable Provider<String> urlProvider,
      HookMetrics metrics,
      GitRepositoryManager gitManager,
      SitePaths sitePaths) {
    this.identifiedUserFactory = identifiedUserFactory;
    this.urlProvider = urlProvider;
    this.metrics = metrics;
    this.gitManager = gitManager;
    this.sitePaths = sitePaths;

    this.args = new ArrayList<>();
  }

  public ImmutableList<String> get() {
    return ImmutableList.copyOf(args);
  }

  public void add(String name, String value) {
    args.add(name);
    args.add(Strings.nullToEmpty(value));
  }

  public void add(String name, int value) {
    args.add(name);
    args.add(String.valueOf(value));
  }

  public void add(String name, Integer value) {
    args.add(name);
    args.add(value.toString());
  }

  public void add(String name, Boolean value) {
    args.add(name);
    args.add(firstNonNull(value, false).toString());
  }

  public void add(String name, AccountInfo account) {
    if (account != null) {
      args.add(name);
      args.add(format(account));

      add(name + "-username", account.username);
    }
  }

  public void addUrl(ChangeInfo change) {
    args.add("--change-url");
    String url = urlProvider.get();
    if (change != null && url != null) {
      args.add(url + change._number);
    } else {
      args.add("");
    }
  }

  public void addApprovals(
      Map<String, ApprovalInfo> approvals, Map<String, ApprovalInfo> oldApprovals) {
    for (Map.Entry<String, ApprovalInfo> approval : approvals.entrySet()) {
      if (approval.getValue() != null) {
        args.add("--" + approval.getKey());
        args.add(approval.getValue().value.toString());
        if (oldApprovals != null && !oldApprovals.isEmpty()) {
          ApprovalInfo oldValue = oldApprovals.get(approval.getKey());
          if (oldValue != null && oldValue.value != null) {
            args.add("--" + approval.getKey() + "-oldValue");
            args.add(oldValue.value.toString());
          }
        }
      }
    }
  }

  private String format(AccountInfo account) {
    return String.format(
        "\"%s\"", identifiedUserFactory.create(new Account.Id(account._accountId)).getNameEmail());
  }

  @Override
  public String toString() {
    return String.format("HookArgs: %s", args.toString());
  }
}
