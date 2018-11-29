/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.common.block.network.IBlockStarlightRecipient;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.base.ItemRockCrystalBase;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileCelestialCrystals;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.Provider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCelestialCrystals
 * Created by HellFirePvP
 * Date: 14.09.2016 / 23:42
 */
public class BlockCelestialCrystals extends BlockContainer implements IBlockStarlightRecipient, BlockDynamicName {

    private static final Random rand = new Random();

    private final String addedName;
    private final AxisAlignedBB boundingBox;
    private final Provider<IBlockState> nextStage;
    private boolean stageDropCrystals = false;

    public static AxisAlignedBB bbStage0 = new AxisAlignedBB(0.1, 0.0, 0.1, 0.9, 0.3, 0.9);
    public static AxisAlignedBB bbStage1 = new AxisAlignedBB(0.1, 0.0, 0.1, 0.9, 0.4, 0.9);
    public static AxisAlignedBB bbStage2 = new AxisAlignedBB(0.1, 0.0, 0.1, 0.9, 0.5, 0.9);
    public static AxisAlignedBB bbStage3 = new AxisAlignedBB(0.1, 0.0, 0.1, 0.9, 0.6, 0.9);
    public static AxisAlignedBB bbStage4 = new AxisAlignedBB(0.1, 0.0, 0.1, 0.9, 0.7, 0.9);

    public BlockCelestialCrystals(String subName, AxisAlignedBB box, Provider<IBlockState> nextStage) {
        super(Material.ROCK, MapColor.QUARTZ);
        setHardness(2.0F);
        setHarvestLevel("pickaxe", 2);
        setResistance(30.0F);
        setLightLevel(0.4F);
        setSoundType(SoundType.STONE);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);

        this.addedName = subName;
        this.boundingBox = box;
        this.nextStage = nextStage;
    }

    public BlockCelestialCrystals setStageDropCrystals() {
        this.stageDropCrystals = true;
        return this;
    }

    @Nullable
    public IBlockState getNextStage() {
        return nextStage.provide();
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return this.boundingBox;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        return true;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        boolean replaceable = super.canPlaceBlockAt(worldIn, pos);
        if(replaceable) {
            BlockPos down = pos.down();
            if(!worldIn.isSideSolid(down, EnumFacing.UP))
                replaceable = false;
        }
        return replaceable;
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.add(ItemCraftingComponent.MetaType.STARDUST.asStack());
        if (stageDropCrystals) {
            if(world != null && world instanceof World && checkSafety((World) world, pos)) {
                if(fortune > 0 || rand.nextInt(2) == 0) {
                    drops.add(ItemCraftingComponent.MetaType.STARDUST.asStack());
                }
                IBlockState down = world.getBlockState(pos.down());
                boolean hasStarmetal = down.getBlock() instanceof BlockCustomOre &&
                        down.getValue(BlockCustomOre.ORE_TYPE).equals(BlockCustomOre.OreType.STARMETAL);

                ItemStack celCrystal = ItemRockCrystalBase.createRandomCelestialCrystal();
                if(hasStarmetal) {
                    CrystalProperties prop = CrystalProperties.getCrystalProperties(celCrystal);
                    int missing = 100 - prop.getPurity();
                    if(missing > 0) {
                        prop = new CrystalProperties(
                                prop.getSize(),
                                MathHelper.clamp(prop.getPurity() + rand.nextInt(missing) + 1, 0, 100),
                                prop.getCollectiveCapability(),
                                prop.getFracturation(),
                                prop.getSizeOverride());
                        CrystalProperties.applyCrystalProperties(celCrystal, prop);
                    }
                }
                drops.add(celCrystal);
                if(hasStarmetal && rand.nextInt(3) == 0) {
                    drops.add(ItemRockCrystalBase.createRandomCelestialCrystal()); //Lucky~~
                }
            }
        }
    }

    private boolean checkSafety(World world, BlockPos pos) {
        EntityPlayer player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10, false);
        return player != null && player.getDistanceSq(pos) < 100;
    }

    @Override
    public void receiveStarlight(World world, Random rand, BlockPos pos, IWeakConstellation starlightType, double amount) {
        TileCelestialCrystals tile = MiscUtils.getTileAt(world, pos, TileCelestialCrystals.class, false);
        if(tile != null) {
            tile.tryGrowth(0.5);
            IBlockState down = world.getBlockState(pos.down());
            if(down.getBlock() instanceof BlockCustomOre && down.getValue(BlockCustomOre.ORE_TYPE) == BlockCustomOre.OreType.STARMETAL) {
                tile.tryGrowth(0.3);
            }
        }
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        BlockPos down = pos.down();
        IBlockState downState = worldIn.getBlockState(down);
        if(!downState.isSideSolid(worldIn, down, EnumFacing.UP)) {
            dropBlockAsItem(worldIn, pos, state, 0);
            breakBlock(worldIn, pos, state);
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileCelestialCrystals te = MiscUtils.getTileAt(worldIn, pos, TileCelestialCrystals.class, true);
        if(te != null && !worldIn.isRemote) {
            PktParticleEvent event = new PktParticleEvent(PktParticleEvent.ParticleEventType.CELESTIAL_CRYSTAL_BURST,
                    pos.getX(), pos.getY(), pos.getZ());
            PacketChannel.CHANNEL.sendToAllAround(event, PacketChannel.pointFromPos(worldIn, pos, 32));
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCelestialCrystals();
    }

    @Nonnull
    @Override
    public String getChangedName(String currentName) {
        return currentName + "_" + addedName;
    }
}
