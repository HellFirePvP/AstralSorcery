/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.config.json.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.config.json.JsonDataRegistry;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AmuletEnchantmentDataRegistry
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AmuletEnchantmentDataRegistry extends JsonDataRegistry<AmuletEnchantmentDataRegistry.Entry> {

    private static final AmuletEnchantmentDataRegistry INSTANCE = new AmuletEnchantmentDataRegistry();

    private AmuletEnchantmentDataRegistry() {}

    public static AmuletEnchantmentDataRegistry getInstance() {
        return INSTANCE;
    }

    public Optional<Holder<Enchantment>> getRandomEnchantment(HolderLookup.Provider registries) {
        HolderLookup.RegistryLookup<Enchantment> enchRegistry = registries.lookupOrThrow(Registries.ENCHANTMENT);
        List<ResolvedEntry> entries = this.getLoadedValues().stream()
                .map(entry -> entry.resolve(enchRegistry))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        return MiscUtil.getWeightedRandomEntry(entries, rand, ResolvedEntry::weight).map(ResolvedEntry::enchantment);
    }

    @Override
    public Codec<Entry> elementCodec() {
        return Entry.CODEC;
    }

    @Override
    public List<Entry> getDefaultValues() {
        MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
        Registry<Enchantment> registry = srv.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        return registry.holders().map(holder -> {
            if (holder.is(EnchantmentTags.CURSE)) return null;
            return new Entry(holder.key(), holder.value().definition().weight());
        }).filter(Objects::nonNull).toList();
    }

    public record Entry(ResourceKey<Enchantment> enchantment, int weight) {

        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                ResourceKey.codec(Registries.ENCHANTMENT).fieldOf("enchantment").forGetter(Entry::enchantment),
                Codec.INT.fieldOf("weight").forGetter(Entry::weight)
        ).apply(inst, Entry::new));

        private Optional<ResolvedEntry> resolve(HolderLookup.RegistryLookup<Enchantment> registry) {
            return registry.get(this.enchantment).map(ref -> new ResolvedEntry(ref, this.weight));
        }
    }

    private record ResolvedEntry(Holder<Enchantment> enchantment, int weight) {}
}
