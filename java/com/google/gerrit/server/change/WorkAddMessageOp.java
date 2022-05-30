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

package com.google.gerrit.server.change;

import com.google.common.collect.ImmutableMap;
import com.google.common.base.Strings;
import com.google.gerrit.common.Nullable;
import com.google.gerrit.entities.Change;
import com.google.gerrit.entities.HumanComment;
import com.google.gerrit.entities.ChangeMessage;
import com.google.gerrit.entities.PatchSet;
import com.google.gerrit.extensions.api.changes.NotifyHandling;
import com.google.gerrit.extensions.common.InputWithMessage;
import com.google.gerrit.server.extensions.events.CommentAdded;
import com.google.gerrit.server.ChangeMessagesUtil;
import com.google.gerrit.server.PatchSetUtil;
import com.google.gerrit.server.CommentsUtil;
import com.google.gerrit.server.PublishCommentUtil;
import com.google.gerrit.server.notedb.ChangeNotes;
import com.google.gerrit.server.notedb.ChangeUpdate;
import com.google.gerrit.server.update.BatchUpdateOp;
import com.google.gerrit.server.update.ChangeContext;
import com.google.gerrit.server.update.Context;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorkAddMessageOp implements BatchUpdateOp {
  public static class Input extends InputWithMessage {
    @Nullable public NotifyHandling notify;

    public Input() {
      this(null);
    }

    public Input(@Nullable String message) {
      super(message);
    }
  }

  public interface Factory {
    WorkAddMessageOp create(String msg, Input in);
  }

  private final ChangeMessagesUtil cmUtil;
  private final String message;
  private final Input in;
  private final CommentAdded commentAdded;
  private final PatchSetUtil psUtil;
  private final CommentsUtil commentsUtil;
  private final PublishCommentUtil publishCommentUtil;

  private Change change;
  private PatchSet ps;
  private ChangeNotes notes;
  private ChangeMessage cmsg;
  private List<HumanComment> comments = new ArrayList<>();

  @Inject
  WorkAddMessageOp(
      ChangeMessagesUtil cmUtil,
      PatchSetUtil psUtil,
      CommentAdded commentAdded,
      CommentsUtil commentsUtil,
      PublishCommentUtil publishCommentUtil,
      @Assisted String message,
      @Assisted Input in) {
    this.cmUtil = cmUtil;
    this.psUtil = psUtil;
    this.message = message;
    this.in = in;
    this.commentAdded = commentAdded;
    this.commentsUtil = commentsUtil;
    this.publishCommentUtil = publishCommentUtil;

  }

  @Override
  public boolean updateChange(ChangeContext ctx) {
    change = ctx.getChange();
    notes = ctx.getNotes();
    ps = psUtil.get(ctx.getNotes(), change.currentPatchSetId());

    comments = commentsUtil.draftByChangeAuthor(ctx.getNotes(), ctx.getUser().getAccountId());


    ChangeUpdate update = ctx.getUpdate(change.currentPatchSetId());
    publishCommentUtil.publish(ctx, update, comments, null);

    addMessage(ctx, update);
    return true;
  }

  private void addMessage(ChangeContext ctx, ChangeUpdate update) {
    Change c = ctx.getChange();
    StringBuilder buf = new StringBuilder(this.message);

    String m = Strings.nullToEmpty(in == null ? null : in.message).trim();
    if (!m.isEmpty()) {
      buf.append("\n\n");
      buf.append(m);
    }

    cmsg = 
      ChangeMessagesUtil.newMessage(
        ctx, buf.toString(), ChangeMessagesUtil.TAG_SET_STARTGB);

    cmUtil.addChangeMessage(update, cmsg);
  }

  @Override
  public void postUpdate(Context ctx) {
    if (cmsg == null || change == null || ps == null) {
      return;
    }

    commentAdded.fire(
        change,
        ps,
        ctx.getAccount(),
        cmsg.getMessage(),
        ImmutableMap.of(),
        ImmutableMap.of(),
        ctx.getWhen());
  }
}
