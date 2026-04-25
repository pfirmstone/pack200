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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.objectweb.asm.Handle;

import org.objectweb.asm.Type;

/**
 * Pack200 Constant Pool Bands
 */
class CpBands extends BandSet {

    // Don't need to include default attribute names in the constant pool bands
    private final Set defaultAttributeNames = new HashSet();

    private final Set cp_Utf8 = new TreeSet();
    private final Set cp_Int = new TreeSet();
    private final Set cp_Float = new TreeSet();
    private final Set cp_Long = new TreeSet();
    private final Set cp_Double = new TreeSet();
    private final Set cp_String = new TreeSet();
    private final Set cp_Class = new TreeSet();
    private final Set cp_Signature = new TreeSet();
    private final Set cp_Descr = new TreeSet();
    private final Set cp_Field = new TreeSet();
    private final Set cp_Method = new TreeSet();
    private final Set cp_Imethod = new TreeSet();
    private final Set cp_MethodHandle = new TreeSet();
    private final Set cp_MethodType = new TreeSet();
    private final Set cp_BootstrapMethod = new TreeSet();
    private final Set cp_InvokeDynamic = new TreeSet();
    private final Set cp_Module = new TreeSet();
    private final Set cp_Package = new TreeSet();

    private final Map<String, CPUTF8> stringsToCpUtf8 = new HashMap<String, CPUTF8>();
    private final Map stringsToCpNameAndType = new HashMap();
    private final Map stringsToCpClass = new HashMap();
    private final Map stringsToCpSignature = new HashMap();
    private final Map stringsToCpMethod = new HashMap();
    private final Map stringsToCpField = new HashMap();
    private final Map stringsToCpIMethod = new HashMap();

    private final Map objectsToCPConstant = new HashMap();

    private final Map<String, CPMethodHandle> keysToCpMethodHandle = new HashMap<String, CPMethodHandle>();
    private final Map<String, CPMethodType> keysToCpMethodType = new HashMap<String, CPMethodType>();
    private final Map<String, CPBootstrapMethod> keysToCpBootstrapMethod = new HashMap<String, CPBootstrapMethod>();
    private final Map<String, CPInvokeDynamic> keysToCpInvokeDynamic = new HashMap<String, CPInvokeDynamic>();

    private final Segment segment;

    public CpBands(Segment segment, int effort) {
	super(effort, segment.getSegmentHeader());
	this.segment = segment;
	defaultAttributeNames.add("AnnotationDefault");
	defaultAttributeNames.add("RuntimeVisibleAnnotations");
	defaultAttributeNames.add("RuntimeInvisibleAnnotations");
	defaultAttributeNames.add("RuntimeVisibleParameterAnnotations");
	defaultAttributeNames.add("RuntimeInvisibleParameterAnnotations");
	defaultAttributeNames.add("Code");
	defaultAttributeNames.add("LineNumberTable");
	defaultAttributeNames.add("LocalVariableTable");
	defaultAttributeNames.add("LocalVariableTypeTable");
	defaultAttributeNames.add("ConstantValue");
	defaultAttributeNames.add("Deprecated");
	defaultAttributeNames.add("EnclosingMethod");
	defaultAttributeNames.add("Exceptions");
	defaultAttributeNames.add("InnerClasses");
	defaultAttributeNames.add("Signature");
	defaultAttributeNames.add("SourceFile");
	// Java 6
	defaultAttributeNames.add("StackMapTable");
	// Java 7
	defaultAttributeNames.add("BootstrapMethods");
	// Java 8
	defaultAttributeNames.add("RuntimeVisibleTypeAnnotations");
	defaultAttributeNames.add("RuntimeInvisibleTypeAnnotations");
	defaultAttributeNames.add("MethodParameters");
	// Java 9
	defaultAttributeNames.add("Module");
	defaultAttributeNames.add("ModulePackages");
	defaultAttributeNames.add("ModuleMainClass");
    }

    public void pack(OutputStream out) throws IOException, Pack200Exception {
	PackingUtils.log("Writing constant pool bands...");
	writeCpUtf8(out);
	writeCpInt(out);
	writeCpFloat(out);
	writeCpLong(out);
	writeCpDouble(out);
	writeCpString(out);
	writeCpClass(out);
	writeCpSignature(out);
	writeCpDescr(out);
	writeCpMethodOrField(cp_Field, out, "cp_Field");
	writeCpMethodOrField(cp_Method, out, "cp_Method");
	writeCpMethodOrField(cp_Imethod, out, "cp_Imethod");
	if (!cp_MethodHandle.isEmpty()) {
	    writeCpMethodHandle(out);
	    writeCpMethodType(out);
	    writeCpBootstrapMethod(out);
	    writeCpInvokeDynamic(out);
	}
    }

