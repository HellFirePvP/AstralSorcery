package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionList;
import hellfirepvp.astralsorcery.common.constellation.effect.GenListEntries;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.tile.TileFakeTree;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.TreeCaptureHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.data.WorldBlockPos;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectAra
 * Created by HellFirePvP
 * Date: 11.11.2016 / 21:25
 */
public class CEffectAra extends CEffectPositionList {

    //TODO ask mezz to include if (!TerrainGen.saplingGrowTree(worldIn, rand, pos)) return; in his sapling things.

    public static boolean enabled = true;
    public static double potencyMultiplier = 1;

    public static int maxCount = 800;
    public static double treeRange = 16D;

    public static int breakChance = 500;
    public static int dropsChance = 5;

    public WeakReference<TreeCaptureHelper.TreeWatcher> refTreeWatcher = null;

    public CEffectAra() {
        super(Constellations.ara, "ara", 0, maxCount, (world, pos) -> false);
    }
    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility, boolean extendedEffects) {
        if(!Minecraft.isFancyGraphicsEnabled() && rand.nextInt(12) != 0) return;
        if(rand.nextBoolean()) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    pos.getX() + rand.nextFloat() * 5 * (rand.nextBoolean() ? 1 : -1) + 0.5,
                    pos.getY() + rand.nextFloat() * 2 + 0.5,
                    pos.getZ() + rand.nextFloat() * 5 * (rand.nextBoolean() ? 1 : -1) + 0.5);
            p.motion((rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.45F).setColor(new Color(63, 255, 63)).setMaxAge(35);
        }
    }

    @Override
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable Constellation possibleTraitEffect) {
        if(!enabled || refTreeWatcher.get() == null) return false;

        boolean ret = false;
        TreeCaptureHelper.TreeWatcher tw = refTreeWatcher.get();
        List<WorldBlockPos> positions = TreeCaptureHelper.getAndClearCachedEntries(tw);
        if(getElementCount() + 30 <= maxCount) { //At that point it's likely we don't have any space anymore anyway.
            if(searchForTrees(world, pos, positions)) ret = true;
        }

        //Only done here due to the inconsistency that comes from this potentially.
        percStrength *= potencyMultiplier;
        if(percStrength < 1) {
            if(world.rand.nextFloat() > percStrength) return ret;
        }

        GenListEntries.SimpleBlockPosEntry posEntry = getRandomElementByChance(rand);
        if(posEntry != null) {
            TileFakeTree tft = MiscUtils.getTileAt(world, posEntry.getPos(), TileFakeTree.class, false);
            if(tft != null && tft.getFakedState() != null) {
                IBlockState fake = tft.getFakedState();
                if(tryHarvest(world, pos, posEntry, fake)) { //True, if block disappeared.
                    world.setBlockToAir(posEntry.getPos());
                    removeElement(posEntry);
                    ret = true;
                }
                PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_TREE_VORTEX, posEntry.getPos());
                PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, posEntry.getPos(), 64));
            } else {
                removeElement(posEntry);
                ret = true;
            }
        }

        return ret;
    }

    private boolean tryHarvest(World world, BlockPos out, GenListEntries.SimpleBlockPosEntry posEntry, IBlockState fakedState) {
        if(rand.nextInt(dropsChance) == 0) {
            BlockPos at = posEntry.getPos();
            Block b = fakedState.getBlock();
            List<ItemStack> drops = b.getDrops(world, at, fakedState, 2);
            for (ItemStack i : drops) {
                if(i == null || i.getItem() == null) continue;
                ItemUtils.dropItemNaturally(world,
                        out.getX() + rand.nextFloat() * 3 * (rand.nextBoolean() ? 1 : -1),
                        out.getY() + rand.nextFloat() * 3,
                        out.getZ() + rand.nextFloat() * 3 * (rand.nextBoolean() ? 1 : -1), i);
            }
        } else if(rand.nextInt(breakChance) == 0) {
            return true;
        }
        return false;
    }

    private boolean searchForTrees(World world, BlockPos pos, List<WorldBlockPos> positions) {
        for(WorldBlockPos possibleSapling : positions) {
            IBlockState state = possibleSapling.getStateAt();
            Block b = state.getBlock();
            if(b instanceof IGrowable) {
                if(!TreeCaptureHelper.oneTimeCatches.contains(possibleSapling)) TreeCaptureHelper.oneTimeCatches.add(possibleSapling);

                int tries = 8;
                world.captureBlockSnapshots = true;
                do {
                    tries--;
                    try {
                        ((IGrowable) b).grow(world, rand, possibleSapling, state);
                    } catch (Exception ignored) {}
                } while (TreeCaptureHelper.oneTimeCatches.contains(possibleSapling) || tries > 0);
                world.captureBlockSnapshots = false;

                return updatePositionsFromSnapshots(world, possibleSapling, pos);
            } else if(b.getTickRandomly()) {
                if(!TreeCaptureHelper.oneTimeCatches.contains(possibleSapling)) TreeCaptureHelper.oneTimeCatches.add(possibleSapling);

                int ticksToExecute = 250;
                world.captureBlockSnapshots = true;
                do {
                    ticksToExecute--;
                    try {
                        b.updateTick(world, possibleSapling, state, rand);
                    } catch (Exception ignored) {}
                } while (TreeCaptureHelper.oneTimeCatches.contains(possibleSapling) || ticksToExecute > 0);
                world.captureBlockSnapshots = false;

                return updatePositionsFromSnapshots(world, possibleSapling, pos);
            }
        }
        return false;
    }

    private boolean updatePositionsFromSnapshots(World world, WorldBlockPos saplingPos, BlockPos origin) {
        boolean ret = false;
        if(!TreeCaptureHelper.oneTimeCatches.remove(saplingPos) &&
                !world.capturedBlockSnapshots.isEmpty() &&
                world.capturedBlockSnapshots.size() > 2) { //I guess then something grew after all?
            if(getElementCount() + world.capturedBlockSnapshots.size() <= maxCount) {
                for (BlockSnapshot snapshot : world.capturedBlockSnapshots) {
                    IBlockState setBlock = snapshot.getCurrentBlock();
                    if(!setBlock.getBlock().equals(BlocksAS.blockFakeTree) && !setBlock.getBlock().equals(Blocks.DIRT) && !setBlock.getBlock().equals(Blocks.GRASS)) {
                        world.setBlockState(snapshot.getPos(), BlocksAS.blockFakeTree.getDefaultState());
                        TileFakeTree tft = MiscUtils.getTileAt(world, snapshot.getPos(), TileFakeTree.class, true);
                        if(tft != null) {
                            tft.setupTile(origin, setBlock);
                        }
                        if(offerNewBlockPos(snapshot.getPos())) ret = true;
                    }
                }
            } else {
                for (BlockSnapshot snapshot : world.capturedBlockSnapshots) {
                    IBlockState current = world.getBlockState(snapshot.getPos());
                    world.notifyBlockUpdate(snapshot.getPos(), current, current, 3);
                }
            }
        }
        world.capturedBlockSnapshots.clear();
        return ret;
    }

    @Override
    public boolean playTraitEffect(World world, BlockPos pos, Constellation traitType, float traitStrength) {
        return false;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        enabled = cfg.getBoolean(getKey() + "Enabled", getConfigurationSection(), true, "Set to false to disable this ConstellationEffect.");
        maxCount = cfg.getInt(getKey() + "Count", getConfigurationSection(), 800, 1, 4000, "Defines the amount of block-positions the ritual can cache at max count");
        potencyMultiplier = cfg.getFloat(getKey() + "PotencyMultiplier", getConfigurationSection(), 1.0F, 0.01F, 100F, "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");
        treeRange = cfg.getFloat(getKey() + "TreeRange", getConfigurationSection(), 16F, 2F, 64F, "Defines the range in which the ritual will look for saplings when they're growing.");

        dropsChance = cfg.getInt(getKey() + "DropsChance", getConfigurationSection(), 5, 1, Integer.MAX_VALUE, "Defines the chance that a drop is generated per random-selection tick. The higher the value the lower the chance.");
        breakChance = cfg.getInt(getKey() + "BreakChance", getConfigurationSection(), 500, 20, Integer.MAX_VALUE, "Defines the chance that the block harvested is going to break per random-selection tick. The higher the value the lower the chance");
    }

    @SideOnly(Side.CLIENT)
    public static void playParticles(PktParticleEvent event) {
        BlockPos fakeTree = event.getVec().toBlockPos();
        TileFakeTree tft = MiscUtils.getTileAt(Minecraft.getMinecraft().theWorld, fakeTree, TileFakeTree.class, false);
        if(tft != null && tft.getReference() != null) {
            Vector3 to = new Vector3(tft.getReference()).add(0.5, 0.5, 0.5);
            int particleCount = Minecraft.isFancyGraphicsEnabled() ? 10 : 2;
            for (int i = 0; i < particleCount; i++) {
                Vector3 from = new Vector3(fakeTree).add(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
                Vector3 mov = to.clone().subtract(from).normalize().multiply(0.1 + 0.1 * rand.nextFloat());
                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(from.getX(), from.getY(), from.getZ());
                p.motion(mov.getX(), mov.getY(), mov.getZ()).setMaxAge(30 + rand.nextInt(25));
                p.gravity(0.004).scale(0.25F).setColor(Color.GREEN);
            }
        }
    }

}
