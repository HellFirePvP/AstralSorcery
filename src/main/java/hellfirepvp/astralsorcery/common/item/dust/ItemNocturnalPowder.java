/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.dust;

import hellfirepvp.astralsorcery.common.entity.EntityNocturnalSpark;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemNocturnalPowder
 * Created by HellFirePvP
 * Date: 17.08.2019 / 10:59
 */
public class ItemNocturnalPowder extends ItemUsableDust {

    @Override
    boolean dispense(IBlockSource dispenser) {
        BlockPos at = dispenser.getBlockPos();
        Direction face = dispenser.getBlockState().get(DispenserBlock.FACING);
        EntityNocturnalSpark nocSpark = new EntityNocturnalSpark(at.getX(), at.getY(), at.getZ(), dispenser.getWorld());
        nocSpark.shoot(face.getXOffset(), face.getYOffset() + 0.1F, face.getZOffset(), 0.7F, 0.9F);
        return dispenser.getWorld().addEntity(nocSpark);
    }

    @Override
    boolean rightClickAir(World world, PlayerEntity player, ItemStack dust) {
        return world.addEntity(new EntityNocturnalSpark(player, world));
    }

    @Override
    boolean rightClickBlock(ItemUseContext ctx) {
        BlockPos pos = ctx.getPos().offset(ctx.getFace());
        EntityNocturnalSpark noc = new EntityNocturnalSpark(ctx.getPlayer(), ctx.getWorld());
        noc.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        noc.setSpawning();
        return ctx.getWorld().addEntity(noc);
    }
}
