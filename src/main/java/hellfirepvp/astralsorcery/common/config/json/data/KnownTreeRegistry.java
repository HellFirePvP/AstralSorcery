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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: KnownTreeRegistry
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class KnownTreeRegistry extends JsonDataRegistry<KnownTreeRegistry.TreeEntry> {

    private static final KnownTreeRegistry INSTANCE = new KnownTreeRegistry();

    private KnownTreeRegistry() {}

    public static KnownTreeRegistry getInstance() {
        return INSTANCE;
    }

    public List<TreeEntry> getMatchingTreesByLog(BlockState state) {
        List<TreeEntry> configuredTrees = new ArrayList<>();
        this.getLoadedValues().stream().filter(tree -> tree.isLog(state)).forEach(configuredTrees::add);
        if (state.is(BlockTags.LOGS)) {
            configuredTrees.add(new GenericTree(state.getBlock()));
        }
        return configuredTrees;
    }

    @Override
    public Codec<TreeEntry> elementCodec() {
        return TreeEntry.CODEC;
    }

    @Override
    public List<TreeEntry> getDefaultValues() {
        return List.of(
                TreeEntry.empty()
                        .addLog(BlockTags.OAK_LOGS)
                        .addLeaf(Blocks.OAK_LEAVES),
                TreeEntry.empty()
                        .addLog(BlockTags.DARK_OAK_LOGS)
                        .addLeaf(Blocks.DARK_OAK_LEAVES),
                TreeEntry.empty()
                        .addLog(BlockTags.BIRCH_LOGS)
                        .addLeaf(Blocks.BIRCH_LEAVES),
                TreeEntry.empty()
                        .addLog(BlockTags.ACACIA_LOGS)
                        .addLeaf(Blocks.ACACIA_LEAVES),
                TreeEntry.empty()
                        .addLog(BlockTags.CHERRY_LOGS)
                        .addLeaf(Blocks.CHERRY_LEAVES),
                TreeEntry.empty()
                        .addLog(BlockTags.JUNGLE_LOGS)
                        .addLeaf(Blocks.JUNGLE_LEAVES),
                TreeEntry.empty()
                        .addLog(BlockTags.SPRUCE_LOGS)
                        .addLeaf(Blocks.SPRUCE_LEAVES),
                TreeEntry.empty()
                        .addLog(BlockTags.MANGROVE_LOGS)
                        .addLog(Blocks.MANGROVE_ROOTS, Blocks.MUDDY_MANGROVE_ROOTS)
                        .addLeaf(Blocks.MANGROVE_LEAVES),
                TreeEntry.empty()
                        .addLog(BlockTags.OAK_LOGS)
                        .addLeaf(Blocks.AZALEA_LEAVES, Blocks.FLOWERING_AZALEA_LEAVES)
        );
    }

    public static class TreeEntry {

        public static final Codec<TreeEntry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                ResourceKey.codec(Registries.BLOCK).listOf().fieldOf("logBlocks").forGetter(e -> e.logBlocks),
                TagKey.codec(Registries.BLOCK).listOf().fieldOf("logTags").forGetter(e -> e.logTags),
                ResourceKey.codec(Registries.BLOCK).listOf().fieldOf("leafBlocks").forGetter(e -> e.leafBlocks),
                TagKey.codec(Registries.BLOCK).listOf().fieldOf("leafTags").forGetter(e -> e.leafTags)
        ).apply(inst, TreeEntry::new));

        private final List<ResourceKey<Block>> logBlocks = new ArrayList<>();
        private final List<TagKey<Block>> logTags = new ArrayList<>();
        private final List<ResourceKey<Block>> leafBlocks = new ArrayList<>();
        private final List<TagKey<Block>> leafTags = new ArrayList<>();

        private TreeEntry() {}

        private TreeEntry(List<ResourceKey<Block>> logBlocks,
                         List<TagKey<Block>> logTags,
                         List<ResourceKey<Block>> leafBlocks,
                         List<TagKey<Block>> leafTags) {
            this.logBlocks.addAll(logBlocks);
            this.logTags.addAll(logTags);
            this.leafBlocks.addAll(leafBlocks);
            this.leafTags.addAll(leafTags);
        }

        public static TreeEntry empty() {
            return new TreeEntry();
        }

        public TreeEntry addLog(Block... logs) {
            for (Block log : logs) {
                BuiltInRegistries.BLOCK.getResourceKey(log).ifPresent(this.logBlocks::add);
            }
            return this;
        }

        public TreeEntry addLog(ResourceKey<Block>... logs) {
            this.logBlocks.addAll(Arrays.asList(logs));
            return this;
        }

        public TreeEntry addLog(TagKey<Block>... logTags) {
            this.logTags.addAll(Arrays.asList(logTags));
            return this;
        }
        public TreeEntry addLeaf(Block... leaves) {
            for (Block leaf : leaves) {
                BuiltInRegistries.BLOCK.getResourceKey(leaf).ifPresent(this.leafBlocks::add);
            }
            return this;
        }

        public TreeEntry addLeaf(ResourceKey<Block>... leaves) {
            this.leafBlocks.addAll(Arrays.asList(leaves));
            return this;
        }

        public TreeEntry addLeaf(TagKey<Block>... leafTags) {
            this.leafTags.addAll(Arrays.asList(leafTags));
            return this;
        }

        public boolean isLog(BlockState state) {
            for (ResourceKey<Block> log : this.logBlocks) {
                if (state.is(log)) return true;
            }
            for (TagKey<Block> logTag : this.logTags) {
                if (state.is(logTag)) return true;
            }
            return false;
        }

        public boolean isLeaf(BlockState state) {
            for (ResourceKey<Block> leaf : this.leafBlocks) {
                if (state.is(leaf)) return true;
            }
            for (TagKey<Block> leafTag : this.leafTags) {
                if (state.is(leafTag)) return true;
            }
            return false;
        }
    }

    public static class GenericTree extends TreeEntry {

        private final Block capturedLog;
        private Block capturedLeaf;

        public GenericTree(Block capturedLog) {
            this.capturedLog = capturedLog;
        }

        @Override
        public boolean isLog(BlockState state) {
            return state.is(this.capturedLog);
        }

        @Override
        public boolean isLeaf(BlockState state) {
            if (this.capturedLeaf == null) {
                if (!state.is(BlockTags.LEAVES)) return false;
                this.capturedLeaf = state.getBlock();
            }
            return state.is(this.capturedLeaf);
        }
    }
}
