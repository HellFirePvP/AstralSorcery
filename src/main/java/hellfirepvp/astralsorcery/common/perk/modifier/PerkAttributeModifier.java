/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.type.base.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.MutableIdentity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkAttributeModifier
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkAttributeModifier extends MutableIdentity {

    public static final Codec<PerkAttributeModifier> SERIALIZE_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("identifier").forGetter(PerkAttributeModifier::getIdentifier),
            PerkAttributeType.CODEC.fieldOf("attribute_type").forGetter(PerkAttributeModifier::getAttributeType),
            CodecUtil.enumCodec(ModifierType.class).fieldOf("mode").forGetter(PerkAttributeModifier::getMode),
            Codec.FLOAT.fieldOf("value").forGetter(PerkAttributeModifier::getRawValue)
    ).apply(inst, PerkAttributeModifier::new));
    public static final Codec<PerkAttributeModifier> CODEC = CodecUtil.registryOr(RegistriesAS.REGISTRY_PERK_CUSTOM_MODIFIERS, SERIALIZE_CODEC);

    private static long counter = 0;

    protected final Supplier<? extends PerkAttributeType> attributeType;
    protected final ModifierType mode;
    protected float value;

    //Can (no longer) be converted via perk converters
    private boolean absolute = false;

    public PerkAttributeModifier(PerkAttributeType attributeType, ModifierType mode, float value) {
        this("perk_modifier_" + counter++, attributeType, mode, value);
    }

    public PerkAttributeModifier(String identifier, PerkAttributeType attributeType, ModifierType mode, float value) {
        this(identifier, () -> attributeType, mode, value);
    }

    protected PerkAttributeModifier(String identifier, Supplier<? extends PerkAttributeType> attributeType, ModifierType mode, float value) {
        super(identifier);
        this.attributeType = attributeType;
        this.mode = mode;
        this.value = value;
        this.initModifier();
    }

    protected void initModifier() {}

    public final PerkAttributeType getAttributeType() {
        return this.attributeType.get();
    }

    public final ModifierType getMode() {
        return this.mode;
    }

    /**
     * Should not be accessed directly unless for internal calculation purposes.
     * The actual effect of the modifier might depend on the player's AS-data.
     * See {@link #getValue(Player, PlayerProgress)} for retrieving the actual value
     */
    @Deprecated
    public final float getRawValue() {
        return this.value;
    }

    public float getValue(@Nullable Player player, @Nullable PlayerProgress progress) {
        return this.getRawValue();
    }

    protected void setAbsolute() {
        this.absolute = true;
    }

    public final boolean isAbsolute() {
        return this.absolute;
    }

    /**
     * Use this method for PerkConverters returning a new PerkAttributeModifier
     * Absolute modifiers cannot be converted
     */
    @Nonnull
    public PerkAttributeModifier convertModifier(PerkAttributeType type, ModifierType mode, float value) {
        if (this.isAbsolute()) {
            return this;
        }
        PerkAttributeModifier mod = this.createModifier(type, mode, value);
        mod.setIdentifier(this.getIdentifier());
        return mod;
    }

    /**
     * Use this method for creating extra Modifiers depending on a given modifier
     * Caches created modifiers based on converter to avoid creating new unique modifiers
     */
    @Nonnull
    public PerkAttributeModifier gainAsExtraModifier(PerkAttributeConverter converter, PerkAttributeType type, ModifierType mode, float value) {
        PerkAttributeModifier modifier = ConvertedModifierCache.getCachedResultModifier(this.getIdentifier(), converter, type, mode);
        if (modifier == null) {
            modifier = this.createModifier(type, mode, value);
            modifier.setAbsolute();
            ConvertedModifierCache.addModifierToCache(this.getIdentifier(), converter, type, mode, modifier);
        }
        return modifier;
    }

    @Nonnull
    protected PerkAttributeModifier createModifier(PerkAttributeType type, ModifierType mode, float value) {
        return type.createModifier(value, mode);
    }
}
