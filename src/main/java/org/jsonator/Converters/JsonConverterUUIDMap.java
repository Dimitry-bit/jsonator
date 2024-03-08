package org.jsonator.Converters;

import java.util.Map;
import java.util.UUID;

import org.jsonator.TypeToken;

public class JsonConverterUUIDMap extends JsonConverterMap<UUID, Object> {

    @Override
    public TypeToken<Map<UUID, Object>> getMyType() {
        return new TypeToken<>() {
        };
    }
}
