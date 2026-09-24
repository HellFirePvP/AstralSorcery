/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.liquid.output;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.crystal.CrystalPropertyGenerator;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.types.LiquidStarlightRecipeOutputTypesAS;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipeInput;
import hellfirepvp.astralsorcery.common.tile.TileCelestialCrystalCluster;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidStarlightOutputFormCrystalCluster
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LiquidStarlightOutputFormGemCrystalCluster extends LiquidStarlightRecipeOutputModifier {

    private static final LiquidStarlightOutputFormGemCrystalCluster INSTANCE = new LiquidStarlightOutputFormGemCrystalCluster();
    public static final MapCodec<LiquidStarlightOutputFormGemCrystalCluster> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, LiquidStarlightOutputFormGemCrystalCluster> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final Type<LiquidStarlightOutputFormGemCrystalCluster> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private LiquidStarlightOutputFormGemCrystalCluster() {}

    public static LiquidStarlightOutputFormGemCrystalCluster getInstance() {
        return INSTANCE;
    }

    @Override
    public Type<?> getType() {
        return LiquidStarlightRecipeOutputTypesAS.FORM_GEM_CRYSTAL_CLUSTER.get();
    }

    @Override
    public boolean isOutput(LiquidStarlightRecipeInput input, ItemEntity output) {
        return false;
    }

    @Override
    public void createOutput(LiquidStarlightRecipe recipe, LiquidStarlightRecipeInput input) {
        Level level = input.getTriggerEntity().level();
        BlockPos pos = input.getTriggerEntity().blockPosition();

        if (level.setBlockAndUpdate(pos, BlocksAS.GEM_CRYSTAL_CLUSTER.get().defaultBlockState())) {
            recipe.consumeItemInputs(input);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playCraftingEffects(LiquidStarlightRecipe recipe, LiquidStarlightRecipeInput input, RandomSource rand, int craftingTick) {
        super.playCraftingEffects(recipe, input, rand, craftingTick);

        if (rand.nextInt(7) == 0) {
            Vector3 pos = new Vector3(input.getTriggerEntity());
            pos = VectorUtil.withRandomOffset(pos, rand, 0.1F).setY(pos.getY());

            EffectHelper.of(EffectTemplatesAS.LIGHT_BEAM)
                    .spawn(pos)
                    .setup(pos.copy().addY(4F + rand.nextFloat() * 3F), 0.8F, 0.8F);
        }

        for (int i = 0; i < 3; i++) {
            Vector3 grav = Vector3.y(-0.002F + rand.nextFloat() * -0.001F);
            float scale = 0.15F + rand.nextFloat() * 0.1F;
            ColorWrapper color = ColorWrapper.ofHSB(rand.nextFloat(), 1F, 1F);

            Vector3 pos = new Vector3(input.getTriggerEntity());
            pos = VectorUtil.withRandomOffset(pos, rand, 0.4F);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .color(FXColorFunction.constant(color))
                    .setScale(scale)
                    .setAlpha(0.8F)
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .setMotion(Vector3.y(0.1F))
                    .setGravity(grav);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .color(FXColorFunction.WHITE)
                    .setScale(scale * 0.3F)
                    .setAlpha(0.8F)
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .setMotion(Vector3.y(0.1F))
                    .setGravity(grav);
        }
    }
}
