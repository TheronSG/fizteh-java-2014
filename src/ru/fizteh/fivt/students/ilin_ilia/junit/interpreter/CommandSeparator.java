package ru.fizteh.fivt.students.ilin_ilia.junit.interpreter;

import ru.fizteh.fivt.students.ilin_ilia.junit.utils.Utils;

import java.util.Arrays;

public class CommandSeparator {

    public static final String PARAM_REGEXP = "\\S+";

    public static String[] setCommand(String statement) {
        String[] chunks = Utils.findAll(PARAM_REGEXP, statement);
        String commandName;
        String[] params;
        if (chunks.length > 1 && chunks[0].equals("show") && chunks[1].equals("tables")) {
            params = Arrays.copyOfRange(chunks, 2, chunks.length);
            commandName = "show tables";
        } else {
            commandName = chunks[0];
            params = Arrays.copyOfRange(chunks, 1, chunks.length);
        }
        String [] returnArray = new String[params.length + 1];
        returnArray[0] = commandName;
        System.arraycopy(params, 0, returnArray, 1, params.length);
        return returnArray;
    }
}
