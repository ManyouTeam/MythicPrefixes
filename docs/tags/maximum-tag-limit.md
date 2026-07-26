# 🪄Maximum Tag Limit

By deafult, common player can only equip **1** tag, and players who have `group.vip` permission can equip extra 1 tag. This can be changed in `config.yml` file.

```yaml
max-prefixes-amount:
  default:
    default: 1
    vip: 2
  scoreboard:
    deafult: 1
    vip: 2
  # Group ID
  # chat:
  #   default: 1
  #   vip: 1

max-prefixes-amount-conditions:
  vip:
    1:
      type: permission
      permission: 'group.vip'
```

The default section means: By default, the maximum limit shared by all tags, this does not represent a group called `default`, and this section cannot be deleted.

The section below `default` are for different groups. For info about **Tag Group**, please view [this page](tag-group-premium.md). In this example, `scoreboard` is group ID. You can add a tag to group in [Tags](tags.md) config. Configs under group section means how many tags can a player use in this group at most.

{% hint style="info" %}
Tag Group feature only available for <mark style="color:red;">**PREMIUM**</mark> version users.
{% endhint %}

Here is an example:

```yaml
max-prefixes-amount:
  default:
    default: 5
  scoreboard:
    deafult: 3
  chat:
    deafult: 3
```

In this example, players can equip up to 5 tags. Among these 5 tags, up to 3 belong to the `chat` group and up to 3 belong to the `scoreboard` group.

Like your server has tag `A, B, C, D` which in `chat` group, and `E, F, G, H` which in `scoreboard` group. If you don't equip any tags, you can now equip any tags, like I choose tag `A, B, C`, after choose those 3 tags, I can not equip tag `D` anymore, because I reached the limit for chat group. But, I can try equip tag `E, F, G, H` because the tags of the `scoreboard` group did not reach the limit, and also did not reach the default limit of 5 tags. At this point, I can also choose two tags from the `scoreboard` group.

## Effect Limit <mark style="color:red;">- Premium</mark>

Want to allow players equip multi prefixes but only 1 prefix effect actived, other prefix effect will not actived? Try this config! You should put them into your [Tags](tags.md) config.

```yaml
# Other tag options in tag configs...

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
