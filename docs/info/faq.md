# ❓FAQ

## Q: Why after I use the tag, the chat spam a message?

A: People often ask me this question, so I have set up a separate page, and I am angry about it. I believe that the person asking this question does not respect my work. The reason is simple: they have never browsed the configuration file of the plugin, nor have they read this message, nor have they browsed the plugin wiki. The answer is that you have set a circle action, which causes the actions within the circle action to be repeatedly executed after the tag is activated. If you don't want this, then simply remove `circle-actions` option at the example prefix config.

## Q: Why conditions of prefix seems do not work?

A: By default, OP will bypass condition check, if you want to test condition check, please deop your self first.

## Q: Do you have tag shop feature?&#x20;

A: No. 90% of servers have shop plugins installed, and you can do the same thing with shop plugins, which is completely redundant for MythicPrefixes to provide. You can condition a `permission` condition in the prefix conditions, and then use a permission management plugin like **LuckPerms** to give the player the permissions you set for the prefix conditions through the command.
