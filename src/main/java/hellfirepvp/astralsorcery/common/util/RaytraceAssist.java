/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.util.block.BlockPredicate;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RaytraceAssist
 * Created by HellFirePvP
 * Date: 03.08.2016 / 15:44
 */
public class RaytraceAssist {

    private static final List<BlockPredicate> passable = new ArrayList<>();
    private static final double STEP_WIDTH = 0.05;
    private static final Vector3 CENTRALIZE = new Vector3(0.5, 0.5, 0.5);

    private final Vector3 start, target;
    private final BlockPos startPos, targetPos;

    private boolean collectEntities = false;
    private Set<Integer> collected = new HashSet<>();
    private AxisAlignedBB collectBox = null;
    private boolean includeEnd = false, hitBlocks = true, hitFluids = true;

    private BlockPos posHit = null;

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

    public RaytraceAssist hitBlock(boolean hitBlocks) {
        this.hitBlocks = hitBlocks;
        return this;
    }

    public RaytraceAssist hitFluidsBeforeBlocks(boolean hitFluids) {
        this.hitFluids = hitFluids;
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
                    collected.add(b.getEntityId());
                }
            }

            if(MiscUtils.isChunkLoaded(world, at)) {
                if(!isStartEnd(at) && !world.isAirBlock(at)) {
                    if (this.hitFluids && !world.getFluidState(at).isEmpty()) {
                        posHit = at;
                        return false;
                    }
                    if ((this.hitBlocks || this.hitFluids) && !isAllowed(world, at, world.getBlockState(at))) {
                        posHit = at;
                        return false;
                    }
                }
            }

            prevVec = stepVec;
        }
        return true;
    }

    public BlockPos positionHit() {
        return posHit;
    }

    public List<Entity> collectedEntities(World world) {
        List<Entity> entities = new LinkedList<>();
        for (Integer id : collected) {
            Entity e = world.getEntityByID(id);
            if(e != null && e.isAlive()) {
                entities.add(e);
            }
        }
        return entities;
    }

    private boolean isAllowed(World world, BlockPos at, IBlockState state) {
        return MiscUtils.contains(passable, predicate -> predicate.test(world, at, state));
    }

    private boolean isStartEnd(BlockPos hit) {
        return hit.equals(startPos) || (!includeEnd && hit.equals(targetPos));
    }

    public static void addPassable(BlockPredicate predicate) {
        passable.add(predicate);
    }

    static {
        addPassable(((world, pos, state) -> state.getFluidState().isEmpty() && state.getMaterial().equals(Material.GLASS)));
    }

}
