# Currently Supported Syntax

Type|Example|Output|Output Type
---|---|---|---
Any Single Token|`thoth`, `iris`|dialect for tokens (player defined & [builtin](https://github.com/YukkuriC/HexParseMod/blob/main/common/src/main/java/io/yukkuric/hexparse/parsers/str2nbt/ToDialect.java))|`ListIota`
Any Code Fragment|`#aimed`, `#read_circle_corners`|macros defined by player|`ListIota`
Nested List|`[`, `]`|list literals; can be nested|`ListIota`
Non-functional Pattern|`(`, `)`, `\\` or `escape`|`Introspection`, `Retrospection` and `Consideration`|`PatternIota`
Normal Pattern|`get_caster`, `entity_pos/eye`|normal static patterns matched by registration key|`PatternIota`
Great Spell|`lightning` , `brainsweep`|great (per-world) patterns; currently not limited `TODO`|`PatternIota`
Mask Pattern|`mask_--vv--`|`Bookkeeper's Gambit`|`PatternIota`
Number Literal Pattern|`num_1.375`|`Numerical Reflection`|`PatternIota`
Raw Pattern|`_wedsaq`|Patterns with given angle signatures|`PatternIota`
Entity Reference|`entity_<uuidString>`|the `EntityIota` of the entity with certain UUID|`EntityIota`
Caster Reference|`self`, `myself`|the `EntityIota` of the player|`EntityIota`
Number Literal|`114514`|corresponding `DoubleIota`|`DoubleIota`
Vector Literal|`vec`, `vec_5_1_4`, `vec_19_19`|corresponding `Vec3Iota`; unassigned axes will be 0s|`Vec3Iota`
Boolean Literal|`true`, `False`|corresponding `BooleanIota` (case insensitive)|`BooleanIota`
Null Literal|`null`, `NULL`|`NullIota` (case insensitive)|`NullIota`
Const Garbage|`garbage`|`GarbageIota` (case insensitive)|`GarbageIota`
Iota Comments|`comment_meow`|comment which displays as text but won't be executed; same limitation as `String Literal`s|`CommentIota`
Iota Indents|`tab_4`, `tab_8`, `tab`|special comment which contains linebreak and leading spaces of given amount;<br>>will be auto-added if input is multi-line|`CommentIota`
# Plugin Parser Installed

Addon|Type|Example|Output|Output Type
---|---|---|---|---
MoreIotas (1.20)<br>/ Hexal (1.19)|Iota Type|`type_pattern`, `type/iota_moreiotas:string`|iota type with given ID; namespace `hexcasting:` can be omitted|`IotaTypeIota`
MoreIotas (1.20)<br>/ Hexal (1.19)|Entity Type|`type/entity_minecraft:warden`|entity type with given ID; namespace `minecraft:` can be omitted|`EntityTypeIota`
MoreIotas (1.20)<br>/ Hexal (1.19)|Item Type|`type/item_golden_apple`|item type with given ID; namespace `minecraft:` can be omitted|`ItemTypeIota`
MoreIotas (1.20)<br>/ Hexal (1.19)|Block Type|`type/block_amethyst_block`|block type with given ID; namespace `minecraft:` can be omitted|`ItemTypeIota`
MoreIotas|String Literal|`str_foobar`|string with given content; currently not supporting spaces, escaping, non-alphabetical char, etc. `TODO`|`StringIota`
Hexal|Gate Literal|`gate`, `gate_114_514_1919`, `gate_0_1_0_self`, `gate_0_deadbeef-c0de-cafe-babe-114514191981`|`gate` for unbinded gates; with vec3 axes for position-binded ones; with entity UUID or `self` (and/or vec3 axes) for entity-binded ones;<br>**NOTE: Extra cost for making gates**<br>**NOTE2: All parsed gates have negative IDs and may conflict with other parsed ones; make sure to generate your own for security usages**|`GateIota`
Hexal|Mote Literal|`mote_<UUID>_<idx>`, `mote_deadbeef-c0de-cafe-babe-114514191981_0`|the corresponding mote from certain nexus in the world|`MoteIota`
Hexcellular|Property Literal|`prop_imaprop`, `property_ImAPropToo`|property iota with certain name;<br>**NOTE: Extra cost for making properties**<br>**NOTE2: All parsed properties start with an underline and may confilict with other parsed ones; make sure to generate your own for security usages**|`PropertyIota`
