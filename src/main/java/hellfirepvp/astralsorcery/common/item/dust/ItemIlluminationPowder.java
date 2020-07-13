/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.dust;

import hellfirepvp.astralsorcery.common.entity.EntityIlluminationSpark;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemIlluminationPowder
 * Created by HellFirePvP
 * Date: 17.08.2019 / 13:54
 */
public class ItemIlluminationPowder extends ItemUsableDust {

    @Override
    boolean dispense(IBlockSource dispenser) {
        BlockPos at = dispenser.getBlockPos();
        Direction face = dispenser.getBlockState().get(DispenserBlock.FACING);
        EntityIlluminationSpark nocSpark = new EntityIlluminationSpark(at.getX(), at.getY(), at.getZ(), dispenser.getWorld());
        nocSpark.shoot(face.getXOffset(), face.getYOffset() + 0.1F, face.getZOffset(), 0.7F, 0.9F);
        return dispenser.getWorld().addEntity(nocSpark);
    }

    @Override
    boolean rightClickAir(World world, PlayerEntity player, ItemStack dust) {
        return world.addEntity(new EntityIlluminationSpark(player, world));
    }

    @Override
    boolean rightClickBlock(ItemUseContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getPos();
        PlayerEntity player = ctx.getPlayer();
        if (player == null) {
            return false;
        }

        if (!BlockUtils.isReplaceable(world, pos)) {
            pos = pos.offset(ctx.getFace());
        }

        if (player.canPlayerEdit(pos, ctx.getFace(), ctx.getItem()) && !ForgeEventFactory.onBlockPlace(player, BlockSnapshot.getBlockSnapshot(world, pos), ctx.getFace())) {
            return world.setBlockState(pos, BlocksAS.FLARE_LIGHT.getDefaultState());
        }
        return false;
    }
}
