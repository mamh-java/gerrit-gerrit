/**
 * @license
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const testFiles = [];
const scriptsPath = '../scripts/';
const elementsPath = '../elements/';
const behaviorsPath = '../behaviors/';
const servicesPath = '../services/';

// Elements tests.
/* eslint-disable max-len */
const elements = [
  // This seemed to be flaky when it was farther down the list. Keep at the
  // beginning.
  'gr-app_test.html',
  'admin/gr-admin-group-list/gr-admin-group-list_test.html',
  'admin/gr-admin-view/gr-admin-view_test.html',
  'admin/gr-confirm-delete-item-dialog/gr-confirm-delete-item-dialog_test.html',
  'admin/gr-create-change-dialog/gr-create-change-dialog_test.html',
  'admin/gr-create-group-dialog/gr-create-group-dialog_test.html',
  'admin/gr-create-pointer-dialog/gr-create-pointer-dialog_test.html',
  'admin/gr-create-repo-dialog/gr-create-repo-dialog_test.html',
  'admin/gr-group-audit-log/gr-group-audit-log_test.html',
  'admin/gr-group-members/gr-group-members_test.html',
  'admin/gr-group/gr-group_test.html',
  'admin/gr-permission/gr-permission_test.html',
  'admin/gr-plugin-config-array-editor/gr-plugin-config-array-editor_test.html',
  'admin/gr-plugin-list/gr-plugin-list_test.html',
  'admin/gr-repo-access/gr-repo-access_test.html',
  'admin/gr-repo-commands/gr-repo-commands_test.html',
  'admin/gr-repo-dashboards/gr-repo-dashboards_test.html',
  'admin/gr-repo-detail-list/gr-repo-detail-list_test.html',
  'admin/gr-repo-list/gr-repo-list_test.html',
  'admin/gr-repo-plugin-config/gr-repo-plugin-config_test.html',
  'admin/gr-repo/gr-repo_test.html',
  'admin/gr-rule-editor/gr-rule-editor_test.html',
  'change-list/gr-change-list-item/gr-change-list-item_test.html',
  'change-list/gr-change-list-view/gr-change-list-view_test.html',
  'change-list/gr-change-list/gr-change-list_test.html',
  'change-list/gr-create-commands-dialog/gr-create-commands-dialog_test.html',
  'change-list/gr-create-change-help/gr-create-change-help_test.html',
  'change-list/gr-dashboard-view/gr-dashboard-view_test.html',
  'change-list/gr-repo-header/gr-repo-header_test.html',
  'change-list/gr-user-header/gr-user-header_test.html',
  'change/gr-change-actions/gr-change-actions_test.html',
  'change/gr-change-requirements/gr-change-requirements_test.html',
  'change/gr-comment-list/gr-comment-list_test.html',
  'change/gr-commit-info/gr-commit-info_test.html',
  'change/gr-confirm-abandon-dialog/gr-confirm-abandon-dialog_test.html',
  'change/gr-confirm-cherrypick-dialog/gr-confirm-cherrypick-dialog_test.html',
  'change/gr-confirm-cherrypick-conflict-dialog/gr-confirm-cherrypick-conflict-dialog_test.html',
  'change/gr-confirm-move-dialog/gr-confirm-move-dialog_test.html',
  'change/gr-confirm-rebase-dialog/gr-confirm-rebase-dialog_test.html',
  'change/gr-confirm-revert-dialog/gr-confirm-revert-dialog_test.html',
  'change/gr-confirm-revert-submission-dialog/gr-confirm-revert-submission-dialog_test.html',
  'change/gr-confirm-submit-dialog/gr-confirm-submit-dialog_test.html',
  'change/gr-download-dialog/gr-download-dialog_test.html',
  'change/gr-file-list-header/gr-file-list-header_test.html',
  'change/gr-file-list/gr-file-list_test.html',
  'change/gr-included-in-dialog/gr-included-in-dialog_test.html',
  'change/gr-label-score-row/gr-label-score-row_test.html',
  'change/gr-label-scores/gr-label-scores_test.html',
  'change/gr-message/gr-message_test.html',
  'change/gr-messages-list/gr-messages-list_test.html',
  'change/gr-messages-list/gr-messages-list-experimental_test.html',
  'change/gr-related-changes-list/gr-related-changes-list_test.html',
  'change/gr-reply-dialog/gr-reply-dialog_test.html',
  'change/gr-reviewer-list/gr-reviewer-list_test.html',
  'change/gr-thread-list/gr-thread-list_test.html',
  'change/gr-upload-help-dialog/gr-upload-help-dialog_test.html',
  'core/gr-account-dropdown/gr-account-dropdown_test.html',
  'core/gr-error-dialog/gr-error-dialog_test.html',
  'core/gr-error-manager/gr-error-manager_test.html',
  'core/gr-key-binding-display/gr-key-binding-display_test.html',
  'core/gr-keyboard-shortcuts-dialog/gr-keyboard-shortcuts-dialog_test.html',
  'core/gr-main-header/gr-main-header_test.html',
  'core/gr-navigation/gr-navigation_test.html',
  'core/gr-router/gr-router_test.html',
  'core/gr-search-bar/gr-search-bar_test.html',
  'core/gr-smart-search/gr-smart-search_test.html',
  'diff/gr-comment-api/gr-comment-api_test.html',
  'diff/gr-coverage-layer/gr-coverage-layer_test.html',
  'diff/gr-diff-builder/gr-diff-builder-element_test.html',
  'diff/gr-diff-builder/gr-diff-builder-unified_test.html',
  'diff/gr-diff-cursor/gr-diff-cursor_test.html',
  'diff/gr-diff-highlight/gr-annotation_test.html',
  'diff/gr-diff-highlight/gr-diff-highlight_test.html',
  'diff/gr-diff-host/gr-diff-host_test.html',
  'diff/gr-diff-mode-selector/gr-diff-mode-selector_test.html',
  'diff/gr-diff-preferences-dialog/gr-diff-preferences-dialog_test.html',
  'diff/gr-diff-processor/gr-diff-processor_test.html',
  'diff/gr-diff-selection/gr-diff-selection_test.html',
  'diff/gr-diff-view/gr-diff-view_test.html',
  'diff/gr-diff/gr-diff-group_test.html',
  'diff/gr-apply-fix-dialog/gr-apply-fix-dialog_test.html',
  'diff/gr-patch-range-select/gr-patch-range-select_test.html',
  'diff/gr-ranged-comment-layer/gr-ranged-comment-layer_test.html',
  'diff/gr-selection-action-box/gr-selection-action-box_test.html',
  'diff/gr-syntax-layer/gr-syntax-layer_test.html',
  'documentation/gr-documentation-search/gr-documentation-search_test.html',
  'edit/gr-default-editor/gr-default-editor_test.html',
  'edit/gr-edit-controls/gr-edit-controls_test.html',
  'edit/gr-edit-file-controls/gr-edit-file-controls_test.html',
  'edit/gr-editor-view/gr-editor-view_test.html',
  'plugins/gr-admin-api/gr-admin-api_test.html',
  'plugins/gr-styles-api/gr-styles-api_test.html',
  'plugins/gr-attribute-helper/gr-attribute-helper_test.html',
  'plugins/gr-dom-hooks/gr-dom-hooks_test.html',
  'plugins/gr-event-helper/gr-event-helper_test.html',
  'plugins/gr-plugin-host/gr-plugin-host_test.html',
  'plugins/gr-popup-interface/gr-plugin-popup_test.html',
  'plugins/gr-popup-interface/gr-popup-interface_test.html',
  'plugins/gr-repo-api/gr-repo-api_test.html',
  'plugins/gr-settings-api/gr-settings-api_test.html',
  'plugins/gr-theme-api/gr-theme-api_test.html',
  'settings/gr-account-info/gr-account-info_test.html',
  'settings/gr-agreements-list/gr-agreements-list_test.html',
  'settings/gr-change-table-editor/gr-change-table-editor_test.html',
  'settings/gr-cla-view/gr-cla-view_test.html',
  'settings/gr-edit-preferences/gr-edit-preferences_test.html',
  'settings/gr-email-editor/gr-email-editor_test.html',
  'settings/gr-gpg-editor/gr-gpg-editor_test.html',
  'settings/gr-group-list/gr-group-list_test.html',
  'settings/gr-http-password/gr-http-password_test.html',
  'settings/gr-identities/gr-identities_test.html',
  'settings/gr-menu-editor/gr-menu-editor_test.html',
  'settings/gr-registration-dialog/gr-registration-dialog_test.html',
  'settings/gr-ssh-editor/gr-ssh-editor_test.html',
  'settings/gr-watched-projects-editor/gr-watched-projects-editor_test.html',
  'shared/gr-account-entry/gr-account-entry_test.html',
  'shared/gr-account-label/gr-account-label_test.html',
  'shared/gr-account-list/gr-account-list_test.html',
  'shared/gr-account-link/gr-account-link_test.html',
  'shared/gr-alert/gr-alert_test.html',
  'shared/gr-autocomplete-dropdown/gr-autocomplete-dropdown_test.html',
  'shared/gr-avatar/gr-avatar_test.html',
  'shared/gr-button/gr-button_test.html',
  'shared/gr-change-star/gr-change-star_test.html',
  'shared/gr-change-status/gr-change-status_test.html',
  'shared/gr-comment-thread/gr-comment-thread_test.html',
  'shared/gr-comment/gr-comment_test.html',
  'shared/gr-copy-clipboard/gr-copy-clipboard_test.html',
  'shared/gr-count-string-formatter/gr-count-string-formatter_test.html',
  'shared/gr-date-formatter/gr-date-formatter_test.html',
  'shared/gr-dialog/gr-dialog_test.html',
  'shared/gr-diff-preferences/gr-diff-preferences_test.html',
  'shared/gr-download-commands/gr-download-commands_test.html',
  'shared/gr-dropdown/gr-dropdown_test.html',
  'shared/gr-dropdown-list/gr-dropdown-list_test.html',
  'shared/gr-editable-content/gr-editable-content_test.html',
  'shared/gr-editable-label/gr-editable-label_test.html',
  'shared/gr-formatted-text/gr-formatted-text_test.html',
  'shared/gr-hovercard/gr-hovercard_test.html',
  'shared/gr-hovercard-account/gr-hovercard-account_test.html',
  'shared/gr-js-api-interface/gr-annotation-actions-context_test.html',
  'shared/gr-js-api-interface/gr-annotation-actions-js-api_test.html',
  'shared/gr-js-api-interface/gr-change-actions-js-api_test.html',
  'shared/gr-js-api-interface/gr-change-reply-js-api_test.html',
  'shared/gr-js-api-interface/gr-api-utils_test.html',
  'shared/gr-js-api-interface/gr-js-api-interface_test.html',
  'shared/gr-js-api-interface/gr-gerrit_test.html',
  'shared/gr-js-api-interface/gr-plugin-action-context_test.html',
  'shared/gr-js-api-interface/gr-plugin-loader_test.html',
  'shared/gr-js-api-interface/gr-plugin-rest-api_test.html',
  'shared/gr-fixed-panel/gr-fixed-panel_test.html',
  'shared/gr-labeled-autocomplete/gr-labeled-autocomplete_test.html',
  'shared/gr-label-info/gr-label-info_test.html',
  'shared/gr-limited-text/gr-limited-text_test.html',
  'shared/gr-linked-chip/gr-linked-chip_test.html',
  'shared/gr-linked-text/gr-linked-text_test.html',
  'shared/gr-list-view/gr-list-view_test.html',
  'shared/gr-overlay/gr-overlay_test.html',
  'shared/gr-page-nav/gr-page-nav_test.html',
  'shared/gr-repo-branch-picker/gr-repo-branch-picker_test.html',
  'shared/gr-rest-api-interface/gr-auth_test.html',
  'shared/gr-rest-api-interface/gr-etag-decorator_test.html',
  'shared/gr-rest-api-interface/gr-rest-api-interface_test.html',
  'shared/gr-rest-api-interface/gr-reviewer-updates-parser_test.html',
  'shared/gr-rest-api-interface/gr-rest-apis/gr-rest-api-helper_test.html',
  'shared/gr-select/gr-select_test.html',
  'shared/gr-shell-command/gr-shell-command_test.html',
  'shared/gr-storage/gr-storage_test.html',
  'shared/gr-textarea/gr-textarea_test.html',
  'shared/gr-tooltip-content/gr-tooltip-content_test.html',
  'shared/gr-tooltip/gr-tooltip_test.html',
  'shared/revision-info/revision-info_test.html',
];
/* eslint-enable max-len */
for (let file of elements) {
  file = elementsPath + file;
  testFiles.push(file);
}

