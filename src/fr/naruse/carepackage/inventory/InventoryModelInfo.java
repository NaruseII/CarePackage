package fr.naruse.carepackage.inventory;

import com.google.common.collect.Lists;
import fr.naruse.carepackage.carepackage.Model;
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

import java.util.List;

public class InventoryModelInfo extends AbstractInventory {

    private final Model model;

    public InventoryModelInfo(CarePackagePlugin pl, Player p, Model model) {
        super(pl, p, pl.getMessageManager().get("inventory.modelInfo.name"), 9, false);

        this.model = model;
        this.canInteract = false;

        initInventory(inventory);
        p.openInventory(inventory);
    }

    @Override
    protected void initInventory(Inventory inventory) {
        ItemStack item = new ItemStack(Material.WOOL, 1, (byte) 5);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(pl.getMessageManager().get("inventory.modelInfo.modelName", new String[]{"name"}, new String[]{model.getName()}));
        item.setItemMeta(meta);
        inventory.setItem(2, item);

        item = new ItemStack(Material.WOOL, 1, (byte) 9);
        meta = item.getItemMeta();
        meta.setDisplayName(pl.getMessageManager().get("inventory.modelInfo.properties"));
        meta.setLore(Lists.newArrayList("§b§lParticle View Radius: §b"+model.getProperty(Model.ModelProperty.PARTICLE_VIEW_RADIUS),
                "§b§lSound Barrier Effect Radius: §b"+model.getProperty(Model.ModelProperty.SOUND_BARRIER_EFFECT_RADIUS),
                "§b§lSeed Reducer: §b"+model.getProperty(Model.ModelProperty.SPEED_REDUCER),
                "§b§lRandom XZ Spawn Range: §b"+model.getProperty(Model.ModelProperty.RANDOM_XZ_SPAWN_RANGE),
                "§b§lSecond Before Remove: §b"+model.getProperty(Model.ModelProperty.SECOND_BEFORE_REMOVE),
                "§b§lTime Before Barrier Effect: §b"+model.getProperty(Model.ModelProperty.TIME_BEFORE_BARRIER_EFFECT)));
        item.setItemMeta(meta);
        inventory.setItem(3, item);

        item = new ItemStack(Material.WOOL, 1, (byte) 14);
        meta = item.getItemMeta();
        meta.setDisplayName(pl.getMessageManager().get("inventory.modelInfo.radius", new String[]{"radius"}, new String[]{model.getRadius()+""}));
        item.setItemMeta(meta);
        inventory.setItem(5, item);

        item = new ItemStack(Material.WOOL, 1, (byte) 6);
        meta = item.getItemMeta();
        meta.setDisplayName(pl.getMessageManager().get("inventory.modelInfo.blockInfos", new String[]{"size"}, new String[]{model.getBlockInfos().size()+""}));
        item.setItemMeta(meta);
        inventory.setItem(6, item);
    }

    @Override
    protected void actionPerformed(Player p, ItemStack item, InventoryAction action, int slot) {

    }
}
