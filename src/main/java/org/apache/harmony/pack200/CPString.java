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
 * Constant pool entry for a String.
 */
class CPString extends CPConstant {

    private final String string;
    private final CPUTF8 utf8;

    public CPString(CPUTF8 utf8) {
        this.utf8 = utf8;
        this.string = utf8.getUnderlyingString();
    }

    public int compareTo(Object arg0) {
        return string.compareTo(((CPString)arg0).string);
    }

    @Override
    public boolean equals(Object o){
	if (!(o instanceof CPString)) return false;
	CPString that = (CPString) o;
	if (!string.equals(that.string)) return false;
	return utf8.equals(that.utf8);
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 89 * hash + (this.string != null ? this.string.hashCode() : 0);
	hash = 89 * hash + (this.utf8 != null ? this.utf8.hashCode() : 0);
	return hash;
    }
    
    @Override
    public String toString() {
        return string;
    }

    public int getIndexInCpUtf8() {
        return utf8.getIndex();
    }

}
