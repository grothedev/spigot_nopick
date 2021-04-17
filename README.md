PlayerPickupItemEvent: item is blocked -> getPlayer().removeFromInv()

CraftItemEvent: item blocked -> setCancelled()

CreatureSpawnEvent: 
    getType() == skeleton -> setHealth()  ~2/3
    -----     creeper -> setExplosionRadius()  range 2-4

EntityDamageEvent: cause()==Entity_Explosion&&?  -> setDamage() ~1/2

make config file with blocked items

ChunkPopulateEvent or WorldInitEvent to generate world border

ideas:
  - creeper explode on snowball
  - spawn chests of blocks
  - shovel dirt has 20% chance of actually giving dirt


  -1006, 1000. 1006,1000. 1006,1006. -1006,1006. T
-1000,-1006. -1000,1006. -1006,1006. -1006,-1006.  L
1006,-1000. -1006,-1000. -1006,-1006. 1006,-1006.  B
1000,1006. 1000,-1006, 1006,-1006. 1006,1006. R

Are you looking for something to make minecraft a little more interesting or challenging? If so, then this may be the server for you. There are a number of modifications that make the survival gamemode require a little more thought and creativity. You cannot craft pickaxes; how will you mine? You cannot make charcoal or plant saplings. Dirt and sandstone will not always give you usable material when shoveled. Is it possible to control creepers in any way? At spawn, there is a big hole that goes to the bottom of the world. You can not use buckets. 