# 如何使用 IPlayerBinder

**简体中文** [English](../en_us/player-binder.md)

> 生成于GLM-5.1

## 概述

`IPlayerBinder` 是 HexParse 解析管线中的玩家上下文注入机制。当解析器需要访问施法者信息（如实体引用、权限检查、世界状态）时，实现此接口即可在每次解析前自动接收当前 `ServerPlayer` 引用。

## 接口定义

```java
public interface IPlayerBinder {
    void BindPlayer(ServerPlayer p);
}
```

仅一个方法——`BindPlayer`，在解析开始前由 `ParserMain` 自动调用。

## 调用时机

`ParserMain` 在两个解析入口处统一绑定玩家：

- **正向解析**（`ParseCode`）：遍历所有 `str2nbtParsers`，对 `IPlayerBinder` 实例调用 `BindPlayer`
- **反向解析**（`ParseIotaNbt`）：遍历所有 `nbt2strParsers`，对 `IPlayerBinder` 实例调用 `BindPlayer`

这意味着无论你的解析器是 `IStr2Nbt` 还是 `INbt2Str`，只要同时实现 `IPlayerBinder`，就能在 `parse` 执行时使用已绑定的玩家引用。

## 使用方式

### 与 IStr2Nbt 组合

让你的正向解析器同时实现 `IPlayerBinder`，在 `BindPlayer` 中缓存玩家引用，然后在 `parse` 中使用：

```kotlin
class MyParser : BaseConstParser.Prefix("my_"), IPlayerBinder {
    private lateinit var player: ServerPlayer

    override fun BindPlayer(p: ServerPlayer) {
        player = p
    }

    override fun parse(node: String): CompoundTag {
        // 使用 player 访问施法者信息
        val level = player.serverLevel()
        // ...
    }
}
```

### 与 INbt2Str 组合

反向解析器同理：

```kotlin
class MyBackParser : INbt2Str, IPlayerBinder {
    private lateinit var player: ServerPlayer

    override fun BindPlayer(p: ServerPlayer) {
        player = p
    }

    override fun match(node: CompoundTag): Boolean { ... }

    override fun parse(node: CompoundTag): String {
        // 使用 player 访问施法者信息
    }
}
```

## 项目中的实现参考

以下内置解析器实现了 `IPlayerBinder`，可作为实际用法的参考：

| 解析器 | 用途 | 文件 |
|--------|------|------|
| `ToMiscConst` | 将 `self`/`myself` 解析为施法者自身的实体 Iota | [ToMiscConst.kt](../../common/src/main/java/io/yukkuric/hexparse/parsers/str2nbt/ToMiscConst.kt) |
| `ToEntity` | 将 `entity_<UUID>` 解析为实体引用，需校验是否为他人名字 | [ToEntity.java](../../common/src/main/java/io/yukkuric/hexparse/parsers/str2nbt/ToEntity.java) |
| `ToPattern.ToGreatPattern` | 检查卓越法术是否已解锁 | [ToPattern.java](../../common/src/main/java/io/yukkuric/hexparse/parsers/str2nbt/ToPattern.java) |
| `FallbackBinaryParser.STR2NBT` | 二进制 NBT 回退解析，需检查管理员权限 | [FallbackBinaryParser.kt](../../common/src/main/java/io/yukkuric/hexparse/parsers/FallbackBinaryParser.kt) |
| `PatternParser` | 反向解析图案，需玩家上下文匹配注册表 | [PatternParser.java](../../common/src/main/java/io/yukkuric/hexparse/parsers/nbt2str/PatternParser.java) |
| `EntityParser` | 反向解析实体 Iota，判断是否为 `self` | [EntityParser.java](../../common/src/main/java/io/yukkuric/hexparse/parsers/nbt2str/EntityParser.java) |
| `PluginConstParsers.TO_PROPERTY.Private` | 解析私有属性，需施法者名字 | [PluginConstParsers.java](../../common/src/main/java/io/yukkuric/hexparse/parsers/str2nbt/plugins/PluginConstParsers.java) |

## 注意事项

- `BindPlayer` 在**每次解析调用前**都会被调用，不要假设引用在多次解析间保持不变
- 不要在 `BindPlayer` 中存储长期状态——它是一个"刷新"操作而非"初始化"操作
- 若你的解析器仅在特定条件下需要玩家信息，可以在 `BindPlayer` 中缓存引用，在 `parse` 中按需使用
- `BindPlayer` 接收的 `ServerPlayer` 可能为 `null`（虽然正常施法流程中不会），建议做防御性检查
