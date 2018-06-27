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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.harmony.pack200.Pack200Exception;

/**
 * Stores a mapping from attribute names to their corresponding layout types.
 * Note that names of attribute layouts and their formats are NOT
 * internationalized, and should not be translated.
 */
class AttributeLayoutMap {
    
    private static final String STACK_MAP_LAYOUTS; //5.5.5
    private static final String NON_PARAMETER_METADATA_ANNOTATIONS;
    private static final String METHOD_PARAMETER_ANNOTATIONS;
    private static final String RUNTIME_TYPE_ANNOTATIONS;
    
    static {
	StringBuilder sb = new StringBuilder(1024);
	// STACK_MAP_LAYOUTS
	sb.append("[NH[(1)]]");
	sb.append("[TB");
	for (int i = 64, l = 128; i < l; i++){
	    sb.append('(').append(i).append(")[(2)]");
	}
	sb.append("(247)[(1)(2)]");
	for (int i = 248, l = 252; i<l; i++){
	    sb.append('(').append(i).append(")[(1)]");
	}
	sb.append("(252)[(1)(2)]");
	sb.append("(253)[(1)(2)(2)]");
	sb.append("(254)[(1)(2)(2)(2)]");
	sb.append("(255)[(1)NH[(2)]NH[(2)]]");
	sb.append("()[]");
	sb.append(']');
	sb.append("[H]");
	sb.append("[TB");
	sb.append("(7)[RCH]");
	sb.append("(8)[PH]");
	sb.append("()[]");
	sb.append(']');
	STACK_MAP_LAYOUTS = sb.toString();
	sb.delete(0, sb.length());
	// METADATA_LAYOUTS common
	sb.append("[RSHNH[RUH(1)]]");
	sb.append("[TB");
	sb.append("(66)[KIH]");
	sb.append("(67)[KIH]");
	sb.append("(73)[KIH]");
	sb.append("(83)[KIH]");
	sb.append("(90)[KIH]");
	sb.append("(68)[KDH]");
	sb.append("(70)[KFH]");
	sb.append("(74)[KJH]");
	sb.append("(99)[RSH]");
	sb.append("(101)[RSHRUH]");
	sb.append("(115)[RUH]");
	sb.append("(91)[NH[(0)]]");
	sb.append("(64)[RSHNH[RUH(0)]]");
	sb.append("()[]");
	sb.append(']');
	String metadataLayoutCommon = sb.toString();
	sb.delete(0, sb.length());
	// NON PARAMETER METADATA ANNOTATIONS
	sb.append("[NH[(1)]]");
	sb.append(metadataLayoutCommon);
	NON_PARAMETER_METADATA_ANNOTATIONS = sb.toString();
	sb.delete(0, sb.length());
	// METHOD_PARAMETER_ANNOTATIONS
	sb.append("[NH[(1)]]");
	sb.append("[NH[(1)]]");
	sb.append(metadataLayoutCommon);
	METHOD_PARAMETER_ANNOTATIONS = sb.toString();
	sb.delete(0, sb.length());
	// RUNTIME_TYPE_ANNOTATIONS
	sb.append("[NH[(1)(2)(3)]]");
	sb.append("[TB");
	sb.append("(0)[B]");
	sb.append("(1)[B]");
	sb.append("(16)[FH]");
	sb.append("(17)[BB]");
	sb.append("(18)[BB]");
	sb.append("(19)[]");
	sb.append("(20)[]");
	sb.append("(21)[]");
	sb.append("(22)[B]");
	sb.append("(23)[H]");
	sb.append("(64)[NH[PHOHH]]");
	sb.append("(65)[NH[PHOHH]]");
	sb.append("(66)[H]");
	sb.append("(67)[PH]");
	sb.append("(68)[PH]");
	sb.append("(69)[PH]");
	sb.append("(70)[PH]");
	sb.append("(71)[PHB]");
	sb.append("(72)[PHB]");
	sb.append("(73)[PHB]");
	sb.append("(74)[PHB]");
	sb.append("(75)[PHB]");
	sb.append("()[]");
	sb.append(']');
	sb.append("[NB[BB]]");
	sb.append(metadataLayoutCommon);
	RUNTIME_TYPE_ANNOTATIONS = sb.toString();
    }

