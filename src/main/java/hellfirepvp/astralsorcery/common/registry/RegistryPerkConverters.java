package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.node.focus.KeyGelu;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static hellfirepvp.astralsorcery.common.lib.PerkConvertersAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryPerkConverters
 * Created by HellFirePvP
 * Date: 14.08.2020 / 16:01
 */
public class RegistryPerkConverters {

    private RegistryPerkConverters() {}

    public static void init() {
        IDENTITY = register(new PerkConverter(AstralSorcery.key("identity")) {
            @Nonnull
            @Override
            public PerkAttributeModifier convertModifier(PlayerEntity player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable ModifierSource owningSource) {
                return modifier;
            }
        });
        FOCUS_ALCARA = register(new PerkConverter(AstralSorcery.key("focus_alcara")) {
            @Nonnull
            @Override
            public PerkAttributeModifier convertModifier(PlayerEntity player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable ModifierSource owningSource) {
                if (modifier.getAttributeType().equals(PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EXP)) {
                    return modifier.convertModifier(PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT, modifier.getMode(), modifier.getValue(player, progress));
                }
                return modifier;
            }
        });
        FOCUS_GELU = register(new PerkConverter(AstralSorcery.key("focus_gelu")) {
            @Nonnull
            @Override
            public PerkAttributeModifier convertModifier(PlayerEntity player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable ModifierSource owningSource) {
                if (modifier.getAttributeType().equals(PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT) &&
                        owningSource != null && !(owningSource instanceof KeyGelu)) {
                    return modifier.convertModifier(PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT, ModifierType.STACKING_MULTIPLY, 1F);
                }
                return modifier;
            }
        });
    }

    private static <T extends PerkConverter> T register(T converter) {
        AstralSorcery.getProxy().getRegistryPrimer().register(converter);
        return converter;
    }
}
