import javax.naming.OperationNotSupportedException;

import java.util.*;
import java.io.*;

/**
 * A basic Java agent to play in Resistance.
 * @author Bryce O'Connor
 * **/

public class BasicAgent implements Agent{

  private String name;
  private static int agentCount;

  public BasicAgent(){
    this.name = "basic" + agentCount;
    agentCount++;
  }

  /**
   * returns an instance of this agent for testing.
   * The progam should allocate the agent's name, 
   * and can use a counter to ensure no two agents have the same name.
   * @return an instance of the agent, with the given name.
   * **/
  public static Agent init(){
    return new BasicAgent();
  }

  /**
   * gets the name of the agent
   * @return the agent's name.
   * **/
  public String getName(){
    return name;
  }

  /**
   * initialises a new game. 
   * The agent should drop their current gameState and reinitialise all their game variables.
   * @param numPlayers the number of players in the game.
   * @param playerIndex the players index in the game.
   * @param spies, the index of all the spies in the game, if this agent is a spy (i.e. playerIndex is an element of spies)
   * **/
  public void newGame(int numPlayers, int playerIndex, int[] spies){
    //TODO
  }

  /**
   * This method is called when the agent is required to lead (propose) a mission
   * @param teamSize the number of agents to go on the mission
   * @param failsRequired the number of agent fails required for the mission to fail
   * @return an array of player indexes, the proposed mission.
   * **/
  public int[] proposeMission(int teamsize, int failsRequired){
    //TODO
    int[] team = new int[teamsize];
    for (int i = 0; i < teamsize; i++){
      team[i] = i;
    }
    return team;
  }

  /**
   * This method is called when an agent is required to vote on whether a mission should proceed
   * @param mission the array of agent indexes who will be going on the mission.
   * @param leader the index of the agent who proposed the mission.
   * @return true is this agent votes that the mission should go ahead, false otherwise.
   * **/
  public boolean vote(int[] mission, int leader){
    //TODO
    return true;
  }

  /**
   * The method is called on an agent to inform them of the outcome of a vote, 
   * and which agent voted for or against the mission.
   * @param mission the array of agent indexes represent the mission team
   * @param leader the agent index of the leader, who proposed the mission
   * @param votes an array of booleans such that votes[i] is true if and only if agent i voted for the mission to go ahead.
   * **/
  public void voteOutcome(int[] mission, int leader, boolean[] votes){
    //TODO
  }

  /**
  * This method is called on an agent who has a choice to betray (fail) the mission
  * @param mission the array of agent indexes representing the mission team
  * @param leader the agent who proposed the mission
  * @return true is the agent choses to betray (fail) the mission
  * **/
  public boolean betray(int[] mission, int leader){
    //TODO
    return true;
  }

  /**
  * Informs all agents of the outcome of the mission, including the number of agents who failed the mission.
  * @param mission the array of agent indexes representing the mission team
  * @param leader the agent who proposed the mission
  * @param numFails the number of agent's who failed the mission
  * @param missionSuccess true if and only if the mission succeeded.
  * **/
  public void missionOutcome(int[] mission, int leader, int numFails, boolean missionSuccess){
    //TODO
  }   

  /**
  * Informs all agents of the game state at the end of the round
  * @param roundsComplete the number of rounds played so far
  * @param roundsLost the number of rounds lost so far
  * **/
  public void roundOutcome(int roundsComplete, int roundsLost){
    //TODO
  } 

  /**
  * Informs all agents of the outcome of the game, including the identity of the spies.
  * @param roundsLost the number of rounds the Resistance lost
  * @param spies an array with the indexes of all the spies in the game.
  * **/
  public void gameOutcome(int roundsLost, int[] spies){
    //TODO
  }

}
