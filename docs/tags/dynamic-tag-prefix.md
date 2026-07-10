# 🚩Dynamic Tag/Prefix

{% hint style="warning" %}
The **dynamic tag/prefix** feature is not currently introduced as a core function of MythicPrefixes and is still in the early testing phase. It may be officially released in future versions or potentially removed if it contains irreparable critical issues.&#x20;

Dynamic Prefix is now available at **2.0.0 or later** version.
{% endhint %}

Dynamic Prefix lets players submit a custom display value for a prefix and lets administrators review it before it becomes active. It is designed for custom personal labels, and other player-entered prefix values that should be moderated first.

## Basic Configuration

Add this option to a prefix configuration file:

```yaml
dynamic-prefix: true
```

After enabling it, the prefix display value will prefer the player's approved custom value. If the player has no approved value, the plugin displays the language key `dynamic-prefix.none`.

Global validation options are in `config.yml`:

```yaml
dynamic-prefix:
  min-length: 1
  max-length: 32
  sensitive-words: []
```

* `min-length`: minimum submitted value length.
* `max-length`: maximum submitted value length.
* `sensitive-words`: submissions containing any listed word are rejected before review.

## Player Flow

Java players right click a dynamic prefix item in the prefix GUI to edit it.

Bedrock players edit it through FormUI.&#x20;

## Review&#x20;

Reviewers need:

```yml
mythicprefixes.dynamicprefix.review
```

Players with `mythicprefixes.admin` can also receive notices and use tab completion.

Commands:

```yaml
/mythicprefixes dynamicprefix list
/mythicprefixes dynamicprefix approve <uuid> <prefix>
/mythicprefixes dynamicprefix deny <uuid> <prefix>
```

`approve` and `deny` support tab completion for player UUIDs and prefix IDs.

## GUI Placeholders

Dynamic prefix `display-item` sections can use:

```
{display-value}
{pending-value}
```

* `{display-value}`: approved value only. If missing, shows `dynamic-prefix.none`.
* `{pending-value}`: pending review value only. If missing, shows `dynamic-prefix.none`.

Example:

```yaml
lore:
  - '{lang:display.display-value}: {display-value}'
  - '{lang:display.dynamic-pending}: {pending-value}'
```

Default example file:

```
prefixes/dynamic_example.yml
```

## PlaceholderAPI

Approved values still use normal prefix placeholders, for example:

```
%mythicprefixes_prefix_dynamic_example_chat%
```

Pending values use:

```
%mythicprefixes_pending_<prefixId>%
```

Example:

```
%mythicprefixes_pending_dynamic_example%
```

If there is no pending value, it returns the language value `dynamic-prefix.none`.
