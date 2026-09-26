/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.binding;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.component.IdentifierComponent;
import hellfirepvp.astralsorcery.common.component.StoredLumenComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.util.RandomMobEffectInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.common.asm.enumextension.ExtensionInfo;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingType
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenBindingType {

    public static final Codec<LumenBindingType> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.unboundedMap(StringRepresentable.fromEnum(SlotType::values), LumenBinding.CODEC).fieldOf("slot_bindings").forGetter(type -> type.slotBindings),
            RandomMobEffectInstance.CODEC.optionalFieldOf("potion_effect").forGetter(type -> Optional.ofNullable(type.potionEffect))
    ).apply(inst, LumenBindingType::new));

    private final Map<SlotType, LumenBinding> slotBindings = new HashMap<>();
    @Nullable
    private final RandomMobEffectInstance potionEffect;

    private LumenBindingType(Map<SlotType, LumenBinding> slotBindings, Optional<RandomMobEffectInstance> effect) {
        this.slotBindings.putAll(slotBindings);
        this.potionEffect = effect.orElse(null);
    }

    public static LumenBindingType of(Map<SlotType, LumenBinding> slotBindings, @Nullable RandomMobEffectInstance effect) {
        return new LumenBindingType(slotBindings, Optional.ofNullable(effect));
    }

    public Optional<LumenBinding> getBinding(ItemStack stack) {
        return this.slotBindings.entrySet().stream()
                .filter(entry -> entry.getKey().isSlotTypeFor(stack))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    public Optional<LumenBinding> getBinding(SlotType type) {
        return Optional.ofNullable(this.slotBindings.get(type));
    }

    public Optional<LumenBinding> getActiveBinding(ItemStack stack, LivingEntity entity) {
        return this.slotBindings.entrySet().stream()
                .filter(entry -> entry.getKey().isSlotTypeFor(stack))
                .filter(entry -> ItemStack.matches(entry.getKey().getStackFromEntity(entity), stack))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    public Optional<MobEffectInstance> getPotionEffect() {
        if (this.potionEffect == null) return Optional.empty();
        return Optional.of(this.potionEffect.createEffect(RandomSource.create()));
    }

    public static void applyBinding(Lumen lumen, ResourceLocation bindingTypeId, ItemStack stack) {
        StoredLumenComponent storedCmp = stack.getOrDefault(DataComponentsAS.STORED_LUMEN, StoredLumenComponent.EMPTY);
        stack.set(DataComponentsAS.STORED_LUMEN, storedCmp.applyBinding(lumen, bindingTypeId));
        IdentifierComponent.createOrOverwriteIdentifier(stack);
    }

    public enum SlotType implements StringRepresentable, IExtensibleEnum {

        HELMET(armorEquipment(EquipmentSlot.HEAD), wearing(EquipmentSlot.HEAD), Items.IRON_HELMET),
        CHESTPLATE(armorEquipment(EquipmentSlot.CHEST), wearing(EquipmentSlot.CHEST), Items.IRON_CHESTPLATE),
        LEGGINGS(armorEquipment(EquipmentSlot.LEGS), wearing(EquipmentSlot.LEGS), Items.IRON_LEGGINGS),
        BOOTS(armorEquipment(EquipmentSlot.FEET), wearing(EquipmentSlot.FEET), Items.IRON_BOOTS),
        MELEE_WEAPON(stack -> stack.is(ItemTags.WEAPON_ENCHANTABLE), wearing(EquipmentSlot.MAINHAND), Items.IRON_SWORD),
        RANGED_WEAPON(stack -> stack.is(ItemTags.BOW_ENCHANTABLE), wearing(EquipmentSlot.MAINHAND), Items.BOW),
        TOOL(stack -> stack.is(ItemTags.MINING_ENCHANTABLE), wearing(EquipmentSlot.MAINHAND), Items.IRON_PICKAXE);

        private final Predicate<ItemStack> stackPredicate;
        private final Function<LivingEntity, ItemStack> stackGetter;
        private final ItemStack displayStack;

        SlotType(Predicate<ItemStack> stackPredicate, Function<LivingEntity, ItemStack> stackGetter, ItemLike displayStack) {
            this(stackPredicate, stackGetter, new ItemStack(displayStack));
        }

        SlotType(Predicate<ItemStack> stackPredicate, Function<LivingEntity, ItemStack> stackGetter, ItemStack displayStack) {
            this.stackPredicate = stackPredicate;
            this.stackGetter = stackGetter;
            this.displayStack = displayStack;
        }

        private static Predicate<ItemStack> armorEquipment(EquipmentSlot slot) {
            return stack -> {
                if (stack.getEquipmentSlot() == slot) return true;
                Equipable equipable = Equipable.get(stack);
                if (equipable != null) {
                    return equipable.getEquipmentSlot() == slot;
                }
                return false; //skip hands
            };
        }

        private static Function<LivingEntity, ItemStack> wearing(EquipmentSlot slot) {
            return entity -> entity.getItemBySlot(slot);
        }

        public boolean isSlotTypeFor(ItemStack stack) {
            return this.stackPredicate.test(stack);
        }

        public ItemStack getStackFromEntity(LivingEntity entity) {
            return this.stackGetter.apply(entity);
        }

        public ItemStack getDisplayStack() {
            return this.displayStack.copy();
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public static ExtensionInfo getExtensionInfo() {
            return ExtensionInfo.nonExtended(SlotType.class);
        }
    }
}
