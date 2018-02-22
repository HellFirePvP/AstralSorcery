/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment.amulet;

import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AmuletEnchantment
 * Created by HellFirePvP
 * Date: 27.01.2018 / 13:37
 */
public class AmuletEnchantment {

    private final AmuletEnchantment.Type type;
    @Nullable private final Enchantment enchantment;
    private int levelAddition;

    public AmuletEnchantment(AmuletEnchantment.Type type, @Nonnull Enchantment enchantment, int levelAddition) {
        if(!type.hasEnchantmentTag()) {
            throw new IllegalArgumentException("Tried to create amulet enchantment that doesn't requires a std. enchantment together with an enchantment!");
        }
        this.type = type;
        this.enchantment = enchantment;
        this.levelAddition = levelAddition;
    }

    public AmuletEnchantment(AmuletEnchantment.Type type, int levelAddition) {
        if(type.hasEnchantmentTag()) {
            throw new IllegalArgumentException("Tried to create amulet enchantment that requires a std. enchantment without an enchantment!");
        }
        this.type = type;
        this.enchantment = null;
        this.levelAddition = levelAddition;
    }

    public AmuletEnchantment.Type getType() {
        return type;
    }

    @Nullable
    public Enchantment getEnchantment() {
        return enchantment;
    }

    public int getLevelAddition() {
        return levelAddition;
    }

    @SideOnly(Side.CLIENT)
    public String getDescription() {
        String unlocKey = "amulet.enchantment." + this.type.name().toLowerCase() + ".name";
        String locLevels = I18n.format("amulet.enchantment.level." + (this.levelAddition == 1 ? "one" : "more"));
        if (this.type.hasEnchantmentTag()) {
            String locEnch = I18n.format(this.enchantment.getName());
            return I18n.format(unlocKey, String.valueOf(this.levelAddition), locLevels, locEnch);
        } else {
            return I18n.format(unlocKey, String.valueOf(this.levelAddition), locLevels);
        }
    }

    public boolean canMerge(AmuletEnchantment other) {
        return this.type.equals(other.type) && (!this.type.hasEnchantmentTag() || this.enchantment.equals(other.enchantment));
    }

    public void merge(AmuletEnchantment src) {
        if(canMerge(src)) {
            this.levelAddition += src.levelAddition;
        }
    }

    public NBTTagCompound serialize() {
        NBTTagCompound cmp = new NBTTagCompound();
        cmp.setInteger("type", this.type.ordinal());
        cmp.setInteger("level", this.levelAddition);
        if(this.type.hasEnchantmentTag()) { //Enchantment must not be null here anyway as the type requires a ench to begin with
            cmp.setString("ench", this.enchantment.getRegistryName().toString());
        }
        return cmp;
    }

    @Nullable
    public static AmuletEnchantment deserialize(NBTTagCompound cmp) {
        int typeId = cmp.getInteger("type");
        AmuletEnchantment.Type type = AmuletEnchantment.Type.values()[MathHelper.clamp(typeId, 0, AmuletEnchantment.Type.values().length - 1)];
        int level = Math.max(0, cmp.getInteger("level"));
        if(type.hasEnchantmentTag()) {
            ResourceLocation res = new ResourceLocation(cmp.getString("ench"));
            Enchantment e = ForgeRegistries.ENCHANTMENTS.getValue(res);
            if(e != null) {
                return new AmuletEnchantment(type, e, level);
            }
        } else {
            return new AmuletEnchantment(type, level);
        }
        return null;
    }

    //The ordering in the enum defines the order of how the types of amulet-enchantments are applied/calculated!
    public enum Type {

        ADD_TO_SPECIFIC,
        ADD_TO_EXISTING_SPECIFIC,
        ADD_TO_EXISTING_ALL(false);

        private final boolean hasEnchantmentTag;

        Type() {
            this(true);
        }

        Type(boolean hasEnchantmentTag) {
            this.hasEnchantmentTag = hasEnchantmentTag;
        }

        public boolean hasEnchantmentTag() {
            return hasEnchantmentTag;
        }

    }

}
