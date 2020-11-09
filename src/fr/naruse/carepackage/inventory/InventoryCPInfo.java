package fr.naruse.carepackage.inventory;

import com.google.common.collect.Lists;
import fr.naruse.carepackage.carepackage.Schedule;
import fr.naruse.carepackage.main.CarePackagePlugin;
import fr.naruse.carepackage.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryCPInfo extends AbstractInventory {

    private final int id;

    public InventoryCPInfo(CarePackagePlugin pl, Player p, int id) {
        super(pl, p, pl.getMessageManager().get("inventory.cpInfo.name"), 9, false);

        this.id = id;
        this.canInteract = false;

        initInventory(inventory);
        p.openInventory(inventory);
    }

    @Override
    protected void initInventory(Inventory inventory) {
        ItemStack item = new ItemStack(Material.WOOL, 1, (byte) 4);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(pl.getMessageManager().get("inventory.cpInfo.type", new String[]{"type"}, new String[]{pl.getConfig().getString("cp."+id+".type", "none")}));
        item.setItemMeta(meta);
        inventory.setItem(2, item);

        item = new ItemStack(Material.WOOL, 1, (byte) 5);
        meta = item.getItemMeta();
        meta.setDisplayName(pl.getMessageManager().get("inventory.cpInfo.cpName", new String[]{"name"}, new String[]{pl.getConfig().getString("cp."+id+".name", "none")}));
        item.setItemMeta(meta);
        inventory.setItem(3, item);

        item = new ItemStack(Material.WOOL, 1, (byte) 9);
        meta = item.getItemMeta();
        meta.setDisplayName(pl.getMessageManager().get("inventory.cpInfo.destination"));
        if(pl.getConfig().contains("cp."+id+".location.destination")){
            Location location = Utils.getConfigLocation(pl.getConfig(), "cp."+id+".location.destination");
            if(location != null){
                meta.setLore(Lists.newArrayList("§b§lX: §b"+location.getBlockX(),
                        "§b§lY: §b"+location.getBlockY(),
                        "§b§lZ: §b"+location.getBlockZ(),
                        "§b§lWorld: §b"+location.getWorld().getName()));
            }
        }
        item.setItemMeta(meta);
        inventory.setItem(4, item);

        item = new ItemStack(Material.WOOL, 1, (byte) 14);
        meta = item.getItemMeta();
        meta.setDisplayName(pl.getMessageManager().get("inventory.cpInfo.money", new String[]{"money"}, new String[]{pl.getConfig().getString("cp."+id+".money", "none")}));
        item.setItemMeta(meta);
        inventory.setItem(5, item);

        item = new ItemStack(Material.WOOL, 1, (byte) 6);
        meta = item.getItemMeta();
        meta.setDisplayName(pl.getMessageManager().get("inventory.cpInfo.schedule"));
        if(pl.getConfig().contains("cp."+id+".schedule")){
            try{
                Schedule schedule = Schedule.Builder.fromString(pl.getConfig().getString("cp."+id+".schedule"));
                meta.setLore(Lists.newArrayList("§d§lEvery: §d"+schedule.getEvery()+" s",
                        "§d§lRandom Spawn Chance: §d"+schedule.getRandomPercentage()+" %",
                        "§d§lBroadcast Spawn: §b"+schedule.canBroadcast()));
            }catch (Exception e){

            }
        }
        item.setItemMeta(meta);
        inventory.setItem(6, item);
    }

    @Override
    protected void actionPerformed(Player p, ItemStack item, InventoryAction action, int slot) {

    }
}
