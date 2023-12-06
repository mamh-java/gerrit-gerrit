// Copyright (C) 2014 The Android Open Source Project
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

package com.google.gerrit.server;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gerrit.common.Nullable;
import com.google.gerrit.entities.Account;
import com.google.gerrit.entities.ChangeMessage;
import com.google.gerrit.entities.PatchSet;
import com.google.gerrit.extensions.common.ChangeMessageInfo;
import com.google.gerrit.server.account.AccountLoader;
import com.google.gerrit.server.notedb.ChangeNotes;
import com.google.gerrit.server.notedb.ChangeUpdate;
import com.google.gerrit.server.update.ChangeContext;
import com.google.gerrit.server.util.AccountTemplateUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;

/** Utility functions to manipulate {@link ChangeMessage}. */
@Singleton
public class ChangeMessagesUtil {
  public static final String AUTOGENERATED_TAG_PREFIX = "autogenerated:";
  public static final String AUTOGENERATED_BY_GERRIT_TAG_PREFIX =
      AUTOGENERATED_TAG_PREFIX + "gerrit:";

  public static final String TAG_ABANDON = AUTOGENERATED_BY_GERRIT_TAG_PREFIX + "abandon";
  public static final String TAG_CHERRY_PICK_CHANGE =
      AUTOGENERATED_BY_GERRIT_TAG_PREFIX + "cherryPickChange";
  public static final String TAG_DELETE_REVIEWER =
      AUTOGENERATED_BY_GERRIT_TAG_PREFIX + "deleteReviewer";
  public static final String TAG_DELETE_VOTE = AUTOGENERATED_BY_GERRIT_TAG_PREFIX + "deleteVote";
  public static final String TAG_MERGED = AUTOGENERATED_BY_GERRIT_TAG_PREFIX + "merged";
  public static final String TAG_MOVE = AUTOGENERATED_BY_GERRIT_TAG_PREFIX + "move";
  public static final String TAG_RESTORE = AUTOGENERATED_BY_GERRIT_TAG_PREFIX + "restore";
  public static final String TAG_REVERT = AUTOGENERATED_BY_GERRIT_TAG_PREFIX + "revert";
  public static final String TAG_UPDATE_ATTENTION_SET =
      AUTOGENERATED_BY_GERRIT_TAG_PREFIX + "updateAttentionSet";
  public static final String TAG_SET_DESCRIPTION =
      AUTOGENERATED_BY_GERRIT_TAG_PREFIX + "setPsDescription";
  public static final String TAG_SET_HASHTAGS = AUTOGENERATED_BY_GERRIT_TAG_PREFIX + "setHashtag";
  public static final String TAG_SET_PRIVATE = AUTOGENERATED_BY_GERRIT_TAG_PREFIX + "setPrivate";
  public static final String TAG_SET_READY =
      AUTOGENERATED_BY_GERRIT_TAG_PREFIX + "setReadyForReview";
  public static final String TAG_SET_TOPIC = AUTOGENERATED_BY_GERRIT_TAG_PREFIX + "setTopic";
  public static final String TAG_SET_WIP = AUTOGENERATED_BY_GERRIT_TAG_PREFIX + "setWorkInProgress";
  public static final String TAG_UNSET_PRIVATE =
      AUTOGENERATED_BY_GERRIT_TAG_PREFIX + "unsetPrivate";
  public static final String TAG_UPLOADED_PATCH_SET =
      AUTOGENERATED_BY_GERRIT_TAG_PREFIX + "newPatchSet";
  public static final String TAG_UPLOADED_WIP_PATCH_SET =
      AUTOGENERATED_BY_GERRIT_TAG_PREFIX + "newWipPatchSet";

  private final AccountTemplateUtil accountTemplateUtil;

  @Inject
  ChangeMessagesUtil(AccountTemplateUtil accountTemplateUtil) {
    this.accountTemplateUtil = accountTemplateUtil;
  }

