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
package org.apache.harmony.unpack200;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.harmony.pack200.Codec;
import org.apache.harmony.pack200.Pack200Exception;
import org.apache.harmony.unpack200.bytecode.CPAnyMemberRef;
import org.apache.harmony.unpack200.bytecode.CPBootstrapMethod;
import org.apache.harmony.unpack200.bytecode.CPClass;
import org.apache.harmony.unpack200.bytecode.CPDouble;
import org.apache.harmony.unpack200.bytecode.CPFieldRef;
import org.apache.harmony.unpack200.bytecode.CPFloat;
import org.apache.harmony.unpack200.bytecode.CPInteger;
import org.apache.harmony.unpack200.bytecode.CPInterfaceMethodRef;
import org.apache.harmony.unpack200.bytecode.CPInvokeDynamic;
import org.apache.harmony.unpack200.bytecode.CPLoadableValue;
import org.apache.harmony.unpack200.bytecode.CPLong;
import org.apache.harmony.unpack200.bytecode.CPMethodHandle;
import org.apache.harmony.unpack200.bytecode.CPMethodRef;
import org.apache.harmony.unpack200.bytecode.CPMethodType;
import org.apache.harmony.unpack200.bytecode.CPNameAndType;
import org.apache.harmony.unpack200.bytecode.CPString;
import org.apache.harmony.unpack200.bytecode.CPUTF8;
import org.apache.harmony.unpack200.bytecode.ClassFileEntry;

/**
 * Constant Pool bands
 */
class CpBands extends BandSet {
    
    
    public SegmentConstantPool getConstantPool() {
        return pool;
    }

    private final SegmentConstantPool pool = new SegmentConstantPool(this);

    private String[] cpClass;
    private int[] cpClassInts;
    private int[] cpDescriptorNameInts;
    private int[] cpDescriptorTypeInts;
    private String[] cpDescriptor;
    private double[] cpDouble;
    private String[] cpFieldClass;
    private String[] cpFieldDescriptor;
    private int[] cpFieldClassInts;
    private int[] cpFieldDescriptorInts;
    private float[] cpFloat;
    private String[] cpIMethodClass;
    private String[] cpIMethodDescriptor;
    private int[] cpIMethodClassInts;
    private int[] cpIMethodDescriptorInts;
    private int[] cpInt;
    private long[] cpLong;
    private String[] cpMethodClass;
    private String[] cpMethodDescriptor;
    private int[] cpMethodClassInts;
    private int[] cpMethodDescriptorInts;
    private String[] cpSignature;
    private int[] cpSignatureInts;
    private String[] cpString;
    private int[] cpStringInts;
    private String[] cpUTF8;
    private int[] cpMethodHandleRefkindInts;
    private CPAnyMemberRef[] cpMethodHandleMember;
    private int[] cpMethodTypeInts;
    private int[] cpBootstrapMethodRef;
    private int[] cpBootstrapMethodArgInts;
    private int[][] cpBootstrapMethodArg;
    private int[] cpInvokeDynamicSpec;
    private int[] cpInvokeDynamicDescr;
    
    private final Map stringsToCPUTF8 = new HashMap();
    private final Map stringsToCPStrings = new HashMap();
    private final Map longsToCPLongs = new HashMap();
    private final Map integersToCPIntegers = new HashMap();
    private final Map floatsToCPFloats = new HashMap();
    private final Map stringsToCPClass = new HashMap();
    private final Map doublesToCPDoubles = new HashMap();
    private final Map descriptorsToCPNameAndTypes = new HashMap();

    private Map mapClass;
    private Map mapDescriptor;
    private Map mapUTF8;

// TODO: Not used
    private Map mapSignature;

    // All group
    private int intOffset;
    private int floatOffset;
    private int longOffset;
    private int doubleOffset;
    private int stringOffset;
    private int classOffset;
    private int signatureOffset;
    private int descrOffset;
    private int fieldOffset;
    private int methodOffset;
    private int imethodOffset;
    private int methodHandleOffset;
    private int methodTypeOffset;
    private int bootstrapMethodOffset;
    private int invokeDynamicOffset;
    
    // Any member group
    private int anyMemberFieldOffset;
    private int anyMemberMethodOffset;
    private int anyMemberIMethodOffset;
    
    // loadable value group
    private int lVIntOffset;
    private int lVFloatOffset;
    private int lVLongOffset;
    private int lVDoubleOffset;
    private int lVStringOffset;
    private int lVClassOffset;
    private int lVMethodHandleOffset;
    private int lVMethodTypeOffset;
    
    public CpBands(Segment segment) {
        super(segment);
    }

