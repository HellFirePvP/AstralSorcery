package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.type.vanilla.VanillaAttributeType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeLuck
 * Created by HellFirePvP
 * Date: 31.07.2020 / 18:44
 */
public class AttributeTypeLuck extends VanillaAttributeType {

    private static final UUID LUCK_ADD_ID = UUID.fromString("84e42c15-06be-453d-a2ea-f241ddce3645");
    private static final UUID LUCK_ADD_MULTIPLY_ID = UUID.fromString("91e42c15-06be-453d-a2ea-f241ddce3645");
    private static final UUID LUCK_STACK_MULTIPLY_ID = UUID.fromString("2de42c15-06be-453d-a2ea-f241ddce3645");

    public AttributeTypeLuck() {
        super(PerkAttributeTypesAS.KEY_ATTR_TYPE_LUCK);
    }

    @Override
    public String getDescription() {
        return "Perk Luck";
    }

    @Nonnull
    @Override
    public IAttribute getAttribute() {
        return SharedMonsterAttributes.LUCK;
    }

    @Override
    public UUID getID(ModifierType mode) {
        switch (mode) {
            case ADDITION:
                return LUCK_ADD_ID;
            case ADDED_MULTIPLY:
                return LUCK_ADD_MULTIPLY_ID;
            case STACKING_MULTIPLY:
                return LUCK_STACK_MULTIPLY_ID;
            default:
                break;
        }
        return null;
    }
}