  /**
   * Sets {@code messageTemplate} and {@code tag}, that should be applied by the {@code update}.
   *
   * <p>The {@code messageTemplate} is persisted in storage and should not contain user identifiable
   * information. See {@link ChangeMessage}.
   *
   * @param update update that sets {@code messageTemplate}.
   * @param messageTemplate message in template form, that should be applied by the update.
   * @param tag tag that should be applied by the update.
   * @return message built from {@code messageTemplate}. Templates are replaced, so it might contain
   *     user identifiable information.
   */
  @CanIgnoreReturnValue
  public String setChangeMessage(
      ChangeUpdate update, String messageTemplate, @Nullable String tag) {
    update.setChangeMessage(messageTemplate);
    update.setTag(tag);
    return accountTemplateUtil.replaceTemplates(messageTemplate);
  }

  /** See {@link #setChangeMessage(ChangeUpdate, String, String)}. */
  @CanIgnoreReturnValue
  public String setChangeMessage(ChangeContext ctx, String messageTemplate, @Nullable String tag) {
    return setChangeMessage(
        ctx.getUpdate(ctx.getChange().currentPatchSetId()), messageTemplate, tag);
  }

  public static String uploadedPatchSetTag(boolean workInProgress) {
    return workInProgress ? TAG_UPLOADED_WIP_PATCH_SET : TAG_UPLOADED_PATCH_SET;
  }

  /**
   * Returns {@link ChangeMessage}s from {@link ChangeNotes}, loads {@link ChangeNotes} from data
   * storage (cache or NoteDB), if it was not loaded yet.
   */
  public List<ChangeMessage> byChange(ChangeNotes notes) {
    return notes.load().getChangeMessages();
  }

  /**
   * Replace an existing change message with the provided new message.
   *
   * <p>The ID of a change message is different between NoteDb and ReviewDb. In NoteDb, it's the
   * commit SHA-1, but in ReviewDb it was generated randomly. Taking the target message as an index
   * rather than an ID allowed us to delete the message from both NoteDb and ReviewDb.
   *
   * @param update change update.
   * @param targetMessageId the id of the target change message.
   * @param newMessage the new message which is going to replace the old.
   */
  public void replaceChangeMessage(ChangeUpdate update, String targetMessageId, String newMessage) {
    update.deleteChangeMessageByRewritingHistory(targetMessageId, newMessage);
  }

  /**
   * Determines whether the tag starts with the autogenerated prefix
   *
   * @param tag value of a tag, or null.
   */
  public static boolean isAutogenerated(@Nullable String tag) {
    return tag != null && tag.startsWith(AUTOGENERATED_TAG_PREFIX);
  }

  public static boolean isAutogeneratedByGerrit(@Nullable String tag) {
    return tag != null && tag.startsWith(AUTOGENERATED_BY_GERRIT_TAG_PREFIX);
  }

  public static ChangeMessageInfo createChangeMessageInfo(
      ChangeMessage message, AccountLoader accountLoader) {
    PatchSet.Id patchNum = message.getPatchSetId();
    ChangeMessageInfo cmi = new ChangeMessageInfo();
    cmi.id = message.getKey().uuid();
    cmi.author = accountLoader.get(message.getAuthor());
    cmi.setDate(message.getWrittenOn());
    cmi.message = message.getMessage();
    cmi.tag = message.getTag();
    cmi._revisionNumber = patchNum != null ? patchNum.get() : null;
    Account.Id realAuthor = message.getRealAuthor();
    if (realAuthor != null && !realAuthor.equals(message.getAuthor())) {
      cmi.realAuthor = accountLoader.get(realAuthor);
    }
    cmi.accountsInMessage =
        AccountTemplateUtil.parseTemplates(message.getMessage()).stream()
            .map(accountLoader::get)
            .collect(toImmutableSet());
    return cmi;
  }

  /**
   * {@link ChangeMessage} is served in template form to {@link
   * com.google.gerrit.extensions.api.changes.ChangeApi}. Serve message with replaced templates to
   * the legacy {@link com.google.gerrit.extensions.api.changes.ChangeMessageApi} endpoints.
   * TODO(mariasavtchouk): remove this, after {@link
   * com.google.gerrit.extensions.api.changes.ChangeMessageApi} is deprecated (gate with
   * experiment).
   */
  public ChangeMessageInfo createChangeMessageInfoWithReplacedTemplates(
      ChangeMessage message, AccountLoader accountLoader) {
    ChangeMessageInfo changeMessageInfo = createChangeMessageInfo(message, accountLoader);
    changeMessageInfo.message = accountTemplateUtil.replaceTemplates(message.getMessage());
    return changeMessageInfo;
  }
}
