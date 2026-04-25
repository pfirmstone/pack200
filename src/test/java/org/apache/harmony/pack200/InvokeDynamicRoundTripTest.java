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

import java.io.*;
import java.util.*;
import java.util.jar.*;
import javax.tools.*;
import org.apache.harmony.pack200.Pack200Archive;
import org.apache.harmony.pack200.PackingOptions;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests that classes containing invokedynamic instructions (lambdas)
 * round-trip correctly through pack200 compress + decompress.
 * Exercises constant pool tags: CP_MethodHandle (15), CP_MethodType (16),
 * CP_InvokeDynamic (18).
 */
public class InvokeDynamicRoundTripTest {

    private static final String LAMBDA_SOURCE =
        "package roundtrip;\n" +
        "import java.util.function.Supplier;\n" +
        "import java.util.function.Function;\n" +
        "import java.util.Arrays;\n" +
        "import java.util.List;\n" +
        "public class LambdaSubject {\n" +
        "    public static String greet(String name) { return \"Hello, \" + name; }\n" +
        "    public static Supplier<String> supplier() {\n" +
        "        String msg = \"world\";\n" +
        "        return () -> msg;\n" +
        "    }\n" +
        "    public static Function<String,String> methodRef() {\n" +
        "        return LambdaSubject::greet;\n" +
        "    }\n" +
        "    public static List<String> sorted(List<String> list) {\n" +
        "        list.sort(String::compareToIgnoreCase);\n" +
        "        return list;\n" +
        "    }\n" +
        "}\n";

    @Test
    public void testLambdaRoundTrip() throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull("JavaCompiler not available (not running on a JDK)", compiler);

        File tmpDir = createTempDir("lambda_roundtrip");
        File srcDir = new File(tmpDir, "src");
        File pkgDir = new File(srcDir, "roundtrip");
        pkgDir.mkdirs();
        File srcFile = new File(pkgDir, "LambdaSubject.java");
        try (PrintWriter pw = new PrintWriter(srcFile)) { pw.print(LAMBDA_SOURCE); }

        File classDir = new File(tmpDir, "classes");
        classDir.mkdirs();

        DiagnosticCollector<JavaFileObject> diags = new DiagnosticCollector<>();
        try (StandardJavaFileManager fm = compiler.getStandardFileManager(diags, null, null)) {
            Iterable<? extends JavaFileObject> units = fm.getJavaFileObjects(srcFile);
            JavaCompiler.CompilationTask task = compiler.getTask(null, fm, diags,
                Arrays.asList("-d", classDir.getAbsolutePath(), "--release", "11"),
                null, units);
            assertTrue("Compilation failed: " + diags.getDiagnostics(), task.call());
        }

        File inputJar = new File(tmpDir, "lambda_input.jar");
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(inputJar))) {
            addDirectory(jos, classDir, classDir.getAbsolutePath());
        }

        File packFile = new File(tmpDir, "lambda.pack");
        try (JarFile jf = new JarFile(inputJar);
             OutputStream os = new FileOutputStream(packFile)) {
            PackingOptions opts = new PackingOptions();
            opts.setGzip(false);
            new Pack200Archive(jf, os, opts).pack();
        }

        File outputJar = new File(tmpDir, "lambda_output.jar");
        try (InputStream is = new FileInputStream(packFile);
             JarOutputStream jos = new JarOutputStream(new FileOutputStream(outputJar))) {
            new org.apache.harmony.unpack200.UnPack200Archive(is, jos).unpack();
        }

        try (JarFile orig = new JarFile(inputJar);
             JarFile unpacked = new JarFile(outputJar)) {
            int checked = 0;
            Enumeration<JarEntry> entries = orig.entries();
            while (entries.hasMoreElements()) {
                JarEntry e = entries.nextElement();
                if (!e.getName().endsWith(".class")) continue;
                JarEntry e2 = unpacked.getJarEntry(e.getName());
                assertNotNull("Missing entry in unpacked jar: " + e.getName(), e2);
                byte[] origBytes = readAll(orig.getInputStream(e));
                byte[] unpackedBytes = readAll(unpacked.getInputStream(e2));
                assertArrayEquals("Class bytes differ for " + e.getName(), origBytes, unpackedBytes);
                checked++;
            }
            assertTrue("No class entries found in original jar", checked > 0);
        }
    }

    private static File createTempDir(String prefix) throws IOException {
        File f = File.createTempFile(prefix, "");
        f.delete();
        f.mkdirs();
        return f;
    }

    private static void addDirectory(JarOutputStream jos, File dir, String basePath) throws IOException {
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                addDirectory(jos, f, basePath);
            } else {
                String entryName = f.getAbsolutePath().substring(basePath.length() + 1)
                                    .replace(File.separatorChar, '/');
                jos.putNextEntry(new JarEntry(entryName));
                try (InputStream is = new FileInputStream(f)) {
                    byte[] buf = new byte[4096];
                    int n;
                    while ((n = is.read(buf)) != -1) jos.write(buf, 0, n);
                }
                jos.closeEntry();
            }
        }
    }

    private static byte[] readAll(InputStream is) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        byte[] tmp = new byte[4096];
        int n;
        while ((n = is.read(tmp)) != -1) buf.write(tmp, 0, n);
        is.close();
        return buf.toByteArray();
    }
}