    public void read(InputStream in) throws IOException, Pack200Exception {
        parseCpUtf8(in);
        parseCpInt(in);
        parseCpFloat(in);
        parseCpLong(in);
        parseCpDouble(in);
        parseCpString(in);
        parseCpClass(in);
        parseCpSignature(in);
        parseCpDescriptor(in);
        parseCpField(in);
        parseCpMethod(in);
        parseCpIMethod(in);
	parseCpMethodHandle(in);
	parseCpMethodType(in);
	parseCpBootstrapMethod(in);
	
	// cp_AnyMember group method handle component.
	anyMemberFieldOffset = 0;
	anyMemberMethodOffset = cpFieldClass.length;
	anyMemberIMethodOffset = cpMethodClass.length + anyMemberMethodOffset;
	
	parseCpMethodHandle(in);
	parseCpMethodType(in);
	
	// cp_LoadableValue group for bootstrap method argument
	lVIntOffset = 0;
	lVFloatOffset = cpInt.length;
	lVLongOffset = cpFloat.length + lVFloatOffset;
	lVDoubleOffset = cpLong.length + lVLongOffset;
	lVStringOffset = cpDouble.length + lVDoubleOffset;
	lVClassOffset = cpString.length + lVStringOffset;
	lVMethodHandleOffset = cpClass.length + lVClassOffset;
	lVMethodTypeOffset = cpMethodHandleRefkindInts.length + lVMethodHandleOffset;
	
	parseCpBootstrapMethod(in);
	parseCpInvokeDynamic(in);
	parseCpModule(in);
	parseCpPackage(in);

	// cp_All group
        intOffset = cpUTF8.length;
        floatOffset = intOffset + cpInt.length;
        longOffset = floatOffset + cpFloat.length;
        doubleOffset = longOffset + cpLong.length;
        stringOffset = doubleOffset + cpDouble.length;
        classOffset = stringOffset + cpString.length;
        signatureOffset = classOffset + cpClass.length;
        descrOffset = signatureOffset + cpSignature.length;
        fieldOffset = descrOffset + cpDescriptor.length;
        methodOffset = fieldOffset + cpFieldClass.length;
        imethodOffset = methodOffset + cpMethodClass.length;
	methodHandleOffset = imethodOffset + cpIMethodClass.length;
	methodTypeOffset = methodHandleOffset + cpMethodHandleRefkindInts.length;
	bootstrapMethodOffset = methodTypeOffset + cpBootstrapMethodRef.length;
	invokeDynamicOffset = bootstrapMethodOffset + cpInvokeDynamicSpec.length;
    }

    public void unpack() {

    }

    /**
     * Parses the constant pool class names, using {@link #cpClassCount} to
     * populate {@link #cpClass} from {@link #cpUTF8}.
     *
     * @param in
     *            the input stream to read from
     * @throws IOException
     *             if a problem occurs during reading from the underlying stream
     * @throws Pack200Exception
     *             if a problem occurs with an unexpected value or unsupported
     *             codec
     */
    private void parseCpClass(InputStream in) throws IOException,
            Pack200Exception {
        int cpClassCount = header.getCpClassCount();
        cpClassInts = decodeBandInt("cp_Class", in, Codec.UDELTA5, cpClassCount);
        cpClass = new String[cpClassCount];
        mapClass = new HashMap(cpClassCount);
        for (int i = 0; i < cpClassCount; i++) {
            cpClass[i] = cpUTF8[cpClassInts[i]];
            mapClass.put(cpClass[i], Integer.valueOf(i));
        }
    }

    /**
     * Parses the constant pool descriptor definitions, using
     * {@link #cpDescriptorCount} to populate {@link #cpNameAndTypeValue}. For ease
     * of use, the cpDescriptor is stored as a string of the form <i>name:type</i>,
     * largely to make it easier for representing field and method descriptors
     * (e.g. <code>out:java.lang.PrintStream</code>) in a way that is
     * compatible with passing String arrays.
     *
     * @param in
     *            the input stream to read from
     * @throws IOException
     *             if a problem occurs during reading from the underlying stream
     * @throws Pack200Exception
     *             if a problem occurs with an unexpected value or unsupported
     *             codec
     */
    private void parseCpDescriptor(InputStream in) throws IOException,
            Pack200Exception {
        int cpDescriptorCount = header.getCpDescriptorCount();
        cpDescriptorNameInts = decodeBandInt("cp_Descr_name", in, Codec.DELTA5,
                cpDescriptorCount);
        cpDescriptorTypeInts = decodeBandInt("cp_Descr_type", in,
                Codec.UDELTA5, cpDescriptorCount);
        String[] cpDescriptorNames = getReferences(cpDescriptorNameInts, cpUTF8);
        String[] cpDescriptorTypes = getReferences(cpDescriptorTypeInts,
                cpSignature);
        cpDescriptor = new String[cpDescriptorCount];
        mapDescriptor = new HashMap(cpDescriptorCount);
        for (int i = 0; i < cpDescriptorCount; i++) {
            cpDescriptor[i] = cpDescriptorNames[i] + ":" + cpDescriptorTypes[i]; //$NON-NLS-1$
            mapDescriptor.put(cpDescriptor[i], Integer.valueOf(i));
        }
    }

