package me.qKing12.AuctionMaster.Utils;

import me.qKing12.AuctionMaster.AuctionMaster;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {
    private static final File log = new File(AuctionMaster.plugin.getDataFolder(), "database/logs.txt");
    public static void injectToLog(String inject){
        Bukkit.getScheduler().runTaskAsynchronously(AuctionMaster.plugin, () -> {
            if(!log.exists())
                try {
                    log.createNewFile();
                }catch(Exception e){
                    e.printStackTrace();
                }
            DateFormat simple = new SimpleDateFormat("[MMM:dd:hh:ss] ");
            try {
                FileWriter fileWriter = new FileWriter(log, true);
                PrintWriter printWriter = new PrintWriter(fileWriter);
                printWriter.println(simple.format(new Date())+inject);
                printWriter.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        });
    }

    public static int getEmptySlots(Player player) {
        PlayerInventory inventory = player.getInventory();
        List<ItemStack> items = new ArrayList<>(Arrays.asList(inventory.getContents().clone()));

        boolean is1_8 = Bukkit.getVersion().contains("1.8");
        if (!is1_8)
            for (ItemStack armor : inventory.getArmorContents())
                items.remove(armor);

        ItemStack offHand = !is1_8 ? inventory.getItemInOffHand() : null;
        boolean offHandDeleted = false;

        int slotCount = 0;
        for (ItemStack item : items) {
            if (!is1_8 && !offHandDeleted) {
                if ((item == null || item.getType().equals(Material.AIR)) && offHand.getType().equals(Material.AIR)) {
                    offHandDeleted = true;
                    continue;
                }

                if (item != null && !item.getType().equals(Material.AIR) && !offHand.getType().equals(Material.AIR)) {
                    if (item == offHand || item.equals(offHand)) {
                        offHandDeleted = true;
                        continue;
                    }
                }
            }

            if (item == null || item.getType().equals(Material.AIR))
                slotCount++;
        }

        return slotCount;
    }

    public static void playSound(Player p, String soundCfg){
        Bukkit.getScheduler().runTaskAsynchronously(AuctionMaster.plugin, () -> {
            String sound = AuctionMaster.soundsCfg.getString(soundCfg);
            if (sound == null || sound.equals("none"))
                return;
            try {
                if (sound.contains(":")) {
                    String[] soundArgs = sound.split(":");
                    Bukkit.getScheduler().runTask(AuctionMaster.plugin, () -> p.playSound(p.getLocation(), Sound.valueOf(soundArgs[0]), 2, Integer.valueOf(soundArgs[1])));
                } else
                    Bukkit.getScheduler().runTask(AuctionMaster.plugin, () -> p.playSound(p.getLocation(), Sound.valueOf(sound), 2, 2));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static String getDisplayName(ItemStack item){
        String itemName;
        if(item.getItemMeta().hasDisplayName())
            itemName=item.getItemMeta().getDisplayName();
        else
            itemName = ChatColor.WHITE + capitalize(item.getType().name().replace("_ITEM", "").replace("_", " "));

        return itemName;
    }

    public static String capitalize(String text){
        String c = (text != null) ? text.trim() : "";
        String[] words = c.split(" ");

        StringBuilder result = new StringBuilder();
        for (String w : words)
            result.append(w.length() > 1 ? w.substring(0, 1).toUpperCase(Locale.US) + w.substring(1).toLowerCase(Locale.US) : w).append(" ");

        return result.toString().trim();
    }

    public static String itemToBase64(ItemStack item) {
        try {
            if (item == null)
                return null;

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(item);

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            AuctionMaster.plugin.getLogger().warning("Unable to save item stacks. "+ e);
        }
        return null;
    }

    public static ItemStack itemFromBase64(String data) {
        try {
            if (data == null || data.equals(""))
                return null;

            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Object object = dataInput.readObject();
            if (!(object instanceof ItemStack item))
                return null;

            dataInput.close();
            return item;
        } catch (Exception e) {
            AuctionMaster.plugin.getLogger().warning("Unable to decode class type. "+ e);
        }
        return null;
    }


    public static String chat (String msg){
        if(msg==null)
            return ChatColor.translateAlternateColorCodes('&', "&cConfig Missing Text");
        else if (!Pattern.compile("\\{#[0-9A-Fa-f]{6}}").matcher(msg).find()) {
            return ChatColor.translateAlternateColorCodes('&', msg);
        } else {
            Matcher m = Pattern.compile("\\{#[0-9A-Fa-f]{6}}").matcher(msg);
            String s;
            String sNew;
            while (m.find()) {
                s = m.group();
                sNew = "§x" + Arrays.stream(s.split("")).map((s2) -> "§" + s2).collect(Collectors.joining()).replace("§#", "");
                msg = msg.replace(s, sNew.replace("§{", "").replace("§}", ""));
            }
            return ChatColor.translateAlternateColorCodes('&', msg);
        }
    }

    public static String fromMilisecondsAuction(Long a){
        Long zi = a/86400000;
        a%=86400000;
        Long ora = a/3600000;
        a%=3600000;
        Long minut = a/60000;
        a%=60000;
        Long secunda = a/1000;
        String timpFinal = "";
        if(zi>0) {
            timpFinal=timpFinal.concat(zi+ AuctionMaster.configLoad.short_day);
        }
        if(ora>0){
            timpFinal=timpFinal.concat(ora+ AuctionMaster.configLoad.short_hour);
        }
        if(minut>0){
            timpFinal=timpFinal.concat(minut+ AuctionMaster.configLoad.short_minute);
        }
        if(secunda>0){
            timpFinal=timpFinal.concat(secunda+ AuctionMaster.configLoad.short_second);
            return timpFinal;
        }
        else
        if(timpFinal.equals(""))
            return Utils.chat("&eEnding Soon!");
        else
            return timpFinal;
    }

    public static String fromMiliseconds(int a){
        int zi = a/86400000;
        int ora = a/3600000;
        int minut = a/60000;
        int secunda = a/1000;
        if(zi>0) {
            if (zi == 1) return "1 ".concat(AuctionMaster.configLoad.day);
            return Integer.toString(zi).concat(" ").concat(AuctionMaster.configLoad.days);
        }
        else if(ora>0){
            if(ora == 1) return "1 ".concat(AuctionMaster.configLoad.hour);
            return Integer.toString(ora).concat(" ").concat(AuctionMaster.configLoad.hours);
        }
        else if(minut>0){
            if(minut == 1) return "1 ".concat(AuctionMaster.configLoad.minute);
            return Integer.toString(minut).concat(" ").concat(AuctionMaster.configLoad.minutes);
        }
        else if(secunda>0){
            if(secunda == 1) return "1 ".concat(AuctionMaster.configLoad.second);
            return Integer.toString(secunda).concat(" ").concat(AuctionMaster.configLoad.seconds);
        }
        else
            return "Never Expire";
    }

    public static int toMiliseconds(String a){
        String[] a2 = a.split(" ");
        int timp = Integer.parseInt(a2[0])*1000;
        switch (a2[1].toLowerCase()) {
            case "m":
                timp *= 60;
                break;
            case "h":
                timp *= 3600;
                break;
            case "d":
                timp *= 3600 * 24;
                break;
            default:
                timp = 1000 * 2 * 3600;
                break;
        }

        return timp;
    }

    public static double moneyInput(String input) {
        double money = 0;
        try {
            money = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            input = input.replace(" ", "");
            String[] components = {"m", "k"};
            double side;
            String[] textParts;
            for (String component : components) {
                textParts = input.split(component);
                try {
                    side = Double.parseDouble(textParts[0]);
                    if (component.equals("k")) {
                        side *= 1000;
                    } else if (component.equals("m")) {
                        side *= 1000000;
                    } else {
                        return -1;
                    }
                    money += side;
                    input = input.substring(input.indexOf(component)+1);
                } catch (NumberFormatException ignored) {

                }
            }
        }
        return money;
    }

    public static String getIdF(int x){
        switch(x){
            case 0:
                return "AIR";
            case 1:
                return "STONE";
            case 2:
                return "GRASS";
            case 3:
                return "DIRT";
            case 4:
                return "COBBLESTONE";
            case 5:
                return "WOOD";
            case 6:
                return "SAPLING";
            case 7:
                return "BEDROCK";
            case 8:
                return "WATER";
            case 9:
                return "STATIONARY_WATER";
            case 10:
                return "LAVA";
            case 11:
                return "STATIONARY_LAVA";
            case 12:
                return "SAND";
            case 13:
                return "GRAVEL";
            case 14:
                return "GOLD_ORE";
            case 15:
                return "IRON_ORE";
            case 16:
                return "COAL_ORE";
            case 17:
                return "LOG";
            case 18:
                return "LEAVES";
            case 19:
                return "SPONGE";
            case 20:
                return "GLASS";
            case 21:
                return "LAPIS_ORE";
            case 22:
                return "LAPIS_BLOCK";
            case 23:
                return "DISPENSER";
            case 24:
                return "SANDSTONE";
            case 25:
                return "NOTE_BLOCK";
            case 26:
                return "BED_BLOCK";
            case 27:
                return "POWERED_RAIL";
            case 28:
                return "DETECTOR_RAIL";
            case 29:
                return "PISTON_STICKY_BASE";
            case 30:
                return "WEB";
            case 31:
                return "LONG_GRASS";
            case 32:
                return "DEAD_BUSH";
            case 33:
                return "PISTON_BASE";
            case 34:
                return "PISTON_EXTENSION";
            case 35:
                return "WOOL";
            case 36:
                return "PISTON_MOVING_PIECE";
            case 37:
                return "YELLOW_FLOWER";
            case 38:
                return "RED_ROSE";
            case 39:
                return "BROWN_MUSHROOM";
            case 40:
                return "RED_MUSHROOM";
            case 41:
                return "GOLD_BLOCK";
            case 42:
                return "IRON_BLOCK";
            case 43:
                return "DOUBLE_STEP";
            case 44:
                return "STEP";
            case 45:
                return "BRICK";
            case 46:
                return "TNT";
            case 47:
                return "BOOKSHELF";
            case 48:
                return "MOSSY_COBBLESTONE";
            case 49:
                return "OBSIDIAN";
            case 50:
                return "TORCH";
            case 51:
                return "FIRE";
            case 52:
                return "MOB_SPAWNER";
            case 53:
                return "WOOD_STAIRS";
            case 54:
                return "CHEST";
            case 55:
                return "REDSTONE_WIRE";
            case 56:
                return "DIAMOND_ORE";
            case 57:
                return "DIAMOND_BLOCK";
            case 58:
                return "WORKBENCH";
            case 59:
                return "CROPS";
            case 60:
                return "SOIL";
            case 61:
                return "FURNACE";
            case 62:
                return "BURNING_FURNACE";
            case 63:
                return "SIGN_POST";
            case 64:
                return "WOODEN_DOOR";
            case 65:
                return "LADDER";
            case 66:
                return "RAILS";
            case 67:
                return "COBBLESTONE_STAIRS";
            case 68:
                return "WALL_SIGN";
            case 69:
                return "LEVER";
            case 70:
                return "STONE_PLATE";
            case 71:
                return "IRON_DOOR_BLOCK";
            case 72:
                return "WOOD_PLATE";
            case 73:
                return "REDSTONE_ORE";
            case 74:
                return "GLOWING_REDSTONE_ORE";
            case 75:
                return "REDSTONE_TORCH_OFF";
            case 76:
                return "REDSTONE_TORCH_ON";
            case 77:
                return "STONE_BUTTON";
            case 78:
                return "SNOW";
            case 79:
                return "ICE";
            case 80:
                return "SNOW_BLOCK";
            case 81:
                return "CACTUS";
            case 82:
                return "CLAY";
            case 83:
                return "SUGAR_CANE_BLOCK";
            case 84:
                return "JUKEBOX";
            case 85:
                return "FENCE";
            case 86:
                return "PUMPKIN";
            case 87:
                return "NETHERRACK";
            case 88:
                return "SOUL_SAND";
            case 89:
                return "GLOWSTONE";
            case 90:
                return "PORTAL";
            case 91:
                return "JACK_O_LANTERN";
            case 92:
                return "CAKE_BLOCK";
            case 93:
                return "DIODE_BLOCK_OFF";
            case 94:
                return "DIODE_BLOCK_ON";
            case 95:
                return "STAINED_GLASS";
            case 96:
                return "TRAP_DOOR";
            case 97:
                return "MONSTER_EGGS";
            case 98:
                return "SMOOTH_BRICK";
            case 99:
                return "HUGE_MUSHROOM_1";
            case 100:
                return "HUGE_MUSHROOM_2";
            case 101:
                return "IRON_FENCE";
            case 102:
                return "THIN_GLASS";
            case 103:
                return "MELON_BLOCK";
            case 104:
                return "PUMPKIN_STEM";
            case 105:
                return "MELON_STEM";
            case 106:
                return "VINE";
            case 107:
                return "FENCE_GATE";
            case 108:
                return "BRICK_STAIRS";
            case 109:
                return "SMOOTH_STAIRS";
            case 110:
                return "MYCEL";
            case 111:
                return "WATER_LILY";
            case 112:
                return "NETHER_BRICK";
            case 113:
                return "NETHER_FENCE";
            case 114:
                return "NETHER_BRICK_STAIRS";
            case 115:
                return "NETHER_WARTS";
            case 116:
                return "ENCHANTMENT_TABLE";
            case 117:
                return "BREWING_STAND";
            case 118:
                return "CAULDRON";
            case 119:
                return "ENDER_PORTAL";
            case 120:
                return "ENDER_PORTAL_FRAME";
            case 121:
                return "ENDER_STONE";
            case 122:
                return "DRAGON_EGG";
            case 123:
                return "REDSTONE_LAMP_OFF";
            case 124:
                return "REDSTONE_LAMP_ON";
            case 125:
                return "WOOD_DOUBLE_STEP";
            case 126:
                return "WOOD_STEP";
            case 127:
                return "COCOA";
            case 128:
                return "SANDSTONE_STAIRS";
            case 129:
                return "EMERALD_ORE";
            case 130:
                return "ENDER_CHEST";
            case 131:
                return "TRIPWIRE_HOOK";
            case 132:
                return "TRIPWIRE";
            case 133:
                return "EMERALD_BLOCK";
            case 134:
                return "SPRUCE_WOOD_STAIRS";
            case 135:
                return "BIRCH_WOOD_STAIRS";
            case 136:
                return "JUNGLE_WOOD_STAIRS";
            case 137:
                return "COMMAND";
            case 138:
                return "BEACON";
            case 139:
                return "COBBLE_WALL";
            case 140:
                return "FLOWER_POT";
            case 141:
                return "CARROT";
            case 142:
                return "POTATO";
            case 143:
                return "WOOD_BUTTON";
            case 144:
                return "SKULL";
            case 145:
                return "ANVIL";
            case 146:
                return "TRAPPED_CHEST";
            case 147:
                return "GOLD_PLATE";
            case 148:
                return "IRON_PLATE";
            case 149:
                return "REDSTONE_COMPARATOR_OFF";
            case 150:
                return "REDSTONE_COMPARATOR_ON";
            case 151:
                return "DAYLIGHT_DETECTOR";
            case 152:
                return "REDSTONE_BLOCK";
            case 153:
                return "QUARTZ_ORE";
            case 154:
                return "HOPPER";
            case 155:
                return "QUARTZ_BLOCK";
            case 156:
                return "QUARTZ_STAIRS";
            case 157:
                return "ACTIVATOR_RAIL";
            case 158:
                return "DROPPER";
            case 159:
                return "STAINED_CLAY";
            case 160:
                return "STAINED_GLASS_PANE";
            case 161:
                return "LEAVES_2";
            case 162:
                return "LOG_2";
            case 163:
                return "ACACIA_STAIRS";
            case 164:
                return "DARK_OAK_STAIRS";
            case 165:
                return "SLIME_BLOCK";
            case 166:
                return "BARRIER";
            case 167:
                return "IRON_TRAPDOOR";
            case 168:
                return "PRISMARINE";
            case 169:
                return "SEA_LANTERN";
            case 170:
                return "HAY_BLOCK";
            case 171:
                return "CARPET";
            case 172:
                return "HARD_CLAY";
            case 173:
                return "COAL_BLOCK";
            case 174:
                return "PACKED_ICE";
            case 175:
                return "DOUBLE_PLANT";
            case 176:
                return "STANDING_BANNER";
            case 177:
                return "WALL_BANNER";
            case 178:
                return "DAYLIGHT_DETECTOR_INVERTED";
            case 179:
                return "RED_SANDSTONE";
            case 180:
                return "RED_SANDSTONE_STAIRS";
            case 181:
                return "DOUBLE_STONE_SLAB2";
            case 182:
                return "STONE_SLAB2";
            case 183:
                return "SPRUCE_FENCE_GATE";
            case 184:
                return "BIRCH_FENCE_GATE";
            case 185:
                return "JUNGLE_FENCE_GATE";
            case 186:
                return "DARK_OAK_FENCE_GATE";
            case 187:
                return "ACACIA_FENCE_GATE";
            case 188:
                return "SPRUCE_FENCE";
            case 189:
                return "BIRCH_FENCE";
            case 190:
                return "JUNGLE_FENCE";
            case 191:
                return "DARK_OAK_FENCE";
            case 192:
                return "ACACIA_FENCE";
            case 193:
                return "SPRUCE_DOOR";
            case 194:
                return "BIRCH_DOOR";
            case 195:
                return "JUNGLE_DOOR";
            case 196:
                return "ACACIA_DOOR";
            case 197:
                return "DARK_OAK_DOOR";
            case 198:
                return "END_ROD";
            case 199:
                return "CHORUS_PLANT";
            case 200:
                return "CHORUS_FLOWER";
            case 201:
                return "PURPUR_BLOCK";
            case 202:
                return "PURPUR_PILLAR";
            case 203:
                return "PURPUR_STAIRS";
            case 204:
                return "PURPUR_DOUBLE_SLAB";
            case 205:
                return "PURPUR_SLAB";
            case 206:
                return "END_BRICKS";
            case 207:
                return "BEETROOT_BLOCK";
            case 208:
                return "GRASS_PATH";
            case 209:
                return "END_GATEWAY";
            case 210:
                return "COMMAND_REPEATING";
            case 211:
                return "COMMAND_CHAIN";
            case 212:
                return "FROSTED_ICE";
            case 213:
                return "MAGMA";
            case 214:
                return "NETHER_WART_BLOCK";
            case 215:
                return "RED_NETHER_BRICK";
            case 216:
                return "BONE_BLOCK";
            case 217:
                return "STRUCTURE_VOID";
            case 218:
                return "OBSERVER";
            case 219:
                return "WHITE_SHULKER_BOX";
            case 220:
                return "ORANGE_SHULKER_BOX";
            case 221:
                return "MAGENTA_SHULKER_BOX";
            case 222:
                return "LIGHT_BLUE_SHULKER_BOX";
            case 223:
                return "YELLOW_SHULKER_BOX";
            case 224:
                return "LIME_SHULKER_BOX";
            case 225:
                return "PINK_SHULKER_BOX";
            case 226:
                return "GRAY_SHULKER_BOX";
            case 227:
                return "SILVER_SHULKER_BOX";
            case 228:
                return "CYAN_SHULKER_BOX";
            case 229:
                return "PURPLE_SHULKER_BOX";
            case 230:
                return "BLUE_SHULKER_BOX";
            case 231:
                return "BROWN_SHULKER_BOX";
            case 232:
                return "GREEN_SHULKER_BOX";
            case 233:
                return "RED_SHULKER_BOX";
            case 234:
                return "BLACK_SHULKER_BOX";
            case 235:
                return "WHITE_GLAZED_TERRACOTTA";
            case 236:
                return "ORANGE_GLAZED_TERRACOTTA";
            case 237:
                return "MAGENTA_GLAZED_TERRACOTTA";
            case 238:
                return "LIGHT_BLUE_GLAZED_TERRACOTTA";
            case 239:
                return "YELLOW_GLAZED_TERRACOTTA";
            case 240:
                return "LIME_GLAZED_TERRACOTTA";
            case 241:
                return "PINK_GLAZED_TERRACOTTA";
            case 242:
                return "GRAY_GLAZED_TERRACOTTA";
            case 243:
                return "SILVER_GLAZED_TERRACOTTA";
            case 244:
                return "CYAN_GLAZED_TERRACOTTA";
            case 245:
                return "PURPLE_GLAZED_TERRACOTTA";
            case 246:
                return "BLUE_GLAZED_TERRACOTTA";
            case 247:
                return "BROWN_GLAZED_TERRACOTTA";
            case 248:
                return "GREEN_GLAZED_TERRACOTTA";
            case 249:
                return "RED_GLAZED_TERRACOTTA";
            case 250:
                return "BLACK_GLAZED_TERRACOTTA";
            case 251:
                return "CONCRETE";
            case 252:
                return "CONCRETE_POWDER";
            case 255:
                return "STRUCTURE_BLOCK";
            case 256:
                return "IRON_SPADE";
            case 257:
                return "IRON_PICKAXE";
            case 258:
                return "IRON_AXE";
            case 259:
                return "FLINT_AND_STEEL";
            case 260:
                return "APPLE";
            case 261:
                return "BOW";
            case 262:
                return "ARROW";
            case 263:
                return "COAL";
            case 264:
                return "DIAMOND";
            case 265:
                return "IRON_INGOT";
            case 266:
                return "GOLD_INGOT";
            case 267:
                return "IRON_SWORD";
            case 268:
                return "WOOD_SWORD";
            case 269:
                return "WOOD_SPADE";
            case 270:
                return "WOOD_PICKAXE";
            case 271:
                return "WOOD_AXE";
            case 272:
                return "STONE_SWORD";
            case 273:
                return "STONE_SPADE";
            case 274:
                return "STONE_PICKAXE";
            case 275:
                return "STONE_AXE";
            case 276:
                return "DIAMOND_SWORD";
            case 277:
                return "DIAMOND_SPADE";
            case 278:
                return "DIAMOND_PICKAXE";
            case 279:
                return "DIAMOND_AXE";
            case 280:
                return "STICK";
            case 281:
                return "BOWL";
            case 282:
                return "MUSHROOM_SOUP";
            case 283:
                return "GOLD_SWORD";
            case 284:
                return "GOLD_SPADE";
            case 285:
                return "GOLD_PICKAXE";
            case 286:
                return "GOLD_AXE";
            case 287:
                return "STRING";
            case 288:
                return "FEATHER";
            case 289:
                return "SULPHUR";
            case 290:
                return "WOOD_HOE";
            case 291:
                return "STONE_HOE";
            case 292:
                return "IRON_HOE";
            case 293:
                return "DIAMOND_HOE";
            case 294:
                return "GOLD_HOE";
            case 295:
                return "SEEDS";
            case 296:
                return "WHEAT";
            case 297:
                return "BREAD";
            case 298:
                return "LEATHER_HELMET";
            case 299:
                return "LEATHER_CHESTPLATE";
            case 300:
                return "LEATHER_LEGGINGS";
            case 301:
                return "LEATHER_BOOTS";
            case 302:
                return "CHAINMAIL_HELMET";
            case 303:
                return "CHAINMAIL_CHESTPLATE";
            case 304:
                return "CHAINMAIL_LEGGINGS";
            case 305:
                return "CHAINMAIL_BOOTS";
            case 306:
                return "IRON_HELMET";
            case 307:
                return "IRON_CHESTPLATE";
            case 308:
                return "IRON_LEGGINGS";
            case 309:
                return "IRON_BOOTS";
            case 310:
                return "DIAMOND_HELMET";
            case 311:
                return "DIAMOND_CHESTPLATE";
            case 312:
                return "DIAMOND_LEGGINGS";
            case 313:
                return "DIAMOND_BOOTS";
            case 314:
                return "GOLD_HELMET";
            case 315:
                return "GOLD_CHESTPLATE";
            case 316:
                return "GOLD_LEGGINGS";
            case 317:
                return "GOLD_BOOTS";
            case 318:
                return "FLINT";
            case 319:
                return "PORK";
            case 320:
                return "GRILLED_PORK";
            case 321:
                return "PAINTING";
            case 322:
                return "GOLDEN_APPLE";
            case 323:
                return "SIGN";
            case 324:
                return "WOOD_DOOR";
            case 325:
                return "BUCKET";
            case 326:
                return "WATER_BUCKET";
            case 327:
                return "LAVA_BUCKET";
            case 328:
                return "MINECART";
            case 329:
                return "SADDLE";
            case 330:
                return "IRON_DOOR";
            case 331:
                return "REDSTONE";
            case 332:
                return "SNOW_BALL";
            case 333:
                return "BOAT";
            case 334:
                return "LEATHER";
            case 335:
                return "MILK_BUCKET";
            case 336:
                return "CLAY_BRICK";
            case 337:
                return "CLAY_BALL";
            case 338:
                return "SUGAR_CANE";
            case 339:
                return "PAPER";
            case 340:
                return "BOOK";
            case 341:
                return "SLIME_BALL";
            case 342:
                return "STORAGE_MINECART";
            case 343:
                return "POWERED_MINECART";
            case 344:
                return "EGG";
            case 345:
                return "COMPASS";
            case 346:
                return "FISHING_ROD";
            case 347:
                return "WATCH";
            case 348:
                return "GLOWSTONE_DUST";
            case 349:
                return "RAW_FISH";
            case 350:
                return "COOKED_FISH";
            case 351:
                return "INK_SACK";
            case 352:
                return "BONE";
            case 353:
                return "SUGAR";
            case 354:
                return "CAKE";
            case 355:
                return "BED";
            case 356:
                return "DIODE";
            case 357:
                return "COOKIE";
            case 358:
                return "MAP";
            case 359:
                return "SHEARS";
            case 360:
                return "MELON";
            case 361:
                return "PUMPKIN_SEEDS";
            case 362:
                return "MELON_SEEDS";
            case 363:
                return "RAW_BEEF";
            case 364:
                return "COOKED_BEEF";
            case 365:
                return "RAW_CHICKEN";
            case 366:
                return "COOKED_CHICKEN";
            case 367:
                return "ROTTEN_FLESH";
            case 368:
                return "ENDER_PEARL";
            case 369:
                return "BLAZE_ROD";
            case 370:
                return "GHAST_TEAR";
            case 371:
                return "GOLD_NUGGET";
            case 372:
                return "NETHER_STALK";
            case 373:
                return "POTION";
            case 374:
                return "GLASS_BOTTLE";
            case 375:
                return "SPIDER_EYE";
            case 376:
                return "FERMENTED_SPIDER_EYE";
            case 377:
                return "BLAZE_POWDER";
            case 378:
                return "MAGMA_CREAM";
            case 379:
                return "BREWING_STAND_ITEM";
            case 380:
                return "CAULDRON_ITEM";
            case 381:
                return "EYE_OF_ENDER";
            case 382:
                return "SPECKLED_MELON";
            case 383:
                return "MONSTER_EGG";
            case 384:
                return "EXP_BOTTLE";
            case 385:
                return "FIREBALL";
            case 386:
                return "BOOK_AND_QUILL";
            case 387:
                return "WRITTEN_BOOK";
            case 388:
                return "EMERALD";
            case 389:
                return "ITEM_FRAME";
            case 390:
                return "FLOWER_POT_ITEM";
            case 391:
                return "CARROT_ITEM";
            case 392:
                return "POTATO_ITEM";
            case 393:
                return "BAKED_POTATO";
            case 394:
                return "POISONOUS_POTATO";
            case 395:
                return "EMPTY_MAP";
            case 396:
                return "GOLDEN_CARROT";
            case 397:
                return "SKULL_ITEM";
            case 398:
                return "CARROT_STICK";
            case 399:
                return "NETHER_STAR";
            case 400:
                return "PUMPKIN_PIE";
            case 401:
                return "FIREWORK";
            case 402:
                return "FIREWORK_CHARGE";
            case 403:
                return "ENCHANTED_BOOK";
            case 404:
                return "REDSTONE_COMPARATOR";
            case 405:
                return "NETHER_BRICK_ITEM";
            case 406:
                return "QUARTZ";
            case 407:
                return "EXPLOSIVE_MINECART";
            case 408:
                return "HOPPER_MINECART";
            case 409:
                return "PRISMARINE_SHARD";
            case 410:
                return "PRISMARINE_CRYSTALS";
            case 411:
                return "RABBIT";
            case 412:
                return "COOKED_RABBIT";
            case 413:
                return "RABBIT_STEW";
            case 414:
                return "RABBIT_FOOT";
            case 415:
                return "RABBIT_HIDE";
            case 416:
                return "ARMOR_STAND";
            case 417:
                return "IRON_BARDING";
            case 418:
                return "GOLD_BARDING";
            case 419:
                return "DIAMOND_BARDING";
            case 420:
                return "LEASH";
            case 421:
                return "NAME_TAG";
            case 422:
                return "COMMAND_MINECART";
            case 423:
                return "MUTTON";
            case 424:
                return "COOKED_MUTTON";
            case 425:
                return "BANNER";
            case 426:
                return "END_CRYSTAL";
            case 427:
                return "SPRUCE_DOOR_ITEM";
            case 428:
                return "BIRCH_DOOR_ITEM";
            case 429:
                return "JUNGLE_DOOR_ITEM";
            case 430:
                return "ACACIA_DOOR_ITEM";
            case 431:
                return "DARK_OAK_DOOR_ITEM";
            case 432:
                return "CHORUS_FRUIT";
            case 433:
                return "CHORUS_FRUIT_POPPED";
            case 434:
                return "BEETROOT";
            case 435:
                return "BEETROOT_SEEDS";
            case 436:
                return "BEETROOT_SOUP";
            case 437:
                return "DRAGONS_BREATH";
            case 438:
                return "SPLASH_POTION";
            case 439:
                return "SPECTRAL_ARROW";
            case 440:
                return "TIPPED_ARROW";
            case 441:
                return "LINGERING_POTION";
            case 442:
                return "SHIELD";
            case 443:
                return "ELYTRA";
            case 444:
                return "BOAT_SPRUCE";
            case 445:
                return "BOAT_BIRCH";
            case 446:
                return "BOAT_JUNGLE";
            case 447:
                return "BOAT_ACACIA";
            case 448:
                return "BOAT_DARK_OAK";
            case 449:
                return "TOTEM";
            case 450:
                return "SHULKER_SHELL";
            case 452:
                return "IRON_NUGGET";
            case 453:
                return "KNOWLEDGE_BOOK";
            case 2256:
                return "GOLD_RECORD";
            case 2257:
                return "GREEN_RECORD";
            case 2258:
                return "RECORD_3";
            case 2259:
                return "RECORD_4";
            case 2260:
                return "RECORD_5";
            case 2261:
                return "RECORD_6";
            case 2262:
                return "RECORD_7";
            case 2263:
                return "RECORD_8";
            case 2264:
                return "RECORD_9";
            case 2265:
                return "RECORD_10";
            case 2266:
                return "RECORD_11";
            case 2267:
                return "RECORD_12";
        }
        return "AIR";
    }
}
