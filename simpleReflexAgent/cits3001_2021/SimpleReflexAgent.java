package cits3001_2021;

import java.util.PriorityQueue;

/**
 * A basic Java agent to play in Resistance.
 * @author Bryce O'Connor
 * **/

public class SimpleReflexAgent implements Agent{

	private String name;
	private static int agentCount;

	private int roundNum;
	private int missionsFailed;
	private int numPlayers;
	private int numSpies;
	private boolean isSpy;
	private int myIndex;

	private int[] safetyLevel;

	private boolean prevVoteFailed;
	private int[] prevMission;
	private boolean[] prevVotes;
	private int prevLeader;

	// ONLY USED WHEN PLAYING AS A SPY
	private Random random;
	private boolean[] spiesBool;
	private int[] spyList;

	public SimpleReflexAgent(int agentCount){
		random = new Random();
		this.name = "simpleReflex" + agentCount;
	}

	public SimpleReflexAgent(String name){
		random = new Random();
		this.name = name;
	}

	/**
	 * returns an instance of this agent for testing.
	 * The progam should allocate the agent's name, 
	 * and can use a counter to ensure no two agents have the same name.
	 * @return an instance of the agent, with the given name.
	 * **/
	public static Agent init(){
		return new SimpleReflexAgent(agentCount++);
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
		int[] spyNum = {2,2,3,3,3,4}; //spyNum[n-5] is the number of spies in an n player game

		roundNum = 1;
		missionsFailed = 0;
		myIndex = playerIndex;
		this.numPlayers = numPlayers;
		numSpies = spyNum[numPlayers - 5];

		if (spies.length == 0) isSpy = false;
		else                   isSpy = true;

		if (isSpy){
			spyList = spies.clone();

			spiesBool = new boolean[numPlayers];
			for (int i = 0; i < numPlayers; i++){
				spiesBool[i] = false;
			}

			for (int i = 0; i < spyList.length; i++){
				spiesBool[spyList[i]] = true;
			}
		}

		safetyLevel = new int[numPlayers];
		for (int i = 0; i < numPlayers; i++){
			safetyLevel[i] = 0;
		}

		if (getName().equals("simpleReflex0")) printSafetyLevels();
	}

	/**
	 * This method is called when the agent is required to lead (propose) a mission
	 * @param teamsize the number of agents to go on the mission
	 * @param failsRequired the number of agent fails required for the mission to fail
	 * @return an array of player indexes, the proposed mission.
	 * **/
	public int[] proposeMission(int teamsize, int failsRequired){
		int[] team; 

		if (!isSpy) team = chooseBestTeam(teamsize);
		else        team = chooseBestSpyTeam(teamsize);

		return team;
	}

	/**
	 * This method is called when an agent is required to vote on whether a mission should proceed
	 * @param mission the array of agent indexes who will be going on the mission.
	 * @param leader the index of the agent who proposed the mission.
	 * @return true is this agent votes that the mission should go ahead, false otherwise.
	 * **/
	public boolean vote(int[] mission, int leader){
		if (getName().equals("simpleReflex0")) printSafetyLevels();

		if (!isSpy){
			int[] sortedAgents = sortAgents();

			// nonspies should have increasingly accurate knowledge as rounds go on.
			// hence they should start approving missions by default, 
			// and become more reticent to approve with each passing round.
			// i implement this by making nonspies choose the most likely set of spies,
			// and voting for the mission iff less than n of them are on the team,
			// where n reduces by 1 each round up to the final round, where it is 1.
			int[] suspectedSpies = new int[numSpies];

			int chosen = 0;
			for (int i = 0; i < numPlayers; i++){
				if (chosen == numSpies) break;

				if (sortedAgents[i] != myIndex) {
					suspectedSpies[chosen] = sortedAgents[i];
					chosen++;
				}
			}

			int dangerThreshold = 6 - roundNum;
			int suspectedSpiesOnMission = 0;

			for (int i = 0; i < numSpies; i++){
				for (int j = 0; j < mission.length; j++){
					if (suspectedSpies[i] == mission[j]) suspectedSpiesOnMission++;
				}
			}

			if (suspectedSpiesOnMission >= dangerThreshold) return false;
			else return true;
		}
		else {
			int spiesOnTeam = spiesOnTeam(mission);

			if (spiesOnTeam == 1) return true;
			else                  return false;
		}
	}

