package dev.thehud.service;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextService {
    private static final Pattern TAG = Pattern.compile("<[^>]+>");
    private static final Pattern GRADIENT = Pattern.compile("<gradient:#([0-9a-fA-F]{6}):#([0-9a-fA-F]{6})>(.*?)</gradient>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern HEX = Pattern.compile("<#([0-9a-fA-F]{6})>");
    private static final Map<String, String> COLORS = new LinkedHashMap<String, String>();
    private final boolean rgb;

    static {
        COLORS.put("dark_blue", "1"); COLORS.put("dark_green", "2"); COLORS.put("dark_aqua", "3");
        COLORS.put("dark_red", "4"); COLORS.put("dark_purple", "5"); COLORS.put("gold", "6");
        COLORS.put("gray", "7"); COLORS.put("dark_gray", "8"); COLORS.put("blue", "9");
        COLORS.put("green", "a"); COLORS.put("aqua", "b"); COLORS.put("red", "c");
        COLORS.put("light_purple", "d"); COLORS.put("yellow", "e"); COLORS.put("white", "f"); COLORS.put("black", "0");
    }

    public TextService(JavaPlugin plugin) {
        Matcher matcher = Pattern.compile("(?:^|[^0-9])1\\.(\\d+)").matcher(plugin.getServer().getBukkitVersion());
        rgb = matcher.find() && Integer.parseInt(matcher.group(1)) >= 16;
    }

    public String render(String input, Map<String, String> placeholders) {
        String output = input == null ? "" : input;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) output = output.replace("<" + entry.getKey() + ">", entry.getValue());
        output = output.replace("<bold>", "&l").replace("</bold>", "").replace("<italic>", "&o").replace("</italic>", "")
                .replace("<underlined>", "&n").replace("</underlined>", "").replace("<strikethrough>", "&m").replace("</strikethrough>", "")
                .replace("<reset>", "&r").replace("</reset>", "");
        output = gradients(output);
        Matcher hex = HEX.matcher(output); StringBuffer converted = new StringBuffer();
        while (hex.find()) hex.appendReplacement(converted, Matcher.quoteReplacement(rgb ? hex(hex.group(1)) : "&9"));
        hex.appendTail(converted); output = converted.toString();
        for (Map.Entry<String, String> color : COLORS.entrySet()) {
            output = output.replace("<" + color.getKey() + ">", "&" + color.getValue()).replace("</" + color.getKey() + ">", "");
        }
        return ChatColor.translateAlternateColorCodes('&', TAG.matcher(output).replaceAll(""));
    }

    private String gradients(String input) {
        Matcher matcher = GRADIENT.matcher(input); StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            if (!rgb) { matcher.appendReplacement(result, Matcher.quoteReplacement("&9" + matcher.group(3))); continue; }
            int from = Integer.parseInt(matcher.group(1), 16), to = Integer.parseInt(matcher.group(2), 16); String value = matcher.group(3); StringBuilder colored = new StringBuilder();
            int visible = Math.max(1, visibleLength(value) - 1), position = 0;
            for (int i = 0; i < value.length(); i++) {
                char character = value.charAt(i);
                if ((character == '&' || character == '§') && i + 1 < value.length()) { colored.append(character).append(value.charAt(++i)); continue; }
                double ratio = position++ / (double) visible; int red = mix(from >> 16 & 255, to >> 16 & 255, ratio); int green = mix(from >> 8 & 255, to >> 8 & 255, ratio); int blue = mix(from & 255, to & 255, ratio);
                colored.append(hex(String.format("%02X%02X%02X", red, green, blue))).append(character);
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(colored.toString()));
        }
        matcher.appendTail(result); return result.toString();
    }

    private int visibleLength(String value) { int length = 0; for (int i = 0; i < value.length(); i++) { if ((value.charAt(i) == '&' || value.charAt(i) == '§') && i + 1 < value.length()) i++; else length++; } return length; }
    private int mix(int from, int to, double ratio) { return (int) Math.round(from + (to - from) * ratio); }
    private String hex(String value) { StringBuilder output = new StringBuilder("§x"); for (char character : value.toCharArray()) output.append('§').append(character); return output.toString(); }
    public String join(java.util.List<String> lines, Map<String, String> placeholders) { StringBuilder output = new StringBuilder(); for (int i = 0; i < lines.size(); i++) { if (i > 0) output.append('\n'); output.append(render(lines.get(i), placeholders)); } return output.toString(); }
}
