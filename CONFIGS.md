# HexParse Configs

<!-- TOC -->

* [HexParse Configs](#hexparse-configs)
    * [Limited great pattern parsing](#limited-great-pattern-parsing)
        * [Normal Mode (by default): `BY_SCROLL`](#normal-mode-by-default-by_scroll)
        * [Easy Mode (by default before ver.`0.7`): `ALL`](#easy-mode-by-default-before-ver07-all)
        * [Hard Mode: `DISABLED`](#hard-mode-disabled)
    * [Handling unsupported iota types](#handling-unsupported-iota-types)
        * [Keeping reversible NBT (by default): `KEEP_NBT`](#keeping-reversible-nbt-by-default-keep_nbt)
            * [Blacklisted tag for unsafe iota types](#blacklisted-tag-for-unsafe-iota-types)
        * [Displaying NBT String : `SHOW_NBT`](#displaying-nbt-string--show_nbt)
        * [No Extra Display: `SIMPLE`](#no-extra-display-simple)
    * [Handling comments and indents](#handling-comments-and-indents)
        * [CommentParsingMode](#commentparsingmode)
        * [IndentParsingMode](#indentparsingmode)
    * [Other configs](#other-configs)

<!-- TOC -->

## Limited Great Pattern Parsing

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

## Handling Unsupported Iota Types

The config entry `ShowUnknownNBT` controls how it treats iotas that are currently unsupported (not included [HERE](https://github.com/YukkuriC/HexParseMod/blob/main/SYNTAX.md)) when exporting to text.

### Keeping reversible NBT (by default): `KEEP_NBT`

Unknown iota NBT gets serialized and compressed to Base64 string like `nbt_DEADBEEF`, which can be parsed to original iota NBT later.

#### Blacklisted tag for unsafe iota types

Iota types with tag `#hexparse:nbt_parsing_forbidden` won't be imported unless player has OP permission.

> by default it contains `hexcasting:entity`, _no more truename escaping, oops_

### Displaying NBT String : `SHOW_NBT`

Unknown iota NBT is displayed as `UNKNOWN(serialized NBT string)`, letting the player know what's inside.

### No Extra Display: `SIMPLE`

Only output `UNKNOWN` as a placeholder.

## Handling Comments and Indents

These config entries controls how text with comments and line-breaks is imported into game as iota.

### CommentParsingMode

How comments get parsed into iotas:

- `ALL`: including `comment_%s`s and `/* */`s & `//`s
- `MANUAL`(default): only `comment_%s`s
- `DISABLED`: no comments at all

### IndentParsingMode

How indents get parsed into iotas:

- `ALL`(default): coding indents will be auto-converted into `tab_%d`
- `MANUAL`: only `tab_%d`s accepted
- `DISABLED`: no indents at all

## Other Configs

| Entry                | Type   | Description                                                                              |
|----------------------|--------|------------------------------------------------------------------------------------------|
| MaxBlankLineCount    | `int`  | how many continuous blank lines are allowed in parsed spell; excess ones will be ignored |
| AddIndentInsideMacro | `bool` | code indentation add to `tab_N`'s inside nested macros                                   |
| AlwaysShortName      | `bool` | Forced using short ID for patterns even from addons                                      |
| ParserBaseCost       | `int`  | Base cost for each iota (except comments/tabs)                                           |
| FairPlayPropNames    | `bool` | randomize property names based on input string                                           |
| ShowColorfulNested   | `bool` | Whether to colorize nested list (and intro/retros in 1.20)                               |
| SyncDisplayToClient  | `bool` | Whether server sends all `en_us` action names to client on player login                  |
