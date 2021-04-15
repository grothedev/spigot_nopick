package grothedev.mcserverplugin.coolplugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.rmi.server.Skeleton;
import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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

    @Override
    public void onEnable(){
        super.onEnable();
        saveDefaultConfig();
        bannedItems = new HashSet<Material>();
        conf = getConfig();
        updateLocalConfig(conf);
        getServer().getPluginManager().registerEvents(this, this);
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
        
        if (e.getEntity() instanceof Player && e.getCause() == DamageCause.ENTITY_EXPLOSION){
            e.setDamage(e.getDamage()*.05f); //TODO cfg val
            
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

    private boolean isItemBanned(Item item){
        return (item.getItemStack().getType().toString().toLowerCase().contains("sapling") 
                || bannedItems.contains(item.getItemStack())); 
    }

    private void updateLocalConfig(Configuration c){

    }
}
