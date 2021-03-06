/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package signature.compare.model.impl;

import signature.compare.model.IAnnotationFieldDelta;
import signature.compare.model.IValueDelta;
import signature.model.IAnnotationField;

public class SigAnnotationFieldDelta extends SigMemberDelta<IAnnotationField>
        implements IAnnotationFieldDelta {

    private IValueDelta defaultValueDelta;

    public SigAnnotationFieldDelta(IAnnotationField from, IAnnotationField to) {
        super(from, to);
    }

    public IValueDelta getDefaultValueDelta() {
        return defaultValueDelta;
    }

    public void setDefaultValueDelta(IValueDelta valueDelta) {
        this.defaultValueDelta = valueDelta;
    }

}
