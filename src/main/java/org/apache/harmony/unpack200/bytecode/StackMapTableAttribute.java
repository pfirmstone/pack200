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

/**
 *
 * @author peter
 */
public class StackMapTableAttribute extends Attribute {
    
    private static CPUTF8 ATTRIBUTE_NAME; // u2

    public static void setAttributeName(CPUTF8 cpUTF8Value) {
	ATTRIBUTE_NAME = cpUTF8Value;
    }
    private final StackMapFrame[] entries;

    public StackMapTableAttribute(StackMapFrame [] entries ) {
	super(ATTRIBUTE_NAME);
	this.entries = entries;
    }

    @Override
    protected int getLength() {
	int length = 2; // number of entries excluding first six bytes (attribute name and length).
	for (int i = 0, len = entries.length; i < len; i++){
	    length += entries[i].getLength();
	}
	return length;
    }

    @Override
    protected void writeBody(DataOutputStream dos) throws IOException {
	// we don't write out attribute name or length, super does that.
	dos.writeShort(entries.length); // u2	number_of_entries
	for (int i = 0, len = entries.length; i < len; i++){
	    entries[i].doWrite(dos);
	}
    }
    
    @Override
    protected void resolve(ClassConstantPool pool) {
	super.resolve(pool);
	for (int i = 0, len = entries.length; i < len; i++){
	    entries[i].resolve(pool);
	}
    }

    @Override
    public String toString() {
	return "StackMapTable_attribute";
    }

    public static abstract class StackMapFrame extends ClassFileEntry {
	protected final int frame_type; // u1

	/**
	 * Constructor for the initial frame (second frame, first is implicit).
	 * @param frameType
	 */
	protected StackMapFrame(int frameType){
	    this.frame_type = frameType;
	}
	
	public void doWrite(DataOutputStream dos) throws IOException {
	    dos.writeByte(frame_type);
	    writeBody(dos);
	}
	
	@Override
	public boolean equals(Object arg0) {
	    if (this == arg0) return true;
	    if (!(arg0 instanceof StackMapFrame)) return false;
	    return frame_type == ((StackMapFrame) arg0).frame_type;
	}

	@Override
	public int hashCode() {
	    int hash = 3;
	    hash = 11 * hash + this.frame_type;
	    return hash;
	}
	
	public abstract void writeBody(DataOutputStream dos) throws IOException;
	
	public abstract int offsetDelta();
	
	public abstract int getLength();
    }
    
    public static class SameFrame extends StackMapFrame {

	/**
	 * 
	 * @param frameType 0 - 63
	 */
	public SameFrame(int frameType) {
	    super(frameType);
	}

	@Override
	public void writeBody(DataOutputStream dos) throws IOException {
	    // nothing to do here.
	}

	@Override
	public int offsetDelta() {
	    return frame_type;
	}

	@Override
	public int getLength() {
	    return 1;
	}

	
	@Override
	public String toString() {
	    return "same_frame";
	}
	
    }
    
    public static class SameLocals1StackItemFrame extends StackMapFrame {
	private final VerificationTypeInfo[] stack;

	/**
	 * 
	 * @param frameType 64 - 127
	 * @param stack 
	 */
	public SameLocals1StackItemFrame(int frameType, VerificationTypeInfo[] stack ) {
	    super(frameType);
	    this.stack = stack;
	}

	@Override
	public void writeBody(DataOutputStream dos) throws IOException {
	    for (int i = 0, l = stack.length; i < l; i++){
		stack[i].doWrite(dos);
	    }
	}

	@Override
	public int offsetDelta() {
	    return frame_type - 64;
	}

	@Override
	public int getLength() {
	    return 1 /*frame_type*/ + stack.length;
	}
	
	@Override
	protected void resolve(ClassConstantPool pool) {
	    super.resolve(pool);
	    for (int i = 0, l = stack.length; i < l; i++){
		stack[i].resolve(pool);
	    }
	}

	@Override
	public String toString() {
	    return "same_locals_1_stack_item_frame";
	}
	
    }
    
    public static class SameLocals1StackItemFrameExtended extends StackMapFrame {
	private final int offset_delta; // u2
	private final VerificationTypeInfo[] stack;

	public SameLocals1StackItemFrameExtended(int offset_delta, VerificationTypeInfo[] stack) {
	    super(247);
	    this.offset_delta = offset_delta;
	    this.stack = stack;
	}

	@Override
	public void writeBody(DataOutputStream dos) throws IOException {
	    dos.writeShort(frame_type);
	    for (int i = 0, l = stack.length; i < l; i++){
		stack[i].writeBody(dos);
	    }
	}

