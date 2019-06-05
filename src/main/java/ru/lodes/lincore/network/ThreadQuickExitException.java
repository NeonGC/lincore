package ru.lodes.lincore.network;

public final class ThreadQuickExitException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private ThreadQuickExitException() {
        this.setStackTrace(new StackTraceElement[0]);
    }
}
