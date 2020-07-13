/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.base;

import hellfirepvp.astralsorcery.common.constellation.ConstellationItem;
import hellfirepvp.astralsorcery.common.constellation.ConstellationTile;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributeItem;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributeTile;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCrystalContainer
 * Created by HellFirePvP
 * Date: 30.09.2019 / 18:02
 */
public abstract class BlockCrystalContainer extends ContainerBlock {

    protected BlockCrystalContainer(Properties builder) {
        super(builder);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        ItemStack stack = super.getPickBlock(state, target, world, pos, player);
        if (stack.getItem() instanceof CrystalAttributeItem) {
            CrystalAttributeTile cat = MiscUtils.getTileAt(world, pos, CrystalAttributeTile.class, true);
            if (cat != null) {
                ((CrystalAttributeItem) stack.getItem()).setAttributes(stack, cat.getAttributes());
            }
        }
        if (stack.getItem() instanceof ConstellationItem) {
            ConstellationTile ct = MiscUtils.getTileAt(world, pos, ConstellationTile.class, true);
            if (ct != null) {
                ((ConstellationItem) stack.getItem()).setAttunedConstellation(stack, ct.getAttunedConstellation());
                ((ConstellationItem) stack.getItem()).setTraitConstellation(stack, ct.getTraitConstellation());
            }
        }
        return stack;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        Item i = stack.getItem();
        if (i instanceof CrystalAttributeItem) {
            CrystalAttributeTile cat = MiscUtils.getTileAt(world, pos, CrystalAttributeTile.class, true);
            if (cat != null) {
                cat.setAttributes(((CrystalAttributeItem) i).getAttributes(stack));
            }
        }
        if (i instanceof ConstellationItem) {
            ConstellationTile ct = MiscUtils.getTileAt(world, pos, ConstellationTile.class, true);
            if (ct != null) {
                ct.setAttunedConstellation(((ConstellationItem) i).getAttunedConstellation(stack));
                ct.setTraitConstellation(((ConstellationItem) i).getTraitConstellation(stack));
            }
        }
    }

}
