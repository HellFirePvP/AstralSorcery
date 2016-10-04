package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.data.research.EnumGatedKnowledge;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.base.ISpecialInteractItem;
import hellfirepvp.astralsorcery.common.item.base.IWandInteract;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemWand
 * Created by HellFirePvP
 * Date: 23.09.2016 / 12:57
 */
public class ItemWand extends Item implements ISpecialInteractItem {

    //private static final ParticleFirework.Factory pFactory = new ParticleFirework.Factory();

    public ItemWand() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    /*@Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);
        Block b = state.getBlock();
        if(b instanceof IWandInteract) {
            if(((IWandInteract) b).onInteract(worldIn, pos, playerIn, facing, playerIn.isSneaking())) {
                return EnumActionResult.SUCCESS;
            }
        }
        IWandInteract wandTe = MiscUtils.getTileAt(worldIn, pos, IWandInteract.class);
        if(wandTe != null) {
            if(wandTe.onInteract(worldIn, pos, playerIn, facing, playerIn.isSneaking())) {
                return EnumActionResult.SUCCESS;
            }
        }
        playerIn.swingArm(hand);
        return EnumActionResult.PASS;
    }*/

    @Override
    @SideOnly(Side.CLIENT)
    public String getUnlocalizedName(ItemStack stack) {
        EnumGatedKnowledge.ViewCapability cap = ResearchManager.clientProgress.getViewCapability();
        if(EnumGatedKnowledge.WAND_TYPE.canSee(cap)) {
            return super.getUnlocalizedName();
        }
        return "item.ItemWand.obf";
    }

    //TODO
    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(worldIn.isRemote) {
            //RockCrystalBuffer buf = WorldCacheManager.getOrLoadData(worldIn, WorldCacheManager.SaveKey.ROCK_CRYSTAL);

        }
    }

    @Override
    public boolean needsSpecialHandling(World world, BlockPos at, EntityPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    public void onRightClick(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side, EnumHand hand, ItemStack stack) {
        IBlockState state = world.getBlockState(pos);
        Block b = state.getBlock();
        if(b instanceof IWandInteract) {
            ((IWandInteract) b).onInteract(world, pos, entityPlayer, side, entityPlayer.isSneaking());
            return;
        }
        IWandInteract wandTe = MiscUtils.getTileAt(world, pos, IWandInteract.class, true);
        if(wandTe != null) {
            wandTe.onInteract(world, pos, entityPlayer, side, entityPlayer.isSneaking());
        }
    }

}
