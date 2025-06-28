package cn.superiormc.mythicprefixes.objects;

import cn.superiormc.mythicprefixes.utils.CommonUtil;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class AbstractSingleRun {

    protected ConfigurationSection section;


    public AbstractSingleRun(ConfigurationSection section) {
        this.section = section;
    }

    protected String replacePlaceholder(String content, Player player) {
        content = CommonUtil.modifyString(content
                ,"world", player.getWorld().getName()
                ,"player_x", String.valueOf(player.getLocation().getX())
                ,"player_y", String.valueOf(player.getLocation().getY())
                ,"player_z", String.valueOf(player.getLocation().getZ())
                ,"player_pitch", String.valueOf(player.getLocation().getPitch())
                ,"player_yaw", String.valueOf(player.getLocation().getYaw())
                ,"player", player.getName());
        content = TextUtil.withPAPI(content, player);
        return content;
    }

    public String getString(String path) {
        return section.getString(path);
    }

    public List<String> getStringList(String path) {
        return section.getStringList(path);
    }

    public int getInt(String path) {
        return section.getInt(path);
    }

    public int getInt(String path, int defaultValue) {
        return section.getInt(path, defaultValue);
    }

    public double getDouble(String path) {
        return section.getDouble(path);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return section.getBoolean(path, defaultValue);
    }

    public String getString(String path, Player player) {
        return replacePlaceholder(section.getString(path), player);
    }

    public ConfigurationSection getSection() {
        return section;
    }
}
