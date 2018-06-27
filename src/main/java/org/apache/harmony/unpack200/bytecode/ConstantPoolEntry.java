/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Abstract superclass for constant pool entries
 */
public abstract class ConstantPoolEntry extends ClassFileEntry {

    // Note the following are constant pool tags from the class file format
    // these are different to the definition order used for CONSTANTS in the pack 200 spec. 
    
    // Class file tag CONSTANT's
    public static final byte CP_UTF8 = 1;
    public static final byte CP_Integer = 3;
    public static final byte CP_Float = 4;
    public static final byte CP_Long = 5;
    public static final byte CP_Double = 6;
    public static final byte CP_String = 8;
    public static final byte CP_Class = 7;
    // CP_Signature (none)
    public static final byte CP_NameAndType = 12;
    public static final byte CP_Fieldref = 9;
    public static final byte CP_Methodref = 10;
    public static final byte CP_InterfaceMethodref = 11;
    public static final byte CP_MethodHandle = 15;
    public static final byte CP_MethodType = 16;
    // CP_BootstrapMethod (none: side table to constant pool)
    public static final byte CP_InvokeDynamic = 18; // Also called CONSTANT_DynamicCallSite
    public static final byte CP_Module = 19; // Supplimental to pack200 standard Java 9
    public static final byte CP_Package = 20; // Supplimental to pack200 standard Java 9
    public static final byte CP_Dynamic = 17; // Supplimental to pack200 standard Java 11
    // JDK-8161256 proposed general data in constant pools
    // public static final byte CP_Group = 13;
    // public static final byte CP_Bytes = 2;

    /*
     * class MemberRef extends ConstantPoolEntry { private int index;
     * Class(String name) { super(CP_Class); index = pool.indexOf(name); } void
     * writeBody(DataOutputStream dos) throws IOException {
     * dos.writeShort(index); } }
     */

    byte tag;

    protected int globalIndex;

    ConstantPoolEntry(byte tag, int globalIndex) {
        this.tag = tag;
        this.globalIndex = globalIndex;
    }

    public abstract boolean equals(Object obj);

    public byte getTag() {
        return tag;
    }

    public abstract int hashCode();

    public void doWrite(DataOutputStream dos) throws IOException {
        dos.writeByte(tag);
        writeBody(dos);
    }

    protected abstract void writeBody(DataOutputStream dos) throws IOException;

    public int getGlobalIndex() {
        return globalIndex;
    }
}
