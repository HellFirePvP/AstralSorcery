/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.knowledge;

import hellfirepvp.astralsorcery.common.entities.EntityItemExplosionResistant;
import hellfirepvp.astralsorcery.common.item.base.ItemHighlighted;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.SoundHelper;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemFragmentCapsule
 * Created by HellFirePvP
 * Date: 27.10.2018 / 18:53
 */
public class ItemFragmentCapsule extends Item implements ItemHighlighted {

    public ItemFragmentCapsule() {
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
        EntityPlayer pl = Minecraft.getMinecraft().player;
        if (pl != null && isValid(pl, stack)) {
            tooltip.add(TextFormatting.GRAY + I18n.format("misc.fragment.container.desc"));
            tooltip.add(TextFormatting.GRAY + I18n.format("misc.fragment.container.open"));
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isRemote && (!(entityIn instanceof EntityPlayer) || !isValid((EntityPlayer) entityIn, stack))) {
            stack.setCount(0);
            SoundHelper.playSoundAround(SoundEvents.BLOCK_GLASS_BREAK, entityIn.getEntityWorld(), entityIn.getPosition(), 1.4F, 1F);
        }
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        if (!entityItem.world.isRemote) {
            ItemStack stack = entityItem.getItem();
            EntityPlayer thrower = entityItem.getThrower() == null ? null : entityItem.world.getPlayerEntityByName(entityItem.getThrower());
            if (thrower == null || !isValid(thrower, stack)) {
                SoundHelper.playSoundAround(SoundEvents.BLOCK_GLASS_BREAK, entityItem.getEntityWorld(), entityItem.getPosition(), 1.4F, 1F);
                entityItem.setDead();
                stack.setCount(0);
                entityItem.setItem(stack);
            }
        }
        return false;
    }

    @Override
    public Color getHightlightColor(ItemStack stack) {
        return new Color(0xCEEAFF);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return RegistryItems.rarityRelic;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            spawnFragment(player, hand);
        }
        return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            spawnFragment(player, hand);
        }
        return EnumActionResult.PASS;
    }

    private void spawnFragment(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (isValid(player, stack)) {
            SoundHelper.playSoundAround(SoundEvents.BLOCK_GLASS_BREAK, player.getEntityWorld(), player.getPosition(), 0.75F, 3.5F);
            ItemStack frag = new ItemStack(ItemsAS.knowledgeFragment);
            ItemKnowledgeFragment.generateSeed(player, frag);
            player.setHeldItem(hand, frag);
        } else {
            player.setHeldItem(hand, ItemStack.EMPTY);
        }
    }

    public static ItemStack generateCapsule(EntityPlayer player) {
        ItemStack stack = new ItemStack(ItemsAS.fragmentCapsule);
        NBTHelper.getPersistentData(stack).setUniqueId("pl", player.getUniqueID());
        return stack;
    }

    private static boolean isValid(EntityPlayer player, ItemStack capsule) {
        if (capsule.isEmpty() || !(capsule.getItem() instanceof ItemFragmentCapsule)) {
            return false;
        }
        NBTTagCompound cmp = NBTHelper.getPersistentData(capsule);
        return cmp.hasUniqueId("pl") && cmp.getUniqueId("pl").equals(player.getUniqueID());
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {}

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        EntityItemExplosionResistant e = new EntityItemExplosionResistant(world, location.posX, location.posY, location.posZ, itemstack);
        e.setDefaultPickupDelay();
        e.motionX = location.motionX;
        e.motionY = location.motionY;
        e.motionZ = location.motionZ;
        if (location instanceof EntityItem) {
            e.setThrower(((EntityItem) location).getThrower());
            e.setOwner(((EntityItem) location).getOwner());
        }
        return e;
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        return 300;
    }

}
