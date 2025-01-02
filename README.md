# HexParse mod

Provides a pair of patterns and a set of commands to convert custom code into (pattern or literal) list iota; requires
player to have a focus item in hand.

(Old KubeJS version [HERE](https://github.com/YukkuriC/hex_playground/blob/1.19/server_scripts/Parser.js))

| Example Code                                                                           | Example Iota                                                                           |
|----------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------|
| ![Sample Code](https://github.com/YukkuriC/HexParseMod/raw/main/img/sample%20code.png) | ![Sample Iota](https://github.com/YukkuriC/HexParseMod/raw/main/img/sample%20iota.png) |

The
highlight [VSCode extension](https://github.com/YukkuriC/hexParse_scripts/tree/main/.vscode/extensions/hexParse_highlight)
has only basic functions, and needs to be put into `"%USERPROFILE%\.vscode\extensions\hexParse_highlight"` manually.

## Limited great pattern parsing

When failing to parse restricted great spells **from code to iota**, the parser leaves a placeholder comment in-place,
which can be read as original input later.

| Example                                                                                          |
|--------------------------------------------------------------------------------------------------|
| ![Code With Missing](https://github.com/YukkuriC/HexParseMod/raw/main/img/code_with_unknown.png) |
| ![Iota With Missing](https://github.com/YukkuriC/HexParseMod/raw/main/img/iota_with_unknown.png) |

The config entry `ParseGreatSpells` determines the mode this mod deals with great patterns.  
Parsing iota with great patterns to code is not limited.

### Normal Mode (by default): `BY_SCROLL`

All great patterns are restricted at first, and have to be unlocked by a `Learn Great Patterns` pattern after acquiring
certain items containing great patterns.

### Easy Mode (by default before ver.`0.7`): `ALL`

Parsing is not limited, and great patterns can be used freely regardless of world exploration and looting progress.

### Hard Mode: `DISABLED`

Parsing is not limited.

## Other configs

| Entry                   | Type   | Description                                            |
|-------------------------|--------|--------------------------------------------------------|
| parseCommentsAndIndents | `bool` | Whether to enable comment iotas (as comments and tabs) |
| parserBaseCost          | `int`  | Base cost for each iota (except comments/tabs)         |

## Commands added

- `/hexParse <code string> [rename]`: parse input code into focus item; optional `rename` argument to rename focus item.
- `/hexParse clipboard [rename]`: read client clipboard text and parse into focus; optional `rename` argument to rename
  focus item.
- `/hexParse clipboard_angles [rename]`: same as above, but only accept patterns input with raw angle string like
  `"wedsaq"`.
- `/hexParse read`: read handheld focus item's iota, parse into code and show in chat window; the result will be copied
  when clicked.
- `/hexParse donate [amount]`: donate custom amount of media to the nature. Pay if you feel guilty using this mod ::)
- `/hexParse share`: (experimental) same as above but broadcasts iota's raw content and click-copy-able parsed code to
  every player in the server.

### OP-only commands

- `/hexParse refreshMappings`: reload pattern mappings when plugins updated; should be auto-executed before first usage
  of above commands in each server, and this command can be ignored in normal cases.
- `/hexParse unlock_great (unlockAll|lockAll|unlock <pattern id>|lock <pattern id>)`: controls great pattern unlocking
  process of current
  world by locking/unlocking all at once, or a single great pattern each execution.

## Patterns added

* `code2focus`: Equivalent to `/hexParse clipboard`.
* `focus2code`: Equivalent to `/hexParse read`.
* `remove_comments`: Clears comment iotas from a (nested) list input.
* `learn_patterns`: Read handheld items and learns great pattern(s) inside.

*Introduction also written in `HexParse Patterns` section inside the book.*

## Supported expressions

see [this file](https://github.com/YukkuriC/HexParseMod/blob/main/SYNTAX.md) for all available symbols.

## New iota: `CommentIota`

`CommentIota` displays string inside, and parses into a null iota (with id: `"hexparse:comment"`) which executes doing
nothing.  
Comment iotas includes text comments, line-breaks & indents, and unknown great spell placeholders.  
When player holds `Shift` key, all comments will be hidden.

## Future plans

* **more complex tokenizer to replace simple regex queues**
* **legal** number pattern generator
