package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingSprite;
import hellfirepvp.astralsorcery.client.util.PositionedLoopSound;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBase;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.SoundHelper;
import hellfirepvp.astralsorcery.common.util.SoundUtils;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileAttunementAltar
 * Created by HellFirePvP
 * Date: 28.11.2016 / 10:26
 */
public class TileAttunementAltar extends TileReceiverBase {

    private IMajorConstellation activeFound = null;
    private boolean doesSeeSky = false, hasMultiblock = false;

    private Map<BlockPos, Boolean> unloadCache = new HashMap<>();

    private Object activeSound = null;
    private List<Object> starSprites = new LinkedList<>();
    private IMajorConstellation highlight = null;
    private int highlightActive = 0;

    @Override
    public void update() {
        super.update();

        if(worldObj.isRemote) {
            renderEffects();
        } else {
            if(getTicksExisted() % 10 == 0) {
                if(activeFound == null) {
                    searchForConstellation();
                } else {
                    matchActiveConstellation();
                }
            }
            if((ticksExisted & 15) == 0) {
                updateSkyState();
            }

            if((ticksExisted & 31) == 0) {
                updateMultiblockState();
            }
            if(activeFound == null && getTicksExisted() % 10 == 0 && hasMultiblock) {
                searchForConstellation();
            }
        }
    }

    protected void updateSkyState() {
        boolean seesSky = true;
        BlockPos at = getPos();
        lbl:
        for (int xx = -7; xx <= 7; xx++) {
            for (int zz = -7; zz <= 7; zz++) {
                BlockPos other = at.add(xx, 0, zz);
                if(MiscUtils.isChunkLoaded(worldObj, new ChunkPos(other))) {
                    boolean see = worldObj.canSeeSky(other);
                    unloadCache.put(other, see);
                    if(!see) {
                        seesSky = false;
                        break lbl;
                    }
                } else if(unloadCache.containsKey(other)) {
                    if(!unloadCache.get(other)) {
                        seesSky = false;
                        break lbl;
                    }
                } else {
                    boolean see = worldObj.canSeeSky(other);
                    unloadCache.put(other, see);
                    if(!see) {
                        seesSky = false;
                        break lbl;
                    }
                }
            }
        }
        boolean update = doesSeeSky != seesSky;
        this.doesSeeSky = seesSky;
        if(update) {
            markForUpdate();
        }
    }

    private void updateMultiblockState() {
        boolean found = MultiBlockArrays.patternAttunementFrame.matches(worldObj, getPos());
        boolean update = hasMultiblock != found;
        this.hasMultiblock = found;
        if(update) {
            markForUpdate();
        }
    }

    private void matchActiveConstellation() {
        List<BlockPos> positions = translateConstellationPositions(activeFound);
        boolean valid = true;
        for (BlockPos pos : positions) {
            IBlockState state = worldObj.getBlockState(pos);
            if(!state.getBlock().equals(BlocksAS.attunementRelay)) {
                valid = false;
            }
        }
        if(!valid) {
            activeFound = null;
            markForUpdate();
        }
    }

    private void searchForConstellation() {
        IMajorConstellation match = null;
        for (IMajorConstellation attuneable : ConstellationRegistry.getMajorConstellations()) {
            List<BlockPos> positions = translateConstellationPositions(attuneable);
            boolean valid = true;
            for (BlockPos pos : positions) {
                IBlockState state = worldObj.getBlockState(pos);
                if(!state.getBlock().equals(BlocksAS.attunementRelay)) {
                    valid = false;
                }
            }
            if(valid) {
                match = attuneable;
                break;
            }
        }
        if(match != null) {
            activeFound = match;
            markForUpdate();
        }
    }

    @SideOnly(Side.CLIENT)
    private void renderEffects() {
        if(highlightActive > 0) {
            highlightActive--;
        }

        if(!hasMultiblock || !doesSeeSky) {
            starSprites.clear();
            activeSound = null;

        } else if(activeFound == null) {
            starSprites.clear();
            activeSound = null;

            spawnAmbientParticles();
            if(highlight != null && highlightActive > 0) {
                List<BlockPos> positions = translateConstellationPositions(highlight);
                for (BlockPos pos : positions) {
                    if(rand.nextBoolean()) continue;
                    EntityFXFacingParticle p = EffectHelper.genericFlareParticle(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).gravity(0.01);
                    p.offset(rand.nextFloat() * 0.7 * (rand.nextBoolean() ? 1 : -1), rand.nextFloat() * 0.7 * (rand.nextBoolean() ? 1 : -1), rand.nextFloat() * 0.7 * (rand.nextBoolean() ? 1 : -1));
                    p.scale(0.4F + rand.nextFloat() * 0.1F);
                    p.setAlphaMultiplier(0.5F);
                }
            }
        } else {
            if(activeSound == null || ((PositionedLoopSound) activeSound).hasStoppedPlaying()) {
                activeSound = SoundHelper.playSoundLoopClient(Sounds.attunement, new Vector3(this).add(0.5, 0.5, 0.5), 0.7F, 0.8F, () -> isInvalid() || activeFound == null);
            }

            for(Object o : starSprites) {
                EntityFXFacingSprite p = (EntityFXFacingSprite) o;
                if(p.isRemoved()) {
                    EffectHandler.getInstance().registerFX(p);
                }
            }
            if(starSprites.isEmpty()) {
                addStarSprites();
            }
            if(getTicksExisted() % 50 == 0) {
                addConnectionBeams();
            }
            spawnAmbientParticles();
        }

    }

