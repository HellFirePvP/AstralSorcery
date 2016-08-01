package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.entities.EntityTelescope;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemTelescopePlacer
 * Created by HellFirePvP
 * Date: 08.05.2016 / 23:04
 */
public class ItemTelescopePlacer extends Item {

    public ItemTelescopePlacer() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setHasSubtypes(false);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (hand == null || hand == EnumHand.OFF_HAND)
            return EnumActionResult.PASS; //LUL rekt 1.9 features

        if (worldIn.isRemote) return EnumActionResult.PASS;
        BlockPos positionSuggested = pos.offset(facing);
        if (worldIn.isAirBlock(positionSuggested)) {
            EntityTelescope telescope = new EntityTelescope(worldIn);
            telescope.setPositionAndRotation(
                    positionSuggested.getX() + 0.5, positionSuggested.getY(), positionSuggested.getZ() + 0.5,
                    0.5F, 0.5F);
            worldIn.spawnEntityInWorld(telescope);

            if (!playerIn.isCreative()) {
                stack.stackSize -= 1;
            }
        }
        return EnumActionResult.PASS;
    }
}