    // Create all the default AttributeLayouts here
    private static AttributeLayout[] getDefaultAttributeLayouts()
            throws Pack200Exception {
        return new AttributeLayout[] {
                new AttributeLayout(AttributeLayout.ACC_PUBLIC,
                        AttributeLayout.CONTEXT_CLASS, "", 0),
                new AttributeLayout(AttributeLayout.ACC_PUBLIC,
                        AttributeLayout.CONTEXT_FIELD, "", 0),
                new AttributeLayout(AttributeLayout.ACC_PUBLIC,
                        AttributeLayout.CONTEXT_METHOD, "", 0),
		new AttributeLayout( // Java 6
			AttributeLayout.ATTRIBUTE_STACK_MAP_TABLE,
			AttributeLayout.CONTEXT_CODE, 
			STACK_MAP_LAYOUTS,
			0),
		
                new AttributeLayout(AttributeLayout.ACC_PRIVATE,
                        AttributeLayout.CONTEXT_CLASS, "", 1),
                new AttributeLayout(AttributeLayout.ACC_PRIVATE,
                        AttributeLayout.CONTEXT_FIELD, "", 1),
                new AttributeLayout(AttributeLayout.ACC_PRIVATE,
                        AttributeLayout.CONTEXT_METHOD, "", 1),
                new AttributeLayout(
                        AttributeLayout.ATTRIBUTE_LINE_NUMBER_TABLE,
                        AttributeLayout.CONTEXT_CODE, "NH[PHH]", 1),
		
                new AttributeLayout(AttributeLayout.ACC_PROTECTED,
                        AttributeLayout.CONTEXT_CLASS, "", 2),
                new AttributeLayout(AttributeLayout.ACC_PROTECTED,
                        AttributeLayout.CONTEXT_FIELD, "", 2),
                new AttributeLayout(AttributeLayout.ACC_PROTECTED,
                        AttributeLayout.CONTEXT_METHOD, "", 2),
                new AttributeLayout(
                        AttributeLayout.ATTRIBUTE_LOCAL_VARIABLE_TABLE,
                        AttributeLayout.CONTEXT_CODE, "NH[PHOHRUHRSHH]", 2),

                new AttributeLayout(AttributeLayout.ACC_STATIC,
                        AttributeLayout.CONTEXT_CLASS, "", 3),
                new AttributeLayout(AttributeLayout.ACC_STATIC,
                        AttributeLayout.CONTEXT_FIELD, "", 3),
                new AttributeLayout(AttributeLayout.ACC_STATIC,
                        AttributeLayout.CONTEXT_METHOD, "", 3),
                new AttributeLayout(
                        AttributeLayout.ATTRIBUTE_LOCAL_VARIABLE_TYPE_TABLE,
                        AttributeLayout.CONTEXT_CODE, "NH[PHOHRUHRSHH]", 3),

                new AttributeLayout(AttributeLayout.ACC_FINAL,
                        AttributeLayout.CONTEXT_CLASS, "", 4),
                new AttributeLayout(AttributeLayout.ACC_FINAL,
                        AttributeLayout.CONTEXT_FIELD, "", 4),
                new AttributeLayout(AttributeLayout.ACC_FINAL,
                        AttributeLayout.CONTEXT_METHOD, "", 4),
                new AttributeLayout(AttributeLayout.ACC_SYNCHRONIZED,
                        AttributeLayout.CONTEXT_CLASS, "", 5),
                new AttributeLayout(AttributeLayout.ACC_SYNCHRONIZED,
                        AttributeLayout.CONTEXT_FIELD, "", 5),
                new AttributeLayout(AttributeLayout.ACC_SYNCHRONIZED,
                        AttributeLayout.CONTEXT_METHOD, "", 5),
                new AttributeLayout(AttributeLayout.ACC_VOLATILE,
                        AttributeLayout.CONTEXT_CLASS, "", 6),
                new AttributeLayout(AttributeLayout.ACC_VOLATILE,
                        AttributeLayout.CONTEXT_FIELD, "", 6),
                new AttributeLayout(AttributeLayout.ACC_VOLATILE,
                        AttributeLayout.CONTEXT_METHOD, "", 6),
                new AttributeLayout(AttributeLayout.ACC_TRANSIENT,
                        AttributeLayout.CONTEXT_CLASS, "", 7),
                new AttributeLayout(AttributeLayout.ACC_TRANSIENT,
                        AttributeLayout.CONTEXT_FIELD, "", 7),
                new AttributeLayout(AttributeLayout.ACC_TRANSIENT,
                        AttributeLayout.CONTEXT_METHOD, "", 7),
                new AttributeLayout(AttributeLayout.ACC_NATIVE,
                        AttributeLayout.CONTEXT_CLASS, "", 8),
                new AttributeLayout(AttributeLayout.ACC_NATIVE,
                        AttributeLayout.CONTEXT_FIELD, "", 8),
                new AttributeLayout(AttributeLayout.ACC_NATIVE,
                        AttributeLayout.CONTEXT_METHOD, "", 8),
                new AttributeLayout(AttributeLayout.ACC_INTERFACE,
                        AttributeLayout.CONTEXT_CLASS, "", 9),
                new AttributeLayout(AttributeLayout.ACC_INTERFACE,
                        AttributeLayout.CONTEXT_FIELD, "", 9),
                new AttributeLayout(AttributeLayout.ACC_INTERFACE,
                        AttributeLayout.CONTEXT_METHOD, "", 9),
                new AttributeLayout(AttributeLayout.ACC_ABSTRACT,
                        AttributeLayout.CONTEXT_CLASS, "", 10),
                new AttributeLayout(AttributeLayout.ACC_ABSTRACT,
                        AttributeLayout.CONTEXT_FIELD, "", 10),
                new AttributeLayout(AttributeLayout.ACC_ABSTRACT,
                        AttributeLayout.CONTEXT_METHOD, "", 10),
                new AttributeLayout(AttributeLayout.ACC_STRICT,
                        AttributeLayout.CONTEXT_CLASS, "", 11),
                new AttributeLayout(AttributeLayout.ACC_STRICT,
                        AttributeLayout.CONTEXT_FIELD, "", 11),
                new AttributeLayout(AttributeLayout.ACC_STRICT,
                        AttributeLayout.CONTEXT_METHOD, "", 11),
                new AttributeLayout(AttributeLayout.ACC_SYNTHETIC,
                        AttributeLayout.CONTEXT_CLASS, "", 12),
                new AttributeLayout(AttributeLayout.ACC_SYNTHETIC,
                        AttributeLayout.CONTEXT_FIELD, "", 12),
                new AttributeLayout(AttributeLayout.ACC_SYNTHETIC,
                        AttributeLayout.CONTEXT_METHOD, "", 12),
                new AttributeLayout(AttributeLayout.ACC_ANNOTATION,
                        AttributeLayout.CONTEXT_CLASS, "", 13),
                new AttributeLayout(AttributeLayout.ACC_ANNOTATION,
                        AttributeLayout.CONTEXT_FIELD, "", 13),
                new AttributeLayout(AttributeLayout.ACC_ANNOTATION,
                        AttributeLayout.CONTEXT_METHOD, "", 13),
                new AttributeLayout(AttributeLayout.ACC_ENUM,
                        AttributeLayout.CONTEXT_CLASS, "", 14),
                new AttributeLayout(AttributeLayout.ACC_ENUM,
                        AttributeLayout.CONTEXT_FIELD, "", 14),
                new AttributeLayout(AttributeLayout.ACC_ENUM,
                        AttributeLayout.CONTEXT_METHOD, "", 14),
                new AttributeLayout(AttributeLayout.ATTRIBUTE_SOURCE_FILE,
                        AttributeLayout.CONTEXT_CLASS, "RUNH", 17),
                new AttributeLayout(AttributeLayout.ATTRIBUTE_CONSTANT_VALUE,
                        AttributeLayout.CONTEXT_FIELD, "KQH", 17),
                new AttributeLayout(AttributeLayout.ATTRIBUTE_CODE,
                        AttributeLayout.CONTEXT_METHOD, "", 17),
                new AttributeLayout(AttributeLayout.ATTRIBUTE_ENCLOSING_METHOD,
                        AttributeLayout.CONTEXT_CLASS, "RCHRDNH", 18),
                new AttributeLayout(AttributeLayout.ATTRIBUTE_EXCEPTIONS,
                        AttributeLayout.CONTEXT_METHOD, "NH[RCH]", 18),
                new AttributeLayout(AttributeLayout.ATTRIBUTE_SIGNATURE,
                        AttributeLayout.CONTEXT_CLASS, "RSH", 19),
                new AttributeLayout(AttributeLayout.ATTRIBUTE_SIGNATURE,
                        AttributeLayout.CONTEXT_FIELD, "RSH", 19),
                new AttributeLayout(AttributeLayout.ATTRIBUTE_SIGNATURE,
                        AttributeLayout.CONTEXT_METHOD, "RSH", 19),
                new AttributeLayout(AttributeLayout.ATTRIBUTE_DEPRECATED,
                        AttributeLayout.CONTEXT_CLASS, "", 20),
                new AttributeLayout(AttributeLayout.ATTRIBUTE_DEPRECATED,
                        AttributeLayout.CONTEXT_FIELD, "", 20),
                new AttributeLayout(AttributeLayout.ATTRIBUTE_DEPRECATED,
                        AttributeLayout.CONTEXT_METHOD, "", 20),
                new AttributeLayout(
                        AttributeLayout.ATTRIBUTE_RUNTIME_VISIBLE_ANNOTATIONS,
                        AttributeLayout.CONTEXT_CLASS,
//			"*",
			NON_PARAMETER_METADATA_ANNOTATIONS,
			21),
                new AttributeLayout(
                        AttributeLayout.ATTRIBUTE_RUNTIME_VISIBLE_ANNOTATIONS,
                        AttributeLayout.CONTEXT_FIELD,
//			"*",
			NON_PARAMETER_METADATA_ANNOTATIONS,
			21),
                new AttributeLayout(
                        AttributeLayout.ATTRIBUTE_RUNTIME_VISIBLE_ANNOTATIONS,
                        AttributeLayout.CONTEXT_METHOD,
//			"*",
			NON_PARAMETER_METADATA_ANNOTATIONS,
			21),
                new AttributeLayout(
                        AttributeLayout.ATTRIBUTE_RUNTIME_INVISIBLE_ANNOTATIONS,
                        AttributeLayout.CONTEXT_CLASS,
//			"*",
			NON_PARAMETER_METADATA_ANNOTATIONS,
			22),
                new AttributeLayout(
                        AttributeLayout.ATTRIBUTE_RUNTIME_INVISIBLE_ANNOTATIONS,
                        AttributeLayout.CONTEXT_FIELD,
//			"*",
			NON_PARAMETER_METADATA_ANNOTATIONS,
			22),
                new AttributeLayout(
                        AttributeLayout.ATTRIBUTE_RUNTIME_INVISIBLE_ANNOTATIONS,
                        AttributeLayout.CONTEXT_METHOD,
//			"*",
			NON_PARAMETER_METADATA_ANNOTATIONS,
			22),
                new AttributeLayout(AttributeLayout.ATTRIBUTE_INNER_CLASSES,
                        AttributeLayout.CONTEXT_CLASS, "", 23),
                new AttributeLayout(
                        AttributeLayout.ATTRIBUTE_RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS,
                        AttributeLayout.CONTEXT_METHOD,
//			"*",
			METHOD_PARAMETER_ANNOTATIONS,
			23),
                new AttributeLayout(
                        AttributeLayout.ATTRIBUTE_CLASS_FILE_VERSION,
                        AttributeLayout.CONTEXT_CLASS, "", 24),
                new AttributeLayout(
                        AttributeLayout.ATTRIBUTE_RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS,
                        AttributeLayout.CONTEXT_METHOD,
//			"*",
			METHOD_PARAMETER_ANNOTATIONS,
			24),
                new AttributeLayout(
                        AttributeLayout.ATTRIBUTE_ANNOTATION_DEFAULT,
                        AttributeLayout.CONTEXT_METHOD,
//			"*",
			NON_PARAMETER_METADATA_ANNOTATIONS,
			25), // TODO: CHECK METADATA LAYOUT
		new AttributeLayout(
			AttributeLayout.ATTRIBUTE_METHOD_PARAMETERS,
			AttributeLayout.CONTEXT_METHOD, "NB[RUNHFH]", 26),
		new AttributeLayout(
			AttributeLayout.ATTRIBUTE_RUNTIME_VISIBLE_TYPE_ANNOTATIONS,
			AttributeLayout.CONTEXT_CLASS, RUNTIME_TYPE_ANNOTATIONS, 27),
		new AttributeLayout(
			AttributeLayout.ATTRIBUTE_RUNTIME_VISIBLE_TYPE_ANNOTATIONS,
			AttributeLayout.CONTEXT_FIELD, RUNTIME_TYPE_ANNOTATIONS, 27),
		new AttributeLayout(
			AttributeLayout.ATTRIBUTE_RUNTIME_VISIBLE_TYPE_ANNOTATIONS,
			AttributeLayout.CONTEXT_METHOD, RUNTIME_TYPE_ANNOTATIONS, 27),
		new AttributeLayout(
			AttributeLayout.ATTRIBUTE_RUNTIME_VISIBLE_TYPE_ANNOTATIONS,
			AttributeLayout.CONTEXT_CODE, RUNTIME_TYPE_ANNOTATIONS, 27),
		new AttributeLayout(
			AttributeLayout.ATTRIBUTE_RUNTIME_INVISIBLE_TYPE_ANNOTATIONS,
			AttributeLayout.CONTEXT_CLASS, RUNTIME_TYPE_ANNOTATIONS, 28),
		new AttributeLayout(
			AttributeLayout.ATTRIBUTE_RUNTIME_INVISIBLE_TYPE_ANNOTATIONS,
			AttributeLayout.CONTEXT_FIELD, RUNTIME_TYPE_ANNOTATIONS, 28),
		new AttributeLayout(
			AttributeLayout.ATTRIBUTE_RUNTIME_INVISIBLE_TYPE_ANNOTATIONS,
			AttributeLayout.CONTEXT_METHOD, RUNTIME_TYPE_ANNOTATIONS, 28),
		new AttributeLayout(
			AttributeLayout.ATTRIBUTE_RUNTIME_INVISIBLE_TYPE_ANNOTATIONS,
			AttributeLayout.CONTEXT_CODE, RUNTIME_TYPE_ANNOTATIONS, 28),
	};
    }

