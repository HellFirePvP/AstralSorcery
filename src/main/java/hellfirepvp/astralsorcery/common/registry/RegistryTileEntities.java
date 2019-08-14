/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import com.google.common.base.CaseFormat;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.*;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

import static hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryTileEntities
 * Created by HellFirePvP
 * Date: 01.06.2019 / 13:35
 */
public class RegistryTileEntities {

    private RegistryTileEntities() {}

    public static void registerTiles() {
        SPECTRAL_RELAY = registerTile(TileSpectralRelay.class, BlocksAS.SPECTRAL_RELAY);
        ALTAR = registerTile(TileAltar.class, BlocksAS.ALTAR_DISCOVERY, BlocksAS.ALTAR_ATTUNEMENT, BlocksAS.ALTAR_CONSTELLATION, BlocksAS.ALTAR_RADIANCE);
        COLLECTOR_CRYSTAL = registerTile(TileCollectorCrystal.class, BlocksAS.ROCK_COLLECTOR_CRYSTAL, BlocksAS.CELESTIAL_COLLECTOR_CRYSTAL);
        RITUAL_LINK = registerTile(TileRitualLink.class, BlocksAS.RITUAL_LINK);
        RITUAL_PEDESTAL = registerTile(TileRitualPedestal.class, BlocksAS.RITUAL_PEDESTAL);
        WELL = registerTile(TileWell.class, BlocksAS.WELL);
    }

    private static <T extends TileEntity> TileEntityType<T> registerTile(Class<T> tileClass, Block... validBlocks) {
        ResourceLocation name = createTileEntityName(tileClass);
        TileEntityType.Builder<T> typeBuilder = TileEntityType.Builder.create(() -> {
            try {
                return tileClass.newInstance();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
            throw new IllegalArgumentException("Unexpected Constructor for class: " + tileClass.getName());
        }, validBlocks);

        TileEntityType<T> type = typeBuilder.build(null);
        type.setRegistryName(name);
        AstralSorcery.getProxy().getRegistryPrimer().register(type);
        return type;
    }

    private static ResourceLocation createTileEntityName(Class<? extends TileEntity> tileClass) {
        String name = tileClass.getSimpleName();
        if (name.startsWith("Tile")) {
            name = name.substring(4);
        }
        name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
        return new ResourceLocation(AstralSorcery.MODID, name);
    }
}
