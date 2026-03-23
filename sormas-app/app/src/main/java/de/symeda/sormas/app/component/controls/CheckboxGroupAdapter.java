package de.symeda.sormas.app.component.controls;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CheckboxGroupAdapter {

    @SerializedName("value")
    private List<String> value;

    // Custom deserializer
    public static class Deserializer implements JsonDeserializer<CheckboxGroupAdapter> {
        @Override
        public CheckboxGroupAdapter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            CheckboxGroupAdapter adapter = new CheckboxGroupAdapter();

            if (json.isJsonArray()) {
                // Handle array format
                List<String> list = new ArrayList<>();
                for (JsonElement element : json.getAsJsonArray()) {
                    if (element.isJsonPrimitive()) {
                        list.add(element.getAsString());
                    }
                }
                adapter.value = list;
            } else if (json.isJsonPrimitive()) {
                JsonPrimitive primitive = json.getAsJsonPrimitive();
                if (primitive.isBoolean()) {
                    // Handle boolean - set empty list
                    adapter.value = new ArrayList<>();
                } else if (primitive.isString()) {
                    String str = primitive.getAsString();
                    if (str.startsWith("[") && str.endsWith("]")) {
                        // Handle string representation of array
                        String content = str.substring(1, str.length() - 1);
                        if ("NA".equals(content.trim())) {
                            adapter.value = new ArrayList<>();
                        } else {
                            String[] parts = content.split("\\s*,\\s*");
                            adapter.value = Arrays.asList(parts);
                        }
                    } else if ("true".equalsIgnoreCase(str) || "false".equalsIgnoreCase(str)) {
                        // Handle string boolean
                        adapter.value = new ArrayList<>();
                    } else {
                        // Handle single value
                        adapter.value = Collections.singletonList(str);
                    }
                }
            }

            return adapter;
        }
    }
}