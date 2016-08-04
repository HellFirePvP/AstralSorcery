package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.Tier;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.base.IMetaItem;
import hellfirepvp.astralsorcery.common.item.base.ItemHighlighted;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.ItemNBTHelper;
import hellfirepvp.astralsorcery.common.util.WRItemObject;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemConstellationPaper
 * Created by HellFirePvP
 * Date: 07.05.2016 / 15:16
 */
public class ItemConstellationPaper extends Item implements ItemHighlighted {

    public ItemConstellationPaper() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setHasSubtypes(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorceryPapers);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        subItems.add(new ItemStack(this, 1));

        for (Tier tier : ConstellationRegistry.ascendingTiers()) {
            for (Constellation c : tier.getConstellations()) {
                ItemStack cPaper = new ItemStack(this, 1);
                setConstellation(cPaper, c);
                subItems.add(cPaper);
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        Constellation c = getConstellation(stack);
        if (c != null) {
            tooltip.add(TextFormatting.BLUE + I18n.translateToLocal(c.getName()));
        } else {
            tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("constellation.noInformation"));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if(worldIn.isRemote && getConstellation(itemStackIn) != null) {
            playerIn.openGui(AstralSorcery.instance, 1, worldIn, ConstellationRegistry.getConstellationId(getConstellation(itemStackIn)), 0, 0);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isRemote || getConstellation(stack) != null) return;

        if (entityIn != null && entityIn instanceof EntityPlayer && (worldIn.getTotalWorldTime() & 7)  == 0) {
            PlayerProgress progress = ResearchManager.getProgress((EntityPlayer) entityIn);
            if (progress != null) {
                ProgressionTier highest = progress.getTierReached();
                List<Constellation> constellations = new ArrayList<>();
                for (Tier tier : ConstellationRegistry.ascendingTiers()) {
                    if (tier.getProgressionNeeded().ordinal() > highest.ordinal()) continue;
                    for (Constellation c : tier.getConstellations()) {
                        if (!progress.hasConstellationDiscovered(c.getName())) {
                            constellations.add(c);
                        }
                    }
                }

                removeInventoryConstellations(((EntityPlayer) entityIn).inventory, constellations);

                if (constellations.isEmpty()) {
                    return;
                }

                List<WRItemObject<Constellation>> wrp = buildWeightedRandomList(constellations);
                WRItemObject<Constellation> result = WeightedRandom.getRandomItem(worldIn.rand, wrp);
                setConstellation(stack, result.getValue());
            }
        }
    }

    private void removeInventoryConstellations(InventoryPlayer inventory, List<Constellation> constellations) {
        if (inventory == null) return;
        for (ItemStack stack : inventory.mainInventory) {
            if (stack == null || stack.getItem() == null) continue;
            if (stack.getItem() instanceof ItemConstellationPaper) {
                Constellation c = getConstellation(stack);
                if (c != null) {
                    constellations.remove(c);
                }
            }
        }
    }

    private List<WRItemObject<Constellation>> buildWeightedRandomList(List<Constellation> constellations) {
        List<WRItemObject<Constellation>> wrc = new ArrayList<WRItemObject<Constellation>>();
        for (Constellation c : constellations) {
            Tier tier = ConstellationRegistry.getTier(c.getAssociatedTier());
            if (tier == null) continue;
            WRItemObject<Constellation> i = new WRItemObject<>((int) (tier.getShowupChance() * 100), c);
            wrc.add(i);
        }
        return wrc;
    }

    @Override
    public Color getHightlightColor(ItemStack stack) {
        Constellation c = getConstellation(stack);
        return c == null ? Color.GRAY : c.queryTier().calcRenderColor();
    }

    public static Constellation getConstellation(ItemStack stack) {
        Item i = stack.getItem();
        if (!(i instanceof ItemConstellationPaper)) return null;
        return Constellation.readFromNBT(ItemNBTHelper.getPersistentData(stack));
    }

    public static void setConstellation(ItemStack stack, Constellation constellation) {
        Item i = stack.getItem();
        if (!(i instanceof ItemConstellationPaper)) return;
        NBTTagCompound tag = ItemNBTHelper.getPersistentData(stack);
        constellation.writeToNBT(tag);
    }

}
