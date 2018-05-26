/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool.sextant;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.common.item.base.ISpecialInteractItem;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktDisplaySextantTarget;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.IMultiblockDependantTile;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.astralsorcery.common.util.struct.PatternBlockArray;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

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
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if(isInCreativeTab(tab)) {
            items.add(new ItemStack(this));
            ItemStack adv = new ItemStack(this);
            setAdvanced(adv);
            items.add(adv);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if(isAdvanced(stack)) {
            tooltip.add(TextFormatting.BLUE.toString() + I18n.format("item.itemsextant.upgraded"));
        }
    }

    public static boolean isAdvanced(ItemStack sextantStack) {
        if (sextantStack.isEmpty() || !(sextantStack.getItem() instanceof ItemSextant)) return false;
        return NBTHelper.getBoolean(NBTHelper.getPersistentData(sextantStack), "advanced", false);
    }

    public static void setAdvanced(ItemStack sextantStack) {
        if (sextantStack.isEmpty() || !(sextantStack.getItem() instanceof ItemSextant)) return;
        NBTHelper.getPersistentData(sextantStack).setBoolean("advanced", true);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn) {
        if(!worldIn.isRemote && AstralSorcery.isRunningInDevEnvironment() && worldIn instanceof WorldServer && player instanceof EntityPlayerMP && !MiscUtils.isPlayerFakeMP((EntityPlayerMP) player)) {
            SextantFinder.TargetObject to = SextantTargets.TARGET_VANILLA_MONUMENT;
            BlockPos result = to.searchFor((WorldServer) worldIn, player.getPosition());
            if(result != null) {
                PktDisplaySextantTarget target = new PktDisplaySextantTarget(to, result);
                PacketChannel.CHANNEL.sendTo(target, (EntityPlayerMP) player);
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(handIn));
        }
        return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(handIn));
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
                if(!world.isRemote && world instanceof WorldServer &&
                        entityPlayer.isCreative() && entityPlayer.isSneaking() &&
                        MiscUtils.isChunkLoaded(world, pos)) {
                    IBlockState current = world.getBlockState(pos);
                    struct.placeInWorld(world, pos);
                    if(!world.getBlockState(pos).equals(current)) {
                        world.setBlockState(pos, current);
                    }
                }
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
