# 🔗Compatibility

## **Direct compatibility**

### <mark style="color:red;">Directly</mark> supported stat plugins list

You can add those plugins stat as prefix effect bonus.

* MythicLib (Support stat from MMOCore or MMOItems)
* MythicMobs
* AuraSkills&#x20;

## PlaceholderAPI: Extra placeholders

MythicPrefixes provides those new placeholders to PlaceholderAPI!

### %mythicprefixes\_\<displayPlaceholderID>%

You can use this **PlaceholderAPI**'s placeholder at other plugins (like chat, scoreboard) to display player's using tag. For example: `%mythicprefixes_chat%`.

{% hint style="info" %}
If you don't know what does `displayPlaceholderID` mean, please view [this page](../tags/tags.md) first.\
If your display placeholder ID or prefix ID includes `_` symbol, you can replace them to `-` symbol, otherwise plugin may not parse them correctly.
{% endhint %}

### %mythicprefixes\_prefix\_\<prefixID>\_\<displayPlaceholderID>%

Use this placeholder can display player's using tag then plus the specifie tag, perfect for people want to help player preview the tag.

### %mythicprefixes\_prefix\_\<prefixID>% <mark style="color:red;">- PREMIUM</mark>

Display the prefix only.

### %mythicprefixes\_no\_\<displayPlaceholderID>\_\<number>% <mark style="color:red;">- PREMIUM</mark>

Display the Xth tag of the current display placeholder. For example: %mythicprefixes\_no\_chat\_2% means the second tag displayed in `chat` display placeholder.

### %mythicprefixes\_amount%

This placeholder will display the amount of player's using tag.

You can set the maximum number of tags that players can use simultaneously in the `config.yml` file.

### %mythicprefixes\_status\_\<prefixID>%

This placeholder will display specified prefix's status for a player.

Possible values: [https://github.com/PQguanfang/MythicPrefixes/blob/master/src/main/java/cn/superiormc/mythicprefixes/objects/PrefixStatus.java](https://github.com/PQguanfang/MythicPrefixes/blob/master/src/main/java/cn/superiormc/mythicprefixes/objects/PrefixStatus.java)

### %mythicprefixes\_max%

Display the max amount of tags that player can use simultaneously.

### About dynamic prefix placeholders

Please click [here](../tags/dynamic-tag-prefix.md) to know about them.
