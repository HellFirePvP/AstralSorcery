/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.convert;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import hellfirepvp.astralsorcery.common.perk.type.base.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.util.MutableIdentity;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import hellfirepvp.astralsorcery.common.util.data.FloatPoint;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkAttributeConverter
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class PerkAttributeConverter extends MutableIdentity {

    public static final Codec<PerkAttributeConverter> CODEC = RegistriesAS.REGISTRY_PERK_CONVERTERS.byNameCodec();

    protected PerkAttributeConverter(String identifier) {
        super(identifier);
    }

    /**
     * Use {@link PerkAttributeModifier#convertModifier(PerkAttributeType, ModifierType, float)} to convert the given modifier
     */
    @Nonnull
    public abstract PerkAttributeModifier convertModifier(Player player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable ModifierSource owningSource);

    /**
     * Use {@link PerkAttributeModifier#gainAsExtraModifier(PerkAttributeConverter, PerkAttributeType, ModifierType, float)} to create new modifiers
     * based off of the given modifier! The resulting modifiers cannot be modified with perk converters.
     */
    @Nonnull
    public Collection<PerkAttributeModifier> gainExtraModifiers(Player player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable ModifierSource owningSource) {
        return Lists.newArrayList();
    }

    public RangedPerkAttributeConverter asRangedConverter(FloatPoint offset, float radius) {
        return RangedPerkAttributeConverter.asRanged(this, offset, radius);
    }

    public List<String> getDescriptionIds() {
        return NameUtil.resolveLocalizedLines(String.format("perk.converter.%s.description", this.getIdentifier()));
    }

    public List<MutableComponent> getDescription() {
        return this.getDescriptionIds().stream()
                .map(Component::translatable)
                .toList();
    }

    public void onApply(Player player, LogicalSide side) {}

    public void onRemove(Player player, LogicalSide side) {}

}