    private void parseCpDouble(InputStream in) throws IOException,
            Pack200Exception {
        int cpDoubleCount = header.getCpDoubleCount();
        long[] band = parseFlags("cp_Double", in, cpDoubleCount, Codec.UDELTA5,
                Codec.DELTA5);
        cpDouble = new double[band.length];
        for (int i = 0; i < band.length; i++) {
            cpDouble[i] = Double.longBitsToDouble(band[i]);
        }
    }

    /**
     * Parses the constant pool field definitions, using {@link #cpFieldCount}
     * to populate {@link #cpFieldClass} and {@link #cpFieldDescriptor}.
     *
     * @param in
     *            the input stream to read from
     * @throws IOException
     *             if a problem occurs during reading from the underlying stream
     * @throws Pack200Exception
     *             if a problem occurs with an unexpected value or unsupported
     *             codec
     */
    private void parseCpField(InputStream in) throws IOException,
            Pack200Exception {
        int cpFieldCount = header.getCpFieldCount();
        cpFieldClassInts = decodeBandInt("cp_Field_class", in, Codec.DELTA5,
                cpFieldCount);
        cpFieldDescriptorInts = decodeBandInt("cp_Field_desc", in, Codec.UDELTA5,
                cpFieldCount);
        cpFieldClass = new String[cpFieldCount];
        cpFieldDescriptor = new String[cpFieldCount];
        for (int i = 0; i < cpFieldCount; i++) {
            cpFieldClass[i] = cpClass[cpFieldClassInts[i]];
            cpFieldDescriptor[i] = cpDescriptor[cpFieldDescriptorInts[i]];
        }
    }

    private void parseCpFloat(InputStream in) throws IOException,
            Pack200Exception {
        int cpFloatCount = header.getCpFloatCount();
        cpFloat = new float[cpFloatCount];
        int floatBits[] = decodeBandInt("cp_Float", in, Codec.UDELTA5,
                cpFloatCount);
        for (int i = 0; i < cpFloatCount; i++) {
            cpFloat[i] = Float.intBitsToFloat(floatBits[i]);
        }
    }

    /**
     * Parses the constant pool interface method definitions, using
     * {@link #cpIMethodCount} to populate {@link #cpIMethodClass} and
     * {@link #cpIMethodDescriptor}.
     *
     * @param in
     *            the input stream to read from
     * @throws IOException
     *             if a problem occurs during reading from the underlying stream
     * @throws Pack200Exception
     *             if a problem occurs with an unexpected value or unsupported
     *             codec
     */
    private void parseCpIMethod(InputStream in) throws IOException,
            Pack200Exception {
        int cpIMethodCount = header.getCpIMethodCount();
        cpIMethodClassInts = decodeBandInt("cp_Imethod_class", in, Codec.DELTA5,
                cpIMethodCount);
        cpIMethodDescriptorInts = decodeBandInt("cp_Imethod_desc", in,
                Codec.UDELTA5, cpIMethodCount);
        cpIMethodClass = new String[cpIMethodCount];
        cpIMethodDescriptor = new String[cpIMethodCount];
        for (int i = 0; i < cpIMethodCount; i++) {
            cpIMethodClass[i] = cpClass[cpIMethodClassInts[i]];
            cpIMethodDescriptor[i] = cpDescriptor[cpIMethodDescriptorInts[i]];
        }
    }

    private void parseCpInt(InputStream in) throws IOException,
            Pack200Exception {
        int cpIntCount = header.getCpIntCount();
        cpInt = decodeBandInt("cpInt", in, Codec.UDELTA5, cpIntCount);
    }

    private void parseCpLong(InputStream in) throws IOException,
            Pack200Exception {
        int cpLongCount = header.getCpLongCount();
        cpLong = parseFlags("cp_Long", in, cpLongCount, Codec.UDELTA5,
                Codec.DELTA5);
    }

