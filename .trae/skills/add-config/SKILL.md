---
name: "add-config"
description: "Adds a new config item across HexParseConfig*.java files. Invoke when user wants to add a new configuration option to the mod."
---

# Add Config Item

This skill adds a new configuration item to all three HexParseConfig files in the HexParseMod project.

## Files Involved

1. **Base class**: `common/src/main/java/io/yukkuric/hexparse/config/HexParseConfig.java`
2. **Fabric impl**: `fabric/src/main/java/io/yukkuric/hexparse/fabric/config/HexParseConfigFabric.java`
3. **Forge impl**: `forge/src/main/java/io/yukkuric/hexparse/forge/config/HexParseConfigForge.java`

## Step 1: Ask User for Input

Use `AskUserQuestion` to ask the user for:

1. **Config item name** in CamelCase (e.g. `fooBar`). This determines:
   - Static method name: `fooBar()`
   - Interface method name: `fooBar()`
   - DESCRIP constant: `DESCRIP_FOO_BAR` (upper snake_case of the CamelCase name)
   - Forge field: `CfgFooBar` (PascalCase of the CamelCase name)
   - Forge config key: `"FooBar"` (PascalCase)
   - Fabric field: `fooBar` (camelCase)

2. **Description text** for the config item. This goes into the `DESCRIP_*` constant.

3. **Config type**: `boolean`, `int`, or `enum`. If `enum`, also ask for the enum class name (must already exist in HexParseConfig.java).

4. **Default value**: e.g. `true`, `false`, `0`, or an enum constant name.

If the type is `int`, also ask for `min` and `max` range values.

## Step 2: Add to HexParseConfig.java (Base Class)

### 2a. Add DESCRIP constant

Find the last `DESCRIP_` constant line and add after it:

```
public static final String DESCRIP_<UPPER_SNAKE> = "<description text>";
```

Where `<UPPER_SNAKE>` is the CamelCase name converted to UPPER_SNAKE_CASE.

### 2b. Add static method

Find the last `public static` method before `public interface API` and add after it. The method delegates to `imp`:

- For `boolean`: `public static boolean <camelCase>() { return imp.<camelCase>(); }`
- For `int`: `public static int <camelCase>() { return imp.<camelCase>(); }`
- For `enum`: `public static <EnumType> <camelCase>() { return imp.<camelCase>(); }`

### 2c. Add interface method

Find the last method in `interface API` and add after it:

- For `boolean`: `boolean <camelCase>();`
- For `int`: `int <camelCase>();`
- For `enum`: `<EnumType> <camelCase>();`

## Step 3: Add to HexParseConfigFabric.java (Fabric Implementation)

All changes are in the inner `Common` class.

### 3a. Add field with annotations

Find the last `@Comment(DESCRIP_*)` field and add after it:

- For `boolean`:
  ```
  @Comment(DESCRIP_<UPPER_SNAKE>)
  private boolean <camelCase> = <defaultValue>;
  ```
- For `int`:
  ```
  @Comment(DESCRIP_<UPPER_SNAKE>)
  private int <camelCase> = <defaultValue>;
  ```
- For `enum`:
  ```
  @Comment(DESCRIP_<UPPER_SNAKE>)
  @Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
  private <EnumType> <camelCase> = <EnumType>.<defaultValue>;
  ```

### 3b. Add @Override method

Find the last `@Override` method in `Common` and add after it:

```
@Override
public <ReturnType> <camelCase>() {
    return <camelCase>;
}
```

## Step 4: Add to HexParseConfigForge.java (Forge Implementation)

### 4a. Add @Override method

Find the last `@Override` method (before the field declarations) and add after it:

```
@Override
public <ReturnType> <camelCase>() {
    return Cfg<PascalCase>.get();
}
```

### 4b. Add field declaration

Find the appropriate field declaration group and add:

- For `boolean`: add to the `ForgeConfigSpec.BooleanValue` group:
  `Cfg<PascalCase>,`
- For `int`: add to the `ForgeConfigSpec.IntValue` group:
  `Cfg<PascalCase>,`
- For `enum`: add a new line:
  `public final ForgeConfigSpec.EnumValue<<EnumType>> Cfg<PascalCase>;`

### 4c. Add constructor line

Find the last line in the constructor (`HexParseConfigForge(ForgeConfigSpec.Builder builder)`) and add after it:

- For `boolean`:
  `Cfg<PascalCase> = builder.comment(DESCRIP_<UPPER_SNAKE>).define("<PascalCase>", <defaultValue>);`
- For `int`:
  `Cfg<PascalCase> = builder.comment(DESCRIP_<UPPER_SNAKE>).defineInRange("<PascalCase>", <defaultValue>, <min>, <max>);`
- For `enum`:
  `Cfg<PascalCase> = builder.comment(DESCRIP_<UPPER_SNAKE>).defineEnum("<PascalCase>", <EnumType>.<defaultValue>);`

## Naming Convention Summary

Given CamelCase name `fooBar`:

| Location | Name |
|---|---|
| DESCRIP constant | `DESCRIP_FOO_BAR` |
| Static method (base) | `fooBar()` |
| Interface method | `fooBar()` |
| Fabric field | `fooBar` |
| Forge field | `CfgFooBar` |
| Forge config key | `"FooBar"` |

## Important Notes

- Do NOT add any comments to the generated code.
- Preserve the existing code style: no blank lines between consecutive `@Override` methods in Fabric, single blank line between method groups in base class.
- For enum types, the enum class must already exist in HexParseConfig.java. If the user wants a new enum, ask them to define it first.
- The `@Gui.EnumHandler(option = EnumDisplayOption.BUTTON)` annotation is only needed for enum fields in Fabric.
- In Forge, boolean fields are grouped in `ForgeConfigSpec.BooleanValue`, int fields in `ForgeConfigSpec.IntValue`, and enum fields each get their own `ForgeConfigSpec.EnumValue<EnumType>` declaration line.
