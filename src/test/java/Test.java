import java.util.Arrays;

public class Test {
    private static String test = "test";
    private static String testS = test;

    public static void main(String[] args) {
        String input = "    %money%\\%money%";
        String[] splitArray = input.split("%money%");
        String result = "";

        System.out.println(Arrays.toString(splitArray));

        for (int i = 0; i < splitArray.length; i++) {
            String last = splitArray[i];

            if (last.endsWith("\\")) {
                result += last.substring(0, last.length() - 1);
                result += "%money%";
            } else {
                result += "1.0";
            }


        }

        System.out.println(result);
    }
}
