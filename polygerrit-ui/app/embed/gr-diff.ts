/**
 * @license
 * Copyright 2020 Google LLC
 * SPDX-License-Identifier: Apache-2.0
 */

// TODO(dmfilippov): remove bundled-polymer.js imports when the following issue
// https://github.com/Polymer/polymer-resin/issues/9 is resolved.
// Because gr-diff.js is a shared component, it shouldn' pollute global
// variables. If an application wants to use Polymer global variable -
// the app must assign/import it and do not rely on the Polymer variable
// exposed by shared gr-diff component.
import '../api/embed';
import '../scripts/bundled-polymer';
import './diff-old/gr-diff/gr-diff';
import './diff-old/gr-diff-cursor/gr-diff-cursor';
import './diff/gr-diff/gr-diff';
import './diff/gr-diff-cursor/gr-diff-cursor';
import {TokenHighlightLayer} from './diff/gr-diff-builder/token-highlight-layer';
import {GrDiffCursor as GrDiffCursorOld} from './diff-old/gr-diff-cursor/gr-diff-cursor';
import {GrDiffCursor as GrDiffCursorNew} from './diff/gr-diff-cursor/gr-diff-cursor';
import {GrAnnotation as GrAnnotationOld} from './diff-old/gr-diff-highlight/gr-annotation';
import {GrAnnotationImpl as GrAnnotationNew} from './diff/gr-diff-highlight/gr-annotation';
import {createDiffAppContext} from './gr-diff-app-context-init';
import {injectAppContext} from '../services/app-context';
import {isNewDiff} from './diff/gr-diff/gr-diff-utils';

// Setup appContext for diff.
// TODO (dmfilippov): find a better solution
injectAppContext(createDiffAppContext());
// Setup global variables for existing usages of this component
window.grdiff = {
  GrAnnotation: isNewDiff() ? GrAnnotationNew : GrAnnotationOld,
  GrDiffCursor: isNewDiff() ? GrDiffCursorNew : GrDiffCursorOld,
  TokenHighlightLayer,
};
