/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.EventFlags;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.util.block.BlockDiscoverer;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemInfusedCrystalShovel
 * Created by HellFirePvP
 * Date: 04.04.2020 / 11:05
 */
public class ItemInfusedCrystalShovel extends ItemCrystalShovel {

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
        World world = player.getEntityWorld();
        if (!world.isRemote() &&
                !player.isSneaking() &&
                !player.getCooldownTracker().hasCooldown(itemstack.getItem()) &&
                player instanceof ServerPlayerEntity) {

            PlayerProgress prog = ResearchHelper.getProgress(player, LogicalSide.SERVER);
            if (prog.doPerkAbilities()) {
                EventFlags.CHAIN_MINING.executeWithFlag(() -> {
                    if (!world.getBlockState(pos).isAir(world, pos)) {
                        List<BlockPos> foundBlocks = BlockDiscoverer.discoverBlocksWithSameStateAround(world, pos, true, 8, 200, false);
                        if (!foundBlocks.isEmpty()) {
                            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

                            foundBlocks.forEach(at -> {
                                BlockState currentState = world.getBlockState(at);
                                if (!currentState.isAir(world, at) && serverPlayer.interactionManager.tryHarvestBlock(at)) {
                                    PktPlayEffect ev = new PktPlayEffect(PktPlayEffect.Type.BLOCK_EFFECT)
                                            .addData(buf -> {
                                                ByteBufUtils.writePos(buf, at);
                                                ByteBufUtils.writeBlockState(buf, currentState);
                                            });
                                    PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, at, 32));
                                }
                            });

                            serverPlayer.getCooldownTracker().setCooldown(itemstack.getItem(), 120);
                        }
                    }
                });
            }
        }
        return super.onBlockStartBreak(itemstack, pos, player);
    }
}
