import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();

        map.put("1", "2");

        map.values().remove("1");

        System.out.println(map);
    }
}
