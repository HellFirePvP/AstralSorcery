/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileGemCrystals;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
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
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockGemCrystals
 * Created by HellFirePvP
 * Date: 27.11.2018 / 18:57
 */
public class BlockGemCrystals extends BlockContainer implements BlockCustomName, BlockVariants {

    public static final PropertyEnum<GrowthStageType> STAGE = PropertyEnum.create("stage", GrowthStageType.class);

    public BlockGemCrystals() {
        super(Material.ROCK, MapColor.QUARTZ);
        setHardness(2.0F);
        setHarvestLevel("pickaxe", 2);
        setResistance(20.0F);
        setLightLevel(0.3F);
        setSoundType(SoundType.STONE);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        setDefaultState(this.blockState.getBaseState().withProperty(STAGE, GrowthStageType.STAGE_0));
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
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (GrowthStageType stageType : GrowthStageType.values()) {
            list.add(new ItemStack(this, 1, stageType.ordinal()));
        }
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
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return super.getPickBlock(world.getBlockState(pos), target, world, pos, player);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        state.withProperty(STAGE, GrowthStageType.values()[
                MathHelper.clamp(stack.getItemDamage(), 0, GrowthStageType.values().length)]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(STAGE).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(STAGE, GrowthStageType.values()[
                MathHelper.clamp(meta, 0, GrowthStageType.values().length)]);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
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
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, STAGE);
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
    public boolean hasTileEntity() {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileGemCrystals();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileGemCrystals();
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        GrowthStageType type = getStateFromMeta(meta).getValue(STAGE);
        return type.getName();
    }

    @Override
    public List<IBlockState> getValidStates() {
        return singleEnumPropertyStates(getDefaultState(), STAGE, GrowthStageType.values());
    }

    @Override
    public String getStateName(IBlockState state) {
        return state.getValue(STAGE).getName();
    }

    public static enum GrowthStageType implements IStringSerializable {

        STAGE_0,
        STAGE_1,
        STAGE_2_SKY,
        STAGE_2_DAY,
        STAGE_2_NIGHT;

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }

}
