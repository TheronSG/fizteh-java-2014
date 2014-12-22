package ru.fizteh.fivt.students.ilin_ilia.storeable.commands;


import ru.fizteh.fivt.students.ilin_ilia.storeable.dbexceptions.StopInterpretationException;

@FunctionalInterface
public interface LambdaFunction<T, R> {
    void accept(T t, R r) throws StopInterpretationException;
}
