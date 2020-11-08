package fr.naruse.carepackage.cmd;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import fr.naruse.carepackage.carepackage.CarePackage;
import fr.naruse.carepackage.carepackage.CarePackageType;
import fr.naruse.carepackage.inventory.InventorySet;
import fr.naruse.carepackage.main.CarePackagePlugin;
import fr.naruse.carepackage.utils.Utils;
import fr.naruse.carepackage.utils.WorldEditUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
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
            CarePackageType type = CarePackageType.valueOf(args[2].toUpperCase());
            if(type == null){
                return sendMessage(sender, "typeNotFound");
            }

            if(getIdFromName(args[1]) != -1){
                return sendMessage(sender, "alreadyUsed");
            }

            int id = getAvailableId();
            pl.getConfig().set("cp."+id+".type", type.getName());
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
            pl.getConfig().set("cp."+id+".location.destination.y", p.getLocation().getY()+1);
            pl.getConfig().set("cp."+id+".location.destination.z", p.getLocation().getZ());
            pl.getConfig().set("cp."+id+".location.destination.world", p.getWorld().getName());
            pl.getConfig().set("cp."+id+".money", 0);
            pl.saveConfig();
            return sendMessage(sender, "destinationSetted");
        }

        //RELOAD
        if(args[0].equalsIgnoreCase("reload")){
            CarePackageType.clear();
            pl.getConfigurations().reload();
            if(pl.getSqlManager() != null){
                pl.getSqlManager().loadModels(true);
            }else{
                pl.getCarePackages().reload();
            }
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
            switch (carePackage.spawn()){
                case 0: return sendMessage(sender, "spawned");
                case 1: return sendMessage(sender, "alreadySpawned");
                case 2: return sendMessage(sender, "blocksOnPath");
            }
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

        //CREATE MODEL
        if(args[0].equalsIgnoreCase("createModel")){
            if(args.length < 3){
                return help(sender, 1);
            }
            int radius;
            try{
                radius = Integer.valueOf(args[2]);
            }catch (Exception e){
                return sendMessage(sender, "wrongNumber");
            }

            args[1] = args[1].toUpperCase();

            CarePackageType type = CarePackageType.valueOf(args[1]);
            if(type != null){
                return sendMessage(sender, "modelAlreadyUsed");
            }

            WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
            if(worldEditPlugin == null){
                return sendMessage(sender, "worldEditNotFound");
            }

            Block[] points = WorldEditUtils.getPointsBlock(worldEditPlugin, p);
            if(points == null){
                return sendMessage(sender, "noSelection");
            }

            Location origin = p.getLocation().add(0, -1, 0);


            FileConfiguration configuration = pl.getConfigurations().createConfigurationModel(args[1]);
            configuration.set("name", args[1]);
            configuration.set("radius", radius);
            configuration.set("particleViewRadius", 100);
            configuration.set("soundBarrierEffectRadius", 50);
            configuration.set("speedReducer", 0.005);
            configuration.set("randomXZSpawnRange", 35);
            configuration.set("secondBeforeRemove", 60);
            configuration.set("timeBeforeBarrierEffect", 8);

            List<Block> list = Utils.blocksFromTwoPoints(points[0].getLocation(), points[1].getLocation());
            int count = 0;
            for (int i = 0; i < list.size(); i++) {
                Block b = list.get(i);
                if(b.getType() == Material.AIR){
                    continue;
                }
                configuration.set(count+".x", b.getX()-origin.getBlockX());
                configuration.set(count+".y", b.getY()-origin.getBlockY());
                configuration.set(count+".z", b.getZ()-origin.getBlockZ());
                configuration.set(count+".type", b.getTypeId());
                configuration.set(count+".data", b.getData());
                count++;
            }

            pl.getConfigurations().saveConfigs();
            CarePackageType.clear();
            pl.getConfigurations().reload();
            if(pl.getSqlManager() != null){
                pl.getSqlManager().loadModels(true);
                pl.getConfigurations().tryDelete(args[1]);
            }

            return sendMessage(sender, "modelCreated");
        }

        //DELETE MODEL
        if(args[0].equalsIgnoreCase("deleteModel")){
            if(args.length < 1){
                return help(sender, 1);
            }

            args[1] = args[1].toUpperCase();

            CarePackageType carePackageType = CarePackageType.valueOf(args[1]);
            if(carePackageType == null){
                return sendMessage(sender, "modelNotFound", new String[]{"name"}, new String[]{args[1]});
            }

            pl.getConfigurations().tryDelete(args[1]);
            if(pl.getSqlManager() != null){
                pl.getSqlManager().deleteModel(args[1]);
            }
            carePackageType.remove();

            return sendMessage(sender, "modelDeleted");
        }

        //LIST
        if(args[0].equalsIgnoreCase("list")){
            String activeCP = ",,";
            String breakdownCP = ",,";

            List<String> list = Lists.newArrayList();
            for (int i = 0; i < pl.getCarePackages().getCarePackages().size(); i++) {
                CarePackage CarePackage = pl.getCarePackages().getCarePackages().get(i);
                activeCP += ", "+CarePackage.getName();
                list.add(CarePackage.getName());
            }

            activeCP = activeCP.replace(",,, ", "");
            for(int i = 0; i != 999; i++){
                if(pl.getConfig().contains("cp."+i+".name")){
                    String name = pl.getConfig().getString("cp."+i+".name");
                    if(!list.contains(name)){
                        breakdownCP += ", "+name;
                    }
                }
            }

            breakdownCP = breakdownCP.replace(",,, ", "");
            if(breakdownCP.contains(",,")){
                breakdownCP = "";
            }
            if(activeCP.contains(",,")){
                activeCP = "";
            }

            StringBuilder stringBuilder = new StringBuilder();
            if(CarePackageType.values().size() != 0){
                stringBuilder.append(CarePackageType.values().get(0).getName());
            }
            for (int i = 1; i < CarePackageType.values().size(); i++) {
                CarePackageType carePackageType = CarePackageType.values().get(i);
                stringBuilder.append(", "+carePackageType.getName());
            }

            sendMessage(sender, "listModels", new String[]{"list"}, new String[]{stringBuilder.toString()});
            sendMessage(sender, "listNotGood", new String[]{"list"}, new String[]{breakdownCP});
            return sendMessage(sender, "listGood", new String[]{"list"}, new String[]{activeCP});
        }

        //ADD MODEL PARTICLE
        if(args[0].equalsIgnoreCase("addModelParticle")){
            if(args.length < 2){
                return help(sender, 2);
            }

            CarePackageType type = CarePackageType.valueOf(args[1].toUpperCase());
            if(type == null){
                return sendMessage(sender, "modelNotFound", new String[]{"name"}, new String[]{args[1]});
            }

            FileConfiguration configuration = pl.getConfigurations().getModelConfiguration(args[1]);
            if(configuration == null){
                return sendMessage(sender, "modelNotFound", new String[]{"name"}, new String[]{args[1]});
            }

            List<String> list;
            if(configuration.contains("particles")){
                list = configuration.getStringList("particles");
                if(list.contains("empty")){
                    list.clear();
                }
            }else{
                list = Lists.newArrayList();
            }
            StringBuilder builder = new StringBuilder(args[2]);
            for (int i = 3; i < args.length; i++) {
                builder.append(args[i]);
            }
            list.add(builder.toString());

            configuration.set("particles", list);
            pl.getConfigurations().saveConfigs();

            return sendMessage(sender, "particleSaved");
        }

        //CLEAR MODEL PARTICLE
        if(args[0].equalsIgnoreCase("clearModelParticle")){
            if(args.length < 2){
                return help(sender, 2);
            }

            CarePackageType type = CarePackageType.valueOf(args[1].toUpperCase());
            if(type == null){
                return sendMessage(sender, "modelNotFound", new String[]{"name"}, new String[]{args[1]});
            }

            FileConfiguration configuration = pl.getConfigurations().getModelConfiguration(args[1]);
            if(configuration == null){
                return sendMessage(sender, "modelNotFound", new String[]{"name"}, new String[]{args[1]});
            }

            configuration.set("particles", Lists.newArrayList("empty"));
            pl.getConfigurations().saveConfigs();

            return sendMessage(sender, "particleDeleted");
        }

        //SET MODEL PROPERTY
        if(args[0].equalsIgnoreCase("setModelProperty")){
            if(args.length < 4){
                return help(sender, 2);
            }

            CarePackageType type = CarePackageType.valueOf(args[1].toUpperCase());
            if(type == null){
                return sendMessage(sender, "modelNotFound", new String[]{"name"}, new String[]{args[1]});
            }

            FileConfiguration configuration = pl.getConfigurations().getModelConfiguration(args[1]);
            if(configuration == null){
                return sendMessage(sender, "modelNotFound", new String[]{"name"}, new String[]{args[1]});
            }

            ModelProperty property;
            try{
                property = ModelProperty.valueOf(args[2]);
            }catch (Exception e){
                return sendMessage(sender, "propertyNotFound");
            }
            Object obj;
            try{
                obj = property.getClazz().getMethod("valueOf", String.class).invoke(null, args[3]);
            }catch (Exception e){
                return sendMessage(sender, "wrongNumber");
            }

            configuration.set(property.getName(), obj);
            pl.getConfigurations().saveConfigs();

            return sendMessage(sender, "propertySaved");
        }

        //SET REWARD
        if(args[0].equalsIgnoreCase("setReward")){
            if(args.length < 3){
                return help(sender, 2);
            }

            int id = getIdFromName(args[1]);
            if(id == -1){
                return sendMessage(sender, "carePackageNotFound", new String[]{"name"}, new String[]{args[1]});
            }

            int money;
            try{
                money = Integer.valueOf(args[2]);
            }catch (Exception e){
                return sendMessage(sender, "wrongNumber");
            }

            pl.getConfig().set("cp."+id+".money", money);
            pl.saveConfig();

            return sendMessage(sender, "particleDeleted");
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
            sendNormalMessage(sender, "§6/§7cp spawn <CarePackage Name>");
            sendNormalMessage(sender, "§6/§7cp setInventory <CarePackage Name>");
            sendNormalMessage(sender, "§6/§7cp createModel <Model Name> <Radius> §7(With WorldEdit)");
            sendNormalMessage(sender, "§6/§7cp deleteModel <Model Name>");
            sendNormalMessage(sender, "§bPage: §21/2");
        }else if(page == 2){
            sendNormalMessage(sender, "§6/§7cp setLang <French, English>");
            sendNormalMessage(sender, "§6/§7cp list");
            sendNormalMessage(sender, "§6/§7cp addModelParticle <Model Name> <Particle>");
            sendNormalMessage(sender, "§6Ex: §e{type:FLAME, count:2, percentage:100, xOffset:0.2, yOffset:1, zOffset:0.2, speed:0, yReduced:1, boost:true}");
            sendNormalMessage(sender, "§6/§7cp clearModelParticle <Model Name>");
            sendNormalMessage(sender, "§6/§7cp setModelProperty <Model Name> <Property> <New Value>");
            sendNormalMessage(sender, "§6/§7cp setReward <CarePackage Name> <Money>");
           /*
            sendNormalMessage(sender, "§6/§7cp ");
            sendNormalMessage(sender, "§6/§7cp ");
            sendNormalMessage(sender, "§6/§7cp ");*/
            sendNormalMessage(sender, "§bPage: §21/2");
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
            if(carePackage != null && carePackage.getName().equals(arg)){
                return carePackage;
            }
        }
        return null;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> list = Lists.newArrayList();
        if(args.length == 2 && args[0].equalsIgnoreCase("spawn")){
            for (CarePackage carePackage : pl.getCarePackages().getCarePackages()) {
                if(carePackage.getName().contains(args[1])){
                    list.add(carePackage.getName());
                }
            }
        }
        if(args.length == 3 && args[0].equalsIgnoreCase("create")){
            fillList(list, args[2]);
        }
        if(args.length == 2 && (args[0].equalsIgnoreCase("addModelParticle") || args[0].equalsIgnoreCase("clearModelParticle"))){
            fillList(list, args[1]);
        }
        if(args.length == 2 && args[0].equalsIgnoreCase("deleteModel")){
            fillList(list, args[1]);
        }
        if(args.length == 2 && args[0].equalsIgnoreCase("setModelProperty")){
            fillList(list, args[1]);
        }
        if(args.length == 3 && args[0].equalsIgnoreCase("setModelProperty")){
            for (ModelProperty value : ModelProperty.values()) {
                if(value.name().contains(args[2].toUpperCase())){
                    list.add(value.name());
                }
            }
        }
        return list;
    }

    private void fillList(List<String> list, String arg){
        for (CarePackageType type : CarePackageType.values()) {
            if(type.getName().contains(arg.toUpperCase())){
                list.add(type.getName());
            }
        }
    }

    enum ModelProperty {
        PARTICLE_VIEW_RADIUS("particleViewRadius", Integer.class),
        SOUND_BARRIER_EFFECT_RADIUS("soundBarrierEffectRadius", Integer.class),
        SPEED_REDUCER("speedReducer", Double.class),
        RANDOM_XZ_SPAWN_RANGE("randomXZSpawnRange", Integer.class),
        SECOND_BEFORE_REMOVE("secondBeforeRemove", Integer.class),
        TIME_BEFORE_BARRIER_EFFECT("timeBeforeBarrierEffect", Integer.class),
        ;

        private String name;
        private Class clazz;
        ModelProperty(String name, Class clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        public Class getClazz() {
            return clazz;
        }

        public String getName() {
            return name;
        }
    }
}
