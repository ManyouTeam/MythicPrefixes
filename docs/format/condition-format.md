# ⚖️Condition Format

## Available Placeholders

* {world}
* {amount}
* {player\_x}
* {player\_y}
* {player\_z}
* {player\_pitch}
* {player\_yaw}
* {player}

## Biome

Player must be in those biomes.

```yaml
  conditions:
    1:
      type: biome
      biome: oraxen
```

## Permission

Player must have all those permissions.

**Remember that OP players will always have all permissions unless plugin set it not by default, so if you want to test this condition, you have to deop yourself.**

```yaml
  conditions:
    1:
      type: permission
      permission: 'group.vip'
```

## Placeholder

Player must be meet the placeholder condition.

Rule can be set to:

* \>=
* <=
* \>
* <
* \== (String)
* \= (Number)
* != (Number or string)
* !\*= (Number or string) Not contains.
* \*= (String) Contains, for example, str \*= string is true, but example \*= ple is false.

```yaml
  conditions:
    1:
      type: placeholder
      placeholder: '%player_health%'
      rule: '<='
      value: 5
```

## Any <mark style="color:red;">- Premium</mark>

```yaml
  conditions:
    1:
      type: any
      conditions:
        1:
          type: placeholder
          placeholder: '%eco_balance%'
          rule: '>='
          value: 200
        2:
          type: placeholder
          placeholder: '%player_points%'
          rule: '>='
          value: 400
```

## Not <mark style="color:red;">- Premium</mark> <a href="#not" id="not"></a>

```yaml
  conditions:
    1:
      type: not
      conditions:
        1:
          type: placeholder
          placeholder: '%eco_balance%'
          rule: '>='
          value: 200
```

## Equipped Prefix <mark style="color:red;">- Premium</mark>

```yaml
  conditions:
    1:
      type: equipped_prefix
      prefixes:
        - tag1
        - tag2
      require-all: false
```

## Equipped Prefix Amount <mark style="color:red;">- Premium</mark>

```yaml
  conditions:
    1:
      type: equipped_prefix_amount
      amount: 2
```

## Effected Prefix Amount <mark style="color:red;">- Premium</mark>

```yaml
  conditions:
    1:
      type: effected_prefix_amount
      amount: 2
```