// Behaviors tests.
/* eslint-disable max-len */
const behaviors = [
  'docs-url-behavior/docs-url-behavior_test.html',
  'dom-util-behavior/dom-util-behavior_test.html',
  'keyboard-shortcut-behavior/keyboard-shortcut-behavior_test.html',
  'rest-client-behavior/rest-client-behavior_test.html',
  'gr-access-behavior/gr-access-behavior_test.html',
  'gr-admin-nav-behavior/gr-admin-nav-behavior_test.html',
  'gr-change-table-behavior/gr-change-table-behavior_test.html',
  'gr-list-view-behavior/gr-list-view-behavior_test.html',
  'gr-display-name-behavior/gr-display-name-behavior_test.html',
  'gr-patch-set-behavior/gr-patch-set-behavior_test.html',
  'gr-path-list-behavior/gr-path-list-behavior_test.html',
  'gr-tooltip-behavior/gr-tooltip-behavior_test.html',
  'gr-url-encoding-behavior/gr-url-encoding-behavior_test.html',
  'safe-types-behavior/safe-types-behavior_test.html',
];
/* eslint-enable max-len */
for (let file of behaviors) {
  // Behaviors do not utilize the DOM, so no shadow DOM test is necessary.
  file = behaviorsPath + file;
  testFiles.push(file);
}

