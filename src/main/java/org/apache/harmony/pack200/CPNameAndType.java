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
package org.apache.harmony.pack200;

/**
 * Constant pool entry for a name and type pair.
 */
class CPNameAndType extends ConstantPoolEntry implements Comparable {

    private final CPUTF8 name;
    private final CPSignature signature;

    public CPNameAndType(CPUTF8 name, CPSignature signature) {
        this.name = name;
        this.signature = signature;
    }
    
    @Override
    public boolean equals(Object o){
	if (!(o instanceof CPNameAndType)) return false;
	CPNameAndType that = (CPNameAndType) o;
	if (!name.equals(that.name)) return false;
	return signature.equals(that.signature);
    }

    @Override
    public int hashCode() {
	int hash = 3;
	hash = 71 * hash + (this.name != null ? this.name.hashCode() : 0);
	hash = 71 * hash + (this.signature != null ? this.signature.hashCode() : 0);
	return hash;
    }

    @Override
    public String toString() {
        return name + ":" + signature;
    }

    public int compareTo(Object obj) {
        if (obj instanceof CPNameAndType) {
            CPNameAndType nat = (CPNameAndType) obj;
            int compareSignature = signature.compareTo(nat.signature);;
            if(compareSignature == 0) {
                return name.compareTo(nat.name);
            } else {
                return compareSignature;
            }
        }
        return 0;
    }

    public int getNameIndex() {
        return name.getIndex();
    }

    public String getName() {
        return name.getUnderlyingString();
    }

    public int getTypeIndex() {
        return signature.getIndex();
    }

}