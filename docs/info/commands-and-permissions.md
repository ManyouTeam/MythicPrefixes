# ⌨️Commands & Permissions

## /mythicprefixes opengui

Open the prefix gui. In-game only.

## /mythicprefixes opengui \<player>

Open the prefix gui for specified player. In-console only.

## /mythicprefixes opengui \<group>

Open the prefix gui, but only includes the tag of the specified group.

## /mythicprefixes opengui \<player> \<group>

Open the prefix gui for specified player, but only includes the tag of the specified group.. In-console only.

## /mythicprefixes reload

Reload the plugin.

## /mythicprefixes addprefix \<prefix>

Add new prefix for self. This is add, which means we won't try remove the prefix player already used. In-game only.

## /mythicprefixes addprefix \<player> \<prefix>

Add new prefix for specifed player. This is add, which means we won't try remove the prefix player already used.

## /mythicprefixes removeprefix \<prefix>

Remove used prefix for self. In-game only.

## /mythicprefixes removeprefix \<player> \<prefix>

Remove used prefix for specifed player.

## /mythicprefixes viewusingprefix

View prefix you are using. In-game only.

## &#x20;/mythicprefixes viewusingprefix \<player>

View specifed player using prefix.

## /mythicprefixes setprefix \<prefixes>

Set your using prefixes to specifed value. Support use `;;` split each prefix.

## /mythicprefixes setprefix \<player> \<prefixes>

Set specified player using prefixes to specifed value. Support use `;;` split each prefix.

## About dynamic prefix commands

Please click [here](../tags/dynamic-tag-prefix.md) to know about them.

## Command's permission

For permissions, you need give player `mythicprefixes.<subCommand>` to use corresponding command, like `mythicprefixes.opengui`.

## Bypass permission

You can give player `mythicprefixes.bypass.<prefixID>` permission to make player bypass plugin check.

By default, op will have this permission.
