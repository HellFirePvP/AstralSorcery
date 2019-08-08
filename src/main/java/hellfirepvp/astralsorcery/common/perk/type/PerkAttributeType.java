/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
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
    private Map<Dist, List<UUID>> applicationCache = Maps.newHashMap();

    private final boolean isOnlyMultiplicative;

    public PerkAttributeType(ResourceLocation key) {
        this(key, false);
    }

    public PerkAttributeType(ResourceLocation key, boolean isMultiplicative) {
        this.setRegistryName(key);
        this.isOnlyMultiplicative = isMultiplicative;
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

    //TODO attribute readers
    //@Nullable
    //public AttributeReader getReader() {
    //    return AttributeReaderRegistry.getReader(this.getTypeString());
    //}

    @Nonnull
    public PerkAttributeModifier createModifier(float modifier, ModifierType mode) {
        if (isMultiplicative() && mode == ModifierType.ADDITION) {
            throw new IllegalArgumentException("Tried creating addition-modifier for a multiplicative-only modifier!");
        }
        return new PerkAttributeModifier(this, mode, modifier);
    }

    public void onApply(PlayerEntity player, Dist side) {
        List<UUID> applied = applicationCache.computeIfAbsent(side, s -> Lists.newArrayList());
        if (!applied.contains(player.getUniqueID())) {
            applied.add(player.getUniqueID());
        }
    }

    public void onRemove(PlayerEntity player, Dist side, boolean removedCompletely) {
        if (removedCompletely) {
            applicationCache.computeIfAbsent(side, s -> Lists.newArrayList()).remove(player.getUniqueID());
        }
    }

    public void onModeApply(PlayerEntity player, ModifierType mode, Dist side) {}

    public void onModeRemove(PlayerEntity player, ModifierType mode, Dist side, boolean removedCompletely) {}

    public boolean hasTypeApplied(PlayerEntity player, Dist side) {
        return applicationCache.computeIfAbsent(side, s -> Lists.newArrayList()).contains(player.getUniqueID());
    }

    //TODO cache clear DC
    public final void clear(Dist side) {
        applicationCache.remove(side);
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
