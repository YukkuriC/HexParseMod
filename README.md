# HexParse mod

JAR-ized `/hexParse` command
in [kubejs version](https://github.com/YukkuriC/hex_playground/blob/main/server_scripts/Parser.js)

Converts custom code into (pattern or literal) list iota; requires player to have a focus item in hand.

| Sample Code                           | Sample Iota                           |
|---------------------------------------|---------------------------------------|
| ![Sample Code](img/sample%20code.png) | ![Sample Iota](img/sample%20iota.png) |

The
highlight [VSCode extension](https://github.com/YukkuriC/hexParse_scripts/tree/main/.vscode/extensions/hexParse_highlight)
is in scratch, and needs to be put into `"%USERPROFILE%\.vscode\extensions\hexParse_highlight"` manually.

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

| Type                       | Example                          | Output                                                                                                                     | Output Type      | Supports<br>`iota -> code` aprsing back |
|----------------------------|----------------------------------|----------------------------------------------------------------------------------------------------------------------------|------------------|-----------------------------------------|
| Nested List                | `[`, `]`                         | list literals; can be nested                                                                                               | `ListIota`       | y                                       |
| Non-functional Pattern     | `(`, `)`, `\\` or `escape`       | `Introspection`, `Retrospection` and `Consideration`                                                                       | `PatternIota`    | y                                       |
| Normal Pattern             | `get_caster`, `entity_pos/eye`   | normal static patterns matched by registration key                                                                         | `PatternIota`    | y                                       |
| Great Spell                | `lightning` , `brainsweep`       | great (per-world) patterns; currently not limited `TODO`                                                                   | `PatternIota`    | y                                       |
| Mask Pattern               | `mask_--vv--`                    | `Bookkeeper's Gambit`                                                                                                      | `PatternIota`    | y                                       |
| Number Literal Pattern     | `num_1.375`                      | `Numerical Reflection`                                                                                                     | `PatternIota`    | y                                       |
| Raw Pattern                | `_wedsaq`                        | Patterns with given angle signatures                                                                                       | `PatternIota`    | y                                       |
| Caster Reference           | `self`, `myself`                 | the `EntityIota` of the player                                                                                             | `EntityIota`     | **NO**                                  |
| Number Literal             | `114514`                         | corresponding `DoubleIota`                                                                                                 | `DoubleIota`     | y                                       |
| Vector Literal             | `vec`, `vec_5_1_4`, `vec_19_19`  | corresponding `Vec3Iota`; unassigned axes will be 0s                                                                       | `Vec3Iota`       | y                                       |
| (Hexal) Iota Type          | `type_pattern`, `type/iota_vec3` | iota type with given ID                                                                                                    | `IotaTypeIota`   | y                                       |
| (Hexal) Entity Type        | `type/entity_warden`             | entity type with given ID                                                                                                  | `EntityTypeIota` | y                                       |
| (MoreIotas) String Literal | `str_foobar`                     | string with given content; currently not supporting spaces, escaping, non-alphabetical char, etc. `TODO`                   | `StringIota`     | y                                       |
| Iota Comments              | `comment_meow`                   | comment which displays as text but won't be executed; same limitation as `String Literal`s                                 | `CommentIota`    | y                                       |
| Iota Indents               | `tab_4`, `tab_8`, `tab`          | special comment which contains linebreak and leading spaces of given amount;<br>>will be auto-added if input is multi-line | `CommentIota`    | y                                       |

## New iota: `CommentIota`

`CommentIota` displays string inside, and parses into a null iota (with id: `"hexparse:comment"`) which executes doing
nothing.

## TODOs

* [x] [Commands](common/src/main/java/io/yukkuric/hexparse/commands/TODO.md)
* [x] [Str2Nbt](common/src/main/java/io/yukkuric/hexparse/parsers/str2nbt/TODO.md)
* [x] [Nbt2Str](common/src/main/java/io/yukkuric/hexparse/parsers/nbt2str/TODO.md)

## Future plans

* [ ] config to control great spells parsing
* [ ] item version
* [ ] i18n lang
* [ ] **more complex tokenizer to replace simple regex queues**
* [ ] limited great spell parsing
* [ ] 1.20 port after `HexCasting` publish