    private void writeCpUtf8(OutputStream out) throws IOException,
	    Pack200Exception {
	PackingUtils.log("Writing " + cp_Utf8.size() + " UTF8 entries...");
	int[] cpUtf8Prefix = new int[cp_Utf8.size() - 2];
	int[] cpUtf8Suffix = new int[cp_Utf8.size() - 1];
	List chars = new ArrayList();
	List bigSuffix = new ArrayList();
	List bigChars = new ArrayList();
	Object[] cpUtf8Array = cp_Utf8.toArray();
	String first = ((CPUTF8) cpUtf8Array[1]).getUnderlyingString();
	cpUtf8Suffix[0] = first.length();
	addCharacters(chars, first.toCharArray());
	for (int i = 2; i < cpUtf8Array.length; i++) {
	    char[] previous = ((CPUTF8) cpUtf8Array[i - 1])
		    .getUnderlyingString().toCharArray();
	    String currentStr = ((CPUTF8) cpUtf8Array[i]).getUnderlyingString();
	    char[] current = currentStr.toCharArray();
	    int prefix = 0;
	    for (int j = 0; j < previous.length; j++) {
		if (previous[j] == current[j]) {
		    prefix++;
		} else {
		    break;
		}
	    }
	    cpUtf8Prefix[i - 2] = prefix;
	    currentStr = currentStr.substring(prefix);
	    char[] suffix = currentStr.toCharArray();
	    if (suffix.length > 1000) { // big suffix (1000 is arbitrary - can we
		// do better?)
		cpUtf8Suffix[i - 1] = 0;
		bigSuffix.add(Integer.valueOf(suffix.length));
		addCharacters(bigChars, suffix);
	    } else {
		cpUtf8Suffix[i - 1] = suffix.length;
		addCharacters(chars, suffix);
	    }
	}
	int[] cpUtf8Chars = new int[chars.size()];
	int[] cpUtf8BigSuffix = new int[bigSuffix.size()];
	int[][] cpUtf8BigChars = new int[bigSuffix.size()][];
	for (int i = 0; i < cpUtf8Chars.length; i++) {
	    cpUtf8Chars[i] = ((Character) chars.get(i)).charValue();
	}
	for (int i = 0; i < cpUtf8BigSuffix.length; i++) {
	    int numBigChars = ((Integer) bigSuffix.get(i)).intValue();
	    cpUtf8BigSuffix[i] = numBigChars;
	    cpUtf8BigChars[i] = new int[numBigChars];
	    for (int j = 0; j < numBigChars; j++) {
		cpUtf8BigChars[i][j] = ((Character) bigChars.remove(0)).charValue();
	    }
	}

	byte[] encodedBand = encodeBandInt("cpUtf8Prefix", cpUtf8Prefix, Codec.DELTA5);
	out.write(encodedBand);
	PackingUtils.log("Wrote " + encodedBand.length
		+ " bytes from cpUtf8Prefix[" + cpUtf8Prefix.length + "]");

	encodedBand = encodeBandInt("cpUtf8Suffix", cpUtf8Suffix, Codec.UNSIGNED5);
	out.write(encodedBand);
	PackingUtils.log("Wrote " + encodedBand.length
		+ " bytes from cpUtf8Suffix[" + cpUtf8Suffix.length + "]");

	encodedBand = encodeBandInt("cpUtf8Chars", cpUtf8Chars, Codec.CHAR3);
	out.write(encodedBand);
	PackingUtils.log("Wrote " + encodedBand.length
		+ " bytes from cpUtf8Chars[" + cpUtf8Chars.length + "]");

	encodedBand = encodeBandInt("cpUtf8BigSuffix", cpUtf8BigSuffix,
		Codec.DELTA5);
	out.write(encodedBand);
	PackingUtils.log("Wrote " + encodedBand.length
		+ " bytes from cpUtf8BigSuffix[" + cpUtf8BigSuffix.length + "]");

	for (int i = 0; i < cpUtf8BigChars.length; i++) {
	    encodedBand = encodeBandInt("cpUtf8BigChars " + i,
		    cpUtf8BigChars[i], Codec.DELTA5);
	    out.write(encodedBand);
	    PackingUtils.log("Wrote " + encodedBand.length
		    + " bytes from cpUtf8BigChars" + i + "["
		    + cpUtf8BigChars[i].length + "]");
	}
    }

    private void addCharacters(List chars, char[] charArray) {
	for (int i = 0; i < charArray.length; i++) {
	    chars.add(Character.valueOf(charArray[i]));
	}
    }

    private void writeCpInt(OutputStream out) throws IOException,
	    Pack200Exception {
	PackingUtils.log("Writing " + cp_Int.size() + " Integer entries...");
	int[] cpInt = new int[cp_Int.size()];
	int i = 0;
	for (Iterator iterator = cp_Int.iterator(); iterator.hasNext();) {
	    CPInt integer = (CPInt) iterator.next();
	    cpInt[i] = integer.getInt();
	    i++;
	}
	byte[] encodedBand = encodeBandInt("cp_Int", cpInt, Codec.UDELTA5);
	out.write(encodedBand);
	PackingUtils.log("Wrote " + encodedBand.length
		+ " bytes from cp_Int[" + cpInt.length + "]");
    }

