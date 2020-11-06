package fr.naruse.carepackage.cmd;

import com.google.common.collect.Lists;
import fr.naruse.carepackage.carepackage.CarePackage;
import fr.naruse.carepackage.carepackage.CarePackageType;
import fr.naruse.carepackage.inventory.InventorySet;
import fr.naruse.carepackage.main.CarePackagePlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class CarePackageCommand implements CommandExecutor, TabCompleter {
    private CarePackagePlugin pl;
    public CarePackageCommand(CarePackagePlugin pl) {
        this.pl = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player)){
            return sendMessage(sender, "onlyForPlayers");
        }
        Player p = (Player) sender;
        if(args.length == 0){
            return help(sender, 1);
        }

        /// ADMIN
        if(!p.hasPermission("cp.help")){
            return sendMessage(sender, "youDontHaveThePermission");
        }
        if(args[0].equalsIgnoreCase("help")){
            int page = 1;
            if(args.length > 1){
                try{
                    page = Integer.valueOf(args[1]);
                }catch (Exception e){
                    return sendMessage(sender, "wrongNumber");
                }
            }
            return help(sender, page);
        }

        //CREATE
        if(args[0].equalsIgnoreCase("create")){
            if(args.length < 3){
                return help(sender, 1);
            }
            CarePackageType type;
            try{
                type = CarePackageType.valueOf(args[2].toUpperCase());
            }catch (Exception e){
                return sendMessage(sender, "typeNotFound");
            }

            if(getIdFromName(args[1]) !=
                    -1){
                return sendMessage(sender, "alreadyUsed");
            }

            int id = getAvailableId();
            pl.getConfig().set("cp."+id+".type", type.name());
            pl.getConfig().set("cp."+id+".name", args[1]);
            pl.saveConfig();

            return sendMessage(sender, "created");
        }

        //DELETE
        if(args[0].equalsIgnoreCase("delete")){
            if(args.length < 1){
                return help(sender, 1);
            }
            int id = getIdFromName(args[1]);
            if(id == -1){
                return sendMessage(sender, "carePackageNotFound", new String[]{"name"}, new String[]{args[1]});
            }

            pl.getConfig().set("cp."+id, null);
            pl.saveConfig();

            return sendMessage(sender, "deleted");
        }

        //SET DESTINATION
        if(args[0].equalsIgnoreCase("setDestination")){
            if(args.length < 2){
                return help(sender, 1);
            }
            int id = getIdFromName(args[1]);
            if(id == -1){
                return sendMessage(sender, "carePackageNotFound", new String[]{"name"}, new String[]{args[1]});
            }

            pl.getConfig().set("cp."+id+".location.destination.x", p.getLocation().getX());
            pl.getConfig().set("cp."+id+".location.destination.y", p.getLocation().getY());
            pl.getConfig().set("cp."+id+".location.destination.z", p.getLocation().getZ());
            pl.getConfig().set("cp."+id+".location.destination.world", p.getWorld().getName());
            pl.saveConfig();
            return sendMessage(sender, "destinationSetted");
        }

        //RELOAD
        if(args[0].equalsIgnoreCase("reload")){
            pl.getConfigurations().reload();
            pl.getCarePackages().reload();
            return sendMessage(sender, "reloaded");
        }

        //LANG
        if(args[0].equalsIgnoreCase("setLang")){
            if(args.length < 2){
                return help(sender, 2);
            }
            if(args[1].equalsIgnoreCase("french")){
                pl.getMessageManager().setLang("french");
                pl.getConfig().set("currentLang", "french");
                pl.saveConfig();
                pl.getConfigurations().reset(0);
                pl.getConfigurations().reload();
                return sendMessage(sender, "langChanged");
            }else if(args[1].equalsIgnoreCase("english")){
                pl.getMessageManager().setLang("english");
                pl.getConfig().set("currentLang", "english");
                pl.saveConfig();
                pl.getConfigurations().reset(0);
                pl.getConfigurations().reload();
                return sendMessage(sender, "langChanged");
            }else{
                return sendMessage(sender, "argumentNotFound", new String[]{"arg"}, new String[]{args[1]});
            }
        }

        //SPAWN
        if(args[0].equalsIgnoreCase("spawn")){
            if(args.length < 2){
                return help(sender, 1);
            }
            CarePackage carePackage = getCarePackageFromName(args[1]);
            if(carePackage == null){
                return sendMessage(sender, "carePackageNotFound", new String[]{"name"}, new String[]{args[1]});
            }
            carePackage.spawn();
            return sendMessage(sender, "spawned");
        }

        //SET INVENTORY
        if(args[0].equalsIgnoreCase("setInventory")){
            if(args.length < 2){
                return help(sender, 1);
            }
            int id = getIdFromName(args[1]);
            if(id == -1){
                return sendMessage(sender, "carePackageNotFound", new String[]{"name"}, new String[]{args[1]});
            }
            new InventorySet(pl, p, id, args[1]);
            return true;
        }
        return false;
    }

    private boolean help(CommandSender sender, int page){
        if(page == 1){
            sendNormalMessage(sender, "§6/§7cp help <[Page]>");
            sendNormalMessage(sender, "§6/§7cp create <CarePackage Name> <CarePackage Type>");
            sendNormalMessage(sender, "§6/§7cp delete <CarePackage Name>");
            sendNormalMessage(sender, "§6/§7cp setDestination <CarePackage Name>");
            sendNormalMessage(sender, "§6/§7cp reload");
            sendNormalMessage(sender, "§6/§7cp setLang <French, English>");
            sendNormalMessage(sender, "§6/§7cp spawn <CarePackage Name>");
            sendNormalMessage(sender, "§6/§7cp setInventory <CarePackage Name>");
            sendNormalMessage(sender, "§6/§7cp ");
            sendNormalMessage(sender, "§bPage: §21/1");
        }
        return true;
    }

    private boolean sendMessage(CommandSender sender, String msg, String[] toReplace, String[] replacedBy){
        sender.sendMessage(pl.getMessageManager().get("commands."+msg, toReplace, replacedBy));
        return true;
    }

    private boolean sendMessage(CommandSender sender, String msg){
        sender.sendMessage(pl.getMessageManager().get("commands."+msg));
        return true;
    }

    private boolean sendNormalMessage(CommandSender sender, String msg){
        sender.sendMessage(msg);
        return true;
    }

    private int getAvailableId() {
        for (int i = 0; i < 99999; i++) {
            if(!pl.getConfig().contains("cp."+i+".type")){
                return i;
            }
        }
        return -1;
    }

    private int getIdFromName(String arg) {
        for (int i = 0; i < 99999; i++) {
            if(pl.getConfig().contains("cp."+i+".name") && pl.getConfig().getString("cp."+i+".name").equals(arg)){
                return i;
            }
        }
        return -1;
    }

    private CarePackage getCarePackageFromName(String arg) {
        for (int i = 0; i < pl.getCarePackages().getCarePackages().size(); i++) {
            CarePackage carePackage = pl.getCarePackages().getCarePackages().get(i);
            if(carePackage.getName().equals(arg)){
                return carePackage;
            }
        }
        return null;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> list = Lists.newArrayList();
        if(args.length == 3 && args[0].equalsIgnoreCase("create")){
            for (CarePackageType type : CarePackageType.values()) {
                if(type.name().contains(args[2].toUpperCase())){
                    list.add(type.name());
                }
            }
        }
        return list;
    }
}
