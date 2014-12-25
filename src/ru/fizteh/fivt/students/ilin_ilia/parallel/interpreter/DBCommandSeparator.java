package ru.fizteh.fivt.students.ilin_ilia.parallel.interpreter;

import ru.fizteh.fivt.students.ilin_ilia.parallel.utils.Utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DBCommandSeparator extends CommandSeparator {

    public static final String PARAM_REGEXP = "\\S+";

    public String[] setCommand(String statement) {
        String[] chunks = Utils.findAll(PARAM_REGEXP, statement);
        String commandName;
        String[] params;
        String buff = "";
        if (chunks[0].equals("put")) {
            commandName = "put";
            for (int i = 2; i < chunks.length; i++) {
                buff += chunks[i] + " ";
            }
            if (chunks.length > 2) {
                chunks[2] = buff;
                params = Arrays.copyOfRange(chunks, 1, 3);
            } else {
                params = new String[1];
            }
        } else if (chunks.length > 1 && chunks[0].equals("show") && chunks[1].equals("tables")) {
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

    public String[] separateLine(String line) {
        List<String> listOfStatements = new LinkedList<>();
        String buff = "";
        for (int i = 0; i < line.length(); i++) {
            while (i < line.length() && line.charAt(i) != ';') {
                if (line.charAt(i) == '[') {
                    while (line.charAt(i) != ']') {
                        if (line.charAt(i) == '"') {
                            buff += line.charAt(i);
                            i++;
                            while (line.charAt(i) != '"') {
                                buff += line.charAt(i);
                                i++;
                            }
                        }
                        buff += line.charAt(i);
                        i++;
                    }
                }
                buff += line.charAt(i);
                i++;
            }
            listOfStatements.add(buff);
            buff = "";
        }
        String[] statements = new String[listOfStatements.size()];
        int j = 0;
        for (String state : listOfStatements) {
            statements[j] = state;
            j++;
        }
        return statements;
    }
}
