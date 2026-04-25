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
 * Constants for Java class file major version numbers.
 *
 * <p>Each constant represents the major version number written in the class
 * file header for the corresponding Java platform version.  The minor version
 * is typically {@code 0} (or {@code 65535} for preview features).
 *
 * <p>Relationship: {@code major = 44 + java_version} for Java 1 and above
 * (e.g. Java 1 → 45, Java 2 → 46, Java 27 → 71).
 */
public final class JavaVersionConstants {

    private JavaVersionConstants() {}

    /** Class file major version for Java 1 (JDK 1.1). */
    public static final int JAVA_1 = 45;

    /** Class file major version for Java 2 (JDK 1.2). */
    public static final int JAVA_2 = 46;

    /** Class file major version for Java 3 (JDK 1.3). */
    public static final int JAVA_3 = 47;

    /** Class file major version for Java 4 (JDK 1.4). */
    public static final int JAVA_4 = 48;

    /** Class file major version for Java 5. */
    public static final int JAVA_5 = 49;

    /** Class file major version for Java 6. */
    public static final int JAVA_6 = 50;

    /** Class file major version for Java 7. */
    public static final int JAVA_7 = 51;

    /** Class file major version for Java 8. */
    public static final int JAVA_8 = 52;

    /** Class file major version for Java 9. */
    public static final int JAVA_9 = 53;

    /** Class file major version for Java 10. */
    public static final int JAVA_10 = 54;

    /** Class file major version for Java 11. */
    public static final int JAVA_11 = 55;

    /** Class file major version for Java 12. */
    public static final int JAVA_12 = 56;

    /** Class file major version for Java 13. */
    public static final int JAVA_13 = 57;

    /** Class file major version for Java 14. */
    public static final int JAVA_14 = 58;

    /** Class file major version for Java 15. */
    public static final int JAVA_15 = 59;

    /** Class file major version for Java 16. */
    public static final int JAVA_16 = 60;

    /** Class file major version for Java 17. */
    public static final int JAVA_17 = 61;

    /** Class file major version for Java 18. */
    public static final int JAVA_18 = 62;

    /** Class file major version for Java 19. */
    public static final int JAVA_19 = 63;

    /** Class file major version for Java 20. */
    public static final int JAVA_20 = 64;

    /** Class file major version for Java 21. */
    public static final int JAVA_21 = 65;

    /** Class file major version for Java 22. */
    public static final int JAVA_22 = 66;

    /** Class file major version for Java 23. */
    public static final int JAVA_23 = 67;

    /** Class file major version for Java 24. */
    public static final int JAVA_24 = 68;

    /** Class file major version for Java 25. */
    public static final int JAVA_25 = 69;

    /** Class file major version for Java 26. */
    public static final int JAVA_26 = 70;

    /** Class file major version for Java 27. */
    public static final int JAVA_27 = 71;
}
