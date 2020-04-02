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
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.PerkConverter;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DynamicAttributeModifier
 * Created by HellFirePvP
 * Date: 09.08.2019 / 07:26
 */
public class DynamicAttributeModifier extends PerkAttributeModifier {

    private final UUID uuid;
    private PerkAttributeModifier actualModifier = null;

    private static Map<UUID, Map<PerkConverter, Table<PerkAttributeType, ModifierType, PerkAttributeModifier>>> gemConverterCache = Maps.newHashMap();

    public DynamicAttributeModifier(UUID uniqueId, PerkAttributeType type, ModifierType mode, float value) {
        super(type, mode, value);
        this.uuid = uniqueId;
    }

    @Override
    protected void initModifier() {
        super.initModifier();

        this.setAbsolute();
    }

    @Nonnull
    @Override
    public PerkAttributeModifier convertModifier(PerkAttributeType type, ModifierType mode, float value) {
        PerkAttributeModifier mod = super.convertModifier(type, mode, value);
        return new DynamicAttributeModifier(this.getUniqueId(), mod.getAttributeType(), mod.getMode(), mod.getRawValue());
    }

    @Override
    @Nullable
    protected PerkAttributeModifier getCachedAttributeModifier(PerkConverter converter, PerkAttributeType type, ModifierType mode) {
        Map<PerkConverter, Table<PerkAttributeType, ModifierType, PerkAttributeModifier>> modifierCache = gemConverterCache.computeIfAbsent(this.getUniqueId(), (u) -> new HashMap<>());
        Table<PerkAttributeType, ModifierType, PerkAttributeModifier> cachedModifiers = modifierCache.computeIfAbsent(converter, (c) -> HashBasedTable.create());
        return cachedModifiers.get(type, mode);
    }

    @Override
    protected void addModifierToCache(PerkConverter converter, PerkAttributeType type, ModifierType mode, PerkAttributeModifier modifier) {
        Map<PerkConverter, Table<PerkAttributeType, ModifierType, PerkAttributeModifier>> modifierCache = gemConverterCache.computeIfAbsent(this.getUniqueId(), (u) -> new HashMap<>());
        Table<PerkAttributeType, ModifierType, PerkAttributeModifier> cachedModifiers = modifierCache.computeIfAbsent(converter, (c) -> HashBasedTable.create());
        cachedModifiers.put(type, mode, modifier);
    }

    private boolean resolveModifier() {
        if (actualModifier != null) {
            return true;
        }

        actualModifier = this.attributeType.createModifier(this.value, this.mode);
        actualModifier.setAbsolute();
        return true;
    }

    @Override
    public float getValue(PlayerEntity player, PlayerProgress progress) {
        if (!resolveModifier()) {
            return super.getValue(player, progress);
        }
        return actualModifier.getValue(player, progress);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public float getValueForDisplay(PlayerEntity player, PlayerProgress progress) {
        if (!resolveModifier()) {
            return super.getValueForDisplay(player, progress);
        }
        return actualModifier.getValueForDisplay(player, progress);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public String getAttributeDisplayFormat() {
        if (!resolveModifier()) {
            return super.getAttributeDisplayFormat();
        }
        return actualModifier.getAttributeDisplayFormat();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public String getUnlocalizedAttributeName() {
        if (!resolveModifier()) {
            return super.getUnlocalizedAttributeName();
        }
        return actualModifier.getUnlocalizedAttributeName();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean hasDisplayString() {
        if (!resolveModifier()) {
            return super.hasDisplayString();
        }
        return actualModifier.hasDisplayString();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public String getLocalizedAttributeValue() {
        if (!resolveModifier()) {
            return super.getLocalizedAttributeValue();
        }
        return actualModifier.getLocalizedAttributeValue();
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public String getLocalizedDisplayString() {
        if (!resolveModifier()) {
            return super.getLocalizedDisplayString();
        }
        return actualModifier.getLocalizedDisplayString();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public String getLocalizedModifierName() {
        if (!resolveModifier()) {
            return super.getLocalizedModifierName();
        }
        return actualModifier.getLocalizedModifierName();
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        tag.putUniqueId("id", getUniqueId());
        tag.putString("type", getAttributeType().getRegistryName().toString());
        tag.putInt("mode", getMode().ordinal());
        tag.putFloat("baseValue", this.value);
        return tag;
    }

    @Nullable
    public static DynamicAttributeModifier deserialize(CompoundNBT tag) {
        PerkAttributeType attrType = RegistriesAS.REGISTRY_PERK_ATTRIBUTE_TYPES.getValue(new ResourceLocation(tag.getString("type")));
        if (attrType == null) {
            return null;
        }
        UUID id = tag.getUniqueId("id");
        ModifierType mode = ModifierType.values()[tag.getInt("mode")];
        float val = tag.getFloat("baseValue");
        return new DynamicAttributeModifier(id, attrType, mode, val);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DynamicAttributeModifier that = (DynamicAttributeModifier) o;
        return this.uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }
}
