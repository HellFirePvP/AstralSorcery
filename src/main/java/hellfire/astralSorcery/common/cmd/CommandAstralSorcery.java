package hellfire.astralSorcery.common.cmd;

import hellfire.astralSorcery.api.constellation.IConstellation;
import hellfire.astralSorcery.api.constellation.IConstellationTier;
import hellfire.astralSorcery.common.constellation.Constellation;
import hellfire.astralSorcery.common.constellation.ConstellationRegistry;
import hellfire.astralSorcery.common.data.research.PlayerProgress;
import hellfire.astralSorcery.common.data.research.ResearchManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 09.02.2016 18:41
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
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) {
            sender.addChatMessage(new ChatComponentText("§cNot enough arguments."));
            sender.addChatMessage(new ChatComponentText("§cType \"/astralsorcery help\" for help"));
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
                    listConstellations(sender, args[1]);
                } else if(args.length == 3) {
                    addConstellations(sender, args[1], args[2]);
                }
            }
        }
    }

    private void addConstellations(ICommandSender sender, String otherPlayerName, String argument) {
        EntityPlayer other;
        try {
            other = getPlayer(sender, otherPlayerName);
        } catch (PlayerNotFoundException e) {
            sender.addChatMessage(new ChatComponentText("§cSpecified player (" + otherPlayerName + ") is not online!"));
            return;
        }
        if(other == null) {
            sender.addChatMessage(new ChatComponentText("§cSpecified player (" + otherPlayerName + ") is not online!"));
            return;
        }
        PlayerProgress progress = ResearchManager.getProgress(other.getUniqueID());
        if(progress == null) {
            sender.addChatMessage(new ChatComponentText("§cCould not get Progress for (" + otherPlayerName + ") !"));
            return;
        }
        if(argument.equals("all")) {
            Collection<IConstellation> constellations = ConstellationRegistry.getAllConstellations();
            if(!ResearchManager.discoverConstellations(constellations, other)) {
                sender.addChatMessage(new ChatComponentText("§cFailed! Could not load Progress for (" + otherPlayerName + ") !"));
                return;
            }
            other.addChatMessage(new ChatComponentText("§aDiscovered all Constellations!"));
            sender.addChatMessage(new ChatComponentText("§aSuccess!"));
        } else if(argument.equals("reset")) {
            ResearchManager.wipeKnowledge(other);
            other.addChatMessage(new ChatComponentText("§cYour research has been reset!"));
            sender.addChatMessage(new ChatComponentText("§aSuccess!"));
        } else {
            IConstellation c = ConstellationRegistry.getConstellationByName(argument);
            if(c == null) {
                sender.addChatMessage(new ChatComponentText("§cUnknown constellation: " + argument));
                return;
            }
            if(!ResearchManager.discoverConstellation(c, other)) {
                sender.addChatMessage(new ChatComponentText("§cFailed! Could not load Progress for (" + otherPlayerName + ") !"));
                return;
            }
            other.addChatMessage(new ChatComponentText("§aDiscovered constellation " + c.getName() + "!"));
            sender.addChatMessage(new ChatComponentText("§aSuccess!"));
        }
    }

    private void listConstellations(ICommandSender sender, String otherPlayerName) {
        EntityPlayer other;
        try {
            other = getPlayer(sender, otherPlayerName);
        } catch (PlayerNotFoundException e) {
            sender.addChatMessage(new ChatComponentText("§cSpecified player (" + otherPlayerName + ") is not online!"));
            return;
        }
        if(other == null) {
            sender.addChatMessage(new ChatComponentText("§cSpecified player (" + otherPlayerName + ") is not online!"));
            return;
        }
        PlayerProgress progress = ResearchManager.getProgress(other.getUniqueID());
        if(progress == null) {
            sender.addChatMessage(new ChatComponentText("§cCould not get Progress for (" + otherPlayerName + ") !"));
            return;
        }
        sender.addChatMessage(new ChatComponentText("§c" + otherPlayerName + " has discovered the constellations:"));
        if(progress.getKnownConstellations().size() == 0) {
            sender.addChatMessage(new ChatComponentText("§c NONE"));
            return;
        }
        for (String s : progress.getKnownConstellations()) {
            sender.addChatMessage(new ChatComponentText("§7" + s));
        }
    }

    private void displayHelp(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText("§a/astralsorcery constellation§7 - lists all constellations"));
        sender.addChatMessage(new ChatComponentText("§a/astralsorcery constellation [playerName]§7 - lists all discovered constellations of the specified player if he/she is online"));
        sender.addChatMessage(new ChatComponentText("§a/astralsorcery constellation [playerName] <cName;all;reset>§7 - player specified discovers the specified constellation or all or resets all"));
    }

    private void listConstellations(ICommandSender sender) {
        for(IConstellationTier tier : ConstellationRegistry.ascendingTiers()) {
            sender.addChatMessage(new ChatComponentText("§cTier: " + tier.tierNumber() + " - showupChance: " + tier.getShowupChance()));
            for(IConstellation c : tier.getConstellations()) {
                sender.addChatMessage(new ChatComponentText("§7" + c.getName()));
            }
        }
    }

}
