/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: Mods
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public enum Mods {

    MINECRAFT("minecraft", true),
    NEOFORGE(NeoForgeVersion.MOD_ID, true),
    ASTRAL_SORCERY(AstralSorcery.MODID, true),
    DRACONIC_EVOLUTION("draconicevolution"),
    CURIOS("curios"),
    SIMULATED("simulated");

    private final String modid;
    private final boolean loaded;

    Mods(String modid) {
        this(modid, ModList.get().isLoaded(modid));
    }

    Mods(String modid, boolean loaded) {
        this.modid = modid;
        this.loaded = loaded;
    }

    public String getModId() {
        return this.modid;
    }

    public boolean isPresent() {
        return loaded;
    }

    @Nonnull
    public ResourceLocation key(String path) {
        return ResourceLocation.fromNamespaceAndPath(this.getModId(), path);
    }

    public static Optional<Mods> byModId(String modId) {
        return Arrays.stream(values())
                .filter(mod -> mod.getModId().equals(modId))
                .findFirst();
    }
}