    private void writeCpFloat(OutputStream out) throws IOException,
	    Pack200Exception {
	PackingUtils.log("Writing " + cp_Float.size() + " Float entries...");
	int[] cpFloat = new int[cp_Float.size()];
	int i = 0;
	for (Iterator iterator = cp_Float.iterator(); iterator.hasNext();) {
	    CPFloat fl = (CPFloat) iterator.next();
	    cpFloat[i] = Float.floatToIntBits(fl.getFloat());
	    i++;
	}
	byte[] encodedBand = encodeBandInt("cp_Float", cpFloat, Codec.UDELTA5);
	out.write(encodedBand);
	PackingUtils.log("Wrote " + encodedBand.length
		+ " bytes from cp_Float[" + cpFloat.length + "]");
    }

    private void writeCpLong(OutputStream out) throws IOException,
	    Pack200Exception {
	PackingUtils.log("Writing " + cp_Long.size() + " Long entries...");
	int[] highBits = new int[cp_Long.size()];
	int[] loBits = new int[cp_Long.size()];
	int i = 0;
	for (Iterator iterator = cp_Long.iterator(); iterator.hasNext();) {
	    CPLong lng = (CPLong) iterator.next();
	    long l = lng.getLong();
	    highBits[i] = (int) (l >> 32);
	    loBits[i] = (int) l;
	    i++;
	}
	byte[] encodedBand = encodeBandInt("cp_Long_hi", highBits, Codec.UDELTA5);
	out.write(encodedBand);
	PackingUtils.log("Wrote " + encodedBand.length
		+ " bytes from cp_Long_hi[" + highBits.length + "]");

	encodedBand = encodeBandInt("cp_Long_lo", loBits, Codec.DELTA5);
	out.write(encodedBand);
	PackingUtils.log("Wrote " + encodedBand.length
		+ " bytes from cp_Long_lo[" + loBits.length + "]");
    }

    private void writeCpDouble(OutputStream out) throws IOException,
	    Pack200Exception {
	PackingUtils.log("Writing " + cp_Double.size() + " Double entries...");
	int[] highBits = new int[cp_Double.size()];
	int[] loBits = new int[cp_Double.size()];
	int i = 0;
	for (Iterator iterator = cp_Double.iterator(); iterator.hasNext();) {
	    CPDouble dbl = (CPDouble) iterator.next();
	    long l = Double.doubleToLongBits(dbl.getDouble());
	    highBits[i] = (int) (l >> 32);
	    loBits[i] = (int) l;
	    i++;
	}
	byte[] encodedBand = encodeBandInt("cp_Double_hi", highBits, Codec.UDELTA5);
	out.write(encodedBand);
	PackingUtils.log("Wrote " + encodedBand.length
		+ " bytes from cp_Double_hi[" + highBits.length + "]");

	encodedBand = encodeBandInt("cp_Double_lo", loBits, Codec.DELTA5);
	out.write(encodedBand);
	PackingUtils.log("Wrote " + encodedBand.length
		+ " bytes from cp_Double_lo[" + loBits.length + "]");
    }

    private void writeCpString(OutputStream out) throws IOException,
	    Pack200Exception {
	PackingUtils.log("Writing " + cp_String.size() + " String entries...");
	int[] cpString = new int[cp_String.size()];
	int i = 0;
	for (Iterator iterator = cp_String.iterator(); iterator.hasNext();) {
	    CPString cpStr = (CPString) iterator.next();
	    cpString[i] = cpStr.getIndexInCpUtf8();
	    i++;
	}
	byte[] encodedBand = encodeBandInt("cpString", cpString, Codec.UDELTA5);
	out.write(encodedBand);
	PackingUtils.log("Wrote " + encodedBand.length
		+ " bytes from cpString[" + cpString.length + "]");
    }

    private void writeCpClass(OutputStream out) throws IOException,
	    Pack200Exception {
	PackingUtils.log("Writing " + cp_Class.size() + " Class entries...");
	int[] cpClass = new int[cp_Class.size()];
	int i = 0;
	for (Iterator iterator = cp_Class.iterator(); iterator.hasNext();) {
	    CPClass cpCl = (CPClass) iterator.next();
	    cpClass[i] = cpCl.getIndexInCpUtf8();
	    i++;
	}
	byte[] encodedBand = encodeBandInt("cpClass", cpClass, Codec.UDELTA5);
	out.write(encodedBand);
	PackingUtils.log("Wrote " + encodedBand.length
		+ " bytes from cpClass[" + cpClass.length + "]");
    }

