package org.jsonator.Converters;

import java.util.Map;

import org.jsonator.TypeToken;

public class JsonConverterDefaultMap extends JsonConverterMap<Object, Object> {

    @Override
    public TypeToken<Map<Object, Object>> getMyType() {
        return new TypeToken<>() {
        };
    }
}
