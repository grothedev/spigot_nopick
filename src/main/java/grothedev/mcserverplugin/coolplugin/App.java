package grothedev.mcserverplugin.coolplugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.rmi.server.Skeleton;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.Recipe;
import org.bukkit.material.Sapling;

/**
 * Hello world!
 *
 */
public class App extends JavaPlugin implements Listener
{

    Configuration conf;
    Random rand;
    enum Side{
        RIGHT, TOP, LEFT, BOTTOM,
        RIGHT_TOP, RIGHT_BOTTOM, LEFT_TOP, LEFT_BOTTOM
    };

    @Override
    public void onEnable(){
        super.onEnable();
        saveDefaultConfig();
        conf = getConfig();
        updateLocalConfig(conf);
        Config.init();
        getServer().getPluginManager().registerEvents(this, this);
        rand = new Random();
        this.getCommand("gm").setExecutor(new CommandHandler());
    }

    @Override
    public void onDisable(){
        getLogger().info("plugin disabled");
    }

    @EventHandler
    public void onEntityPickupItemEvent(EntityPickupItemEvent e){
            //LivingEntity entity, Item item, int remaining){
        if (e.getEntityType() == EntityType.PLAYER && 
            isItemBanned(e.getItem().getItemStack().getType())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemSpawnEvent(ItemSpawnEvent e){
        if (e.getEntityType() == EntityType.DROPPED_ITEM && isItemBanned(e.getEntity().getItemStack().getType())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCraftItemEvent(CraftItemEvent e){
        //Recipe r, InventoryView invView, InventoryType.SlotType type, int slot, ClickType c, InventoryAction a){
        if (Config.BANNED_RECIPES.contains(e.getRecipe().getResult().getType())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent e){
        //LivingEntity entity, CreatureSpawnEvent.SpawnReason reason){
        
        switch (e.getEntityType()){
            case SKELETON:
                AttributeInstance mh = e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH);
                mh.setBaseValue(mh.getBaseValue() * .65f); //TODO cfg val
                break;
            case CREEPER:
                ( (Creeper) e.getEntity() ).setExplosionRadius(10); 
                break;
            default:
                break;

        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e){
        //Entity damagee, EntityDamageEvent.DamageCause cause, double damage){
        
        if (e.getEntity() instanceof Player && e.getCause() == DamageCause.ENTITY_EXPLOSION){
            e.setDamage(e.getDamage()*.05f); //TODO cfg val
        } else if (e.getDamager().getType() == EntityType.SNOWBALL && e.getEntityType() == EntityType.CREEPER){
            Creeper c = ((Creeper)e.getEntity());
            if (c.getFuseTicks() <= 0){
                c.ignite();
                c.setMaxFuseTicks((int)(c.getMaxFuseTicks()*Config.CREEPER_FUSETIME));
            } else {
                if (c.isPowered()){
                    c.setMaxFuseTicks((int)(c.getMaxFuseTicks()*Config.CREEPER_FUSETIME*.75f));
                } else {
                    c.setPowered(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerFishEvent(PlayerFishEvent e){
        //Player player, Entity entity, FishHook hookEntity, PlayerFishEvent.State state){
        if (e.getState() == PlayerFishEvent.State.CAUGHT_ENTITY && e.getCaught().getType() == EntityType.CREEPER) {
            Creeper c = ((Creeper)e.getCaught());
            if (c.getFuseTicks() <= 0){
                c.ignite();
                c.setMaxFuseTicks(c.getMaxFuseTicks()*3); //TODO cfg value
            } else {
                if (c.isPowered()){
                    //? increaes fuse time or instant explode here?
                    c.explode();
                } else {
                    c.setPowered(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        String msg = conf.getString("welcome_msg");
        if (msg.length() > 0) p.sendMessage(msg);
    }

    @EventHandler
    public void onChunkLoadEvent(ChunkLoadEvent e){
        //chunk coords are chunk-based, not block-based
        if (e.isNewChunk()){
           // getServer().broadcastMessage(e.getChunk().getX() + ", " + e.getChunk().getZ());
            Side side = chunkContainsWall(e.getChunk(), 1000);
            if (side != null){
                Chunk ch = e.getChunk();
                int w = 0; //coord, either x or z
                switch (side){
                    case RIGHT:
                    case BOTTOM:
                        w = 1000%16;
                        break;
                    case TOP:
                    case LEFT:
                        w = 16-(1000%16);
                        break;
                    default:
                        w = 1000%16;
                        break;
                }
                for (int i = 0; i < 5; i++){
                    w += i;
                    for (int v = 0; v < 16; v++){
                        for (int y = 0; y < 256; y++){
                            switch (side){
                                case RIGHT:
                                case LEFT:
                                    ch.getBlock(w, y, v).setType(getRandomStoneBlockMaterial());
                                    break;
                                case TOP:
                                case BOTTOM:
                                    ch.getBlock(v, y, w).setType(getRandomStoneBlockMaterial());
                                    break;
                                case LEFT_BOTTOM:
                                    ch.getBlock(16-w, y, v).setType(getRandomStoneBlockMaterial());
                                    ch.getBlock(16-v, y, w).setType(getRandomStoneBlockMaterial());
                                    break;
                                case RIGHT_BOTTOM:
                                    ch.getBlock(w, y, v).setType(getRandomStoneBlockMaterial());
                                    ch.getBlock(v, y, w).setType(getRandomStoneBlockMaterial());
                                    break;
                                case LEFT_TOP:
                                    ch.getBlock(16-w, y, 16-v).setType(getRandomStoneBlockMaterial());
                                    ch.getBlock(16-v, y, 16-w).setType(getRandomStoneBlockMaterial());
                                    break;
                                case RIGHT_TOP:
                                    ch.getBlock(w, y, 16-v).setType(getRandomStoneBlockMaterial());
                                    ch.getBlock(v, y, 16-w).setType(getRandomStoneBlockMaterial());
                                    break;
                            }
                        }
                    }
                }            
            }
        }
    }
    


    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e){
        if (e.getBlock().getType() == Material.GRASS_BLOCK || e.getBlock().getType() == Material.DIRT){
            e.setDropItems(rand.nextInt(100) < Config.DIRT_DROP_CHANCE*100);
        }
    }

    @EventHandler
    public void onWorldInitEvent(WorldInitEvent e){
        //NOTE: this isn't working i think because the plugin is loaded after the world
        //getServer().broadcastMessage("WORLD INIT");
        //World w = e.getWorld();
        //generateSquareBorderWall(1000, 6, w);
    }

    private Material getRandomStoneBlockMaterial(){
        
        return Config.WALL_BLOCK_TYPES_0[rand.nextInt(Config.WALL_BLOCK_TYPES_0.length-1)];
    }

    //TODO pass in a function that returns block type based on iteration# or location
    private void generateSquareBorderWall(int radius, int thick, World w){
        Material blockMaterial = getRandomStoneBlockMaterial();
        for (int x = -(radius+thick); x <= (radius+thick); x++){ //top
            for (int z = radius; z <= (radius+thick); z++){
                for (int y = 1; y < 256; y++){
                    w.getBlockAt(x, y, z).setType(blockMaterial);
                }
            }
        }
        for (int x = -(radius+thick); x <= -radius; x++){ //left
            for (int z = -(radius+thick); z <= (radius+thick); z++){
                for (int y = 1; y < 256; y++){
                    w.getBlockAt(x, y, z).setType(blockMaterial);
                }
            }
        }
        for (int x = -(radius+thick); x <= (radius+thick); x++){ //bottom
            for (int z = -(radius+thick); z <= -radius; z++){
                for (int y = 1; y < 256; y++){
                    w.getBlockAt(x, y, z).setType(blockMaterial);
                }
            }
        }
        for (int x = radius; x <= (radius+thick); x++){ //right
            for (int z = -(radius+thick); z <= (radius+thick); z++){
                for (int y = 1; y < 256; y++){
                    w.getBlockAt(x, y, z).setType(blockMaterial);
                }
            }
        }
    }

    private boolean isItemBanned(Material item){
        //return (item.getItemStack().getType().toString().toLowerCase().contains("sapling") 
        return (Config.BANNED_ITEMS.contains(item)
                || item.toString().toLowerCase().contains("sapling")); 
    }

    private void updateLocalConfig(Configuration c){
        Config.CREEPER_BLAST = Float.valueOf(getConfig().getString("creeper_blast_radius_multiplier"));
        Config.DIRT_DROP_CHANCE = Float.valueOf(getConfig().getString("dirt_drop_chance"));
        Config.SKELE_HP_MULT = Float.valueOf(getConfig().getString("skele_hp_multiplier"));
        Config.CREEPER_FUSETIME = Float.valueOf(getConfig().getString("creeper_fusetime_multiplier"));
    }

    private Side chunkContainsWall(Chunk ch, int radius){
        if (ch.getX() == radius/16 && ch.getZ() == -(radius/16 - 1)){
            return Side.RIGHT_TOP;
        }
        if (ch.getX() == radius/16 && ch.getZ() == radius/16){
            return Side.RIGHT_BOTTOM;
        }
        if (ch.getX() == -(radius/16 - 1) && ch.getZ() == -(radius/16 - 1)){
            return Side.LEFT_TOP;
        }
        if (ch.getX() == -(radius/16 - 1) && ch.getZ() == radius/16){
            return Side.LEFT_BOTTOM;
        }
        if (ch.getX() == radius/16 && ch.getZ() <= radius/16 && ch.getZ() >= -radius/16){
            return Side.RIGHT;
        }
        if (ch.getX() == -radius/16 && ch.getZ() <= radius/16 && ch.getZ() >= -radius/16){
            return Side.LEFT;
        }
        //damn video games and their reverse coordinates
        if (ch.getZ() == -radius/16 && ch.getX() <= radius/16 && ch.getX() >= -radius/16){
            return Side.TOP;
        }
        if (ch.getZ() == radius/16 && ch.getX() <= radius/16 && ch.getX() >= -radius/16){
            return Side.BOTTOM;
        }
        return null;
    }

    private void generateBorder(){
        
    }
}