    /**
     * Parses the constant pool method definitions, using {@link #cpMethodCount}
     * to populate {@link #cpMethodClass} and {@link #cpMethodDescriptor}.
     *
     * @param in
     *            the input stream to read from
     * @throws IOException
     *             if a problem occurs during reading from the underlying stream
     * @throws Pack200Exception
     *             if a problem occurs with an unexpected value or unsupported
     *             codec
     */
    private void parseCpMethod(InputStream in) throws IOException,
            Pack200Exception {
        int cpMethodCount = header.getCpMethodCount();
        cpMethodClassInts = decodeBandInt("cp_Method_class", in, Codec.DELTA5,
                cpMethodCount);
        cpMethodDescriptorInts = decodeBandInt("cp_Method_desc", in,
                Codec.UDELTA5, cpMethodCount);
        cpMethodClass = new String[cpMethodCount];
        cpMethodDescriptor = new String[cpMethodCount];
        for (int i = 0; i < cpMethodCount; i++) {
            cpMethodClass[i] = cpClass[cpMethodClassInts[i]];
            cpMethodDescriptor[i] = cpDescriptor[cpMethodDescriptorInts[i]];
        }
    }

    /**
     * Parses the constant pool signature classes, using
     * {@link #cpSignatureCount} to populate {@link #cpSignature}. A signature
     * form is akin to the bytecode representation of a class; Z for boolean, I
     * for int, [ for array etc. However, although classes are started with L,
     * the classname does not follow the form; instead, there is a separate
     * array of classes. So an array corresponding to
     * <code>public static void main(String args[])</code> has a form of
     * <code>[L(V)</code> and a classes array of
     * <code>[java.lang.String]</code>. The {@link #cpSignature} is a string
     * representation identical to the bytecode equivalent
     * <code>[Ljava/lang/String;(V)</code> TODO Check that the form is as
     * above and update other types e.g. J
     *
     * @param in
     *            the input stream to read from
     * @throws IOException
     *             if a problem occurs during reading from the underlying stream
     * @throws Pack200Exception
     *             if a problem occurs with an unexpected value or unsupported
     *             codec
     */
    private void parseCpSignature(InputStream in) throws IOException,
            Pack200Exception {
        int cpSignatureCount = header.getCpSignatureCount();
        cpSignatureInts = decodeBandInt("cp_Signature_form", in,
                Codec.DELTA5, cpSignatureCount);
        String[] cpSignatureForm = getReferences(cpSignatureInts, cpUTF8);
        cpSignature = new String[cpSignatureCount];
        mapSignature = new HashMap();
        int lCount = 0;
        for (int i = 0; i < cpSignatureCount; i++) {
            String form = cpSignatureForm[i];
            char[] chars = form.toCharArray();
            for (int j = 0, length = chars.length; j <length; j++) {
                if (chars[j] == 'L') {
                    cpSignatureInts[i] = -1;
                    lCount++;
                }
            }
        }
        String[] cpSignatureClasses = parseReferences("cp_Signature_classes",
                in, Codec.UDELTA5, lCount, cpClass);
        int index = 0;
        for (int i = 0; i < cpSignatureCount; i++) {
            String form = cpSignatureForm[i];
            int len = form.length();
            StringBuilder signature = new StringBuilder(64);
            ArrayList list = new ArrayList();
            for (int j = 0; j < len; j++) {
                char c = form.charAt(j);
                signature.append(c);
                if (c == 'L') {
                    String className = cpSignatureClasses[index];
                    list.add(className);
                    signature.append(className);
                    index++;
                }
            }
            cpSignature[i] = signature.toString();
            mapSignature.put(signature.toString(), Integer.valueOf(i));
        }
//        for (int i = 0; i < cpSignatureInts.length; i++) {
//            if(cpSignatureInts[i] == -1) {
//                cpSignatureInts[i] = search(cpUTF8, cpSignature[i]);
//            }
//        }
    }

    /**
     * Parses the constant pool strings, using {@link #cpStringCount} to
     * populate {@link #cpString} from indexes into {@link #cpUTF8}.
     *
     * @param in
     *            the input stream to read from
     * @throws IOException
     *             if a problem occurs during reading from the underlying stream
     * @throws Pack200Exception
     *             if a problem occurs with an unexpected value or unsupported
     *             codec
     */
    private void parseCpString(InputStream in) throws IOException,
            Pack200Exception {
        int cpStringCount = header.getCpStringCount();
        cpStringInts = decodeBandInt("cp_String", in, Codec.UDELTA5,
                cpStringCount);
        cpString = new String[cpStringCount];
        for (int i = 0; i < cpStringCount; i++) {
            cpString[i] = cpUTF8[cpStringInts[i]];
        }
    }

