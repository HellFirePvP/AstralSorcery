/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.damage;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static hellfirepvp.astralsorcery.common.lib.DamageTypesAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralDamageTypeTagProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralDamageTypeTagProvider extends DamageTypeTagsProvider {

    public AstralDamageTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, AstralSorcery.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(DamageTypeTags.BYPASSES_ARMOR)
                .addOptional(STELLAR.idLocation());
        this.tag(DamageTypeTags.BYPASSES_RESISTANCE)
                .addOptional(STELLAR.idLocation());
        this.tag(DamageTypeTags.WITCH_RESISTANT_TO)
                .addOptional(STELLAR.idLocation());
        this.tag(Tags.DamageTypes.IS_MAGIC)
                .addOptional(STELLAR.idLocation());

        this.tag(TagsAS.DamageTypes.IS_ELEMENTAL)
                .addTag(DamageTypeTags.IS_FIRE)
                .addTag(DamageTypeTags.IS_FREEZING)
                .addTag(DamageTypeTags.IS_LIGHTNING)
                .addTag(DamageTypeTags.IS_DROWNING);
    }
}
