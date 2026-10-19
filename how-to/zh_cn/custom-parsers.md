# 如何注册自定义解析器

**简体中文** [English](../en_us/custom-parsers.md)

> 生成于蓝色大肥鱼-v4.1-Flash

## 概述

HexParse 提供了双向解析管线：**正向解析**（字符串 → NBT）和**反向解析**（NBT → 字符串）。通过 `HexParseAPI` 的 `AddForthParser` 和 `AddBackParser`，你可以注册自定义解析器来扩展 HexParse 支持的 Iota 类型或语法格式。

解析器按注册顺序依次匹配——第一个 `match` 返回 `true` 的解析器将处理该节点。

## 关键接口

### IStr2Nbt — 正向解析器（字符串 → NBT）

```java
public interface IStr2Nbt {
    boolean match(String node);
    CompoundTag parse(String node);
    default boolean ignored() { return false; }
    default int getCost() { return HexParseConfig.parserBaseCost(); }
}
```

| 方法 | 说明 |
|------|------|
| `match(String node)` | 判断输入字符串片段是否可被此解析器处理 |
| `parse(String node)` | 将匹配的字符串解析为 `CompoundTag`（Iota 的 NBT 序列化形式） |
| `ignored()` | 若返回 `true`，解析结果将被丢弃（如纯注释/缩进） |
| `getCost()` | 返回解析此节点的媒质消耗，默认取配置项 `parserBaseCost` |

### INbt2Str — 反向解析器（NBT → 字符串）

```java
public interface INbt2Str extends IConfigNumReceiver, IMetaCollector {
    boolean match(CompoundTag node);
    String parse(CompoundTag node);
    default boolean isType(CompoundTag node, String type) { ... }
    default String displayMinimal(Double raw) { ... }
    default String parseAndCollect(CompoundTag node) { ... }
}
```

| 方法 | 说明 |
|------|------|
| `match(CompoundTag node)` | 判断 NBT 节点是否可被此解析器处理 |
| `parse(CompoundTag node)` | 将匹配的 NBT 节点转换为字符串表示 |
| `isType(CompoundTag node, String type)` | 辅助方法，检查节点的 `type` 字段是否匹配 |
| `displayMinimal(Double raw)` | 辅助方法，将浮点数格式化为最简表示（去除尾部零） |
| `parseAndCollect(CompoundTag node)` | 调用 `parse`，并在启用附属模组元数据收集时记录该 Iota 的命名空间；`ParserMain` 实际调用的是此方法 |

### IMetaCollector — 附属模组命名空间收集

`INbt2Str` 继承了 `IMetaCollector`。默认的 `getNamespace` 读取节点的 `type` 字段并返回其命名空间；注释 Iota 返回 `null`，因此不会出现在 `// Requires:` 头部中。仅当命名空间无法从 Iota 类型推导时才需要覆写它。

### IPlayerBinder — 玩家绑定接口

如果你的解析器需要访问玩家信息（如实体引用、权限检查），可以实现此接口：

```java
public interface IPlayerBinder {
    void BindPlayer(ServerPlayer p);
}
```

在每个解析入口（`ParseCode` / `ParseIotaNbt`）处，`ParserMain` 会自动调用所有实现了 `IPlayerBinder` 的解析器的 `BindPlayer` 方法，将当前施法玩家传入。

### IConfigNumReceiver — 配置编号接口

`INbt2Str` 继承了此接口，用于接收解析配置位掩码：

```java
public interface IConfigNumReceiver {
    default void receiveConfigNum(int configNum) {}
    default boolean hasConfigNum(int mine, int comparer) {
        return (mine & comparer) == comparer;
    }
}
```

## API 方法

### AddForthParser

```kotlin
HexParseAPI.AddForthParser(p: IStr2Nbt)
HexParseAPI.AddForthParser(p: IStr2Nbt, id: String)
```

