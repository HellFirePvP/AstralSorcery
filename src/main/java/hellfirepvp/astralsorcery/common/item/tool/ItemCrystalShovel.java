/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.Constants;

import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCrystalShovel
 * Created by HellFirePvP
 * Date: 17.08.2019 / 18:26
 */
public class ItemCrystalShovel extends ItemCrystalTierItem {

    private static final Map<Block, BlockState> BLOCK_PAVE_MAP = new ImmutableMap.Builder<Block, BlockState>()
            .put(Blocks.GRASS_BLOCK, Blocks.GRASS_PATH.getDefaultState())
            .build();

    public ItemCrystalShovel() {
        super(ToolType.SHOVEL, new Properties(), Sets.newHashSet(Material.SNOW, Material.SNOW_BLOCK));
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
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        EnchantmentType type = enchantment.type;
        return type == EnchantmentType.ALL || type == EnchantmentType.DIGGER || type == EnchantmentType.BREAKABLE;
    }

    @Override
    double getAttackDamage() {
        return 3;
    }

    @Override
    double getAttackSpeed() {
        return -1.5;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        World world = ctx.getWorld();
        BlockPos blockpos = ctx.getPos();
        if (ctx.getFace() != Direction.DOWN && world.getBlockState(blockpos.up()).isAir(world, blockpos.up())) {
            BlockState blockstate = BLOCK_PAVE_MAP.get(world.getBlockState(blockpos).getBlock());
            if (blockstate != null) {
                PlayerEntity playerentity = ctx.getPlayer();
                world.playSound(playerentity, blockpos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!world.isRemote()) {
                    if (world.setBlockState(blockpos, blockstate, Constants.BlockFlags.DEFAULT_AND_RERENDER) &&
                            playerentity != null) {
                        ctx.getItem().damageItem(1, playerentity,
                                (e) -> e.sendBreakAnimation(ctx.getHand()));
                    }
                }
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }
}
