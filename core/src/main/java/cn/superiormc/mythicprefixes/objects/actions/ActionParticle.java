package cn.superiormc.mythicprefixes.objects.actions;

import cn.superiormc.mythicprefixes.manager.ErrorManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ActionParticle extends AbstractRunAction {

    public ActionParticle() {
        super("particle");
        setRequiredArgs("particle", "count", "offset-x", "offset-y", "offset-z", "speed");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, Player player) {
        Location loc = player.getLocation().add(0, 1, 0); // 在玩家头顶播放

        // 读取参数
        String particleName = singleAction.getString("particle", player);
        int count = singleAction.getInt("count");
        double offsetX = singleAction.getDouble("offset-x", player);
        double offsetY = singleAction.getDouble("offset-y", player);
        double offsetZ = singleAction.getDouble("offset-z", player);
        double speed = singleAction.getDouble("speed", player);

        try {
            Particle particle = Particle.valueOf(particleName.toUpperCase());
            player.getWorld().spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ, speed);
        } catch (IllegalArgumentException e) {
            ErrorManager.errorManager.sendErrorMessage("§cInvalid particle name: " + particleName);
        }
    }
}