import org.apache.harmony.pack200.*;
import org.apache.harmony.unpack200.*;
import org.apache.harmony.unpack200.bytecode.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.jar.*;

public class DebugRoundTrip3 {
    public static void main(String[] args) throws Exception {
        // Re-unpack to trigger debug output
        FileInputStream packIn = new FileInputStream("output.pack");
        JarOutputStream jos = new JarOutputStream(new FileOutputStream("output3.jar"));
        
        // Override ClassConstantPool to add debugging
        UnPack200Archive archive = new UnPack200Archive(packIn, jos) {
        };
        archive.unpack();
        jos.close();
        packIn.close();
        
        System.out.println("Done.");
    }
}