	@Override
	public int offsetDelta() {
	    return offset_delta;
	}

	@Override
	public int getLength() {
	    return 1 /*frame_type*/ + 2 /*offset_delta*/+ stack.length;
	}
	
	@Override
	protected void resolve(ClassConstantPool pool) {
	    super.resolve(pool);
	    for (int i = 0, l = stack.length; i < l; i++){
		stack[i].resolve(pool);
	    }
	}

	@Override
	public String toString() {
	    return "same_locals_1_stack_item_frame_extended";
	}
	
    }
    
    public static class ChopFrame extends StackMapFrame {
	private final int offset_delta; //u2

	/**
	 * 
	 * @param frameType 248 - 250
	 */
	public ChopFrame(int frameType, int offset_delta) {
	    super(frameType);
	    this.offset_delta = offset_delta;
	}

	@Override
	public void writeBody(DataOutputStream dos) throws IOException {
	    dos.writeShort(offset_delta);
	}

	@Override
	public int offsetDelta() {
	    return offset_delta;
	}

	@Override
	public int getLength() {
	    return 3; // frame_type + offset_delta
	}

	@Override
	public String toString() {
	    return "chop_frame";
	}
	
    }
        
    public static class SameFrameExtended extends StackMapFrame {
	private final int offset_delta; // u2

	public SameFrameExtended(int offset_delta) {
	    super(251);
	    this.offset_delta = offset_delta;
	}

	@Override
	public void writeBody(DataOutputStream dos) throws IOException {
	    dos.writeShort(frame_type);
	}

	@Override
	public int offsetDelta() {
	    return offset_delta;
	}

	@Override
	public int getLength() {
	    return 3;
	}

	@Override
	public String toString() {
	    return "same_frame_extended";
	}
    }
    
    public static class AppendFrame extends StackMapFrame {
	private final int offset_delta; // u2
	private final VerificationTypeInfo[] locals;

	/**
	 * 
	 * @param frameType 252 - 254;
	 * @param offset_delta
	 * @param locals length = frameType - 251;
	 */
	public AppendFrame(int frameType, int offset_delta, VerificationTypeInfo [] locals) {
	    super(frameType);
	    this.offset_delta = offset_delta;
	    this.locals = locals;
	}

	@Override
	public void writeBody(DataOutputStream dos) throws IOException {
	    dos.writeShort(frame_type);
	    for (int i = 0, len = locals.length; i < len; i++){
		locals[i].doWrite(dos);
	    }
	}

	@Override
	public int offsetDelta() {
	    return offset_delta;
	}

	@Override
	public int getLength() {
	    int result = 3;
	    for (int i = 0, len = locals.length; i < len; i++){
		result += locals[i].getLength();
	    }
	    return result;
	}
	
	@Override
	protected void resolve(ClassConstantPool pool) {
	    super.resolve(pool);
	    for (int i = 0, l = locals.length; i < l; i++){
		locals[i].resolve(pool);
	    }
	}

	@Override
	public String toString() {
	    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
    }
    
    public static class FullFrame extends StackMapFrame {
	private final int offset_delta;
	private final VerificationTypeInfo[] locals;
	private final VerificationTypeInfo [] stack;

	public FullFrame(int offset_delta, VerificationTypeInfo [] locals, VerificationTypeInfo [] stack) {
	    super(255);
	    this.offset_delta = offset_delta;
	    this.locals = locals;
	    this.stack = stack;
	}

	@Override
	public void writeBody(DataOutputStream dos) throws IOException {
	    dos.writeShort(offset_delta);
	    dos.writeShort(locals.length);
	    for (int i = 0, len = locals.length; i < len; i++){
		locals[i].doWrite(dos);
	    }
	    dos.writeShort(stack.length);
	    for (int i = 0, len = stack.length; i < len; i++){
		stack[i].doWrite(dos);
	    }
	}

	@Override
	public int offsetDelta() {
	    return offset_delta;
	}

	@Override
	public int getLength() {
	    int length = 7;
	    for (int i = 0, len = locals.length; i < len; i++){
		length += locals[i].getLength();
	    }
	    for (int i = 0, len = stack.length; i < len; i++){
		length += stack[i].getLength();
	    }
	    return length;
	}
	
	@Override
	protected void resolve(ClassConstantPool pool) {
	    super.resolve(pool);
	    for (int i = 0, l = locals.length; i < l; i++){
		locals[i].resolve(pool);
	    }
	    for (int i = 0, l = stack.length; i < l; i++){
		stack[i].resolve(pool);
	    }
	}