注册一个正向解析器。解析器被追加到解析器列表末尾，在所有内置解析器之后尝试匹配。

带 `id` 的重载保证同一 ID 只保留一个解析器：用已存在的 ID 再次注册时，会先把旧解析器从列表中移除，再追加新的。适用于可能重复注册的场景（如资源重载）。

### AddBackParser

```kotlin
HexParseAPI.AddBackParser(p: INbt2Str)
HexParseAPI.AddBackParser(p: INbt2Str, id: String)
```

注册一个反向解析器，带 `id` 的重载与 `AddForthParser` 具有相同的替换行为。

### RemoveForthParser / RemoveBackParser

```kotlin
HexParseAPI.RemoveForthParser(p: IStr2Nbt)
HexParseAPI.RemoveBackParser(p: INbt2Str)
```

从解析器列表中移除指定的解析器实例。移除依据实例本身，而非 ID。

## 实战示例

### 示例 1：注册一个简单的正向解析器

以下示例创建一个解析器，将 `mytype_` 前缀的字符串解析为自定义 Iota NBT：

```kotlin
import io.yukkuric.hexparse.api.HexParseAPI
import io.yukkuric.hexparse.parsers.str2nbt.BaseConstParser
import net.minecraft.nbt.CompoundTag

class MyTypeParser : BaseConstParser.Prefix("mytype_") {
    override fun parse(node: String): CompoundTag {
        val value = node.substring(8)
        val tag = CompoundTag()
        tag.putString("type", "mymod:my_type")
        tag.putString("data", value)
        return tag
    }
}

HexParseAPI.AddForthParser(MyTypeParser())
```

`BaseConstParser.Prefix` 是内置的便捷基类，只需提供前缀字符串即可自动实现 `match` 方法。

### 示例 2：注册反向解析器

```kotlin
import io.yukkuric.hexparse.api.HexParseAPI
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str
import net.minecraft.nbt.CompoundTag

class MyTypeBackParser : INbt2Str {
    companion object {
        const val TYPE_ID = "mymod:my_type"
    }

    override fun match(node: CompoundTag): Boolean {
        return isType(node, TYPE_ID)
    }

    override fun parse(node: CompoundTag): String {
        val data = node.getString("data")
        return "mytype_$data"
    }
}

HexParseAPI.AddBackParser(MyTypeBackParser())
```

### 示例 3：注册忽略型解析器

某些解析器产生的 Iota 在实际施法中不产生效果（如注释），可以通过 `ignored()` 返回 `true` 来跳过：

```kotlin
import io.yukkuric.hexparse.api.HexParseAPI
import io.yukkuric.hexparse.parsers.str2nbt.IStr2Nbt
import net.minecraft.nbt.CompoundTag

class MyIgnoredParser : IStr2Nbt {
    override fun match(node: String): Boolean = node.startsWith("ignore_")

    override fun parse(node: String): CompoundTag {
        // 返回值无关紧要，因为 ignored() 返回 true
        return CompoundTag()
    }

    override fun ignored(): Boolean = true
}

HexParseAPI.AddForthParser(MyIgnoredParser())
```

### 示例 4：自定义解析消耗

```kotlin
import io.yukkuric.hexparse.api.HexParseAPI
import io.yukkuric.hexparse.parsers.str2nbt.BaseConstParser
import net.minecraft.nbt.CompoundTag

class MyExpensiveParser : BaseConstParser.Prefix("expensive_") {
    override fun parse(node: String): CompoundTag {
        // 高级解析逻辑...
        return CompoundTag()
    }

    override fun getCost(): Int {
        return 1000  // 自定义媒质消耗
    }
}

HexParseAPI.AddForthParser(MyExpensiveParser())
```

## 内置解析器参考

HexParse 内置了以下正向解析器（按匹配顺序）：

