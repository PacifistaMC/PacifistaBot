package fr.pacifista.bot.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

    public static boolean isStringContainUrl(final String toCheck) {
        Pattern p = Pattern.compile(URL_REGEX);
        Matcher m = p.matcher(toCheck);
        return m.find();
    }

}
