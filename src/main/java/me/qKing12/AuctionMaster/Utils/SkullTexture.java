package me.qKing12.AuctionMaster.Utils;

import me.qKing12.AuctionMaster.AuctionMaster;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import static me.qKing12.AuctionMaster.Utils.HeadDatabase.headApi;

public class SkullTexture {

    private static Method GET_PROPERTIES;
    private static Method INSERT_PROPERTY;
    private static Constructor<?> GAME_PROFILE_CONSTRUCTOR;
    private static Constructor<?> PROPERTY_CONSTRUCTOR;

    static {
        try {
            final Class<?> gameProfile = Class.forName("com.mojang.authlib.GameProfile");
            final Class<?> property = Class.forName("com.mojang.authlib.properties.Property");
            final Class<?> propertyMap = Class.forName("com.mojang.authlib.properties.PropertyMap");
            GAME_PROFILE_CONSTRUCTOR = getConstructor(gameProfile, 2);
            PROPERTY_CONSTRUCTOR = getConstructor(property, 2);
            GET_PROPERTIES = getMethod(gameProfile, "getProperties");
            INSERT_PROPERTY = getMethod(propertyMap, "put");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Method getMethod(final Class<?> clazz, final String name) {
        for (final Method m : clazz.getMethods()) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return null;
    }

    private static Field getField(final Class<?> clazz, final String fieldName) throws NoSuchFieldException {
        return clazz.getDeclaredField(fieldName);
    }

    public static void setFieldValue(final Object object, final String fieldName, final Object value) throws NoSuchFieldException, IllegalAccessException {
        final Field f = getField(object.getClass(), fieldName);
        f.setAccessible(true);
        f.set(object, value);
    }

    public static Constructor<?> getConstructor(final Class<?> clazz, final int numParams) {
        for (final Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.getParameterTypes().length == numParams) {
                return constructor;
            }
        }

        return null;
    }

    public static ItemStack getSkull(String texture) {
        texture=texture.replace(" ", "");
        if(texture.length()>16) {
            try {
                ItemStack skull;
                if(AuctionMaster.upperVersion) {
                    skull = new ItemStack(Material.getMaterial("PLAYER_HEAD"), 1);
                }
                else {
                    skull = new ItemStack(Material.getMaterial("SKULL_ITEM"), 1, (short) 3);
                }

                final ItemMeta meta = skull.getItemMeta();
                try {
                    final Object profile = GAME_PROFILE_CONSTRUCTOR.newInstance(UUID.randomUUID(), UUID.randomUUID().toString().substring(17).replace("-", ""));
                    final Object properties = GET_PROPERTIES.invoke(profile);
                    INSERT_PROPERTY.invoke(properties, "textures", PROPERTY_CONSTRUCTOR.newInstance("textures", texture));
                    setFieldValue(meta, "profile", profile);
                } catch (Exception e) {
                    System.err.println("Failed to create fake GameProfile for custom player head:");
                    e.printStackTrace();
                }
                skull.setItemMeta(meta);
                return skull;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            if (texture.contains("hdb-")) {
                try {
                    return headApi.getItemHead(texture.replace("hdb-", ""));
                } catch (Exception e) {
                    texture = "mhf_question";
                }
            }
            ItemStack playerHead;
            if(AuctionMaster.upperVersion) {
                playerHead = new ItemStack(Material.getMaterial("PLAYER_HEAD"), 1);
            }
            else {
                playerHead = new ItemStack(Material.getMaterial("SKULL_ITEM"), 1, (short) 3);
            }
            SkullMeta sm = (SkullMeta) playerHead.getItemMeta();
            sm.setOwner(texture);
            playerHead.setItemMeta(sm);
            return playerHead;
        }
        return null;
    }

}
