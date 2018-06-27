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
public class CPInvokeDynamic extends ConstantPoolEntry {
    private final CPBootstrapMethod cpBootstrapMethodValue;
    private final CPNameAndType cpNameAndTypeValue;

    private int bootstrap_method_attr_index;
    private int cpNameAndTypeIndex;

    public CPInvokeDynamic(CPBootstrapMethod cpBootstrapMethodValue, CPNameAndType cpNameAndTypeValue, int globalIndex) {
	super(ConstantPoolEntry.CP_InvokeDynamic, globalIndex);
	this.cpBootstrapMethodValue = cpBootstrapMethodValue;
	this.cpNameAndTypeValue = cpNameAndTypeValue;
    }
    
    @Override
    protected void resolve(ClassConstantPool pool) {
	bootstrap_method_attr_index = cpBootstrapMethodValue.getBootstrapMethodAttrIndex();
	cpNameAndTypeIndex = pool.indexOf(cpNameAndTypeValue);
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == this) return true;
	if (!(obj instanceof CPInvokeDynamic)) return false;
	CPInvokeDynamic that = (CPInvokeDynamic) obj;
	if (!cpBootstrapMethodValue.equals(that.cpBootstrapMethodValue)) return false;
	return cpNameAndTypeValue.equals(that.cpNameAndTypeValue);
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 53 * hash + (this.cpBootstrapMethodValue != null ? this.cpBootstrapMethodValue.hashCode() : 0);
	hash = 53 * hash + (this.cpNameAndTypeValue != null ? this.cpNameAndTypeValue.hashCode() : 0);
	return hash;
    }

    @Override
    protected void writeBody(DataOutputStream dos) throws IOException {
	dos.writeShort(bootstrap_method_attr_index);
	dos.writeShort(cpNameAndTypeIndex);
    }

    @Override
    public String toString() {
	return "cp_InvokeDynamic " + cpBootstrapMethodValue + " " + cpNameAndTypeValue;
    }
    
}
