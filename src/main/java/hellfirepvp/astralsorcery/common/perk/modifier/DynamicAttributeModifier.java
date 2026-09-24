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
import hellfirepvp.astralsorcery.common.perk.type.base.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import hellfirepvp.astralsorcery.common.util.MutableIdentity;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DynamicAttributeModifier
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class DynamicAttributeModifier extends PerkAttributeModifier {

    public static final Codec<DynamicAttributeModifier> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("identifier").forGetter(DynamicAttributeModifier::getIdentifier),
            PerkAttributeType.CODEC.fieldOf("attribute_type").forGetter(DynamicAttributeModifier::getAttributeType),
            CodecUtil.enumCodec(ModifierType.class).fieldOf("mode").forGetter(DynamicAttributeModifier::getMode),
            Codec.FLOAT.fieldOf("value").forGetter(DynamicAttributeModifier::getRawValue)
    ).apply(inst, DynamicAttributeModifier::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, DynamicAttributeModifier> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            MutableIdentity::getIdentifier,
            ByteBufCodecs.registry(RegistriesAS.KEY_PERK_ATTRIBUTE_TYPES),
            DynamicAttributeModifier::getAttributeType,
            CodecUtil.enumStreamCodec(ModifierType.class),
            DynamicAttributeModifier::getMode,
            ByteBufCodecs.FLOAT,
            DynamicAttributeModifier::getRawValue,
            DynamicAttributeModifier::new
    );

    public DynamicAttributeModifier(String identifier, PerkAttributeType attributeType, ModifierType mode, float value) {
        this(identifier, () -> attributeType, mode, value);
    }

    public DynamicAttributeModifier(String identifier, Supplier<? extends PerkAttributeType> attributeType, ModifierType mode, float value) {
        super(identifier, attributeType, mode, value);
        this.setAbsolute();
    }
    
    public DynamicAttributeModifier changeValue(float newValue) {
        String newId = this.getIdentifier() + "_" + Float.floatToIntBits(newValue);
        return new DynamicAttributeModifier(newId, this.getAttributeType(), this.getMode(), newValue);
    }
}
