/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.armor;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.model.armor.ModelArmorMantle;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.ConstellationBaseItem;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.item.base.AlignmentChargeConsumer;
import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.modifier.DynamicAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.source.provider.equipment.EquipmentAttributeModifierProvider;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemMantle
 * Created by HellFirePvP
 * Date: 17.02.2020 / 19:09
 */
public class ItemMantle extends ArmorItem implements ItemDynamicColor, ConstellationBaseItem, EquipmentAttributeModifierProvider, AlignmentChargeConsumer {

    private static final DynamicAttributeModifier EVORSIO_MANTLE_MINING_SIZE =
            new DynamicAttributeModifier(UUID.fromString("aae54b9d-e1c8-4e74-8ac6-efa06093bd1a"), PerkAttributeTypesAS.ATTR_TYPE_MINING_SIZE, ModifierType.ADDITION, 2F);

    private static Object modelArmor = null;

    public ItemMantle() {
        super(CommonProxy.ARMOR_MATERIAL_IMBUED_LEATHER,
                EquipmentSlotType.CHEST,
                new Properties()
                    .maxStackSize(1)
                    .group(CommonProxy.ITEM_GROUP_AS)
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
                this.setConstellation(stack, cst);
                items.add(stack);
            }
        }
    }

    @Override
    public Collection<PerkAttributeModifier> getModifiers(ItemStack stack, PlayerEntity player, LogicalSide side, boolean ignoreRequirements) {
        if (ItemMantle.getEffect(stack, ConstellationsAS.evorsio) == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(EVORSIO_MANTLE_MINING_SIZE);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        IConstellation cst = this.getConstellation(stack);
        if (cst instanceof IWeakConstellation) {
            tooltip.add(cst.getConstellationName().applyTextStyle(TextFormatting.BLUE));
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
        return AstralSorcery.key("textures/model/armor/mantle.png").toString();
    }

    @Nullable
    public static <V extends MantleEffect> V getEffect(@Nullable LivingEntity entity) {
        return getEffect(entity, null);
    }

    @Nullable
    public static <V extends MantleEffect> V getEffect(@Nullable LivingEntity entity, @Nullable IWeakConstellation expected) {
        if (entity == null) {
            return null;
        }
        ItemStack stack = entity.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemMantle)) {
            return null;
        }
        return getEffect(stack, expected);
    }

    @Nullable
    public static <V extends MantleEffect> V getEffect(@Nonnull ItemStack stack, @Nullable IWeakConstellation expected) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemMantle)) {
            return null;
        }
        IConstellation cst = ((ItemMantle) stack.getItem()).getConstellation(stack);
        if (!(cst instanceof IWeakConstellation)) {
            return null;
        }
        if (expected != null && !expected.equals(cst)) {
            return null;
        }
        MantleEffect effect = ((IWeakConstellation) cst).getMantleEffect();
        if (effect == null || !effect.getConfig().enabled.get()) {
            return null;
        }
        return (V) effect;
    }

    @Nullable
    @Override
    public IConstellation getConstellation(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        return IConstellation.readFromNBT(NBTHelper.getPersistentData(stack), IConstellation.getDefaultSaveKey());
    }

    @Override
    public boolean setConstellation(ItemStack stack, @Nullable IConstellation cst) {
        if (stack.isEmpty()) {
            return false;
        }
        if (cst == null) {
            NBTHelper.getPersistentData(stack).remove(IConstellation.getDefaultSaveKey());
        } else {
            cst.writeToNBT(NBTHelper.getPersistentData(stack), IConstellation.getDefaultSaveKey());
        }
        return true;
    }

    @Override
    public float getAlignmentChargeCost(PlayerEntity player, ItemStack stack) {
        return 0;
    }
}
