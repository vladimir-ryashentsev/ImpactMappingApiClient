package ru.kackbip.impactMapping.api;

/**
 * Created by ryashentsev on 08.11.2016.
 */

public class CommandExecutorNotFound extends Exception {
    public CommandExecutorNotFound(Object command){
        super(String.format("Command executor not found for command %s", command.getClass().getCanonicalName()));
    }
}
