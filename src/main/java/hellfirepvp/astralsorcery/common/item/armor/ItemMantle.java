/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.armor;

import com.google.common.collect.Multimap;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.model.armor.ModelArmorMantle;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffectProvider;
import hellfirepvp.astralsorcery.common.event.EventFlags;
import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.common.thread.EffectiveSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemMantle
 * Created by HellFirePvP
 * Date: 17.02.2020 / 19:09
 */
public class ItemMantle extends ArmorItem implements ItemDynamicColor {

    private static final UUID OCTANS_UNWAVERING = UUID.fromString("845DB25C-C624-495F-8C9F-60210A958B6B");
    private static Object modelArmor = null;

    public ItemMantle() {
        super(RegistryItems.ARMOR_MATERIAL_IMBUED_LEATHER,
                EquipmentSlotType.CHEST,
                new Properties()
                    .maxStackSize(1)
                    .group(RegistryItems.ITEM_GROUP_AS)
        );
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            items.add(new ItemStack(this));
            for (IConstellation cst : RegistriesAS.REGISTRY_CONSTELLATIONS.getValues()) {
                if (!(cst instanceof IWeakConstellation)) {
                    continue;
                }

                ItemStack stack = new ItemStack(this);
                this.setConstellation(stack, (IWeakConstellation) cst);
                items.add(stack);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        IWeakConstellation cst = this.getConstellation(stack);
        if (cst != null) {
            tooltip.add(new TranslationTextComponent(cst.getUnlocalizedName()).setStyle(new Style().setColor(TextFormatting.BLUE)));
        }
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex != 1) {
            return 0xFFFFFF;
        }
        IConstellation cst = getConstellation(stack);
        if (cst != null) {
            Color c = cst.getConstellationColor();
            return 0xFF000000 | c.getRGB();
        }
        return 0xFF000000;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        if (EventFlags.IN_ELYTRA_CHECK.isSet()) {
            return;
        }
        super.setDamage(stack, damage);
    }

    //TODO re-evaluate

    /*@Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        Multimap<String, AttributeModifier> out = super.getAttributeModifiers(slot, stack);
        if (slot == EquipmentSlotType.CHEST) {
            IWeakConstellation cst = this.getConstellation(stack);
            if (cst != null && cst.equals(ConstellationsAS.octans)) {
                PlayerEntity player = this.getServerPlayer(stack);
                if (player != null && player.isInWater()) {
                    out.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(),
                            new AttributeModifier(OCTANS_UNWAVERING, OCTANS_UNWAVERING.toString(), 500, AttributeModifier.Operation.ADDITION).setSaved(false));
                }
            }
        }
        return out;
    }*/

    @Override
    @Nullable
    @OnlyIn(Dist.CLIENT)
    public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
        if (modelArmor == null) {
            modelArmor = new ModelArmorMantle();
        }
        return (A) modelArmor;
    }

    @Override
    @Nullable
    @OnlyIn(Dist.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return AstralSorcery.key("textures/models/armor/mantle.png").toString();
    }

    @Nullable
    public <V extends MantleEffect> V getEffect(@Nullable PlayerEntity player) {
        return this.getEffect(player, null);
    }

    @Nullable
    public <V extends MantleEffect> V getEffect(@Nullable PlayerEntity player, @Nullable IWeakConstellation expected) {
        if (player == null) {
            return null;
        }
        ItemStack stack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        IWeakConstellation cst = this.getConstellation(stack);
        if (cst == null) {
            return null;
        }
        if (expected != null && !expected.equals(cst)) {
            return null;
        }
        return getEffect(stack);
    }

    @Nullable
    public <V extends MantleEffect> V getEffect(@Nonnull ItemStack stack) {
        IConstellation cst = this.getConstellation(stack);
        if (cst == null) {
            return null;
        }
        MantleEffectProvider provider = RegistriesAS.REGISTRY_MANTLE_EFFECT.getValue(cst.getRegistryName());
        if (provider == null) {
            return null;
        }
        try {
            CompoundNBT mantleData = NBTHelper.getPersistentData(stack).getCompound("mantleData");
            V effect = (V) provider.createEffect(mantleData);
            return effect.getConfig().enabled.get() ? effect : null;
        } catch (Exception exc) {
            AstralSorcery.log.error("Unable to create mantle effect!", exc);
            return null;
        }
    }

    @Nullable
    public IWeakConstellation getConstellation(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        return (IWeakConstellation) IConstellation.readFromNBT(NBTHelper.getPersistentData(stack));
    }

    public void setConstellation(ItemStack stack, @Nonnull IWeakConstellation cst) {
        if (stack.isEmpty()) {
            return;
        }
        cst.writeToNBT(NBTHelper.getPersistentData(stack));
    }
}
