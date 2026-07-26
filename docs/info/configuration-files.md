# 🛠️Configuration files

The plugin generates the following configuration files, some of which will only be generated after you first use this feature.

* `datas`: The location for storing plugin data files. It will only be generated without using a database. Do not modify any content here.
* `languages`: The location for storing language files. You can set the language file used by the plugin through the `config-files.language` option in the `config.yml` file. You can customize various messages within the plugin game through language files. It is not supported to display the corresponding language file based on the player client language. You can only display the same language for all players.
* `display_placeholders`: The location for display placeholder configuration files.
* `prefixes`: The location for tag configuration files.
* `config.yml` file: The location for main common settings for plugins.

## Config.yml file content <a href="#config.yml-file-content" id="config.yml-file-content"></a>

```yaml
# MythicPrefixes Made by @PQguanfang
#
# Read the wiki here: mythicprefixes.superiormc.cn

debug: false

language: en_US

cache:
  # If you are facing issue when plugin load cache, try set this option to JOIN.
  load-mode: LOGIN
  # Bypass condition check when player still joining the server.
  bypass-condition-when-loading: true
  # Cache remove will delay 3 seconds after player left the server, set -1 to disable.
  remove-delay: -1

# {status} Placeholder
status-placeholder:
  unlocked: '&eClick to use'
  using: '&eClick to cancel use this prefix.'
  locked: '&cYou do not have permission to use this prefix.'
  max-reached: '&cYou can not use anymore prefix.'

circle-actions:
  period-tick: 20

max-prefixes-amount:
  default: 1
  vip: 2

max-prefixes-amount-conditions:
  vip:
    1:
      type: permission
      permission: 'group.vip'

choose-prefix-gui:
  title: 'Choose your prefix'
  size: 54
  forbid-click-outside: false
  auto-translate-item-name: true
  prefix-item-slot: [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,
                     16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,
                     31,32,33,34,35,36,37,38,39,40,41,42,43,44]
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
  next-page-item:
    slot: 52
    material: ARROW
    name: '&cNext page'
    lore:
      - '&7Page: {now}/{max}'
      - '&eClick to view next page'
  previous-page-item:
    slot: 46
    material: ARROW
    name: '&cPrevious page'
    lore:
      - '&7Page: {now}/{max}'
      - '&eClick to view previous page'
  custom-item:
    49: # Mean the slot
      material: SPAWNER
      name: '&4Unequip All tags'
      lore:
        - '&cClick to unequip.'
      actions:
        1:
          type: removeall
    53: # Mean the slot
      material: BARRIER
      name: '&cClose'
      lore:
        - '&cClick to close this menu.'
      actions:
        1:
          type: close

database:
  enabled: false
  jdbc-url: "jdbc:mysql://localhost:3306/mythicprefixes?useSSL=false&autoReconnect=true"
  jdbc-class: "com.mysql.cj.jdbc.Driver"
  properties:
    user: root
    password: 123456

auto-save:
  enabled: false
  period-tick: 6000
  hide-message: false

libreforge-hook: false
libreforge-effects:
  - id: default
    effects:
      - id: bonus_health
        args:
          health: 40
      - id: damage_multiplier
        args:
          multiplier: 4.0
        triggers:
          - melee_attack
    conditions: []
```
