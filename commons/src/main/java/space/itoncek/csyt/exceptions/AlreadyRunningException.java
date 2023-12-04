/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.exceptions;

public class AlreadyRunningException extends Throwable {
    public AlreadyRunningException() {
        super("This object is already running!");
    }
}
