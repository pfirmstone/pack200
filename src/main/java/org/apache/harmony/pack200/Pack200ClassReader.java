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

import org.objectweb.asm.ClassReader;

/**
 * Wrapper for ClassReader that enables pack200 to obtain extra class file
 * information
 */
class Pack200ClassReader extends ClassReader {

    private boolean lastConstantHadWideIndex;
    private int lastUnsignedShort;
    private boolean anySyntheticAttributes;
    private String fileName;

    /**
     * @param b
     *            the contents of class file in the format of bytes
     */
    public Pack200ClassReader(byte[] b) {
        super(b);
    }
    
    @Override
    public int readUnsignedShort(int index) {
	// Doing this to check whether last load-constant instruction was ldc (18) or ldc_w (19)
        // TODO:  Assess whether this impacts on performance
        int unsignedShort = super.readUnsignedShort(index);
        if(b[index - 1] == 19) {
		lastUnsignedShort = unsignedShort;
	    } else {
		lastUnsignedShort = Short.MIN_VALUE;
	    }
        return unsignedShort;
    }

    @Override
    public Object readConst(int item, char[] buf) {
        lastConstantHadWideIndex = item == lastUnsignedShort;
        return super.readConst(item, buf);
    }

    @Override
    public String readUTF8(int offset, char[] arg1) {
	if (offset == 0) { // To prevent readUnsignedShort throwing ArrayIndexOutOfBoundsException.
	    return null;
	}
        String utf8 = super.readUTF8(offset, arg1);
        if(!anySyntheticAttributes && "Synthetic".equals(utf8)) {
            anySyntheticAttributes = true;
        }
        return utf8;
    }

    public boolean lastConstantHadWideIndex() {
        return lastConstantHadWideIndex;
    }

    public boolean hasSyntheticAttributes() {
        return anySyntheticAttributes;
    }

    public void setFileName(String name) {
        this.fileName = name;
    }

    public String getFileName() {
        return fileName;
    }

}
