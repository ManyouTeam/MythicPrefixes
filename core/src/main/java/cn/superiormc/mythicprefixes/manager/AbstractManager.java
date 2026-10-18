package cn.superiormc.mythicprefixes.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractManager {

    private static final Map<Class<? extends AbstractManager>, AbstractManager> MANAGERS =
            new LinkedHashMap<>();

    private static final Set<AbstractManager> INITIALIZED =
            Collections.newSetFromMap(new IdentityHashMap<>());

    protected AbstractManager() {
        synchronized (AbstractManager.class) {
            AbstractManager previous = MANAGERS.put(getClass(), this);
            if (previous != null) {
                INITIALIZED.remove(previous);
            }
        }
    }

    public void onInit() {
    }

    public void onPluginReload() {
    }

    public void onPluginDisable() {
    }

    public static void initializeManagers() {
        for (AbstractManager manager : snapshot(false)) {
            synchronized (AbstractManager.class) {
                if (!INITIALIZED.add(manager)) {
                    continue;
                }
            }
            manager.onInit();
        }
    }

    public static void reloadManagers() {
        for (AbstractManager manager : snapshot(true)) {
            manager.onPluginReload();
        }
    }

    public static void disableManagers() {
        for (AbstractManager manager : snapshot(true)) {
            manager.onPluginDisable();
        }
        synchronized (AbstractManager.class) {
            MANAGERS.clear();
            INITIALIZED.clear();
        }
    }

    private static List<AbstractManager> snapshot(boolean reverse) {
        List<AbstractManager> result;
        synchronized (AbstractManager.class) {
            result = new ArrayList<>(MANAGERS.values());
        }
        if (reverse) {
            Collections.reverse(result);
        }
        return result;
    }
}