    private void writeCpSignature(OutputStream out) throws IOException,
	    Pack200Exception {
	PackingUtils.log("Writing " + cp_Signature.size() + " Signature entries...");
	int[] cpSignatureForm = new int[cp_Signature.size()];
	List classes = new ArrayList();
	int i = 0;
	for (Iterator iterator = cp_Signature.iterator(); iterator.hasNext();) {
	    CPSignature cpS = (CPSignature) iterator.next();
	    classes.addAll(cpS.getClasses());
	    cpSignatureForm[i] = cpS.getIndexInCpUtf8();
	    i++;
	}
	int[] cpSignatureClasses = new int[classes.size()];
	for (int j = 0; j < cpSignatureClasses.length; j++) {
	    cpSignatureClasses[j] = ((CPClass) classes.get(j)).getIndex();
	}

	byte[] encodedBand = encodeBandInt("cpSignatureForm", cpSignatureForm,
		Codec.DELTA5);
	out.write(encodedBand);
	PackingUtils.log("Wrote " + encodedBand.length
		+ " bytes from cpSignatureForm[" + cpSignatureForm.length + "]");

	encodedBand = encodeBandInt("cpSignatureClasses", cpSignatureClasses,
		Codec.UDELTA5);
	out.write(encodedBand);
	PackingUtils.log("Wrote " + encodedBand.length
		+ " bytes from cpSignatureClasses[" + cpSignatureClasses.length + "]");
    }
    
    private void writeCpDescr(OutputStream out) throws IOException,
	    Pack200Exception {
	PackingUtils.log("Writing " + cp_Descr.size()
		+ " Descriptor entries...");
	int[] cpDescrName = new int[cp_Descr.size()];
	int[] cpDescrType = new int[cp_Descr.size()];
	int i = 0;
	for (Iterator iterator = cp_Descr.iterator(); iterator.hasNext();) {
	    CPNameAndType nameAndType = (CPNameAndType) iterator.next();
	    cpDescrName[i] = nameAndType.getNameIndex();
	    cpDescrType[i] = nameAndType.getTypeIndex();
	    i++;
	}

	byte[] encodedBand = encodeBandInt("cp_Descr_Name", cpDescrName,
		Codec.DELTA5);
	out.write(encodedBand);
	PackingUtils.log("Wrote " + encodedBand.length
		+ " bytes from cp_Descr_Name[" + cpDescrName.length + "]");

	encodedBand = encodeBandInt("cp_Descr_Type", cpDescrType, Codec.UDELTA5);
	out.write(encodedBand);
	PackingUtils.log("Wrote " + encodedBand.length
		+ " bytes from cp_Descr_Type[" + cpDescrType.length + "]");
    }

    private void writeCpMethodOrField(Set cp, OutputStream out, String name)
	    throws IOException, Pack200Exception {
	PackingUtils.log("Writing " + cp.size()
		+ " Method and Field entries...");
	int[] cp_methodOrField_class = new int[cp.size()];
	int[] cp_methodOrField_desc = new int[cp.size()];
	int i = 0;
	for (Iterator iterator = cp.iterator(); iterator.hasNext();) {
	    CPMethodOrField mOrF = (CPMethodOrField) iterator.next();
	    cp_methodOrField_class[i] = mOrF.getClassIndex();
	    cp_methodOrField_desc[i] = mOrF.getDescIndex();
	    i++;
	}
	byte[] encodedBand = encodeBandInt(name + "_class",
		cp_methodOrField_class, Codec.DELTA5);
	out.write(encodedBand);
	PackingUtils.log("Wrote " + encodedBand.length + " bytes from "
		+ name + "_class[" + cp_methodOrField_class.length + "]");

	encodedBand = encodeBandInt(name + "_desc", cp_methodOrField_desc,
		Codec.UDELTA5);
	out.write(encodedBand);
	PackingUtils.log("Wrote " + encodedBand.length + " bytes from "
		+ name + "_desc[" + cp_methodOrField_desc.length + "]");
    }

    /**
     * All input classes for the segment have now been read in, so this method
     * is called so that this class can calculate/complete anything it could not
     * do while classes were being read.
     */
    public void finaliseBands() {
	addCPUtf8("");
	removeSignaturesFromCpUTF8();
	addIndices();
	segmentHeader.setCp_Utf8_count(cp_Utf8.size());
	segmentHeader.setCp_Int_count(cp_Int.size());
	segmentHeader.setCp_Float_count(cp_Float.size());
	segmentHeader.setCp_Long_count(cp_Long.size());
	segmentHeader.setCp_Double_count(cp_Double.size());
	segmentHeader.setCp_String_count(cp_String.size());
	segmentHeader.setCp_Class_count(cp_Class.size());
	segmentHeader.setCp_Signature_count(cp_Signature.size());
	segmentHeader.setCp_Descr_count(cp_Descr.size());
	segmentHeader.setCp_Field_count(cp_Field.size());
	segmentHeader.setCp_Method_count(cp_Method.size());
	segmentHeader.setCp_Imethod_count(cp_Imethod.size());
	segmentHeader.setCp_MethodHandle_count(cp_MethodHandle.size());
	segmentHeader.setCp_MethodType_count(cp_MethodType.size());
	segmentHeader.setCp_BootstrapMethod_count(cp_BootstrapMethod.size());
	segmentHeader.setCp_InvokeDynamic_count(cp_InvokeDynamic.size());
    }

