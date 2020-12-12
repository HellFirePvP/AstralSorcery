/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import com.mojang.datafixers.util.Either;
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
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
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
            if (!corruptedSkipWaterCheck) {
                pos = world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos).down();
            }
            return corruptedSkipWaterCheck || (
                    world.isAirBlock(pos.up()) &&
                            (state.getBlock() instanceof FlowingFluidBlock &&
                                    state.getMaterial() == Material.WATER &&
                                    state.get(FlowingFluidBlock.LEVEL) == 0) ||
                            state.getBlock() instanceof BubbleColumnBlock
                    );
        });
        this.excludeRitualColumn();
    }

    @Nullable
    @Override
    public ListEntries.CounterMaxEntry recreateElement(CompoundNBT tag, BlockPos pos) {
        return new ListEntries.CounterMaxEntry(pos, 1);
    }

    @Nullable
    @Override
    public ListEntries.CounterMaxEntry createElement(World world, BlockPos pos) {
        pos = world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos).down();
        return new ListEntries.CounterMaxEntry(pos, 1);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float alphaMultiplier, boolean extended) {
        ConstellationEffectProperties prop = this.createProperties(pedestal.getMirrorCount());

        Vector3 at = new Vector3(pos).add(0.5, 0.5, 0.5);
        at.addY(prop.getSize() * 0.75F);
        for (int i = 0; i < Math.max(1, prop.getSize() / 6); i++) {
            Vector3 vec = at.clone().add(Vector3.random().setY(0).multiply(rand.nextFloat() * prop.getSize()));

            Color c = MiscUtils.eitherOf(rand,
                    () -> ColorsAS.CONSTELLATION_OCTANS,
                    () -> ColorsAS.CONSTELLATION_OCTANS.darker());
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(vec)
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .color(VFXColorFunction.constant(c))
                    .setScaleMultiplier(0.6F + rand.nextFloat() * 0.3F)
                    .setGravityStrength(0.0004F + rand.nextFloat() * 0.0008F)
                    .setMaxAge(100 + rand.nextInt(60));
        }
    }

    @Override
    public boolean playEffect(World world, BlockPos pos, ConstellationEffectProperties properties, @Nullable IMinorConstellation trait) {
        if (!(world instanceof ServerWorld)) {
            return false;
        }

        boolean update = false;
        if (properties.isCorrupted()) {
            corruptedSkipWaterCheck = true;
            Either<ListEntries.CounterMaxEntry, BlockPos> newEntry = this.peekNewPosition(world, pos, properties);
            corruptedSkipWaterCheck = false;
            return newEntry.mapLeft(entry -> {
                BlockState state = world.getBlockState(entry.getPos());
                BlockPos offset = entry.getPos().subtract(pos);
                if (world.isAirBlock(entry.getPos()) &&
                        (this.isLinkedRitual || Math.abs(offset.getX()) > 5 || Math.abs(offset.getZ()) > 5 || offset.getY() < 0)) {
                    if (!world.getDimensionType().isUltrawarm()) {
                        if (world.setBlockState(entry.getPos(), Blocks.WATER.getDefaultState())) {
                            for (int i = 0; i < 3; i++) {
                                spawnFishingDropsAt((ServerWorld) world, entry.getPos());
                            }
                            world.neighborChanged(entry.getPos(), Blocks.WATER, entry.getPos());
                        }
                    }
                } else if (BlockUtils.isFluidBlock(state)) {
                    if (state.getBlock() == Blocks.WATER) {
                        if (rand.nextInt(100) == 0) {
                            spawnFishingDropsAt((ServerWorld) world, entry.getPos());
                        }
                    } else {
                        world.setBlockState(entry.getPos(), Blocks.SAND.getDefaultState());
                    }
                } else if (state.getBlock() instanceof BubbleColumnBlock) {
                    if (rand.nextInt(70) == 0) {
                        spawnFishingDropsAt((ServerWorld) world, entry.getPos());
                    }
                }
                return true;
            }).left().orElse(false);
        }

        ListEntries.CounterMaxEntry entry = getRandomElementChanced();
        if (entry != null) {
            if (MiscUtils.canEntityTickAt(world, entry.getPos())) {
                if (!isValid(world, entry)) {
                    removeElement(entry);
                } else {
                    sendConstellationPing(world, new Vector3(entry.getPos()).add(0.5, 1, 0.5));
                    int count = entry.getCounter();
                    count++;
                    entry.setCounter(count);

                    if (count >= entry.getMaxCount()) {
                        int min = Math.min(CONFIG.minFishTickTime.get(), CONFIG.maxFishTickTime.get());
                        int max = Math.max(CONFIG.minFishTickTime.get(), CONFIG.maxFishTickTime.get());

                        int diff = Math.max(1, max - min + 1);
                        entry.setMaxCount(min + rand.nextInt(diff));
                        entry.setCounter(0);

                        spawnFishingDropsAt((ServerWorld) world, entry.getPos());
                    }
                }
                update = true;
            }
        }

        if (findNewPosition(world, pos, properties)
                .ifRight(attemptedPos -> sendConstellationPing(world, new Vector3(world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, attemptedPos).down()).add(0.5, 0.5, 0.5)))
                .left().isPresent()) {
            update = true;
        }
        return update;
    }

    private void spawnFishingDropsAt(ServerWorld world, BlockPos pos) {
        Vector3 dropLoc = new Vector3(pos).add(0.5, 0.85, 0.5);
        ItemStack tool = new ItemStack(Items.FISHING_ROD);
        tool.addEnchantment(Enchantments.LUCK_OF_THE_SEA, 2);

        ResourceLocation fromTable = LootTables.GAMEPLAY_FISHING_FISH;
        if (rand.nextFloat() < 0.1F) {
            fromTable = LootTables.GAMEPLAY_FISHING_TREASURE;
        }

        LootContext.Builder builder = new LootContext.Builder(world);
        builder.withLuck(rand.nextInt(2) * rand.nextFloat());
        builder.withRandom(rand);
        builder.withParameter(LootParameters.TOOL, tool);
        builder.withParameter(LootParameters.field_237457_g_, Vector3d.copyCentered(pos));
        LootTable lootTable = world.getServer().getLootTableManager().getLootTableFromLocation(fromTable);
        for (ItemStack loot : lootTable.generate(builder.build(LootParameterSets.FISHING))) {
            ItemEntity ei = ItemUtils.dropItemNaturally(world, dropLoc.getX(), dropLoc.getY(), dropLoc.getZ(), loot);
            Vector3 motion = new Vector3(ei.getMotion());
            motion.setY(Math.abs(motion.getY()));
            ei.setMotion(motion.toVector3d());
        }
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    private static class OctansConfig extends CountConfig {

        private final int defaultMinFishTickTime = 20;
        private final int defaultMaxFishTickTime = 60;

        public ForgeConfigSpec.IntValue minFishTickTime;
        public ForgeConfigSpec.IntValue maxFishTickTime;

        public OctansConfig() {
            super("octans", 8D, 1D, 64);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            super.createEntries(cfgBuilder);

            this.minFishTickTime = cfgBuilder
                    .comment("Defines the minimum default tick-time until a fish may be fished by the ritual. Gets reduced internally the more starlight was provided at the ritual.")
                    .translation(translationKey("minFishTickTime"))
                    .defineInRange("minFishTickTime", this.defaultMinFishTickTime, 5, Integer.MAX_VALUE);

            this.maxFishTickTime = cfgBuilder
                    .comment("Defines the maximum default tick-time until a fish may be fished by the ritual. Gets reduced internally the more starlight was provided at the ritual. Has to be bigger as the minimum time; if it isn't it'll be set to the minimum.")
                    .translation(translationKey("maxFishTickTime"))
                    .defineInRange("maxFishTickTime", this.defaultMaxFishTickTime, 10, Integer.MAX_VALUE);
        }
    }
}
