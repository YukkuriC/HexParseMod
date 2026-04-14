# HexParse mod

[![Curseforge](https://badges.moddingx.org/curseforge/versions/1148731) ![CurseForge](https://badges.moddingx.org/curseforge/downloads/1148731)](https://www.curseforge.com/minecraft/mc-mods/hexparse)  
[![Modrinth](https://badges.moddingx.org/modrinth/versions/WjFyIzFj) ![Modrinth](https://badges.moddingx.org/modrinth/downloads/WjFyIzFj)](https://modrinth.com/mod/hexparse)

Provides a pair of patterns and a set of commands to convert custom code into (pattern or literal) list iota; requires
player to have a focus item in hand.

[<img src="https://github.com/SamsTheNerd/HexGloop/blob/73ea39b3becd/externalassets/hexdoc-badgecozy.svg?raw=true" alt="A badge for hexdoc in the style of Devins Badges" width=180>](https://yukkuric.github.io/HexParseMod)
[<img src="https://github.com/SamsTheNerd/HexGloop/blob/73ea39b3becd/externalassets/addon-badge-cozy.svg?raw=true" alt="A badge for addons.hexxy.media in the style of Devins Badges" width=160>](https://addons.hexxy.media)

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
    * [Commands Added](#commands-added)
        * [Reading & Writing](#reading--writing)
        * [Configs](#configs)
        * [Misc. & Helpers](#misc--helpers)
        * [OP-Only Commands](#op-only-commands)
    * [Patterns Added](#patterns-added)
    * [Misc. Features](#misc-features)
    * [New Iota: `CommentIota`](#new-iota-commentiota)
    * [`HexParseAPI`](#hexparseapi)
    * [External Documents](#external-documents)
        * [Supported Expressions](#supported-expressions)
        * [Available Configs](#available-configs)

<!-- TOC -->

## Supported IO Item Types

- Focuses
- Spell Books
- (1.20) Thought Knots
- recognized by item classes, able to add more via `HexParseAPI` or `IOMethod`

## Commands Added

### Reading & Writing

- `/hexParse <code string> [rename]`: parse input code into supported held item; optional `rename` argument to rename the item.
- `/hexParse read`: read handheld item's iota, parse into code and show in chat window; the result will be copied
  when clicked.
- `/hexParse read_signatures`: same as `read`, but ignores all known pattern IDs and outputs all as angle signatures.
- `/hexParse clipboard [rename]`: read client clipboard text and parse into supported held item; optional `rename` argument to rename
  the item.
- `/hexParse clipboard_angles [rename]`: same as `clipboard`, but only accept patterns input with raw angle string like
  `"wedsaq"`.
- `/hexParse clipboard_hexpattern [rename]`: (experimental) same as `clipboard`, but accepts `.hexpattern` format; _warning: only partial features supported_.
- `/hexParse share`: (experimental) same as `read` but broadcasts iota's raw content and click-copy-able parsed code to
  every player in the server.
- `/hexParse read_hexbug`: same as `read` but translates the result to the format used by discord HexBug's `/patterns hex` command. _note:
  non-pattern constants and some old registry names still need to be handled manually_
- `/hexParse mind_stack ...`: read/write iota from player's mind (staff casting VM)
    - `... peek`: read the last iota inside mind stack; gets `null` if stack is empty
    - `... push <code>`: parse code and push into mind stack
    - `... push_clipboard`: same as above, but code comes from clipboard
- `/hexParse property ...`: (`Hexcellular` interop) get/set data for PropertyIota; used property names all force-added leading `_` for security reason
    - `... read <propName>`: read and parse from certain property
    - `... write <propName> <code>`: write code into certain property
    - `... clipboard <propName>`: same as above, but code comes from clipboard

### Configs

- `/hexParse (macro/dialect) ...`: edit client-saved code dialects (1-on-1 mapping, not starting with `#`) and macros (
  mapped to code segments, starting with `#`)
    - `... list`: list all saved macros/dialects; there exist several predefined macros from ~~the nature~~
    - `... define <key> <value>`: define a macro/dialect mapping; could be fresh-new or overriding existed one.
    - `macro define_clipboard <key>`: same as above, but only for macros, and reads player's clipboard
    - `... remove <key>`: remove mapping entry with given key (if exists)
- `/hexParse conflict`: conflict resolver for multiple patterns with same short name (ID path)
    - _only enables in physical client (singleplayer, local multiplayer) or with OP permission_
    - `...` or `... list`: list all short names pointed by multiple long IDs
    - `... list <short_name>`: list all conflicting IDs under certain short name
    - `... set <short_name> <long_ID>`: redirect certain short name to input pattern ID

### Misc. & Helpers

- `/hexParse donate [amount]`: donate custom amount of media to the nature. Pay if you feel guilty using this mod ::)
- `/hexParse learn_great`: Read handheld items and learns great pattern(s) inside.
- `/hexParse lehmer [...nums]`: calculate lehmer code for given permutation (from ascending, e.g. `0 1 2 3 4`); input
  should be separated with space; the result number can be used for **Swindler's Gambit**.

### OP-Only Commands

- `/hexParse unlock_great (unlockAll|lockAll|unlock <pattern id>|lock <pattern id>)`: controls great pattern unlocking
  process of current
  world by locking/unlocking all at once, or a single great pattern each execution.

## Patterns Added

* `comment_switcher`: Transforms input Comment Iota into String Iota, or everything else into Comment Iota.
* `code2focus`: Equivalent to `/hexParse clipboard` (now not only focuses).
* `focus2code`: Equivalent to `/hexParse read`.
* `remove_comments`: Clears comment iotas from a (nested) list input.
* `learn_patterns`: Read handheld items and learns great pattern(s) inside.
* `create_linebreak`: Adds a line-break comment iota with space-indents of given number to the stack.
* `donate`: Equivalent to `/hexParse donate`.
* (great spell) `compile`: takes in a MoreIotas string iota, and parses it into a code list.

*Introduction also written in `HexParse Patterns` section inside the book.*

## Misc. Features

* At each pattern's page, press `Shift` to display the pattern's registry ID

## New Iota: `CommentIota`

`CommentIota` displays string inside, and parses into a null iota (with id: `"hexparse:comment"`) which executes doing
nothing.  
Comment iotas includes text comments, line-breaks & indents, and unknown great spell placeholders.  
When player holds `Shift` key, all comments will be hidden.

## `HexParseAPI`

- Location: `io.yukkuric.hexparse.api.HexParseAPI`
- Contents:
    - `AddForthParser(p: IStr2Nbt)`
    - `AddBackParser(p: INbt2Str)`
    - `AddSpecialHandlerBackParser(id: String, func: (Action, CompoundTag, ServerPlayer) -> String)`
    - `CreateItemIOMethod(cls: Class<*>, writer: ((ItemStack, CompoundTag) -> Unit)?, reader: ((ItemStack) -> CompoundTag?)?, priority: Int = 0, validator: ((ItemStack, Boolean) -> Boolean)?`
      )`

## External Documents

### Supported Expressions

see [this file](https://github.com/YukkuriC/HexParseMod/blob/main/SYNTAX.md) for all available symbols.

### Available Configs

see [this file](https://github.com/YukkuriC/HexParseMod/blob/main/CONFIGS.md) for all available configs.