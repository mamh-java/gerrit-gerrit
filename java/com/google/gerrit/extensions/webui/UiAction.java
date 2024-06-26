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

package com.google.gerrit.extensions.webui;

import com.google.common.collect.ImmutableList;
import com.google.gerrit.common.Nullable;
import com.google.gerrit.extensions.conditions.BooleanCondition;
import com.google.gerrit.extensions.restapi.RestResource;
import com.google.gerrit.extensions.restapi.RestView;
import java.util.ArrayList;
import java.util.List;

public interface UiAction<R extends RestResource> extends RestView<R> {
  /**
   * Get the description of the action customized for the resource.
   *
   * @param resource the resource the view would act upon if the action is invoked by the client.
   *     Information from the resource can be used to customize the description.
   * @return a description of the action. The server will populate the {@code id} and {@code method}
   *     properties. If null the action will assumed unavailable and not presented. This is usually
   *     the same as {@code setVisible(false)}.
   */
  @Nullable
  Description getDescription(R resource) throws Exception;

  /** Describes an action invokable through the web interface. */
  class Description {
    private String method;
    private String id;
    private String label;
    private String title;
    private BooleanCondition visible = BooleanCondition.TRUE;
    private BooleanCondition enabled = BooleanCondition.TRUE;
    private List<String> enabledOptions = new ArrayList<>();

    public String getMethod() {
      return method;
    }

    /** {@code PrivateInternals_UiActionDescription.setMethod()} */
    void setMethod(String method) {
      this.method = method;
    }

    public String getId() {
      return id;
    }

    /** {@code PrivateInternals_UiActionDescription.setId()} */
    void setId(String id) {
      this.id = id;
    }

    public String getLabel() {
      return label;
    }

    /** Set the label to appear on the button to activate this action. */
    public Description setLabel(String label) {
      this.label = label;
      return this;
    }

    public String getTitle() {
      return title;
    }

    /** Set the tool-tip text to appear when the mouse hovers on the button. */
    public Description setTitle(String title) {
      this.title = title;
      return this;
    }

    public boolean isVisible() {
      return getVisibleCondition().value();
    }

    public BooleanCondition getVisibleCondition() {
      return visible;
    }

    /**
     * Set if the action's button is visible on screen for the current client. If not visible the
     * action description may not be sent to the client.
     */
    public Description setVisible(boolean visible) {
      return setVisible(BooleanCondition.valueOf(visible));
    }

    /**
     * Set if the action's button is visible on screen for the current client. If not visible the
     * action description may not be sent to the client.
     */
    public Description setVisible(BooleanCondition visible) {
      this.visible = visible;
      return this;
    }

    public boolean isEnabled() {
      return getEnabledCondition().value();
    }

    public BooleanCondition getEnabledCondition() {
      return BooleanCondition.and(enabled, visible);
    }

    /** Set if the button should be invokable (true), or greyed out (false). */
    public Description setEnabled(boolean enabled) {
      return setEnabled(BooleanCondition.valueOf(enabled));
    }

    /** Set if the button should be invokable (true), or greyed out (false). */
    public Description setEnabled(BooleanCondition enabled) {
      this.enabled = enabled;
      return this;
    }

    @Nullable
    public ImmutableList<String> getEnabledOptions() {
      if (enabledOptions.isEmpty()) {
        return null;
      }
      return ImmutableList.copyOf(enabledOptions);
    }

    public Description setOption(String optionName, boolean enabled) {
      if (enabled) {
        enabledOptions.add(optionName);
      } else {
        enabledOptions.remove(optionName);
      }
      return this;
    }
  }
}
