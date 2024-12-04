# HexParse mod

Provides a pair of patterns and a set of commands to convert custom code into (pattern or literal) list iota; requires player to have a focus item in hand.

(Old KubeJS version [HERE](https://github.com/YukkuriC/hex_playground/blob/1.19/server_scripts/Parser.js))

| Sample Code                           | Sample Iota                           |
|---------------------------------------|---------------------------------------|
| ![Sample Code](img/sample%20code.png) | ![Sample Iota](img/sample%20iota.png) |

The
highlight [VSCode extension](https://github.com/YukkuriC/hexParse_scripts/tree/main/.vscode/extensions/hexParse_highlight)
has only basic functions, and needs to be put into `"%USERPROFILE%\.vscode\extensions\hexParse_highlight"` manually.

## Commands added

- `/hexParse <code string> [rename]`: parse imput code into focus item; optional `rename` argument to rename focus item.
- `/hexParse clipboard [rename]`: read client clipboard text and parse into focus; optional `rename` argument to rename
  focus item.
- `/hexParse clipboard_angles [rename]`: same as above, but only accept patterns input with raw angle string like
  `"wedsaq"`.
- `/hexParse read`: read handheld focus item's iota, parse into code and show in chat window; the result will be copied
  when clicked.
- `/hexParse share`: (experimental) same as above but broadcasts iota's raw content and click-copy-able parsed code to
  every player in the server.
- `/hexParse refreshMappings`: reload pattern mappings when plugins updated; should be auto-executed before first usage
  of above commands in each server, and may be ignored.

## Supported expressions

see [this file](SYNTAX.md) for all available symbols.

## New iota: `CommentIota`

`CommentIota` displays string inside, and parses into a null iota (with id: `"hexparse:comment"`) which executes doing
nothing.

## TODOs

* [x] [Commands](common/src/main/java/io/yukkuric/hexparse/commands/TODO.md)
* [x] [Str2Nbt](common/src/main/java/io/yukkuric/hexparse/parsers/str2nbt/TODO.md)
* [x] [Nbt2Str](common/src/main/java/io/yukkuric/hexparse/parsers/nbt2str/TODO.md)

## Future plans

* [x] config to control great spells parsing
* [x] ~~item~~ spell pattern version
* [ ] i18n lang
* [ ] **more complex tokenizer to replace simple regex queues**
* [ ] limited great spell parsing before scroll acquired
* [x] 1.20 port after `HexCasting` publish
