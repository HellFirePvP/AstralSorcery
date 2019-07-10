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
import hellfirepvp.astralsorcery.common.tile.TileRitualLink;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.tile.TileWell;
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
        RITUAL_LINK = registerTile(TileRitualLink.class);
        RITUAL_PEDESTAL = registerTile(TileRitualPedestal.class);
        WELL = registerTile(TileWell.class);
    }

    private static <T extends TileEntity> TileEntityType<T> registerTile(Class<T> tileClass) {
        ResourceLocation name = createTileEntityName(tileClass);
        TileEntityType.Builder<T> typeBuilder = TileEntityType.Builder.create(() -> {
            try {
                return tileClass.newInstance();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
            throw new IllegalArgumentException("Unexpected Constructor for class: " + tileClass.getName());
        });

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