    private void parseCpUtf8(InputStream in) throws IOException,
            Pack200Exception {
        int cpUTF8Count = header.getCpUTF8Count();
        cpUTF8 = new String[cpUTF8Count];
        mapUTF8 = new HashMap(cpUTF8Count+1);
        cpUTF8[0] = ""; //$NON-NLS-1$
        mapUTF8.put("", Integer.valueOf(0));
        int[] prefix = decodeBandInt("cpUTF8Prefix", in, Codec.DELTA5,
                cpUTF8Count - 2);
        int charCount = 0;
        int bigSuffixCount = 0;
        int[] suffix = decodeBandInt("cpUTF8Suffix", in, Codec.UNSIGNED5,
                cpUTF8Count - 1);

        for (int i = 0; i < suffix.length; i++) {
            if (suffix[i] == 0) {
                bigSuffixCount++;
            } else {
                charCount += suffix[i];
            }
        }
        char[] data = new char[charCount];
        int[] dataBand = decodeBandInt("cp_Utf8_chars", in, Codec.CHAR3,
                charCount);
        for (int i = 0; i < data.length; i++) {
            data[i] = (char) dataBand[i];
        }

        // Read in the big suffix data
        int[] bigSuffixCounts = decodeBandInt("cp_Utf8_big_suffix", in,
                Codec.DELTA5, bigSuffixCount);
        int[][] bigSuffixDataBand = new int[bigSuffixCount][];
        for (int i = 0; i < bigSuffixDataBand.length; i++) {
            bigSuffixDataBand[i] = decodeBandInt("cp_Utf8_big_chars " + i, in,
                    Codec.DELTA5, bigSuffixCounts[i]);
        }

        // Convert big suffix data to characters
        char bigSuffixData[][] = new char[bigSuffixCount][];
        for (int i = 0; i < bigSuffixDataBand.length; i++) {
            bigSuffixData[i] = new char[bigSuffixDataBand[i].length];
            for (int j = 0; j < bigSuffixDataBand[i].length; j++) {
                bigSuffixData[i][j] = (char) bigSuffixDataBand[i][j];
            }
        }
        // Go through the strings
        charCount = 0;
        bigSuffixCount = 0;
        for (int i = 1; i < cpUTF8Count; i++) {
            String lastString = cpUTF8[i - 1];
            if (suffix[i - 1] == 0) {
                // The big suffix stuff hasn't been tested, and I'll be
                // surprised if it works first time w/o errors ...
                cpUTF8[i] = lastString.substring(0, i > 1 ? prefix[i - 2] : 0)
                        + new String(bigSuffixData[bigSuffixCount++]);
                mapUTF8.put(cpUTF8[i], Integer.valueOf(i));
            } else {
                cpUTF8[i] = lastString.substring(0, i > 1 ? prefix[i - 2] : 0)
                        + new String(data, charCount, suffix[i - 1]);
                charCount += suffix[i - 1];
                mapUTF8.put(cpUTF8[i], Integer.valueOf(i));
            }
        }
    }
    
    private void parseCpMethodHandle(InputStream in) throws IOException, Pack200Exception {
	int cpMethodHandleCount = header.getCpMethodHandleCount();
	cpMethodHandleRefkindInts = decodeBandInt("cp_MethodHandle_refkind", in,
                Codec.DELTA5, cpMethodHandleCount);
	int [] cpMethodHandleMemberInts = decodeBandInt("cp_MethodHandle_member", in,
		Codec.UDELTA5, cpMethodHandleCount);
	cpMethodHandleMember = new CPAnyMemberRef [cpMethodHandleCount];
	for (int i = 0; i < cpMethodHandleCount; i++){
	    cpMethodHandleMember[i] = cpAnyMemberValue(cpMethodHandleMemberInts[i]);
	}
    }

    private void parseCpMethodType(InputStream in) throws IOException, Pack200Exception {
	int cpMethodTypeCount = header.getCpMethodTypeCount();
	cpMethodTypeInts = decodeBandInt("cp_MethodType_form", in,
                Codec.DELTA5, cpMethodTypeCount);
    }

    private void parseCpBootstrapMethod(InputStream in) throws IOException, Pack200Exception {
	int cpBootstrapMethodCount = header.getCpBootstrapMethodCount();
	cpBootstrapMethodRef = decodeBandInt("cp_BootstrapMethod_ref", in,
                Codec.DELTA5, cpBootstrapMethodCount);
	cpBootstrapMethodArgInts = decodeBandInt("cp_BootstrapMethod_arg_count", in,
                Codec.UDELTA5, cpBootstrapMethodCount);
//	int sumBootstrapMethodArgInts = 0;
	cpBootstrapMethodArg = new int [cpBootstrapMethodCount][];
	for (int i = 0, l = cpBootstrapMethodArgInts.length; i < l; i++){
//	    sumBootstrapMethodArgInts += cpBootstrapMethodArgInts[i];
	    cpBootstrapMethodArg[i] = decodeBandInt("cp_BootstrapMethod_arg", in,
                Codec.DELTA5, cpBootstrapMethodArgInts[i]);
	}
    }

