package cn.superiormc.mythicprefixes.objects;

public class DynamicPrefixRequest {

    private final String playerUUID;

    private final String playerName;

    private final String prefixID;

    private final String value;

    public DynamicPrefixRequest(String playerUUID, String playerName, String prefixID, String value) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.prefixID = prefixID;
        this.value = value;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPrefixID() {
        return prefixID;
    }

    public String getValue() {
        return value;
    }
}
