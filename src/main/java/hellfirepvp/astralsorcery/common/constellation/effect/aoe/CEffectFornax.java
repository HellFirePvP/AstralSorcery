/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.constellation.effect.base.CEffectAbstractList;
import hellfirepvp.astralsorcery.common.constellation.effect.base.ListEntries;
import hellfirepvp.astralsorcery.common.crafting.nojson.WorldFreezingRegistry;
import hellfirepvp.astralsorcery.common.crafting.nojson.WorldMeltableRegistry;
import hellfirepvp.astralsorcery.common.crafting.nojson.freezing.WorldFreezingRecipe;
import hellfirepvp.astralsorcery.common.crafting.nojson.meltable.WorldMeltableRecipe;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import hellfirepvp.astralsorcery.common.util.block.iterator.BlockPositionGenerator;
import hellfirepvp.astralsorcery.common.util.block.iterator.BlockRandomPositionGenerator;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectFornax
 * Created by HellFirePvP
 * Date: 29.11.2019 / 22:23
 */
public class CEffectFornax extends CEffectAbstractList<ListEntries.PosEntry> {

    public static FornaxConfig CONFIG = new FornaxConfig();

    public CEffectFornax(@Nonnull ILocatable origin) {
        super(origin, ConstellationsAS.fornax, 1, (world, pos, state) -> true);
        this.excludeRitualPositions();
        this.selectSphericalPositions();
    }

    @Nonnull
    @Override
    protected BlockPositionGenerator createPositionStrategy() {
        BlockPositionGenerator gen = new BlockRandomPositionGenerator();
        gen.andFilter(pos -> pos.getY() < 0);
        return gen;
    }

    @Nullable
    @Override
    public ListEntries.PosEntry recreateElement(CompoundNBT tag, BlockPos pos) {
        return new ListEntries.PosEntry(pos);
    }

    @Nullable
    @Override
    public ListEntries.PosEntry createElement(World world, BlockPos pos) {
        return new ListEntries.PosEntry(pos);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float alphaMultiplier, boolean extended) {
        Vector3 motion = Vector3.random().multiply(0.04);
        if (pos.equals(pedestal.getPos())) {
            motion.setY(0);
        } else {
            motion.setY(Math.abs(motion.getY()) * -1);
        }
        Color c = MiscUtils.eitherOf(rand,
                () -> ColorsAS.CONSTELLATION_FORNAX.brighter(),
                () -> ColorsAS.CONSTELLATION_FORNAX.darker(),
                () -> ColorsAS.CONSTELLATION_FORNAX.darker(),
                () -> ColorsAS.CONSTELLATION_FORNAX.darker().darker());
        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(new Vector3(pos).add(0.5, 0.2, 0.5))
                .alpha(VFXAlphaFunction.FADE_OUT)
                .color(VFXColorFunction.constant(c))
                .setScaleMultiplier(0.3F + rand.nextFloat() * 0.4F)
                .setMotion(motion)
                .setGravityStrength(-0.0015F)
                .setMaxAge(60 + rand.nextInt(30));
    }

    @Override
    public boolean playEffect(World world, BlockPos pos, ConstellationEffectProperties properties, @Nullable IMinorConstellation trait) {
        if (!(world instanceof ServerWorld)) {
            return false;
        }

        Consumer<ItemStack> dropResult = stack -> ItemUtils.dropItemNaturally(world, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, stack);

        ListEntries.PosEntry newEntry = this.peekNewPosition(world, pos, properties);
        if (newEntry == null) {
            return false;
        }
        BlockPos at = newEntry.getPos();

        if (properties.isCorrupted()) {
            WorldFreezingRecipe freezingRecipe = WorldFreezingRegistry.INSTANCE.getRecipeFor(world, at);
            if (freezingRecipe != null) {
                freezingRecipe.doOutput(world, at, world.getBlockState(at), dropResult);
                return true;
            }
            return false;
        }

        WorldMeltableRecipe meltRecipe = WorldMeltableRegistry.INSTANCE.getRecipeFor(world, at);
        if (meltRecipe != null) {
            meltRecipe.doOutput(world, at, world.getBlockState(at), dropResult);
            return true;
        }
        return false;
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    private static class FornaxConfig extends Config {

        private final float defaultMeltFailChance = 0F;

        public ForgeConfigSpec.DoubleValue meltFailChance;

        public FornaxConfig() {
            super("fornax", 8D, 2D);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            super.createEntries(cfgBuilder);

            this.meltFailChance = cfgBuilder
                    .comment("Defines the chance (0% to 100% -> 0.0 to 1.0) if the block will be replaced with air instead of being properly melted into something.")
                    .translation(translationKey("meltFailChance"))
                    .defineInRange("meltFailChance", this.defaultMeltFailChance, 0.0D, 1.0D);
        }
    }
}