    private void removeSignaturesFromCpUTF8() {
	for (Iterator iterator = cp_Signature.iterator(); iterator.hasNext();) {
	    CPSignature signature = (CPSignature) iterator.next();
	    String sigStr = signature.getUnderlyingString();
	    CPUTF8 utf8 = signature.getSignatureForm();
	    String form = utf8.getUnderlyingString();
	    if (!sigStr.equals(form)) {
		removeCpUtf8(sigStr);
	    }
	}
    }

    private void addIndices() {
	Set[] sets = new Set[]{cp_Utf8, cp_Int, cp_Float, cp_Long, cp_Double,
	    cp_String, cp_Class, cp_Signature, cp_Descr, cp_Field,
	    cp_Method, cp_Imethod, cp_MethodHandle, cp_MethodType,
	    cp_BootstrapMethod, cp_InvokeDynamic};
	for (int i = 0; i < sets.length; i++) {
	    int j = 0;
	    for (Iterator iterator = sets[i].iterator(); iterator.hasNext();) {
		ConstantPoolEntry entry = (ConstantPoolEntry) iterator.next();
		entry.setIndex(j);
		j++;
	    }
	}
	Map classNameToIndex = new HashMap();
	for (Iterator iterator = cp_Field.iterator(); iterator.hasNext();) {
	    CPMethodOrField mOrF = (CPMethodOrField) iterator.next();
	    CPClass className = mOrF.getClassName();
	    Integer index = (Integer) classNameToIndex.get(className);
	    if (index == null) {
		classNameToIndex.put(className, Integer.valueOf(1));
		mOrF.setIndexInClass(0);
	    } else {
		int theIndex = index.intValue();
		mOrF.setIndexInClass(theIndex);
		classNameToIndex.put(className, Integer.valueOf(theIndex + 1));
	    }
	}
	classNameToIndex.clear();
	Map classNameToConstructorIndex = new HashMap();
	for (Iterator iterator = cp_Method.iterator(); iterator.hasNext();) {
	    CPMethodOrField mOrF = (CPMethodOrField) iterator.next();
	    CPClass className = mOrF.getClassName();
	    Integer index = (Integer) classNameToIndex.get(className);
	    if (index == null) {
		classNameToIndex.put(className, Integer.valueOf(1));
		mOrF.setIndexInClass(0);
	    } else {
		int theIndex = index.intValue();
		mOrF.setIndexInClass(theIndex);
		classNameToIndex.put(className, Integer.valueOf(theIndex + 1));
	    }
	    if (mOrF.getDesc().getName().equals("<init>")) {
		Integer constructorIndex = (Integer) classNameToConstructorIndex.get(className);
		if (constructorIndex == null) {
		    classNameToConstructorIndex.put(className, Integer.valueOf(1));
		    mOrF.setIndexInClassForConstructor(0);
		} else {
		    int theIndex = constructorIndex.intValue();
		    mOrF.setIndexInClassForConstructor(theIndex);
		    classNameToConstructorIndex.put(className, Integer.valueOf(theIndex + 1));
		}
	    }
	}
    }

    private void removeCpUtf8(String string) {
	CPUTF8 utf8 = (CPUTF8) stringsToCpUtf8.get(string);
	if (utf8 != null) {
	    if (stringsToCpClass.get(string) == null) { // don't remove if strings are also in cpclass
		stringsToCpUtf8.remove(string);
		cp_Utf8.remove(utf8);
	    }
	}
    }

    void addCPUtf8(String utf8) {
	getCPUtf8(utf8);
    }

    public CPUTF8 getCPUtf8(String utf8) {
	if (utf8 == null) {
	    return null;
	}
	CPUTF8 cpUtf8 = (CPUTF8) stringsToCpUtf8.get(utf8);
	if (cpUtf8 == null) {
	    cpUtf8 = new CPUTF8(utf8);
	    cp_Utf8.add(cpUtf8);
	    stringsToCpUtf8.put(utf8, cpUtf8);
	}
	return cpUtf8;
    }

