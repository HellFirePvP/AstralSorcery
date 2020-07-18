/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event.effect;

import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXLightbeam;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.data.sync.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.sync.client.ClientLightConnections;
import hellfirepvp.astralsorcery.common.tile.TileLens;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.event.TickEvent;

import java.awt.*;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LightbeamRenderHelper
 * Created by HellFirePvP
 * Date: 24.08.2019 / 21:12
 */
public class LightbeamRenderHelper implements ITickHandler {

    private static final LightbeamRenderHelper INSTANCE = new LightbeamRenderHelper();
    private int ticksExisted = 0;

    private LightbeamRenderHelper() {}

    public static void attachTickListener(Consumer<ITickHandler> registrar) {
        registrar.accept(INSTANCE);
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        ticksExisted++;
        if (ticksExisted % 35 == 0) {
            ticksExisted = 0;
            Entity rView = Minecraft.getInstance().getRenderViewEntity();
            if (rView == null) {
                rView = Minecraft.getInstance().player;
            }
            if (rView != null) {
                Entity renderView = rView;
                DimensionType dimType = renderView.getEntityWorld().getDimension().getType();

                SyncDataHolder.executeClient(SyncDataHolder.DATA_LIGHT_CONNECTIONS, ClientLightConnections.class, (data) -> {
                    for (Map.Entry<BlockPos, Set<BlockPos>> entry : data.getClientConnections(dimType).entrySet()) {

                        BlockPos at = entry.getKey();
                        if (renderView.getDistanceSq(at.getX(), at.getY(), at.getZ()) <= RenderingConfig.CONFIG.getMaxEffectRenderDistanceSq()) {
                            Vector3 source = new Vector3(at).add(0.5, 0.5, 0.5);
                            Color overlay = null;
                            TileLens lens = MiscUtils.getTileAt(renderView.getEntityWorld(), at, TileLens.class, true);
                            if (lens != null) {
                                if (lens.getColorType() != null) {
                                    overlay = lens.getColorType().getColor();
                                }
                            }
                            for (BlockPos dst : entry.getValue()) {
                                Vector3 to = new Vector3(dst).add(0.5, 0.5, 0.5);
                                FXLightbeam beam = EffectHelper.of(EffectTemplatesAS.LIGHTBEAM_TRANSFER)
                                        .spawn(source)
                                        .setup(to, 0.4, 0.4)
                                        .setAlphaMultiplier(0.4F);
                                if (overlay != null) {
                                    beam.color(VFXColorFunction.constant(overlay));
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.CLIENT);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Lightbeam Render Helper";
    }
}
