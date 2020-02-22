/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.reader.PerkAttributeReader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkAttributeType
 * Created by HellFirePvP
 * Date: 08.08.2019 / 16:56
 */
public class PerkAttributeType extends ForgeRegistryEntry<PerkAttributeType> {

    protected static final Random rand = new Random();

    //May be used by subclasses to more efficiently track who's got a perk applied
    private Map<LogicalSide, Set<UUID>> applicationCache = Maps.newHashMap();

    private final boolean isOnlyMultiplicative;

    protected PerkAttributeType(ResourceLocation key) {
        this(key, false);
    }

    protected PerkAttributeType(ResourceLocation key, boolean isMultiplicative) {
        this.setRegistryName(key);
        this.isOnlyMultiplicative = isMultiplicative;

        this.init();
        this.attachListeners(MinecraftForge.EVENT_BUS);
    }

    public static PerkAttributeType makeDefault(ResourceLocation name, boolean isMultiplicative) {
        return new PerkAttributeType(name, isMultiplicative);
    }

    public boolean isMultiplicative() {
        return isOnlyMultiplicative;
    }

    public ITextComponent getTranslatedName() {
        return new TranslationTextComponent(this.getUnlocalizedName());
    }

    public String getUnlocalizedName() {
        return String.format("perk.attribute.%s.%s.name",
                this.getRegistryName().getNamespace(), this.getRegistryName().getPath());
    }

    protected void init() {}

    protected void attachListeners(IEventBus eventBus) {}

    protected LogicalSide getSide(Entity entity) {
        return entity.getEntityWorld().isRemote() ? LogicalSide.CLIENT : LogicalSide.SERVER;
    }

    @Nullable
    public PerkAttributeReader getReader() {
        return RegistriesAS.REGISTRY_PERK_ATTRIBUTE_READERS.getValue(this.getRegistryName());
    }

    @Nonnull
    public PerkAttributeModifier createModifier(float modifier, ModifierType mode) {
        if (isMultiplicative() && mode == ModifierType.ADDITION) {
            throw new IllegalArgumentException("Tried creating addition-modifier for a multiplicative-only modifier!");
        }
        return new PerkAttributeModifier(this, mode, modifier);
    }

    public void onApply(PlayerEntity player, LogicalSide side) {
        Set<UUID> applied = applicationCache.computeIfAbsent(side, s -> new HashSet<>());
        applied.add(player.getUniqueID());
    }

    public void onRemove(PlayerEntity player, LogicalSide side, boolean removedCompletely) {
        if (removedCompletely) {
            applicationCache.computeIfAbsent(side, s -> new HashSet<>()).remove(player.getUniqueID());
        }
    }

    public void onModeApply(PlayerEntity player, ModifierType mode, LogicalSide side) {}

    public void onModeRemove(PlayerEntity player, ModifierType mode, LogicalSide side, boolean removedCompletely) {}

    public boolean hasTypeApplied(PlayerEntity player, LogicalSide side) {
        return applicationCache.computeIfAbsent(side, s -> new HashSet<>()).contains(player.getUniqueID());
    }

    private void clear(LogicalSide side) {
        this.applicationCache.remove(side);
    }

    public static void clearCache(LogicalSide side) {
        for (PerkAttributeType type : RegistriesAS.REGISTRY_PERK_ATTRIBUTE_TYPES) {
            type.clear(side);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerkAttributeType that = (PerkAttributeType) o;
        return Objects.equals(this.getRegistryName(), that.getRegistryName());
    }

    @Override
    public int hashCode() {
        return this.getRegistryName().hashCode();
    }
}
