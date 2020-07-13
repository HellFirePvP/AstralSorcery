/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.render.tile.*;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.*;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;

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
        ATTUNEMENT_ALTAR = registerTile(TileAttunementAltar.class, BlocksAS.ATTUNEMENT_ALTAR);
        CELESTIAL_CRYSTAL_CLUSTER = registerTile(TileCelestialCrystals.class, BlocksAS.CELESTIAL_CRYSTAL_CLUSTER);
        CHALICE = registerTile(TileChalice.class, BlocksAS.CHALICE);
        COLLECTOR_CRYSTAL = registerTile(TileCollectorCrystal.class, BlocksAS.ROCK_COLLECTOR_CRYSTAL, BlocksAS.CELESTIAL_COLLECTOR_CRYSTAL);
        GEM_CRYSTAL_CLUSTER = registerTile(TileGemCrystals.class, BlocksAS.GEM_CRYSTAL_CLUSTER);
        ILLUMINATOR = registerTile(TileIlluminator.class, BlocksAS.ILLUMINATOR);
        INFUSER = registerTile(TileInfuser.class, BlocksAS.INFUSER);
        LENS = registerTile(TileLens.class, BlocksAS.LENS);
        OBSERVATORY = registerTile(TileObservatory.class, BlocksAS.OBSERVATORY);
        PRISM = registerTile(TilePrism.class, BlocksAS.PRISM);
        REFRACTION_TABLE = registerTile(TileRefractionTable.class, BlocksAS.REFRACTION_TABLE);
        RITUAL_LINK = registerTile(TileRitualLink.class, BlocksAS.RITUAL_LINK);
        RITUAL_PEDESTAL = registerTile(TileRitualPedestal.class, BlocksAS.RITUAL_PEDESTAL);
        TELESCOPE = registerTile(TileTelescope.class, BlocksAS.TELESCOPE);
        TRANSLUCENT_BLOCK = registerTile(TileTranslucentBlock.class, BlocksAS.TRANSLUCENT_BLOCK);
        VANISHING = registerTile(TileVanishing.class, BlocksAS.VANISHING);
        WELL = registerTile(TileWell.class, BlocksAS.WELL);
    }

    @OnlyIn(Dist.CLIENT)
    public static void initClient() {
        ClientRegistry.bindTileEntityRenderer(ALTAR, RenderAltar::new);
        ClientRegistry.bindTileEntityRenderer(ATTUNEMENT_ALTAR, RenderAttunementAltar::new);
        ClientRegistry.bindTileEntityRenderer(CHALICE, RenderChalice::new);
        ClientRegistry.bindTileEntityRenderer(COLLECTOR_CRYSTAL, RenderCollectorCrystal::new);
        ClientRegistry.bindTileEntityRenderer(INFUSER, RenderInfuser::new);
        ClientRegistry.bindTileEntityRenderer(LENS, RenderLens::new);
        ClientRegistry.bindTileEntityRenderer(OBSERVATORY, RenderObservatory::new);
        ClientRegistry.bindTileEntityRenderer(PRISM, RenderPrism::new);
        ClientRegistry.bindTileEntityRenderer(REFRACTION_TABLE, RenderRefractionTable::new);
        ClientRegistry.bindTileEntityRenderer(RITUAL_PEDESTAL, RenderRitualPedestal::new);
        ClientRegistry.bindTileEntityRenderer(SPECTRAL_RELAY, RenderSpectralRelay::new);
        ClientRegistry.bindTileEntityRenderer(TELESCOPE, RenderTelescope::new);
        ClientRegistry.bindTileEntityRenderer(TRANSLUCENT_BLOCK, RenderTranslucentBlock::new);
        ClientRegistry.bindTileEntityRenderer(WELL, RenderWell::new);
    }

    private static <T extends TileEntity> TileEntityType<T> registerTile(Class<T> tileClass, Block... validBlocks) {
        ResourceLocation name = NameUtil.fromClass(tileClass, "Tile");
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
}
