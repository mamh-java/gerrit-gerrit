// Copyright (C) 2024 The Android Open Source Project
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

package com.google.gerrit.server.restapi.project;

import com.google.gerrit.extensions.common.BatchSubmitRequirementInput;
import com.google.gerrit.extensions.common.ChangeInfo;
import com.google.gerrit.extensions.restapi.Response;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.gerrit.extensions.restapi.RestModifyView;
import com.google.gerrit.server.permissions.PermissionBackendException;
import com.google.gerrit.server.project.ProjectConfig;
import com.google.gerrit.server.project.ProjectResource;
import com.google.gerrit.server.restapi.project.RepoMetaDataUpdater.ConfigChangeCreator;
import com.google.gerrit.server.update.UpdateException;
import com.google.inject.Inject;
import java.io.IOException;
import javax.inject.Singleton;
import org.eclipse.jgit.errors.ConfigInvalidException;

@Singleton
public class PostSubmitRequirementsReview
    implements RestModifyView<ProjectResource, BatchSubmitRequirementInput> {

  private final RepoMetaDataUpdater repoMetaDataUpdater;
  private final PostSubmitRequirements postSubmitRequirements;

  @Inject
  PostSubmitRequirementsReview(
      RepoMetaDataUpdater repoMetaDataUpdater, PostSubmitRequirements postSubmitRequirements) {
    this.repoMetaDataUpdater = repoMetaDataUpdater;
    this.postSubmitRequirements = postSubmitRequirements;
  }

  @Override
  public Response<ChangeInfo> apply(ProjectResource rsrc, BatchSubmitRequirementInput input)
      throws PermissionBackendException, IOException, ConfigInvalidException, UpdateException,
          RestApiException {
    try (ConfigChangeCreator creator =
        repoMetaDataUpdater.configChangeCreator(
            rsrc.getNameKey(), input.commitMessage, "Review submit requirements change")) {
      ProjectConfig config = creator.getConfig();
      var unused = postSubmitRequirements.updateProjectConfig(config, input);
      // If config isn't updated, the createChange throws BadRequestException. We don't need
      // to explicitly check the updateProjectConfig result here.
      return creator.createChange();
    }
  }
}
