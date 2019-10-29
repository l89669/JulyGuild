package com.github.julyss2019.mcsp.julyguild;

import java.util.HashMap;
import java.util.Map;

public class Placeholder {
    private Map<String, String> placeholderMap;

    private Placeholder(Map<String, String> map) {
        this.placeholderMap = map;
    }

    private Placeholder() {}

    public Map<String, String> getPlaceholders() {
        return placeholderMap;
    }

    public static class Builder {
        private Map<String, String> map = new HashMap<>();

        public Builder() {}

        public Builder add(String key, String value) {
            map.put(key, value);
            return this;
        }

        public Placeholder build() {
            return new Placeholder(map);
        }
    }
}
