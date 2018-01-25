/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.RockCrystalBuffer;
import hellfirepvp.astralsorcery.common.item.crystal.base.ItemRockCrystalBase;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCrystalOre
 * Created by HellFirePvP
 * Date: 07.05.2016 / 18:03
 */
public class BlockCustomOre extends Block implements BlockCustomName, BlockVariants {

    public static boolean allowCrystalHarvest = false;
    private static final Random rand = new Random();

    public static PropertyEnum<OreType> ORE_TYPE = PropertyEnum.create("oretype", OreType.class);

    public BlockCustomOre() {
        super(Material.ROCK, MapColor.GRAY);
        setHardness(3.0F);
        setHarvestLevel("pickaxe", 3);
        setResistance(25.0F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);

        if(state.getValue(ORE_TYPE).equals(OreType.STARMETAL)) {
            ((RockCrystalBuffer) WorldCacheManager.getOrLoadData(worldIn, WorldCacheManager.SaveKey.ROCK_CRYSTAL)).removeOre(pos);
        }
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (OreType t : OreType.values()) {
            list.add(new ItemStack(this, 1, t.ordinal()));
        }
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        OreType type = state.getValue(ORE_TYPE);
        return type.getMeta();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return meta < OreType.values().length ? getDefaultState().withProperty(ORE_TYPE, OreType.values()[meta]) : getDefaultState();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ORE_TYPE);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
        OreType type = state.getValue(ORE_TYPE);
        if(type != OreType.ROCK_CRYSTAL || (allowCrystalHarvest || (securityCheck(worldIn, pos, player) && checkSafety(worldIn, pos)))) {
            super.harvestBlock(worldIn, player, pos, state, te, stack);
        }
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        OreType type = state.getValue(ORE_TYPE);
        List<ItemStack> drops = new ArrayList<>();
        switch (type) {
            case ROCK_CRYSTAL:
                if(world != null && world instanceof World && (allowCrystalHarvest || (checkSafety((World) world, pos) && securityCheck((World) world, pos, harvesters.get())))) {
                    drops.add(ItemRockCrystalBase.createRandomBaseCrystal());
                    for (int i = 0; i < (fortune + 1); i++) {
                        if(((World) world).rand.nextBoolean()) {
                            drops.add(ItemRockCrystalBase.createRandomBaseCrystal());
                        }
                    }
                    if(((World) world).rand.nextBoolean()) {
                        drops.add(ItemRockCrystalBase.createRandomBaseCrystal());
                    }
                }
                break;
            case STARMETAL:
                drops.add(new ItemStack(this, 1, OreType.STARMETAL.ordinal()));
                break;
        }
        return drops;
    }

    private boolean securityCheck(World world, BlockPos pos, EntityPlayer player) {
        return !world.isRemote && player != null && !MiscUtils.isPlayerFakeMP((EntityPlayerMP) player);
    }

    private boolean checkSafety(World world, BlockPos pos) {
        EntityPlayer player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10, false);
        return player != null && player.getDistanceSq(pos) < 100;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }


    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return true;
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return true;
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        OreType ot = getStateFromMeta(meta).getValue(ORE_TYPE);
        return ot.getName();
    }

    @Override
    public List<IBlockState> getValidStates() {
        return singleEnumPropertyStates(getDefaultState(), ORE_TYPE, OreType.values());
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        OreType ot = state.getValue(ORE_TYPE);
        if(ot == OreType.ROCK_CRYSTAL) {
            if(Config.rockCrystalOreSilkTouchHarvestable) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getStateName(IBlockState state) {
        return state.getValue(ORE_TYPE).getName();
    }

    @SideOnly(Side.CLIENT)
    public static void playStarmetalOreEffects(PktParticleEvent event) {
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                event.getVec().getX() + rand.nextFloat(),
                event.getVec().getY() + rand.nextFloat(),
                event.getVec().getZ() + rand.nextFloat());
        p.motion(0, rand.nextFloat() * 0.05, 0);
        p.scale(0.2F);
    }

    public static enum OreType implements IStringSerializable {

        ROCK_CRYSTAL(0),
        STARMETAL(1);

        private final int meta;

        private OreType(int meta) {
            this.meta = meta;
        }

        public ItemStack asStack() {
            return new ItemStack(BlocksAS.customOre, 1, meta);
        }

        public int getMeta() {
            return meta;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }


}
