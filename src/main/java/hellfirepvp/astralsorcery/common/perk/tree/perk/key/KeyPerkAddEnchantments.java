/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree.perk.key;

import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.enchantment.EnchantmentModifier;
import hellfirepvp.astralsorcery.common.event.DynamicEnchantmentEvent;
import hellfirepvp.astralsorcery.common.lib.types.PerkDataTypesAS;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.tree.AttributeModifierPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkCategory;
import hellfirepvp.astralsorcery.common.perk.tree.PerkType;
import hellfirepvp.astralsorcery.common.perk.tree.perk.KeyPerk;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecProducts;
import hellfirepvp.astralsorcery.common.util.event.SidedEventBus;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: KeyPerkAddEnchantments
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class KeyPerkAddEnchantments extends KeyPerk {

    protected static <T extends KeyPerkAddEnchantments> Products.P9<RecordCodecBuilder.Mu<T>, ResourceLocation, String, Float, Float, PerkCategory, Set<PerkRequirement>, Set<PerkAttributeConverter>, Set<PerkAttributeModifier>, List<EnchantmentModifier>> perkKeyEnchantmentFields(RecordCodecBuilder.Instance<T> instance) {
        return CodecProducts.and(perkModifierFields(instance),
                EnchantmentModifier.CODEC.listOf().fieldOf("enchantments").forGetter(KeyPerkAddEnchantments::getEnchantments)
        );
    }

    public static final MapCodec<KeyPerkAddEnchantments> CODEC = RecordCodecBuilder.mapCodec(inst -> perkKeyEnchantmentFields(inst).apply(inst, KeyPerkAddEnchantments::new));
    public static final PerkType<KeyPerkAddEnchantments> TYPE =
            PerkType.of(KeyPerkAddEnchantments.CODEC, PerkDataTypesAS.DEFAULT_DATA, KeyPerkAddEnchantments::new);

    private final List<EnchantmentModifier> enchantments = new ArrayList<>();

    private KeyPerkAddEnchantments(ResourceLocation key, float x, float y) {
        this(key, defaultNameKey(key), x, y, PerkCategory.MAJOR, Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptyList());
    }

    protected KeyPerkAddEnchantments(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements, Collection<PerkAttributeConverter> converters, Collection<PerkAttributeModifier> modifiers, Collection<EnchantmentModifier> enchantments) {
        super(key, nameKey, x, y, category, requirements, converters, modifiers);
        this.enchantments.addAll(enchantments);
    }

    @Override
    protected void attachEventListeners(SidedEventBus sidedEventBus) {
        super.attachEventListeners(sidedEventBus);
        sidedEventBus.addListener(DynamicEnchantmentEvent.Add.class, SidedEventBus.entityEvent(), this::onAddDynamicEnchantments);
    }

    public <P extends KeyPerkAddEnchantments> P addEnchantment(EnchantmentModifier modifier) {
        this.enchantments.add(modifier);
        return MiscUtil.cast(this);
    }

    public List<EnchantmentModifier> getEnchantments() {
        return Collections.unmodifiableList(this.enchantments);
    }

    private void onAddDynamicEnchantments(DynamicEnchantmentEvent.Add event) {
        LogicalSide side = this.getSide(event.getEntity());
        PlayerProgress prog = ResearchManager.getProgress(event.getEntity(), side);
        if (!prog.getPerkData().hasPerkEffect(this)) return;

        this.getEnchantments().forEach(modifier -> event.getDynamicEnchantments().addModifier(modifier));
    }

    @Override
    protected boolean addTooltip(Collection<MutableComponent> tooltip, PlayerProgress progress, @Nullable Player player, LogicalSide side) {
        boolean addLine = super.addTooltip(tooltip, progress, player, side);
        if (!this.canSee(progress)) {
            return addLine;
        }
        this.getEnchantments().forEach(modifier -> {
            tooltip.add(modifier.getDisplay().withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        });

        return addLine || !this.getEnchantments().isEmpty();
    }

    @Override
    public PerkType<?> getType() {
        return TYPE;
    }
}
