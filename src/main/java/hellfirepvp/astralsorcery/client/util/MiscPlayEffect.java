/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.block.tile.BlockGemCrystalCluster;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MiscPlayEffect
 * Created by HellFirePvP
 * Date: 31.08.2019 / 20:46
 */
public class MiscPlayEffect {

    private static final Random rand = new Random();

    @OnlyIn(Dist.CLIENT)
    public static void fireLightning(PktPlayEffect effect) {
        Vector3 start = ByteBufUtils.readVector(effect.getExtraData());
        Vector3 end   = ByteBufUtils.readVector(effect.getExtraData());
        Color color = Color.WHITE;
        if (effect.getExtraData().isReadable()) {
            color = new Color(effect.getExtraData().readInt(), true);
        }
        EffectHelper.of(EffectTemplatesAS.LIGHTNING)
                .spawn(start)
                .makeDefault(end)
                .color(VFXColorFunction.constant(color));
    }

    @OnlyIn(Dist.CLIENT)
    public static void catalystBurst(PktPlayEffect event) {
        Vector3 vec = ByteBufUtils.readVector(event.getExtraData());

        BatchRenderContext<? extends FXFacingParticle> ctx;
        switch (rand.nextInt(3)) {
            case 2:
                ctx = EffectTemplatesAS.CRYSTAL_BURST_3;
                break;
            case 1:
                ctx = EffectTemplatesAS.CRYSTAL_BURST_2;
                break;
            default:
            case 0:
                ctx = EffectTemplatesAS.CRYSTAL_BURST_1;
                break;
        }
        EffectHelper.of(ctx)
                .spawn(vec)
                .setScaleMultiplier(1.5F);
    }

    @OnlyIn(Dist.CLIENT)
    public static void gemCrystalBurst(PktPlayEffect event) {
        Vector3 vec = ByteBufUtils.readVector(event.getExtraData());
        BlockGemCrystalCluster.GrowthStageType type = MiscUtils.getEnumEntry(BlockGemCrystalCluster.GrowthStageType.class, event.getExtraData().readInt());

        BatchRenderContext<? extends FXFacingParticle> ctx = EffectTemplatesAS.GEM_CRYSTAL_BURST;
        float scale = 0.5F;
        switch (type) {
            case STAGE_2_SKY:
                ctx = EffectTemplatesAS.GEM_CRYSTAL_BURST_SKY;
                scale = 1.2F;
                break;
            case STAGE_2_DAY:
                ctx = EffectTemplatesAS.GEM_CRYSTAL_BURST_DAY;
                scale = 1.2F;
                break;
            case STAGE_2_NIGHT:
                ctx = EffectTemplatesAS.GEM_CRYSTAL_BURST_NIGHT;
                scale = 1.2F;
                break;
        }
        EffectHelper.of(ctx)
                .spawn(vec.add(0.5, 0.3, 0.5))
                .setScaleMultiplier(scale);
    }
}