    private final Map classLayouts = new HashMap();
    private final Map fieldLayouts = new HashMap();
    private final Map methodLayouts = new HashMap();
    private final Map codeLayouts = new HashMap();

    // The order of the maps in this array should not be changed as their
    // indices correspond to
    // the value of their context constants (AttributeLayout.CONTEXT_CLASS etc.)
    private final Map[] layouts = new Map[] { classLayouts, fieldLayouts,
            methodLayouts, codeLayouts };

    private final Map layoutsToBands = new HashMap();

    public AttributeLayoutMap() throws Pack200Exception {
        AttributeLayout[] defaultAttributeLayouts = getDefaultAttributeLayouts();
        for (int i = 0; i < defaultAttributeLayouts.length; i++) {
            add(defaultAttributeLayouts[i]);
        }
    }

    public void add(AttributeLayout layout) {
        layouts[layout.getContext()]
                .put(Integer.valueOf(layout.getIndex()), layout);
    }

    public void add(AttributeLayout layout, NewAttributeBands newBands) {
        add(layout);
        layoutsToBands.put(layout, newBands);
    }

    public AttributeLayout getAttributeLayout(String name, int context)
            throws Pack200Exception {
        Map map = layouts[context];
        for (Iterator iter = map.values().iterator(); iter.hasNext();) {
            AttributeLayout layout = (AttributeLayout) iter.next();
            if (layout.getName().equals(name)) {
                return layout;
            }
        }
        return null;
    }

