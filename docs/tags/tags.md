# 🏷️Tags

Open `/prefixes/` folder in plugin folder, then you will find `default.yml` file, this is an example file for tag config, if you want to create new tag, please copy it and rename it to tag ID you want to use. The prefix ID is same as file name, for example, `default.yml` file means it's tag (or prefix) **ID** is `default`.

An example file is here:

```yaml
# Remove whole display item section to make it hide in GUI
display-item:
  unlocked:
    material: DRAGON_EGG
    name: '{display-value}'
    lore:
      - '&7Display Value: {display-value}'
      - '&7Preview: %mythicprefixes_prefix_example_chat% %player_name%'
      - '&dOp will always can use the tag, give'
      - '&dyourself &4-mythicprefixes.bypass.*'
      - '&dpermission to avoid that.'
      - '&eClick to use!'
  using:
    material: DRAGON_EGG
    name: '{display-value}'
    lore:
      - '&7Display Value: {display-value}'
      - '&7Preview: %mythicprefixes_prefix_example_chat% %player_name%'
      - '&cYou are now using this prefix!'
      - '&eClick to cancel use this prefix!'
  locked:
    material: DRAGON_EGG
    name: '&cLocked'
    lore:
      - '&7Display Value: {display-value}'
      - '&7Preview: %mythicprefixes_prefix_example_chat% %player_name%'
      - '&cKill a dragon to unlock!'
  max-reached:
    material: DRAGON_EGG
    name: '{display-value}'
    lore:
      - '&7Preview: %mythicprefixes_prefix_example_chat% %player_name%'
      - '&cYou have reached max use of prefix!'

bedrock:
  extra-line: '&f{status}'

dynamic-prefix: false

display-value: '&8Dragon Killer'
weight: 15
auto-hide: false

# Premium version only
groups:
  - chat
  - example

effects:
  enabled: false
  1:
    type: MythicLib
    stat: MAX_HEALTH
    value: 1
  2:
    # Premium version only
    type: MythicMobs
    stat: ATTACK_DAMAGE
    value: 1

equip-actions:
  1:
    type: message
    message: 'Start equip the tag!'
unequip-actions:
  1:
    type: message
    message: 'Not equip the tag!'
circle-actions:
  period-tick: 20
  1:
    type: message
    message: 'This is default message. Default prefix has equipped so prefix effect also activated!'
# Premium version only
click-actions:
  condition-not-meet:
    1:
      type: message
      message: 'You did not unlock this prefix!'
  max-limit-reached:
    1:
      type: message
      message: 'You reached the limit of max prefix using!'

conditions:
  1:
    type: permission
    permission: 'killed.dragon'
```

The `display-item` section is used to set the display items in the title GUI. If deleted, this tag will not be visible in the GUI.

If you find it troublesome, you can directly use ItemFormat under `display-item` key, so that all four states will use the same item, and support the **{status}** placeholder to display the current status of the tag.

The content displayed by the `{status}` placeholder can be set in `config.yml`, as shown below:

```yaml
# {status} Placeholder
status-placeholder:
  unlocked: '&eClick to use'
  using: '&eClick to cancel use this prefix.'
  locked: '&cYou do not have permission to use this prefix.'
  max-reached: '&cYou can not use anymore prefix.'
```

Other options:

* display-value: The content displayed by this tag supports the use of PlaceholderAPI.&#x20;
* weight: The weight displayed for this tag is higher for lower values, and tags with the same weight will be sorted based on their ID.&#x20;
* groups: Which groups does this tag belong to. <mark style="color:red;">(Premium)</mark>
* effects: See [this page](tag-effect-buff.md).&#x20;
* conditions: The unlocking conditions for this tag.&#x20;
* equip-actions: The actions will executed after player equip this tag.
* unequip-actions: The actions will executed after player unequip this tag.
* circle-actions: The actions that will circle executed when player using this tag, you can set perior time at `config.yml` file.
  * period-tick:  Optional. If not set, will use default value in `config.yml` file.
* click-actions: The actions will executed if player click the prefix with specifed status, only supports `condition-not-meet` and `max-limit-reached` status at the moment. <mark style="color:red;">(Premium)</mark>
* auto-hide: Whether we auto hide this prefix in GUI when player does not meet it's use condition.
* bedrock: The settings for bedrock UI. Click [here](tag-gui.md) to know more.
* dynamic-prefix: Whether enable or disable dynamic prefix feature for this prefix. If enabled, `display-value` set in this prefix will be ignored. Click [here](dynamic-tag-prefix.md) to know more about this feature.

When the prefix be used in `default-prefixes` option at display placeholder configs, only those options will work.

```yaml
display-value: '&fPlayer'
weight: 1
auto-hide: false

conditions: []
```
