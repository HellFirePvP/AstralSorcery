/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.registry.sets;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigDataSet;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AmuletEnchantmentEntry
 * Created by HellFirePvP
 * Date: 11.08.2019 / 20:42
 */
public class AmuletEnchantmentEntry implements ConfigDataSet, Comparable<AmuletEnchantmentEntry> {

    private final Enchantment enchantment;
    private final int weight;

    public AmuletEnchantmentEntry(Enchantment ench, int weight) {
        this.enchantment = ench;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    @Override
    public int compareTo(AmuletEnchantmentEntry o) {
        return Integer.compare(this.weight, o.weight);
    }

    @Nonnull
    @Override
    public String serialize() {
        return this.enchantment.getRegistryName().toString() + ";" + weight;
    }

    @Nullable
    public static AmuletEnchantmentEntry deserialize(String str) {
        String[] spl = str.split(";");
        if (spl.length < 2) {
            return null;
        }
        String enchantmentKey = spl[0];
        String weight = spl[1];

        //TODO find a better solution than hardcoding (duh)
        ResourceLocation registryName = new ResourceLocation(enchantmentKey);
        if (registryName.toString().equalsIgnoreCase("cofhcore:holding")) {
            AstralSorcery.log.info("Auto-ignoring amulet enchantment 'cofhcore:holding' as it's prone to cause issues.");
            return null;
        }

        Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(registryName);
        if (ench == null) {
            AstralSorcery.log.info("Ignoring whitelist entry " + str + " for amulet enchantments - Enchantment does not exist!");
            return null;
        }
        int w;
        try {
            w = Integer.parseInt(weight);
        } catch (NumberFormatException exc) {
            AstralSorcery.log.info("Ignoring whitelist entry " + str + " for amulet enchantments - last :-separated argument is not a number!");
            return null;
        }
        return new AmuletEnchantmentEntry(ench, w);
    }
}
