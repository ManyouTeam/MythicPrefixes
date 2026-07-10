# 📝ItemFormat™ (Simply version)

## Material

If the value is empty or illegal, defaults to stone.

```yaml
material: APPLE
```

## Amount

Support use PlaceholderAPI or math calculate. For example, `%player_health% * 5`.

```yaml
amount: 5
```

## Custom Name/Display Name

According to your configuration file, there are two formats, one is the old version color code used before version 1.9, or the Text Component used in later versions. The former uses a color code format we created, while the latter uses Mini Message format, as detailed [here](https://docs.advntr.dev/minimessage/format.html). Mini Message format require your server core is Paper.

```yaml
name: '&fA smart sword'
```

## Lore

You can use `\n` to represent line breaks.

According to your configuration file, there are two formats, one is the old version color code used before version 1.9, or the Text Component used in later versions. The former uses a color code format we created, while the latter uses Mini Message format, as detailed [here](https://docs.advntr.dev/minimessage/format.html). Mini Message format require your server core is Paper.

```yaml
lore:
  - '&fLine 1'
  - '&fLine 2'
```

## Flags

Possible Value: `HIDE_ENCHANTS, HIDE_ATTRIBUTES, HIDE_UNBREAKABLE, HIDE_DESTROYS, HIDE_PLACED_ON, HIDE_ADDITIONAL_TOOLTIP, HIDE_DYE, HIDE_ARMOR_TRIM`.

```yaml
flags:
  - HIDE_ENCHANTS
  - HIDE_ATTRIBUTES
  - HIDE_UNBREAKABLE
  - HIDE_DESTROYS
  - HIDE_PLACED_ON
  - HIDE_ADDITIONAL_TOOLTIP
  - HIDE_DYE
  - HIDE_ARMOR_TRIM
```

## Enchants

Config section format is: `Enchant ID: Enchant Level`.

For enchantment book: You maybe need use `stored-enchants` instead of `enchants`.

For custom enchantments: Some enchantments plugins are not registered their enchantment into game, so this won't work for them.

You should use Minecraft enchantment ID instead of Spigot's after 1.20.5.

```yaml
enchants:
  MENDING: 1
```

## Custom Model Data

```yaml
custom-model-data: 15
```

## Skull&#x20;

Base64: Like example below, only support 1.19+.

```yaml
skull: eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZ
```

## Item Model (1.21.2+) <mark style="color:red;">- Premium</mark> <a href="#item-model-1.21.2" id="item-model-1.21.2"></a>

```yaml
item-model: 'mycustom:itemmodel'
```

## Tooltip Style (1.21.2+)  <mark style="color:red;">- Premium</mark> <a href="#tooltip-style-1.21.2" id="tooltip-style-1.21.2"></a>

```yaml
tootip-style: 'mycustom:tooltip'
```
