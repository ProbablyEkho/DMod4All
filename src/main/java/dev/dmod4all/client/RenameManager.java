package dev.dmod4all.client;

import com.google.gson.*;
import dev.dmod4all.DMod4All;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class RenameManager {
    private static final Map<String, String> RENAMES = new HashMap<>();
    private static final Path RENAME_LIST_FILE = FMLPaths.CONFIGDIR.get().resolve(DMod4All.MODID).resolve("rename_list.json");
    public static void load() {
        try {
            Files.createDirectories(RENAME_LIST_FILE.getParent());
            if(!Files.exists(RENAME_LIST_FILE)) {
                save();
                return;
            }
            RENAMES.clear();
            JsonObject json = JsonParser.parseString(Files.readString(RENAME_LIST_FILE)).getAsJsonObject();
            for(Map.Entry<String, JsonElement> entry : json.entrySet()) {
                JsonElement value = entry.getValue();

                if(value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
                    RENAMES.put(entry.getKey(), value.getAsString());
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public static void setName(ItemStack itemStack, String name) {
        if(itemStack.isEmpty()) {
            return;
        }
        ResourceLocation item = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        if(name == null || name.isBlank()) {
            RENAMES.remove(item.toString());
        } else {
            RENAMES.put(item.toString(), name);
        }
        save();
    }
    public static String getName(ItemStack itemStack) {
        if(itemStack.isEmpty()) {
            return null;
        }
        return RENAMES.get(BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString());
    }
    private static void save() {
        try {
            Files.writeString(RENAME_LIST_FILE, new GsonBuilder().setPrettyPrinting().create().toJson(RENAMES));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
