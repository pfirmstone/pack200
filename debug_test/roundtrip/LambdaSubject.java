package roundtrip;
import java.util.function.Supplier;
import java.util.function.Function;
import java.util.Arrays;
import java.util.List;
public class LambdaSubject {
    public static String greet(String name) { return "Hello, " + name; }
    public static Supplier<String> supplier() {
        String msg = "world";
        return () -> msg;
    }
    public static Function<String,String> methodRef() {
        return LambdaSubject::greet;
    }
    public static List<String> sorted(List<String> list) {
        list.sort(String::compareToIgnoreCase);
        return list;
    }
}