| 解析器 | 前缀/匹配规则 | 说明 |
|--------|---------------|------|
| `ToPattern.META` | 元操作名（`(`, `)`, `\`, `del` 等） | 元操作图案 |
| `ToMiscConst` | `self`/`myself`, `true`, `false`, `null`, `garbage` | 常量 |
| `ToPattern.NORMAL` | 图案名 | 普通图案 |
| `ToPattern.GREAT` | 卓越法术名 | 卓越图案（已解锁的正常解析，未解锁的变为注释占位） |
| `Comment.Indent` | `tab` / `indent` | 缩进 |
| `Comment`（`TO_COMMENT`） | `comment_` | 手动注释 |
| `Comment`（`TO_SCOMMENT`） | `c"..."` | 单行字符串式注释 |
| `ToNum` | 数字（正则） | 数值 Iota |
| `ToVec` | `vec_X_Y_Z` | 向量 Iota |
| `ToMask` | `mask_[-v]+` | Mask 特殊处理器 |
| `ToNumPattern` | `num_N` | 数字字面量特殊处理器 |
| `ToEntity` | `entity_<UUID>` | 实体引用 |
| `ToDialect` | 方言映射 | 自定义名称映射 |
| `FallbackBinaryParser.STR2NBT` | `nbt_` | 二进制 NBT 回退；仅当 `showUnknownNBT` 为 `KEEP_NBT` 时匹配 |
| `TO_RAW_PATTERN` | `^_[wedsaq]*$` | 原始角度签名图案 |

若对应模组已加载，会追加以下正向解析器：

| 模组 | 正向解析器 |
|------|------------|
| `hexal` | `ToGate`, `ToMote` |
| `moreiotas` | `TO_ENTITY_TYPE`, `TO_IOTA_TYPE`, `TO_ITEM_TYPE`, `TO_BLOCK_TYPE`, `TO_STRING`, `TO_STRING_LIT`, `TO_MATRIX` |
| `hexcellular` | `TO_PROPERTY`, `TO_MY_PROPERTY` |
| `hexpose` | `TO_IDENTIFIER` |
| `oneironaut` | `TO_DIMENSION` |
| `ephemera` | `TO_POTION` |

内置反向解析器（按匹配顺序）：

| 解析器 | 匹配类型 | 输出格式 |
|--------|----------|----------|
| `PatternParser` | `hexcasting:pattern` | 图案名 / 角度签名 / `(`, `)`, `\` |
| `CommentParser` | `hexparse:comment` | `comment_...` / `/*...*/` / 原始缩进 |
| `NumParser` | `hexcasting:double` | 最简浮点数 |
| `VecParser` | `hexcasting:vec3` | `vec_X_Y_Z` |
| `EntityParser` | `hexcasting:entity` | `self` / `entity_<UUID>` |
| `BoolParser` | `hexcasting:boolean` | `true` / `false` |
| `NullParser` | `hexcasting:null` | `null` |
| `GarbageParser` | `hexcasting:garbage` | `garbage` |

若对应模组已加载，会追加以下反向解析器：`hexal`（`GateParser`、`MoteParser`）、`moreiotas`（`StringParser.IOTA`、`StringParser.ENTITY`、`ItemTypeParser`、`StringLitParser`、`MatrixParser`）、`hexcellular`（`PropertyParser`）、`hexpose`（`IdentifierParser`）、`oneironaut`（`DimParser`）、`ephemera`（`PotionParser`）。

## 注意事项

- 自定义解析器追加在列表末尾，因此**优先级低于所有内置解析器**。如果你的自定义前缀与内置解析器冲突，内置解析器会先匹配
- 若需更高优先级，需在 `ParserMain.init()` 之前注册，或使用不与内置前缀冲突的命名
- `IPlayerBinder.BindPlayer` 在每个解析入口（`ParseCode` / `ParseIotaNbt`）都会被调用，不要在其中存储长期状态
- `getCost()` 返回的消耗会在解析时通过 `CostTracker` 累积，最终从施法者身上扣除