    private void parseCpInvokeDynamic(InputStream in) throws IOException, Pack200Exception {
	int cpInvokeDynamicCount = header.getCpInvokeDynamicCount();
	cpInvokeDynamicSpec = decodeBandInt("cp_InvokeDynamic_spec", in,
                Codec.DELTA5, cpInvokeDynamicCount);
	cpInvokeDynamicDescr = decodeBandInt("cp_InvokeDynamic_descr", in,
                Codec.UDELTA5, cpInvokeDynamicCount);
    }

    private void parseCpModule(InputStream in) {
	
    }

    private void parseCpPackage(InputStream in) {
	
    }


    public String[] getCpClass() {
        return cpClass;
    }

    public String[] getCpDescriptor() {
        return cpDescriptor;
    }

    public String[] getCpFieldClass() {
        return cpFieldClass;
    }

    public String[] getCpIMethodClass() {
        return cpIMethodClass;
    }

    public int[] getCpInt() {
        return cpInt;
    }

    public long[] getCpLong() {
        return cpLong;
    }

    public String[] getCpMethodClass() {
        return cpMethodClass;
    }

    public String[] getCpMethodDescriptor() {
        return cpMethodDescriptor;
    }

    public String[] getCpSignature() {
        return cpSignature;
    }

    public String[] getCpUTF8() {
        return cpUTF8;
    }

    public CPUTF8 cpUTF8Value(int index) {
	if (index >= cpUTF8.length) throw new ArrayIndexOutOfBoundsException(
		"Index: " + index + " Length: "+ cpUTF8.length);
        String string = cpUTF8[index];
        CPUTF8 cputf8 = (CPUTF8) stringsToCPUTF8.get(string);
        if (cputf8 == null) {
            cputf8 = new CPUTF8(string, index);
            stringsToCPUTF8.put(string, cputf8);
        } else if(cputf8.getGlobalIndex() > index) {
            cputf8.setGlobalIndex(index);
        }
        return cputf8;
    }

    public CPUTF8 cpUTF8Value(String string) {
        return cpUTF8Value(string, true);
    }

    public CPUTF8 cpUTF8Value(String string, boolean searchForIndex) {
        CPUTF8 cputf8 = (CPUTF8) stringsToCPUTF8.get(string);
        if (cputf8 == null) {
        	Integer index = null;
        	if(searchForIndex) {
            	index = (Integer)mapUTF8.get(string);
            }
            if(index != null) {
            	return cpUTF8Value(index.intValue());
            }
            if(searchForIndex) {
                index = (Integer)mapSignature.get(string);
            }
            if(index != null) {
            	return cpSignatureValue(index.intValue());
            }
            cputf8 = new CPUTF8(string, -1);
            stringsToCPUTF8.put(string, cputf8);
        }
        return cputf8;
    }

    public CPString cpStringValue(int index) {
        String string = cpString[index];
        int utf8Index = cpStringInts[index];
        int globalIndex = stringOffset + index;
        CPString cpString = (CPString) stringsToCPStrings.get(string);
        if (cpString == null) {
            cpString = new CPString(cpUTF8Value(utf8Index), globalIndex);
            stringsToCPStrings.put(string, cpString);
        }
        return cpString;
    }

    public CPLong cpLongValue(int index) {
        Long l = Long.valueOf(cpLong[index]);
        CPLong cpLong = (CPLong) longsToCPLongs.get(l);
        if (cpLong == null) {
            cpLong = new CPLong(l, index + longOffset);
            longsToCPLongs.put(l, cpLong);
        }
        return cpLong;
    }

    public CPInteger cpIntegerValue(int index) {
        Integer i = Integer.valueOf(cpInt[index]);
        CPInteger cpInteger = (CPInteger) integersToCPIntegers.get(i);
        if (cpInteger == null) {
            cpInteger = new CPInteger(i, index + intOffset);
            integersToCPIntegers.put(i, cpInteger);
        }
        return cpInteger;
    }

    public CPFloat cpFloatValue(int index) {
        Float f = Float.valueOf(cpFloat[index]);
        CPFloat cpFloat = (CPFloat) floatsToCPFloats.get(f);
        if (cpFloat == null) {
            cpFloat = new CPFloat(f, index + floatOffset);
            floatsToCPFloats.put(f, cpFloat);
        }
        return cpFloat;
    }

