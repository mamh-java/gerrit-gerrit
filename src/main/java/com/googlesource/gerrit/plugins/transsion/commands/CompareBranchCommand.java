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
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

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

  @Option(name = "--alias", usage = "use native git comamnd or git alias command")
  private boolean alias;

  @Option(name = "--new",metaVar = "revision", required = true, usage = "the new revision")
  private String newRevision;

  @Option(name = "--old", metaVar = "revision", required = true, usage = "the old revision")
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
        List<String> logargv = new ArrayList<>();
        logargv.add("git");
        logargv.add("--no-pager");
        logargv.add("-C");
        logargv.add(directory);
        logargv.add("log");
        logargv.add("--left-right");
        logargv.add("--cherry-pick");
        logargv.add("--date=short");
        logargv.add("--pretty='%m  %h ||  %<(120,trunc)%s ||  (%<(10,trunc)%an) ||  (%cd)'");
        logargv.add(newRevision + "..." + oldRevision);

        List<String> cmpargv = new ArrayList<>();
        cmpargv.add("git");
        cmpargv.add("--no-pager");
        cmpargv.add("-C");
        cmpargv.add(directory);
        cmpargv.add("cmp");
        cmpargv.add(newRevision);
        cmpargv.add(oldRevision);

        List<String> argv;
        if(alias){
          argv = cmpargv;
        }else {
          argv = logargv;
        }
        ProcessBuilder pb = new ProcessBuilder(argv);
        pb.redirectErrorStream(true);

        Process ps = pb.start();

        ps.getOutputStream().close();
        String out = new String(ByteStreams.toByteArray(ps.getInputStream()), UTF_8);
        ps.waitFor();

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
