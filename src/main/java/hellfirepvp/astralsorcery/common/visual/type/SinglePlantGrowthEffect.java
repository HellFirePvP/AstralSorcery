/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.visual.type;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.visual.BlockPosEffect;
import hellfirepvp.astralsorcery.common.visual.VisualEffectTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SinglePlantGrowthEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class SinglePlantGrowthEffect extends BlockPosEffect {

    public static final StreamCodec<RegistryFriendlyByteBuf, SinglePlantGrowthEffect> STREAM_CODEC =
            BlockPosEffect.createCodec(SinglePlantGrowthEffect::new);
    public static final VisualEffectTypes.EffectType<SinglePlantGrowthEffect> TYPE =
            new VisualEffectTypes.EffectType<>(AstralSorcery.key("single_plant_growth"), STREAM_CODEC);

    private SinglePlantGrowthEffect(BlockPos pos) {
        super(pos);
    }

    public static SinglePlantGrowthEffect at(BlockPos pos) {
        return new SinglePlantGrowthEffect(pos);
    }

    @Override
    public void playEffect(RandomSource rand) {
        int amount = 3 + rand.nextInt(3);
        for (int i = 0; i < amount; i++) {
            Vector3 pos = this.getPos().add(rand.nextFloat(), 0.2F, rand.nextFloat());
            float scale = 0.4F + rand.nextFloat() * 0.5F;
            float gravity = 0.0008F + rand.nextFloat() * 0.0003F;
            int age = 30 + rand.nextInt(20);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .setScale(scale)
                    .setAlpha(0.8F)
                    .color(FXColorFunction.constant(ColorsAS.GROWTH_EFFECT))
                    .setGravity(Vector3.y(gravity))
                    .setMaxAge(age);
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .setScale(scale * 0.3F)
                    .setAlpha(0.8F)
                    .color(FXColorFunction.WHITE)
                    .setGravity(Vector3.y(gravity))
                    .setMaxAge(age);
        }
    }

    @Override
    public VisualEffectTypes.EffectType<?> getType() {
        return TYPE;
    }
}
