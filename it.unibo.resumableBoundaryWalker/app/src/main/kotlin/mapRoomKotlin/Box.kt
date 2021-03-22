package mapRoomKotlin

import java.io.Serializable

data class Box (
			var isObstacle : Boolean = false,
            var notExplored: Boolean = true,
            var isRobot    : Boolean = false){


}