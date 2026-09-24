/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.source.AttributeConverterProvider;
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
 * Class: AttributeConverterPerk
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class AttributeConverterPerk<D extends AbstractPerk.Data> extends ProgressPerk<D> implements AttributeConverterProvider {

    protected static <T extends AttributeConverterPerk<?>> Products.P7<RecordCodecBuilder.Mu<T>, ResourceLocation, String, Float, Float, PerkCategory, Set<PerkRequirement>, Set<PerkAttributeConverter>> perkConverterFields(RecordCodecBuilder.Instance<T> instance) {
        return progressFields(instance).and(
                SetCodec.of(PerkAttributeConverter.CODEC).fieldOf("converters").forGetter(AttributeConverterPerk::getConverters)
        );
    }

    private final Set<PerkAttributeConverter> converters = new HashSet<>();

    protected AttributeConverterPerk(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements, Collection<PerkAttributeConverter> converters) {
        super(key, nameKey, x, y, category, requirements);
        this.converters.addAll(converters);
    }

    public <P extends AttributeConverterPerk<?>> P addConverter(PerkAttributeConverter converter) {
        AstralSorcery.assertDataGeneration();
        this.converters.add(converter);
        return MiscUtil.cast(this);
    }

    protected Set<PerkAttributeConverter> getConverters() {
        return Collections.unmodifiableSet(this.converters);
    }

    @Override
    protected boolean addTooltip(Collection<MutableComponent> tooltip, PlayerProgress progress, @Nullable Player player, LogicalSide side) {
        boolean addLine = super.addTooltip(tooltip, progress, player, side);
        if (!this.canSee(progress)) {
            return addLine;
        }
        this.getConverters().forEach(converter -> tooltip.addAll(converter.getDescription()));
        return addLine || !this.getConverters().isEmpty();
    }

    @Override
    public Collection<PerkAttributeConverter> getConverters(Player player, LogicalSide side, boolean ignoreRequirements) {
        if (!ignoreRequirements && ResearchManager.getProgress(player, side).getPerkData().isPerkSealed(this)) {
            return Collections.emptyList();
        }
        return this.getConverters();
    }
}
