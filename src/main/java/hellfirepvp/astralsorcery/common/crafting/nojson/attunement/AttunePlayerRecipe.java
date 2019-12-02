package hellfirepvp.astralsorcery.common.crafting.nojson.attunement;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.crafting.nojson.attunement.active.ActivePlayerAttunementRecipe;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.entity.EntityUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttunePlayerRecipe
 * Created by HellFirePvP
 * Date: 02.12.2019 / 19:37
 */
public class AttunePlayerRecipe extends AttunementRecipe<ActivePlayerAttunementRecipe> {

    private static final AxisAlignedBB BOX = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    public AttunePlayerRecipe() {
        super(AstralSorcery.key("attune_player"));
    }

    @Override
    public boolean canStartCrafting(TileAttunementAltar altar) {
        World world = altar.getWorld();
        if (altar.getActiveConstellation() instanceof IMajorConstellation && DayTimeHelper.isNight(world)) {
            AxisAlignedBB boxAt = BOX.offset(altar.getPos().up()).grow(1);

            Vector3 thisVec = new Vector3(altar).add(0.5, 1.5, 0.5);
            List<ServerPlayerEntity> players = world.getEntitiesWithinAABB(ServerPlayerEntity.class, boxAt);
            if (!players.isEmpty()) {
                ServerPlayerEntity pl = EntityUtils.selectClosest(players, (player) -> thisVec.distanceSquared(player.getPositionVector()));
                if (pl != null && !MiscUtils.isPlayerFakeMP(pl) && !pl.isSneaking()) {
                    PlayerProgress prog = ResearchHelper.getProgress(pl, LogicalSide.SERVER);

                    return prog.isValid() &&
                            prog.getAttunedConstellation() == null &&
                            prog.getTierReached().isThisLaterOrEqual(ProgressionTier.ATTUNEMENT) &&
                            prog.hasConstellationDiscovered(altar.getActiveConstellation());
                }
            }
        }
        return false;
    }

    @Override
    public ActivePlayerAttunementRecipe createRecipe(TileAttunementAltar altar) {
        return new ActivePlayerAttunementRecipe(this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ActivePlayerAttunementRecipe deserialize(TileAttunementAltar altar, CompoundNBT nbt, @Nullable ActivePlayerAttunementRecipe previousInstance) {
        ActivePlayerAttunementRecipe recipe =  super.deserialize(altar, nbt, previousInstance);
        if (previousInstance != null) {
            recipe.cameraHack = previousInstance.cameraHack;
        }
        return recipe;
    }
}
