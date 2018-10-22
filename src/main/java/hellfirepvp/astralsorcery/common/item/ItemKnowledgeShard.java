/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.client.data.KnowledgeFragmentData;
import hellfirepvp.astralsorcery.client.data.PersistentDataManager;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragment;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragmentManager;
import hellfirepvp.astralsorcery.common.entities.EntityItemHighlighted;
import hellfirepvp.astralsorcery.common.item.base.ItemHighlighted;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemKnowledgeShard
 * Created by HellFirePvP
 * Date: 21.10.2018 / 14:35
 */
public class ItemKnowledgeShard extends Item implements ItemHighlighted {

    public ItemKnowledgeShard() {
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {}

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

        if (!worldIn.isRemote &&
                !stack.isEmpty() &&
                stack.getItem() instanceof ItemKnowledgeShard &&
                !getSeed(stack).isPresent()) {
            long baseRand = (((entityIn.getEntityId() << 6) | itemSlot) << 16) | worldIn.getTotalWorldTime();
            Random r = new Random(baseRand);
            r.nextLong();
            setSeed(stack, r.nextLong());
        }
    }

    @Override
    public Color getHightlightColor(ItemStack stack) {
        return new Color(0x00BBFF);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity entity, ItemStack itemstack) {
        EntityItemHighlighted ei = new EntityItemHighlighted(world, entity.posX, entity.posY, entity.posZ, itemstack);
        ei.setDefaultPickupDelay();
        ei.motionX = entity.motionX;
        ei.motionY = entity.motionY;
        ei.motionZ = entity.motionZ;
        return ei;
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public static KnowledgeFragment resolveFragment(ItemStack stack) {
        Optional<Long> seedOpt = getSeed(stack);
        if (!seedOpt.isPresent()) return null;
        Random sRand = new Random(seedOpt.get());
        List<KnowledgeFragment> all = KnowledgeFragmentManager.getInstance().getAllFragments();
        KnowledgeFragmentData dat = PersistentDataManager.INSTANCE.getData(PersistentDataManager.PersistentKey.KNOWLEDGE_FRAGMENTS);
        all.removeAll(dat.getAllFragments());
        if (all.isEmpty()) return null;
        int index = sRand.nextInt(all.size());
        return all.get(index);
    }

    @SideOnly(Side.CLIENT)
    public static List<KnowledgeFragment> gatherFragments(EntityPlayer player) {
        Collection<ItemStack> fragItems = ItemUtils.findItemsInInventory(
                player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null),
                new ItemStack(ItemsAS.knowledgeShard),
                false);
        List<KnowledgeFragment> frags = new LinkedList<>();
        for (ItemStack stack : fragItems) {
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemKnowledgeShard)) continue;
            KnowledgeFragment fr = resolveFragment(stack);
            if (!frags.contains(fr)) {
                frags.add(fr);
            }
        }
        return frags;
    }

    public static void setSeed(ItemStack stack, long seed) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemKnowledgeShard)) return;
        NBTHelper.getPersistentData(stack).setLong("seed", seed);
    }

    public static Optional<Long> getSeed(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemKnowledgeShard)) return Optional.empty();
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        if (!cmp.hasKey("seed")) {
            return Optional.empty();
        }
        return Optional.of(cmp.getLong("seed"));
    }

}
