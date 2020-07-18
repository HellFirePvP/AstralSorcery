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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.EnumSet;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpectralToolBreakBlockGoal
 * Created by HellFirePvP
 * Date: 22.02.2020 / 15:40
 */
public class SpectralToolBreakBlockGoal extends SpectralToolGoal {

    private BlockPos selectedBreakPos = null;

    public SpectralToolBreakBlockGoal(EntitySpectralTool entity, double speed) {
        super(entity, speed);
        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.TARGET, Flag.LOOK));
    }

    private BlockPredicate breakableSimpleBlocks() {
        return (world, pos, state) -> {
            return world.getTileEntity(pos) == null &&
                    pos.getY() >= this.getEntity().getStartPosition().getY() &&
                    !state.isAir(world, pos) &&
                    state.getBlockHardness(world, pos) != -1 &&
                    state.getBlockHardness(world, pos) <= 10 &&
                    BlockUtils.canToolBreakBlockWithoutPlayer(world, pos, state, new ItemStack(Items.DIAMOND_PICKAXE));
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
                    this.breakableSimpleBlocks());
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
                8,
                Vector3.atEntityCorner(this.getEntity()),
                this.breakableSimpleBlocks());

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
                if (this.actionCooldown >= MantleEffectPelotrio.CONFIG.ticksPerPickaxeBlockBreak.get() && world instanceof ServerWorld) {
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
