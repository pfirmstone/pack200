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
import java.util.ArrayList;
import java.util.List;

/**
 * The BootstrapMethods class file attribute, which is required for any class
 * that contains invokedynamic instructions.
 */
public class BootstrapMethodsAttribute extends Attribute {

    private static CPUTF8 attributeName;

    public static void setAttributeName(CPUTF8 cpUTF8Value) {
        attributeName = cpUTF8Value;
    }

    private final List bootstrapMethods; // List<CPBootstrapMethod>

    public BootstrapMethodsAttribute(List bootstrapMethods) {
        super(attributeName);
        this.bootstrapMethods = bootstrapMethods;
    }

    @Override
    protected ClassFileEntry[] getNestedClassFileEntries() {
        List nested = new ArrayList(1 + bootstrapMethods.size());
        nested.add(getAttributeName());
        nested.addAll(bootstrapMethods);
        return (ClassFileEntry[]) nested.toArray(new ClassFileEntry[nested.size()]);
    }

    @Override
    protected int getLength() {
        int length = 2; // num_bootstrap_methods
        for (int i = 0; i < bootstrapMethods.size(); i++) {
            CPBootstrapMethod bsm = (CPBootstrapMethod) bootstrapMethods.get(i);
            // 2 bytes for method_handle index + 2 bytes for num_args + 2 bytes per arg
            length += 2 + 2 + 2 * bsm.getArgCount();
        }
        return length;
    }

    @Override
    protected void writeBody(DataOutputStream dos) throws IOException {
        dos.writeShort(bootstrapMethods.size());
        for (int i = 0; i < bootstrapMethods.size(); i++) {
            CPBootstrapMethod bsm = (CPBootstrapMethod) bootstrapMethods.get(i);
            bsm.write(dos);
        }
    }

    @Override
    public String toString() {
        return "BootstrapMethods: " + bootstrapMethods.size() + " entries";
    }
}
