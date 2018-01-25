/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TreeTypes
 * Created by HellFirePvP
 * Date: 11.03.2017 / 22:02
 */
public enum TreeTypes {

    OAK("minecraft", "log", "leaves", "sapling", new int[] {0, 4, 8, 12}, new int[] {0, 4, 8, 12}, 0),
    SPRUCE("minecraft", "log", "leaves", "sapling", new int[] {1, 5, 9, 13}, new int[] {1, 5, 9, 13}, 1),
    BIRCH("minecraft", "log", "leaves", "sapling", new int[] {2, 6, 10, 14}, new int[] {2, 6, 10, 14}, 2),
    JUNGLE("minecraft", "log", "leaves", "sapling", new int[] {3, 7, 11, 15}, new int[] {3, 7, 11, 15}, 3),
    ACACIA("minecraft", "log2", "leaves2", "sapling", new int[] {0, 4, 8, 12}, new int[] {0, 4, 8, 12}, 4),
    DARK_OAK("minecraft", "log2", "leaves2", "sapling", new int[] {1, 5, 9, 13}, new int[] {1, 5, 9, 13}, 5),

    SLIME(Mods.TICONSTRUCT, "slime_congealed", "slime_leaves", "slime_sapling", null, null, null);

    private String parentModId;
    private ResourceLocation resBlockName;
    private ResourceLocation resLeavesName;
    private ResourceLocation resSaplingName;

    private boolean exists = false;

    private Collection<IBlockState> logStates = Lists.newArrayList();
    private Collection<IBlockState> leaveStates = Lists.newArrayList();
    private IBlockState saplingState = null;

    private BlockStateCheck logCheck;
    private BlockStateCheck leavesCheck;
    private BlockStateCheck saplingCheck;

    TreeTypes(Mods parentMod, String resBlockName, String resLeavesName, String resSaplingName, @Nullable int[] logMeta, @Nullable int[] leaveMeta, @Nullable Integer saplingMeta) {
        this(parentMod.modid, new ResourceLocation(parentMod.modid, resBlockName), new ResourceLocation(parentMod.modid, resLeavesName), new ResourceLocation(parentMod.modid, resSaplingName), logMeta, leaveMeta, saplingMeta);
    }

    TreeTypes(Mods parentMod, ResourceLocation resBlockName, ResourceLocation resLeavesName, ResourceLocation resSaplingName, @Nullable int[] logMeta, @Nullable int[] leaveMeta, @Nullable Integer saplingMeta) {
        this(parentMod.modid, resBlockName, resLeavesName, resSaplingName, logMeta, leaveMeta, saplingMeta);
    }

    TreeTypes(String parentModId, String resBlockName, String resLeavesName, String resSaplingName, @Nullable int[] logMeta, @Nullable int[] leaveMeta, @Nullable Integer saplingMeta) {
        this(parentModId, new ResourceLocation(parentModId, resBlockName), new ResourceLocation(parentModId, resLeavesName), new ResourceLocation(parentModId, resSaplingName), logMeta, leaveMeta, saplingMeta);
    }

    TreeTypes(String parentModId, ResourceLocation resBlockName, ResourceLocation resLeavesName, ResourceLocation resSaplingName, @Nullable int[] logMeta, @Nullable int[] leaveMeta, @Nullable Integer saplingMeta) {
        this.parentModId = parentModId;
        this.resBlockName = resBlockName;
        this.resLeavesName = resLeavesName;
        this.resSaplingName = resSaplingName;

        load(logMeta, leaveMeta, saplingMeta);
    }

    private void load(@Nullable int[] logMeta, @Nullable int[] leaveMeta, @Nullable Integer saplingMeta) {
        if (!Loader.isModLoaded(this.parentModId) && !this.parentModId.equals("minecraft")) {
            AstralSorcery.log.info("[AstralSorcery] Not loading tree type " + name() + " as the mod " + this.parentModId + " is not loaded.");
            return;
        }

        Block log = ForgeRegistries.BLOCKS.getValue(this.resBlockName);
        Block leaf = ForgeRegistries.BLOCKS.getValue(this.resLeavesName);
        Block sapling = ForgeRegistries.BLOCKS.getValue(this.resSaplingName);

        if (isEmpty(log) || isEmpty(leaf) || isEmpty(sapling)) {
            AstralSorcery.log.info("[AstralSorcery] Not loading tree type " + name() + " as its blocks don't exist in the currently loaded mods.");
            return;
        }

        logCheck = logMeta == null ? new BlockStateCheck.Block(log) : new BlockStateCheck.AnyMeta(log, logMeta);
        leavesCheck = leaveMeta == null ? new BlockStateCheck.Block(leaf) : new BlockStateCheck.AnyMeta(leaf, leaveMeta);
        saplingCheck = saplingMeta == null ? new BlockStateCheck.Block(sapling) : new BlockStateCheck.Meta(sapling, saplingMeta);

        if (logMeta == null) {
            this.logStates.add(log.getDefaultState());
        } else {
            for (int m : logMeta) {
                IBlockState state = log.getStateFromMeta(m);
                if (!this.logStates.contains(state)) {
                    this.logStates.add(state);
                }
            }
        }

        if (leaveMeta == null) {
            this.leaveStates.add(leaf.getDefaultState());
        } else {
            for (int m : leaveMeta) {
                IBlockState state = leaf.getStateFromMeta(m);
                if (!this.leaveStates.contains(state)) {
                    this.leaveStates.add(state);
                }
            }
        }

        if (saplingMeta == null) {
            this.saplingState = sapling.getDefaultState();
        } else {
            this.saplingState = sapling.getStateFromMeta(saplingMeta);
        }

        exists = true;
        AstralSorcery.log.info("[AstralSorcery] Loaded " + name() + " of " + this.parentModId + " into tree registry.");
    }

    private boolean isEmpty(@Nullable Block block) {
        return block == null || block.equals(Blocks.AIR);
    }

    public boolean exists() {
        return exists;
    }

    public Collection<IBlockState> getLeaveStates() {
        return leaveStates;
    }

    public Collection<IBlockState> getLogStates() {
        return logStates;
    }

    public IBlockState getSaplingState() {
        return saplingState;
    }

    public BlockStateCheck getLogCheck() {
        return logCheck;
    }

    public BlockStateCheck getLeavesCheck() {
        return leavesCheck;
    }

    public BlockStateCheck getSaplingCheck() {
        return saplingCheck;
    }

    @Nullable
    public static TreeTypes getTree(World world, BlockPos pos) {
        return getTree(world, pos, world.getBlockState(pos));
    }

    @Nullable
    public static TreeTypes getTree(World world, BlockPos pos, IBlockState blockToTest) {
        for (TreeTypes type : values()) {
            if (type.exists() && (type.logCheck.isStateValid(world, pos, blockToTest) || type.leavesCheck.isStateValid(world, pos, blockToTest))) {
                return type;
            }
        }
        return null;
    }

    public static void init() {} //Well... all static here.

}
