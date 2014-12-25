package ru.fizteh.fivt.students.ilin_ilia.parallel.interpreter;

public abstract class CommandSeparator {

        public static final String PARAM_REGEXP = "\\S+";

        public abstract String[] setCommand(String statement);

        public abstract String[] separateLine(String line);
}
