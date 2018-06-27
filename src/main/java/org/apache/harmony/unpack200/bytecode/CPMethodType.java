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
public class CPMethodType extends CPLoadableValue {

    CPUTF8 cp_Signature;
    int cp_signature_index;
    
    public CPMethodType(CPUTF8 cp_Signature, int globalIndex) {
	super(ConstantPoolEntry.CP_MethodType, globalIndex);
	this.cp_Signature = cp_Signature;
    }
    
    @Override
    protected void resolve(ClassConstantPool pool) {
	super.resolve(pool);
	cp_signature_index = pool.indexOf(cp_Signature);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) return true;
	if (!(obj instanceof CPMethodType)) return false;
	return cp_Signature.equals(((CPMethodType)obj).cp_Signature);
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 29 * hash + (this.cp_Signature != null ? this.cp_Signature.hashCode() : 0);
	return hash;
    }

    
    @Override
    protected void writeBody(DataOutputStream dos) throws IOException {
	dos.writeShort(cp_signature_index);
    }

    @Override
    public String toString() {
	return "MethodType: descriptor = " + cp_Signature;
    }
    
}
