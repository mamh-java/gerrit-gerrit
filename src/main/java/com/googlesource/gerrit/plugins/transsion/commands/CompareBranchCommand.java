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

package com.googlesource.gerrit.plugins.transsion.commands;

import com.google.common.io.ByteStreams;
import com.google.gerrit.common.data.GlobalCapability;
import com.google.gerrit.extensions.annotations.RequiresCapability;
import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.IdentifiedUser;
import com.google.gerrit.server.config.SitePaths;
import com.google.gerrit.server.git.GitRepositoryManager;
import com.google.gerrit.server.git.VisibleRefFilter;
import com.google.gerrit.server.permissions.PermissionBackend;
import com.google.gerrit.server.project.ProjectControl;
import com.google.gerrit.server.util.ManualRequestContext;
import com.google.gerrit.server.util.OneOffRequestContext;
import com.google.gerrit.sshd.CommandMetaData;
import com.google.gerrit.sshd.SshCommand;
import com.google.gwtorm.server.OrmException;
import com.google.inject.Inject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.eclipse.jgit.lib.RefDatabase.ALL;

@RequiresCapability(GlobalCapability.ADMINISTRATE_SERVER)
@CommandMetaData(name = "compare-branch", description = "compare two branch revision tag by git log comamnd")
public final class CompareBranchCommand extends SshCommand {
  @Inject
  private IdentifiedUser currentUser;
  @Inject
  private PermissionBackend permissionBackend;
  @Inject
  private OneOffRequestContext requestContext;
  @Inject
  private VisibleRefFilter.Factory refFilterFactory;
  @Inject
  private GitRepositoryManager repoManager;
  @Inject
  private SitePaths sitePaths;

  @Option(
      name = "--project",
      aliases = {"-p"},
      metaVar = "PROJECT",
      required = true,
      usage = "project for compare")
  private ProjectControl projectControl;


  @Option(name = "--new",metaVar = "rev", required = true, usage = "the new revision")
  private String newRevision;

  @Option(name = "--old", metaVar = "rev", required = true, usage = "the old revision")
  private String oldRevision;


  @Override
  protected void run() throws Failure {
    if (currentUser == null) {
      stdout.print("No single user could be found when searching for: " + currentUser + '\n');
      stdout.flush();
      return;
    }



    Project.NameKey projectName = projectControl.getProject().getNameKey();
    try (Repository repo = repoManager.openRepository(projectName);
         ManualRequestContext ctx = requestContext.openAs(currentUser.getAccountId())) {
      try {
        String directory = repo.getDirectory().getAbsolutePath(); // the git directory
        List<String> argv = new ArrayList<>();
        argv.add("git");
        argv.add("--no-pager");
        argv.add("-C");
        argv.add(directory);
        argv.add("log");
        argv.add("--left-right");
        argv.add("--cherry-pick");
        argv.add("--date=short");
        argv.add("--pretty='%m || %h ||  %<(120,trunc)%s (%<(10,trunc)%an) (%cd)'");
        argv.add(newRevision + "..." + oldRevision);

        ProcessBuilder pb = new ProcessBuilder(argv);
        pb.redirectErrorStream(true);

        Map<String, String> env = pb.environment();
        env.put("GERRIT_SITE", sitePaths.site_path.toAbsolutePath().toString());

        Process ps = pb.start();

        ps.getOutputStream().close();
        String out = new String(ByteStreams.toByteArray(ps.getInputStream()), UTF_8);
        ps.waitFor();
        stdout.println(argv);
        stdout.println(out);
        //String cmd = "git --no-pager -C " + directory + " log --left-right --cherry-pick --date=short --pretty='%m || %h ||  %<(120,trunc)%s (%<(10,trunc)%an) (%cd)' '" + oldRevision + "'...'" + newRevision + "' ";
        ps.destroy();
      } catch (IOException e) {
        throw new Failure(1, "fatal: Error reading refs: '" + projectName, e);
      } catch (InterruptedException e) {
      }
    } catch (RepositoryNotFoundException e) {
      throw die("'" + projectName + "': not a git archive");
    } catch (IOException | OrmException e) {
      throw die("Error opening: '" + projectName);
    }
  }
}
