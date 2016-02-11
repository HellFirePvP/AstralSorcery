package hellfire.astralSorcery.common.items;

import hellfire.astralSorcery.api.constellation.IConstellation;
import hellfire.astralSorcery.api.constellation.IConstellationTier;
import hellfire.astralSorcery.common.constellation.ConstellationRegistry;
import hellfire.astralSorcery.common.data.research.PlayerProgress;
import hellfire.astralSorcery.common.data.research.ResearchManager;
import hellfire.astralSorcery.common.lib.LibMisc;
import hellfire.astralSorcery.common.util.ItemNBTHelper;
import hellfire.astralSorcery.common.util.WRItemObject;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
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
        subItems.add(new ItemStack(itemIn, 1));

        for (IConstellationTier tier : ConstellationRegistry.ascendingTiers()) {
            for(IConstellation c : tier.getConstellations()) {
                //if(ResearchManager.clientProgress.hasConstellationDiscovered(c.getName())) continue; //We don't add unnecessary stuff TODO change back later
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
        if(stack.getItemDamage() == 1) {
            tooltip.add(EnumChatFormatting.GRAY + "No useful information");
            return;
        }
        IConstellation c = getConstellation(stack);
        if(c != null) {
            String s = String.format(IConstellation.CONSTELLATION_TRANSLATOR, c.getName());
            s = StatCollector.translateToLocal(s);
            tooltip.add(EnumChatFormatting.BLUE + s);
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(worldIn.isRemote || getConstellation(stack) != null) return;

        if(entityIn != null && entityIn instanceof EntityPlayer) {
            PlayerProgress progress = ResearchManager.getProgress((EntityPlayer) entityIn);
            if(progress != null) {
                int highest = progress.getTierReached();
                List<IConstellation> constellations = new ArrayList<IConstellation>();
                for(IConstellationTier tier : ConstellationRegistry.ascendingTiers()) {
                    if(tier.tierNumber() > highest) continue;
                    for (IConstellation c : tier.getConstellations()) {
                        if(!progress.hasConstellationDiscovered(c.getName())) constellations.add(c);
                    }
                }

                removeInventoryConstellations(((EntityPlayer) entityIn).inventory, constellations);

                if(constellations.isEmpty()) {
                    stack.setItemDamage(1);
                    return;
                }

                Collection<WRItemObject<IConstellation>> wrp = buildWeightedRandomList(constellations);
                WRItemObject<IConstellation> result = WeightedRandom.getRandomItem(worldIn.rand, wrp);
                if(worldIn.rand.nextBoolean()) {
                    stack.setItemDamage(1);
                } else {
                    setConstellation(stack, result.getValue());
                }
            }
        }
    }

    private void removeInventoryConstellations(InventoryPlayer inventory, List<IConstellation> constellations) {
        if(inventory == null) return;
        for (ItemStack stack : inventory.mainInventory) {
            if(stack == null || stack.getItem() == null) continue;
            if(stack.getItem() instanceof ItemConstellationPaper) {
                IConstellation c = getConstellation(stack);
                if(c != null) {
                    constellations.remove(c);
                }
            }
        }
    }

    private Collection<WRItemObject<IConstellation>> buildWeightedRandomList(List<IConstellation> constellations) {
        Collection<WRItemObject<IConstellation>> wrc = new ArrayList<WRItemObject<IConstellation>>();
        for (IConstellation c : constellations) {
            IConstellationTier tier = ConstellationRegistry.getTier(c.getAssociatedTier());
            if(tier == null) continue;
            WRItemObject<IConstellation> i = new WRItemObject<IConstellation>((int) (tier.getShowupChance() * 100), c);
            wrc.add(i);
        }
        return wrc;
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
