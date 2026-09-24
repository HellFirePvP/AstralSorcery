/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.data.builder;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.perk.data.RawPerkData;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkDataProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class PerkDataProvider implements DataProvider {

    private static final boolean generateFullTree = false;

    protected final PackOutput.PathProvider pathProvider;
    protected final CompletableFuture<HolderLookup.Provider> registries;

    protected PerkDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "perks");
        this.registries = registries;
    }

    public abstract void registerPerks(HolderLookup.Provider registries, Consumer<BuiltPerk> registrar);

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return this.registries.thenCompose(provider -> {
            List<CompletableFuture<?>> perkRegisters = new ArrayList<>();
            List<BuiltPerk> builtPerks = new ArrayList<>();

            this.registerPerks(provider, builtPerk -> {
                ResourceLocation perkName = builtPerk.getRawPerk().perk().getKey();
                if (builtPerks.contains(builtPerk)) {
                    throw new IllegalArgumentException("Duplicate perk: " + perkName);
                }
                builtPerks.add(builtPerk);

                Path path = this.pathProvider.json(perkName);
                perkRegisters.add(DataProvider.saveStable(output, provider, RawPerkData.CODEC, builtPerk.getRawPerk(), path));
            });

            if (generateFullTree) {
                List<RawPerkData> builtRawPerks = builtPerks.stream()
                        .map(BuiltPerk::getRawPerk)
                        .sorted(Comparator.comparing(rpd -> rpd.perk().getKey()))
                        .toList();

                Path allPerksPath = this.pathProvider.json(AstralSorcery.key("_full_tree"));
                perkRegisters.add(DataProvider.saveStable(output, provider, RawPerkData.CODEC.listOf(), builtRawPerks, allPerksPath));
            }

            return CompletableFuture.allOf(perkRegisters.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "Perks";
    }

    public static final class BuiltPerk implements Comparable<BuiltPerk> {

        private final RawPerkData rawPerk;
        private final ResourceLocation comparisonKey;

        private BuiltPerk(RawPerkData rawPerk) {
            this.rawPerk = rawPerk;
            this.comparisonKey = rawPerk.perk().getKey();
        }

        public static BuiltPerk of(PerkDataBuilder<?> builder) {
            return new BuiltPerk(new RawPerkData(builder.perk, builder.connections));
        }

        public RawPerkData getRawPerk() {
            return this.rawPerk;
        }

        @Override
        public int compareTo(@Nonnull BuiltPerk o) {
            return this.comparisonKey.compareTo(o.comparisonKey);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (BuiltPerk) obj;
            return Objects.equals(this.comparisonKey, that.comparisonKey);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.comparisonKey);
        }
    }
}
