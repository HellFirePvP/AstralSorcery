/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.structure;

import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.data.world.data.StructureGenBuffer;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemEnderEye;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.BiomeDictionary;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureTreasureShrine
 * Created by HellFirePvP
 * Date: 22.07.2017 / 17:21
 */
public class StructureTreasureShrine extends WorldGenAttributeStructure {

    public StructureTreasureShrine() {
        super(2, 20, "treasureShrine", () -> MultiBlockArrays.treasureShrine, StructureGenBuffer.StructureType.TREASURE, true);
        this.cfgEntry.setMinY(10);
        this.cfgEntry.setMaxY(40);
        this.idealDistance = 192;
    }

    @Override
    public void generate(BlockPos pos, World world, Random rand) {
        CaveAdjacencyInformation information = validatePosition(pos, world);
        if(information != null) { //Which i'd expect
            this.getStructureTemplate().placeInWorld(world, pos);
            BlockPos offsetPos = pos.add(0, 3, 0).offset(information.direction, 4);
            world.setBlockToAir(offsetPos);
            world.setBlockToAir(offsetPos.up());
            IBlockState mru = BlocksAS.blockMarble.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RUNED);
            IBlockState mrw = BlocksAS.blockMarble.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW);
            for (int i = 0; i < information.tunnelDistance; i++) {
                offsetPos = offsetPos.offset(information.direction);

                world.setBlockToAir(offsetPos);
                world.setBlockToAir(offsetPos.up());

                world.setBlockState(offsetPos.down(), mrw);
                world.setBlockState(offsetPos.up(2), mrw);

                world.setBlockState(offsetPos.up().offset(information.direction.rotateY()), mrw);
                world.setBlockState(offsetPos.offset(information.direction.rotateY()), mru);
                world.setBlockState(offsetPos.up().offset(information.direction.rotateYCCW()), mrw);
                world.setBlockState(offsetPos.offset(information.direction.rotateYCCW()), mru);
            }
            getBuffer(world).markStructureGeneration(pos, StructureGenBuffer.StructureType.TREASURE);
        }
    }

    @Override
    public boolean fulfillsSpecificConditions(BlockPos pos, World world, Random random) {
        return true;
    }

    @Override
    public BlockPos getGenerationPosition(int chX, int chZ, World world, Random rand) {
        BlockPos initial = new BlockPos(chX * 16 + 8, 0, chZ * 16 + 8);
        if(world instanceof WorldServer) {
            try {
                ChunkGeneratorSettings settings = ChunkGeneratorSettings.Factory.jsonToFactory(world.getWorldInfo().getGeneratorOptions()).build();
                if(settings.useStrongholds) {
                    BlockPos blockpos = ((WorldServer) world).getChunkProvider().getNearestStructurePos(world, "Stronghold", initial, false);
                    if(blockpos != null) {
                        double xDst = blockpos.getX() - initial.getX();
                        double zDst = blockpos.getZ() - initial.getZ();
                        float flatDst = MathHelper.sqrt(xDst * xDst + zDst * zDst);
                        if(flatDst <= 20) {
                            return null;
                        }
                    }
                }
            } catch (Exception ignored) {} //Well, then we just don't care about generating into strongholds *shrugs*
        }
        for (int i = 0; i < 15; i++) {
            BlockPos pos = initial.add(rand.nextInt(16), this.cfgEntry.getMinY() + rand.nextInt(this.cfgEntry.getMaxY() - this.cfgEntry.getMinY()), rand.nextInt(16));
            CaveAdjacencyInformation information = validatePosition(pos, world);
            if(information != null) {
                return pos;
            }
        }
        return null;
    }

    @Nullable
    private CaveAdjacencyInformation validatePosition(BlockPos pos, World world) {
        BlockPos.PooledMutableBlockPos move = BlockPos.PooledMutableBlockPos.retain();
        for (int xx = -4; xx <= 4; xx++) {
            for (int zz = -4; zz <= 4; zz++) {
                for (int yy = 0; yy <= 8; yy++) {
                    move.setPos(pos.getX() + xx, pos.getY() + yy, pos.getZ() + zz);
                    IBlockState at = world.getBlockState(move);
                    if(!at.isFullCube()) {
                        move.release();
                        return null;
                    }
                }
            }
        }
        move.release();
        for (EnumFacing face : EnumFacing.HORIZONTALS) {
            BlockPos offsetPos = pos.add(0, 3, 0).offset(face, 4);
            for (int n = 1; n < 4; n++) {
                BlockPos testAt = offsetPos.offset(face, n);
                if(world.isAirBlock(testAt) && world.isAirBlock(testAt.up())) {
                    return new CaveAdjacencyInformation(face, n);
                }
            }
        }
        return null;
    }

    private static class CaveAdjacencyInformation {

        private final EnumFacing direction;
        private final int tunnelDistance;

        private CaveAdjacencyInformation(EnumFacing direction, int tunnelDistance) {
            this.direction = direction;
            this.tunnelDistance = tunnelDistance;
        }

    }

}