const scripts = [
  'gr-reviewer-suggestions-provider/gr-reviewer-suggestions-provider_test.html',
  'gr-group-suggestions-provider/gr-group-suggestions-provider_test.html',
  'gr-display-name-utils/gr-display-name-utils_test.html',
  'gr-email-suggestions-provider/gr-email-suggestions-provider_test.html',
];
/* eslint-enable max-len */
for (let file of scripts) {
  file = scriptsPath + file;
  testFiles.push(file);
}

const services = [
  'app-context-init_test.html',
  'flags_test.html',
  'gr-reporting/gr-reporting_test.html',
  'gr-reporting/gr-reporting_mock_test.html',
  'gr-event-interface/gr-event-interface_test.html',
];
for (let file of services) {
  file = servicesPath + file;
  testFiles.push(file);
}

// embed test
testFiles.push('../embed/gr-diff-app-context-init_test.html');

/**
 * Converts multiline string to a map<file_name, test_count>.
 *
 * @param {number} testsPerFileString - multiline input string in the following format:
 *   fileName1:test_count1
 *   fileName2:test_count2
 *   ...
 *   fileName3:test_count3
 * @return Object<string, number> - key is the test file name, value is the number of tests
 */
function parseTestsPerFileString(testsPerFileString) {
  return testsPerFileString.split('\n').map(s => s.trim().replace('./', '../'))
      .reduce((acc, fileAndCount) => {
        const [file, countStr] = fileAndCount.split(':');
        acc[file] = parseInt(countStr);
        return acc;
      }, {});
}

