/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import com.google.common.collect.Sets;
import hellfirepvp.astralsorcery.common.item.base.TypeEnchantableItem;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCrystalAxe
 * Created by HellFirePvP
 * Date: 17.08.2019 / 18:10
 */
public class ItemCrystalAxe extends ItemCrystalTierItem implements TypeEnchantableItem {

    public ItemCrystalAxe() {
        super(ToolType.AXE, new Properties(), Sets.newHashSet(Material.WOOD, Material.PLANTS, Material.TALL_PLANTS, Material.BAMBOO, Material.LEAVES));
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> stacks) {
        if (this.isInGroup(group)) {
            ItemStack stack = new ItemStack(this);
            CrystalPropertiesAS.CREATIVE_CRYSTAL_TOOL_ATTRIBUTES.store(stack);
            stacks.add(stack);
        }
    }

    @Override
    public boolean canEnchantItem(ItemStack stack, EnchantmentType type) {
        return type == EnchantmentType.BREAKABLE || type == EnchantmentType.DIGGER;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        EnchantmentType type = enchantment.type;
        return type == EnchantmentType.DIGGER || type == EnchantmentType.BREAKABLE;
    }

    @Override
    double getAttackDamage() {
        return 11;
    }

    @Override
    double getAttackSpeed() {
        return -3;
    }

    @Override
    protected boolean isToolEfficientAgainst(BlockState state) {
        return state.getMaterial() == Material.LEAVES;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos blockpos = context.getPos();
        BlockState blockstate = world.getBlockState(blockpos);
        BlockState block = blockstate.getToolModifiedState(world, blockpos, context.getPlayer(), context.getItem(), ToolType.AXE);
        if (block != null) {
            PlayerEntity playerentity = context.getPlayer();
            world.playSound(playerentity, blockpos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!world.isRemote()) {
                world.setBlockState(blockpos, block, 11);
                if (playerentity != null) {
                    context.getItem().damageItem(1, playerentity, (stack) ->
                            stack.sendBreakAnimation(context.getHand()));
                }
            }

            return ActionResultType.func_233537_a_(world.isRemote());
        } else {
            return ActionResultType.PASS;
        }
    }
}
