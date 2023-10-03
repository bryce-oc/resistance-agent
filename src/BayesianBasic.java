package cits3001_2021;

import java.util.*;
import java.io.*;
import java.util.PriorityQueue;

/**
 * A basic Java agent to play in Resistance.
 * @author Bryce O'Connor
 * **/

public class BayesianBasic implements Agent{

	private String name;
	private static int agentCount;

	private int roundNum;
	private int missionsFailed;
	private int numPlayers;
	private int numSpies;
	private boolean isSpy;
	private int myIndex;

	private World[] worlds;
	private double[][] missionOutcomeTable;

	private boolean bingus = false;

	// ONLY USED WHEN PLAYING AS A SPY
	private Random random;
	private boolean[] spiesBool;
	private int[] spyList;

	public BayesianBasic(int agentCount){
		random = new Random();
		this.name = "bayesianBasic" + agentCount;
	}

	public BayesianBasic(String name){
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
		return new BayesianBasic(agentCount++);
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

		int[][] spyWorlds = generatePlayerSubsets(0, new int[1][numSpies]);
		double baseWorldProbability = (double)1/(double)spyWorlds.length;

		worlds = new World[spyWorlds.length];
		for (int i = 0; i < spyWorlds.length; i++){
			worlds[i] = new World(spyWorlds[i], baseWorldProbability);
		}

		if (getName().equals("bayesianBasic0")){
			printWorldProbabilities();
			System.out.println("ASDFGHJKLLKJHGFDSASDFGHJKLKJHGFDSASDFGHJKLKJHGFDSA");
			System.out.println("arglebargle");
		}
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
		if (!isSpy){
			double successProb = 0;
			for (int i = 0; i < worlds.length; i++){
				int spiesOnTeam = spiesOnTeamGivenWorld(mission, i);
				if (spiesOnTeam == 0 || (spiesOnTeam == 1 && failsRequired() == 2)){
					successProb += worlds[i].probability;
				}
			}

			double thresholdProb = ((1 + 2 * roundNum) / 10);
			return (successProb > thresholdProb);
		}
		else {
			if (spiesOnTeam(mission) == failsRequired()) return true;
			else return false;
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
	}

	/**
	* This method is called on an agent who has a choice to betray (fail) the mission
	* @param mission the array of agent indexes representing the mission team
	* @param leader the agent who proposed the mission
	* @return true is the agent choses to betray (fail) the mission
	* **/
	public boolean betray(int[] mission, int leader){
		int spiesOnTeam = spiesOnTeam(mission);
		int failsRequired = failsRequired();

		//if there are too few spies on the mission to make it fail, none of them should fail it
		if (spiesOnTeam < failsRequired) return false;

		// if EVERY mission from now on must be failed:
		if ((6 - roundNum) == (3 - missionsFailed)) {			
			switch (failsRequired) {
				case 1:
					switch (spiesOnTeam) {
						case 1:
							return true;

						case 2:
							if (random.nextInt(5) < 4) return true;
							else return false;

						case 3:
							if (random.nextInt(5) < 3) return true;
							else return false;

						case 4:
							if (random.nextInt(2) < 1) return true;
							else return false;
					}

				case 2:
					switch (spiesOnTeam) {
						case 2:
							return true;

						case 3:
							if (random.nextInt(6) < 5) return true;
							else return false;

						case 4:
							if (random.nextInt(5) < 4) return true;
							else return false;
					}
			}
		}
		else {
			switch (failsRequired) {
				case 1:
					switch (missionsFailed){
						case 0:
							switch (spiesOnTeam) {
								case 1:
									if (random.nextInt(5) < 3) return true;
									else return false;

								case 2:
									if (random.nextInt(5) < 1) return true;
									else return false;

								case 3:
									if (random.nextInt(10) < 1) return true;
									else return false;

								case 4:
									if (random.nextInt(2) < 1) return true;
									else return false;
							}

						case 1:
							switch (spiesOnTeam) {
								case 1:
									if (random.nextInt(5) < 4) return true;
									else return false;

								case 2:
									if (random.nextInt(5) < 2) return true;
									else return false;

								case 3:
									return false;

								case 4:
									return false;
							}

						case 2:
							return true;
					}

				case 2:
					switch (missionsFailed){
						case 2:
							return true;

						// one mission failed is covered by 'every following round must be failed' contingency
						// if no missions are failed and you're on the fourth mission, spies have already lost!
						default:
							return false;
					}
			}
		}

		// this is here so the compiler knows the function will definitely return something;
		// however all cases should be caught by the switch cases above
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
		bingus = true;
		if (getName().equals("bayesianBasic0")){
			System.out.println("arglebargle");
			System.out.println("grunch");
			printWorldProbabilities();
		}

		int updatedWorldProbabilitySum = 0;
		for (int i = 0; i < worlds.length; i++){
			worlds[i].probability *= getProbMissionOutcomeGivenWorld(i, mission, numFails, missionSuccess);
			updatedWorldProbabilitySum += worlds[i].probability;
		}

		// divide each world probability by their sum to make them now sum to 1
		// they should sum to P(missionSuccess), so by dividing them by their sum we satisfy Bayes' Rule
		for (int i = 0; i < worlds.length; i++){
			worlds[i].probability /= updatedWorldProbabilitySum;
		}

		if (getName().equals("bayesianBasic0")) printWorldProbabilities();
	}   

	/**
	* Informs all agents of the game state at the end of the round
	* @param roundsComplete the number of rounds played so far
	* @param roundsLost the number of rounds lost so far
	* **/
	public void roundOutcome(int roundsComplete, int roundsLost){
		//TODO
		roundNum = roundsComplete + 1;
		missionsFailed = roundsLost;
	} 

	/**
	* Informs all agents of the outcome of the game, including the identity of the spies.
	* @param roundsLost the number of rounds the Resistance lost
	* @param spies an array with the indexes of all the spies in the game.
	* **/
	public void gameOutcome(int roundsLost, int[] spies){
		if (bingus) System.out.println("BINGUS, BABYYYYYYYYYYYY");
		else System.out.println("no bingus :(");
		//TODO
	}

	/**
	 * This method chooses the agents with the highest calculated odds of success for the mission.
	 * @param teamsize the number of agents to go on the mission
	 * @return an array of player indexes, the proposed mission.
	 * **/
	private int[] chooseBestTeam(int teamsize) {
		double[] agentSpyProbs = getAgentSpyProbs();

		// make a priority queue and put all agents in it
		PriorityQueue<Integer> spyProbPQ = new PriorityQueue<Integer>((a, b) -> (int)(1000*(agentSpyProbs[a] - agentSpyProbs[b])));
		for (int i = 0; i < numPlayers; i++){
			spyProbPQ.add(i);
		}

		int[] bestTeam = new int[teamsize];
		for (int i = 0; i < teamsize; i++){
			bestTeam[i] = spyProbPQ.poll();
		}

		return bestTeam;
	}

	/**
	 * This method chooses the agents for the mission,
	 * choosing the most trustworthy team that has exactly the right amount of spies on it to fail the mission.
	 * @param teamsize the number of agents to go on the mission
	 * @return an array of player indexes, the proposed mission.
	 * **/
	private int[] chooseBestSpyTeam(int teamsize) {
		double[] agentSpyProbs = getAgentSpyProbs();

		// make two priority queues, one for the spies and one for the nonspies
		PriorityQueue<Integer> nonspyProbPQ = new PriorityQueue<Integer>((a, b) -> (int)(1000*(agentSpyProbs[a] - agentSpyProbs[b])));
		PriorityQueue<Integer> spyProbPQ = new PriorityQueue<Integer>((a, b) -> (int)(1000*(agentSpyProbs[a] - agentSpyProbs[b])));
		for (int i = 0; i < numPlayers; i++){
			if (spiesBool[i]) spyProbPQ.add(i);
			else nonspyProbPQ.add(i);
		}

		int[] bestTeam = new int[teamsize];

		// if one fail is needed for the mission, put one spy on the team
		if (failsRequired() == 1){
			for (int i = 0; i < teamsize - 1; i++){
				bestTeam[i] = nonspyProbPQ.poll();
			}
			bestTeam[teamsize - 1] = spyProbPQ.poll();
		}
		// if two fails are needed for the mission, put two spies on the team
		else{
			for (int i = 0; i < teamsize - 2; i++){
				bestTeam[i] = nonspyProbPQ.poll();
			}
			bestTeam[teamsize - 2] = spyProbPQ.poll();
			bestTeam[teamsize - 1] = spyProbPQ.poll();
		}

		return bestTeam;
	}

	private double getProbMissionOutcomeGivenWorld(int worldIndex, int[] mission, int numFails, boolean missionSuccess){
		int spiesOnTeam = spiesOnTeamGivenWorld(mission, worldIndex);

		double failProbability;

		if (spiesOnTeam < failsRequired()) failProbability = 0;
		else {
			failProbability = 0.8;
		}

		if (missionSuccess) return (1 - failProbability);
		else return failProbability;
	}

	public double[] getAgentSpyProbs(){
		double[] agentSpyProbs = new double[numPlayers];
		for (int i = 0; i < numPlayers; i++){
			agentSpyProbs[i] = 0;
		}

		for (int i = 0; i < worlds.length; i++){
			for (int j = 0; j < numSpies; j++){
				int spy = worlds[i].spies[j];
				agentSpyProbs[spy] += worlds[i].probability/numSpies;
			}
		}

		return agentSpyProbs;
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

	private int failsRequired(){
		return (numPlayers>6 && roundNum==4)?2:1;
	}

	/**
	 * Generate all subsets of a certain size of the set of the player indexes.
	 * Used in finding all of the worlds of spies, and finding all possible missions.
	 * @param layer the current layer of the recursive function (when called, should start at 0)
	 * @param subsets the set of all subsets of size (layer - 1) of the set of the player indexes 
	 * (when called, should start as a array with 1 element which is an arbitrary array of the correct size)
	 * @return the set of all subsets of the desired size of the set of the player indexes
	 * **/
	private int[][] generatePlayerSubsets(int layer, int[][] subsets){
		int[][] newSubsets = new int[subsets.length * numPlayers][subsets[0].length];

		int newSubsetsGenerated = 0;
		for (int i = 0; i < subsets.length; i++){
			int minNextPlayerNum;
			if (layer != 0) minNextPlayerNum = subsets[i][layer - 1] + 1;
			else            minNextPlayerNum = 0;

			// get a copy of the current array,
			// and generate all arrays that add one more player of greater index than the last-added one
			for (int j = minNextPlayerNum; j < numPlayers; j++){
				int[] newSubset = subsets[i].clone();
				newSubset[layer] = j;
				newSubsets[newSubsetsGenerated] = newSubset;
				newSubsetsGenerated++;
			}
		}

		//truncate the array of worlds to the correct size by copying it to an array of the correct size
		newSubsets = Arrays.copyOf(newSubsets, newSubsetsGenerated);

		// repeat if not all spies have been added, otherwise return the current set of all worlds of spies
		if (layer + 1 == subsets[0].length) return newSubsets;
		else                                return generatePlayerSubsets(layer + 1, newSubsets);
	}

	private void printWorldProbabilities(){
		System.out.println("\n\nSPIES        PROBABILITY");
		for (int i = 0; i < worlds.length; i++){
			System.out.print("(");
			for (int j = 0; j < numSpies - 1; j++){
				System.out.print(worlds[i].spies[j] + ",");
			}
			System.out.print(worlds[i].spies[numSpies - 1] + ")");
			System.out.println(("       ") + worlds[i].probability);
		}
		System.out.println("\n");
	}

	private int spiesOnTeamGivenWorld(int[] mission, int worldIndex){
		int spiesOnTeam = 0;
		for (int i = 0; i < mission.length; i++){
			int missionMember = mission[i];
			if (worlds[worldIndex].spiesBool[missionMember]) spiesOnTeam++;
		}

		return spiesOnTeam;
	}

	public class World {
		public int[] spies;
		public boolean[] spiesBool;
		public double probability;

		public World(int[] spies, double probability){
			this.spies = spies;
			this.probability = probability;

			boolean[] spiesBool = new boolean[numPlayers];
			for (int i = 0; i < numPlayers; i++){
				spiesBool[i] = false;
			}
			for (int i = 0; i < spies.length; i++){
				spiesBool[i] = true;
			}
			this.spiesBool = spiesBool;
		}
	}
}
