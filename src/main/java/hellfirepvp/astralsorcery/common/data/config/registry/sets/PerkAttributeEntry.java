/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.registry.sets;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigDataSet;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkAttributeEntry
 * Created by HellFirePvP
 * Date: 01.09.2019 / 19:22
 */
public class PerkAttributeEntry implements ConfigDataSet {

    private final PerkAttributeType type;
    private final int weight;

    public PerkAttributeEntry(PerkAttributeType type, int weight) {
        this.type = type;
        this.weight = weight;
    }

    public PerkAttributeType getType() {
        return type;
    }

    public int getWeight() {
        return weight;
    }

    @Nullable
    public static PerkAttributeEntry deserialize(String str) {
        String[] split = str.split(";");
        if (split.length != 2) {
            return null;
        }
        ResourceLocation keyAttributeType = new ResourceLocation(split[0]);
        PerkAttributeType type = RegistriesAS.REGISTRY_PERK_ATTRIBUTE_TYPES.getValue(keyAttributeType);
        if (type == null) {
            return null;
        }
        String strWeight = split[1];
        int weight;
        try {
            weight = Integer.parseInt(strWeight);
        } catch (NumberFormatException exc) {
            return null;
        }
        return new PerkAttributeEntry(type, weight);
    }

    @Nonnull
    @Override
    public String serialize() {
        return this.type.getRegistryName().toString() + ";" + this.weight;
    }
}
