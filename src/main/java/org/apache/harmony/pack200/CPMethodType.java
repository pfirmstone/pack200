/*
 * Copyright 2018 peter.
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
package org.apache.harmony.pack200;

class CPMethodType extends ConstantPoolEntry implements Comparable {

    private final CPSignature signature;

    CPMethodType(CPSignature signature) {
        this.signature = signature;
    }

    public CPSignature getSignature() { return signature; }

    /** Index of the underlying signature in cp_Signature (for cp_MethodType_form band). */
    public int getIndexInCpSignature() { return signature.getIndex(); }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof CPMethodType)) return 0;
        return signature.compareTo(((CPMethodType) o).signature);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CPMethodType)) return false;
        return signature.equals(((CPMethodType) o).signature);
    }

    @Override
    public int hashCode() { return signature.hashCode(); }

    @Override
    public String toString() { return "MethodType:" + signature; }
}
