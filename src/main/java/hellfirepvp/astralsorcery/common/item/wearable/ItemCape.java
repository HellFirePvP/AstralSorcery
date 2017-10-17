/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wearable;

import com.google.common.collect.Multimap;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeEffectFactory;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeEffectRegistry;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectOctans;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerCapeEffects;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCape
 * Created by HellFirePvP
 * Date: 09.10.2017 / 23:08
 */
public class ItemCape extends ItemArmor {

    public ItemCape() {
        super(RegistryItems.imbuedLeatherMaterial, -1, EntityEquipmentSlot.CHEST);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if(this.isInCreativeTab(tab)) {
            ItemStack stack;
            for (IConstellation c : ConstellationRegistry.getAllConstellations()) {
                stack = new ItemStack(this);
                setAttunedConstellation(stack, c);
                items.add(stack);
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        IConstellation cst = getAttunedConstellation(stack);
        if(cst != null) {
            tooltip.add(cst.getUnlocalizedName());
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
