// Copyright (C) 2025 The Android Open Source Project
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

package com.google.gerrit.acceptance.api.change;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.gerrit.acceptance.AbstractDaemonTest;
import com.google.gerrit.acceptance.ExtensionRegistry;
import com.google.gerrit.acceptance.ExtensionRegistry.Registration;
import com.google.gerrit.acceptance.NoHttpd;
import com.google.gerrit.acceptance.PushOneCommit;
import com.google.gerrit.acceptance.TestAccount;
import com.google.gerrit.acceptance.TestExtensions.TestPluginPushOption;
import com.google.gerrit.extensions.common.ValidationOptionInfo;
import com.google.gerrit.extensions.common.ValidationOptionInfos;
import com.google.gerrit.server.PluginPushOption;
import com.google.inject.Inject;
import org.junit.Test;

@NoHttpd
public class ValidationOptionsIT extends AbstractDaemonTest {

  @Inject private ExtensionRegistry extensionRegistry;

  @Test
  public void getValidationOptions() throws Exception {
    PluginPushOption fooOption = new TestPluginPushOption("foo", "some description", true);
    PluginPushOption barOption = new TestPluginPushOption("bar", "other description", true);
    PluginPushOption disableBazOption = new TestPluginPushOption("baz", "other description", false);

    TestAccount admin = this.accountCreator.admin();
    String filename = "foo";
    PushOneCommit push =
        pushFactory.create(admin.newIdent(), testRepo, "subject1", filename, "contentold");
    PushOneCommit.Result result = push.to("refs/for/master");
    result.assertOkStatus();
    String changeId = result.getChangeId();

    try (Registration registration =
        extensionRegistry.newRegistration().add(fooOption).add(barOption).add(disableBazOption)) {
      ValidationOptionInfos validationOptionsInfos =
          gApi.changes().id(changeId).getValidationOptions();
      assertThat(validationOptionsInfos.validationOptions)
          .isEqualTo(
              ImmutableList.of(
                  new ValidationOptionInfo("foo", "some description"),
                  new ValidationOptionInfo("bar", "other description")));
    }
  }
}