	@Override
	public String toString() {
	    return "full_frame";
	}
	
    }
    
    public static abstract class VerificationTypeInfo extends ClassFileEntry {

	protected final int tag; // u1
	
	protected VerificationTypeInfo(int tag){
	    this.tag = tag;
	}
	
	public void doWrite(DataOutputStream dos) throws IOException {
	    dos.writeByte(tag);
	    writeBody(dos);
	}
	
	public abstract void writeBody(DataOutputStream dos) throws IOException;
	
	public int getLength() {
	    return 1;
	}

	@Override
	public boolean equals(Object arg0) {
	    if (arg0 == this) return true;
	    if (!(arg0 instanceof VerificationTypeInfo)) return false; // handles null case.
	    return tag == ((VerificationTypeInfo)arg0).tag;
	    
	}

	@Override
	public int hashCode() {
	    int hash = 7;
	    hash = 97 * hash + this.tag;
	    return hash;
	}

	
	
    }
    
    public static class TopVariableInfo extends VerificationTypeInfo {

	public TopVariableInfo() {
	    super(0);
	}

	public void writeBody(DataOutputStream dos) throws IOException {}

	@Override
	public String toString() {
	    return "Top_variable_info";
	}
    }
    
    public static class IntegerVariableInfo extends VerificationTypeInfo {

	public IntegerVariableInfo() {
	    super(1);
	}

	public void writeBody(DataOutputStream dos) throws IOException {}

	@Override
	public String toString() {
	    return "Integer_variable_info";
	}
    }
    
    public static class FloatVariableInfo extends VerificationTypeInfo {

	public FloatVariableInfo() {
	    super(2);
	}

	public void writeBody(DataOutputStream dos) throws IOException {}

	@Override
	public String toString() {
	    return "Float_variable_info";
	}
    }
    
    public static class NullVariableInfo extends VerificationTypeInfo {

	public NullVariableInfo() {
	    super(5);
	}

	public void writeBody(DataOutputStream dos) throws IOException {}

	@Override
	public String toString() {
	    return "Null_variable_info";
	}
    }
    
    public static class UninitializedThisVariableInfo extends VerificationTypeInfo {

	public UninitializedThisVariableInfo() {
	    super(6);
	}
	
	public void writeBody(DataOutputStream dos) throws IOException {}

	@Override
	public String toString() {
	    return "Uninitializedthis_variable_info";
	}
	
    }
    
    public static class ObjectVariableInfo extends VerificationTypeInfo {
	private final CPClass classInfo;
	private int class_info; // u2

	public ObjectVariableInfo(CPClass classInfo) {
	    super(7);
	    this.classInfo = classInfo;
	}

	@Override
	public void writeBody(DataOutputStream dos) throws IOException {
	    dos.writeShort(class_info);
	}
	
	@Override
	public int getLength() {
	    return 3;
	}

	@Override
	public String toString() {
	    return "Object_variable_info";
	}
	
	@Override
	public boolean equals(Object o){
	    if (!super.equals(o)) return false;
	    if (!(o instanceof ObjectVariableInfo)) return false;
	    return classInfo.equals(((ObjectVariableInfo)o).classInfo);
	}

	@Override
	public int hashCode() {
	    int hash = 5;
	    hash = 53 * hash + (this.classInfo != null ? this.classInfo.hashCode() : 0);
	    return hash;
	}
	
	@Override
	protected void resolve(ClassConstantPool pool) {
	    super.resolve(pool);
	    class_info = pool.indexOf(classInfo);
	}
    }
    
    public static class UnitializedVariableInfo extends VerificationTypeInfo {
	private final int offset; // u2

	public UnitializedVariableInfo(int offset) {
	    super(8);
	    this.offset = offset;
	}

	@Override
	public void writeBody(DataOutputStream dos) throws IOException {
	    dos.writeShort(offset);
	}
	
	@Override
	public int getLength() {
	    return 3;
	}

	@Override
	public String toString() {
	    return "Unitialized_variable_info";
	}
	
    }
    
    public static class LongVariableInfo extends VerificationTypeInfo {

	public LongVariableInfo() {
	    super(4);
	}

	public void writeBody(DataOutputStream dos) throws IOException {}

	@Override
	public String toString() {
	    return "Long_variable_info";
	}
    }
    
    public static class DoubleVariableInfo extends VerificationTypeInfo {

	public DoubleVariableInfo() {
	    super(3);
	}

	@Override
	public void writeBody(DataOutputStream dos) throws IOException {}

	@Override
	public String toString() {
	    return "Double_variable_info";
	}
	
    }
    
}
