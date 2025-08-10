# HexParse mod

[![Curseforge](https://badges.moddingx.org/curseforge/versions/1148731) ![CurseForge](https://badges.moddingx.org/curseforge/downloads/1148731)](https://www.curseforge.com/minecraft/mc-mods/hexparse)  
[![Modrinth](https://badges.moddingx.org/modrinth/versions/WjFyIzFj) ![Modrinth](https://badges.moddingx.org/modrinth/downloads/WjFyIzFj)](https://modrinth.com/mod/hexparse)

Provides a pair of patterns and a set of commands to convert custom code into (pattern or literal) list iota; requires
player to have a focus item in hand.

(Old KubeJS version [HERE](https://github.com/YukkuriC/hex_playground/blob/1.19/server_scripts/Parser.js))

| Example Code                                                                    | Example Iota                                                                    | Example Iota (Colorful Nested)                                                                        |
|---------------------------------------------------------------------------------|---------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------|
| ![Code](https://github.com/YukkuriC/HexParseMod/raw/main/img/sample%20code.png) | ![Iota](https://github.com/YukkuriC/HexParseMod/raw/main/img/sample%20iota.png) | ![Iota (Colorful)](https://github.com/YukkuriC/HexParseMod/raw/main/img/colorful%20nested%20iota.png) |

_The
highlight [VSCode extension](https://github.com/YukkuriC/hexParse_scripts/tree/main/.vscode/extensions/hexParse_highlight)
has only basic functions, and needs to be put into `"%USERPROFILE%\.vscode\extensions\hexParse_highlight"` manually._

<!-- TOC -->

* [HexParse mod](#hexparse-mod)
    * [Supported IO Item Types](#supported-io-item-types)
    * [Commands added](#commands-added)
        * [OP-only commands](#op-only-commands)
    * [Patterns added](#patterns-added)
    * [Supported expressions](#supported-expressions)
    * [Misc. Features](#misc-features)
    * [Available Configs](#available-configs)
        * [Limited great pattern parsing](#limited-great-pattern-parsing)
            * [Normal Mode (by default): `BY_SCROLL`](#normal-mode-by-default-by_scroll)
            * [Easy Mode (by default before ver.`0.7`): `ALL`](#easy-mode-by-default-before-ver07-all)
            * [Hard Mode: `DISABLED`](#hard-mode-disabled)
        * [Other configs](#other-configs)
    * [New iota: `CommentIota`](#new-iota-commentiota)
    * [Future plans](#future-plans)

<!-- TOC -->

## Supported IO Item Types

- Focuses
- Spell Books
- (1.20) Thought Knots

## Commands added

- `/hexParse <code string> [rename]`: parse input code into supported held item; optional `rename` argument to rename the item.
- `/hexParse clipboard [rename]`: read client clipboard text and parse into supported held item; optional `rename` argument to rename
  the item.
- `/hexParse clipboard_angles [rename]`: same as above, but only accept patterns input with raw angle string like
  `"wedsaq"`.
- `/hexParse (macro/dialect) ...`: edit client-saved code dialects (1-on-1 mapping, not starting with `#`) and macros (
  mapped to code segments, starting with `#`)
    - `... list`: list all saved macros/dialects.
    - `... define <key> <value>`: define a macro/dialect mapping; could be fresh-new or overriding existed one.
    - `macro define_clipboard <key>`: same as above, but only for macros, and reads player's clipboard
    - `... remove <key>`: remove mapping entry with given key (if exists)
- `/hexParse read`: read handheld item's iota, parse into code and show in chat window; the result will be copied
  when clicked.
- `/hexParse donate [amount]`: donate custom amount of media to the nature. Pay if you feel guilty using this mod ::)
- `/hexParse lehmer [...nums]`: calculate lehmer code for given permutation (from ascending, e.g. `0 1 2 3 4`); input
  should be separated with space.
- `/hexParse share`: (experimental) same as `read` but broadcasts iota's raw content and click-copy-able parsed code to
  every player in the server.
- `/hexParse read_hexbug`: same as `read` but translates the result to the format used by discord HexBug's `/patterns hex` command. _note:
  non-pattern constants and some old registry names still need to be handled manually_
- `/hexParse mind_stack ...`: read/write iota from player's mind (staff casting VM)
    - `... peek`: read the last iota inside mind stack; gets `null` if stack is empty
    - `... push <code>`: parse code and push into mind stack
    - `... push_clipboard`: same as above, but code comes from clipboard
- `/hexParse property ...`: (Hexcellular interop) get/set data for PropertyIota; used property names all force-added leading `_` for security reason
    - `... read <propName>`: read and parse from certain property
    - `... write <propName> <code>`: write code into certain property
    - `... clipboard <propName>`: same as above, but code comes from clipboard

### OP-only commands

- `/hexParse unlock_great (unlockAll|lockAll|unlock <pattern id>|lock <pattern id>)`: controls great pattern unlocking
  process of current
  world by locking/unlocking all at once, or a single great pattern each execution.

## Patterns added

* `code2focus`: Equivalent to `/hexParse clipboard` (now not only focuses).
* `focus2code`: Equivalent to `/hexParse read`.
* `remove_comments`: Clears comment iotas from a (nested) list input.
* `learn_patterns`: Read handheld items and learns great pattern(s) inside.
* (great spell) `compile`: takes in a MoreIotas string iota, and parses it into a code list.

*Introduction also written in `HexParse Patterns` section inside the book.*

## Supported expressions

see [this file](https://github.com/YukkuriC/HexParseMod/blob/main/SYNTAX.md) for all available symbols.

## Misc. Features

* At each pattern's page, press `Shift` to display the pattern's registry ID

## Available Configs

### Limited great pattern parsing

When failing to parse restricted great spells **from code to iota**, the parser leaves a placeholder comment in-place,
which can be read as original input later.

| Example                                                                                          |
|--------------------------------------------------------------------------------------------------|
| ![Code With Missing](https://github.com/YukkuriC/HexParseMod/raw/main/img/code_with_unknown.png) |
| ![Iota With Missing](https://github.com/YukkuriC/HexParseMod/raw/main/img/iota_with_unknown.png) |

The config entry `ParseGreatSpells` determines the mode this mod deals with great patterns.  
Parsing iota with great patterns to code is not limited.

#### Normal Mode (by default): `BY_SCROLL`

All great patterns are restricted at first, and have to be unlocked by a `Learn Great Patterns` pattern after acquiring
certain items containing great patterns.

#### Easy Mode (by default before ver.`0.7`): `ALL`

Parsing is not limited, and great patterns can be used freely regardless of world exploration and looting progress.

#### Hard Mode: `DISABLED`

Parsing is not limited.

### Other configs

| Entry              | Type   | Description                                                                                                                                                                      |
|--------------------|--------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| CommentParsingMode | `enum` | how comments get parsed into iotas<br>`ALL`: including `comment_%s`s and `/* */`s & `//`s;<br>`MANUAL`(default): only `comment_%s`s;<br>`DISABLED`: no comments at all           |
| IndentParsingMode  | `enum` | how indents get parsed into iotas<br>`ALL`(default): coding indents will be auto-converted into `tab_%d`;<br>`MANUAL`: only `tab_%d`s accepted;<br>`DISABLED`: no indents at all |
| MaxBlankLineCount  | `int`  | how many continuous blank lines are allowed in parsed spell; excess ones will be ignored                                                                                         |
| ParserBaseCost     | `int`  | Base cost for each iota (except comments/tabs)                                                                                                                                   |
| ShowColorfulNested | `bool` | Whether to colorize nested list (and intro/retros in 1.20)                                                                                                                       |

## New iota: `CommentIota`

`CommentIota` displays string inside, and parses into a null iota (with id: `"hexparse:comment"`) which executes doing
nothing.  
Comment iotas includes text comments, line-breaks & indents, and unknown great spell placeholders.  
When player holds `Shift` key, all comments will be hidden.

## Future plans

* **more complex tokenizer to replace simple regex queues**
* **legal** number pattern generator
