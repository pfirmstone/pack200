/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.harmony.pack200;

import java.util.Arrays;

class CPBootstrapMethod extends ConstantPoolEntry implements Comparable {

    private final CPMethodHandle methodHandle;
    private final ConstantPoolEntry[] args;

    CPBootstrapMethod(CPMethodHandle methodHandle, ConstantPoolEntry[] args) {
        this.methodHandle = methodHandle;
        this.args = args;
    }

    public CPMethodHandle getMethodHandle() { return methodHandle; }
    public ConstantPoolEntry[] getArgs() { return args; }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof CPBootstrapMethod)) return 0;
        CPBootstrapMethod that = (CPBootstrapMethod) o;
        int cmp = methodHandle.compareTo(that.methodHandle);
        if (cmp != 0) return cmp;
        if (args.length != that.args.length) return args.length - that.args.length;
        for (int i = 0; i < args.length; i++) {
            String a = args[i].toString(), b = that.args[i].toString();
            int ac = a.compareTo(b);
            if (ac != 0) return ac;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CPBootstrapMethod)) return false;
        CPBootstrapMethod that = (CPBootstrapMethod) o;
        if (!methodHandle.equals(that.methodHandle)) return false;
        return Arrays.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        int h = methodHandle.hashCode();
        for (ConstantPoolEntry a : args) h = 31 * h + a.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return "BootstrapMethod:" + methodHandle + Arrays.toString(args);
    }
}
