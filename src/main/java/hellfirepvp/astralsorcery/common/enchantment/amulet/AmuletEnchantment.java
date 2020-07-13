/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment.amulet;

import hellfirepvp.astralsorcery.common.enchantment.dynamic.DynamicEnchantment;
import hellfirepvp.astralsorcery.common.enchantment.dynamic.DynamicEnchantmentType;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AmuletEnchantment
 * Created by HellFirePvP
 * Date: 11.08.2019 / 20:12
 */
public class AmuletEnchantment extends DynamicEnchantment {

    public AmuletEnchantment(DynamicEnchantmentType type, @Nonnull Enchantment enchantment, int levelAddition) {
        super(type, enchantment, levelAddition);
    }

    public AmuletEnchantment(DynamicEnchantmentType type, int levelAddition) {
        super(type, levelAddition);
    }

    @OnlyIn(Dist.CLIENT)
    public String getDisplay() {
        String typeStr = this.getType().getDisplayName();
        String levelsStr = I18n.format(String.format("astralsorcery.amulet.enchantment.level.%s", this.levelAddition > 1 ? "more" : "one"));

        if (this.getType().isEnchantmentSpecific()) {
            return I18n.format(typeStr, String.valueOf(this.getLevelAddition()), levelsStr, I18n.format(this.getEnchantment().getName()));
        } else {
            return I18n.format(typeStr, String.valueOf(this.getLevelAddition()), levelsStr);
        }
    }

    public boolean canMerge(AmuletEnchantment other) {
        return this.type.equals(other.type) && (!this.type.isEnchantmentSpecific() || this.enchantment.equals(other.enchantment));
    }

    public void merge(AmuletEnchantment src) {
        if (canMerge(src)) {
            this.levelAddition += src.levelAddition;
        }
    }

    public CompoundNBT serialize() {
        CompoundNBT cmp = new CompoundNBT();
        cmp.putInt("type", this.type.ordinal());
        cmp.putInt("level", this.levelAddition);
        if (this.type.isEnchantmentSpecific()) { //Enchantment must not be null here anyway as the type requires a ench to begin with
            cmp.putString("ench", this.enchantment.getRegistryName().toString());
        }
        return cmp;
    }

    @Nullable
    public static AmuletEnchantment deserialize(CompoundNBT cmp) {
        int typeId = cmp.getInt("type");
        DynamicEnchantmentType type = DynamicEnchantmentType.values()[typeId];
        int level = Math.max(0, cmp.getInt("level"));
        if (type.isEnchantmentSpecific()) {
            ResourceLocation res = new ResourceLocation(cmp.getString("ench"));
            Enchantment e = ForgeRegistries.ENCHANTMENTS.getValue(res);
            if (e != null) {
                return new AmuletEnchantment(type, e, level);
            }
        } else {
            return new AmuletEnchantment(type, level);
        }
        return null;
    }

}
