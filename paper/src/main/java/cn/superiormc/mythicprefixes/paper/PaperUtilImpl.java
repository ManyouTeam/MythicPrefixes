package cn.superiormc.mythicprefixes.paper;

import cn.superiormc.mythicprefixes.utils.PacketInventoryUtil;
import cn.superiormc.mythicprefixes.utils.PaperTextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class PaperUtilImpl implements PacketInventoryUtil.PaperUtil {

    @Override
    public Component modernParse(Player player, String message) {
        return PaperTextUtil.modernParse(message, player);
    }

}
