plugins {
    idea
    java
    id("gg.essential.loom") version "1.10.+"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val mod_name = "FurMixinBooter"
val mod_id = "furmixinbooter"
val mod_version = "10.7+1"

val mod_base = "zone.rong.mixinbooter"
val mc_version = "1.8.9"

val mod_archives_name = "+${mod_id}-$mod_version"
val transformerFile = file("src/main/resources/${mod_id}_at.cfg")

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

loom {
    log4jConfigs.from(file("log4j2.xml"))

    runs {
        // NOTE: Every time a variable is modified, the config needs to be regenerated manually
        named("client") {
            // If you don't want to log in with your real minecraft account, remove these lines
            //property("devauth.configDir", rootProject.file(".devauth").absolutePath)

            property("fml.coreMods.load", "zone.rong.mixinbooter.MixinBooterPlugin")
            property("mixin.checks.interfaces", "true")
            property("mixin.debug.export", "true")

            programArgs("--tweakClass", "org.spongepowered.asm.launch.MixinTweaker")

            //"-ea:${project.group}"

            isIdeConfigGenerated = true
        }

        removeIf { it.name == "server" }
    }
	
    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())

        if (transformerFile.exists()) {
            println("Installing access transformer")
            accessTransformer(transformerFile)
        }
    }

    // If you don't want mixins, remove these lines
    mixin {
        mixin.useLegacyMixinAp.set(true)
        defaultRefmapName.set("mixins.mixinbooter.refmap.json")
    }
}

tasks.runClient {
    this.javaLauncher.set(
        javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(8))
        },
    )
}

sourceSets.main {
    output.setResourcesDir(sourceSets.main.flatMap { it.java.classesDirectory })
}

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven/")
    // If you don't want to log in with your real minecraft account, remove this line
    //maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    maven("https://www.jitpack.io")
}

val shadowImpl: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")

    // These libraries make Loom work like intended
    annotationProcessor("com.google.code.gson:gson:2.10.1")
    annotationProcessor("com.google.guava:guava:17.0")
    annotationProcessor("org.ow2.asm:asm-debug-all:5.2")

    shadowImpl("com.github.Legxshi:FurMixin:c2911246c3") { isTransitive = false }
    annotationProcessor("com.github.Legxshi:FurMixin:c2911246c3")

    shadowImpl("io.github.llamalad7:mixinextras-common:0.5.3") { isTransitive = false }
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.3")

    // If you don't want to log in with your real minecraft account, remove this line
    //runtimeOnly("me.djtheredstoner:DevAuth-forge-legacy:1.2.1")
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

tasks.withType(org.gradle.jvm.tasks.Jar::class) {
    archiveBaseName.set(mod_id)
    manifest.attributes.run {
        this["FMLCorePluginContainsFMLMod"] = true
        this["ForceLoadAsMod"] = true
        this["FMLCorePlugin"] = "zone.rong.mixinbooter.MixinBooterPlugin"

        this["TweakClass"] = "org.spongepowered.asm.launch.MixinTweaker"
        this["Premain-Class"] = "org.spongepowered.tools.agent.MixinAgent"
        this["Agent-Class"] = "org.spongepowered.tools.agent.MixinAgent"
        this["Can-Redefine-Classes"] = true
        this["Can-Retransform-Classes"] = true

        if (transformerFile.exists()) {
            this["FMLAT"] = "${mod_id}_at.cfg"
        }
    }
}

tasks.processResources {
    inputs.property("mod_name", mod_name)
    inputs.property("mod_version", mod_version)
    inputs.property("mod_id", mod_id)

    inputs.property("mc_version", mc_version)

    filesMatching(listOf("mcmod.info")) {
        expand(inputs.properties)
    }

    rename("(.+_at.cfg)", "META-INF/$1")
}

val remapJar by tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
    archiveClassifier.set("")
    from(tasks.shadowJar)
    inputFile.set(tasks.shadowJar.get().archiveFile)
    archiveBaseName.set(mod_archives_name)
}

tasks.jar {
    archiveClassifier.set("without-deps")
    destinationDirectory.set(layout.buildDirectory.dir("intermediates"))
}

tasks.shadowJar {
    destinationDirectory.set(layout.buildDirectory.dir("intermediates"))
    archiveClassifier.set("non-obfuscated-with-deps")
    configurations = listOf(shadowImpl)
    doLast {
        configurations.forEach {
            println("Copying dependencies into mod: ${it.files}")
        }
    }

    exclude(
        "**/LICENSE.md",
        "**/LICENSE.txt",
        "LICENSE.txt",
        "**/LICENSE",
        "**/NOTICE",
        "**/NOTICE.txt",
        "pack.mcmeta",
        "dummyThing",
        "**/module-info.class",
        "META-INF/proguard/**",
        "META-INF/maven/**",
        "META-INF/versions/**",
        "META-INF/com.android.tools/**",
        "fabric.mod.json",
        "org/**/*.html",
        "com.example/**",
        "META-INF/*.RSA",
        "LICENSE_MixinExtras",
        "README.md"
    )

    // If you want to include other dependencies and shadow them, you can relocate them in here
    fun relocate(name: String) = relocate(name, "$mod_base.deps.$name")

    //relocate("org.spongepowered")
}

tasks.assemble.get().dependsOn(tasks.remapJar)
