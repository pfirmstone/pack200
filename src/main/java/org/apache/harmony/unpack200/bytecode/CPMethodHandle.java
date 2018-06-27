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
package org.apache.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author peter
 */
public class CPMethodHandle extends CPLoadableValue { 
    
    private int reference_kind;
    private int reference_index;
    private CPAnyMemberRef member;

    public CPMethodHandle(int reference_kind, CPAnyMemberRef member, int globalIndex) {
	super(ConstantPoolEntry.CP_MethodHandle, globalIndex);
	if (reference_kind < 1 || reference_kind > 9) 
	    throw new IllegalArgumentException(
		"reference_kind out of range must be 1 - 9, was: " + reference_kind);
	if (member == null) throw new NullPointerException();
	this.reference_kind = reference_kind;
	this.member = member;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == this) return true;
	if (!(obj instanceof CPMethodHandle)) return false;
	CPMethodHandle that = (CPMethodHandle) obj;
	if (reference_kind != that.reference_kind) return false;
	return member.equals(that.member);
    }

    @Override
    public int hashCode() {
	int hash = 3;
	hash = 19 * hash + this.reference_kind;
	hash = 19 * hash + (this.member != null ? this.member.hashCode() : 0);
	return hash;
    }
    
    @Override
    protected void resolve(ClassConstantPool pool) {
	super.resolve(pool);
	reference_index = pool.indexOf(member);
    }

    @Override
    protected void writeBody(DataOutputStream dos) throws IOException {
	dos.writeByte(reference_kind);
	dos.writeShort(reference_index);
    }

    @Override
    public String toString() {
	return "MethodHandle: reference_kind = " + reference_kind + " member = " + member;
    }
    
}
