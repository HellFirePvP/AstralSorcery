/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.event.EventFlags;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockPredicate;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeMiningSize
 * Created by HellFirePvP
 * Date: 29.03.2020 / 15:50
 */
public class AttributeTypeMiningSize extends PerkAttributeType {

    public static final Config CONFIG = new Config("type." + PerkAttributeTypesAS.KEY_ATTR_TYPE_MINING_SIZE.getPath());

    public AttributeTypeMiningSize() {
        super(PerkAttributeTypesAS.KEY_ATTR_TYPE_MINING_SIZE);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);

        eventBus.addListener(this::onBreak);
    }

    private void onBreak(BlockEvent.BreakEvent event) {
        IWorld world = event.getWorld();
        PlayerEntity player = event.getPlayer();

        if (!(world instanceof World) || world.isRemote()) {
            return;
        }
        if (player instanceof ServerPlayerEntity) {
            PlayerProgress prog = ResearchHelper.getProgress(player, LogicalSide.SERVER);
            if (!prog.doPerkAbilities() || MiscUtils.isPlayerFakeMP((ServerPlayerEntity) player)) {
                return;
            }
            EventFlags.MINING_SIZE_BREAK.executeWithFlag(() -> {
                float size = PerkAttributeHelper.getOrCreateMap(player, LogicalSide.SERVER)
                        .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_MINING_SIZE, 0);
                size = AttributeEvent.postProcessModded(player, PerkAttributeTypesAS.ATTR_TYPE_MINING_SIZE, size);
                if (size >= 1F) {
                    BlockRayTraceResult brtr = MiscUtils.rayTraceLookBlock(player, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE);
                    if (brtr != null && brtr.getType() == RayTraceResult.Type.BLOCK) {
                        int levelBroken = event.getState().getHarvestLevel();
                        float hardnessBroken = event.getState().getBlockHardness(world, event.getPos());
                        BlockPredicate miningTest = (worldIn, posIn, stateIn) ->
                                stateIn.getHarvestLevel() <= levelBroken &&
                                        stateIn.getBlockHardness(worldIn, posIn) <= hardnessBroken;
                        Direction dir = brtr.getFace();
                        if (dir.getAxis() == Direction.Axis.Y) {
                            this.breakBlocksPlaneHorizontal((ServerPlayerEntity) player, dir, (World) world, event.getPos(), miningTest, MathHelper.floor(size));
                        } else {
                            this.breakBlocksPlaneVertical((ServerPlayerEntity) player, dir, (World) world, event.getPos(), miningTest, MathHelper.floor(size));
                        }
                    }
                }
            });
        }
    }

    private void breakBlocksPlaneVertical(ServerPlayerEntity player, Direction sideBroken, World world, BlockPos at, BlockPredicate miningTest, int size) {
        if (size <= 0) {
            return;
        }
        for (int xx = -size; xx <= size; xx++) {
            if (sideBroken.getDirectionVec().getX() != 0 && xx != 0) continue;
            for (int yy = -1; yy <= (size * 2 - 1); yy++) {
                if (sideBroken.getDirectionVec().getY() != 0 && yy != 0) continue;
                for (int zz = -size; zz <= size; zz++) {
                    if (sideBroken.getDirectionVec().getZ() != 0 && zz != 0) continue;
                    if (xx == 0 && yy == 0 && zz == 0) continue;

                    BlockPos other = at.add(xx, yy, zz);
                    BlockState otherState = world.getBlockState(other);
                    if (otherState.getBlockHardness(world, other) != -1 &&
                            (player.isCreative() || miningTest.test(world, other, otherState)) &&
                            AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerBreak.get(), true)) {
                        BlockState state = world.getBlockState(other);
                        if (!BlockUtils.isFluidBlock(state) && (player.isCreative() || otherState.canHarvestBlock(world, other, player)) && player.interactionManager.tryHarvestBlock(other)) {
                            AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerBreak.get(), false);
                        }
                    }
                }
            }
        }
    }

    private void breakBlocksPlaneHorizontal(ServerPlayerEntity player, Direction sideBroken, World world, BlockPos at, BlockPredicate miningTest, int size) {
        if (size <= 0) {
            return;
        }
        for (int xx = -size; xx <= size; xx++) {
            if (sideBroken.getDirectionVec().getX() != 0 && xx != 0) continue;
            for (int zz = -size; zz <= size; zz++) {
                if (sideBroken.getDirectionVec().getZ() != 0 && zz != 0) continue;
                if (xx == 0 && zz == 0) continue;

                BlockPos other = at.add(xx, 0, zz);
                BlockState otherState = world.getBlockState(other);
                if (otherState.getBlockHardness(world, other) != -1 &&
                        (player.isCreative() || miningTest.test(world, other, otherState)) &&
                        AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerBreak.get(), true)) {
                    BlockState state = world.getBlockState(other);
                    if (!BlockUtils.isFluidBlock(state) && (player.isCreative() || otherState.canHarvestBlock(world, other, player)) && player.interactionManager.tryHarvestBlock(other)) {
                        AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerBreak.get(), false);
                    }
                }
            }
        }
    }

    private static class Config extends ConfigEntry {

        private ForgeConfigSpec.IntValue chargeCostPerBreak;

        private Config(String section) {
            super(section);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            chargeCostPerBreak = cfgBuilder
                    .comment("Defines the amount of starlight charge consumed per additional block break through this attribute.")
                    .translation(translationKey("chargeCostPerBreak"))
                    .defineInRange("chargeCostPerBreak", 2, 1, 500);
        }
    }
}
