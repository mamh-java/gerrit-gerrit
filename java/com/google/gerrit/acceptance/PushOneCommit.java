import com.google.gerrit.server.approval.ApprovalsUtil;
import org.eclipse.jgit.dircache.DirCacheEditor.PathEdit;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevBlob;
  public PushOneCommit noParent() throws Exception {
    commitBuilder.noParents();
    return this;
  }

  public PushOneCommit addSymlink(String path, String target) throws Exception {
    RevBlob blobId = testRepo.blob(target);
    commitBuilder.edit(
        new PathEdit(path) {
          @Override
          public void apply(DirCacheEntry ent) {
            ent.setFileMode(FileMode.SYMLINK);
            ent.setObjectId(blobId);
          }
        });
    return this;
  }

  public PushOneCommit addGitSubmodule(String modulePath, ObjectId commitId) {
    commitBuilder.edit(
        new PathEdit(modulePath) {
          @Override
          public void apply(DirCacheEntry ent) {
            ent.setFileMode(FileMode.GITLINK);
            ent.setObjectId(commitId);
          }
        });
    return this;
  }

  public PushOneCommit rmFile(String filename) {
    commitBuilder.rm(filename);
    return this;
  }
