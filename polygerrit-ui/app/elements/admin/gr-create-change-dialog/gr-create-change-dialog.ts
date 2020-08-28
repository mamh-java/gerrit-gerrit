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
import '@polymer/iron-input/iron-input';
import '../../../styles/gr-form-styles';
import '../../../styles/shared-styles';
import '../../shared/gr-autocomplete/gr-autocomplete';
import '../../shared/gr-button/gr-button';
import '../../shared/gr-rest-api-interface/gr-rest-api-interface';
import '../../shared/gr-select/gr-select';
import {GestureEventListeners} from '@polymer/polymer/lib/mixins/gesture-event-listeners';
import {LegacyElementMixin} from '@polymer/polymer/lib/legacy/legacy-element-mixin';
import {PolymerElement} from '@polymer/polymer/polymer-element';
import {htmlTemplate} from './gr-create-change-dialog_html';
import {GerritNav} from '../../core/gr-navigation/gr-navigation';
import {customElement, property, observe} from '@polymer/decorators';
import {
  RepoName,
  BranchName,
  ChangeId,
  ConfigInfo,
  InheritedBooleanInfo,
} from '../../../types/common';
import {InheritedBooleanInfoConfiguredValue} from '../../../constants/constants';
import {hasOwnProperty} from '../../../utils/common-util';
import {RestApiService} from '../../../services/services/gr-rest-api/gr-rest-api';

const SUGGESTIONS_LIMIT = 15;
const REF_PREFIX = 'refs/heads/';

export interface GrCreateChangeDialog {
  $: {
    restAPI: RestApiService & Element;
    privateChangeCheckBox: HTMLInputElement;
  };
}
@customElement('gr-create-change-dialog')
export class GrCreateChangeDialog extends GestureEventListeners(
  LegacyElementMixin(PolymerElement)
) {
  static get template() {
    return htmlTemplate;
  }

  @property({type: String})
  repoName?: RepoName;

  @property({type: String})
  branch?: BranchName;

  @property({type: Object})
  _repoConfig?: ConfigInfo;

  @property({type: String})
  subject?: string;

  @property({type: String})
  topic?: string;

  @property({type: Object})
  _query?: (input: string) => Promise<{name: string}[]>;

  @property({type: String})
  baseChange?: ChangeId;

  @property({type: String})
  baseCommit?: string;

  @property({type: Object})
  privateByDefault?: InheritedBooleanInfo;

  @property({type: Boolean, notify: true})
  canCreate = false;

  @property({type: Boolean})
  _privateChangesEnabled?: boolean;

  constructor() {
    super();
    this._query = this._getRepoBranchesSuggestions.bind(this);
  }

  /** @override */
  attached() {
    super.attached();
    if (!this.repoName) {
      return Promise.resolve();
    }

    const promises = [];

    promises.push(
      this.$.restAPI.getProjectConfig(this.repoName).then(config => {
        if (!config) return;
        this.privateByDefault = config.private_by_default;
      })
    );

    promises.push(
      this.$.restAPI.getConfig().then(config => {
        if (!config) {
          return;
        }

        this._privateChangesEnabled =
          config && config.change && !config.change.disable_private_changes;
      })
    );

    return Promise.all(promises);
  }

  _computeBranchClass(baseChange: boolean) {
    return baseChange ? 'hide' : '';
  }

  @observe('branch', 'subject')
  _allowCreate(branch: BranchName, subject: string) {
    this.canCreate = !!branch && !!subject;
  }

  handleCreateChange() {
    if (!this.repoName || !this.branch || !this.subject) return;
    const isPrivate = this.$.privateChangeCheckBox.checked;
    const isWip = true;
    return this.$.restAPI
      .createChange(
        this.repoName,
        this.branch,
        this.subject,
        this.topic,
        isPrivate,
        isWip,
        this.baseChange,
        this.baseCommit || undefined
      )
      .then(changeCreated => {
        if (!changeCreated) {
          return;
        }
        GerritNav.navigateToChange(changeCreated);
      });
  }

  _getRepoBranchesSuggestions(input: string) {
    if (!this.repoName) {
      return Promise.reject(new Error('missing repo name'));
    }
    if (input.startsWith(REF_PREFIX)) {
      input = input.substring(REF_PREFIX.length);
    }
    return this.$.restAPI
      .getRepoBranches(input, this.repoName, SUGGESTIONS_LIMIT)
      .then(response => {
        if (!response) return [];
        const branches = [];
        let branch;
        for (const key in response) {
          if (!hasOwnProperty(response, key)) {
            continue;
          }
          if (response[key].ref.startsWith('refs/heads/')) {
            branch = response[key].ref.substring('refs/heads/'.length);
          } else {
            branch = response[key].ref;
          }
          branches.push({
            name: branch,
          });
        }
        return branches;
      });
  }

  _formatBooleanString(config: InheritedBooleanInfo) {
    if (
      config &&
      config.configured_value === InheritedBooleanInfoConfiguredValue.TRUE
    ) {
      return true;
    } else if (
      config &&
      config.configured_value === InheritedBooleanInfoConfiguredValue.FALSE
    ) {
      return false;
    } else if (
      config &&
      config.configured_value === InheritedBooleanInfoConfiguredValue.INHERITED
    ) {
      return !!(config && config.inherited_value);
    } else {
      return false;
    }
  }

  _computePrivateSectionClass(config: boolean) {
    return config ? 'hide' : '';
  }
}

declare global {
  interface HTMLElementTagNameMap {
    'gr-create-change-dialog': GrCreateChangeDialog;
  }
}
