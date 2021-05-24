package demoWithRobot

import it.unibo.actor0.ActorBasicKotlin
import it.unibo.actor0.ApplMessage
import it.unibo.actor0.sysUtil
import it.unibo.robotService.ApplMsgs
import it.unibo.robotService.BasicStepRobotActor
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class BasicStepRobotCaller(name: String ) : ActorBasicKotlin( name ) {

    private val robot = BasicStepRobotActor("stepRobot", ownerActor=this, scope, "localhost")
    private val obs   = NaiveObserverActorKotlin("obs", scope )

     fun doStep() {

       // robot.registerActor(obs)




       // robot.send(ApplMsgs.stepRobot_l("main" ))
       // robot.send(ApplMsgs.stepRobot_r("main" ))
        robot.send(ApplMsgs.forwardMsg)
/*
        robot.send(ApplMsgs.stepRobot_l("main" ))
        robot.send(ApplMsgs.stepRobot_l("main" ))
        robot.send(ApplMsgs.stepRobot_step("main", "350"))
        robot.send(ApplMsgs.stepRobot_l("main" ))
        robot.send(ApplMsgs.stepRobot_l("main" ))
*/
    }
    fun doLeft(){
        robot.send(ApplMsgs.turnLeftMsg)

    }


    override suspend fun handleInput(msg: ApplMessage) {
        println("$name | handleInput $msg")
        if (msg.msgId == "start") {
            doStep()
        }
        if (msg.msgId == "stepAnswer") {
            val answerJson = JSONObject(msg.msgContent)
            if (answerJson.has("stepDone")) {
                //robot.send(ApplMsgs.stepRobot_l("main"))
            }else if (answerJson.has("stepFail")){
                val tback = answerJson.getString("stepFail")
                println("$name | handleInput stepFail tback=$tback")
                //robot.send(ApplMsgs.stepRobot_s("main", tback))
            }
        }
        if (msg.msgId == "endmove") {
            val answerJson = JSONObject(msg.msgContent)  //.replace("@", ","))
            if (answerJson.has("endmove")) {//&& answerJson.getString("endmove") == "notallowed"){
                val endmove = answerJson.getString("endmove")
                val move = answerJson.getString("move")
                println("endmove=${endmove} move=$move")
                robot.send(ApplMsgs.forwardMsg)

            }
        }
    }
}

fun main( ) {
    println("BEGINS CPU=${sysUtil.cpus} ${sysUtil.curThread()}")
    runBlocking {
        val rc = BasicStepRobotCaller("rc")

        println("main | ${ApplMsgs.forwardMsg}")
        //println("main | ${ApplMsgs.for("main", "350" )}")

        val startMsg = ApplMsgs.startAny("main")
        println("main | $startMsg")

        //rc.send( startMsg )

        rc.send("start")
        println("ENDS runBlocking ${sysUtil.curThread()}")
    }
    println("ENDS main ${sysUtil.curThread()}")
}
