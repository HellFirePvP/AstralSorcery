/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.GuiType;
import hellfirepvp.astralsorcery.common.constellation.ConstellationBaseItem;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.container.factory.ContainerTomeProvider;
import hellfirepvp.astralsorcery.common.item.base.PerkExperienceRevealer;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemTome
 * Created by HellFirePvP
 * Date: 09.08.2019 / 21:12
 */
public class ItemTome extends Item implements PerkExperienceRevealer {

    public ItemTome() {
        super(new Properties()
                .maxStackSize(1)
                .group(CommonProxy.ITEM_GROUP_AS));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (world.isRemote() && !player.isSneaking()) {
            AstralSorcery.getProxy().openGui(player, GuiType.TOME);
        } else if (!world.isRemote() && player.isSneaking() && hand == Hand.MAIN_HAND && player instanceof ServerPlayerEntity) {
            new ContainerTomeProvider(player.getHeldItem(hand), player.inventory.currentItem)
                    .openFor((ServerPlayerEntity) player);
        }
        return ActionResult.resultSuccess(player.getHeldItem(hand));
    }

    public static IInventory getTomeStorage(ItemStack stack) {
        Inventory i = new Inventory(27);
        ItemStack[] toFill = getStoredConstellationStacks(stack);
        for (int i1 = 0; i1 < toFill.length; i1++) {
            ItemStack item = toFill[i1];
            i.setInventorySlotContents(i1, item);
        }
        return i;
    }

    public static ItemStack[] getStoredConstellationStacks(ItemStack stack) {
        List<IConstellation> out = getStoredConstellations(stack);
        ItemStack[] items = new ItemStack[out.size()];
        for (int i = 0; i < out.size(); i++) {
            IConstellation c = out.get(i);
            ItemStack paper = new ItemStack(ItemsAS.CONSTELLATION_PAPER);
            if (paper.getItem() instanceof ConstellationBaseItem) {
                ((ConstellationBaseItem) paper.getItem()).setConstellation(paper, c);
            }
            items[i] = paper;
        }
        return items;
    }

    public static List<IConstellation> getStoredConstellations(ItemStack stack) {
        CompoundNBT cmp = NBTHelper.getPersistentData(stack);
        ListNBT constellationPapers = cmp.getList("constellations", Constants.NBT.TAG_STRING);
        LinkedList<IConstellation> out = new LinkedList<>();
        for (int i = 0; i < constellationPapers.size(); i++) {
            IConstellation c = RegistriesAS.REGISTRY_CONSTELLATIONS.getValue(new ResourceLocation(constellationPapers.getString(i)));
            if (c != null) {
                out.add(c);
            }
        }
        return out;
    }

    public static void setStoredConstellations(ItemStack parentJournal, LinkedList<IConstellation> saveConstellations) {
        CompoundNBT cmp = NBTHelper.getPersistentData(parentJournal);
        ListNBT list = new ListNBT();
        for (IConstellation c : saveConstellations) {
            list.add(StringNBT.valueOf(c.getRegistryName().toString()));
        }
        cmp.put("constellations", list);
    }
}
