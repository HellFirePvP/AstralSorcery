/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.useables;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.base.render.INBTModel;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.SoundHelper;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
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
 * Class: ItemShiftingStar
 * Created by HellFirePvP
 * Date: 09.02.2017 / 23:05
 */
public class ItemShiftingStar extends Item implements INBTModel {

    public ItemShiftingStar() {
        setMaxStackSize(1);
        setMaxDamage(0);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack, ModelResourceLocation suggestedDefaultLocation) {
        IMajorConstellation cst = getAttunement(stack);
        if (cst != null) {
            return new ModelResourceLocation(new ResourceLocation(suggestedDefaultLocation.getResourceDomain(), suggestedDefaultLocation.getResourcePath() + "_" + cst.getSimpleName()), suggestedDefaultLocation.getVariant());
        }
        return suggestedDefaultLocation;
    }

    @Override
    public List<ResourceLocation> getAllPossibleLocations(ModelResourceLocation defaultLocation) {
        List<ResourceLocation> all = Lists.newArrayList();
        all.add(defaultLocation);
        for (IMajorConstellation cst : ConstellationRegistry.getMajorConstellations()) {
            all.add(new ResourceLocation(defaultLocation.getResourceDomain(), defaultLocation.getResourcePath() + "_" + cst.getSimpleName()));
        }
        return all;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            items.add(new ItemStack(this));
            for (IMajorConstellation cst : ConstellationRegistry.getMajorConstellations()) {
                ItemStack star = new ItemStack(this);
                setAttunement(star, cst);
                items.add(star);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        IMajorConstellation cst;
        if ((cst = getAttunement(stack)) != null) {
            PlayerProgress prog = ResearchManager.clientProgress;
            if (prog != null) {
                if (prog.hasConstellationDiscovered(cst.getUnlocalizedName())) {
                    tooltip.add(TextFormatting.BLUE + I18n.format(cst.getUnlocalizedName()));
                } else {
                    tooltip.add(TextFormatting.GRAY + I18n.format("progress.missing.knowledge"));
                }
            }
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        String unloc = super.getUnlocalizedName(stack);
        if (getAttunement(stack) != null) {
            unloc += ".enhanced";
        }
        return unloc;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        playerIn.setActiveHand(hand);
        return super.onItemRightClick(worldIn, playerIn, hand);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        if(!worldIn.isRemote && entityLiving instanceof EntityPlayer) {
            EntityPlayer pl = (EntityPlayer) entityLiving;
            if(ResearchManager.setAttunedConstellation(pl, null)) {
                pl.sendMessage(new TextComponentTranslation("progress.remove.attunement").setStyle(new Style().setColor(TextFormatting.BLUE)));
                SoundHelper.playSoundAround(SoundEvents.BLOCK_GLASS_BREAK, worldIn, entityLiving.getPosition(), 1F, 1F);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        if(player.getEntityWorld().isRemote) {
            playEffects();
        }
    }

    @SideOnly(Side.CLIENT)
    private void playEffects() {
        EntityPlayer p = Minecraft.getMinecraft().player;
        for (int i = 0; i < 3; i++) {
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(p.posX, p.posY + p.getEyeHeight() / 2, p.posZ);
            particle.motion(-0.1 + itemRand.nextFloat() * 0.2, 0.01, -0.1 + itemRand.nextFloat() * 0.2);
            if(itemRand.nextInt(3) == 0) particle.setColor(Color.WHITE);
            particle.scale(0.3F);
            if(itemRand.nextInt(4) == 0) particle.scale(0.2F);
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 60;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    public static void setAttunement(ItemStack stack, IMajorConstellation cst) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemShiftingStar)) {
            return;
        }
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        cmp.setString("starAttunement", cst.getUnlocalizedName());
    }

    @Nullable
    public static IMajorConstellation getAttunement(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemShiftingStar)) {
            return null;
        }
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        if (!cmp.hasKey("starAttunement")) {
            return null;
        }
        return ConstellationRegistry.getMajorConstellationByName(cmp.getString("starAttunement"));
    }

}
