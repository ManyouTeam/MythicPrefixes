# 👥Tag Group - Premium

## Create a new tag group

Open any prefix config at `prefixes` folder, you will find below content:

```yaml
# Premium version only
groups:
  - chat
  - example
```

The `groups` option determines which groups does this tag belong to. You don't need do anything to create new tag group, you just need put a randomly selected IDs here, the plugin will automatically classify them for you!

## What this feature can do?

* You can use command `/prefix opengui <groupID>` to open a GUI which only includes the tag of the specified group. For more info, please view [Commands](../info/commands-and-permissions.md) page.
* You can use `display-prefixes.group` option in display placeholder configs to determine which tag will display in this display placeholder. For more info, please view [Display placeholder](display-placeholders.md) page.
* You can determine max prefixes amount by group in `config.yml` file. For more info, please view [Maximium Tag Limit](maximum-tag-limit.md) page.
