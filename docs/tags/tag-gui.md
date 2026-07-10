# 📋Tag GUI

You can use `/prefix opengui` to open tag GUI, player need `mythicprefixes.opengui` permission to use it.&#x20;

You can set prefix display item slot at `prefix-item-slots` option in `config.yml` file.

## Title Update - <mark style="color:red;">Premium, Require packetevents</mark>

You can use {now} and {max} placeholder in choose prefix GUI's title.

```yaml
choose-prefix-gui:
  # You can use {max} to display max page amount, {now} to display now page amount. Require enable title update to
  # update those placeholder after open GUI.
  title: 'Choose your prefix {now}/{max}' # Use placeholder here.
  # PREMIUM version only, if enabled, can update dynamic value used in GUI title.
  title-update:
    # Require packetevents.
    enabled: true # <--- Set it to true
    resend-items-pack: false
```

## Filter <mark style="color:red;">- Premium</mark>

You can add filter item by below format:

```yaml
  filter-item:
    # Set to -1 to hide.
    slot: 47
    material: ANVIL
    name: '&bFilter'
    lore:
      - '&7Filter: {filter}'
    placeholder:
      all: '&aALL'
      using: '&cUSING'
      can-use: '&dCan Use'
```

## Custom Item

You can add custom item by below format:

```yaml
  custom-item:
    49: # Mean the slot
      material: SPAWNER
      name: '&4Unequip All tags'
      lore:
        - '&cClick to unequip.'
      actions:
        1:
          type: removeall
      bedrock:
        extra-line: '&fThis can not rollback!'
    53: # Mean the slot
      material: BARRIER
      name: '&cClose'
      lore:
        - '&cClick to close this menu.'
      actions:
        1:
          type: close
```

## Bedrock <mark style="color:red;">- Premium</mark>

This allows bedrock players display form UI instead of Java menus. Like this:

<figure><img src="../.gitbook/assets/屏幕截图 2025-02-02 223508.png" alt=""><figcaption></figcaption></figure>

```yaml
  # Premium version only
  bedrock:
    enabled: true
    # Support value: FLOODGATE, UUID
    check-method: FLOODGATE
```

### Requirements <a href="#requirements" id="requirements"></a>

* Both Geyser and Floodgate are **required in your Spigot server**. If you are using BungeeCord proxy, you need install them both in backward server and proxy server.
* You must set Geyser's `auth-type` to **`floodgate`**.
* You need carefully follow [those steps](https://wiki.geysermc.org/floodgate/setup/) to setup floodgate in your backend server if you are using BungeeCord.

{% hint style="info" %}
If your server is correctly installed and configured with floorgate, the console will prompt `Hooking into floorgate` when MythicPrefixes start to run. If this prompt didn't appear but if you insist that your server has a floodgate, it is very likely that you accidentally downloaded the free version of the plugin.&#x20;
{% endhint %}

* All bedrock players will use the new UI. If not, you can try set `choose-prefix-gui.bedrock.check-method` option value from **FLOODGATE** to **UUID** in `config.yml`.
* Bedrock UI is auto generated and don't need any manual modification.

For now, we support those options for bedrock buttons or prefix configs.

* icon: The icon of this button, format is `path;;<image path> or url;;<image url>`. The image path is bedrock texture path, not your plugin path, for example: `path;;textures/blocks/stone_granite.png`. If you don't know what it is, ingore this, don't ask me.
* hide: Hide button for bedrock players.
* extra-line: Display second line at bedrock button.
