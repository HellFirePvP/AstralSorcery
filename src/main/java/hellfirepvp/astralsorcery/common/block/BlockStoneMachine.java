package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.common.tile.IVariantTileProvider;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStoneMachine
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:11
 */
public class BlockStoneMachine extends BlockContainer implements BlockCustomName, BlockVariants {

    public static PropertyEnum<MachineType> MACHINE_TYPE = PropertyEnum.create("machine", MachineType.class);

    public BlockStoneMachine() {
        super(Material.ROCK, MapColor.GRAY);
        setHardness(3.0F);
        setSoundType(SoundType.STONE);
        setResistance(25.0F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        //setDefaultState(this.blockState.getBaseState().withProperty(MACHINE_TYPE, MachineType.GRINDSTONE));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(MACHINE_TYPE) != null;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (MachineType type : MachineType.values()) {
            list.add(new ItemStack(item, 1, type.ordinal()));
        }
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        MachineType type = state.getValue(MACHINE_TYPE);
        if (type == null) return null;
        return type.provideTileEntity(world, state);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return meta < MachineType.values().length ? getDefaultState().withProperty(MACHINE_TYPE, MachineType.values()[meta]) : getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        MachineType type = state.getValue(MACHINE_TYPE);
        return type == null ? 0 : type.ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, MACHINE_TYPE);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return super.getPickBlock(world.getBlockState(pos), target, world, pos, player); //Waila fix. wtf. why waila. why.
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        MachineType mt = getStateFromMeta(meta).getValue(MACHINE_TYPE);
        return mt == null ? "null" : mt.getName();
    }

    @Override
    public List<IBlockState> getValidStates() {
        List<IBlockState> ret = new LinkedList<>();
        for (MachineType type : MachineType.values()) {
            ret.add(getDefaultState().withProperty(MACHINE_TYPE, type));
        }
        return ret;
    }

    @Override
    public String getStateName(IBlockState state) {
        return state.getValue(MACHINE_TYPE).getName();
    }

    public static enum MachineType implements IStringSerializable, IVariantTileProvider {

        ;

        //Ugly workaround to make constructors nicer
        private final IVariantTileProvider provider;

        private MachineType(IVariantTileProvider provider) {
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
    }

}
