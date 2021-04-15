PlayerPickupItemEvent: item is blocked -> getPlayer().removeFromInv()

CraftItemEvent: item blocked -> setCancelled()

CreatureSpawnEvent: 
    getType() == skeleton -> setHealth()  ~2/3
    -----     creeper -> setExplosionRadius()  range 2-4

EntityDamageEvent: cause()==Entity_Explosion&&?  -> setDamage() ~1/2

PlayerFishEvent: getCaught()==Creeper -> explode()

make config file with blocked items

ideas:
  - creeper explode on fishing pole hook
  - spawn chests of blocks
  - concentric world borders per 1k