package cn.superiormc.mythicprefixes.manager;

import cn.superiormc.mythicprefixes.MythicPrefixes;
import cn.superiormc.mythicprefixes.utils.CommonUtil;

import java.io.File;

public class InitManager {
    public static InitManager initManager;

    private boolean firstLoad = false;

    public InitManager() {
        initManager = this;
        File file = new File(MythicPrefixes.instance.getDataFolder(), "config.yml");
        if (!file.exists()) {
            MythicPrefixes.instance.saveDefaultConfig();
            firstLoad = true;
        }
        init();
    }

    public void init() {
        resourceOutput("languages/en_US.yml", true);
        resourceOutput("languages/zh_CN.yml", true);
        resourceOutput("display_placeholders/chat.yml", false);
        resourceOutput("prefixes/example.yml", false);
        resourceOutput("prefixes/default.yml", false);
    }
    
    private void resourceOutput(String fileName, boolean fix) {
        File tempVal1 = new File(MythicPrefixes.instance.getDataFolder(), fileName);
        if (!tempVal1.exists()) {
            if (!firstLoad && !fix) {
                return;
            }
            File tempVal2 = new File(fileName);
            if (tempVal2.getParentFile() != null) {
                CommonUtil.mkDir(tempVal2.getParentFile());
            }
            MythicPrefixes.instance.saveResource(tempVal2.getPath(), false);
        }
    }
}