    public CPClass cpClassValue(int index) {
        String string = cpClass[index];
        int utf8Index = cpClassInts[index];
        int globalIndex = classOffset + index;
        CPClass cpString = (CPClass) stringsToCPClass.get(string);
        if (cpString == null) {
            cpString = new CPClass(cpUTF8Value(utf8Index), globalIndex);
            stringsToCPClass.put(string, cpString);
        }
        return cpString;
    }

    public CPClass cpClassValue(String string) {
        CPClass cpString = (CPClass) stringsToCPClass.get(string);
        if (cpString == null) {
        	Integer index = (Integer)mapClass.get(string);
        	if(index != null) {
        		return cpClassValue(index.intValue());
            }
            cpString = new CPClass(cpUTF8Value(string, false), -1);
            stringsToCPClass.put(string, cpString);
        }
        return cpString;
    }

    public CPDouble cpDoubleValue(int index) {
        Double dbl = Double.valueOf(cpDouble[index]);
        CPDouble cpDouble = (CPDouble) doublesToCPDoubles.get(dbl);
        if (cpDouble == null) {
            cpDouble = new CPDouble(dbl, index + doubleOffset);
            doublesToCPDoubles.put(dbl, cpDouble);
        }
        return cpDouble;
    }

    public CPNameAndType cpNameAndTypeValue(int index) {
        String descriptor = cpDescriptor[index];
        CPNameAndType cpNameAndType = (CPNameAndType) descriptorsToCPNameAndTypes
                .get(descriptor);
        if (cpNameAndType == null) {
            int nameIndex = cpDescriptorNameInts[index];
            int descriptorIndex = cpDescriptorTypeInts[index];

            CPUTF8 name = cpUTF8Value(nameIndex);
            CPUTF8 descriptorU = cpSignatureValue(descriptorIndex);
            cpNameAndType = new CPNameAndType(name, descriptorU, index + descrOffset);
            descriptorsToCPNameAndTypes.put(descriptor, cpNameAndType);
        }
        return cpNameAndType;
    }
    
    public CPAnyMemberRef cpAnyMemberValue(int index){
	if (index < 0) throw new IndexOutOfBoundsException(String.valueOf(index));
	if (index < anyMemberMethodOffset) return cpFieldValue(index - anyMemberFieldOffset);
	if (index < anyMemberIMethodOffset) return cpMethodValue(index - anyMemberMethodOffset);
	return cpIMethodValue(index - anyMemberIMethodOffset); // throws ArrayIndexOutOfBoundsException if wrong.
    }
    
    public CPLoadableValue cpLoadableValue(int index){
	if (index < 0) throw new IndexOutOfBoundsException(String.valueOf(index));
	if (index < lVFloatOffset) return cpIntegerValue(index);
	if (index < lVLongOffset) return cpFloatValue(index - lVFloatOffset);
	if (index < lVDoubleOffset) return cpLongValue(index - lVLongOffset);
	if (index < lVStringOffset) return cpDoubleValue(index - lVDoubleOffset);
	if (index < lVClassOffset) return cpStringValue(index - lVStringOffset);
	if (index < lVMethodHandleOffset) return cpClassValue(index - lVClassOffset);
	if (index < lVMethodTypeOffset) return cpMethodHandleValue(index - lVMethodHandleOffset);
	return cpMethodTypeValue(index - lVMethodTypeOffset); // throws ArrayIndexOutOfBoundsException if wrong.
    }
    
    public ClassFileEntry all(int index){
	if (index < 0) throw new IndexOutOfBoundsException(String.valueOf(index));
	if (index < intOffset) return cpUTF8Value(index);
	if (index < floatOffset) return cpIntegerValue(index - intOffset);
	if (index < longOffset) return cpFloatValue(index - floatOffset);
	if (index < doubleOffset) return cpLongValue(index - longOffset);
	if (index < stringOffset) return cpDoubleValue(index - doubleOffset);
	if (index < classOffset) return cpStringValue(index - stringOffset);
	if (index < signatureOffset) return cpClassValue(index - classOffset);
	if (index < descrOffset) return cpSignatureValue(index - signatureOffset);
	if (index < fieldOffset) return cpNameAndTypeValue(index - descrOffset);
	if (index < methodOffset) return cpFieldValue(index - fieldOffset);
	if (index < imethodOffset) return cpMethodValue(index - methodOffset);
	if (index < methodHandleOffset) return cpIMethodValue(index - imethodOffset);
	if (index < methodTypeOffset) return cpMethodHandleValue(index - methodHandleOffset);
	if (index < bootstrapMethodOffset) return cpMethodTypeValue(index - methodTypeOffset);
	if (index < invokeDynamicOffset) return cpBootstrapMethodValue(index - bootstrapMethodOffset);
	return this.cpInvokeDynamicValue(index - invokeDynamicOffset);
    }

