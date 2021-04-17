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
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
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
    HashSet<Material> bannedItems;
    Random rand;

    @Override
    public void onEnable(){
        super.onEnable();
        saveDefaultConfig();
        bannedItems = new HashSet<Material>();
        conf = getConfig();
        updateLocalConfig(conf);
        getServer().getPluginManager().registerEvents(this, this);
        rand = new Random();
    }

    @Override
    public void onDisable(){
        getLogger().info("plugin disabled");
    }

    @EventHandler
    public void onEntityPickupItemEvent(EntityPickupItemEvent e){
            //LivingEntity entity, Item item, int remaining){
        if (e.getEntityType() == EntityType.PLAYER && 
            isItemBanned(e.getItem())){
            //TODO remove item
        }
    }

    @EventHandler
    public void onCraftItemEvent(CraftItemEvent e){
        //Recipe r, InventoryView invView, InventoryType.SlotType type, int slot, ClickType c, InventoryAction a){
        if (bannedItems.contains(e.getRecipe().getResult().getType())){
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
        
        getServer().broadcastMessage(e.getEntity() + "<*" + e.getDamager());

        if (e.getEntity() instanceof Player && e.getCause() == DamageCause.ENTITY_EXPLOSION){
            e.setDamage(e.getDamage()*.05f); //TODO cfg val
        } else if (e.getDamager().getType() == EntityType.SNOWBALL && e.getEntityType() == EntityType.CREEPER){
            Creeper c = ((Creeper)e.getEntity());
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
    public void onChunkLoadEvent(ChunkLoadEvent e){
        //chunk coords are chunk-based, not block-based
        if (e.isNewChunk()){
            getServer().broadcastMessage(e.getChunk().getX() + ", " + e.getChunk().getZ());
            if (chunkContainsWall(e.getChunk())){
                //TODO
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
        World w = e.getWorld();
        generateSquareBorderWall(1000, 6, w);
        //world border generation
        /*
        Material blockMaterial = getRandomStoneBlockMaterial();
        for (int x = -1006; x <= 1006; x++){ //top
            for (int z = 1000; z <= 1006; z++){
                for (int y = 1; y < 256; y++){
                    w.getBlockAt(x, y, z).setType(blockMaterial);
                }
            }
        }
        for (int x = -1006; x <= -1000; x++){ //left
            for (int z = -1006; z <= 1006; z++){
                for (int y = 1; y < 256; y++){
                    w.getBlockAt(x, y, z).setType(blockMaterial);
                }
            }
        }
        for (int x = -1006; x <= 1006; x++){ //bottom
            for (int z = -1006; z <= -1000; z++){
                for (int y = 1; y < 256; y++){
                    w.getBlockAt(x, y, z).setType(blockMaterial);
                }
            }
        }
        for (int x = 1000; x <= 1006; x++){ //right
            for (int z = -1006; z <= 1006; z++){
                for (int y = 1; y < 256; y++){
                    w.getBlockAt(x, y, z).setType(blockMaterial);
                }
            }
        }*/
    }

    private Material getRandomStoneBlockMaterial(){
        Material[] types = {
            Material.CHISELED_POLISHED_BLACKSTONE,
            Material.CHISELED_STONE_BRICKS,
            Material.COBBLESTONE,
            Material.CRACKED_STONE_BRICKS,
            Material.CRACKED_POLISHED_BLACKSTONE_BRICKS,
            Material.MOSSY_COBBLESTONE,
            Material.MOSSY_STONE_BRICKS,
            Material.POLISHED_ANDESITE,
            Material.POLISHED_BLACKSTONE,
            Material.POLISHED_DIORITE,
            Material.POLISHED_BLACKSTONE_BRICKS,
            Material.POLISHED_GRANITE
        };
        return types[rand.nextInt() % types.length];
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

    private boolean isItemBanned(Item item){
        return (item.getItemStack().getType().toString().toLowerCase().contains("sapling") 
                || bannedItems.contains(item.getItemStack())); 
    }

    private void updateLocalConfig(Configuration c){
        Config.CREEPER_BLAST = Float.valueOf(getConfig().getString("creeper_blast_radius_multiplier"));
        Config.DIRT_DROP_CHANCE = Float.valueOf(getConfig().getString("dirt_drop_chance"));
        Config.SKELE_HP_MULT = Float.valueOf(getConfig().getString("skele_hp_multiplier"));
        Config.CREEPER_FUSETIME = Float.valueOf(getConfig().getString("creeper_fusetime_multiplier"));
    }

    private boolean chunkContainsWall(Chunk ch){
        return false;
    }
}
