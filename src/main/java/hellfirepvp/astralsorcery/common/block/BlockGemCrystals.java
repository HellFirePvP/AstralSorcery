/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.common.item.gem.ItemPerkGem;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileGemCrystals;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
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

    private static final AxisAlignedBB boxStage0 = new AxisAlignedBB(0.25, 0, 0.25,
            0.75, 0.375, 0.75);
    private static final AxisAlignedBB boxStage1 = new AxisAlignedBB(0.25, 0, 0.25,
            0.75, 0.5, 0.75);
    private static final AxisAlignedBB boxStage2Night = new AxisAlignedBB(0.25, 0, 0.25,
            0.75, 0.5, 0.75);
    private static final AxisAlignedBB boxStage2Sky = new AxisAlignedBB(0.25, 0, 0.25,
            0.75, 0.5625, 0.75);
    private static final AxisAlignedBB boxStage2Day = new AxisAlignedBB(0.25, 0, 0.25,
            0.75, 0.5625, 0.75);

    public BlockGemCrystals() {
        super(Material.ROCK, MapColor.QUARTZ);
        setHardness(2.0F);
        setHarvestLevel("pickaxe", 2);
        setResistance(20.0F);
        setLightLevel(0.3F);
        setSoundType(SoundType.GLASS);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        setDefaultState(this.blockState.getBaseState().withProperty(STAGE, GrowthStageType.STAGE_0));
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
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(STAGE)) {
            case STAGE_0:
                return boxStage0;
            case STAGE_1:
                return boxStage1;
            case STAGE_2_SKY:
                return boxStage2Sky;
            case STAGE_2_DAY:
                return boxStage2Day;
            case STAGE_2_NIGHT:
                return boxStage2Night;
        }
        return super.getBoundingBox(state, source, pos);
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
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ItemStack gem = ItemStack.EMPTY;
        switch (state.getValue(STAGE)) {
            case STAGE_2_SKY:
                gem = ItemPerkGem.GemType.SKY.asStack();
                break;
            case STAGE_2_DAY:
                gem = ItemPerkGem.GemType.DAY.asStack();
                break;
            case STAGE_2_NIGHT:
                gem = ItemPerkGem.GemType.NIGHT.asStack();
                break;
        }
        if (!gem.isEmpty()) {
            drops.add(gem);
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
        TileGemCrystals te = MiscUtils.getTileAt(worldIn, pos, TileGemCrystals.class, true);
        if(te != null && !worldIn.isRemote) {
            PktParticleEvent event = new PktParticleEvent(PktParticleEvent.ParticleEventType.GEM_CRYSTAL_BURST,
                    pos.getX(), pos.getY(), pos.getZ());
            event.setAdditionalDataLong(state.getValue(STAGE).ordinal());
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

        STAGE_0(0, Color.WHITE),
        STAGE_1(1, Color.WHITE),
        STAGE_2_SKY(2, new Color(0x2561B5)),
        STAGE_2_DAY(2, new Color(0xE04C02)),
        STAGE_2_NIGHT(2, new Color(0x808080));

        private final int growthStage;
        private final Color displayColor;

        GrowthStageType(int growthStage, Color displayColor) {
            this.growthStage = growthStage;
            this.displayColor = displayColor;
        }

        public Color getDisplayColor() {
            return displayColor;
        }

        public int getGrowthStage() {
            return growthStage;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }

}
