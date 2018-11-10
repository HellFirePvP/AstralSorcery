/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.knowledge;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.data.KnowledgeFragmentData;
import hellfirepvp.astralsorcery.client.data.PersistentDataManager;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.MoonPhase;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragment;
import hellfirepvp.astralsorcery.common.entities.EntityItemHighlighted;
import hellfirepvp.astralsorcery.common.item.base.ItemHighlighted;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.client.PktRemoveKnowledgeFragment;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
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
 * Class: ItemKnowledgeFragment
 * Created by HellFirePvP
 * Date: 21.10.2018 / 14:35
 */
public class ItemKnowledgeFragment extends Item implements ItemHighlighted {

    public ItemKnowledgeFragment() {
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {}

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return RegistryItems.rarityRelic;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
        if (getSeed(stack).isPresent()) {
            if (hasConstellationInformation(stack)) {
                tooltip.add(TextFormatting.GRAY + I18n.format("misc.fragment.constellation.desc.1"));
                tooltip.add(TextFormatting.GRAY + I18n.format("misc.fragment.constellation.desc.2"));
            } else if(resolveFragment(stack) != null) {
                tooltip.add(TextFormatting.GRAY + I18n.format("misc.fragment.content.desc.1"));
                tooltip.add(TextFormatting.GRAY + I18n.format("misc.fragment.content.desc.2"));
            } else {
                tooltip.add(TextFormatting.GRAY + I18n.format("misc.fragment.content.empty"));
            }
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isRemote &&
                !stack.isEmpty() &&
                stack.getItem() instanceof ItemKnowledgeFragment &&
                !getSeed(stack).isPresent()) {
            stack.setCount(0);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote) {
            ItemStack stack = player.getHeldItem(hand);
            if (hasConstellationInformation(stack)) {
                player.openGui(AstralSorcery.instance, CommonProxy.EnumGuiId.KNOWLEDGE_CONSTELLATION.ordinal(), world, 0, 0, 0);
                return ActionResult.newResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
            } else {
                KnowledgeFragment frag = resolveFragment(stack);
                if (frag != null) {
                    ItemKnowledgeFragment.clearFragment(player, frag);
                    KnowledgeFragmentData dat = PersistentDataManager.INSTANCE.getData(PersistentDataManager.PersistentKey.KNOWLEDGE_FRAGMENTS);
                    if (dat.addFragment(frag)) {
                        player.sendMessage(new TextComponentString(
                                TextFormatting.GREEN +
                                        I18n.format("misc.fragment.added", frag.getLocalizedIndexName())));
                    }
                }
            }
        }
        return ActionResult.newResult(EnumActionResult.PASS, player.getHeldItem(hand));
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return EnumActionResult.PASS;
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        if (!entityItem.world.isRemote) {
            ItemStack stack = entityItem.getItem();
            if (!stack.isEmpty() &&
                    stack.getItem() instanceof ItemKnowledgeFragment &&
                    !getSeed(stack).isPresent()) {
                entityItem.setDead();
                stack.setCount(0);
                entityItem.setItem(stack);
            }
        }
        return false;
    }

    static void generateSeed(EntityPlayer player, ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof ItemKnowledgeFragment) {
            long baseRand = (((player.getEntityId() << 6) | (System.currentTimeMillis() & 223)) << 16) | player.getEntityWorld().getTotalWorldTime();
            Random r = new Random(baseRand);
            r.nextLong();
            setSeed(stack, r.nextLong());
        }
    }

    @Override
    public Color getHightlightColor(ItemStack stack) {
        return new Color(0xCEEAFF);
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
        if (entity instanceof EntityItem) {
            ei.setThrower(((EntityItem) entity).getThrower());
            ei.setOwner(((EntityItem) entity).getOwner());
        }
        return ei;
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public static Tuple<IConstellation, List<MoonPhase>> getConstellationInformation(ItemStack stack) {
        KnowledgeFragment frag = resolveFragment(stack);
        if (frag != null) {
            Optional<Long> seedOpt = getSeed(stack);
            if (seedOpt.isPresent()) {
                long seed = seedOpt.get();
                IConstellation cst = frag.getDiscoverConstellation(seed);
                List<MoonPhase> phases = frag.getShowupPhases(seed);
                if (cst != null && !phases.isEmpty()) {
                    return new Tuple<>(cst, phases);
                }
            }
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public static boolean hasConstellationInformation(ItemStack stack) {
        return getConstellationInformation(stack) != null;
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public static KnowledgeFragment resolveFragment(ItemStack stack) {
        Optional<Long> seedOpt = getSeed(stack);
        if (!seedOpt.isPresent()) return null;
        Random sRand = new Random(seedOpt.get());
        KnowledgeFragmentData dat = PersistentDataManager.INSTANCE.getData(PersistentDataManager.PersistentKey.KNOWLEDGE_FRAGMENTS);
        List<KnowledgeFragment> all = dat.getDiscoverableFragments();
        all.removeIf(f -> !f.isFullyPresent());
        if (all.isEmpty()) return null;
        int index = sRand.nextInt(all.size());
        return all.get(index);
    }

    @SideOnly(Side.CLIENT)
    public static List<ItemStack> gatherFragments(EntityPlayer player) {
        Collection<ItemStack> fragItems = ItemUtils.findItemsInInventory(
                player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null),
                new ItemStack(ItemsAS.knowledgeFragment),
                false);
        List<ItemStack> frags = new LinkedList<>();
        for (ItemStack stack : fragItems) {
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemKnowledgeFragment)) continue;
            KnowledgeFragment fr = resolveFragment(stack);
            if (fr != null) {
                frags.add(stack);
            }
        }
        return frags;
    }

    @SideOnly(Side.CLIENT)
    public static void clearFragment(EntityPlayer player, KnowledgeFragment frag) {
        Map<Integer, ItemStack> fragItems = ItemUtils.findItemsIndexedInInventory(
                player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null),
                new ItemStack(ItemsAS.knowledgeFragment),
                false);
        for (Map.Entry<Integer, ItemStack> entry : fragItems.entrySet()) {
            ItemStack stack = entry.getValue();
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemKnowledgeFragment)) continue;
            KnowledgeFragment fr = resolveFragment(stack);
            if (fr != null && fr.equals(frag)) {
                PacketChannel.CHANNEL.sendToServer(new PktRemoveKnowledgeFragment(entry.getKey()));
                player.inventory.setInventorySlotContents(entry.getKey(), ItemStack.EMPTY);
                break;
            }
        }
    }


    public static void setSeed(ItemStack stack, long seed) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemKnowledgeFragment)) return;
        NBTHelper.getPersistentData(stack).setLong("seed", seed);
    }

    public static Optional<Long> getSeed(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemKnowledgeFragment)) {
            return Optional.empty();
        }
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        if (!cmp.hasKey("seed")) {
            return Optional.empty();
        }
        return Optional.of(cmp.getLong("seed"));
    }

}
