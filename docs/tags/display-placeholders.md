# 🖥️Display Placeholders

## Create a new display placeholder

Open `/display_placeholders/` folder in plugin folder, then you will find `chat.yml` file, this is an example file for display placeholder config, if you want to create new display placeholder, please copy it and rename it to display placeholder ID you want to use.

An example file is here:

```yaml
start-symbol: '&f['
split-symbol: '&f, '
end-symbol: '&f]'
parse-color: true
display-prefixes:
  mode: WHITE
  prefixes: 
    - tag1
    - tag2
  groups:
    - example
display-amount: 2
empty-display: ''
default-prefixes:
 - 'default'
always-display-default-prefixes: false
```

This is an example for display placeholder, and the file name (which means `chat` ) is it's ID. You can create unlimited display placeholder by this format.

* start-symbol: The first content of the placeholder.
* split-symbol: The interval content between each tag.
* end-symbol: The last content of the placeholder.
* display-prefixes:&#x20;
  * mode: Support value: **BLACK** and **WHITE**. (Free version only supports **BLACK**)
  * prefixes: Which tags will be displayed in this placeholder.
  * groups: Which tags of the corresponding group displayed in the placeholder. <mark style="color:red;">(Premium)</mark>
* display-amount: The max amount of the prefix will display, set to -1 means unlimited.
* parse-color: Some plugins may already support colors in the MiniMessage format. If that's the case, you can consider disabling this option so that MythicPrefixes won't parse color codes that exist in the placeholder.
* empty-display: The text will display if there is no tag to display. This will replace the content provided by `start-symbol` and `end-symbol` option.
* default-prefixes: If there is no tag to display in this placeholder, we will use the default prefixes instead, we will find the first tag that the player can use in order from top to bottom. It will only display and will not execute any actions or effects. <mark style="color:red;">(Premium)</mark>
* always-display-default-prefixes: If set to true, even if the player equips any tag, we will still auto equip the default tag that set in `default-prefixes` option. <mark style="color:red;">(Premium)</mark>

## Use display placeholder

You can use display placeholder by using PlaceholderAPI in any other plugins support PlaceholderAPI, for more info, please view [this page](../info/compatibility.md).
