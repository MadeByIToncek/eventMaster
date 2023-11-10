/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.cg.logic;

import org.json.JSONObject;

public interface Processor<T> {
    public T decompile(JSONObject payload);

    public JSONObject compile(T payload);
}
