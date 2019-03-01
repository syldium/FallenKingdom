###Api Fk
Le plugin est doté d'un évent spigot : GameEvent

Vous pouvez untiliser ce dernier comme un évent classique :

```java
	@EventHandler
	public void onFkEvent(GameEvent event){
		event.getDay(); //retourne un int indiquant le jour actuel
		event.getType(); //retourne le type d'évenement
	}
```

Les types d'event sonts : 
* NEWDAY
* PVPENABLED
* TNTENABLED
* NETHERENABLED
* ENDENABLED
* PAUSEEVENT
* RESUMEEVENT
* STARTEVENT

Et viennent de l'Enum "GameEvent.Type"