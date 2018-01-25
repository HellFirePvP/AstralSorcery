/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wearable;

import com.google.common.collect.Multimap;
import hellfirepvp.astralsorcery.client.models.base.ASCape;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeEffectFactory;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeEffectRegistry;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectOctans;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerCapeEffects;
import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCape
 * Created by HellFirePvP
 * Date: 09.10.2017 / 23:08
 */
public class ItemCape extends ItemArmor implements ItemDynamicColor {

    private Object objASCape = null;

    public ItemCape() {
        super(RegistryItems.imbuedLeatherMaterial, -1, EntityEquipmentSlot.CHEST);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if(this.isInCreativeTab(tab)) {
            ItemStack stack;
            for (IConstellation c : ConstellationRegistry.getAllConstellations()) {
                if(c instanceof IMinorConstellation) continue;

                stack = new ItemStack(this);
                setAttunedConstellation(stack, c);
                items.add(stack);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if(Mods.DRACONICEVOLUTION.isPresent()) {
            float perc = Config.capeChaosResistance;
            if(perc > 0) {
                int displayPerc = MathHelper.floor(perc * 100);
                String out = I18n.format("misc.chaos.resistance", displayPerc + "%");
                if(perc >= 1) {
                    out = I18n.format("misc.chaos.resistance.max");
                }
                tooltip.add(TextFormatting.DARK_PURPLE + out);
            }
        }
        IConstellation cst = getAttunedConstellation(stack);
        if(cst != null) {
            String n = cst.getUnlocalizedName();
            n = I18n.format(n);
            tooltip.add(TextFormatting.BLUE + n);
        }
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        super.onArmorTick(world, player, itemStack);

        if(!world.isRemote) {
            CapeEffectOctans ceo = getCapeEffect(player, Constellations.octans);
            if(ceo != null && player.isInWater()) {
                NBTTagCompound perm = NBTHelper.getPersistentData(itemStack);
                perm.setInteger("AS_UpdateAttributes", itemRand.nextInt());
            }
        }
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> out = super.getAttributeModifiers(slot, stack);
        if(slot == EntityEquipmentSlot.CHEST) {
            IConstellation cst = getAttunedConstellation(stack);
            if(cst != null && cst == Constellations.octans) {
                CapeEffectOctans ceo = getCapeEffect(stack);
                if(ceo != null) {
                    EntityPlayer potentialCurrent = EventHandlerCapeEffects.currentPlayerInTick;
                    if(potentialCurrent != null && potentialCurrent.isInWater()) {
                        out.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(UUID.fromString("845DB25C-C624-495F-8C9F-60210A958B6B").toString(), 500, 0));
                    }
                }
            }
        }
        return out;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        if(EventHandlerCapeEffects.inElytraCheck) {
            return; //It shouldn't damage the vicio cape by flying with it.
        }
        super.setDamage(stack, damage);
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        if(objASCape == null) {
            objASCape = new ASCape();
        }
        return (ModelBiped) objASCape;
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "astralsorcery:textures/models/as_cape.png";
    }

    @Override
    public int getColorForItemStack(ItemStack stack, int tintIndex) {
        if(tintIndex != 1) return 0xFFFFFF;
        IConstellation cst = getAttunedConstellation(stack);
        if(cst != null) {
            Color c = cst.getConstellationColor();
            return 0xFF000000 | c.getRGB();
        }
        return 0xFF000000;
    }

    @Nullable
    public static CapeArmorEffect getCapeEffect(@Nullable EntityPlayer entity) {
        if(entity == null) return null;
        ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        IConstellation cst = getAttunedConstellation(stack);
        if(cst == null) {
            return null;
        }
        return getCapeEffect(stack);
    }

    @Nullable
    public static <V extends CapeArmorEffect> V getCapeEffect(@Nullable EntityPlayer entity, @Nonnull IConstellation expectedConstellation) {
        if(entity == null) return null;
        ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        IConstellation cst = getAttunedConstellation(stack);
        if(cst == null || cst != expectedConstellation) {
            return null;
        }
        return getCapeEffect(stack);
    }

    @Nullable
    public static <V extends CapeArmorEffect> V getCapeEffect(@Nonnull ItemStack stack) {
        IConstellation cst = getAttunedConstellation(stack);
        if(cst == null) {
            return null;
        }
        CapeEffectFactory<? extends CapeArmorEffect> call = CapeEffectRegistry.getArmorEffect(cst);
        if(call == null) {
            return null;
        }
        try {
            NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
            return (V) call.deserializeCapeEffect(cmp);
        } catch (Exception exc) {
            return null;
        }
    }

    @Nullable
    public static IConstellation getAttunedConstellation(@Nonnull ItemStack stack) {
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemCape)) {
            return null;
        }
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        return IConstellation.readFromNBT(cmp);
    }

    public static void setAttunedConstellation(@Nonnull ItemStack stack, @Nonnull IConstellation cst) {
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemCape)) {
            return;
        }
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        cst.writeToNBT(cmp);
    }

}