    public AttributeLayout getAttributeLayout(int index, int context)
            throws Pack200Exception {
        Map map = layouts[context];
        return (AttributeLayout) map.get(Integer.valueOf(index));
    }

    /**
     * The map should not contain the same layout and name combination more than
     * once for each context.
     *
     * @throws Pack200Exception
     *
     */
    public void checkMap() throws Pack200Exception {
        for (int i = 0; i < layouts.length; i++) {
            Map map = layouts[i];
            Collection c = map.values();
            if (!(c instanceof List)) {
                c = new ArrayList(c);
            }
            List l = (List) c;
            for (int j = 0; j < l.size(); j++) {
                AttributeLayout layout1 = (AttributeLayout) l.get(j);
                for (int j2 = j + 1; j2 < l.size(); j2++) {
                    AttributeLayout layout2 = (AttributeLayout) l.get(j2);
                    if (layout1.getName().equals(layout2.getName())
                            && layout1.getLayout().equals(layout2.getLayout())) {
                        throw new Pack200Exception(
                                "Same layout/name combination: "
                                        + layout1.getLayout()
                                        + "/"
                                        + layout1.getName()
                                        + " exists twice for context: "
                                        + AttributeLayout.contextNames[layout1
                                                .getContext()]);
                    }
                }
            }
        }
    }

    public NewAttributeBands getAttributeBands(AttributeLayout layout) {
        return (NewAttributeBands) layoutsToBands.get(layout);
    }

}