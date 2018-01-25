/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool.sextant;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.common.item.base.ISpecialInteractItem;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.IMultiblockDependantTile;
import hellfirepvp.astralsorcery.common.util.struct.PatternBlockArray;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemSextant
 * Created by HellFirePvP
 * Date: 25.01.2018 / 18:42
 */
public class ItemSextant extends Item implements ISpecialInteractItem {

    public ItemSextant() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public boolean needsSpecialHandling(World world, BlockPos at, EntityPlayer player, ItemStack stack) {
        TileEntity te = world.getTileEntity(at);
        if(te != null && te instanceof IMultiblockDependantTile) {
            PatternBlockArray struct = ((IMultiblockDependantTile) te).getRequiredStructure();
            if(struct != null &&
                    !struct.matches(world, at)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onRightClick(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side, EnumHand hand, ItemStack stack) {
        TileEntity te = world.getTileEntity(pos);
        if(te != null && te instanceof IMultiblockDependantTile) {
            PatternBlockArray struct = ((IMultiblockDependantTile) te).getRequiredStructure();
            if(struct != null &&
                    !struct.matches(world, pos)) {
                if(world.isRemote) {
                    requestPreview(te);
                }
                return true;
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    private void requestPreview(TileEntity te) {
        EffectHandler.getInstance().requestStructurePreviewFor((IMultiblockDependantTile) te);
    }

}
