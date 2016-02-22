package psyco.kver.util;

import org.apache.commons.lang3.Validate;

/**
 * Created by peng on 16/2/22.
 */
public class Paths {

    public static String path(String path, String... subs) {
        Validate.notBlank(path);
        path = fixPath(path);
        if (subs == null || subs.length == 0)
            return path;
        StringBuilder stringBuilder = new StringBuilder(path);
        for (String s : subs)
            stringBuilder.append(fixPath(s));
        return stringBuilder.toString();
    }

    /***
     * -->  /a
     */
    private static String fixPath(String s) {
        String re = s.startsWith("/") ? s : ("/" + s);
        return re.endsWith("/") ? re.substring(0, re.length() - 1) : re;
    }
}
