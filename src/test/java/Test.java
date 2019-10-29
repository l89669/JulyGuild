import org.apache.commons.lang.StringEscapeUtils;

public class Test {

    public static void main(String[] args) {
        String s = "\\s0000\s-";
        String result = new String();
        boolean hadSlash = false;
        boolean b = false;

        out:
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);


            if (hadSlash) {
                switch (c) {
                    case '%':
                        result += "%";
                        hadSlash = false;
                        break;
                    case 's':
                        result += "*";
                        break;
                        default:
                            result += c;
                            break;
                }
            } else if (c == '%') {
                hadSlash = true;
                result += c;
            }
        }

        System.out.println(StringEscapeUtils.escapeJava("\\\n"));
        System.out.println(result);
    }
}