    private void spawnAmbientParticles() {
        if(rand.nextBoolean()) return;
        Vector3 at = new Vector3(this).add(0, -0.98, 0);
        at.add(rand.nextFloat() * 15 - 7, 0, rand.nextFloat() * 15 - 7);
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
        p.setAlphaMultiplier(0.7F);
        p.setColor(Color.WHITE);
        p.gravity(0.004).scale(0.3F + rand.nextFloat() * 0.1F);
    }

    @SideOnly(Side.CLIENT)
    private void addConnectionBeams() {
        List<Tuple<BlockPos, BlockPos>> connectionTuples = translateConnectionPositions(activeFound);
        Color ov = new Color(0x2100FD);
        float cR = ov.getRed() / 255F;
        float cG = ov.getGreen() / 255F;
        float cB = ov.getBlue() / 255F;
        float alpha = 0.2F;
        for (Tuple<BlockPos, BlockPos> connection : connectionTuples) {
            Vector3 from = new Vector3(connection.key)  .add(0.5, 0.5, 0.5);
            Vector3 to   = new Vector3(connection.value).add(0.5, 0.5, 0.5);
            EffectHandler.getInstance().lightbeam(from, to, 1.1).setColorOverlay(cR, cG, cB, alpha);
            EffectHandler.getInstance().lightbeam(to, from, 1.1).setColorOverlay(cR, cG, cB, alpha);
        }
    }

    @SideOnly(Side.CLIENT)
    private void addStarSprites() {
        List<BlockPos> positions = translateConstellationPositions(activeFound);
        for (BlockPos pos : positions) {
            EntityFXFacingSprite sprite = EntityFXFacingSprite.fromSpriteSheet(SpriteLibrary.spriteStar1, pos.getX() + 0.5, pos.getY() + 0.55, pos.getZ() + 0.5, 1.5F, 2);
            EffectHandler.getInstance().registerFX(sprite);
            starSprites.add(sprite);
            sprite.setRefreshFunc(() -> starSprites.contains(sprite) && !isInvalid());
        }
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.BlockAttunementAltar.name";
    }

    private void receiveStarlight(IMajorConstellation type, double amount) {}

    @Override
    public ITransmissionReceiver provideEndpoint(BlockPos at) {
        return new TransmissionReceiverAttunementAltar(at);
    }

    @SideOnly(Side.CLIENT)
    public void highlightConstellation(IMajorConstellation highlight) {
        this.highlight = highlight;
        this.highlightActive = 60;
    }

    private List<Tuple<BlockPos, BlockPos>> translateConnectionPositions(IConstellation cst) {
        List<Tuple<BlockPos, BlockPos>> offsetPositions = new LinkedList<>();
        for (StarConnection c : cst.getStarConnections()) {
            StarLocation from = c.from;
            StarLocation to = c.to;
            offsetPositions.add(new Tuple<>(new BlockPos(from.x / 2 - 7, -1, from.y / 2 - 7).add(getPos()), new BlockPos(to.x / 2 - 7, -1, to.y / 2 - 7).add(getPos())));
        }
        return offsetPositions;
    }

    private List<BlockPos> translateConstellationPositions(IConstellation cst) {
        List<BlockPos> offsetPositions = new LinkedList<>();
        for (StarLocation sl : cst.getStars()) {
            int x = sl.x / 2;
            int z = sl.y / 2;
            offsetPositions.add(new BlockPos(x - 7, -1, z - 7).add(getPos()));
        }
        return offsetPositions;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.hasMultiblock = compound.getBoolean("mbState");
        this.doesSeeSky = compound.getBoolean("skState");

        IConstellation found = IConstellation.readFromNBT(compound);
        if(found == null || !(found instanceof IMajorConstellation)) {
            activeFound = null;
        } else {
            activeFound = (IMajorConstellation) found;
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setBoolean("mbState", hasMultiblock);
        compound.setBoolean("skState", doesSeeSky);

        if (activeFound != null) {
            activeFound.writeToNBT(compound);
        }
    }

    public static class TransmissionReceiverAttunementAltar extends SimpleTransmissionReceiver {

        public TransmissionReceiverAttunementAltar(@Nonnull BlockPos thisPos) {
            super(thisPos);
        }

        @Override
        public void onStarlightReceive(World world, boolean isChunkLoaded, IMajorConstellation type, double amount) {
            if(isChunkLoaded) {
                TileAttunementAltar ta = MiscUtils.getTileAt(world, getPos(), TileAttunementAltar.class, false);
                if(ta != null) {
                    ta.receiveStarlight(type, amount);
                }
            }
        }

        @Override
        public TransmissionClassRegistry.TransmissionProvider getProvider() {
            return new AttunementAltarReceiverProvider();
        }

    }

    public static class AttunementAltarReceiverProvider implements TransmissionClassRegistry.TransmissionProvider {

        @Override
        public TransmissionReceiverAttunementAltar provideEmptyNode() {
            return new TransmissionReceiverAttunementAltar(null);
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":TransmissionReceiverAttunementAltar";
        }

    }

}
