/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.lib;

import hellfirepvp.astralsorcery.client.render.entity.RenderEntityAltarFluidInput;
import hellfirepvp.astralsorcery.client.render.entity.RenderEntityEmpty;
import hellfirepvp.astralsorcery.client.render.entity.RenderEntityGrapplingHook;
import hellfirepvp.astralsorcery.client.render.entity.RenderItemEntityHighlighted;
import hellfirepvp.astralsorcery.client.tile.*;
import hellfirepvp.astralsorcery.common.lib.EntitiesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderersAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderersAS {

    public static void registerTileEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TileEntitiesAS.ALTAR.type(),
                ctx -> new TileAltarRenderer());

        event.registerBlockEntityRenderer(TileEntitiesAS.FOCUS_RELAY.type(),
                ctx -> new TileFocusRelayRenderer(ctx.getItemRenderer()));
        event.registerBlockEntityRenderer(TileEntitiesAS.LUMEN_CRYSTAL_CLUSTER.type(),
                ctx -> new TileLumenCrystalClusterRenderer(ctx.getBlockRenderDispatcher()));

        event.registerBlockEntityRenderer(TileEntitiesAS.LUMEN_ARRAY.type(),
                ctx -> new TileLumenArrayRenderer(ctx.getItemRenderer()));
        event.registerBlockEntityRenderer(TileEntitiesAS.LUMEN_ALCHEMY_ARRAY.type(),
                ctx -> new TileLumenAlchemyArrayRenderer(ctx.getItemRenderer()));
        event.registerBlockEntityRenderer(TileEntitiesAS.LUMEN_CRYSTALLIZER.type(),
                ctx -> new TileLumenCrystallizerRenderer());
        event.registerBlockEntityRenderer(TileEntitiesAS.LIGHTWELL.type(),
                ctx -> new TileLightwellRenderer(ctx.getItemRenderer()));
        event.registerBlockEntityRenderer(TileEntitiesAS.INFUSER.type(),
                ctx -> new TileInfuserRenderer(ctx.getItemRenderer()));
        event.registerBlockEntityRenderer(TileEntitiesAS.CHALICE.type(),
                ctx -> new TileChaliceRenderer());
        event.registerBlockEntityRenderer(TileEntitiesAS.ATTUNEMENT_ALTAR.type(),
                ctx -> new TileAttunementAltarRenderer(ctx.getModelSet()));
        event.registerBlockEntityRenderer(TileEntitiesAS.TRANSLUCENT_BLOCK.type(),
                ctx -> new TileTranslucentBlockRenderer());
        event.registerBlockEntityRenderer(TileEntitiesAS.TRANSLUCENT_TREE.type(),
                ctx -> new TileTranslucentTreeRenderer());
        event.registerBlockEntityRenderer(TileEntitiesAS.STARLIGHT_FOCUS_CRYSTAL.type(),
                ctx -> new TileStarlightFocusCrystalRenderer());
        event.registerBlockEntityRenderer(TileEntitiesAS.LENS.type(),
                ctx -> new TileLensRenderer(ctx.getModelSet()));
        event.registerBlockEntityRenderer(TileEntitiesAS.PRISM.type(),
                ctx -> new TilePrismRenderer());

        event.registerEntityRenderer(EntitiesAS.ITEM_HIGHLIGHTED.get(), RenderItemEntityHighlighted::new);
        event.registerEntityRenderer(EntitiesAS.ITEM_STARMETAL.get(), RenderItemEntityHighlighted::new);
        event.registerEntityRenderer(EntitiesAS.ITEM_CRYSTAL.get(), RenderItemEntityHighlighted::new);
        event.registerEntityRenderer(EntitiesAS.ITEM_ARTIFACT.get(), RenderItemEntityHighlighted::new);
        event.registerEntityRenderer(EntitiesAS.ITEM_ALTAR_INPUT.get(), RenderItemEntityHighlighted::new);
        event.registerEntityRenderer(EntitiesAS.FLUID_ALTAR_INPUT.get(), RenderEntityAltarFluidInput::new);

        event.registerEntityRenderer(EntitiesAS.FLARE.get(), RenderEntityEmpty::new);
        event.registerEntityRenderer(EntitiesAS.ILLUMINATION_SPARK.get(), RenderEntityEmpty::new);
        event.registerEntityRenderer(EntitiesAS.NOCTURNAL_SPARK.get(), RenderEntityEmpty::new);
        event.registerEntityRenderer(EntitiesAS.VIVID_SPARK.get(), RenderEntityEmpty::new);
        event.registerEntityRenderer(EntitiesAS.GRAPPLING_HOOK.get(), RenderEntityGrapplingHook::new);
        event.registerEntityRenderer(EntitiesAS.SHOOTING_STAR.get(), RenderEntityEmpty::new);
    }
}
