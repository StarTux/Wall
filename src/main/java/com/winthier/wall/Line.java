package com.winthier.wall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.json.simple.JSONValue;

abstract class Line {
    abstract void send(Player player);

    static Line of(Object o) {
        if (o instanceof String) {
            final String string = ChatColor.translateAlternateColorCodes('&', (String)o);
            return new Line() {
                @Override void send(Player player) {
                    player.sendMessage(string);
                }
            };
        }
        if (o instanceof List) {
            @SuppressWarnings("unchecked") final List<?> list = (List<?>)o;
            return new AdvancedLine(list);
        }
        if (o instanceof Map) {
            @SuppressWarnings("unchecked") final Map<?, ?> map = (Map<?, ?>)o;
            if (map.containsKey("Command")) {
                final String command = map.get("Command").toString();
                return new Line() {
                    @Override void send(Player player) {
                        player.performCommand(command);
                    }
                };
            }
        }
        return null;
    }
}

class AdvancedLine extends Line {
    final String json;

    AdvancedLine(List<?> list) {
        List<Object> json = new ArrayList<>();
        for (Object o: list) {
            Object comp = component(o);
            if (comp != null) json.add(comp);
        }
        this.json = JSONValue.toJSONString(json);
    }

    static String formatted(Object o) {
        return ChatColor.translateAlternateColorCodes('&', o.toString());
    }

    static Object component(Object o) {
        if (o instanceof String) {
            Map<String, String> result = new HashMap<>();
            result.put("text", formatted(o));
            return result;
        } else if (o instanceof Map) {
            @SuppressWarnings("unchecked") final Map<?, ?> map = (Map<?, ?>)o;
            ConfigurationSection config = new MemoryConfiguration().createSection("tmp", map);
            return componentOfConfig(config);
        }
        return null;
    }

    static Object componentOfConfig(ConfigurationSection config) {
        if (config.isConfigurationSection("button")) {
            return buttonOfConfig(config.getConfigurationSection("button"));
        }
        return null;
    }

    static Object buttonOfConfig(ConfigurationSection config) {
        // Text
        Map<String, Object> result = new HashMap<>();
        result.put("text", formatted(config.getString("Text", "")));
        // Command or Suggestion
        Map<String, Object> clickEvent = new HashMap<>();
        boolean hasClickEvent = true;
        if (config.isSet("Suggestion")) {
            clickEvent.put("action", "suggest_command");
            clickEvent.put("value", config.getString("Suggestion"));
        } else if (config.isSet("URL")) {
            clickEvent.put("action", "open_url");
            clickEvent.put("value", config.getString("URL"));
        } else if (config.isSet("Command")) {
            clickEvent.put("action", "run_command");
            clickEvent.put("value", config.getString("Command", "/help"));
        } else {
            hasClickEvent = false;
        }
        if (hasClickEvent) { result.put("clickEvent", clickEvent); }
        // Tooltip
        if (config.isSet("Tooltip")) {
            Map<String, Object> hoverEvent = new HashMap<>();
            result.put("hoverEvent", hoverEvent);
            hoverEvent.put("action", "show_text");
            final String value;
            value = formatted(config.getString("Tooltip"));
            hoverEvent.put("value", value);
        }
        return result;
    }

    @Override void send(Player player) {
        String command = "tellraw " + player.getName() + " " + json;
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
    }
}
