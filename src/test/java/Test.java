import java.util.UUID;

public class Test {
    private static String test = "test";
    private static String testS = test;

    public static void main(String[] args) {
        test = "1";
        System.out.println(UUID.fromString("Gilgamesh").toString());
    }
}