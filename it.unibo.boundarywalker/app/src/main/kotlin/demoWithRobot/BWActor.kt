package demoWithRobot


import it.unibo.supports2021.ActorBasicJava
import demoWithRobot.BasicStepRobotCaller
import it.unibo.actor0.ActorBasicContextKb.name
import it.unibo.actor0.ActorBasicKotlin
import it.unibo.actor0.ApplMessage
import it.unibo.actor0.sysUtil
import it.unibo.interaction.IUniboActor
import it.unibo.supports2021.IssWsHttpJavaSupport
import it.unibo.boundarywalker.RobotMovesInfo
import it.unibo.robotService.ApplMsgs
import it.unibo.robotService.BasicStepRobotActor
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class BWActor (myname: String?) : ActorBasicKotlin(name) {
    val forwardMsg = "{\"robotmove\":\"moveForward\", \"time\": 350}"
    val backwardMsg = "{\"robotmove\":\"moveBackward\", \"time\": 350}"
    val turnLeftMsg = "{\"robotmove\":\"turnLeft\", \"time\": 300}"
    val turnRightMsg = "{\"robotmove\":\"turnRight\", \"time\": 300}"
    val haltMsg = "{\"robotmove\":\"alarm\", \"time\": 100}"


    //PathExecutor pathExecutor= new PathExecutor()
   // var robotCaller = BasicStepRobotCaller("robot")
    private val robot = BasicStepRobotActor("stepRobot", ownerActor=this, scope, "localhost")
    private val obs   = NaiveObserverActorKotlin("obs", scope )

    override fun registerActor(iUniboActor: IUniboActor) {
        registerActor(this)
    }

    override fun removeActor(iUniboActor: IUniboActor) {
        removeActor(this)
    }

    private enum class State {
        start, walking, obstacle, end
    }

    private val support: IssWsHttpJavaSupport? = null
    private var curState: State = State.start
    private var stepNum = 1
    private var moves = RobotMovesInfo(true)
    private var fail =0;

    /*
//Removed since we want use just the fsm, without any 'external' code
    public void reset(){
        System.out.println("RobotBoundaryLogic | FINAL MAP:"  );
        moves.showRobotMovesRepresentation();
        stepNum        = 1;
        curState       =  State.start;
        moves.getMovesRepresentationAndClean();
        moves.showRobotMovesRepresentation();
    }
*/
    //------------------------------------------------
    protected fun doStep() {
        //support.forward( forwardMsg);
        println("BoundaryActor | doStep()")
       // robotCaller.doStep()
        robot.send(ApplMsgs.stepRobot_w("main", "350" ))
        //delay(1000); //to avoid too-rapid movement
    }

    protected fun turnLeft() {
        //  support.forward( turnLeftMsg );
       // robotCaller.doLeft()
        //delay(500) //to avoid too-rapid movement
        robot.send(ApplMsgs.stepRobot_l("main" ))
    }

    protected fun turnRight() {
//        support.forward( turnRightMsg );
        //delay(500) //to avoid too-rapid movement
        robot.send(ApplMsgs.stepRobot_r("main"))

    }

 /*    fun fsm(move: String, endmove: String) {
        println("${myname()} | fsm state=$curState stepNum=$stepNum move=$move endmove=$endmove")
        when (curState) {
            State.start -> {
                moves.showRobotMovesRepresentation()
                doStep()
                curState = State.walking
            }
           State.walking -> {


                if (move == "moveForward" && endmove == "true") {
                    //curState = State.walk;
                    moves.updateMovesRep("w")
                    doStep()
                } else if (move == "moveForward" && endmove == "false") {
                    curState = State.obstacle
                    turnLeft()
                } else {
                    println("IGNORE answer of turnLeft")
                }
            } //walk
            State.obstacle -> if (move == "turnLeft" && endmove == "true") {
                if (stepNum < 4) {
                    stepNum++
                    moves.updateMovesRep("l")
                    moves.showRobotMovesRepresentation()
                    curState = State.walking
                    doStep()
                } else {  //at home again
                    curState = State.end
                    turnLeft() //to force state transition
                }
            }
            State.end -> {
                if (move == "turnLeft") {
                    println("BOUNDARY WALK END")
                    moves.showRobotMovesRepresentation()
                    turnRight() //to compensate last turnLeft
                } else {
                    stepNum = 1
                    curState = State.start
                    moves = RobotMovesInfo(true)
                }
            }
            else -> {
                println("error - curState = $curState")
            }
        }
    }
*/
    override suspend  fun  handleInput(msg: ApplMessage) {     //called when a msg is in the queue
        //System.out.println( name + " | input=" + msgJsonStr);

          println("$name | handleInput $msg")
            if (msg.msgId == "start") {
                //fsm("","")
                doStep()
            }
            if (msg.msgId == "stepAnswer") {
                val answerJson = JSONObject(msg.msgContent)
                if (answerJson.has("stepDone")) {
                    //robot.send(ApplMsgs.stepRobot_l("main"))
                    doStep()
                }else if (answerJson.has("stepFail")) {
                      val tback = answerJson.getString("stepFail")
                   // ApplMsgs.stepRobot_step("main", "$tback" )
                        ApplMsgs.turnLeftMsg

                    /* if (stepNum < 4) {
                        robot.send(ApplMsgs.stepRobot_l("main"))

                        println("$name | handleInput stepFail left=$stepNum")
                    }
                    */
                }
                if(answerJson.has("collision")){
                    turnLeft()
                }
            }
            if (msg.msgId == "endmove") {
                val answerJson = JSONObject(msg.msgContent)  //.replace("@", ","))
                if (answerJson.has("endmove")) {//&& answerJson.getString("endmove") == "notallowed"){
                    val endmove = answerJson.getString("endmove")
                    val move = answerJson.getString("move")
                    if(move=="moveForward" && endmove==" false" ){
                        //ApplMsgs.turnLeftMsg
                            turnLeft()
                    }else if(move == "moveForward" && endmove== "true"){
                        doStep()
                    } else  if (move == "turnLeft" && endmove == "true") {
                        if (stepNum < 4) {
                            stepNum++
                            moves.updateMovesRep("l")
                            moves.showRobotMovesRepresentation()
                            curState = State.walking
                            doStep()
                        } else {  //at home again
                            println("BOUNDARY WALK END")
                            moves.showRobotMovesRepresentation() //to force state transition
                        }

                    }
                }
            }
    }
        //called when a msg is in the queue
        //System.out.println( name + " | input=" + msgJsonStr);
        /*  override suspend  fun  handleInput(msg: ApplMessage) {
              if (msg.msgId == "start") {
                  fsm("", "")
                  println("start")
              }

              else {
                  if(msg.msgId == "supportInfo") {
                      val answerJson = JSONObject(msg.msgContent)
                      msgDriven(answerJson)
                  }
              }
          }
        }*/


    fun handleCollision(collisioninfo: JSONObject?) {
        //we should handle a collision  when there are moving obstacles
        //in this case we could have a collision even if the robot does not move
        //String move   = (String) collisioninfo.get("move");
        //System.out.println("RobotApplication | handleCollision move=" + move  );
    }

    fun handleRobotCmd(robotCmd: JSONObject) {
        val cmd = robotCmd["robotcmd"] as String
        println("====================================================")
        println("RobotApplication | handleRobotCmd cmd=$cmd")
        println("====================================================")
    }
    fun handleSonar(sonarinfo: JSONObject) {
        val sonarname = sonarinfo["sonarName"] as String
        val distance = sonarinfo["distance"] as Int
        //System.out.println("RobotApplication | handleSonar:" + sonarname + " distance=" + distance);
    }

     fun msgDriven(infoJson: JSONObject) {
        if (infoJson.has("endmove"))
            //fsm(infoJson.getString("move"), infoJson.getString("endmove")) else if (infoJson.has("sonarName"))
            handleSonar(infoJson)
        else if (infoJson.has("collision"))
            handleCollision(infoJson) else {
                if (infoJson.has("robotcmd"))
                    handleRobotCmd(infoJson)
    }





}
}

fun main( ) {
    println("BEGINS CPU=${sysUtil.cpus} ${sysUtil.curThread()}")
    runBlocking {
        val rc = BWActor("rc")

        println("main | ${ApplMsgs.stepRobot_l("main" )}")
        println("main | ${ApplMsgs.stepRobot_step("main", "350" )}")

        val startMsg = ApplMsgs.startAny("main")
        println("main | $startMsg")
       // rc.send("startApp")

        rc.send( startMsg )

        println("ENDS runBlocking ${sysUtil.curThread()}")
    }
    println("ENDS main ${sysUtil.curThread()}")
}
