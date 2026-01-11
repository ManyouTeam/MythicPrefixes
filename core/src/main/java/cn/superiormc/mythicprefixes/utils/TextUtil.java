package cn.superiormc.mythicprefixes.utils;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.manager.LanguageManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

    public static final Pattern SINGLE_HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    public static final Pattern GRADIENT_PATTERN = Pattern.compile("&<#([A-Fa-f0-9]{6})>(.*?)&<#([A-Fa-f0-9]{6})>");
    public static final Pattern LEGACY_COLOR_PATTERN = Pattern.compile("[&§]([0-9a-frlomn])", Pattern.CASE_INSENSITIVE);

    // Minecraft 16 原生颜色码映射
    private static final Map<Character, Color> LEGACY_COLORS = Map.ofEntries(
            Map.entry('0', new Color(0, 0, 0)),
            Map.entry('1', new Color(0, 0, 170)),
            Map.entry('2', new Color(0, 170, 0)),
            Map.entry('3', new Color(0, 170, 170)),
            Map.entry('4', new Color(170, 0, 0)),
            Map.entry('5', new Color(170, 0, 170)),
            Map.entry('6', new Color(255, 170, 0)),
            Map.entry('7', new Color(170, 170, 170)),
            Map.entry('8', new Color(85, 85, 85)),
            Map.entry('9', new Color(85, 85, 255)),
            Map.entry('a', new Color(85, 255, 85)),
            Map.entry('b', new Color(85, 255, 255)),
            Map.entry('c', new Color(255, 85, 85)),
            Map.entry('d', new Color(255, 85, 255)),
            Map.entry('e', new Color(255, 255, 85)),
            Map.entry('f', new Color(255, 255, 255))
    );

    public static String pluginPrefix() {
        if (!CommonUtil.getMajorVersion(16)) {
            return "§a[MythicPrefixes]";
        }
        return "§x§9§8§F§B§9§8[MythicPrefixes]";
    }

    public static String colorize(String input) {

        boolean supportHex = CommonUtil.getMajorVersion(16);

        if (input == null || input.isEmpty()) {
            return input;
        }

        input = applyGradients(input, supportHex);

        Matcher hexMatcher = SINGLE_HEX_PATTERN.matcher(input);
        StringBuilder hexBuffer = new StringBuilder();
        while (hexMatcher.find()) {
            String hex = hexMatcher.group(1);
            if (supportHex) {
                hexMatcher.appendReplacement(hexBuffer, "§x" + toMinecraftHex(hex));
            } else {
                char legacy = getClosestLegacyColor(hex);
                hexMatcher.appendReplacement(hexBuffer, "§" + legacy);
            }
        }
        hexMatcher.appendTail(hexBuffer);
        input = hexBuffer.toString();

        // 处理传统颜色符号
        Matcher legacyMatcher = LEGACY_COLOR_PATTERN.matcher(input);
        StringBuilder legacyBuffer = new StringBuilder();
        while (legacyMatcher.find()) {
            legacyMatcher.appendReplacement(legacyBuffer, "§" + legacyMatcher.group(1).toLowerCase());
        }
        legacyMatcher.appendTail(legacyBuffer);
        input = legacyBuffer.toString();

        return input;
    }

    private static String applyGradients(String input, boolean supportHex) {
        Matcher matcher = GRADIENT_PATTERN.matcher(input);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            String startColor = matcher.group(1);
            String text = matcher.group(2);
            String endColor = matcher.group(3);

            String gradientText = supportHex
                    ? applyGradient(startColor, endColor, text)
                    : applyLegacyGradient(startColor, endColor, text);

            matcher.appendReplacement(buffer, gradientText);
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String applyGradient(String startHex, String endHex, String text) {
        Color start = Color.decode("#" + startHex);
        Color end = Color.decode("#" + endHex);

        int length = text.length();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            float ratio = (float) i / (length - 1);
            int red = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
            int green = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
            int blue = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));

            String hex = String.format("%02x%02x%02x", red, green, blue);
            builder.append("§x").append(toMinecraftHex(hex)).append(text.charAt(i));
        }

        return builder.toString();
    }

    private static String applyLegacyGradient(String startHex, String endHex, String text) {
        Color start = Color.decode("#" + startHex);
        Color end = Color.decode("#" + endHex);

        int length = text.length();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            float ratio = (float) i / (length - 1);
            int red = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
            int green = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
            int blue = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));

            String hex = String.format("%02x%02x%02x", red, green, blue);
            char legacyColor = getClosestLegacyColor(hex);
            builder.append("§").append(legacyColor).append(text.charAt(i));
        }

        return builder.toString();
    }

    private static String toMinecraftHex(String hex) {
        StringBuilder builder = new StringBuilder();
        for (char c : hex.toCharArray()) {
            builder.append("§").append(c);
        }
        return builder.toString();
    }

    private static char getClosestLegacyColor(String hex) {
        Color target = Color.decode("#" + hex);
        double minDistance = Double.MAX_VALUE;
        char closest = 'f';

        for (Map.Entry<Character, Color> entry : LEGACY_COLORS.entrySet()) {
            double distance = colorDistance(target, entry.getValue());
            if (distance < minDistance) {
                minDistance = distance;
                closest = entry.getKey();
            }
        }

        return closest;
    }

    private static double colorDistance(Color c1, Color c2) {
        int r = c1.getRed() - c2.getRed();
        int g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        return 0.3 * r * r + 0.59 * g * g + 0.11 * b * b;
    }

    public static String parse(String text) {
        return MythicPrefixes.methodUtil.legacyParse(text);
    }

    public static String parse(String text, Player player) {
        return parse(withPAPI(text, player));
    }

    public static String withPAPI(String text, Player player) {
        if (text == null) {
            return "";
        }
        if (text.matches("[0-9]+")) {
            return text;
        }
        if (text.contains("%") && CommonUtil.checkPluginLoad("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        text = CommonUtil.parseLang(player, text);
        return text;
    }

    public static void sendMessage(Player player, String rawText) {
        if (rawText == null || rawText.isEmpty()) {
            return;
        }

        if (!rawText.contains("[") || MythicPrefixes.freeVersion) {
            MythicPrefixes.methodUtil.sendChat(player, rawText);
            return;
        }

        boolean sentAny = false;

        // message
        for (String msg : parseSimpleTag(rawText, "message")) {
            MythicPrefixes.methodUtil.sendChat(player, msg);
            sentAny = true;
        }

        // title
        for (TagResult tag : parseArgTag(rawText, "title")) {
            TitleData data = parseTitle(tag);
            MythicPrefixes.methodUtil.sendTitle(
                    player,
                    data.title,
                    data.subTitle,
                    data.fadeIn,
                    data.stay,
                    data.fadeOut
            );
            sentAny = true;
        }

        // actionbar
        for (String msg : parseSimpleTag(rawText, "actionbar")) {
            MythicPrefixes.methodUtil.sendActionBar(player, msg);
            sentAny = true;
        }

        // bossbar
        for (TagResult tag : parseArgTag(rawText, "bossbar")) {
            BossBarData data = parseBossBar(tag);
            MythicPrefixes.methodUtil.sendBossBar(
                    player,
                    data.title,
                    data.progress,
                    data.color,
                    data.style
            );
            sentAny = true;
        }

        // sound
        for (TagResult tag : parseArgTag(rawText, "sound")) {
            SoundData data = parseSound(tag);
            if (data.sound != null) {
                player.playSound(player.getLocation(), data.sound, data.volume, data.pitch);
                sentAny = true;
            }
        }

        // 兜底
        if (!sentAny) {
            MythicPrefixes.methodUtil.sendChat(player, rawText);
        }
    }

    /* ================= 标签解析 ================= */

    private static List<String> parseSimpleTag(String text, String tag) {
        List<String> list = new ArrayList<>();
        Pattern p = Pattern.compile(
                "\\[" + tag + "]([\\s\\S]*?)\\[/" + tag + "]",
                Pattern.CASE_INSENSITIVE
        );
        Matcher m = p.matcher(text);
        while (m.find()) {
            list.add(m.group(1).trim());
        }
        return list;
    }

    private static List<TagResult> parseArgTag(String text, String tag) {
        List<TagResult> list = new ArrayList<>();
        Pattern p = Pattern.compile(
                "\\[" + tag + "(?:=([^\\]]+))?]([\\s\\S]*?)\\[/" + tag + "]",
                Pattern.CASE_INSENSITIVE
        );
        Matcher m = p.matcher(text);
        while (m.find()) {
            list.add(new TagResult(
                    m.group(1),
                    m.group(2).trim()
            ));
        }
        return list;
    }

    private static TitleData parseTitle(TagResult tag) {
        int fadeIn = 10;
        int stay = 70;
        int fadeOut = 20;

        if (tag.args != null) {
            String[] t = tag.args.split(",");
            if (t.length == 3) {
                fadeIn = parseInt(t[0], fadeIn);
                stay = parseInt(t[1], stay);
                fadeOut = parseInt(t[2], fadeOut);
            }
        }

        String[] parts = tag.content.split(";;", 2);
        String title = parts[0];
        String sub = parts.length > 1 ? parts[1] : "";

        return new TitleData(title, sub, fadeIn, stay, fadeOut);
    }

    /* ================= BossBar 解析 ================= */

    private static BossBarData parseBossBar(TagResult tag) {
        String color = "WHITE";
        String style = "SOLID";
        float progress = 1.0f;

        if (tag.args != null) {
            String[] a = tag.args.split(",");
            if (a.length > 0) color = a[0];
            if (a.length > 1) style = a[1];
            if (a.length > 2) progress = parseFloat(a[2], progress);
        }

        return new BossBarData(tag.content, progress, color, style);
    }

    /* ================= 工具 ================= */

    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    private static float parseFloat(String s, float def) {
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            return def;
        }
    }

    /* ================= 内部数据类 ================= */

    private static class TagResult {
        final String args;
        final String content;

        TagResult(String args, String content) {
            this.args = args;
            this.content = content;
        }
    }

    private static class TitleData {
        final String title;
        final String subTitle;
        final int fadeIn, stay, fadeOut;

        TitleData(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
            this.title = title;
            this.subTitle = subTitle;
            this.fadeIn = fadeIn;
            this.stay = stay;
            this.fadeOut = fadeOut;
        }
    }

    private static class BossBarData {
        final String title;
        final float progress;
        final String color;
        final String style;

        BossBarData(String title, float progress, String color, String style) {
            this.title = title;
            this.progress = progress;
            this.color = color;
            this.style = style;
        }
    }

    private static SoundData parseSound(TagResult tag) {
        Sound sound = null;
        float volume = 1f;
        float pitch = 1f;

        if (tag.args != null) {
            String[] args = tag.args.split(",");
            if (args.length > 0) {
                try {
                    sound = Sound.valueOf(args[0].trim().toUpperCase());
                } catch (Exception e) {
                    sound = null; // 无效音效忽略
                }
            }
            if (args.length > 1) volume = parseFloat(args[1], 1f);
            if (args.length > 2) pitch = parseFloat(args[2], 1f);
        }

        return new SoundData(sound, volume, pitch);
    }

    private static class SoundData {
        final Sound sound;
        final float volume;
        final float pitch;

        SoundData(Sound sound, float volume, float pitch) {
            this.sound = sound;
            this.volume = volume;
            this.pitch = pitch;
        }
    }
}
