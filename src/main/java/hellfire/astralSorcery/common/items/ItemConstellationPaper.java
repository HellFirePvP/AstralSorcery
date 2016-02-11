package hellfire.astralSorcery.common.items;

import hellfire.astralSorcery.api.constellation.IConstellation;
import hellfire.astralSorcery.api.constellation.IConstellationTier;
import hellfire.astralSorcery.common.constellation.ConstellationRegistry;
import hellfire.astralSorcery.common.lib.LibMisc;
import hellfire.astralSorcery.common.util.ItemNBTHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 09.02.2016 11:40
 */
public class ItemConstellationPaper extends Item {

    public ItemConstellationPaper() {
        setUnlocalizedName("ItemConstellationPaper");
        setMaxDamage(0);
        setMaxStackSize(1);
        setHasSubtypes(true);
        setCreativeTab(LibMisc.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (IConstellationTier tier : ConstellationRegistry.ascendingTiers()) {
            for(IConstellation c : tier.getConstellations()) {
                ItemStack cPaper = new ItemStack(this, 1);
                setConstellation(cPaper, c);
                subItems.add(cPaper);
            }
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return LibMisc.rarityOld;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        IConstellation c = getConstellation(stack);
        if(c != null) {
            String s = String.format(IConstellation.CONSTELLATION_TRANSLATOR, c.getName());
            s = StatCollector.translateToLocal(s);
            tooltip.add(EnumChatFormatting.BLUE + s);
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public static IConstellation getConstellation(ItemStack stack) {
        Item i = stack.getItem();
        if(!(i instanceof ItemConstellationPaper)) return null;
        String name = ItemNBTHelper.getPersistentData(stack).getString("constellation");
        if(name == null || name.isEmpty()) return null;
        return ConstellationRegistry.getConstellationByName(name);
    }

    public static void setConstellation(ItemStack stack, IConstellation constellation) {
        Item i = stack.getItem();
        if(!(i instanceof ItemConstellationPaper)) return;
        String name = constellation.getName();
        if(name == null) return;
        ItemNBTHelper.getPersistentData(stack).setString("constellation", name);
    }

}
