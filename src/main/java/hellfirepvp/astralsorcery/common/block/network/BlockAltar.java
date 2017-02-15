/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.network;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.block.BlockCustomName;
import hellfirepvp.astralsorcery.common.block.BlockVariants;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.IVariantTileProvider;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockAltar
 * Created by HellFirePvP
 * Date: 01.08.2016 / 20:52
 */
public class BlockAltar extends BlockStarlightNetwork implements BlockCustomName, BlockVariants {

    //private static final AxisAlignedBB boxDiscovery =     new AxisAlignedBB(  1D / 16D,  0D,   1D / 16D,        15D / 16D,  15D / 16D,       15D / 16D);
    private static final AxisAlignedBB boxAttenuation =   new AxisAlignedBB(-( 8D / 16D), 0D, -( 8D / 16D), 1D + ( 8D / 16D), 1D + ( 3D / 16D), 1D + ( 8D / 16D));
    private static final AxisAlignedBB boxConstellation = new AxisAlignedBB(-(12D / 16D), 0D, -(12D / 16D), 1D + (12D / 16D), 1D + ( 8D / 16D), 1D + (12D / 16D));

    public static PropertyBool RENDER_FULLY = PropertyBool.create("render");
    public static PropertyEnum<AltarType> ALTAR_TYPE = PropertyEnum.create("altartype", AltarType.class);

