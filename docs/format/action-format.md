# 🎬Action Format

## Available Placeholders

* {world}
* {amount}
* {player\_x}
* {player\_y}
* {player\_z}
* {player\_pitch}
* {player\_yaw}
* {player}

## Add Prefix

```yaml
   actions:
     1:
       type: add_prefix
       prefix: example
```

## Remove Prefix

```yaml
   actions:
     1:
       type: remove_prefix
       prefix: example
```

## Remove All

Remove all equipped prefixes.

```yaml
   actions:
     1:
       type: removeall
```

## Close

Close the opened GUI.

```yaml
   actions:
     1:
       type: close
```

## Sound

Send sound to player.

```yaml
    actions:
      1:
        type: sound
        sound: 'ui.button.click'
        volume: 1
        pitch: 1
```

## Message

Send a message to the player, support color code.

```yaml
    actions:
      1:
        type: message
        message: 'Hello!'
```

## Announcement

Send a message to all online players, support color code.&#x20;

```yaml
    actions:
      1:
        type: announcement
        message: 'Hello!'
```

## Effect

Give players potion effect.

```yaml
    actions:
      1:
        type: effect
        potion: BLINDNESS
        duration: 60
        level: 1
        ambient: true # Optional
        particles: true # Optional
        icon: true # Optional
```

## Teleport

Teleport player to specified location.

```yaml
    actions:
      1:
        type: teleport
        world: LobbyWorld
        x: 100
        y: 30
        z: 300
        pitch: 90 # Optional
        yaw: 0 # Optional
```

## Player Command

Make the player excutes a command.

```yaml
    actions:
      1:
        type: player_command
        command: 'tell Hello!'
```

## Op Command

Make the player excutes a command as OP.

```yaml
    actions:
      1:
        type: op_command
        command: 'tell Hello!'
```

## Console Command

Make the console excutes a command.

```yaml
    actions:
      1:
        type: console_command
        command: 'op {player}'
```

## Spawn vanilla mobs

Spawn vanilla mobs.

```yaml
    actions:
      1:
        type: entity_spawn
        entity: ZOMBIE
        world: LOBBY # Optional
        x: 100.0 # Optional
        y: 2.0 # Optional
        z: -100.0 # Optional
```

## Delay <mark style="color:red;">- Premium</mark>

Make the action run after X ticks.

```yaml
    actions:
      1:
        type: delay
        time: 50
        wait-for-player: true
        actions:
          1:
            type: entity_spawn
            entity: ZOMBIE
```

## Chance <mark style="color:red;">- Premium</mark>

Set the chance the action will be excuted, up to 100. 50 means this action has 50% chance to excute.

```yaml
    actions:
      1:
        type: chance
        rate: 50
        actions:
          1:
            type: entity_spawn
            entity: ZOMBIE
```

## Any <mark style="color:red;">- Premium</mark>

Randomly choose a action to execute.

```yaml
    actions:
      1:
        type: any
        amount: 2
        actions:
          1:
            type: entity_spawn
            entity: ZOMBIE
          2:
            type: entity_spawn
            entity: SKELETON
          3:
            type: entity_spawn
            entity: WITHER
```
