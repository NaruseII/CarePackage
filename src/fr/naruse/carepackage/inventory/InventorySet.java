package fr.naruse.carepackage.inventory;

import fr.naruse.carepackage.main.CarePackagePlugin;
import fr.naruse.carepackage.utils.ItemStackSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventorySet extends AbstractInventory {

    private final int id;
    private final String name;

    public InventorySet(CarePackagePlugin pl, Player p, int id, String name) {
        super(pl, p, pl.getMessageManager().get("inventory.set.name"), 9*6, false);
        this.id = id;
        this.name = name;
        initInventory(inventory);
        p.openInventory(inventory);
    }

    @Override
    protected void initInventory(Inventory inventory) {
        for (int i = 0; i < 9*6; i++) {
            if(pl.getConfig().contains("cp."+id+".inventory."+i)) {
                ItemStack item = ItemStackSerializer.deserialize(pl.getConfig().getString("cp."+i+".inventory."+i));
                inventory.setItem(i, item);
            }
        }
    }

    @Override
    protected void actionPerformed(Player p, ItemStack item, InventoryAction action, int slot) {

    }

    @Override
    public void onClose() {
        super.onClose();
        pl.getConfig().set("cp."+id+".inventory", null);
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if(itemStack != null){
                String json = ItemStackSerializer.serialize(itemStack);
                pl.getConfig().set("cp."+id+".inventory."+i, json);
            }
        }
        pl.saveConfig();
        p.sendMessage(pl.getMessageManager().get("inventory.set.saved", new String[]{"name"}, new String[]{name}));
    }
}
