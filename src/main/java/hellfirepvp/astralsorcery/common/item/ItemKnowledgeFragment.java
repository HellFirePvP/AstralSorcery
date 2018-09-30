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
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemKnowledgeFragment
 * Created by HellFirePvP
 * Date: 29.09.2018 / 15:20
 */
public class ItemKnowledgeFragment extends Item {

    public ItemKnowledgeFragment() {
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

        if (!worldIn.isRemote &&
                !stack.isEmpty() &&
                stack.getItem() instanceof ItemKnowledgeFragment &&
                !getSeed(stack).isPresent()) {
            long baseRand = (((entityIn.getEntityId() << 6) | itemSlot) << 16) | worldIn.getTotalWorldTime();
            Random r = new Random(baseRand);
            r.nextLong();
            setSeed(stack, r.nextLong());
        }
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
                new ItemStack(ItemsAS.knowledgeFragment),
                false);
        List<KnowledgeFragment> frags = new LinkedList<>();
        for (ItemStack stack : fragItems) {
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemKnowledgeFragment)) continue;
            KnowledgeFragment fr = resolveFragment(stack);
            if (!frags.contains(fr)) {
                frags.add(fr);
            }
        }
        return frags;
    }

    public static void setSeed(ItemStack stack, long seed) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemKnowledgeFragment)) return;
        NBTHelper.getPersistentData(stack).setLong("seed", seed);
    }

    public static Optional<Long> getSeed(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemKnowledgeFragment)) return Optional.empty();
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        if (!cmp.hasKey("seed")) {
            return Optional.empty();
        }
        return Optional.of(cmp.getLong("seed"));
    }

}
