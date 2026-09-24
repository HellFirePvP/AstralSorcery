/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.linking;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: Linkable
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface Linkable {

    Component getDisplayName(Level level);

    /**
     * Get the max distance this linkable can accept targets linked to.
     * May be empty if there's no limit.
     *
     * @return the max link distance
     */
    default Optional<Integer> getMaxBlockLinkDistance() {
        return Optional.empty();
    }

    /**
     * Get the maximum amount this linkable can be linked to.
     * May be empty if there's no limit.
     *
     * @return the max linkable count
     */
    default Optional<Integer> getMaxBlockLinkCount() {
        return Optional.empty();
    }

    /**
     * Called when a player tries to start linking from this linkable.
     * By default, returns false if this linkable can not send links
     *
     * @param player the player linking
     * @return if this linkable can be selected to start linking from
     */
    default boolean startLinking(Player player) {
        return true;
    }

    /**
     * Try to link this linkable to the given position.
     *
     * @param level the level this link happens in
     * @param to the position linked to
     * @param action if the action is simulated or not
     * @return the result of the linking attempt, success or containing the reason for failure
     */
    LinkResult tryLinkTo(Level level, BlockPos to, LinkAction action);

    /**
     * Try to unlink this linkable from the given position.
     *
     * @param level the level this unlink happens in
     * @param to the position to remove the link from
     * @param action if the action is simulated or not
     * @return the result of the linking attempt, success or containing the reason for failure
     */
    LinkResult tryUnlinkFrom(Level level, BlockPos to, LinkAction action);

    /**
     * Try to link this linkable to the given entity.
     *
     * @param level the level this link happens in
     * @param entity the entity linked to
     * @param action if the action is simulated or not
     * @return the result of the linking attempt, success or containing the reason for failure
     */
    LinkResult tryLinkToEntity(Level level, Entity entity, LinkAction action);

    /**
     * Try to unlink this linkable from the given entity.
     *
     * @param level the level this unlink happens in
     * @param entity the entity to remove the link from
     * @param action if the action is simulated or not
     * @return the result of the linking attempt, success or containing the reason for failure
     */
    LinkResult tryUnlinkFromEntity(Level level, Entity entity, LinkAction action);

    /**
     * Notify this linkable of a link state change.
     * This is only called on successful link/unlink operations.
     *
     * @param sLevel the server level
     * @param to the position linked/unlinked to/from
     * @param isLink true if linked, false if unlinked
     */
    default void updateLinkStateChange(ServerLevel sLevel, BlockPos to, boolean isLink) {}

    /**
     * Notify this linkable of a link state change.
     * This is only called on successful link/unlink operations.
     *
     * @param sLevel the server level
     * @param entity the entity linked/unlinked to/from
     * @param isLink true if linked, false if unlinked
     */
    default void updateLinkStateChange(ServerLevel sLevel, Entity entity, boolean isLink) {}

    /**
     * Get the current positions this linkable is linked to.
     * This explicitly relates to block connections.
     *
     * @return the linked positions
     */
    Collection<BlockLinkConnection> getLinkedPositions();

    /**
     * Get the current entity ids this linkable is linked to.
     *
     * @return the linked entities
     */
    Collection<EntityLinkConnection> getLinkedEntities();

    /**
     * Check if this linkable is linked to the given position.
     *
     * @param pos the position to check
     * @return true if linked, false otherwise
     */
    default boolean isLinkedTo(BlockPos pos) {
        for (BlockLinkConnection link : getLinkedPositions()) {
            if (link.getTo().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if this linkable is linked to the given entity.
     *
     * @param entity the entity to check
     * @return true if linked, false otherwise
     */
    default boolean isLinkedTo(Entity entity) {
        for (EntityLinkConnection link : getLinkedEntities()) {
            if (link.entityUid() == entity.getUUID()) {
                return true;
            }
        }
        return false;
    }

    public static enum LinkAction {

        SIMULATE,
        EXECUTE;

        public boolean simulate() {
            return this == SIMULATE;
        }

        public boolean execute() {
            return this == EXECUTE;
        }
    }

}
