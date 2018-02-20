/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RaytraceAssist
 * Created by HellFirePvP
 * Date: 03.08.2016 / 15:44
 */
public class RaytraceAssist {

    //-1 is wildcard
    private static final Map<Block, List<Integer>> passable = new HashMap<>();

    private static final double STEP_WIDTH = 0.05;
    private static final Vector3 CENTRALIZE = new Vector3(0.5, 0.5, 0.5);

    private final Vector3 start, target;
    private final BlockPos startPos, targetPos;

    private boolean collectEntities = false;
    private List<Integer> collected = new LinkedList<>();
    private AxisAlignedBB collectBox = null;
    private boolean includeEnd = false;

    private BlockPos hit = null;

    public RaytraceAssist(BlockPos start, BlockPos target) {
        this(new Vector3(start).add(CENTRALIZE), new Vector3(target).add(CENTRALIZE));
    }

    public RaytraceAssist(Vector3 start, Vector3 target) {
        this.start = start.clone();
        this.target = target.clone();
        this.startPos = this.start.toBlockPos();
        this.targetPos = this.target.toBlockPos();
    }

    public RaytraceAssist includeEndPoint() {
        this.includeEnd = true;
        return this;
    }

    public void setCollectEntities(double additionalCollectRadius) {
        this.collectEntities = true;
        this.collectBox = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
        this.collectBox = this.collectBox.grow(additionalCollectRadius);
    }

    public boolean isClear(World world) {
        Vector3 aim = start.vectorFromHereTo(target);
        Vector3 stepAim = aim.clone().normalize().multiply(STEP_WIDTH);
        double distance = aim.length();
        Vector3 prevVec = start.clone();
        for (double distancePart = STEP_WIDTH; distancePart <= distance; distancePart += STEP_WIDTH) {
            Vector3 stepVec = prevVec.clone().add(stepAim);
            BlockPos at = stepVec.toBlockPos();

            if(collectEntities) {
                List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, collectBox.offset(stepVec.getX(), stepVec.getY(), stepVec.getZ()));
                for (Entity b : entities) {
                    if(!collected.contains(b.getEntityId())) {
                        collected.add(b.getEntityId());
                    }
                }
            }

            if(MiscUtils.isChunkLoaded(world, new ChunkPos(at))) {
                if(!isStartEnd(at) && !world.isAirBlock(at)) {
                    IBlockState state = world.getBlockState(at);
                    if(!isAllowed(state)) {
                        hit = at;
                        return false;
                    }
                }
            }

            /* Tried often enough. doesn't work properly for whatever reason.
            RayTraceResult rtr = world.rayTraceBlocks(prevVec.toVec3d(), stepVec.toVec3d());

            if(rtr != null && rtr.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos hit = rtr.getBlockPos();
                if(!isStartEnd(hit)) {
                    IBlockState state = world.getBlockState(hit);
                    if(!isAllowed(state)) {
                        return false;
                    }
                }
            }*/

            prevVec = stepVec;
        }
        return true;
    }

    public BlockPos blockHit() {
        return hit;
    }

    public List<Entity> collectedEntities(World world) {
        List<Entity> entities = new LinkedList<>();
        for (Integer id : collected) {
            Entity e = world.getEntityByID(id);
            if(e != null && !e.isDead) {
                entities.add(e);
            }
        }
        return entities;
    }

    private boolean isAllowed(IBlockState state) {
        Block b = state.getBlock();
        List<Integer> accepted = passable.get(b);
        if(accepted != null) {
            if(accepted.size() == 1 && accepted.get(0) == -1) return true;
            if(accepted.contains(b.getMetaFromState(state))) return true;
        }
        return false;
    }

    private boolean isStartEnd(BlockPos hit) {
        return hit.equals(startPos) || (!includeEnd && hit.equals(targetPos));
    }

    public static void addPassable(Block b, Integer... stateMetas) {
        List<Integer> passStates = new ArrayList<>();
        if(stateMetas == null || stateMetas.length == 0) {
            passStates.add(-1);
        } else {
            Collections.addAll(passStates, stateMetas);
        }
        passable.put(b, passStates);
    }

    static {
        addPassable(Blocks.GLASS);
        addPassable(Blocks.GLASS_PANE);
        addPassable(Blocks.STAINED_GLASS);
        addPassable(Blocks.STAINED_GLASS_PANE);
    }

    @SideOnly(Side.CLIENT)
    public static void playDebug(PktParticleEvent event) {
        Vector3 pos = event.getVec();
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(pos.getX(), pos.getY(), pos.getZ());
        p.gravity(0.004).scale(0.05F);
    }

}
