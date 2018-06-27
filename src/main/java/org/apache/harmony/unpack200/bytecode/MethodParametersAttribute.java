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
 *
 * @author peter
 */
public class MethodParametersAttribute extends Attribute {
    
    private static CPUTF8 ATTRIBUTE_NAME; // u2

    public static void setAttributeName(CPUTF8 cpUTF8Value) {
	ATTRIBUTE_NAME = cpUTF8Value;
    }
    private final int attribute_length; // u4
    private final int parameters_count; // u1
    private final CPUTF8[] name; 
    private final int[] name_index; // [u2]
    private final int[] access_flags; // [u2]

    public MethodParametersAttribute(int parameters_count, 
				    CPUTF8 [] name, 
				    int [] access_flags)
    {
	super(ATTRIBUTE_NAME);
	this.attribute_length = 1 /*parameters_count*/ + name.length * 4; // name_index and access_flags item pairs
	this.parameters_count = parameters_count;
	this.name = name;
	name_index = new int [name.length];
	this.access_flags = access_flags;
    }
    
    @Override
    protected void resolve(ClassConstantPool pool) {
	super.resolve(pool);
	for (int i = 0, l = name.length; i < l; i++){
	    name_index[i] = pool.indexOf(name[i]);
	}
    }

    @Override
    protected int getLength() {
	return attribute_length;
    }

    @Override
    protected void writeBody(DataOutputStream dos) throws IOException {
	dos.writeByte(parameters_count);
	for (int i = 0, l = name_index.length; i < l; i++){
	    dos.writeShort(name_index[i]);
	    dos.writeShort(access_flags[i]);
	}
    }

    @Override
    public String toString() {
	return "MethodParameters";
    }
    
    @Override
    public boolean equals(Object o){
	if (!super.equals(o)) return false; // Superclass checks classes are same
	MethodParametersAttribute that = (MethodParametersAttribute) o;
	if (parameters_count != that.parameters_count) return false;
	if (!Arrays.equals(access_flags, that.access_flags)) return false;
	return Arrays.equals(name, that.name);
    }

    @Override
    public int hashCode() {
	int hash = 5;
	hash = 67 * hash + this.parameters_count;
	hash = 67 * hash + Arrays.deepHashCode(this.name);
	hash = 67 * hash + Arrays.hashCode(this.access_flags);
	return hash;
    }
    
}
