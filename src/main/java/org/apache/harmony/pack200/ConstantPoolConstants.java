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
 * Constant pool tag constants as defined in the Java Virtual Machine
 * Specification (JVMS), Table 4.4-B.
 *
 * <p>These values are the {@code tag} byte written at the start of each
 * constant pool entry in a Java class file.  They are valid for all class
 * files supported by this library (Java 1 through Java 27, major versions
 * 45–71).
 *
 * <p>The numeric values match the JVMS exactly; they must not be changed.
 * Tags 2, 13, and 14 are currently unassigned by the specification and are
 * therefore not defined here.
 *
 * <p>References:
 * <ul>
 *   <li>JVMS §4.4 – introduced UTF8, Integer, Float, Long, Double, Class,
 *       String, Fieldref, Methodref, InterfaceMethodref, NameAndType</li>
 *   <li>JVMS §4.4.8 – MethodHandle, MethodType, InvokeDynamic (Java 7,
 *       JSR 292)</li>
 *   <li>JVMS §4.4.11 – Dynamic (Java 11, JEP 309)</li>
 *   <li>JVMS §4.4.12 – Module, Package (Java 9, JEP 261)</li>
 * </ul>
 */
public final class ConstantPoolConstants {

    private ConstantPoolConstants() {}

    // -----------------------------------------------------------------------
    // Core constant pool tags (present since Java 1.0.2)
    // -----------------------------------------------------------------------

    /**
     * {@code CONSTANT_Utf8} (tag 1).
     * A modified-UTF-8 string value used for class/interface names, field and
     * method descriptors, string literals, and attribute names.
     */
    public static final byte CP_UTF8 = 1;

    /**
     * {@code CONSTANT_Integer} (tag 3).
     * A 4-byte big-endian {@code int} constant.
     */
    public static final byte CP_Integer = 3;

    /**
     * {@code CONSTANT_Float} (tag 4).
     * A 4-byte big-endian IEEE 754 single-precision {@code float} constant.
     */
    public static final byte CP_Float = 4;

    /**
     * {@code CONSTANT_Long} (tag 5).
     * An 8-byte big-endian {@code long} constant.  Takes up two constant pool
     * slots.
     */
    public static final byte CP_Long = 5;

    /**
     * {@code CONSTANT_Double} (tag 6).
     * An 8-byte big-endian IEEE 754 double-precision {@code double} constant.
     * Takes up two constant pool slots.
     */
    public static final byte CP_Double = 6;

    /**
     * {@code CONSTANT_Class} (tag 7).
     * A symbolic reference to a class or interface.
     */
    public static final byte CP_Class = 7;

    /**
     * {@code CONSTANT_String} (tag 8).
     * A {@code java.lang.String} literal.
     */
    public static final byte CP_String = 8;

    /**
     * {@code CONSTANT_Fieldref} (tag 9).
     * A symbolic reference to a field.
     */
    public static final byte CP_Fieldref = 9;

    /**
     * {@code CONSTANT_Methodref} (tag 10).
     * A symbolic reference to a method of a class.
     */
    public static final byte CP_Methodref = 10;

    /**
     * {@code CONSTANT_InterfaceMethodref} (tag 11).
     * A symbolic reference to a method of an interface.
     */
    public static final byte CP_InterfaceMethodref = 11;

    /**
     * {@code CONSTANT_NameAndType} (tag 12).
     * A field or method name together with its descriptor.
     */
    public static final byte CP_NameAndType = 12;

    // -----------------------------------------------------------------------
    // Invokedynamic / method-handle pool tags (Java 7+, JSR 292)
    // -----------------------------------------------------------------------

    /**
     * {@code CONSTANT_MethodHandle} (tag 15).
     * A direct reference to a field, method, or constructor represented as a
     * method handle.  Introduced in Java 7 as part of JSR 292.
     */
    public static final byte CP_MethodHandle = 15;

    /**
     * {@code CONSTANT_MethodType} (tag 16).
     * A method descriptor as a {@code java.lang.invoke.MethodType}.
     * Introduced in Java 7 as part of JSR 292.
     */
    public static final byte CP_MethodType = 16;

    /**
     * {@code CONSTANT_Dynamic} (tag 17).
     * A dynamically-computed constant value resolved via a bootstrap method.
     * Introduced in Java 11 (JEP 309).
     *
     * <p>This is supplementary to the Pack200 standard (Java 11+).
     */
    public static final byte CP_Dynamic = 17;

    /**
     * {@code CONSTANT_InvokeDynamic} (tag 18).
     * Also known as {@code CONSTANT_DynamicCallSite}.  A dynamically-computed
     * call-site descriptor resolved via a bootstrap method.
     * Introduced in Java 7 (JSR 292).
     */
    public static final byte CP_InvokeDynamic = 18;

    // -----------------------------------------------------------------------
    // Module-system pool tags (Java 9+, JEP 261)
    // -----------------------------------------------------------------------

    /**
     * {@code CONSTANT_Module} (tag 19).
     * A symbolic reference to a module.
     * Introduced in Java 9 (JEP 261); supplementary to the Pack200 standard.
     */
    public static final byte CP_Module = 19;

    /**
     * {@code CONSTANT_Package} (tag 20).
     * A symbolic reference to a package exported or opened by a module.
     * Introduced in Java 9 (JEP 261); supplementary to the Pack200 standard.
     */
    public static final byte CP_Package = 20;
}
