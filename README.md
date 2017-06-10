# BukkitJs

## Installation

- Download and drop into plugins directory
- Done

## Usage

Examples of how to use this plugin can be found in `examples/`

### Server interface documentation

This section documents the methods available through the server object

| Goal                    | Method                                                                                                                                                 |
|-------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|
| Register command        | void command(String name, String description, String usage, (JS Array of Strings) aliases, function(CommandSender alias, String alias, String[] args)) |
| Register event listener | Listener on(String eventClass, function(event))                                                                                                        |
| Unregister listener     | void unregisterListener(Listener listener)                                                                                                             |
| Run task delayed        | BukkitTask delay(boolean async, long by, function())                                                                                                   |
| Run task repeatedly     | BukkitTask repeat(boolean async, long delay, long period, function())                                                                                  |