    public BlockAltar() {
        super(Material.ROCK, MapColor.GRAY);
        setHardness(3.0F);
        setSoundType(SoundType.STONE);
        setResistance(25.0F);
        setHarvestLevel("pickaxe", 3);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        setDefaultState(this.blockState.getBaseState().withProperty(ALTAR_TYPE, AltarType.ALTAR_1));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote) {
            TileAltar ta = MiscUtils.getTileAt(worldIn, pos, TileAltar.class, true);
            if(ta != null) {
                switch (ta.getAltarLevel()) {
                    case DISCOVERY:
                        AstralSorcery.proxy.openGui(CommonProxy.EnumGuiId.ALTAR_DISCOVERY, playerIn, worldIn, pos.getX(), pos.getY(), pos.getZ());
                        return true;
                    case ATTUNEMENT:
                        AstralSorcery.proxy.openGui(CommonProxy.EnumGuiId.ALTAR_ATTUNEMENT, playerIn, worldIn, pos.getX(), pos.getY(), pos.getZ());
                        return true;
                    case CONSTELLATION_CRAFT:
                        AstralSorcery.proxy.openGui(CommonProxy.EnumGuiId.ALTAR_CONSTELLATION, playerIn, worldIn, pos.getX(), pos.getY(), pos.getZ());
                        return true;
                    case TRAIT_CRAFT:
                        break;
                    case ENDGAME:
                        break;
                }
            }
        }
        return true;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.withProperty(RENDER_FULLY, false);
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        RenderingUtils.playBlockBreakParticles(pos,
                BlocksAS.blockMarble.getDefaultState()
                        .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW));
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        return true;
    }*/

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(ALTAR_TYPE) != null;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (AltarType type : AltarType.values()) {
            if(type == AltarType.ALTAR_5) continue;
            ItemStack stack = new ItemStack(item, 1, type.ordinal());
            NBTTagCompound pers = NBTHelper.getPersistentData(stack);
            pers.setInteger("exp", 0);
            pers.setInteger("lvl", type.ordinal());
            list.add(stack);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        /*TileAltar ta = MiscUtils.getTileAt(source, pos, TileAltar.class, true);
        if(ta != null) {
            TileAltar.AltarLevel al = ta.getAltarLevel();
            switch (al) {
                case DISCOVERY:
                    return boxDiscovery;
                case ATTENUATION:
                    return boxAttenuation;
                case CONSTELLATION_CRAFT:
                    return boxConstellation;
                case TRAIT_CRAFT:
                    break;
                case ENDGAME:
                    break;
            }
        }*/
        AltarType type = state.getValue(ALTAR_TYPE);
        if(type != null) {
            AxisAlignedBB box = type.getBox();
            if(box != null) {
                return box;
            }
        }
        return FULL_BLOCK_AABB;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        AltarType type = state.getValue(ALTAR_TYPE);
        if (type == null) return null;
        return type.provideTileEntity(world, state);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return meta < AltarType.values().length ? getDefaultState().withProperty(ALTAR_TYPE, AltarType.values()[meta]) : getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        AltarType type = state.getValue(ALTAR_TYPE);
        return type == null ? 0 : type.ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ALTAR_TYPE, RENDER_FULLY);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        NBTTagCompound pers = NBTHelper.getPersistentData(stack);
        int exp = pers.getInteger("exp");
        int lvl = pers.getInteger("lvl");
        TileAltar ta = MiscUtils.getTileAt(worldIn, pos, TileAltar.class, true);
        if(ta != null) {
            ta.onPlace(exp, TileAltar.AltarLevel.values()[lvl]);
        }
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);

        if(!worldIn.isRemote && te != null && te instanceof TileAltar) {
            ItemStack out = new ItemStack(BlocksAS.blockAltar, 1, damageDropped(state));
            int exp = ((TileAltar) te).getExperience();
            int levelOrdinal = ((TileAltar) te).getAltarLevel().ordinal();
            NBTTagCompound tag = NBTHelper.getPersistentData(out);
            tag.setInteger("exp", exp);
            tag.setInteger("lvl", levelOrdinal);
            ItemUtils.dropItemNaturally(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, out);
        }
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        /*ItemStack stack = new ItemStack(BlocksAS.blockAltar, 1, damageDropped(state));
        TileAltar ta = MiscUtils.getTileAt(world, pos, TileAltar.class);
        if(ta != null) {
            int exp = ta.getExperience();
            int levelOrdinal = ta.getAltarLevel().ordinal();
            NBTTagCompound tag = ItemNBTHelper.getPersistentData(stack);
            tag.setInteger("exp", exp);
            tag.setInteger("lvl", levelOrdinal);
        }*/
        return new LinkedList<>();
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        IBlockState actState = world.getBlockState(pos);
        ItemStack stack = super.getPickBlock(actState, target, world, pos, player); //Waila fix. wtf. why waila. why.
        TileAltar te = MiscUtils.getTileAt(world, pos, TileAltar.class, true);
        if(te != null) {
            int exp = te.getExperience();
            int levelOrdinal = te.getAltarLevel().ordinal();
            NBTTagCompound tag = NBTHelper.getPersistentData(stack);
            tag.setInteger("exp", exp);
            tag.setInteger("lvl", levelOrdinal);
        }
        return stack;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack) {
        return getStateFromMeta(meta);
    }

    @Override
    public boolean isFullCube(IBlockState state) { return false; }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        AltarType mt = getStateFromMeta(meta).getValue(ALTAR_TYPE);
        return mt == null ? "null" : mt.getName();
    }

    @Override
    public List<IBlockState> getValidStates() {
        List<IBlockState> ret = new LinkedList<>();
        for (AltarType type : AltarType.values()) {
            ret.add(getDefaultState().withProperty(ALTAR_TYPE, type));
        }
        return ret;
    }

    @Override
    public String getStateName(IBlockState state) {
        return state.getValue(ALTAR_TYPE).getName();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    public static enum AltarType implements IStringSerializable, IVariantTileProvider {

        ALTAR_1((world, state) -> new TileAltar(TileAltar.AltarLevel.DISCOVERY)),
        ALTAR_2((world, state) -> new TileAltar(TileAltar.AltarLevel.ATTUNEMENT)),
        ALTAR_3((world, state) -> new TileAltar(TileAltar.AltarLevel.CONSTELLATION_CRAFT)),
        ALTAR_4((world, state) -> new TileAltar(TileAltar.AltarLevel.TRAIT_CRAFT)),
        ALTAR_5((world, state) -> new TileAltar(TileAltar.AltarLevel.ENDGAME));

        //Ugly workaround to make constructors nicer
        private final IVariantTileProvider provider;

        private AltarType(IVariantTileProvider provider) {
            this.provider = provider;
        }

        @Override
        public TileEntity provideTileEntity(World world, IBlockState state) {
            return provider.provideTileEntity(world, state);
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        @Override
        public String toString() {
            return getName();
        }

        public AxisAlignedBB getBox() {
            switch (this) {
                case ALTAR_1:
                    return FULL_BLOCK_AABB;
                case ALTAR_2:
                    return FULL_BLOCK_AABB;
                    //return boxAttenuation;
                case ALTAR_3:
                    return FULL_BLOCK_AABB;
                    //return boxConstellation;
                case ALTAR_4:
                    return null;
                case ALTAR_5:
                    return null;
            }
            return null;
        }
    }
}
