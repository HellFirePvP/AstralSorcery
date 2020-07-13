/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.capability.ChunkFluidEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapabilitiesAS
 * Created by HellFirePvP
 * Date: 19.07.2019 / 13:59
 */
public class CapabilitiesAS {

    private CapabilitiesAS() {}

    public static final ResourceLocation CHUNK_FLUID_KEY = AstralSorcery.key("chunk_fluid");

    @CapabilityInject(ChunkFluidEntry.class)
    public static Capability<ChunkFluidEntry> CHUNK_FLUID = null;

}
