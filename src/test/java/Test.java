import org.apache.commons.lang.StringEscapeUtils;

import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
        String input = "%%u";
        String[] array = input.split("%%");
        String result = "";

        for (int i = 0; i < array.length; i++) {
            String s = array[i];

            if (s.equalsIgnoreCase("")) {
                result += "%";
            } else {
                result += s.replace("%u", "666");
            }
        }

        System.out.println(Arrays.toString(array));
        System.out.println(result);
    }
}
