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
import java.util.Arrays;

/**
 * cp_BootstrapMethod is a constant pool band, not a constant pool member.
 * 
 * It represents a bootstrap_method entry in the BootstrapMethodsAttribute
 * bootstrap_methods table.
 * @author peter
 */
public class CPBootstrapMethod extends ClassFileEntry {
    private final CPMethodHandle cpMethodHandleValue;
    private final int cpBootstrapMethodArgInt;
    private final CPLoadableValue[] bootstrapMethodArg;
    private int bootstrap_method_attr_index;
    
    private int cPmethodHandleIndex;
    private int [] bootstrapMethodArgIndexes;
    

    public CPBootstrapMethod(CPMethodHandle cpMethodHandleValue,
			    int cpBootstrapMethodArgInt,
			    CPLoadableValue[] bootstrapMethodArg,
			    int bootstrap_method_attr_index) {
	this.cpMethodHandleValue = cpMethodHandleValue;
	this.cpBootstrapMethodArgInt = cpBootstrapMethodArgInt;
	this.bootstrapMethodArg = bootstrapMethodArg;
	this.bootstrap_method_attr_index = bootstrap_method_attr_index;
    }
    
    public int getBootstrapMethodAttrIndex(){
	return bootstrap_method_attr_index;
    }
    
    @Override
    protected void resolve(ClassConstantPool pool) {
        super.resolve(pool);
	cPmethodHandleIndex = pool.indexOf(cpMethodHandleValue);
	int len = bootstrapMethodArg.length;
	bootstrapMethodArgIndexes = new int [len];
	for (int i = 0; i < len; i++){
	    bootstrapMethodArgIndexes[i] = pool.indexOf(bootstrapMethodArg[i]);
	}
    }

    @Override
    protected void doWrite(DataOutputStream dos) throws IOException {
	dos.writeShort(cPmethodHandleIndex);
	dos.writeShort(cpBootstrapMethodArgInt);
	for (int i = 0, l = bootstrapMethodArgIndexes.length; i < l; i++){
	    dos.writeShort(bootstrapMethodArgIndexes[i]);
	}
    }

    @Override
    public boolean equals(Object arg0) {
	if (this == arg0) return true;
	if (!(arg0 instanceof CPBootstrapMethod)) return false;
	CPBootstrapMethod that = (CPBootstrapMethod) arg0;
	if (cpBootstrapMethodArgInt != that.cpBootstrapMethodArgInt) return false;
	if (!this.cpMethodHandleValue.equals(that.cpMethodHandleValue)) return false;
	return Arrays.equals(bootstrapMethodArg, this.bootstrapMethodArg);
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 97 * hash + (this.cpMethodHandleValue != null ? this.cpMethodHandleValue.hashCode() : 0);
	hash = 97 * hash + this.cpBootstrapMethodArgInt;
	hash = 97 * hash + Arrays.deepHashCode(this.bootstrapMethodArg);
	return hash;
    }

    @Override
    public String toString() {
	return "bootstrap_methods: " + cpMethodHandleValue +
		"argument length=" +cpBootstrapMethodArgInt+ 
		" Arguments: " + Arrays.toString(bootstrapMethodArg);
    }
    
}
