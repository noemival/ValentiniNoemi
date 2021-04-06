package it.unibo.CautiousExplorer;


import mapRoomKotlin.mapUtil;

public class RobotMovesInfo {
    private boolean  doMap = false;
    private String journey = "";//the journey is diferent from the resumable

    public RobotMovesInfo(boolean doMap){
        this.doMap = doMap;

    }
    public void showRobotMovesRepresentation(  ){
        if( doMap ) mapUtil.showMap();
        else System.out.println( "journey=" + journey );
    }

    public String getMovesRepAndClean(  ){
        if( doMap ) return mapUtil.getMapAndClean();
        else {
            String answer = journey;
            journey       = "";
            return answer;
        }
    }

    public String getMovesRep(  ){
        if( doMap ) return mapUtil.getMapRep();
        else return journey;
    }

    public void updateMovesRep(String move ){
        if( doMap )  mapUtil.doMove( move );
        else journey = journey + move;
    }


}