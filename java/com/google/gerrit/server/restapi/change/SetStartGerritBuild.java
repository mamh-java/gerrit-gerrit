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

package com.google.gerrit.server.restapi.change;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.gerrit.extensions.conditions.BooleanCondition.and;

import com.google.gerrit.entities.Change;
import com.google.gerrit.extensions.api.changes.NotifyHandling;
import com.google.gerrit.extensions.restapi.ResourceConflictException;
import com.google.gerrit.extensions.restapi.Response;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.gerrit.extensions.restapi.RestModifyView;
import com.google.gerrit.extensions.webui.UiAction;
import com.google.gerrit.server.ChangeUtil;
import com.google.gerrit.server.change.ChangeResource;
import com.google.gerrit.server.change.NotifyResolver;
import com.google.gerrit.server.change.WorkAddMessageOp;
import com.google.gerrit.server.change.WorkAddMessageOp.Input;
import com.google.gerrit.server.permissions.ChangePermission;
import com.google.gerrit.server.permissions.PermissionBackendException;
import com.google.gerrit.server.update.BatchUpdate;
import com.google.gerrit.server.update.UpdateException;
import com.google.gerrit.server.util.time.TimeUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SetStartGerritBuild
    implements RestModifyView<ChangeResource, Input>, UiAction<ChangeResource> {
  private final BatchUpdate.Factory updateFactory;
  private final WorkAddMessageOp.Factory opFactory;

  @Inject
  SetStartGerritBuild(BatchUpdate.Factory updateFactory, WorkAddMessageOp.Factory opFactory) {
    this.updateFactory = updateFactory;
    this.opFactory = opFactory;
  }

  @Override
  public Response<String> apply(ChangeResource rsrc, Input input)
      throws RestApiException, UpdateException, PermissionBackendException {
    rsrc.permissions().check(ChangePermission.TOGGLE_WORK_IN_PROGRESS_STATE);

    Change change = rsrc.getChange();
    if (!change.isNew()) {
      throw new ResourceConflictException("change is " + ChangeUtil.status(change));
    }

    try (BatchUpdate bu = updateFactory.create(rsrc.getProject(), rsrc.getUser(), TimeUtil.nowTs())) {
      
      bu.setNotify(NotifyResolver.Result.create(firstNonNull(input.notify, NotifyHandling.ALL)));
      
      bu.addOp(rsrc.getChange().getId(), opFactory.create("start_TriggerGerritBuild", input));
      
      bu.execute();
      
      return Response.ok();
    }
  }

  @Override
  public Description getDescription(ChangeResource rsrc) {
    return new Description()
        .setLabel("start gb")
        .setTitle("点我触发门禁！")
        .setVisible(and(
                rsrc.getChange().isNew(),
                rsrc.permissions().testCond(ChangePermission.READ)));
  }
}
