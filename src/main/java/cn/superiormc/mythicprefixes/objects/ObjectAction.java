package cn.superiormc.mythicprefixes.objects;

import cn.superiormc.mythicprefixes.manager.CacheManager;
import cn.superiormc.mythicprefixes.manager.ConfigManager;
import cn.superiormc.mythicprefixes.manager.ErrorManager;
import cn.superiormc.mythicprefixes.objects.buttons.ObjectPrefix;
import cn.superiormc.mythicprefixes.utils.CommonUtil;
import cn.superiormc.mythicprefixes.utils.TextUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ObjectAction {

    private final List<String> actions;

    public ObjectAction(List<String> actions) {
        this.actions = actions;
    }

    public boolean isEmpty() {
        return actions.isEmpty();
    }

    public void doAction(Player player) {
        if (player == null) {
            return;
        }
        for (String singleAction : actions) {
            singleAction = replacePlaceholder(singleAction, player);
            if (singleAction.startsWith("none")) {
                return;
            } else if (singleAction.startsWith("sound: ")) {
                // By: iKiwo
                String soundData = singleAction.substring(7); // "sound: LEVEL_UP;volume;pitch"
                String[] soundParts = soundData.split(";;");
                if (soundParts.length >= 1) {
                    String soundName = soundParts[0];
                    float volume = 1.0f;
                    float pitch = 1.0f;
                    if (soundParts.length >= 2) {
                        try {
                            volume = Float.parseFloat(soundParts[1]);
                        } catch (NumberFormatException e) {
                            ErrorManager.errorManager
                                    .sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Invalid volume value in sound action.");
                        }
                    }
                    if (soundParts.length >= 3) {
                        try {
                            pitch = Float.parseFloat(soundParts[2]);
                        } catch (NumberFormatException e) {
                            ErrorManager.errorManager
                                    .sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Invalid pitch value in sound action.");
                        }
                    }
                    Location location = player.getLocation();
                    player.playSound(location, soundName, volume, pitch);
                } else {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Invalid sound action format.");
                }
            } else if (singleAction.startsWith("message: ")) {
                player.sendMessage(TextUtil.parse(singleAction.substring(9), player));
            } else if (singleAction.startsWith("announcement: ")) {
                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                for (Player p : players) {
                    p.sendMessage(TextUtil.parse(singleAction.substring(14), player));
                }
            } else if (singleAction.startsWith("effect: ")) {
                try {
                    if (PotionEffectType.getByName(singleAction.substring(8).split(";;")[0].toUpperCase()) == null) {
                        ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicPrefixes] §cError: Can not found potion effect: " +
                                singleAction.split(";;")[0] + ".");
                    }
                    PotionEffect effect = new PotionEffect(PotionEffectType.getByName(singleAction.split(";;")[0].toUpperCase()),
                            Integer.parseInt(singleAction.substring(8).split(";;")[2]),
                            Integer.parseInt(singleAction.substring(8).split(";;")[1]) - 1,
                            true,
                            true,
                            true);
                    player.addPotionEffect(effect);
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your effect action in shop configs can not being correctly load.");
                }
            } else if (singleAction.startsWith("entity_spawn: ")) {
                if (singleAction.split(";;").length == 1) {
                    EntityType entity = EntityType.valueOf(singleAction.substring(14).split(";;")[0].toUpperCase());
                    player.getLocation().getWorld().spawnEntity(player.getLocation(), entity);
                } else if (singleAction.split(";;").length == 5) {
                    World world = Bukkit.getWorld(singleAction.substring(18).split(";;")[1]);
                    Location location = new Location(world,
                            Double.parseDouble(singleAction.substring(18).split(";;")[2]),
                            Double.parseDouble(singleAction.substring(18).split(";;")[3]),
                            Double.parseDouble(singleAction.substring(18).split(";;")[4]));
                    EntityType entity = EntityType.valueOf(singleAction.substring(14).split(";;")[0].toUpperCase());
                    if (location.getWorld() == null) {
                        ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[MythicPrefixes] §cError: Your entity_spawn action in shop configs can not being correctly load.");
                    }
                    location.getWorld().spawnEntity(location, entity);
                }
            } else if (singleAction.startsWith("teleport: ")) {
                try {
                    if (singleAction.split(";;").length == 4) {
                        Location loc = new Location(Bukkit.getWorld(singleAction.substring(10).split(";;")[0]),
                                Double.parseDouble(singleAction.substring(10).split(";;")[1]),
                                Double.parseDouble(singleAction.substring(10).split(";;")[2]),
                                Double.parseDouble(singleAction.substring(10).split(";;")[3]),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        player.teleport(loc);
                    }
                    else if (singleAction.split(";;").length == 6) {
                        Location loc = new Location(Bukkit.getWorld(singleAction.split(";;")[0]),
                                Double.parseDouble(singleAction.substring(10).split(";;")[1]),
                                Double.parseDouble(singleAction.substring(10).split(";;")[2]),
                                Double.parseDouble(singleAction.substring(10).split(";;")[3]),
                                Float.parseFloat(singleAction.substring(10).split(";;")[4]),
                                Float.parseFloat(singleAction.substring(10).split(";;")[5]));
                        player.teleport(loc);
                    }
                    else {
                        ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your teleport action in shop configs can not being correctly load.");
                    }
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your teleport action in shop configs can not being correctly load.");
                }
            } else if (singleAction.startsWith("console_command: ")) {
                CommonUtil.dispatchCommand(singleAction.substring(17));
            } else if (singleAction.startsWith("player_command: ")) {
                CommonUtil.dispatchCommand(player, singleAction.substring(16));
            } else if (singleAction.startsWith("op_command: ")) {
                CommonUtil.dispatchOpCommand(player, singleAction.substring(12));
            } else if (singleAction.equals("close")) {
                player.closeInventory();
            } else if (singleAction.startsWith("addprefix: ")) {
                try {
                    ObjectPrefix prefix = ConfigManager.configManager.getPrefix(singleAction.substring(11));
                    if (prefix != null) {
                        CacheManager.cacheManager.getPlayerCache(player).addActivePrefix(prefix);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your buy action in shop configs can not being correctly load.");
                }
            } else if (singleAction.startsWith("removeprefix: ")) {
                try {
                    ObjectPrefix prefix = ConfigManager.configManager.getPrefix(singleAction.substring(14));
                    if (prefix != null) {
                        CacheManager.cacheManager.getPlayerCache(player).removeActivePrefix(prefix);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your buy action in shop configs can not being correctly load.");
                }
            } else if (singleAction.equals("removeall")) {
                CacheManager.cacheManager.getPlayerCache(player).removeAllActivePrefix();
            }
        }
    }
    private String replacePlaceholder(String str, Player player){
        str = str.replace("{world}", player.getWorld().getName())
                .replace("{player_x}", String.valueOf(player.getLocation().getX()))
                .replace("{player_y}", String.valueOf(player.getLocation().getY()))
                .replace("{player_z}", String.valueOf(player.getLocation().getZ()))
                .replace("{player_pitch}", String.valueOf(player.getLocation().getPitch()))
                .replace("{player_yaw}", String.valueOf(player.getLocation().getYaw()))
                .replace("{player}", player.getName());
        if (CommonUtil.checkPluginLoad("PlaceholderAPI")) {
            str = PlaceholderAPI.setPlaceholders(player, str);
        }
        return str;
    }
}
