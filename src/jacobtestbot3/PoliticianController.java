package jacobtestbot3;

import battlecode.common.*;
import communication.MarsNet.MarsNet;
import controllers.CustomPoliticianController;

import java.util.Map;

public class PoliticianController extends CustomPoliticianController<MessageType> {
    private MapLocation attackLocation;

    public PoliticianController(MarsNet<MessageType> marsNet) {
        super(marsNet);
    }

    // This cannot be moved into CustomPoliticianController, because it depends
    // on your actual implementation of PoliticianController, which is not
    // accessible from the abstract class. Keep it, but add to as necessary.
    public PoliticianController(SlandererController sc) {
        super(sc);
    }

    @Override
    public void doTurn() throws GameActionException {
        MapLocation me = getLocation();
        int inRadius = 0;
        for (RobotInfo robot : senseNearbyRobots(RobotType.POLITICIAN.sensorRadiusSquared, getTeam().opponent())) {
            if (robot.type == RobotType.ENLIGHTENMENT_CENTER) {
                marsNet.broadcastLocation(MessageType.FoundEnemyEC, robot.location);
                break;
            }
            if (me.isWithinDistanceSquared(robot.location, 9)) {
                inRadius++;
            }
        }
        if (inRadius >= 1 && canEmpower(9)) {
            empower(9);
            return;
        }

        attackLocation = marsNet.getAndHandleSafe(EC.ID, (p) -> {
            switch (p.mType) {
                case A_Zerg:
                case P_Zerg:
                    return p.asLocation();
                case A_StopZerg:
                case P_StopZerg:
                    return null;
            }
            return attackLocation;
        });

        if (attackLocation != null) {
            if (me.isWithinDistanceSquared(attackLocation, 8) && canEmpower(9)) {
                empower(9);
                return;
            }
            tryMoveToward(attackLocation);
        } else {
            tryMoveRandom();
        }
    }
}
