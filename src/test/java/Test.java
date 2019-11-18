import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.scalified.tree.multinode.ArrayMultiTreeNode;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
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
