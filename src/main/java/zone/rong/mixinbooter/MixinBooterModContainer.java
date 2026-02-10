package zone.rong.mixinbooter;

import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

import java.util.Collections;
import java.util.Set;

public class MixinBooterModContainer extends DummyModContainer {

    public MixinBooterModContainer() {
        super(new ModMetadata());
        MixinBooterPlugin.LOGGER.info("Initializing FurMixinBooter's Mod Container.");
        ModMetadata meta = this.getMetadata();
        meta.modId = "furmixinbooter";
        meta.name = "FurMixinBooter";
        meta.description = "A mod that provides the Sponge Mixin library, a standard API for mods to load mixins targeting Minecraft and other mods, and associated useful utilities on 1.8.";
        meta.credits = "Thanks to CleanroomMC + LegacyModdingMC + Fabric for providing the initial mixin fork.";
        meta.version = "10.7+1";
        meta.logoFile = "/assets/icon.png";
        meta.authorList.add("Legxshi");
        meta.authorList.add("Rongmario");
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    @Override
    public Set<ArtifactVersion> getRequirements() {
        return Collections.emptySet();
    }
}
