/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity.goal;

import hellfirepvp.astralsorcery.common.constellation.mantle.effect.MantleEffectPelotrio;
import hellfirepvp.astralsorcery.common.entity.EntitySpectralTool;
import hellfirepvp.astralsorcery.common.util.BlockDropCaptureAssist;
import hellfirepvp.astralsorcery.common.util.block.BlockDiscoverer;
import hellfirepvp.astralsorcery.common.util.block.BlockPredicate;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpectralToolBreakLogGoal
 * Created by HellFirePvP
 * Date: 22.02.2020 / 16:54
 */
public class SpectralToolBreakLogGoal extends SpectralToolGoal {

    private BlockPos selectedBreakPos = null;

    public SpectralToolBreakLogGoal(EntitySpectralTool entity, double speed) {
        super(entity, speed);
    }

    private BlockPredicate breakableLogs() {
        return (world, pos, state) -> {
            return world.getTileEntity(pos) == null &&
                    pos.getY() >= this.getEntity().getStartPosition().getY() &&
                    !state.isAir(world, pos) &&
                    state.getBlockHardness(world, pos) != -1 &&
                    state.getBlockHardness(world, pos) <= 10 &&
                    (state.isIn(BlockTags.LOGS) || state.isIn(BlockTags.LEAVES)) &&
                    BlockUtils.canToolBreakBlockWithoutPlayer(world, pos, state, new ItemStack(Items.DIAMOND_AXE));
        };
    }

    @Override
    public boolean shouldExecute() {
        MovementController ctrl = this.getEntity().getMoveHelper();

        if (!ctrl.isUpdating()) {
            return true;
        } else {
            BlockPos validPos = BlockDiscoverer.searchAreaForFirst(
                    this.getEntity().getEntityWorld(),
                    this.getEntity().getStartPosition(),
                    8,
                    Vector3.atEntityCorner(this.getEntity()),
                    this.breakableLogs());
            return validPos != null;
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.selectedBreakPos != null;
    }

    @Override
    public void startExecuting() {
        super.startExecuting();

        BlockPos validPos = BlockDiscoverer.searchAreaForFirst(
                this.getEntity().getEntityWorld(),
                this.getEntity().getStartPosition(),
                10,
                Vector3.atEntityCorner(this.getEntity()),
                this.breakableLogs());

        if (validPos != null) {
            this.selectedBreakPos = validPos;

            this.getEntity().getMoveHelper().setMoveTo(
                    this.selectedBreakPos.getX() + 0.5,
                    this.selectedBreakPos.getY() + 0.5,
                    this.selectedBreakPos.getZ() + 0.5,
                    this.getSpeed());
        }
    }

    @Override
    public void resetTask() {
        super.resetTask();

        this.selectedBreakPos = null;
        this.actionCooldown = 0;
    }

    @Override
    public void tick() {
        super.tick();

        if (!shouldContinueExecuting()) {
            return;
        }

        if (this.actionCooldown < 0) {
            this.actionCooldown = 0; //lol. wtf.
        }

        World world = this.getEntity().getEntityWorld();
        boolean resetTimer = false;

        if (world.isAirBlock(this.selectedBreakPos)) {
            this.selectedBreakPos = null;
            resetTimer = true;
        } else {
            this.getEntity().getMoveHelper().setMoveTo(
                    this.selectedBreakPos.getX() + 0.5,
                    this.selectedBreakPos.getY() + 0.5,
                    this.selectedBreakPos.getZ() + 0.5,
                    this.getSpeed());

            if (Vector3.atEntityCorner(this.getEntity()).distance(this.selectedBreakPos) <= 4) {
                this.actionCooldown++;
                if (this.actionCooldown >= MantleEffectPelotrio.CONFIG.ticksPerAxeLogBreak.get() && world instanceof ServerWorld) {
                    LivingEntity owner = this.getEntity().getOwningEntity();
                    if (owner instanceof PlayerEntity) {
                        BlockDropCaptureAssist.startCapturing();
                    }
                    if (BlockUtils.breakBlockWithoutPlayer(
                            (ServerWorld) world,
                            this.selectedBreakPos,
                            world.getBlockState(this.selectedBreakPos),
                            this.getEntity().getItem(),
                            true,
                            true,
                            true)) {
                        resetTimer = true;
                    }
                    if (owner instanceof PlayerEntity) {
                        for (ItemStack dropped : BlockDropCaptureAssist.getCapturedStacksAndStop()) {
                            ItemStack remainder = ItemUtils.dropItemToPlayer((PlayerEntity) owner, dropped);
                            if (!remainder.isEmpty()) {
                                ItemUtils.dropItemNaturally(world, owner.getPosX(), owner.getPosY(), owner.getPosZ(), remainder);
                            }
                        }
                    }
                }
            }
        }

        if (resetTimer) {
            this.actionCooldown = 0;
        }
    }
}
