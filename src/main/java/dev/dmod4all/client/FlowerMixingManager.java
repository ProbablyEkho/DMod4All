package dev.dmod4all.client;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.dmod4all.DMod4All;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FlowerMixingManager {
    private static final Map<String, String> MIXES = new HashMap<>();
    private static final Path MIX_LIST_FILE = FMLPaths.CONFIGDIR.get().resolve(DMod4All.MODID).resolve("mix_list.json");
    public static void load() {
        try {
            Files.createDirectories(MIX_LIST_FILE.getParent());
            if(!Files.exists(MIX_LIST_FILE)) {
                save();
                return;
            }
            MIXES.clear();
            JsonObject json = JsonParser.parseString(Files.readString(MIX_LIST_FILE)).getAsJsonObject();
            for(Map.Entry<String, JsonElement> entry : json.entrySet()) {
                JsonElement value = entry.getValue();

                if(value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
                    MIXES.put(entry.getKey(), value.getAsString());
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public static Block getFlower(Block first, Block second) {
        ResourceLocation firstFlower = ResourceLocation.parse(BuiltInRegistries.BLOCK.getKey(first).toString());
        ResourceLocation secondFlower = ResourceLocation.parse(BuiltInRegistries.BLOCK.getKey(second).toString());
        String result = MIXES.get(firstFlower + "+" + secondFlower);
        if(result == null) {
            result = MIXES.get(secondFlower + "+" + firstFlower);
        }
        if(result == null) {
            return null;
        }
        return BuiltInRegistries.BLOCK.get(ResourceLocation.parse(result));
    }
    private static void save() {
        try {
            Files.writeString(MIX_LIST_FILE, new GsonBuilder().setPrettyPrinting().create().toJson(MIXES));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
