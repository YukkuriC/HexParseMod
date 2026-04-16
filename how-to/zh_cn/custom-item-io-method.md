# 如何注册自定义物品 IO 方法

**简体中文** [English](../en_us/custom-item-io-method.md)

> 生成于GLM-5.1

## 概述

HexParse 通过 `IOMethod` 类管理物品的 Iota 读写操作。当玩家手持物品执行解析/写入时，HexParse 会查找与该物品类型匹配的 `IOMethod` 来执行数据的读写。

通过 `HexParseAPI.CreateItemIOMethod`，你可以为自定义物品注册 IO 方法，使 HexParse 能够从你的物品中读取 Iota 或将 Iota 写入你的物品。

## IOMethod 工作原理

`IOMethod` 在构造时自动注册到全局映射表 `ITEM_IO_TYPES` 中，以物品的 `Class` 为键。当需要读写时，`IOMethod.get()` 会根据物品实例的 `Class` 查找对应的 `IOMethod`。

### 查找优先级

当玩家手持物品时，HexParse 同时检查主手和副手：

1. 分别查找主手和副手物品的 `IOMethod`
2. 若两者都有 `IOMethod`，选择 `priority` 更低（数值更小）的那个
3. 若 `priority` 相同，优先选择副手

### validator 验证

`validator` 参数允许你在运行时动态判断某个物品实例是否可用于读/写操作：

```kotlin
fun validator(stack: ItemStack, isWrite: Boolean): Boolean
```

- `stack`：当前物品实例
- `isWrite`：`true` 表示写入操作，`false` 表示读取操作
- 返回 `false` 表示该物品实例不支持此操作

## API 方法

### CreateItemIOMethod

```kotlin
HexParseAPI.CreateItemIOMethod(
    cls: Class<*>,
    writer: ((ItemStack, CompoundTag) -> Unit)? = null,
    reader: ((ItemStack) -> CompoundTag?)? = null,
    priority: Int = 0,
    validator: ((ItemStack, Boolean) -> Boolean)? = null,
)
```

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `cls` | `Class<*>` | — | 物品的 `Class` 对象，用于匹配 |
| `writer` | `((ItemStack, CompoundTag) -> Unit)?` | `null` | 写入函数，将 NBT 数据写入物品 |
| `reader` | `((ItemStack) -> CompoundTag?)?` | `null` | 读取函数，从物品中读取 NBT 数据 |
| `priority` | `Int` | `0` | 优先级，数值越小优先级越高 |
| `validator` | `((ItemStack, Boolean) -> Boolean)?` | `null` | 验证函数，判断物品实例是否可用 |

### writer 和 reader 的默认行为

- **writer 为 `null`**：默认行为是将 NBT 数据写入物品的 `data` 标签（适用于实现了 `IotaHolderItem` 接口的物品）
- **reader 为 `null`**：默认行为是调用 `(item as IotaHolderItem).readIotaTag(stack)`（适用于实现了 `IotaHolderItem` 接口的物品）

## 内置 IOMethod 注册

HexParse 已为以下 HexCasting 物品注册了 `IOMethod`：

| 物品类 | writer | reader | 说明 |
|--------|--------|--------|------|
| `ItemFocus` | 默认（`IotaHolderItem`） | 默认 | 法术焦点 |
| `ItemThoughtKnot` | 默认（`IotaHolderItem`） | 默认 | 思维结 |
| `ItemSpellbook` | 自定义（写入当前页） | 默认 | 法术书，写入时写入当前翻到的页面 |

### ItemSpellbook 的自定义 writer

```kotlin
IOMethod(
    ItemSpellbook::class.java,
    writer = { stack, nbt ->
        val idx = ItemSpellbook.getPage(stack, 1)
        val pageKey = idx.toString()
        stack.getOrCreateCompound(ItemSpellbook.TAG_PAGES).put(pageKey, nbt)
    }
)
```

## 实战示例

### 示例 1：为 IotaHolderItem 注册

如果你的物品实现了 `IotaHolderItem` 接口，只需传入 `Class` 对象即可：

```kotlin
import io.yukkuric.hexparse.api.HexParseAPI

HexParseAPI.CreateItemIOMethod(
    cls = MyFocusItem::class.java
)
```

`writer` 和 `reader` 均使用默认的 `IotaHolderItem` 行为。

### 示例 2：为自定义存储物品注册

如果你的物品使用自定义的 NBT 结构存储 Iota：

```kotlin
import io.yukkuric.hexparse.api.HexParseAPI
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack

HexParseAPI.CreateItemIOMethod(
    cls = MyCustomStorageItem::class.java,
    writer = { stack, nbt ->
        stack.getOrCreateTag().put("stored_iota", nbt)
    },
    reader = { stack ->
        stack.tag?.getCompound("stored_iota")
    }
)
```

### 示例 3：使用 validator 限制操作条件

```kotlin
import io.yukkuric.hexparse.api.HexParseAPI

HexParseAPI.CreateItemIOMethod(
    cls = MyConditionalItem::class.java,
    writer = { stack, nbt ->
        stack.getOrCreateTag().put("hex_data", nbt)
    },
    reader = { stack ->
        stack.tag?.getCompound("hex_data")
    },
    validator = { stack, isWrite ->
        if (isWrite) {
            // 只有已激活的物品才能写入
            stack.hasTag() && stack.tag!!.getBoolean("activated")
        } else {
            // 任何状态都可以读取
            true
        }
    }
)
```

### 示例 4：使用 priority 控制主副手优先级

```kotlin
import io.yukkuric.hexparse.api.HexParseAPI

// 高优先级物品（priority 更低）
HexParseAPI.CreateItemIOMethod(
    cls = MyPriorityItem::class.java,
    priority = -100
)

// 普通优先级物品
HexParseAPI.CreateItemIOMethod(
    cls = MyNormalItem::class.java,
    priority = 0
)
```

### 示例 5：在 Java 中注册

```java
import io.yukkuric.hexparse.api.HexParseAPI;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

HexParseAPI.CreateItemIOMethod(
    MyCustomItem.class,
    (stack, nbt) -> {
        stack.getOrCreateTag().put("iota_data", nbt);
    },
    (stack) -> {
        return stack.getTag() != null ? stack.getTag().getCompound("iota_data") : null;
    },
    0,
    null
);
```

## 注意事项

- 每个 `Class` 只能注册一个 `IOMethod`，重复注册会在日志中输出错误（不会覆盖）
- `IOMethod` 在构造时即自动注册，无需额外调用注册方法
- `writer` 和 `reader` 中不要修改物品的类型或数量，只应修改物品的 NBT 数据
- `reader` 返回 `null` 表示该物品当前没有存储的 Iota 数据
- 注册时机应在 mod 初始化阶段，确保在玩家使用之前完成
