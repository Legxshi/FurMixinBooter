# FurMixinBooter

This port was made to provide a more stable mixins that works on 1.8 without any additional modules or broken compatibilities.

## For developers

- Add the jitpack repository and the mod!
```groovy
repositories {
    maven("https://www.jitpack.io")
}

dependencies {
    implementation("com.github.Legxshi:FurMixinBooter:VERSION") { isTransitive = false }
    annotationProcessor("com.github.Legxshi:FurMixinBooter:VERSION") { isTransitive = false }
}
```
Note: If you don't know what version to use, I recommend
[![](https://www.jitpack.io/v/Legxshi/FurMixinBooter.svg)](https://www.jitpack.io/#Legxshi/FurMixinBooter)

- Use it as if you had normal mixins, and add this code to your manifest
```groovy
manifest.attributes.run {
    this["TweakClass"] = "org.spongepowered.asm.launch.MixinTweaker"
}
```

- And place this mod into your mods folder + your mod!

### Other actions directly from [MixinBooter](https://github.com/CleanroomMC/MixinBooter)

1. Pick your path:

- Mixin into minecraft, forge or coremods? Make a *coremod* with `IFMLLoadingPlugin` and implement the class with `IEarlyMixinLoader`
-  Mixin into normal mods? Make a normal class anywhere in your mod file, implement the class with `ILateMixinLoader`

2. Register your mixin configs
- In either your `IEarlyMixinLoader` or `ILateMixinLoader` you have to return a list of mixin config names via the `getMixinConfigs` method
- This is the path (relative to your `resources` root) to your mixin config.
