package hellfirepvp.astralsorcery.common.crafting.nojson.attunement;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
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

import javax.annotation.Nonnull;
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
        if (DayTimeHelper.isNight(world)) {
            return findEligiblePlayer(altar) != null;
        }
        return false;
    }

    @Override
    @Nonnull
    public ActivePlayerAttunementRecipe createRecipe(TileAttunementAltar altar) {
        ServerPlayerEntity player = findEligiblePlayer(altar);
        return new ActivePlayerAttunementRecipe(this, (IMajorConstellation) altar.getActiveConstellation(), player.getUniqueID());
    }

    @Override
    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public ActivePlayerAttunementRecipe deserialize(TileAttunementAltar altar, CompoundNBT nbt, @Nullable ActivePlayerAttunementRecipe previousInstance) {
        ActivePlayerAttunementRecipe recipe = new ActivePlayerAttunementRecipe(this, nbt);
        if (previousInstance != null) {
            recipe.cameraHack = previousInstance.cameraHack;
        }
        return recipe;
    }

    @Nullable
    private static ServerPlayerEntity findEligiblePlayer(TileAttunementAltar altar) {
        if (!(altar.getActiveConstellation() instanceof IMajorConstellation)) {
            return null;
        }
        AxisAlignedBB boxAt = BOX.offset(altar.getPos().up()).grow(1);

        Vector3 thisVec = new Vector3(altar).add(0.5, 1.5, 0.5);
        List<ServerPlayerEntity> players = altar.getWorld().getEntitiesWithinAABB(ServerPlayerEntity.class, boxAt);
        if (!players.isEmpty()) {
            ServerPlayerEntity pl = EntityUtils.selectClosest(players, (player) -> thisVec.distanceSquared(player.getPositionVector()));
            if (isEligablePlayer(pl, altar.getActiveConstellation())) {
                return pl;
            }
        }
        return null;
    }

    public static boolean isEligablePlayer(ServerPlayerEntity player, IConstellation attuneTo) {
        if (player != null && player.isAlive() && !MiscUtils.isPlayerFakeMP(player) && !player.isSneaking()) {
            PlayerProgress prog = ResearchHelper.getProgress(player, LogicalSide.SERVER);

            return prog.isValid() &&
                    attuneTo instanceof IMajorConstellation &&
                    prog.getAttunedConstellation() == null &&
                    prog.getTierReached().isThisLaterOrEqual(ProgressionTier.ATTUNEMENT) &&
                    prog.hasConstellationDiscovered(attuneTo);
        }
        return false;
    }
}
