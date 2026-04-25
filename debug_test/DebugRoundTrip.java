import org.apache.harmony.pack200.*;
import org.apache.harmony.unpack200.*;
import java.io.*;
import java.util.jar.*;

public class DebugRoundTrip {
    public static void main(String[] args) throws Exception {
        // Pack
        JarFile jf = new JarFile("input.jar");
        FileOutputStream packOut = new FileOutputStream("output.pack");
        PackingOptions opts = new PackingOptions();
        opts.setGzip(false);
        new Pack200Archive(jf, packOut, opts).pack();
        packOut.close();
        jf.close();
        
        // Unpack
        FileInputStream packIn = new FileInputStream("output.pack");
        JarOutputStream jos = new JarOutputStream(new FileOutputStream("output.jar"));
        new UnPack200Archive(packIn, jos).unpack();
        jos.close();
        packIn.close();
        
        // Extract unpacked class file
        JarFile outJar = new JarFile("output.jar");
        JarEntry e = outJar.getJarEntry("roundtrip/LambdaSubject.class");
        if (e == null) {
            System.out.println("Entry not found in output.jar!");
            System.out.println("Entries:");
            java.util.Enumeration<JarEntry> entries = outJar.entries();
            while (entries.hasMoreElements()) {
                System.out.println("  " + entries.nextElement().getName());
            }
        } else {
            InputStream is = outJar.getInputStream(e);
            FileOutputStream fos = new FileOutputStream("unpacked_LambdaSubject.class");
            byte[] buf = new byte[4096]; int n;
            while ((n = is.read(buf)) != -1) fos.write(buf, 0, n);
            fos.close();
            System.out.println("Unpacked class extracted.");
        }
        outJar.close();
    }
}
