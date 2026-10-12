/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.config.json.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.common.config.json.JsonDataRegistry;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityNetwork;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileTickManipulationRegistry
 * Created by HellFirePvP
 * Date: 09.10.2026 / 12:30
 */
public class TileTickManipulationRegistry extends JsonDataRegistry<TileTickManipulationRegistry.Entry> {

    private static final TileTickManipulationRegistry INSTANCE = new TileTickManipulationRegistry();

    private final List<Class<?>> erroredTiles = new ArrayList<>();

    private TileTickManipulationRegistry() {}

    public static TileTickManipulationRegistry getInstance() {
        return INSTANCE;
    }

    public boolean canBeAffected(BlockEntity entity) {
        Class<?> clazz = entity.getClass();
        if (this.erroredTiles.contains(clazz)) return false;

        for (Class<?> excluded : this.erroredTiles) {
            if (excluded.isAssignableFrom(clazz)) return false;
        }

        for (Entry entry : this.getLoadedValues()) {
            if (entry.matches(entity)) return false;
        }
        return true;
    }

    public void addErrored(BlockEntity tile) {
        Class<?> clazz = tile.getClass();
        if (!this.erroredTiles.contains(clazz)) {
            this.erroredTiles.add(clazz);
        }
    }

    @Override
    public Codec<Entry> elementCodec() {
        return Entry.CODEC;
    }

    @Override
    public List<Entry> getDefaultValues() {
        return List.of(
                // if you're another mod looking to blacklist stuff, here you go. stops accel and timestop
                new Entry(Either.left(TagsAS.BlockEntity.MAY_NOT_TICK_MANIPULATE)),

                //Exclude storage mods' things
                new Entry(Either.right("appeng")),
                new Entry(Either.right("com.refinedmods")),

                new Entry(Either.right(TileEntityNetwork.class.getName()))
        );
    }

    public record Entry(Either<TagKey<BlockEntityType<?>>, String> tileFilter) {

        public static final Codec<Entry> CODEC = Codec.either(TagKey.codec(Registries.BLOCK_ENTITY_TYPE), Codec.STRING)
                .xmap(Entry::new, Entry::tileFilter);

        public boolean matches(BlockEntity entity) {
            return this.tileFilter().map(
                    tag -> entity.getType().builtInRegistryHolder().is(tag),
                    classPart -> entity.getClass().getName().toLowerCase(Locale.ROOT).startsWith(classPart)
            );
        }
    }
}
