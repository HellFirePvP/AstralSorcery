/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree;

import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.types.PerkDataTypesAS;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.reader.PerkAttributeTypeReader;
import hellfirepvp.astralsorcery.common.perk.source.AttributeModifierProvider;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AttributeModifierPerk
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AttributeModifierPerk<D extends AbstractPerk.Data> extends AttributeConverterPerk<D> implements AttributeModifierProvider {

    protected static <T extends AttributeModifierPerk<?>> Products.P8<RecordCodecBuilder.Mu<T>, ResourceLocation, String, Float, Float, PerkCategory, Set<PerkRequirement>, Set<PerkAttributeConverter>, Set<PerkAttributeModifier>> perkModifierFields(RecordCodecBuilder.Instance<T> instance) {
        return perkConverterFields(instance).and(
                SetCodec.of(PerkAttributeModifier.CODEC).fieldOf("modifiers").forGetter(AttributeModifierPerk::getModifiers)
        );
    }
    public static final MapCodec<AttributeModifierPerk<?>> CODEC = RecordCodecBuilder.mapCodec(inst -> perkModifierFields(inst).apply(inst, AttributeModifierPerk::new));
    public static final PerkType<AttributeModifierPerk<?>> TYPE =
            PerkType.of(AttributeModifierPerk.CODEC, PerkDataTypesAS.DEFAULT_DATA, AttributeModifierPerk::new);

    private final Set<PerkAttributeModifier> modifiers = new HashSet<>();

    private AttributeModifierPerk(ResourceLocation key, float x, float y) {
        this(key, defaultNameKey(key), x, y, PerkCategory.DEFAULT, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    protected AttributeModifierPerk(ResourceLocation key,
                                    String nameKey,
                                    float x,
                                    float y,
                                    PerkCategory category,
                                    Collection<PerkRequirement> requirements,
                                    Collection<PerkAttributeConverter> converters,
                                    Collection<PerkAttributeModifier> modifiers) {
        super(key, nameKey, x, y, category, requirements, converters);
        this.modifiers.addAll(modifiers);
    }

    public <P extends AttributeModifierPerk<?>> P addModifier(PerkAttributeModifier modifier) {
        AstralSorcery.assertDataGeneration();
        this.modifiers.add(modifier);
        return MiscUtil.cast(this);
    }

    protected Set<PerkAttributeModifier> getModifiers() {
        return Collections.unmodifiableSet(this.modifiers);
    }

    @Override
    protected boolean addTooltip(Collection<MutableComponent> tooltip, PlayerProgress progress, @Nullable Player player, LogicalSide side) {
        boolean addLine = super.addTooltip(tooltip, progress, player, side);
        if (!this.canSee(progress)) {
            return addLine;
        }
        this.getModifiers().forEach(modifier -> {
            modifier.getAttributeType().getReader().ifPresent(reader -> {
                tooltip.add(reader.getDisplay(modifier, player, progress));
            });
        });

        return addLine || !this.getModifiers().isEmpty();
    }

    @Override
    public Collection<PerkAttributeModifier> getModifiers(Player player, LogicalSide side, boolean ignoreRequirements) {
        if (!ignoreRequirements && ResearchManager.getProgress(player, side).getPerkData().isPerkSealed(this)) {
            return Collections.emptyList();
        }
        return this.getModifiers();
    }

    @Override
    public PerkType<?> getType() {
        return TYPE;
    }
}
