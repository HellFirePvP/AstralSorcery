/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkConverter;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkAttributeModifier
 * Created by HellFirePvP
 * Date: 08.07.2018 / 11:08
 */
public class PerkAttributeModifier {

    private static long counter = 0;

    private long id;
    protected final Mode mode;
    protected final String attributeType;
    protected float value;

    //Cannot be converted to anything else.
    private boolean absolute = false;

    //Cached in case the value of the modifier actually is supposed to change down the road.
    protected double ctMultiplier = 1.0D;

    private Map<PerkConverter, Table<String, Mode, PerkAttributeModifier>> cachedConverters = Maps.newHashMap();

    public PerkAttributeModifier(String type, Mode mode, float value) {
        this.id = counter;
        counter++;
        this.attributeType = type;
        this.mode = mode;
        this.value = value;
        initModifier();
    }

    protected void initModifier() {}

    protected void setAbsolute() {
        this.absolute = true;
    }

    void multiplyValue(double multiplier) {
        this.ctMultiplier = multiplier;
        if (mode == Mode.STACKING_MULTIPLY) {
            this.value = ((this.value - 1F) * ((float) multiplier)) + 1F;
        } else {
            this.value *= multiplier;
        }
    }

    /**
     * Use this method for PerkConverters returning a new PerkAttributeModifier!
     */
    @Nonnull
    public final PerkAttributeModifier convertModifier(String attributeType, Mode mode, float value) {
        if (absolute) {
            return this;
        }
        PerkAttributeModifier mod;
        PerkAttributeType type = AttributeTypeRegistry.getType(attributeType);
        if (type != null) {
            mod = type.createModifier(value, mode);
        } else {
            mod = new PerkAttributeModifier(attributeType, mode, value);
        }
        mod.id = this.id;
        return mod;
    }

    /**
     * Use this method for creating extra Modifiers depending on a given modifier.
     */
    @Nullable
    public final PerkAttributeModifier gainAsExtraModifier(PerkConverter converter, String attributeType, Mode mode, float value) {
        Table<String, Mode, PerkAttributeModifier> cachedModifiers = cachedConverters.computeIfAbsent(converter, (c) -> HashBasedTable.create());
        PerkAttributeModifier modifier;
        PerkAttributeType attrType;
        if ((modifier = cachedModifiers.get(attributeType, mode)) == null &&
                (attrType = AttributeTypeRegistry.getType(attributeType)) != null) {
            modifier = attrType.createModifier(value, mode);
            modifier.setAbsolute();
            cachedModifiers.put(attributeType, mode, modifier);
        }
        return modifier;
    }

    // Should not be accessed directly unless for internal calculation purposes.
    // The actual effect of the modifier might depend on the player's AS-data.
    @Deprecated
    public final float getFlatValue() {
        return value;
    }

    public float getValue(EntityPlayer player, PlayerProgress progress) {
        return getFlatValue();
    }

    @SideOnly(Side.CLIENT)
    public float getValueForDisplay(EntityPlayer player, PlayerProgress progress) {
        return getValue(player, progress);
    }

    public Mode getMode() {
        return mode;
    }

    public String getAttributeType() {
        return attributeType;
    }

    @Nullable
    public PerkAttributeType resolveType() {
        return AttributeTypeRegistry.getType(attributeType);
    }

    protected String getUnlocalizedAttributeName() {
        PerkAttributeType type;
        if ((type = resolveType()) != null) {
            return type.getUnlocalizedName();
        }
        return "???";
    }

    @SideOnly(Side.CLIENT)
    public boolean hasDisplayString() {
        PerkAttributeType type;
        if ((type = resolveType()) != null) {
            return I18n.hasKey(type.getUnlocalizedName());
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public String getLocalizedAttributeValue() {
        return getMode().stringifyValue(getValueForDisplay(Minecraft.getMinecraft().player, ResearchManager.clientProgress));
    }

    @SideOnly(Side.CLIENT)
    public String getLocalizedModifierName() {
        return I18n.format(getMode().getUnlocalizedModifierName(getValueForDisplay(Minecraft.getMinecraft().player, ResearchManager.clientProgress)));
    }

    @SideOnly(Side.CLIENT)
    public String getAttributeDisplayFormat() {
        return I18n.format("perk.modifier.format");
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public String getLocalizedDisplayString() {
        if (!hasDisplayString()) {
            return null;
        }
        return String.format(getAttributeDisplayFormat(),
                getLocalizedAttributeValue(),
                getLocalizedModifierName(),
                I18n.format(getUnlocalizedAttributeName()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerkAttributeModifier that = (PerkAttributeModifier) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    public static enum Mode {

        ADDITION,
        ADDED_MULTIPLY,
        STACKING_MULTIPLY;

        public static Mode fromVanillaAttributeOperation(int op) {
            return values()[MathHelper.clamp(op, 0, values().length - 1)];
        }

        public int getVanillaAttributeOperation() {
            return ordinal();
        }

        // We don't need the explicit + addition to positive percentages
        public String stringifyValue(float number) {
            if (this == ADDITION) {
                String str = Integer.toString(Math.round(number));
                if (number > 0) {
                    str = "+" + str;
                }
                return str;
            } else {
                int nbr = Math.round(number * 100);
                return Integer.toString(Math.abs(this == STACKING_MULTIPLY ? 100 - nbr : nbr));
            }
        }

        public String getUnlocalizedModifierName(float number) {
            boolean positive;
            if (this == ADDITION) {
                positive = number > 0; //0 would be kinda... weird as addition/subtraction modifier...
            } else {
                int nbr = Math.round(number * 100);
                positive = this == STACKING_MULTIPLY ? nbr > 100 : nbr > 0;
            }
            return getUnlocalizedModifierName(positive);
        }

        public String getUnlocalizedModifierName(boolean positive) {
            String base = positive ? "perk.modifier.%s.add" : "perk.modifier.%s.sub";
            return String.format(base, name().toLowerCase());
        }

    }

}
