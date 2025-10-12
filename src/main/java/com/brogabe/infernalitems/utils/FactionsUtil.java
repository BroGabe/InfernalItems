package com.brogabe.infernalitems.utils;

import com.massivecraft.factions.*;
import com.massivecraft.factions.perms.Relation;
import org.bukkit.entity.Player;

public class FactionsUtil {

    public static boolean hasFaction(Player player) {
        return FPlayers.getInstance().getByPlayer(player).hasFaction();
    }

    public static boolean isInBaseRegion(Player player) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);

        FLocation fLocation = new FLocation(fPlayer);

        Faction factionAtLocation = Board.getInstance().getFactionAt(fLocation);

        if(factionAtLocation.isSystemFaction()) return false;

        return factionAtLocation.isInBaseRegion(fLocation);
    }

    public static boolean hasRelation(Player player1, Player player2) {
        if(!hasFaction(player1) || !hasFaction(player2)) return false;

        FPlayer fPlayer1 = FPlayers.getInstance().getByPlayer(player1);
        FPlayer fPlayer2 = FPlayers.getInstance().getByPlayer(player2);

        Faction faction1 = fPlayer1.getFaction();
        Faction faction2 = fPlayer2.getFaction();

        Relation relation = faction1.getRelationTo(faction2);

        return (relation.isAlly() || relation.isMember() || relation.isTruce());
    }

    public static boolean isEnemy(Player player1, Player player2) {
        if(!hasFaction(player1) || !hasFaction(player2)) return true;

        FPlayer fPlayer1 = FPlayers.getInstance().getByPlayer(player1);
        FPlayer fPlayer2 = FPlayers.getInstance().getByPlayer(player2);

        Faction faction1 = fPlayer1.getFaction();
        Faction faction2 = fPlayer2.getFaction();

        Relation relation = faction1.getRelationTo(faction2);

        return (!relation.isTruce() && !relation.isMember() && !relation.isAlly());
    }
}
