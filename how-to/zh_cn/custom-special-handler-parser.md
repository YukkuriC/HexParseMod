# 如何注册自定义 SpecialHandler 反向解析器

**简体中文** [English](../en_us/custom-special-handler-parser.md)

> 生成于GLM-5.1

## 概述

HexCasting 中的某些图案由 `SpecialHandler` 处理，而非普通的 `Action`。HexParse 的 `PatternParser` 在反向解析（NBT → 字符串）时，遇到 `SpecialHandler` 类型的图案会查找已注册的反向解析器来生成字符串表示。

通过 `HexParseAPI.AddSpecialHandlerBackParser`，你可以为自定义的 `SpecialHandler` 类型注册反向解析逻辑。

## SpecialHandler 反向解析流程

当 `PatternParser` 解析一个图案 Iota 时，执行流程如下：

1. 从 NBT 中重建 `HexPattern` 对象
2. 调用 `PatternRegistryManifest.matchPattern()` 匹配图案
3. 若匹配结果为 `PatternShapeMatch.Special`，获取其 `SpecialHandler` 实例
4. 在 `SPECIAL_HANDLER_MAP` 中查找该 `SpecialHandler` 的 `Class` 对应的解析函数
5. 若找到，调用该函数生成字符串；否则回退到角度签名（`_anglesSignature`）

## API 方法

### AddSpecialHandlerBackParser

```kotlin
HexParseAPI.AddSpecialHandlerBackParser(
    cls: Class<T>,
    func: (T, CompoundTag) -> String
)
```

| 参数 | 类型 | 说明 |
|------|------|------|
| `cls` | `Class<T>` | `SpecialHandler` 的子类 `Class` 对象 |
| `func` | `(T, CompoundTag) -> String` | 反向解析函数，接收 `SpecialHandler` 实例和原始 NBT 节点，返回字符串表示 |

- `T` 必须是 `SpecialHandler` 的子类
- `func` 的第一个参数是已匹配的 `SpecialHandler` 实例，可以直接从中提取数据
- `func` 的第二个参数是图案 Iota 的完整 `CompoundTag`，可用于访问额外数据

## 内置 SpecialHandler 解析器

HexParse 已为以下 `SpecialHandler` 注册了反向解析器：

| SpecialHandler 类 | 输出格式 | 说明 |
|-------------------|----------|------|
| `SpecialHandlerMask` | `mask_...` | Mask 操作，`-` 表示遮蔽，`v` 表示可见 |
| `SpecialHandlerNumberLiteral` | `num_N` | 数字字面量，使用最简浮点表示 |

### SpecialHandlerMask 示例

对于一个 3 位 Mask（遮蔽、可见、遮蔽），输出为 `mask_-v-`。

### SpecialHandlerNumberLiteral 示例

对于数字 `3.14`，输出为 `num_3.14`。

## 实战示例

### 示例：注册自定义 SpecialHandler 反向解析器

假设你有一个自定义 `SpecialHandler`，用于表示颜色值：

```kotlin
import at.petrak.hexcasting.api.casting.castables.SpecialHandler
import io.yukkuric.hexparse.api.HexParseAPI
import net.minecraft.nbt.CompoundTag

class ColorSpecialHandler(val color: Int) : SpecialHandler {
    companion object {
        fun registerBackParser() {
            HexParseAPI.AddSpecialHandlerBackParser(
                ColorSpecialHandler::class.java
            ) { handler, node ->
                "color_%06x".format(handler.color)
            }
        }
    }
}

ColorSpecialHandler.registerBackParser()
```

### 在 Java 中注册

```java
import at.petrak.hexcasting.api.casting.castables.SpecialHandler;
import io.yukkuric.hexparse.api.HexParseAPI;
import net.minecraft.nbt.CompoundTag;

public class MySpecialHandler extends SpecialHandler {
    public static void registerBackParser() {
        HexParseAPI.AddSpecialHandlerBackParser(
            MySpecialHandler.class,
            (handler, node) -> "myhandler_" + handler.getValue()
        );
    }
}
```

## 注意事项

- 注册时机应在 mod 初始化阶段完成，确保在首次解析之前注册
- 每个 `SpecialHandler` 类只能注册一个反向解析器，重复注册会覆盖之前的
- 如果未为某个 `SpecialHandler` 注册解析器，`PatternParser` 会回退到输出角度签名（`_` 前缀 + 角度字符串），这仍然有效但可读性较差
- `func` 中可以使用 `INbt2Str.displayMinimalStatic(Double)` 辅助方法来格式化浮点数
