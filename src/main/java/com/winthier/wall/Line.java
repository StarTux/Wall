package com.winthier.wall;

import com.cavetale.core.font.Emoji;
import com.cavetale.core.font.GlyphPolicy;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;

public interface Line {
    Component toComponent();

    static String formatted(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    static Line of(Object o) {
        if (o instanceof String) {
            Component component = Component.text(formatted((String) o));
            Component component2 = Emoji.replaceText(component, GlyphPolicy.HIDDEN);
            return new Line() {
                @Override
                public Component toComponent() {
                    return component2;
                }
            };
        }
        if (o instanceof List) {
            @SuppressWarnings("unchecked") final List<?> list = (List<?>) o;
            return new AdvancedLine(list);
        }
        return null;
    }
}

final class AdvancedLine implements Line {
    private final Component component;

    @Override
    public Component toComponent() {
        return component;
    }

    AdvancedLine(final List<?> ls) {
        Component[] list = new Component[ls.size()];
        for (int i = 0; i < list.length; i += 1) {
            list[i] = component(ls.get(i));
        }
        component = TextComponent.ofChildren(list);
    }

    static Component component(Object o) {
        if (o instanceof String) {
            return Component.text(Line.formatted((String) o));
        } else if (o instanceof Map) {
            @SuppressWarnings("unchecked")
            final Map<?, ?> map = (Map<?, ?>) o;
            return configComponent(map);
        } else if (o instanceof List) {
            @SuppressWarnings("unchecked")
            final List<?> list = (List<?>) o;
            Component[] array = new Component[list.size()];
            for (int i = 0; i < array.length; i += 1) {
                array[i] = component(list.get(i));
            }
            return TextComponent.ofChildren(array);
        }
        return Component.empty();
    }

    static TextColor color(String in) {
        if (in == null) {
            return null;
        } else if (in.startsWith("#")) {
            try {
                return TextColor.fromHexString(in);
            } catch (IllegalArgumentException iae) {
                return null;
            }
        } else {
            return NamedTextColor.NAMES.value(in);
        }
    }

    private static Component configComponent(Map<?, ?> config) {
        TextComponent.Builder cb = Component.text();
        if (config.containsKey("Text")) {
            Component component = Component.text(Line.formatted(config.get("Text").toString()));
            component = Emoji.replaceText(component, GlyphPolicy.HIDDEN);
            List<Component> children = component.children();
            for (int i = 0; i < children.size(); i += 1) {
                children.set(i, children.get(i).hoverEvent(null));
            }
            component = component.children(children);
            component = component.hoverEvent(null);
            cb.append(component);
        }
        if (config.containsKey("Color")) {
            TextColor color = color(config.get("Color").toString());
            if (color != null) cb.color(color);
        }
        for (TextDecoration decoration : TextDecoration.values()) {
            String key = decoration.name().substring(0, 1)
                + decoration.name().substring(1).toLowerCase();
            if (config.containsKey(key)) {
                Object v = config.get(key);
                if (v instanceof Boolean) {
                    cb.decoration(decoration, (Boolean) v);
                }
            }
        }
        if (config.containsKey("Font")) {
            cb.font(Key.key(config.get("Font").toString()));
        }
        if (config.containsKey("Suggestion")) {
            cb.clickEvent(ClickEvent.suggestCommand(config.get("Suggestion").toString()));
        } else if (config.containsKey("URL")) {
            cb.clickEvent(ClickEvent.openUrl(config.get("URL").toString()));
        } else if (config.containsKey("Command")) {
            cb.clickEvent(ClickEvent.runCommand(config.get("Command").toString()));
        }
        if (config.containsKey("Tooltip")) {
            cb.hoverEvent(HoverEvent.showText(component((Object) config.get("Tooltip"))));
        }
        return cb.build();
    }
}
