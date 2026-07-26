# 💪Tag Effect/BUFF

## Note

Free version can only set up to 3 tags enable tag effect, premium version don't have this limit.&#x20;

## libreforge Effects

{% hint style="info" %}
MythicPrefixes does not package libreforege, you have to purcahse any of Auxilor's plugin that package libreforage then install it in your server to make this work!
{% endhint %}

If you want to a tag has libreforge effects, you need do those things:

* Set `libreforge-hook` option in `config.yml` to `true`.
* Set `effects.enabled` option in tag configs to `true`.&#x20;
* Add effects at `config.yml`'s libreforge-effects option. **Please note that effect ID must same as tag ID.**

An example:

```yaml
libreforge-effects:
  - id: default # Effect ID
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

## Built-in Effects

If you want to a tag has built-in effect bonus, you need do those things:

* Set `effects.enabled` option in tag configs to `true`.&#x20;
* Add below contents at your tag config if it is not exist.&#x20;
* If you removed BUFF here, you need to restart the server.&#x20;

### MythicLib

Add stats from MythicLib plugin. (Support stats from MMOCore, MMOItems)

```yaml
effects:
  enabled: true
  1: 
    type: MythicLib
    stat: MAX_HEALTH # Stat ID
    value: 1 # Add value
  2: # More effects...
```

### MythicMobs&#x20;

Add stats from MythicMobs plugin.

{% hint style="info" %}
If you are getting **NoSuchMethod** error, this means you are using old version of MythicMobs, you need update it to **LATEST**.\
By default, all stats exist in MythicMobs are disabled, you need enable them in `plugins/MythicMobs/stats.yml` file or other stat configs.
{% endhint %}

```yaml
effects:
  enabled: true
  1: 
    type: MythicMobs
    modifier-type: SET # ADD, SET, MULTIPLY, COMPOUND
    stat: HEALTH
    value: 100
  2: # More effects...
```

### AuraSkills&#x20;

Add stats from AuraSkills plugin.

{% hint style="info" %}
Since AuraSkills is saving the stat modifier, so if your server crash, prefix config change or other situations where the player's stat may not be cleared properly. Although MythicPrefixes consider this problem, if it still occur in your server: You can try restarting the server. If this does not solve the problem, you will have to use the `/skills modifier removeall` command for every players.
{% endhint %}

```yaml
effects:
  enabled: true
  1:
    type: AuraSkills
    stat: HEALTH
    value: 100
  2: # More effects...
```

## Condition

You can set condition for effects. Just try add `conditions` section here.

```yaml
effects:
  enabled: true
  1:
    type: MythicLib
    stat: MAX_HEALTH
    value: 100
    bypass-condition-after-equip: true
    conditions:
      1:
        type: world
        world: lobby
```

There is also a option called `bypass-condition-after-equip` option available, if set to `false`, plugin will auto remove effect if we found player no longer meet the condition of effect.

## Effect Limit <mark style="color:red;">- Premium</mark>

Want to allow players equip multi prefixes but only 1 prefix effect actived, other prefix effect will not actived? Try this config!

```yaml
effects:
  enabled: true
  1:
    type: MythicLib
    stat: MAX_HEALTH
    value: 100
    bypass-condition-after-equip: true # Added this
    conditions: # Added this
      1:
        type: effected_prefix_amount
        amount: 1 # Change to the limit value you want.
```

## FAQ: Your effect not clear correctly?

This is usually caused by multiple plugins on your server attempting to change the player's basic attributes. For example, your server has installed MMOItems and EcoEnchants (which is a very common situation), both of which have the function of increasing the maximum health of players, which may lead to potential functional conflicts. This is not caused by our plugin, MythicPrefixes will clean up the added properties normally, but your other plugins mistakenly identifying the previous health as the player's true health and then adding it back.

Multi server sync plugin like HuskSync may also lead to this problem, try disable attriubute sync in those plugins.
