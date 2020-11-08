package fr.naruse.carepackage.sql;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fr.naruse.carepackage.carepackage.BlockInfo;
import fr.naruse.carepackage.carepackage.CarePackageType;
import fr.naruse.carepackage.carepackage.ParticleInfo;
import fr.naruse.carepackage.main.CarePackagePlugin;
import fr.naruse.dbapi.api.DatabaseAPI;
import fr.naruse.dbapi.database.Database;
import fr.naruse.dbapi.sql.SQLHelper;
import fr.naruse.dbapi.sql.SQLRequest;
import fr.naruse.dbapi.sql.SQLResponse;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class SQLManager {

    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>(){}.getType();

    private CarePackagePlugin pl;

    private final String TABLE_NAME;
    private Database database;

    public SQLManager(CarePackagePlugin pl) {
        this.pl = pl;

        this.TABLE_NAME = pl.getConfig().getString("sql.tableName");

        DatabaseAPI.createNewDatabase(database = new Database("CarePackage", TABLE_NAME) {
            @Override
            public String getQuery() {
                return "CREATE TABLE `" + TABLE_NAME + "` ("
                        + "`name` varchar(64) COLLATE utf8_unicode_ci NOT NULL,"
                        + "`properties` varchar(8000) NOT NULL)";
            }
        });

        loadModels(false);
    }

    public void loadModels(boolean saveModels) {
        SQLRequest sqlRequest = new SQLRequest(SQLHelper.getSelectRequest(TABLE_NAME, "*"));
        database.getResultSet(sqlRequest, new SQLResponse() {
            @Override
            public void handleResponse(Object response) {
                if(response == null){
                    return;
                }
                ResultSet set = (ResultSet) response;

                try{
                    while (set.next()){
                        String name = set.getString("name");
                        String b64 = set.getString("properties");

                        processDecode(name, b64);
                    }
                    pl.getCarePackages().reload();
                    if(saveModels){
                        saveModels();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void processDecode(String name, String b64) {
        String properties = new String(Base64.getDecoder().decode(b64));
        Map<String, Object> map = GSON.fromJson(properties, MAP_TYPE);

        int radius = (int) map.get("radius");
        int particleViewRadius = (int) map.get("particleViewRadius");
        int soundBarrierEffectRadius = (int) map.get("soundBarrierEffectRadius");
        double speedReducer = (double) map.get("speedReducer");
        int randomXZSpawnRange = (int) map.get("randomXZSpawnRange");
        int secondBeforeRemove = (int) map.get("secondBeforeRemove");
        int timeBeforeBarrierEffect = (int) map.get("timeBeforeBarrierEffect");

        CarePackageType carePackageType = CarePackageType.valueOf(name.toUpperCase());
        if(carePackageType != null){
            pl.getLogger().warning("[SQL Model] Care PackageType with name '"+name+"' not found.");
            return;
        }
        carePackageType = CarePackageType.registerCarePackage(name);

        List<BlockInfo> blockInfos = Lists.newArrayList();

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if(!map.containsKey(i+"")){
                break;
            }
            String blockJson = (String) map.get(i+"");
            BlockInfo location = BlockInfo.Builder.fromJson(GSON.fromJson(blockJson, MAP_TYPE));
            if(location == null){
                pl.getLogger().warning("[SQL Model] Block with id in config '"+i+"' not found.");
                continue;
            }
            blockInfos.add(location);
        }

        ParticleInfo[] particleInfos = null;
        try{
            if(map.containsKey("particles")){
                List<ParticleInfo> list = Lists.newArrayList();
                for (String jsonParticle : ((String) map.get("particles")).split("/")) {
                    if(jsonParticle.equals("empty")){
                        break;
                    }
                    ParticleInfo particleInfo = ParticleInfo.Builder.fromJson(GSON.fromJson(jsonParticle, MAP_TYPE));
                    list.add(particleInfo);
                }

                particleInfos = list.toArray(new ParticleInfo[0]);
            }
        }catch (Exception e){
            pl.getLogger().warning("[Model] Particles for model '"+name+"' badly configured");
            pl.getCarePackages().getBadlyConfiguredModels().add(carePackageType);
            e.printStackTrace();
            return;
        }

        carePackageType.registerCustomModel(name, blockInfos, radius, particleInfos, particleViewRadius, soundBarrierEffectRadius, speedReducer,
                randomXZSpawnRange, secondBeforeRemove, timeBeforeBarrierEffect);
    }

    public void saveModels(){
        SQLRequest sqlRequest = new SQLRequest(SQLHelper.getTruncateRequest(TABLE_NAME));
        database.prepareStatement(sqlRequest);

        String[] args = new String[]{"name", "properties"};

        for (CarePackageType type : CarePackageType.values()) {
            String b64 = Base64.getEncoder().encodeToString(type.getModel().toJson(GSON).getBytes());
            SQLRequest sqlRequest1 = new SQLRequest(SQLHelper.getInsertRequest(TABLE_NAME, args), type.getName(), b64);
            database.prepareStatement(sqlRequest1);
        }
    }
}
