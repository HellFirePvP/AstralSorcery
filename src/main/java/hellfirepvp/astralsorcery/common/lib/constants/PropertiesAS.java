/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib.constants;

import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PropertiesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PropertiesAS {

    public static class Block {

        public static final BlockBehaviour.Properties MARBLE = BlockBehaviour.Properties.of()
                .mapColor(MapColor.QUARTZ)
                .strength(3F, 5F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.STONE);

        public static final BlockBehaviour.Properties MARBLE_NO_OCCLUSION = BlockBehaviour.Properties.of()
                .mapColor(MapColor.QUARTZ)
                .strength(3F, 5F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.STONE)
                .noOcclusion();

        public static final BlockBehaviour.Properties WOODEN_FOLIAGE = BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_GREEN)
                .strength(3F)
                .sound(SoundType.WOOD)
                .requiresCorrectToolForDrops()
                .isValidSpawn(Blocks::never)
                .ignitedByLava()
                .noOcclusion();

        public static final BlockBehaviour.Properties SOOTY_MARBLE = BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BLACK)
                .strength(3F, 5F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.STONE);

        public static final BlockBehaviour.Properties INFUSED_WOOD = BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BROWN)
                .strength(2F, 7F)
                .sound(SoundType.WOOD);

        public static final BlockBehaviour.Properties INFUSED_GLASS = BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                .strength(0.5F, 10F)
                .sound(SoundType.GLASS);

        public static final BlockBehaviour.Properties INFUSED_GLASS_LIT = BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                .strength(0.5F, 10F)
                .sound(SoundType.GLASS)
                .lightLevel(state -> 6);

        public static final BlockBehaviour.Properties CRYSTAL_LIT = BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                .strength(0.5F, 10F)
                .sound(SoundsAS.CRYSTAL_SOUND_TYPE)
                .lightLevel(state -> 8);

        public static final BlockBehaviour.Properties UNBREAKABLE_GLASS_LIT = BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                .strength(-1.0F, 3600000.0F)
                .isValidSpawn(Blocks::never)
                .sound(SoundType.EMPTY)
                .lightLevel(state -> 6)
                .noOcclusion();

        public static final BlockBehaviour.Properties UNBREAKABLE_TRANSLUCENT_BLOCK_LIT = BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                .strength(-1.0F, 3600000.0F)
                .noLootTable()
                .isValidSpawn(Blocks::never)
                .sound(SoundType.EMPTY)
                .lightLevel(state -> 12)
                .noOcclusion();

        public static final BlockBehaviour.Properties AIRY_LIT = BlockBehaviour.Properties.of()
                .noLootTable()
                .isValidSpawn(Blocks::never)
                .mapColor(MapColor.NONE)
                .sound(SoundType.EMPTY)
                .lightLevel(state -> 15)
                .noCollission();

        public static final BlockBehaviour.Properties CHALICE_GOLD_MACHINERY = BlockBehaviour.Properties.of()
                .mapColor(MapColor.GOLD)
                .strength(1.0F, 4.0F)
                .sound(SoundType.STONE)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }

}
