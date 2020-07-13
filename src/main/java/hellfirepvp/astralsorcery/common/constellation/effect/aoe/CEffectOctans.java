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
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectOctans
 * Created by HellFirePvP
 * Date: 01.02.2020 / 15:45
 */
public class CEffectOctans extends CEffectAbstractList<ListEntries.CounterMaxEntry> {

    public static OctansConfig CONFIG = new OctansConfig();

    private static boolean corruptedSkipWaterCheck = false;

    public CEffectOctans(@Nonnull ILocatable origin) {
        super(origin, ConstellationsAS.octans, CONFIG.maxAmount.get(), (world, pos, state) -> {
            return corruptedSkipWaterCheck || (
                    state.getBlock() instanceof FlowingFluidBlock &&
                            state.getMaterial() == Material.WATER &&
                            state.get(FlowingFluidBlock.LEVEL) == 0 &&
                            world.isAirBlock(pos.up())
                    );
        });
        this.excludeRitualColumn();
    }

    @Nullable
    @Override
    public ListEntries.CounterMaxEntry recreateElement(CompoundNBT tag, BlockPos pos) {
        return null;
    }

    @Nullable
    @Override
    public ListEntries.CounterMaxEntry createElement(World world, BlockPos pos) {
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float alphaMultiplier, boolean extended) {
        ConstellationEffectProperties prop = this.createProperties(pedestal.getMirrorCount());

        Vector3 at = new Vector3(pos).add(0.5, 0.5, 0.5);
        at.addY(prop.getSize() * 0.75F);
        at.add(Vector3.random().setY(0).multiply(rand.nextFloat() * prop.getSize()));

        Color c = MiscUtils.eitherOf(rand,
                () -> ColorsAS.CONSTELLATION_OCTANS,
                () -> ColorsAS.CONSTELLATION_OCTANS.darker(),
                () -> ColorsAS.CONSTELLATION_OCTANS.darker().darker());
        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(at)
                .alpha(VFXAlphaFunction.FADE_OUT)
                .color(VFXColorFunction.constant(c))
                .setScaleMultiplier(0.5F + rand.nextFloat() * 0.2F)
                .setGravityStrength(0.0025F)
                .setMaxAge(50 + rand.nextInt(20));
    }

    @Override
    public boolean playEffect(World world, BlockPos pos, ConstellationEffectProperties properties, @Nullable IMinorConstellation trait) {
        if (!(world instanceof ServerWorld)) {
            return false;
        }

        boolean update = false;
        if (properties.isCorrupted()) {
            corruptedSkipWaterCheck = true;
            ListEntries.CounterMaxEntry entry = this.peekNewPosition(world, pos, properties);
            corruptedSkipWaterCheck = false;
            if (entry != null) {
                BlockState state = world.getBlockState(entry.getPos());
                BlockPos offset = entry.getPos().subtract(pos);
                if (world.isAirBlock(entry.getPos()) &&
                        (this.isLinkedRitual || Math.abs(offset.getX()) > 5 || Math.abs(offset.getZ()) > 5 || offset.getY() < 0)) {
                    if (world.setBlockState(entry.getPos(), Blocks.WATER.getDefaultState())) {
                        for (int i = 0; i < 3; i++) {
                            spawnFishingDropsAt((ServerWorld) world, entry.getPos());
                        }
                        world.neighborChanged(entry.getPos(), Blocks.WATER, entry.getPos());
                    }
                } else if (state.getBlock() instanceof FlowingFluidBlock) {
                    if (state.getBlock() == Blocks.WATER) {
                        if (rand.nextInt(100) == 0) {
                            spawnFishingDropsAt((ServerWorld) world, entry.getPos());
                        }
                    } else {
                        world.setBlockState(entry.getPos(), Blocks.SAND.getDefaultState());
                    }
                }
                update = true;
            }
            return update;
        }

        ListEntries.CounterMaxEntry entry = getRandomElementChanced();
        if (entry != null) {
            if (MiscUtils.canEntityTickAt(world, entry.getPos())) {
                if (!verifier.test(world, entry.getPos(), world.getBlockState(entry.getPos()))) {
                    removeElement(entry);
                } else {
                    int count = entry.getCounter();
                    count++;
                    entry.setCounter(count);

                    if (count >= entry.getMaxCount()) {
                        int min = Math.min(CONFIG.minFishTickTime.get(), CONFIG.maxFishTickTime.get());
                        int max = Math.max(CONFIG.minFishTickTime.get(), CONFIG.maxFishTickTime.get());

                        entry.setMaxCount(min + rand.nextInt(max - min + 1));
                        entry.setCounter(0);

                        spawnFishingDropsAt((ServerWorld) world, entry.getPos());
                    }
                }
                update = true;
            }
        }

        if (findNewPosition(world, pos, properties) != null) {
            update = true;
        }
        return update;
    }

    private void spawnFishingDropsAt(ServerWorld world, BlockPos pos) {
        Vector3 dropLoc = new Vector3(pos).add(0.5, 0.85, 0.5);
        ItemStack tool = new ItemStack(Items.FISHING_ROD);
        tool.addEnchantment(Enchantments.LUCK_OF_THE_SEA, 2);

        LootContext.Builder builder = new LootContext.Builder(world);
        builder.withLuck(rand.nextInt(2) * rand.nextFloat());
        builder.withRandom(rand);
        builder.withParameter(LootParameters.TOOL, tool);
        builder.withParameter(LootParameters.POSITION, pos);
        LootTable table = world.getServer().getLootTableManager().getLootTableFromLocation(LootTables.GAMEPLAY_FISHING);
        for (ItemStack loot : table.generate(builder.build(LootParameterSets.FISHING))) {
            ItemEntity ei = ItemUtils.dropItemNaturally(world, dropLoc.getX(), dropLoc.getY(), dropLoc.getZ(), loot);
            Vector3 motion = new Vector3(ei.getMotion());
            motion.setY(Math.abs(motion.getY()));
            ei.setMotion(motion.toVec3d());
        }
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    private static class OctansConfig extends CountConfig {

        private final int defaultMinFishTickTime = 100;
        private final int defaultMaxFishTickTime = 500;

        public ForgeConfigSpec.IntValue minFishTickTime;
        public ForgeConfigSpec.IntValue maxFishTickTime;

        public OctansConfig() {
            super("octans", 12D, 2D, 5);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            super.createEntries(cfgBuilder);

            this.maxFishTickTime = cfgBuilder
                    .comment("Defines the maximum default tick-time until a fish may be fished by the ritual. Gets reduced internally the more starlight was provided at the ritual. Has to be bigger as the minimum time; if it isn't it'll be set to the minimum.")
                    .translation(translationKey("maxFishTickTime"))
                    .defineInRange("maxFishTickTime", this.defaultMaxFishTickTime, 20, Integer.MAX_VALUE);

            this.minFishTickTime = cfgBuilder
                    .comment("Defines the minimum default tick-time until a fish may be fished by the ritual. Gets reduced internally the more starlight was provided at the ritual.")
                    .translation(translationKey("minFishTickTime"))
                    .defineInRange("minFishTickTime", this.defaultMinFishTickTime, 20, Integer.MAX_VALUE);
        }
    }
}
