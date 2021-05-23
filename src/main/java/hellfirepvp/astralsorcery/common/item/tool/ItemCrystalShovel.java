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
import hellfirepvp.astralsorcery.common.item.base.TypeEnchantableItem;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
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

import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCrystalShovel
 * Created by HellFirePvP
 * Date: 17.08.2019 / 18:26
 */
public class ItemCrystalShovel extends ItemCrystalTierItem implements TypeEnchantableItem {

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
        return 3;
    }

    @Override
    double getAttackSpeed() {
        return -1.5;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        BlockState state = world.getBlockState(pos);
        if (context.getFace() == Direction.DOWN) {
            return ActionResultType.PASS;
        } else {
            PlayerEntity playerentity = context.getPlayer();
            BlockState modifiedState = state.getToolModifiedState(world, pos, playerentity, context.getItem(), ToolType.SHOVEL);
            BlockState targetState = null;
            if (modifiedState != null && world.isAirBlock(pos.up())) {
                world.playSound(playerentity, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                targetState = modifiedState;
            } else if (state.getBlock() instanceof CampfireBlock && state.get(CampfireBlock.LIT)) {
                if (!world.isRemote()) {
                    world.playEvent(null, 1009, pos, 0);
                }

                CampfireBlock.extinguish(world, pos, state);
                targetState = state.with(CampfireBlock.LIT, false);
            }

            if (targetState != null) {
                if (!world.isRemote()) {
                    world.setBlockState(pos, targetState, 11);
                    if (playerentity != null) {
                        context.getItem().damageItem(1, playerentity, (player) -> {
                            player.sendBreakAnimation(context.getHand());
                        });
                    }
                }

                return ActionResultType.func_233537_a_(world.isRemote());
            } else {
                return ActionResultType.PASS;
            }
        }
    }
}
