package ru.fizteh.fivt.students.ilin_ilia.parallel.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static void interpreterError(String errorMessage) {
        System.err.println(errorMessage);
    }

    public static String[] findAll(String regexp, String text) {
        Matcher matcher = Pattern.compile(regexp).matcher(text);
        List<String> occurences = new ArrayList<>();
        while (matcher.find()) {
            occurences.add(matcher.group());
        }
        return  occurences.toArray(new String[occurences.size()]);
    }
}
