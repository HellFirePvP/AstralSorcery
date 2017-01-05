/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.cmd;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerkLevelManager;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerks;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.registry.RegistryStructures;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.struct.BlockArray;
import hellfirepvp.astralsorcery.common.util.struct.StructureBlockArray;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CommandAstralSorcery
 * Created by HellFirePvP
 * Date: 07.05.2016 / 13:39
 */
public class CommandAstralSorcery extends CommandBase {

    private List<String> cmdAliases = new ArrayList<>();

    public CommandAstralSorcery() {
        this.cmdAliases.add("astralsorcery");
        this.cmdAliases.add("as");
    }

    @Override
    public String getCommandName() {
        return "astralsorcery";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/astralsorcery <action> [player] [arguments...]";
    }

    @Override
    public List<String> getCommandAliases() {
        return cmdAliases;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.addChatMessage(new TextComponentString("§cNot enough arguments."));
            sender.addChatMessage(new TextComponentString("§cType \"/astralsorcery help\" for help"));
            return;
        }
        if (args.length >= 1) {
            String identifier = args[0];
            if (identifier.equalsIgnoreCase("help")) {
                displayHelp(sender);
            } else if (identifier.equalsIgnoreCase("constellation") || identifier.equalsIgnoreCase("constellations")) {
                if (args.length == 1) {
                    listConstellations(sender);
                } else if (args.length == 2) {
                    listConstellations(server, sender, args[1]);
                } else if (args.length == 3) {
                    addConstellations(server, sender, args[1], args[2]);
                }
            } else if(identifier.equalsIgnoreCase("research") || identifier.equalsIgnoreCase("res")) {
                if(args.length == 3) {
                    modifyResearch(server, sender, args[1], args[2]);
                }
            } else if (identifier.equalsIgnoreCase("progress") || identifier.equalsIgnoreCase("prog")) {
                if(args.length <= 2) {
                    showProgress(server, sender, args.length == 1 ? sender.getName() : args[1]);
                } else if(args.length == 3) {
                    modifyProgress(server, sender, args[1], args[2]);
                }
            } else if (identifier.equalsIgnoreCase("reset")) {
                if (args.length == 2) {
                    wipeProgression(server, sender, args[1]);
                }
            } else if (identifier.equalsIgnoreCase("charge")) {
                if (args.length == 3) {
                    setCharge(server, sender, args[1], args[2]);
                }
            } else if (identifier.equalsIgnoreCase("attune")) {
                if(args.length == 3) {
                    attuneToConstellation(server, sender, args[1], args[2]);
                }
            } else if (identifier.equalsIgnoreCase("build")) {
                if(args.length == 2) {
                    buildStruct(server, sender, args[1]);
                } else {
                    RegistryStructures.init(); //Reload
                }
            } else if(identifier.equalsIgnoreCase("maximize")) {
                if (args.length == 2) {
                    maxAll(server, sender, args[1]);
                }
            }
        }
    }

    private void attuneToConstellation(MinecraftServer server, ICommandSender sender, String otherPlayerName, String majorConstellationStr) {
        Tuple<EntityPlayer, PlayerProgress> prTuple = tryGetProgressWithMessages(server, sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        PlayerProgress progress = prTuple.value;
        EntityPlayer other = prTuple.key;

        IMajorConstellation cst = ConstellationRegistry.getMajorConstellationByName(majorConstellationStr);
        if(cst == null) {
            sender.addChatMessage(new TextComponentString("§cFailed! Given constellation name is not a (major) constellation! " + majorConstellationStr));
            sender.addChatMessage(new TextComponentString("§cSee '/astralsorcery constellations' to get all constellations!"));
            return;
        }

        if(ResearchManager.setAttunedConstellation(other, cst)) {
            sender.addChatMessage(new TextComponentString("§aSuccess! Player has been attuned to " + cst.getUnlocalizedName()));
        } else {
            sender.addChatMessage(new TextComponentString("§cFailed! Player specified doesn't seem to have a research progress!"));
        }
    }

    private void setCharge(MinecraftServer server, ICommandSender sender, String otherPlayerName, String strCharge) {
        Tuple<EntityPlayer, PlayerProgress> prTuple = tryGetProgressWithMessages(server, sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        PlayerProgress progress = prTuple.value;
        EntityPlayer other = prTuple.key;

        int chargeToSet;
        try {
            chargeToSet = Integer.parseInt(strCharge);
        } catch (NumberFormatException exc) {
            sender.addChatMessage(new TextComponentString("§cFailed! Alignment charge to set should be a number! " + strCharge));
            return;
        }

        if(ResearchManager.forceCharge(other, chargeToSet)) {
            sender.addChatMessage(new TextComponentString("§aSuccess! Player charge has been set to " + chargeToSet));
        } else {
            sender.addChatMessage(new TextComponentString("§cFailed! Player specified doesn't seem to have a research progress!"));
        }
    }

    private void modifyResearch(MinecraftServer server, ICommandSender sender, String otherPlayerName, String research) {
        Tuple<EntityPlayer, PlayerProgress> prTuple = tryGetProgressWithMessages(server, sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        PlayerProgress progress = prTuple.value;
        EntityPlayer other = prTuple.key;

        if(research.equalsIgnoreCase("all")) {
            ResearchManager.forceMaximizeResearch(other);
            sender.addChatMessage(new TextComponentString("§aSuccess!"));
        } else {
            ResearchProgression pr = ResearchProgression.getByEnumName(research);
            if(pr == null) {
                sender.addChatMessage(new TextComponentString("§cFailed! Unknown research: " + research));
            } else {
                /*ProgressionTier pt = pr.getRequiredProgress();
                ResearchManager.giveProgressionIgnoreFail(other, pt);*/
                ResearchManager.unsafeForceGiveResearch(other, pr);
                sender.addChatMessage(new TextComponentString("§aSuccess!"));
            }
        }
    }

    private void maxAll(MinecraftServer server, ICommandSender sender, String otherPlayerName) {
        Tuple<EntityPlayer, PlayerProgress> prTuple = tryGetProgressWithMessages(server, sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        PlayerProgress progress = prTuple.value;
        EntityPlayer other = prTuple.key;

        Collection<IConstellation> constellations = ConstellationRegistry.getAllConstellations();
        if (!ResearchManager.discoverConstellations(constellations, other)) {
            sender.addChatMessage(new TextComponentString("§cFailed! Could not load Progress for (" + otherPlayerName + ") !"));
            return;
        }
        sender.addChatMessage(new TextComponentString("§aDiscovered all Constellations!"));
        if(!ResearchManager.maximizeTier(other)) {
            sender.addChatMessage(new TextComponentString("§cFailed! Could not load Progress for (" + otherPlayerName + ") !"));
        } else {
            sender.addChatMessage(new TextComponentString("§aMaximized ProgressionTier for " + otherPlayerName + " !"));
        }
        if(!ResearchManager.forceMaximizeResearch(other)) {
            sender.addChatMessage(new TextComponentString("§cFailed! Could not load Progress for (" + otherPlayerName + ") !"));
        } else {
            sender.addChatMessage(new TextComponentString("§aMaximized ResearchProgression for " + otherPlayerName + " !"));
        }
        sender.addChatMessage(new TextComponentString("§aSuccess!"));
    }

    private void buildStruct(MinecraftServer server, ICommandSender sender, String name) {
        BlockArray array;
        try {
            Field f = MultiBlockArrays.class.getDeclaredField(name);
            f.setAccessible(true);
            if(f.isAnnotationPresent(MultiBlockArrays.PasteBlacklist.class)) {
                sender.addChatMessage(new TextComponentString("§cFailed! You may not paste " + name + ", as it may be unstable or may have other unwanted effects!"));
                return;
            }
            array = (BlockArray) f.get(null);
        } catch (NoSuchFieldException e) {
            sender.addChatMessage(new TextComponentString("§cFailed! " + name + " doesn't exist!"));
            return;
        } catch (IllegalAccessException e) {
            return; //doesn't happen
        }
        EntityPlayer exec;
        try {
            exec = getCommandSenderAsPlayer(sender);
        } catch (PlayerNotFoundException e) {
            sender.addChatMessage(new TextComponentString("§cFailed! Couldn't find you as player in the world!"));
            return;
        }
        RayTraceResult res = MiscUtils.rayTraceLook(exec, 60);
        if(res == null) {
            sender.addChatMessage(new TextComponentString("§cFailed! Couldn't find the block you're looking at?"));
            return;
        }
        BlockPos hit;
        switch (res.typeOfHit) {
            case BLOCK:
                hit = res.getBlockPos();
                break;
            case MISS:
            case ENTITY:
            default:
                sender.addChatMessage(new TextComponentString("§cFailed! Couldn't find the block you're looking at?"));
                return;
        }
        sender.addChatMessage(new TextComponentString("§aStarting to build " + name + " at " + hit.toString() + "!"));
        array.placeInWorld(exec.world, hit);
        sender.addChatMessage(new TextComponentString("§aBuilt " + name + "!"));
    }

    private void wipeProgression(MinecraftServer server, ICommandSender sender, String otherPlayerName) {
        Tuple<EntityPlayer, PlayerProgress> prTuple = tryGetProgressWithMessages(server, sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        PlayerProgress progress = prTuple.value;
        EntityPlayer other = prTuple.key;

        ResearchManager.wipeKnowledge(other);
        sender.addChatMessage(new TextComponentString("§aWiped " + otherPlayerName + "'s data!"));
    }

    private void modifyProgress(MinecraftServer server, ICommandSender sender, String otherPlayerName, String argument) {
        Tuple<EntityPlayer, PlayerProgress> prTuple = tryGetProgressWithMessages(server, sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        PlayerProgress progress = prTuple.value;
        EntityPlayer other = prTuple.key;
        if(argument.equalsIgnoreCase("all")) {
            if(!ResearchManager.maximizeTier(other)) {
                sender.addChatMessage(new TextComponentString("§cFailed! Could not load Progress for (" + otherPlayerName + ") !"));
            } else {
                sender.addChatMessage(new TextComponentString("§aMaximized ProgressionTier for " + otherPlayerName + " !"));
            }
        }/* else {
            Optional<ProgressionTier> did = ResearchManager.stepTier(other);
            if(!did.isPresent()) {
                sender.addChatMessage(new TextComponentString("§cCould not step Progress for " + otherPlayerName + " ! (Is already at max)"));
            } else {
                if(did.get() != null) {
                    sender.addChatMessage(new TextComponentString("§aPlayer " + otherPlayerName + " advanced to Tier " + did.get().name() + "!"));
                } else {
                    sender.addChatMessage(new TextComponentString("§cFailed! Could not load Progress for (" + otherPlayerName + ") !"));
                }
            }
        }*/
    }

    private void showProgress(MinecraftServer server, ICommandSender sender, String otherPlayerName) {
        Tuple<EntityPlayer, PlayerProgress> prTuple = tryGetProgressWithMessages(server, sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        PlayerProgress progress = prTuple.value;
        EntityPlayer other = prTuple.key;

        sender.addChatMessage(new TextComponentString("§aPlayer " + otherPlayerName + "'s research Data:"));

        sender.addChatMessage(new TextComponentString("§aProgression tier: " + progress.getTierReached().name()));
        sender.addChatMessage(new TextComponentString("§aAttuned to: " + (progress.getAttunedConstellation() == null ? "<none>" : progress.getAttunedConstellation().getUnlocalizedName())));
        sender.addChatMessage(new TextComponentString("§aAlignment charge: " + progress.getAlignmentCharge() + " - As level: " + ConstellationPerkLevelManager.getAlignmentLevel(progress)));
        sender.addChatMessage(new TextComponentString("§aUnlocked perks + unlock-level:"));
        for (Map.Entry<ConstellationPerk, Integer> entry : progress.getAppliedPerks().entrySet()) {
            sender.addChatMessage(new TextComponentString("§7" + entry.getKey().getUnlocalizedName() + " / " + entry.getValue()));
        }
        sender.addChatMessage(new TextComponentString("§aUnlocked research groups:"));
        StringBuilder sb = new StringBuilder();
        for (ResearchProgression rp : progress.getResearchProgression()) {
            if(sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(rp.name());
        }
        sender.addChatMessage(new TextComponentString("§7" + sb.toString()));
        sender.addChatMessage(new TextComponentString("§aUnlocked constellations:"));
        sb = new StringBuilder();
        for (String str : progress.getKnownConstellations()) {
            if(sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(str);
        }
        sender.addChatMessage(new TextComponentString("§7" + sb.toString()));
    }

    private void addConstellations(MinecraftServer server, ICommandSender sender, String otherPlayerName, String argument) {
        Tuple<EntityPlayer, PlayerProgress> prTuple = tryGetProgressWithMessages(server, sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        PlayerProgress progress = prTuple.value;
        EntityPlayer other = prTuple.key;
        if (argument.equals("all")) {
            Collection<IConstellation> constellations = ConstellationRegistry.getAllConstellations();
            if (!ResearchManager.discoverConstellations(constellations, other)) {
                sender.addChatMessage(new TextComponentString("§cFailed! Could not load Progress for (" + otherPlayerName + ") !"));
                return;
            }
            other.addChatMessage(new TextComponentString("§aDiscovered all Constellations!"));
            sender.addChatMessage(new TextComponentString("§aSuccess!"));
        } else {
            IConstellation c = ConstellationRegistry.getConstellationByName(argument);
            if (c == null) {
                sender.addChatMessage(new TextComponentString("§cUnknown constellation: " + argument));
                return;
            }
            if (!ResearchManager.discoverConstellation(c, other)) {
                sender.addChatMessage(new TextComponentString("§cFailed! Could not load Progress for (" + otherPlayerName + ") !"));
                return;
            }
            other.addChatMessage(new TextComponentString("§aDiscovered constellation " + c.getUnlocalizedName() + "!"));
            sender.addChatMessage(new TextComponentString("§aSuccess!"));
        }
    }

    private void listConstellations(MinecraftServer server, ICommandSender sender, String otherPlayerName) {
        Tuple<EntityPlayer, PlayerProgress> prTuple = tryGetProgressWithMessages(server, sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        PlayerProgress progress = prTuple.value;
        EntityPlayer other = prTuple.key;
        sender.addChatMessage(new TextComponentString("§c" + otherPlayerName + " has discovered the constellations:"));
        if (progress.getKnownConstellations().size() == 0) {
            sender.addChatMessage(new TextComponentString("§c NONE"));
            return;
        }
        for (String s : progress.getKnownConstellations()) {
            sender.addChatMessage(new TextComponentString("§7" + s));
        }
    }

    private Tuple<EntityPlayer, PlayerProgress> tryGetProgressWithMessages(MinecraftServer server, ICommandSender sender, String otherPlayerName) {
        EntityPlayer other;
        try {
            other = getPlayer(server, sender, otherPlayerName);
        } catch (PlayerNotFoundException e) {
            sender.addChatMessage(new TextComponentString("§cSpecified player (" + otherPlayerName + ") is not online!"));
            return null;
        }
        if (other == null) {
            sender.addChatMessage(new TextComponentString("§cSpecified player (" + otherPlayerName + ") is not online!"));
            return null;
        }
        PlayerProgress progress = ResearchManager.getProgress(other.getUniqueID());
        if (progress == null) {
            sender.addChatMessage(new TextComponentString("§cCould not get Progress for (" + otherPlayerName + ") !"));
            return null;
        }
        return new Tuple<>(other, progress);
    }

    private void displayHelp(ICommandSender sender) {
        sender.addChatMessage(new TextComponentString("§a/astralsorcery constellation§7 - lists all constellations"));
        sender.addChatMessage(new TextComponentString("§a/astralsorcery constellation [playerName]§7 - lists all discovered constellations of the specified player if he/she is online"));
        sender.addChatMessage(new TextComponentString("§a/astralsorcery constellation [playerName] <cName;all>§7 - player specified discovers the specified constellation or all or resets all"));
        sender.addChatMessage(new TextComponentString("§a/astralsorcery progress [playerName]§7 - displays progress information about the player (Enter no player to view your own)"));
        sender.addChatMessage(new TextComponentString("§a/astralsorcery progress [playerName] <all>§7 - maximize progression"));
        sender.addChatMessage(new TextComponentString("§a/astralsorcery research [playerName] <research;all>§7 - set/add Research"));
        sender.addChatMessage(new TextComponentString("§a/astralsorcery reset [playerName]§7 - resets all progression-related data for that player."));
        sender.addChatMessage(new TextComponentString("§a/astralsorcery build [structure]§7 - builds the named structure wherever the player is looking at."));
        sender.addChatMessage(new TextComponentString("§a/astralsorcery maximize [playerName]§7 - unlocks everything for that player."));
        sender.addChatMessage(new TextComponentString("§a/astralsorcery charge [playerName] <charge>§7 - sets the alignment charge for a player"));
        sender.addChatMessage(new TextComponentString("§a/astralsorcery attune [playerName] <majorConstellationName>§7 - sets the attunement constellation for a player"));
    }

    private void listConstellations(ICommandSender sender) {
        sender.addChatMessage(new TextComponentString("§cMajor Constellations:"));
        for (IMajorConstellation c : ConstellationRegistry.getMajorConstellations()) {
            sender.addChatMessage(new TextComponentString("§7" + c.getUnlocalizedName()));
        }
        sender.addChatMessage(new TextComponentString("§Weak Constellations:"));
        for (IWeakConstellation c : ConstellationRegistry.getWeakConstellations()) {
            if(c instanceof IMajorConstellation) continue;
            sender.addChatMessage(new TextComponentString("§7" + c.getUnlocalizedName()));
        }
        sender.addChatMessage(new TextComponentString("§cMinor Constellations:"));
        for (IMinorConstellation c : ConstellationRegistry.getMinorConstellations()) {
            sender.addChatMessage(new TextComponentString("§7" + c.getUnlocalizedName()));
        }
    }

}
