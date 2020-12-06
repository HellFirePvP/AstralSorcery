/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.versions.forge.ForgeVersion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Mods
 * Created by HellFirePvP
 * Date: 31.10.2016 / 11:30
 */
public enum Mods {

    MINECRAFT("minecraft", true),
    FORGE(ForgeVersion.MOD_ID, true),
    ASTRAL_SORCERY(AstralSorcery.MODID, true),
    DRACONIC_EVOLUTION("draconicevolution"),
    CURIOS("curios"),
    JEI("jei"),
    BOTANIA("botania");

    private final String modid;
    private final boolean loaded;

    //private static Class<?> gcPlayerClass, urPlayerClass;

    Mods(String modid) {
        this(modid, ModList.get().isLoaded(modid));
    }

    private Mods(String modid, boolean loaded) {
        this.modid = modid;
        this.loaded = loaded;
    }

    public String getModId() {
        return this.modid;
    }

    public boolean isPresent() {
        return loaded;
    }

    public void executeIfPresent(Supplier<Runnable> execSupplier) {
        if (this.isPresent()) {
            execSupplier.get().run();
        }
    }

    @Nullable
    public <T> T getIfPresent(Supplier<Supplier<T>> supplierSupplier) {
        if (this.isPresent()) {
            return supplierSupplier.get().get();
        }
        return null;
    }

    public boolean owns(IForgeRegistryEntry<?> entry) {
        return this.isPresent() &&
                entry.getRegistryName() != null &&
                entry.getRegistryName().getNamespace().equals(this.modid);
    }

    @Nonnull
    public ResourceLocation key(String path) {
        return new ResourceLocation(this.getModId(), path);
    }

    public void sendIMC(String method, Supplier<?> thing) {
        if (this.isPresent()) {
            InterModComms.sendTo(AstralSorcery.MODID, this.getModId(), method, thing);
        }
    }

    @Nullable
    public static Mods byModId(String modId) {
        for (Mods mod : values()) {
            if (mod.getModId().equals(modId)) {
                return mod;
            }
        }
        return null;
    }

    @Nullable
    public Class<?> getExtendedPlayerClass() {
        if (!isPresent()) return null;

        //switch (this) {
        //    case GALACTICRAFT_CORE:
        //        if (gcPlayerClass == null) {
        //            try {
        //                gcPlayerClass = Class.forName("micdoodle8.mods.galacticraft.core.entities.player.GCServerPlayerEntity");
        //            } catch (Exception ignored) {}
        //        }
        //        return gcPlayerClass;
        //    case UNIVERSALREMOTE:
        //        if (urPlayerClass == null) {
        //            try {
        //                urPlayerClass = Class.forName("clayborn.universalremote.hooks.entity.HookedServerPlayerEntity");
        //            } catch (Exception ignored) {}
        //        }
        //        return urPlayerClass;
        //    default:
        //        break;
        //}
        return null;
    }

}
