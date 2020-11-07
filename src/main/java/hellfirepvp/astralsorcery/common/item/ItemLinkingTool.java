/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.auxiliary.link.IItemLinkingTool;
import hellfirepvp.astralsorcery.common.auxiliary.link.LinkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemLinkingTool
 * Created by HellFirePvP
 * Date: 24.08.2019 / 13:51
 */
public class ItemLinkingTool extends Item implements IItemLinkingTool {

    public ItemLinkingTool() {
        super(new Properties()
                .maxStackSize(1)
                .group(CommonProxy.ITEM_GROUP_AS));
    }

    @Override
    public boolean shouldInterceptBlockInteract(LogicalSide side, PlayerEntity player, Hand hand, BlockPos pos, Direction face) {
        return true;
    }

    @Override
    public boolean shouldInterceptEntityInteract(LogicalSide side, PlayerEntity player, Hand hand, Entity interacted) {
        return interacted instanceof PlayerEntity;
    }

    @Override
    public boolean doBlockInteract(LogicalSide side, PlayerEntity player, Hand hand, BlockPos pos, Direction face) {
        World world = player.getEntityWorld();
        if (!world.isRemote()) {
            LinkHandler.LinkSession session = LinkHandler.getActiveSession(player);
            if (session != null && session.getType() == LinkHandler.LinkType.ENTITY) {
                LinkHandler.RightClickResult result = LinkHandler.onInteractBlock(player, world, pos, player.isSneaking());
                if (result.shouldProcess()) {
                    LinkHandler.processInteraction(result, player, world, pos);
                    return true;
                }
            }
            LinkHandler.RightClickResult result = LinkHandler.onInteractBlock(player, world, pos, player.isSneaking());
            if (result.shouldProcess()) {
                LinkHandler.processInteraction(result, player, world, pos);
            }
        } else {
            player.swingArm(hand);
        }
        return true;
    }

    @Override
    public boolean doEntityInteract(LogicalSide side, PlayerEntity player, Hand hand, Entity interacted) {
        if (!(interacted instanceof LivingEntity)) {
            return false;
        }
        LivingEntity target = (LivingEntity) interacted;
        World world = player.getEntityWorld();
        if (!world.isRemote()) {
            LinkHandler.LinkSession session = LinkHandler.getActiveSession(player);
            if (session == null || session.getType() == LinkHandler.LinkType.ENTITY) {
                LinkHandler.RightClickResult result = LinkHandler.onInteractEntity(player, target);
                if (result.shouldProcess()) {
                    LinkHandler.processInteraction(result, player, world, BlockPos.ZERO);
                }
            }
        } else {
            player.swingArm(hand);
        }
        return true;
    }
}