	/**
	 * The method is called on an agent to inform them of the outcome of a vote, 
	 * and which agent voted for or against the mission.
	 * @param mission the array of agent indexes represent the mission team
	 * @param leader the agent index of the leader, who proposed the mission
	 * @param votes an array of booleans such that votes[i] is true if and only if agent i voted for the mission to go ahead.
	 * **/
	public void voteOutcome(int[] mission, int leader, boolean[] votes){
		if (prevVoteFailed) {
			// do some calculations
		}

		prevMission = mission;
		prevLeader = leader;
		prevVotes = votes;
	}

	/**
	* This method is called on an agent who has a choice to betray (fail) the mission
	* @param mission the array of agent indexes representing the mission team
	* @param leader the agent who proposed the mission
	* @return true is the agent choses to betray (fail) the mission
	* **/
	public boolean betray(int[] mission, int leader){
		//TODO
		int spiesOnTeam = spiesOnTeam(mission);
		int failsRequired = failsRequired();

		//if there are too few spies on the mission to make it fail, none of them should fail it
		if (spiesOnTeam < failsRequired) return false;

		// if EVERY mission from now on must be failed:
		if ((6 - roundNum) == (3 - missionsFailed)) {
			if (spiesOnTeam == 1) return true;
			
			if (failsRequired == 1){
				if (spiesOnTeam == 2) {
					if (random.nextInt(5) < 4) return true;
					else return false;
				}
				else if (spiesOnTeam == 3) {
					if (random.nextInt(5) < 3) return true;
					else return false;
				}
				else if (spiesOnTeam == 4) {
					if (random.nextInt(2) < 1) return true;
					else return false;
				}
			}
			else if (failsRequired == 2){
				if (spiesOnTeam == 2) return true;
				
				else if (spiesOnTeam == 3) {
					if (random.nextInt(6) < 5) return true;
					else return false;
				}
				else if (spiesOnTeam == 4) {
					if (random.nextInt(5) < 4) return true;
					else return false;
				}
			}
		}
		else {
			if (failsRequired == 1){
				if (missionsFailed == 2) return true;
				else if (missionsFailed == 1){
					if (spiesOnTeam == 1){
						if (random.nextInt(5) < 4) return true;
						else return false;
					else if (spiesOnTeam == 2){
						if (random.nextInt(5) < 2) return true;
						else return false;
					}
					else if (spiesOnTeam >= 3) return false;
				}
				else if (missionsFailed == 0){
					if (spiesOnTeam == 1){
						if (random.nextInt(5) < 3) return true;
						else return false;
					else if (spiesOnTeam == 2){
						if (random.nextInt(5) < 1) return true;
						else return false;
					}
					else if (spiesOnTeam == 3){
						if (random.nextInt(10) < 1) return true;
						else return false;
					}
				}
			}
			else if (failsRequired == 2){
				if (missionsFailed == 2) return true;
				// one mission failed is covered by 'every following round must be failed' contingency
				// if no missions are failed and you're on the fourth mission, spies have already lost!
				else return false;
				}
			}
		}
	}

	/**
	* Informs all agents of the outcome of the mission, including the number of agents who failed the mission.
	* @param mission the array of agent indexes representing the mission team
	* @param leader the agent who proposed the mission
	* @param numFails the number of agent's who failed the mission
	* @param missionSuccess true if and only if the mission succeeded.
	* **/
	public void missionOutcome(int[] mission, int leader, int numFails, boolean missionSuccess){
		int failsRequired = failsRequired();

		if (numFails == 0) safetyLevel[leader]++;
		else if (failsRequired == 1 && numFails == 1) safetyLevel[leader]--;
		//else if (failsRequired == 1 && numFails == 2) safetyLevel[leader] stays the same
		//else if (failsRequired == 2 && numFails == 1) safetyLevel[leader] stays the same
		else if (failsRequired == 2 && numFails == 2) safetyLevel[leader] -= 2;

		if (numFails > 0){
			// if there was at least one fail,
			// reduce all team members' safety score by 3 x the number of fails
			// FUTURE IMPLEMENTATION: pick a larger multiplier than 3 and divide by the size of the team
			for (int i = 0; i < mission.length; i++){
				safetyLevel[mission[i]] -= 3 * numFails;
			}
		}
		else {
			// if there were no fails,
			// increase all team members' safety score by 1
			for (int i = 0; i < mission.length; i++){
				safetyLevel[mission[i]]++;
			}
		}
	}   

	/**
	* Informs all agents of the game state at the end of the round
	* @param roundsComplete the number of rounds played so far
	* @param roundsLost the number of rounds lost so far
	* **/
	public void roundOutcome(int roundsComplete, int roundsLost){
		//TODO
		roundNum = roundsComplete + 1;
		System.out.println(roundNum);
		missionsFailed = roundsLost;
	} 

	/**
	* Informs all agents of the outcome of the game, including the identity of the spies.
	* @param roundsLost the number of rounds the Resistance lost
	* @param spies an array with the indexes of all the spies in the game.
	* **/
	public void gameOutcome(int roundsLost, int[] spies){
		//TODO
	}

	/**
	 * This method chooses the agents with the highest safety score for the mission.
	 * @param teamsize the number of agents to go on the mission
	 * @return an array of player indexes, the proposed mission.
	 * **/
	private int[] chooseBestTeam(int teamsize) {
		int[] team = new int[teamsize];

		PriorityQueue<int[]> safetyLevelPQ = new PriorityQueue<int[]>((a, b) -> b[1] - a[1]);

		for (int i = 0; i < numPlayers; i++){
			safetyLevelPQ.add(new int[] {i, safetyLevel[i]});
		}

		for (int i = 0; i < teamsize; i++){
			team[i] = safetyLevelPQ.poll()[0];
		}

		return team;
	}

	/**
	 * This method chooses the agents for the mission,
	 * choosing the spy with the highest safety score,
	 * and filling the rest of the team with the nonspies with the highest safety score.
	 * @param teamsize the number of agents to go on the mission
	 * @return an array of player indexes, the proposed mission.
	 * **/
	private int[] chooseBestSpyTeam(int teamsize) {
		int[] team = new int[teamsize];

		PriorityQueue<int[]> nonspySafetyLevelPQ = new PriorityQueue<int[]>((a, b) -> b[1] - a[1]);
		PriorityQueue<int[]> spySafetyLevelPQ = new PriorityQueue<int[]>((a, b) -> b[1] - a[1]);

		for (int i = 0; i < numPlayers; i++){
			if (spiesBool[i]){
				spySafetyLevelPQ.add(new int[] {i, safetyLevel[i]});
			}
			else {
				nonspySafetyLevelPQ.add(new int[] {i, safetyLevel[i]});
			}

		}

		for (int i = 0; i < teamsize - 1; i++){
			team[i] = nonspySafetyLevelPQ.poll()[0];
		}
		team[teamsize - 1] = spySafetyLevelPQ.poll()[0];

		return team;
	}


	/**
	 * This method is called by spies to find out how many spies there are on the current mission.
	 * @param mission the current mission
	 * @return the number of spies on the current mission
	 */
	private int spiesOnTeam(int[] mission){
		int spiesOnTeam = 0;

		for (int i = 0; i < mission.length; i++){
			if (spiesBool[mission[i]]) spiesOnTeam++;
		}

		return spiesOnTeam;
	}

	/**
	 * This method returns the player indexes, sorted by their safety score (lowest to highest).
	 * @return the sorted array of player indexes.
	 * **/
	private int[] sortAgents() {
		int[] players = new int[numPlayers];

		PriorityQueue<int[]> safetyLevelPQ = new PriorityQueue<int[]>((a, b) -> a[1] - b[1]);

		for (int i = 0; i < numPlayers; i++){
			safetyLevelPQ.add(new int[] {i, safetyLevel[i]});
		}

		for (int i = 0; i < numPlayers; i++){
			players[i] = safetyLevelPQ.poll()[0];
		}

		return players;
	}

	private int failsRequired(){
		return (numPlayers>6 && roundNum==4)?2:1;
	}

	private void printSafetyLevels(){
		System.out.println("\n\nSafety levels:");
		for (int i = 0; i < numPlayers; i++){
			System.out.print(safetyLevel[i] + " ");
		}
		System.out.println("\n\n");
	}
}
