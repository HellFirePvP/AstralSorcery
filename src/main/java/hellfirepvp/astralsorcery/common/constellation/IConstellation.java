/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.common.constellation.engraving.EngravingEffect;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IConstellation
 * Created by HellFirePvP
 * Date: 16.11.2016 / 23:04
 */
public interface IConstellation extends IForgeRegistryEntry<IConstellation>, Comparable<IConstellation> {

    // 0-indexed
    public static final int STAR_GRID_INDEX = 31;
    // 1-indexed
    public static final int STAR_GRID_WIDTH_HEIGHT = (STAR_GRID_INDEX + 1);

    /**
     * Should only be called before registering the Constellation.
     */
    public StarLocation addStar(int x, int y);

    /**
     * Should only be called before registering the Constellation.
     */
    public StarConnection addConnection(StarLocation star1, StarLocation star2);

    public int getSortingId();

    public List<StarLocation> getStars();

    public List<StarConnection> getStarConnections();

    public String getSimpleName();

    public String getTranslationKey();

    default public ITextComponent getConstellationName() {
        return new TranslationTextComponent(this.getTranslationKey());
    }

    default public ITextComponent getConstellationTypeDescription() {
        String type = "unknown";
        if (this instanceof IMajorConstellation) {
            type = "major";
        } else if (this instanceof IWeakConstellation) {
            type = "weak";
        } else if (this instanceof IMinorConstellation) {
            type = "minor";
        }
        return new TranslationTextComponent(String.format("astralsorcery.journal.constellation.type.%s", type));
    }

    default public ITextComponent getConstellationTag() {
        return new TranslationTextComponent(this.getTranslationKey() + ".tag");
    }

    default public ITextComponent getConstellationDescription() {
        return new TranslationTextComponent(this.getTranslationKey() + ".description");
    }

    default public ITextComponent getConstellationEnchantmentDescription() {
        return new TranslationTextComponent(this.getTranslationKey() + ".enchantments");
    }

    public static String getDefaultSaveKey() {
        return "constellationName";
    }

    public List<Ingredient> getConstellationSignatureItems();

    @Nullable
    default public EngravingEffect getEngravingEffect() {
        return RegistriesAS.REGISTRY_ENGRAVING_EFFECT.getValue(this.getRegistryName());
    }

    default public IConstellation addSignatureItem(ItemStack item) {
        return this.addSignatureItem(Ingredient.fromStacks(item));
    }

    default public IConstellation addSignatureItem(IItemProvider item) {
        return this.addSignatureItem(Ingredient.fromItems(item));
    }

    default public IConstellation addSignatureItem(Tag<Item> tag) {
        return this.addSignatureItem(Ingredient.fromTag(tag));
    }

    public IConstellation addSignatureItem(Ingredient item);

    public Color getConstellationColor();

    default public Color getTierRenderColor() {
        if (this instanceof IMinorConstellation) {
            return ColorsAS.CONSTELLATION_TYPE_MINOR;
        }
        if (this instanceof IMajorConstellation) {
            return ColorsAS.CONSTELLATION_TYPE_MAJOR;
        }
        return ColorsAS.CONSTELLATION_TYPE_WEAK;
    }

    boolean canDiscover(PlayerEntity player, PlayerProgress progress);

    default public void writeToNBT(CompoundNBT compound) {
        writeToNBT(compound, getDefaultSaveKey());
    }

    default public void writeToNBT(CompoundNBT compound, String key) {
        compound.putString(key, getRegistryName().toString());
    }

    @Nullable
    public static IConstellation readFromNBT(CompoundNBT compound) {
        return readFromNBT(compound, getDefaultSaveKey());
    }

    @Nullable
    public static IConstellation readFromNBT(CompoundNBT compound, String key) {
        return ConstellationRegistry.getConstellation(new ResourceLocation(compound.getString(key)));
    }

    @Override
    default Class<IConstellation> getRegistryType() {
        return IConstellation.class;
    }

}
