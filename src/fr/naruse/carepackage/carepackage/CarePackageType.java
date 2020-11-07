package fr.naruse.carepackage.carepackage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fr.naruse.carepackage.carepackage.type.CarePackageCustom;
import fr.naruse.carepackage.carepackage.type.CarePackageSimple;
import fr.naruse.carepackage.main.CarePackagePlugin;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

public class CarePackageType {

    private static final List<CarePackageType> carePackageTypes = Lists.newArrayList();
    private static final Map<String, CarePackageType> carePackageTypeMap = Maps.newHashMap();
    private static final Map<CarePackageType, Model> modelMap = Maps.newHashMap();

    public static final CarePackageType SIMPLE = new CarePackageType(CarePackageSimple.class, "SIMPLE", false);

    static {
        carePackageTypes.add(SIMPLE);
        carePackageTypeMap.put(SIMPLE.getName(), SIMPLE);
    }

    private Class clazz;
    private String name;
    private boolean isCustom;
    public CarePackageType(Class<? extends CarePackage> clazz, String name, boolean isCustom) {
        this.clazz = clazz;
        this.name = name;
        this.isCustom = isCustom;
    }

    public String getName(){
        return name;
    }

    public CarePackage build(CarePackagePlugin pl, String name, Location location, Inventory inventory){
        try{
            if(isCustom){
                if(!modelMap.containsKey(this)){
                    return null;
                }
                Model model = modelMap.get(this);
                Constructor<CarePackageCustom> constructor = clazz.getConstructor(pl.getClass(), String.class, getClass(), Location.class, Inventory.class, Model.class);
                return constructor.newInstance(pl, name, this, location, inventory, model);
            }else{
                Constructor<CarePackage> constructor = clazz.getConstructor(pl.getClass(), String.class, Location.class, Inventory.class);
                return constructor.newInstance(pl, name, location, inventory);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void registerCustomModel(String name, List<BlockInfo> blockInfos, int radius, ParticleInfo[] particleInfos){
        if(modelMap.containsKey(this)){
            return;
        }
        Model model = new Model(name, blockInfos, radius, particleInfos);
        modelMap.put(this, model);
    }

    public static CarePackageType registerCarePackage(String name){
        name = name.toUpperCase();
        if(carePackageTypeMap.containsKey(name)){
            return null;
        }
        CarePackageType carePackageType = new CarePackageType(CarePackageCustom.class, name, true);
        carePackageTypeMap.put(name, carePackageType);
        carePackageTypes.add(carePackageType);
        return carePackageType;
    }

    public static void clear() {
        carePackageTypes.clear();
        carePackageTypeMap.clear();
        carePackageTypes.add(SIMPLE);
        carePackageTypeMap.put(SIMPLE.getName(), SIMPLE);
    }

    public static CarePackageType valueOf(String name){
        if(carePackageTypeMap.containsKey(name)){
            return carePackageTypeMap.get(name);
        }
        return null;
    }

    public static List<CarePackageType> values(){
        return carePackageTypes;
    }
}
