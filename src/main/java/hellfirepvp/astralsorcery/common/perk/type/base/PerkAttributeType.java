/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type.base;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.reader.PerkAttributeTypeReader;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkAttributeType
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkAttributeType {

    public static final Codec<PerkAttributeType> CODEC = RegistriesAS.REGISTRY_PERK_ATTRIBUTE_TYPES.byNameCodec();

    //Used by subclasses to track application of this perkAttributeType on a player-uuid
    private final Map<LogicalSide, Set<UUID>> applicationCache = Maps.newHashMap();

    //Signify if this attribute perkAttributeType may only be used as multipliers
    private final boolean isOnlyMultiplicative;
    private String nameId = null;

    protected PerkAttributeType(boolean isOnlyMultiplicative) {
        this.isOnlyMultiplicative = isOnlyMultiplicative;

        this.init();
        this.attachListeners(NeoForge.EVENT_BUS);
    }

    public boolean isMultiplicative() {
        return this.isOnlyMultiplicative;
    }

    public static PerkAttributeType create(boolean isOnlyMultiplicative) {
        return new PerkAttributeType(isOnlyMultiplicative);
    }

    public static Optional<PerkAttributeType> fromVanillaType(Holder<Attribute> attribute) {
        return RegistriesAS.REGISTRY_PERK_ATTRIBUTE_TYPES.stream()
                .filter(type -> type instanceof VanillaPerkAttributeType)
                .map(type -> (VanillaPerkAttributeType) type)
                .filter(type -> type.getAttribute().is(attribute))
                .findFirst()
                .map(MiscUtil::cast);
    }

    public Optional<PerkAttributeTypeReader> getReader() {
        return RegistriesAS.REGISTRY_PERK_ATTRIBUTE_TYPE_READERS.stream()
                .filter(type -> type.perkAttributeType().get().is(this))
                .findFirst()
                .map(PerkAttributeTypeReader.Type::reader);
    }

    protected void init() {}

    protected void attachListeners(IEventBus eventBus) {}

    public boolean is(PerkAttributeType other) {
        return this == other;
    }

    public boolean is(Supplier<? extends PerkAttributeType> other) {
        return this == other.get();
    }

    @Nonnull
    protected String getOrCreateNameId() {
        if (this.nameId == null) {
            this.nameId = Util.makeDescriptionId("perk.attribute", RegistriesAS.REGISTRY_PERK_ATTRIBUTE_TYPES.getKey(this));
        }
        return this.nameId;
    }

    @Nonnull
    public MutableComponent getName() {
        return Component.translatable(this.getOrCreateNameId());
    }

    @Nonnull
    public PerkAttributeModifier createModifier(float modifier, ModifierType mode) {
        if (this.isMultiplicative() && mode == ModifierType.ADDITION) {
            throw new IllegalArgumentException("Tried creating addition-modifier for a multiplicative-only modifier!");
        }
        return new PerkAttributeModifier(this, mode, modifier);
    }

    public void onApply(Player player, LogicalSide side, ModifierSource source) {
        this.applicationCache.computeIfAbsent(side, s -> new HashSet<>()).add(player.getUUID());
    }

    public void onRemove(Player player, LogicalSide side, boolean removedCompletely, ModifierSource source) {
        if (removedCompletely) {
            this.applicationCache.getOrDefault(side, Collections.emptySet()).remove(player.getUUID());
        }
    }

    //Called if no modifiers of this perkAttributeType were applied on the player, but now there is at least 1 added.
    //Called before any modifiers are actually applied!
    public void onModeApply(Player player, ModifierType mode, LogicalSide side) {}

    //Called if no more modifiers of this perkAttributeType are applied on the player.
    //Called after that last modifier is removed!
    public void onModeRemove(Player player, ModifierType mode, LogicalSide side, boolean removedCompletely) {}

    public boolean hasTypeApplied(Player player, LogicalSide side) {
        return this.applicationCache.getOrDefault(side, Collections.emptySet()).contains(player.getUUID());
    }

    private void clear(LogicalSide side) {
        this.applicationCache.remove(side);
    }

    public static void clearCache(LogicalSide side) {
        for (PerkAttributeType type : RegistriesAS.REGISTRY_PERK_ATTRIBUTE_TYPES) {
            type.clear(side);
        }
    }
}