const defaultTestsPerFile = [];

function getBucketWithMinTests(buckets) {
  let minBucket = buckets[0];
  for (let i = 1; i < buckets.length; i++) {
    if (buckets[i].count < minBucket.count) {
      minBucket = buckets[i];
    }
  }
  return minBucket;
}

/**
 * Split testFiles among all buckets. The greedy algorithm is used,
 * because we don't need accurate splitting
 */
function splitTestsByBuckets(buckets, testsPerFile) {
  for (const testFile of testFiles) {
    const testsInFile = testsPerFile[testFile] ?
      testsPerFile[testFile] : defaultTestsPerFile;
    const minBucket = getBucketWithMinTests(buckets);
    minBucket.count += testsInFile;
    minBucket.items.push(testFile);
  }
}

/**
 * Returns list of test files for specified splitIndex
 *
 * @param {string} testsPerFileString - information about number of tests in each file
 *  (see suite_conf.js for exact format)
 * @param {number} splitIndex - index of split to return (0<=splitIndex<splitCount)
 * @param {number} splitCount - total number of splits
 * @return Array<string> - list of test files
 */
export function getSuiteTests(testsPerFileString, splitIndex, splitCount) {
  const testsPerFile = parseTestsPerFileString(testsPerFileString);
  const buckets = [];
  for (let i = 0; i < splitCount; i++) {
    buckets.push({count: 0, items: []});
  }
  // TODO(dmfilippov): split tests by buckets only once
  // This doesn't affect overall performance, so we can keep it
  // while we have only small amounts of test files.
  splitTestsByBuckets(buckets, testsPerFile);
  console.log(buckets);
  return buckets[splitIndex].items;
}

