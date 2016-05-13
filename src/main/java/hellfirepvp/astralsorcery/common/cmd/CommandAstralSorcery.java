package hellfirepvp.astralsorcery.common.cmd;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.Tier;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CommandAstralSorcery
 * Created by HellFirePvP
 * Date: 07.05.2016 / 13:39
 */
public class CommandAstralSorcery extends CommandBase {

    private List<String> cmdAliases = new ArrayList<String>();

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
        if(args.length == 0) {
            sender.addChatMessage(new TextComponentString("§cNot enough arguments."));
            sender.addChatMessage(new TextComponentString("§cType \"/astralsorcery help\" for help"));
            return;
        }
        if(args.length >= 1) {
            String identifier = args[0];
            if(identifier.equalsIgnoreCase("help")) {
                displayHelp(sender);
            } else if(identifier.equalsIgnoreCase("constellation") || identifier.equalsIgnoreCase("constellations")) {
                if(args.length == 1) {
                    listConstellations(sender);
                } else if(args.length == 2) {
                    listConstellations(server, sender, args[1]);
                } else if(args.length == 3) {
                    addConstellations(server, sender, args[1], args[2]);
                }
            }
        }
    }

    private void addConstellations(MinecraftServer server, ICommandSender sender, String otherPlayerName, String argument) {
        EntityPlayer other;
        try {
            other = getPlayer(server, sender, otherPlayerName);
        } catch (PlayerNotFoundException e) {
            sender.addChatMessage(new TextComponentString("§cSpecified player (" + otherPlayerName + ") is not online!"));
            return;
        }
        if(other == null) {
            sender.addChatMessage(new TextComponentString("§cSpecified player (" + otherPlayerName + ") is not online!"));
            return;
        }
        PlayerProgress progress = ResearchManager.getProgress(other.getUniqueID());
        if(progress == null) {
            sender.addChatMessage(new TextComponentString("§cCould not get Progress for (" + otherPlayerName + ") !"));
            return;
        }
        if(argument.equals("all")) {
            Collection<Constellation> constellations = ConstellationRegistry.getAllConstellations();
            if(!ResearchManager.discoverConstellations(constellations, other)) {
                sender.addChatMessage(new TextComponentString("§cFailed! Could not load Progress for (" + otherPlayerName + ") !"));
                return;
            }
            other.addChatMessage(new TextComponentString("§aDiscovered all Constellations!"));
            sender.addChatMessage(new TextComponentString("§aSuccess!"));
        } else if(argument.equals("reset")) {
            ResearchManager.wipeKnowledge(other);
            other.addChatMessage(new TextComponentString("§cYour research has been reset!"));
            sender.addChatMessage(new TextComponentString("§aSuccess!"));
        } else {
            Constellation c = ConstellationRegistry.getConstellationByName(argument);
            if(c == null) {
                sender.addChatMessage(new TextComponentString("§cUnknown constellation: " + argument));
                return;
            }
            if(!ResearchManager.discoverConstellation(c, other)) {
                sender.addChatMessage(new TextComponentString("§cFailed! Could not load Progress for (" + otherPlayerName + ") !"));
                return;
            }
            other.addChatMessage(new TextComponentString("§aDiscovered constellation " + c.getName() + "!"));
            sender.addChatMessage(new TextComponentString("§aSuccess!"));
        }
    }

    private void listConstellations(MinecraftServer server, ICommandSender sender, String otherPlayerName) {
        EntityPlayer other;
        try {
            other = getPlayer(server, sender, otherPlayerName);
        } catch (PlayerNotFoundException e) {
            sender.addChatMessage(new TextComponentString("§cSpecified player (" + otherPlayerName + ") is not online!"));
            return;
        }
        if(other == null) {
            sender.addChatMessage(new TextComponentString("§cSpecified player (" + otherPlayerName + ") is not online!"));
            return;
        }
        PlayerProgress progress = ResearchManager.getProgress(other.getUniqueID());
        if(progress == null) {
            sender.addChatMessage(new TextComponentString("§cCould not get Progress for (" + otherPlayerName + ") !"));
            return;
        }
        sender.addChatMessage(new TextComponentString("§c" + otherPlayerName + " has discovered the constellations:"));
        if(progress.getKnownConstellations().size() == 0) {
            sender.addChatMessage(new TextComponentString("§c NONE"));
            return;
        }
        for (String s : progress.getKnownConstellations()) {
            sender.addChatMessage(new TextComponentString("§7" + s));
        }
    }

    private void displayHelp(ICommandSender sender) {
        sender.addChatMessage(new TextComponentString("§a/astralsorcery constellation§7 - lists all constellations"));
        sender.addChatMessage(new TextComponentString("§a/astralsorcery constellation [playerName]§7 - lists all discovered constellations of the specified player if he/she is online"));
        sender.addChatMessage(new TextComponentString("§a/astralsorcery constellation [playerName] <cName;all;reset>§7 - player specified discovers the specified constellation or all or resets all"));
    }

    private void listConstellations(ICommandSender sender) {
        for(Tier tier : ConstellationRegistry.ascendingTiers()) {
            sender.addChatMessage(new TextComponentString("§cTier: " + tier.tierNumber() + " - showupChance: " + tier.getShowupChance()));
            for(Constellation c : tier.getConstellations()) {
                sender.addChatMessage(new TextComponentString("§7" + c.getName()));
            }
        }
    }
    
}
