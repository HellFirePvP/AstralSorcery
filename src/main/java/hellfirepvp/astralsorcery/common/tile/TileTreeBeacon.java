/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBase;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.TreeCaptureHelper;
import hellfirepvp.astralsorcery.common.util.data.NonDuplicateCappedList;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.data.WorldBlockPos;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileTreeBeacon
 * Created by HellFirePvP
 * Date: 30.12.2016 / 13:28
 */
public class TileTreeBeacon extends TileReceiverBase {

    private static final Random rand = new Random();

    private TreeCaptureHelper.TreeWatcher treeWatcher = null;
    private double starlightCharge = 0D;

    private NonDuplicateCappedList<BlockPos> treePositions = new NonDuplicateCappedList<>(MathHelper.floor(ConfigEntryTreeBeacon.maxCount));

    @Override
    public void update() {
        super.update();

        if(world.isRemote) {
            playEffects();
        } else {
            if(treePositions.getCap() != ConfigEntryTreeBeacon.maxCount) {
                treePositions.setCap(MathHelper.floor(ConfigEntryTreeBeacon.maxCount)); //Post-update
            }

            boolean changed = false;
            if(treeWatcher != null) {
                List<WorldBlockPos> possibleTreePositions = TreeCaptureHelper.getAndClearCachedEntries(treeWatcher);
                if(treePositions.getSize() + 30 <= ConfigEntryTreeBeacon.maxCount) {
                    if(searchForTrees(possibleTreePositions)) changed = true;
                }
            }
            int runs = MathHelper.ceil(starlightCharge * 3.4D);
            starlightCharge = 0D;
            for (int i = 0; i < Math.max(8, runs); i++) {
                BlockPos randPos = treePositions.getRandomElementByChance(rand, ConfigEntryTreeBeacon.speedLimiter);
                if(randPos != null) {
                    TileFakeTree tft = MiscUtils.getTileAt(world, randPos, TileFakeTree.class, false);
                    if(tft != null && tft.getFakedState() != null) {
                        IBlockState fake = tft.getFakedState();
                        if(tryHarvestBlock(world, pos, randPos, fake)) { //True, if block disappeared.
                            world.setBlockToAir(randPos);
                            if(treePositions.removeElement(randPos)) {
                                changed = true;
                            }
                        }
                        PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.TREE_VORTEX, randPos);
                        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, randPos, 32));
                    } else {
                        if(treePositions.removeElement(randPos)) {
                            changed = true;
                        }
                    }
                }
            }

            if(changed) {
                markForUpdate();
            }
        }
    }

    private boolean tryHarvestBlock(World world, BlockPos out, BlockPos treeBlockPos, IBlockState fakedState) {
        if(rand.nextInt(ConfigEntryTreeBeacon.dropsChance) == 0) {
            Block b = fakedState.getBlock();
            List<ItemStack> drops = b.getDrops(world, treeBlockPos, fakedState, 2);
            for (ItemStack i : drops) {
                if(i.isEmpty()) continue;
                ItemUtils.dropItemNaturally(world,
                        out.getX() + rand.nextFloat() * 3 * (rand.nextBoolean() ? 1 : -1),
                        out.getY() + rand.nextFloat() * 3,
                        out.getZ() + rand.nextFloat() * 3 * (rand.nextBoolean() ? 1 : -1), i);
            }
        }
        return rand.nextInt(ConfigEntryTreeBeacon.breakChance) == 0;
    }

    private boolean searchForTrees(List<WorldBlockPos> possibleTreePositions) {
        for(WorldBlockPos possibleSapling : possibleTreePositions) {
            IBlockState state = possibleSapling.getStateAt();
            Block b = state.getBlock();
            if(b instanceof IGrowable) { //If it's an IGrowable, chances are it just grows to a tree when we call .grow on it often enough
                if(!TreeCaptureHelper.oneTimeCatches.contains(possibleSapling)) TreeCaptureHelper.oneTimeCatches.add(possibleSapling);

                int tries = 8;
                world.captureBlockSnapshots = true;
                do {
                    tries--;
                    try {
                        ((IGrowable) b).grow(world, rand, possibleSapling, state);
                    } catch (Exception ignored) {}
                    state = possibleSapling.getStateAt();
                    b = state.getBlock();
                } while (b instanceof IGrowable && TreeCaptureHelper.oneTimeCatches.contains(possibleSapling) && tries > 0);
                world.captureBlockSnapshots = false;

                return updatePositionsFromSnapshots(world, possibleSapling, pos);
            } else if(b.getTickRandomly()) { //If it's not an IGrowable it might just grow into a tree with tons of block updates.
                if(!TreeCaptureHelper.oneTimeCatches.contains(possibleSapling)) TreeCaptureHelper.oneTimeCatches.add(possibleSapling);

                int ticksToExecute = 250;
                world.captureBlockSnapshots = true;
                do {
                    ticksToExecute--;
                    try {
                        b.updateTick(world, possibleSapling, state, rand);
                    } catch (Exception ignored) {}
                    state = possibleSapling.getStateAt();
                    b = state.getBlock();
                } while (b.getTickRandomly() && TreeCaptureHelper.oneTimeCatches.contains(possibleSapling) && ticksToExecute > 0);
                world.captureBlockSnapshots = false;

                return updatePositionsFromSnapshots(world, possibleSapling, pos);
            }
        }
        return false;
    }

    private boolean updatePositionsFromSnapshots(World world, WorldBlockPos saplingPos, BlockPos origin) {
        boolean ret = false;
        try {
            if (!TreeCaptureHelper.oneTimeCatches.remove(saplingPos) &&
                    !world.capturedBlockSnapshots.isEmpty() &&
                    world.capturedBlockSnapshots.size() > 2) { //I guess then something grew after all?
                if (treePositions.getSize() + world.capturedBlockSnapshots.size() <= ConfigEntryTreeBeacon.maxCount) {
                    for (BlockSnapshot snapshot : world.capturedBlockSnapshots) {
                        IBlockState setBlock = snapshot.getCurrentBlock();
                        BlockPos at = snapshot.getPos();
                        IBlockState current = world.getBlockState(at);
                        if (current.getBlockHardness(world, at) == -1 || world.getTileEntity(at) != null) {
                            continue;
                        }
                        if (!setBlock.getBlock().equals(BlocksAS.blockFakeTree) && !setBlock.getBlock().equals(Blocks.DIRT) && !setBlock.getBlock().equals(Blocks.GRASS)) {
                            world.setBlockState(snapshot.getPos(), BlocksAS.blockFakeTree.getDefaultState());
                            TileFakeTree tft = MiscUtils.getTileAt(world, snapshot.getPos(), TileFakeTree.class, true);
                            if (tft != null) {
                                tft.setupTile(origin, setBlock);
                            }
                            if (treePositions.offerElement(snapshot.getPos())) ret = true;
                        }
                    }
                } else {
                    for (BlockSnapshot snapshot : world.capturedBlockSnapshots) {
                        IBlockState current = world.getBlockState(snapshot.getPos());
                        world.notifyBlockUpdate(snapshot.getPos(), current, current, 3);
                    }
                }
            }
        } catch (Exception ignored) {
        } finally {
            world.capturedBlockSnapshots.clear();
        }
        return ret;
    }

    @SideOnly(Side.CLIENT)
    private void playEffects() {
        if(rand.nextInt(3) == 0) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    pos.getX() + rand.nextFloat() * 5 * (rand.nextBoolean() ? 1 : -1) + 0.5,
                    pos.getY() + rand.nextFloat() * 2 + 0.5,
                    pos.getZ() + rand.nextFloat() * 5 * (rand.nextBoolean() ? 1 : -1) + 0.5);
            p.motion((rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.45F).setColor(new Color(63, 255, 63)).gravity(0.008).setMaxAge(55);
        }
        if((ticksExisted % 32) == 0) {
            float alphaDaytime = ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(world);
            alphaDaytime *= 0.8F;
            Vector3 from = new Vector3(this).add(0.5, 0.05, 0.5);
            MiscUtils.applyRandomOffset(from, EffectHandler.STATIC_EFFECT_RAND, 0.05F);
            EffectLightbeam lightbeam = EffectHandler.getInstance().lightbeam(from.clone().addY(7), from, 1.5F);
            lightbeam.setAlphaMultiplier(alphaDaytime);
            lightbeam.setColorOverlay(63F / 255F, 1F, 63F / 255F, 1F);
            lightbeam.setMaxAge(64);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void playParticles(PktParticleEvent event) {
        BlockPos fakeTree = event.getVec().toBlockPos();
        TileFakeTree tft = MiscUtils.getTileAt(Minecraft.getMinecraft().world, fakeTree, TileFakeTree.class, false);
        if(tft != null && tft.getReference() != null) {
            Vector3 to = new Vector3(tft.getReference()).add(0.5, 0.5, 0.5);
            for (int i = 0; i < 10; i++) {
                Vector3 from = new Vector3(fakeTree).add(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
                Vector3 mov = to.clone().subtract(from).normalize().multiply(0.1 + 0.1 * rand.nextFloat());
                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(from.getX(), from.getY(), from.getZ());
                p.motion(mov.getX(), mov.getY(), mov.getZ()).setMaxAge(30 + rand.nextInt(25));
                p.gravity(0.004).scale(0.25F).setColor(Color.GREEN);
            }
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();

        TreeCaptureHelper.getAndClearCachedEntries(treeWatcher); //Prevent mem leak
        treeWatcher = null; //Clears weak ref in the TreeCaptureHelper later on automatically.
    }

    @Override
    public void validate() {
        super.validate();

        treeWatcher = new TreeCaptureHelper.TreeWatcher(world.provider.getDimension(), getPos(), ConfigEntryTreeBeacon.treeBeaconRange);
        TreeCaptureHelper.offerWeakWatcher(treeWatcher);
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.starlightCharge = compound.getDouble("starlight");

        treePositions.clear();
        NBTTagList list = compound.getTagList("positions", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            BlockPos pos = NBTUtils.readBlockPosFromNBT(tag);
            treePositions.offerElement(pos);
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setDouble("starlight", this.starlightCharge);

        NBTTagList listPositions = new NBTTagList();
        for (BlockPos pos : treePositions) {
            NBTTagCompound tag = new NBTTagCompound();
            NBTUtils.writeBlockPosToNBT(pos, tag);
            listPositions.appendTag(tag);
        }
        compound.setTag("positions", listPositions);
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.blocktreebeacon.name";
    }

    @Override
    @Nonnull
    public ITransmissionReceiver provideEndpoint(BlockPos at) {
        return new TransmissionReceiverTreeBeacon(at);
    }

    private void receiveStarlight(IWeakConstellation type, double amount) {
        this.starlightCharge += amount * 3;
        if(type == Constellations.aevitas) {
            this.starlightCharge += amount * 3;
        }
    }

    public static class TransmissionReceiverTreeBeacon extends SimpleTransmissionReceiver {

        public TransmissionReceiverTreeBeacon(BlockPos thisPos) {
            super(thisPos);
        }

        @Override
        public void onStarlightReceive(World world, boolean isChunkLoaded, IWeakConstellation type, double amount) {
            if(isChunkLoaded) {
                TileTreeBeacon tw = MiscUtils.getTileAt(world, getPos(), TileTreeBeacon.class, false);
                if(tw != null) {
                    tw.receiveStarlight(type, amount);
                }
            }
        }

        @Override
        public TransmissionClassRegistry.TransmissionProvider getProvider() {
            return new TreeBeaconReceiverProvider();
        }

    }

    public static class TreeBeaconReceiverProvider implements TransmissionClassRegistry.TransmissionProvider {

        @Override
        public TransmissionReceiverTreeBeacon provideEmptyNode() {
            return new TransmissionReceiverTreeBeacon(null);
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":TransmissionReceiverTreeBeacon";
        }

    }

    public static class ConfigEntryTreeBeacon extends ConfigEntry {

        public static final ConfigEntryTreeBeacon instance = new ConfigEntryTreeBeacon();

        public static double treeBeaconRange = 16D;
        public static int maxCount = 600;
        public static int dropsChance = 4;
        public static int breakChance = 500;
        public static float speedLimiter = 1;

        private ConfigEntryTreeBeacon() {
            super(Section.MACHINERY, "treeBeacon");
        }

        @Override
        public void loadFromConfig(Configuration cfg) {
            speedLimiter = cfg.getFloat(getKey() + "EfficiencyLimiter", getConfigurationSection(), 1F, 0F, 1F, "Percentage, how hard the speed limiter should slow down production of the tree beacon. 1=max, 0=no limiter");
            maxCount = cfg.getInt(getKey() + "Count", getConfigurationSection(), 600, 1, 4000, "Defines the amount of blocks the treeBeacon can support at max count");
            treeBeaconRange = cfg.getFloat(getKey() + "Range", getConfigurationSection(), 16F, 4F, 64F, "Defines the Range where the TreeBeacon will scan for Tree's to grow.");
            dropsChance = cfg.getInt(getKey() + "DropsChance", getConfigurationSection(), 4, 1, Integer.MAX_VALUE, "Defines the chance that a drop is generated per random-selection tick. The higher the value the lower the chance.");
            breakChance = cfg.getInt(getKey() + "BreakChance", getConfigurationSection(), 500, 20, Integer.MAX_VALUE, "Defines the chance that the block harvested is going to break per random-selection tick. The higher the value the lower the chance");
        }

    }

}