    public CPSignature getCPSignature(String signature) {
	if (signature == null) {
	    return null;
	}
	CPSignature cpS = (CPSignature) stringsToCpSignature.get(signature);
	if (cpS == null) {
	    List cpClasses = new ArrayList();
	    CPUTF8 signatureUTF8;
	    if (signature.length() > 1 && signature.indexOf('L') != -1) {
		List classes = new ArrayList();
		char[] chars = signature.toCharArray();
		StringBuffer signatureString = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
		    signatureString.append(chars[i]);
		    if (chars[i] == 'L') {
			StringBuffer className = new StringBuffer();
			for (int j = i + 1; j < chars.length; j++) {
			    char c = chars[j];
			    if (Character.isLetter(c) || Character.isDigit(c)
				    || c == '/' || c == '$' || c == '_') {
				className.append(c);
			    } else {
				classes.add(className.toString());
				i = j - 1;
				break;
			    }
			}
		    }
		}
		removeCpUtf8(signature);
		for (Iterator iterator2 = classes.iterator(); iterator2
			.hasNext();) {
		    String className = (String) iterator2.next();
		    CPClass cpClass = null;
		    if (className != null) {
			className = className.replace('.', '/');
			cpClass = (CPClass) stringsToCpClass.get(className);
			if (cpClass == null) {
			    CPUTF8 cpUtf8 = getCPUtf8(className);
			    cpClass = new CPClass(cpUtf8);
			    cp_Class.add(cpClass);
			    stringsToCpClass.put(className, cpClass);
			}
		    }
		    cpClasses.add(cpClass);
		}

		signatureUTF8 = getCPUtf8(signatureString.toString());
	    } else {
		signatureUTF8 = getCPUtf8(signature);
	    }
	    cpS = new CPSignature(signature, signatureUTF8, cpClasses);
	    cp_Signature.add(cpS);
	    stringsToCpSignature.put(signature, cpS);
	}
	return cpS;
    }

    public CPClass getCPClass(String className) {
	if (className == null) {
	    return null;
	}
	className = className.replace('.', '/');
	CPClass cpClass = (CPClass) stringsToCpClass.get(className);
	if (cpClass == null) {
	    CPUTF8 cpUtf8 = getCPUtf8(className);
	    cpClass = new CPClass(cpUtf8);
	    cp_Class.add(cpClass);
	    stringsToCpClass.put(className, cpClass);
	}
	if (cpClass.isInnerClass()) {
	    segment.getClassBands().currentClassReferencesInnerClass(cpClass);
	}
	return cpClass;
    }

    public void addCPClass(String className) {
	getCPClass(className);
    }

    public CPNameAndType getCPNameAndType(String name, String signature) {
	String descr = name + ":" + signature;
	CPNameAndType nameAndType = (CPNameAndType) stringsToCpNameAndType
		.get(descr);
	if (nameAndType == null) {
	    nameAndType = new CPNameAndType(getCPUtf8(name),
		    getCPSignature(signature));
	    stringsToCpNameAndType.put(descr, nameAndType);
	    cp_Descr.add(nameAndType);
	}
	return nameAndType;
    }

    public CPMethodOrField getCPField(CPClass cpClass, String name, String desc) {
	String key = cpClass.toString() + ":" + name + ":" + desc;
	CPMethodOrField cpF = (CPMethodOrField) stringsToCpField
		.get(key);
	if (cpF == null) {
	    CPNameAndType nAndT = getCPNameAndType(name, desc);
	    cpF = new CPMethodOrField(cpClass, nAndT);
	    cp_Field.add(cpF);
	    stringsToCpField.put(key, cpF);
	}
	return cpF;
    }

    public CPConstant getConstant(Object value) {
	CPConstant constant = (CPConstant) objectsToCPConstant.get(value);
	if (constant == null) {
	    if (value instanceof Integer) {
		constant = new CPInt(((Integer) value).intValue());
		cp_Int.add(constant);
	    } else if (value instanceof Long) {
		constant = new CPLong(((Long) value).longValue());
		cp_Long.add(constant);
	    } else if (value instanceof Float) {
		constant = new CPFloat(((Float) value).floatValue());
		cp_Float.add(constant);
	    } else if (value instanceof Double) {
		constant = new CPDouble(((Double) value).doubleValue());
		cp_Double.add(constant);
	    } else if (value instanceof String) {
		constant = new CPString(getCPUtf8((String) value));
		cp_String.add(constant);
	    } else if (value instanceof Type) {
		int sort = ((Type) value).getSort();
		if (sort == Type.OBJECT) {
		    constant = getCPClass(((Type) value).getClassName());
		} else if (sort == Type.ARRAY) {
		    String className = ((Type) value).getClassName();
		    className = "[L" + className.substring(0, className.length() - 2);
		    while (className.endsWith("[]")) {
			className = "[" + className.substring(0, className.length() - 2);
		    }
		    className += ";";
		    constant = getCPClass(className);
		} else if (sort == Type.METHOD) {
		    // ...
		} else {
		    // throw an exception
		    throw new RuntimeException("Unknown constant " + value);
		}
	    } else if (value instanceof Handle) {
		// ...
	    } else {
		// throw an exception
		throw new RuntimeException("Unknown constant " + value);
	    }
	    objectsToCPConstant.put(value, constant);
	}
	return constant;
    }

    public CPMethodOrField getCPMethod(CPClass cpClass, String name, String desc) {
	String key = cpClass.toString() + ":" + name + ":" + desc;
	CPMethodOrField cpM = (CPMethodOrField) stringsToCpMethod
		.get(key);
	if (cpM == null) {
	    CPNameAndType nAndT = getCPNameAndType(name, desc);
	    cpM = new CPMethodOrField(cpClass, nAndT);
	    cp_Method.add(cpM);
	    stringsToCpMethod.put(key, cpM);
	}
	return cpM;
    }

    public CPMethodOrField getCPIMethod(CPClass cpClass, String name,
	    String desc) {
	String key = cpClass.toString() + ":" + name + ":" + desc;
	CPMethodOrField cpIM = (CPMethodOrField) stringsToCpIMethod
		.get(key);
	if (cpIM == null) {
	    CPNameAndType nAndT = getCPNameAndType(name, desc);
	    cpIM = new CPMethodOrField(cpClass, nAndT);
	    cp_Imethod.add(cpIM);
	    stringsToCpIMethod.put(key, cpIM);
	}
	return cpIM;
    }

    public CPMethodOrField getCPField(String owner, String name, String desc) {
	return getCPField(getCPClass(owner), name, desc);
    }

    public CPMethodOrField getCPMethod(String owner, String name, String desc) {
	return getCPMethod(getCPClass(owner), name, desc);
    }

    public CPMethodOrField getCPIMethod(String owner, String name, String desc) {
	return getCPIMethod(getCPClass(owner), name, desc);
    }

    public boolean existsCpClass(String className) {
	CPClass cpClass = (CPClass) stringsToCpClass.get(className);
	return cpClass != null;
    }

    public CPMethodHandle getCPMethodHandle(int refKind, Handle handle) {
	String key = refKind + ":" + handle.getOwner() + ":" + handle.getName() + ":" + handle.getDesc();
	CPMethodHandle existing = keysToCpMethodHandle.get(key);
	if (existing != null) return existing;
	CPClass owner = getCPClass(handle.getOwner());
	CPMethodOrField member;
	if (refKind <= 4) { // field: H_GETFIELD=1, H_GETSTATIC=2, H_PUTFIELD=3, H_PUTSTATIC=4
	    member = getCPField(owner, handle.getName(), handle.getDesc());
	} else if (refKind == 9) { // H_INVOKEINTERFACE
	    member = getCPIMethod(owner, handle.getName(), handle.getDesc());
	} else { // 5-8: virtual, static, special, newinvokespecial
	    member = getCPMethod(owner, handle.getName(), handle.getDesc());
	}
	CPMethodHandle mh = new CPMethodHandle(refKind, member);
	cp_MethodHandle.add(mh);
	keysToCpMethodHandle.put(key, mh);
	return mh;
    }

    public CPMethodType getCPMethodType(String descriptor) {
	CPMethodType existing = keysToCpMethodType.get(descriptor);
	if (existing != null) return existing;
	CPSignature sig = getCPSignature(descriptor);
	CPMethodType mt = new CPMethodType(sig);
	cp_MethodType.add(mt);
	keysToCpMethodType.put(descriptor, mt);
	return mt;
    }

    public CPBootstrapMethod getCPBootstrapMethod(Handle bsmHandle, Object[] bsmArgs) {
	CPMethodHandle mh = getCPMethodHandle(bsmHandle.getTag(), bsmHandle);
	ConstantPoolEntry[] args = new ConstantPoolEntry[bsmArgs.length];
	for (int i = 0; i < bsmArgs.length; i++) {
	    args[i] = getLoadableConstant(bsmArgs[i]);
	}
	String key = mh.toString() + java.util.Arrays.toString(args);
	CPBootstrapMethod existing = keysToCpBootstrapMethod.get(key);
	if (existing != null) return existing;
	CPBootstrapMethod bsm = new CPBootstrapMethod(mh, args);
	cp_BootstrapMethod.add(bsm);
	keysToCpBootstrapMethod.put(key, bsm);
	return bsm;
    }

    private ConstantPoolEntry getLoadableConstant(Object value) {
	if (value instanceof Handle) {
	    Handle h = (Handle) value;
	    return getCPMethodHandle(h.getTag(), h);
	}
	if (value instanceof Type && ((Type) value).getSort() == Type.METHOD) {
	    return getCPMethodType(((Type) value).getDescriptor());
	}
	return getConstant(value);
    }

    public CPInvokeDynamic getCPInvokeDynamic(String name, String desc, Handle bsm, Object[] bsmArgs) {
	CPBootstrapMethod bootstrapMethod = getCPBootstrapMethod(bsm, bsmArgs);
	CPNameAndType nat = getCPNameAndType(name, desc);
	String key = bootstrapMethod.toString() + ":" + name + ":" + desc;
	CPInvokeDynamic existing = keysToCpInvokeDynamic.get(key);
	if (existing != null) return existing;
	CPInvokeDynamic id = new CPInvokeDynamic(bootstrapMethod, nat);
	cp_InvokeDynamic.add(id);
	keysToCpInvokeDynamic.put(key, id);
	return id;
    }

    private void writeCpMethodHandle(OutputStream out) throws IOException, Pack200Exception {
	PackingUtils.log("Writing " + cp_MethodHandle.size() + " MethodHandle entries...");
	int[] refkind = new int[cp_MethodHandle.size()];
	int[] member = new int[cp_MethodHandle.size()];
	int fieldCount = cp_Field.size();
	int methodCount = cp_Method.size();
	int i = 0;
	for (Iterator it = cp_MethodHandle.iterator(); it.hasNext(); ) {
	    CPMethodHandle mh = (CPMethodHandle) it.next();
	    refkind[i] = mh.getReferenceKind();
	    int rk = mh.getReferenceKind();
	    if (rk <= 4) { // field ref
		member[i] = mh.getMember().getIndex();
	    } else if (rk == 9) { // imethod ref
		member[i] = fieldCount + methodCount + mh.getMember().getIndex();
	    } else { // method ref (5-8)
		member[i] = fieldCount + mh.getMember().getIndex();
	    }
	    i++;
	}
	byte[] encodedBand = encodeBandInt("cp_MethodHandle_refkind", refkind, Codec.DELTA5);
	out.write(encodedBand);
	encodedBand = encodeBandInt("cp_MethodHandle_member", member, Codec.UDELTA5);
	out.write(encodedBand);
    }

    private void writeCpMethodType(OutputStream out) throws IOException, Pack200Exception {
	PackingUtils.log("Writing " + cp_MethodType.size() + " MethodType entries...");
	int[] form = new int[cp_MethodType.size()];
	int i = 0;
	for (Iterator it = cp_MethodType.iterator(); it.hasNext(); ) {
	    CPMethodType mt = (CPMethodType) it.next();
	    form[i++] = mt.getIndexInCpSignature();
	}
	byte[] encodedBand = encodeBandInt("cp_MethodType_form", form, Codec.DELTA5);
	out.write(encodedBand);
    }

    private void writeCpBootstrapMethod(OutputStream out) throws IOException, Pack200Exception {
	PackingUtils.log("Writing " + cp_BootstrapMethod.size() + " BootstrapMethod entries...");
	int[] bsmRef = new int[cp_BootstrapMethod.size()];
	int[] bsmArgCount = new int[cp_BootstrapMethod.size()];
	int intBase = 0;
	int floatBase = cp_Int.size();
	int longBase = floatBase + cp_Float.size();
	int doubleBase = longBase + cp_Long.size();
	int stringBase = doubleBase + cp_Double.size();
	int classBase = stringBase + cp_String.size();
	int mhBase = classBase + cp_Class.size();
	int mtBase = mhBase + cp_MethodHandle.size();
	int i = 0;
	java.util.List<int[]> argBands = new java.util.ArrayList<int[]>();
	for (Iterator it = cp_BootstrapMethod.iterator(); it.hasNext(); ) {
	    CPBootstrapMethod bsm = (CPBootstrapMethod) it.next();
	    bsmRef[i] = bsm.getMethodHandle().getIndex();
	    ConstantPoolEntry[] args = bsm.getArgs();
	    bsmArgCount[i] = args.length;
	    int[] argIndices = new int[args.length];
	    for (int j = 0; j < args.length; j++) {
		argIndices[j] = loadableValueIndex(args[j], intBase, floatBase, longBase,
			doubleBase, stringBase, classBase, mhBase, mtBase);
	    }
	    argBands.add(argIndices);
	    i++;
	}
	byte[] encodedBand = encodeBandInt("cp_BootstrapMethod_ref", bsmRef, Codec.DELTA5);
	out.write(encodedBand);
	encodedBand = encodeBandInt("cp_BootstrapMethod_arg_count", bsmArgCount, Codec.UDELTA5);
	out.write(encodedBand);
	for (int[] argBand : argBands) {
	    encodedBand = encodeBandInt("cp_BootstrapMethod_arg", argBand, Codec.DELTA5);
	    out.write(encodedBand);
	}
    }

    private int loadableValueIndex(ConstantPoolEntry entry, int intBase, int floatBase,
	    int longBase, int doubleBase, int stringBase, int classBase, int mhBase, int mtBase) {
	if (entry instanceof CPInt) return intBase + entry.getIndex();
	if (entry instanceof CPFloat) return floatBase + entry.getIndex();
	if (entry instanceof CPLong) return longBase + entry.getIndex();
	if (entry instanceof CPDouble) return doubleBase + entry.getIndex();
	if (entry instanceof CPString) return stringBase + entry.getIndex();
	if (entry instanceof CPClass) return classBase + entry.getIndex();
	if (entry instanceof CPMethodHandle) return mhBase + entry.getIndex();
	if (entry instanceof CPMethodType) return mtBase + entry.getIndex();
	throw new IllegalArgumentException("Not a loadable value: " + entry);
    }

    private void writeCpInvokeDynamic(OutputStream out) throws IOException, Pack200Exception {
	PackingUtils.log("Writing " + cp_InvokeDynamic.size() + " InvokeDynamic entries...");
	int[] spec = new int[cp_InvokeDynamic.size()];
	int[] descr = new int[cp_InvokeDynamic.size()];
	int i = 0;
	for (Iterator it = cp_InvokeDynamic.iterator(); it.hasNext(); ) {
	    CPInvokeDynamic id = (CPInvokeDynamic) it.next();
	    spec[i] = id.getBootstrapMethod().getIndex();
	    descr[i] = id.getNameAndType().getIndex();
	    i++;
	}
	byte[] encodedBand = encodeBandInt("cp_InvokeDynamic_spec", spec, Codec.DELTA5);
	out.write(encodedBand);
	encodedBand = encodeBandInt("cp_InvokeDynamic_descr", descr, Codec.UDELTA5);
	out.write(encodedBand);
    }

}
