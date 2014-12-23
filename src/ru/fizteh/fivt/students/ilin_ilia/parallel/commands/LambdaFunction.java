package ru.fizteh.fivt.students.ilin_ilia.parallel.commands;


import ru.fizteh.fivt.students.ilin_ilia.parallel.dbexceptions.StopInterpretationException;

@FunctionalInterface
public interface LambdaFunction<T, R> {
    void accept(T t, R r) throws StopInterpretationException;
}
