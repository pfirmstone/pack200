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

class CPMethodHandle extends ConstantPoolEntry implements Comparable {

    private final int referenceKind;
    private final CPMethodOrField member;

    CPMethodHandle(int referenceKind, CPMethodOrField member) {
        this.referenceKind = referenceKind;
        this.member = member;
    }

    public int getReferenceKind() { return referenceKind; }
    public CPMethodOrField getMember() { return member; }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof CPMethodHandle)) return 0;
        CPMethodHandle that = (CPMethodHandle) o;
        int cmp = referenceKind - that.referenceKind;
        if (cmp != 0) return cmp;
        return member.compareTo(that.member);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CPMethodHandle)) return false;
        CPMethodHandle that = (CPMethodHandle) o;
        return referenceKind == that.referenceKind && member.equals(that.member);
    }

    @Override
    public int hashCode() {
        return 31 * referenceKind + member.hashCode();
    }

    @Override
    public String toString() {
        return "MethodHandle:" + referenceKind + ":" + member;
    }
}
