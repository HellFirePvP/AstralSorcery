/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import com.mojang.datafixers.types.Type;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.tile.*;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileEntitiesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileEntitiesAS {

    public static final DeferredRegister<BlockEntityType<?>> TILE_REGISTER =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, AstralSorcery.MODID);

    public static final TileRegistryObject<TileAltar> ALTAR =
            register("altar", TileAltar::new,
                    BlocksAS.ALTAR_ILLUMINATION, BlocksAS.ALTAR_RESONANCE, BlocksAS.ALTAR_LUMINANCE, BlocksAS.ALTAR_RADIANCE);

    public static final TileRegistryObject<TileFocusRelay> FOCUS_RELAY =
            register("focus_relay", TileFocusRelay::new,
                    BlocksAS.FOCUS_RELAY);
    public static final TileRegistryObject<TileCelestialCrystalCluster> CELESTIAL_CRYSTAL_CLUSTER =
            register("celestial_crystal_cluster", TileCelestialCrystalCluster::new,
                    BlocksAS.CELESTIAL_CRYSTAL_CLUSTER);
    public static final TileRegistryObject<TileGemCrystalCluster> GEM_CRYSTAL_CLUSTER =
            register("gem_crystal_cluster", TileGemCrystalCluster::new,
                    BlocksAS.GEM_CRYSTAL_CLUSTER);
    public static final TileRegistryObject<TileLumenCrystalCluster> LUMEN_CRYSTAL_CLUSTER =
            register("lumen_crystal_cluster", TileLumenCrystalCluster::new,
                    BlocksAS.LUMEN_CRYSTAL_CLUSTER);

    public static final TileRegistryObject<TileLumenArray> LUMEN_ARRAY =
            register("lumen_array", TileLumenArray::new,
                    BlocksAS.LUMEN_ARRAY);
    public static final TileRegistryObject<TileLumenAlchemyArray> LUMEN_ALCHEMY_ARRAY =
            register("lumen_alchemy_array", TileLumenAlchemyArray::new,
                    BlocksAS.LUMEN_ALCHEMY_ARRAY);
    public static final TileRegistryObject<TileLumenFilament> LUMEN_FILAMENT =
            register("lumen_filament", TileLumenFilament::new,
                    BlocksAS.LUMEN_FILAMENT);
    public static final TileRegistryObject<TileLumenCrystallizer> LUMEN_CRYSTALLIZER =
            register("lumen_crystallizer", TileLumenCrystallizer::new,
                    BlocksAS.LUMEN_CRYSTALLIZER);
    public static final TileRegistryObject<TileLightwell> LIGHTWELL =
            register("lightwell", TileLightwell::new,
                    BlocksAS.LIGHTWELL);
    public static final TileRegistryObject<TileInfuser> INFUSER =
            register("infuser", TileInfuser::new,
                    BlocksAS.INFUSER);
    public static final TileRegistryObject<TileChalice> CHALICE =
            register("chalice", TileChalice::new,
                    BlocksAS.CHALICE);
    public static final TileRegistryObject<TileAttunementAltar> ATTUNEMENT_ALTAR =
            register("attunement_altar", TileAttunementAltar::new,
                    BlocksAS.ATTUNEMENT_ALTAR);
    public static final TileRegistryObject<TileTranslucentBlock> TRANSLUCENT_BLOCK =
            register("translucent_block", TileTranslucentBlock::new,
                    BlocksAS.TRANSLUCENT_BLOCK);
    public static final TileRegistryObject<TileTranslucentTree> TRANSLUCENT_TREE =
            register("translucent_tree", TileTranslucentTree::new,
                    BlocksAS.TRANSLUCENT_TREE);
    public static final TileRegistryObject<TileTreeBeacon> TREE_BEACON =
            register("tree_beacon", TileTreeBeacon::new,
                    BlocksAS.TREE_BEACON);
    public static final TileRegistryObject<TileCelestialGateway> CELESTIAL_GATEWAY =
            register("celestial_gateway", TileCelestialGateway::new,
                    BlocksAS.CELESTIAL_GATEWAY);

    public static final TileRegistryObject<TileLens> LENS =
            register("lens", TileLens::new,
                    BlocksAS.LENS);
    public static final TileRegistryObject<TilePrism> PRISM =
            register("prism", TilePrism::new,
                    BlocksAS.PRISM);
    public static final TileRegistryObject<TileStarlightFocusCrystal> STARLIGHT_FOCUS_CRYSTAL =
            register("starlight_focus_crystal", TileStarlightFocusCrystal::new,
                    BlocksAS.STARLIGHT_FOCUS_ROCK_CRYSTAL, BlocksAS.STARLIGHT_FOCUS_CELESTIAL_CRYSTAL);
    public static final TileRegistryObject<TileCaveIlluminator> CAVE_ILLUMINATOR =
            register("cave_illuminator", TileCaveIlluminator::new,
                    BlocksAS.CAVE_ILLUMINATOR);

    private static <T extends BlockEntity> TileRegistryObject<T> register(String name,
                                                                          BlockEntityType.BlockEntitySupplier<T> tileCtor,
                                                                          DeferredBlock<?>... validBlocks) {
        String fullKey = AstralSorcery.MODID + "_" + name;
        Type<?> dataFixerType = Util.fetchChoiceType(References.BLOCK_ENTITY, fullKey);
        return new TileRegistryObject<>(TILE_REGISTER.register(name, () -> {
            Block[] blocks = new Block[validBlocks.length];
            for (int i = 0; i < validBlocks.length; i++) {
                blocks[i] = validBlocks[i].value();
            }
            return BlockEntityType.Builder.of(tileCtor, blocks).build(dataFixerType);
        }));
    }
}
