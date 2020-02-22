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
    public boolean shouldInterceptInteract(LogicalSide side, PlayerEntity player, Hand hand, BlockPos pos, Direction face) {
        return true;
    }

    @Override
    public boolean doInteract(LogicalSide side, PlayerEntity player, Hand hand, BlockPos pos, Direction face) {
        World world = player.getEntityWorld();
        if (!world.isRemote()) {
            LinkHandler.RightClickResult result = LinkHandler.onRightClick(player, world, pos, player.isSneaking());
            LinkHandler.propagateClick(result, player, world, pos);
        } else {
            player.swingArm(hand);
        }
        return true;
    }
}
