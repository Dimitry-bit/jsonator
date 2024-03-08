package org.jsonator.Converters;

import java.util.Collection;

import org.jsonator.TypeToken;

public class JsonConverterDefaultCollection extends JsonConverterCollection<Object> {

    @Override
    public TypeToken<Collection<Object>> getMyType() {
        return new TypeToken<>() {
        };
    }
}
