/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.modifier;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.perk.PerkConverter;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkAttributeModifier
 * Created by HellFirePvP
 * Date: 08.08.2019 / 17:25
 */
public class PerkAttributeModifier {

    private static long counter = 0;

    private long id;
    protected final ModifierType mode;
    protected final PerkAttributeType attributeType;
    protected float value;

    //Cannot be converted to anything else.
    private boolean absolute = false;

    //Cached in case the value of the modifier actually is supposed to change down the road.
    protected double ctMultiplier = 1.0D;

    private Map<PerkConverter, Table<PerkAttributeType, ModifierType, PerkAttributeModifier>> cachedConverters = Maps.newHashMap();

    public PerkAttributeModifier(PerkAttributeType type, ModifierType mode, float value) {
        this.id = counter;
        counter++;
        this.attributeType = type;
        this.mode = mode;
        this.value = value;
        initModifier();
    }

    public long getId() {
        return id;
    }

    protected void initModifier() {}

    protected void setAbsolute() {
        this.absolute = true;
    }

    void multiplyValue(double multiplier) {
        this.ctMultiplier = multiplier;
        if (mode == ModifierType.STACKING_MULTIPLY) {
            this.value = ((this.value - 1F) * ((float) multiplier)) + 1F;
        } else {
            this.value *= multiplier;
        }
    }

    /**
     * Use this method for PerkConverters returning a new PerkAttributeModifier!
     */
    @Nonnull
    public PerkAttributeModifier convertModifier(PerkAttributeType type, ModifierType mode, float value) {
        if (absolute) {
            return this;
        }
        PerkAttributeModifier mod = this.createModifier(type, mode, value);
        mod.id = this.id;
        return mod;
    }

    /**
     * Use this method for creating extra Modifiers depending on a given modifier.
     */
    @Nonnull
    public PerkAttributeModifier gainAsExtraModifier(PerkConverter converter, PerkAttributeType type, ModifierType mode, float value) {
        PerkAttributeModifier modifier = getCachedAttributeModifier(converter, type, mode);
        if (modifier == null) {
            modifier = this.createModifier(type, mode, value);
            modifier.setAbsolute();
            addModifierToCache(converter, type, mode, modifier);
        }
        return modifier;
    }

    @Nullable
    protected PerkAttributeModifier getCachedAttributeModifier(PerkConverter converter, PerkAttributeType type, ModifierType mode) {
        Table<PerkAttributeType, ModifierType, PerkAttributeModifier> cachedModifiers = cachedConverters.computeIfAbsent(converter, (c) -> HashBasedTable.create());
        return cachedModifiers.get(type, mode);
    }

    protected void addModifierToCache(PerkConverter converter, PerkAttributeType type, ModifierType mode, PerkAttributeModifier modifier) {
        Table<PerkAttributeType, ModifierType, PerkAttributeModifier> cachedModifiers = cachedConverters.computeIfAbsent(converter, (c) -> HashBasedTable.create());
        cachedModifiers.put(type, mode, modifier);
    }

    @Nonnull
    protected PerkAttributeModifier createModifier(PerkAttributeType type, ModifierType mode, float value) {
        return type.createModifier(value, mode);
    }

    // Should not be accessed directly unless for internal calculation purposes.
    // The actual effect of the modifier might depend on the player's AS-data.
    @Deprecated
    public final float getRawValue() {
        return value;
    }

    public float getValue(PlayerEntity player, PlayerProgress progress) {
        return getRawValue();
    }

    @OnlyIn(Dist.CLIENT)
    public float getValueForDisplay(PlayerEntity player, PlayerProgress progress) {
        return getValue(player, progress);
    }

    public ModifierType getMode() {
        return mode;
    }

    public PerkAttributeType getAttributeType() {
        return attributeType;
    }

    protected String getUnlocalizedAttributeName() {
        return getAttributeType().getUnlocalizedName();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean hasDisplayString() {
        return I18n.hasKey(getAttributeType().getUnlocalizedName());
    }

    @OnlyIn(Dist.CLIENT)
    public String getLocalizedAttributeValue() {
        return getMode().stringifyValue(getValueForDisplay(Minecraft.getInstance().player, ResearchHelper.getClientProgress()));
    }

    @OnlyIn(Dist.CLIENT)
    public String getLocalizedModifierName() {
        return I18n.format(getMode().getUnlocalizedModifierName(getValueForDisplay(Minecraft.getInstance().player, ResearchHelper.getClientProgress())));
    }

    @OnlyIn(Dist.CLIENT)
    public String getAttributeDisplayFormat() {
        return I18n.format("perk.modifier.astralsorcery.format");
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
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
}
