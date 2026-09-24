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
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;
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

    public Optional<Holder<Enchantment>> getRandomEnchantment() {
        return this.getRandomEntry(Entry::weight).map(Entry::enchantment);
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
            return new Entry(holder, holder.value().definition().weight());
        }).filter(Objects::nonNull).toList();
    }

    public record Entry(Holder<Enchantment> enchantment, int weight) {

        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                RegistryFixedCodec.create(Registries.ENCHANTMENT).fieldOf("enchantment").forGetter(Entry::enchantment),
                Codec.INT.fieldOf("weight").forGetter(Entry::weight)
        ).apply(inst, Entry::new));
    }
}
