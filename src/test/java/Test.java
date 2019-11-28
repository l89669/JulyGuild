import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.scalified.tree.multinode.ArrayMultiTreeNode;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Random;

public class Test {

    public static int random(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    public static int random1(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

    public static int get(String a, String b) {
        return 1;
    }

    public static int get(File a, String b) {
        return 1;
    }

    public static void main(String[] args) {

        Util.getIntegerList("1").stream().forEach(integer -> System.out.println(integer));


        String input = "%%u%u";
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
        System.out.println(PlaceholderText.replacePlaceholders("&f[%PERMISSION%&f] &e%NAME%",
                new Placeholder.Builder().add("%PERMISSION%", "测试")
                        .add("%NAME%", "JJJ").build()));
    }
}