    public CPInterfaceMethodRef cpIMethodValue(int index) {
        return new CPInterfaceMethodRef(cpClassValue(cpIMethodClassInts[index]),
                cpNameAndTypeValue(cpIMethodDescriptorInts[index]), index
                        + imethodOffset);
    }

    public CPMethodRef cpMethodValue(int index) {
        return new CPMethodRef(cpClassValue(cpMethodClassInts[index]),
                cpNameAndTypeValue(cpMethodDescriptorInts[index]), index
                        + methodOffset);
    }

    public CPFieldRef cpFieldValue(int index) {
        return new CPFieldRef(cpClassValue(cpFieldClassInts[index]),
                cpNameAndTypeValue(cpFieldDescriptorInts[index]), index
                        + fieldOffset);
    }

    public CPUTF8 cpSignatureValue(int index) {
        int globalIndex;
        if(index >= 0 && index < cpSignatureInts.length && cpSignatureInts[index] != -1) {
            globalIndex = cpSignatureInts[index];
        } else {
            globalIndex = index + signatureOffset;
        }
	if (index < 0 || index >= cpSignature.length) 
	    throw new ArrayIndexOutOfBoundsException(
		    "Index: " + index + " Length: "+ cpSignature.length);
        String string = cpSignature[index];
        CPUTF8 cpUTF8 = (CPUTF8) stringsToCPUTF8.get(string);
        if(cpUTF8 == null) {
            cpUTF8 = new CPUTF8(string, globalIndex);
            stringsToCPUTF8.put(string, cpUTF8);
        }
        return cpUTF8;
    }

    public CPNameAndType cpNameAndTypeValue(String descriptor) {
        CPNameAndType cpNameAndType = (CPNameAndType) descriptorsToCPNameAndTypes
            .get(descriptor);
        if (cpNameAndType == null) {
        	Integer index = (Integer)mapDescriptor.get(descriptor);
        	if(index != null) {
        		return cpNameAndTypeValue(index.intValue());
            }
            int colon = descriptor.indexOf(':');
            String nameString = descriptor.substring(0, colon);
            String descriptorString = descriptor.substring(colon + 1);

            CPUTF8 name = cpUTF8Value(nameString, true);
            CPUTF8 descriptorU = cpUTF8Value(descriptorString, true);
            cpNameAndType = new CPNameAndType(name, descriptorU, -1 + descrOffset);
            descriptorsToCPNameAndTypes.put(descriptor, cpNameAndType);
        }
        return cpNameAndType;
    }

    public int[] getCpDescriptorNameInts() {
        return cpDescriptorNameInts;
    }

    public int[] getCpDescriptorTypeInts() {
        return cpDescriptorTypeInts;
    }

    CPMethodHandle cpMethodHandleValue(int index) {
	return new CPMethodHandle(cpMethodHandleRefkindInts[index], 
		cpMethodHandleMember[index], index + methodHandleOffset);
    }

    CPMethodType cpMethodTypeValue(int index) {
	return new CPMethodType(cpSignatureValue(cpMethodTypeInts[index]),
		index + methodTypeOffset);
    }
    
    CPBootstrapMethod cpBootstrapMethodValue(int index) {
	int length = cpBootstrapMethodArg[index].length;
	CPLoadableValue [] bootstrapMethodArg = new CPLoadableValue [length];
	for (int i = 0;  i < length; i++ ){
	    int cpLoadableValueIndex = cpBootstrapMethodArg[index][i];
	    bootstrapMethodArg[i] = cpLoadableValue(cpLoadableValueIndex);
	}
	return new CPBootstrapMethod(
		cpMethodHandleValue(cpBootstrapMethodRef[index]),
		cpBootstrapMethodArgInts[index],
		bootstrapMethodArg, index
	);
    }

    CPInvokeDynamic cpInvokeDynamicValue(int index) {
	return new CPInvokeDynamic(
		cpBootstrapMethodValue(cpInvokeDynamicSpec[index]),
		cpNameAndTypeValue(cpInvokeDynamicDescr[index]),
		index + invokeDynamicOffset );
    }
    
    ClassFileEntry cpModuleValue(int index) {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    ClassFileEntry cpPackageValue(int index) {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    ClassFileEntry cpDynamicValue(int index) {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}