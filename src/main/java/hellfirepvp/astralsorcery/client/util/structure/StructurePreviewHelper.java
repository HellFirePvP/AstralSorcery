/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.structure;

import hellfirepvp.observerlib.api.structure.MatchableStructure;
import hellfirepvp.observerlib.api.util.StructureBlockArray;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StructurePreviewHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StructurePreviewHelper {

    private static StructurePreview currentPreview;

    public static void setCurrentPreview(StructurePreview preview) {
        if (currentPreview != null) {
            currentPreview.removed();
        }
        currentPreview = preview;
    }

    public static StructurePreview.Builder newPreview(Level level, BlockPos center, MatchableStructure structure) {
        return new StructurePreview.Builder(level, center, structure);
    }

    public static void renderPreview(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
        if (currentPreview == null) return;
        Player player = Minecraft.getInstance().player;
        if (player != null && currentPreview.canRender(player.level(), player.blockPosition())) {
            currentPreview.render(event.getCamera(), event.getPartialTick().getGameTimeDeltaPartialTick(false));
        }
    }

    public static void tickPreview(ClientTickEvent.Post event) {
        if (currentPreview == null) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            currentPreview.removed();
            currentPreview = null;
            return;
        }
        if (currentPreview.canPersist(player.level(), player.blockPosition())) {
            currentPreview.tick(player.level(), player.blockPosition());
        } else {
            currentPreview.removed();
            currentPreview = null;
        }
    }
}
