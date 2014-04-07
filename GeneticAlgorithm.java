/* GeneticAlgorithm.java
 *
 * Solves the N-Queens puzzle using Genetic Algorithm.
 * Code is based on partially mapped crossover genetic algortihm for n queens
 * Base code at http://mnemstudio.org/ai/ga/nqueens_java_ex1.txt
 *
 * @author: James M. Bayon-on
 * @version: 1.3
 */

import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

public class GeneticAlgorithm {
	/*GA PARAMETERS*/
	private int MAX_LENGTH;                 // chess board width. or n in n queens
	private int START_SIZE;				    // Population size at start.
	private int MAX_EPOCHS;                 // Arbitrary number of test cycles.
	private double MATING_PROBABILITY;      // Probability of two chromosomes mating. Range: 0.0 < MATING_PROBABILITY < 1.0
	private double MUTATION_RATE;           // Mutation Rate. Range: 0.0 < MUTATION_RATE < 1.0
	private int MIN_SELECT;                 // Minimum parents allowed for selection.
	private int MAX_SELECT;                 // Maximum parents allowed for selection. Range: MIN_SELECT < MAX_SELECT < START_SIZE
	private int OFFSPRING_PER_GENERATION;   // New offspring created per generation. Range: 0 < OFFSPRING_PER_GENERATION < MAX_SELECT.
	private int MINIMUM_SHUFFLES;           // For randomizing starting chromosomes
	private int MAXIMUM_SHUFFLES;

	private int nextMutation;               // For scheduling mutations.
	private ArrayList<Chromosome> population;
	private ArrayList<Chromosome> solutions;
	private Random rand;
	private int childCount;
	private int mutations;
	private int epoch;
	private int populationSize;

	/* Instantiates the genetic algorithm along with its parameters.
	 *
	 * @param: size of n queens
	 */
	public GeneticAlgorithm(int n) {
		MAX_LENGTH = n;
		START_SIZE = 40;
		MAX_EPOCHS = 1000;
		MATING_PROBABILITY = 0.7;
		MUTATION_RATE = 0.001;
		MIN_SELECT = 10; 
		MAX_SELECT = 30;
		OFFSPRING_PER_GENERATION = 20;
		MINIMUM_SHUFFLES = 8; 
		MAXIMUM_SHUFFLES = 20;  
		epoch = 0;
		populationSize = 0;
	}

	/* Starts the genetic algorithm solving for n queens.
	 *
	 */
	public boolean algorithm() {
		population = new ArrayList<Chromosome>();
		solutions = new ArrayList<Chromosome>();
		rand = new Random();
		nextMutation = 0;
		childCount = 0;                 
		mutations = 0;
		epoch = 0;
		populationSize = 0;

		boolean done = false;
		Chromosome thisChromo = null;
		nextMutation = getRandomNumber(0, (int)Math.round(1.0 / MUTATION_RATE));

		initialize();

		while(!done) {
			populationSize = population.size();

			for(int i = 0; i < populationSize; i++) {
				thisChromo = population.get(i);
				if((thisChromo.getConflicts() == 0)) {			//if solution found
					done = true;
				}
			}

			if(epoch == MAX_EPOCHS) {							//if Max Number of Cycles 
				done = true;
			}

			getFitness();

			rouletteSelection();

			mating();

			prepNextEpoch();

			epoch++;
			System.out.println("Epoch: " + epoch);
		}

		if(epoch >= MAX_EPOCHS) {
			System.out.println("No solution found");
			done = false;
		} else {
			populationSize = population.size();					//prints the solutions if found within mnc
			for(int i = 0; i < populationSize; i++) {
				thisChromo = population.get(i);
				if(thisChromo.getConflicts() == 0) {
					solutions.add(thisChromo);
					printSolution(thisChromo);
				}
			}
		}
		System.out.println("done.");

		System.out.println("Completed " + epoch + " epochs.");
		System.out.println("Encountered " + mutations + " mutations in " + childCount + " offspring."); 
		
		return done;
	}

	/* Starts the mating process with the selected chromosomes.
	 *
	 */
	public void mating() {
		int getRand = 0;
        int parentA = 0;
        int parentB = 0;
        int newIndex1 = 0;
        int newIndex2 = 0;
        Chromosome newChromo1 = null;
        Chromosome newChromo2 = null;

        for(int i = 0; i < OFFSPRING_PER_GENERATION; i++) {
            parentA = chooseParent();
            // Test probability of mating.
            getRand = getRandomNumber(0, 100);
            if(getRand <= MATING_PROBABILITY * 100) {
                parentB = chooseParent(parentA);
                newChromo1 = new Chromosome(MAX_LENGTH);
                newChromo2 = new Chromosome(MAX_LENGTH);
                population.add(newChromo1);
                newIndex1 = population.indexOf(newChromo1);
                population.add(newChromo2);
                newIndex2 = population.indexOf(newChromo2);
                
                // partiallyMappedCrossover
                partiallyMappedCrossover(parentA, parentB, newIndex1, newIndex2);

                if(childCount - 1 == nextMutation) {
                    exchangeMutation(newIndex1, 1);
                } else if (childCount == nextMutation) {
                    exchangeMutation(newIndex2, 1);
                }

                population.get(newIndex1).computeConflicts();
                population.get(newIndex2).computeConflicts();

                childCount += 2;

                // Schedule next mutation.
                if(childCount % (int)Math.round(1.0 / MUTATION_RATE) == 0) {
                    nextMutation = childCount + getRandomNumber(0, (int)Math.round(1.0 / MUTATION_RATE));
                    //System.out.println("HYE   "+nextMutation);
                }
            }
        } // i
	}

	/* Crossovers two chromosome parents. Uses partiallyMappedCrossover technique.
	 *
	 * @param: parent A
	 * @param: parent B
	 * @param: child A
	 * @param: child B
	 */
	public void partiallyMappedCrossover(int chromA, int chromB, int child1, int child2) {
        int j = 0;
        int item1 = 0;
        int item2 = 0;
        int pos1 = 0;
        int pos2 = 0;
        Chromosome thisChromo = population.get(chromA);
        Chromosome thatChromo = population.get(chromB);
        Chromosome newChromo1 = population.get(child1);
        Chromosome newChromo2 = population.get(child2);
        int crossPoint1 = getRandomNumber(0, MAX_LENGTH - 1);
        int crossPoint2 = getExclusiveRandomNumber(MAX_LENGTH - 1, crossPoint1);
        
        //gets the crosspoint from where to swap
        if(crossPoint2 < crossPoint1) {
            j = crossPoint1;
            crossPoint1 = crossPoint2;
            crossPoint2 = j;
        }

        // Copy Parent genes to offspring.
        for(int i = 0; i < MAX_LENGTH; i++) {
            newChromo1.setGene(i, thisChromo.getGene(i));
            newChromo2.setGene(i, thatChromo.getGene(i));
        }

        for(int i = crossPoint1; i <= crossPoint2; i++) {
            // Get the two items to swap.
            item1 = thisChromo.getGene(i);
            item2 = thatChromo.getGene(i);

            // Get the items//  positions in the offspring.
            for(j = 0; j < MAX_LENGTH; j++) {
                if(newChromo1.getGene(j) == item1) {
                    pos1 = j;
                } else if (newChromo1.getGene(j) == item2) {
                    pos2 = j;
                }
            } // j

            // Swap them.
            if(item1 != item2) {
                newChromo1.setGene(pos1, item2);
                newChromo1.setGene(pos2, item1);
            }

            // Get the items//  positions in the offspring.
            for(j = 0; j < MAX_LENGTH; j++) {
                if(newChromo2.getGene(j) == item2) {
                    pos1 = j;
                } else if(newChromo2.getGene(j) == item1) {
                    pos2 = j;
                }
            } // j

            // Swap them.
            if(item1 != item2) {
                newChromo2.setGene(pos1, item1);
                newChromo2.setGene(pos2, item2);
            }

        } // i
	}

	/* Chooses a randomly selected parent.
	 *
	 * @return: random index of parent
	 */
	public int chooseParent() {
    	// Overloaded function, see also "chooseparent(ByVal parentA As Integer)".
        int parent = 0;
        Chromosome thisChromo = null;
        boolean done = false;

        while(!done) {
            // Randomly choose an eligible parent.
            parent = getRandomNumber(0, population.size() - 1);
            thisChromo = population.get(parent);
            if(thisChromo.isSelected() == true) {
                done = true;
            }
        }

        return parent;    	
    }    
    
    /* Chooses a randomly selected parent which is not the parameter.
	 *
	 * @param: selected parent index
	 * @return: random index of parent
	 */
    public int chooseParent(int parentA) {
        // Overloaded function, see also "chooseparent()".
        int parent = 0;
        Chromosome thisChromo = null;
        boolean done = false;

        while(!done) {
            // Randomly choose an eligible parent.
            parent = getRandomNumber(0, population.size() - 1);
            if(parent != parentA){
                thisChromo = population.get(parent);
                if(thisChromo.isSelected() == true){
                    done = true;
                }
            }
        }

        return parent;    	
    } 

	/* Chooses selected parents based on roulette selection.
	 *
	 */
	public void rouletteSelection() {
   	 	int j = 0;
        int populationSize = population.size();
        int maximumToSelect = getRandomNumber(MIN_SELECT, MAX_SELECT);
        double genTotal = 0.0;
        double selTotal = 0.0;
        double rouletteSpin = 0.0;
        Chromosome thisChromo = null;
        Chromosome thatChromo = null;
        boolean done = false;
        
        for(int i = 0; i < populationSize; i++) {												//get total fitness
            thisChromo = population.get(i);
            genTotal += thisChromo.getFitness();
        }
        
        genTotal *= 0.01;															

        for(int i = 0; i < populationSize; i++) {
            thisChromo = population.get(i);
            thisChromo.setSelectionProbability(thisChromo.getFitness() / genTotal);		//set selection probability. the more fit the better selection probability
        }
        
        for(int i = 0; i < maximumToSelect; i++) {										//selects parents
            rouletteSpin = getRandomNumber(0, 99);
            j = 0;
            selTotal = 0;
            done = false;
            while(!done) {
                thisChromo = population.get(j);
                selTotal += thisChromo.getSelectionProbability();
                if(selTotal >= rouletteSpin) {
					 if(j == 0) {
					    thatChromo = population.get(j);
					 } else if(j >= populationSize - 1) {
					     thatChromo = population.get(populationSize - 1);
					 } else {
					     thatChromo = population.get(j-1);
					 }
					thatChromo.setSelected(true);
					done = true;
                } else {
                    j++;
                }
            }
        }
	}

	/* Sets the fitness of each chromosome based on its conflicts
	 *
	 */
	public void getFitness() {
		// Lowest errors = 100%, Highest errors = 0%
		int populationSize = population.size();
		Chromosome thisChromo = null;
		double bestScore = 0;
		double worstScore = 0;

		// The worst score would be the one with the highest energy, best would be lowest.
		worstScore = Collections.max(population).getConflicts();

		// Convert to a weighted percentage.
		bestScore = worstScore - Collections.min(population).getConflicts();

		for(int i = 0; i < populationSize; i++) {
			thisChromo = population.get(i);
			thisChromo.setFitness((worstScore - thisChromo.getConflicts()) * 100.0 / bestScore);
		}   
	}

	/* Resets all flags in the selection
	 *
	 */ 
	public void prepNextEpoch() {
		int populationSize = 0;
		Chromosome thisChromo = null;

		// Reset flags for selected individuals.
		populationSize = population.size();
		for(int i = 0; i < populationSize; i++) {
			thisChromo = population.get(i);
			thisChromo.setSelected(false);
		}   
	}

	/* Prints the nxn board with the queens
	 *
	 * @param: a chromosome
	 */ 
	public void printSolution(Chromosome solution) {
		String board[][] = new String[MAX_LENGTH][MAX_LENGTH];

		// Clear the board.
		for(int x = 0; x < MAX_LENGTH; x++) {
			for(int y = 0; y < MAX_LENGTH; y++) {
			board[x][y] = "";
			}
		}

		for(int x = 0; x < MAX_LENGTH; x++) {
			board[x][solution.getGene(x)] = "Q";
		}

		// Display the board.
		System.out.println("Board:");
		for(int y = 0; y < MAX_LENGTH; y++) {
			for(int x = 0; x < MAX_LENGTH; x++) {
				if(board[x][y] == "Q") {
					System.out.print("Q ");
				} else {
					System.out.print(". ");
				}
			}
			System.out.print("\n");
		}
	}

	/* Initializes all of the chromosomes' placement of queens in ramdom positions.
	 *
	 */ 
	public void initialize() {
		int shuffles = 0;
		Chromosome newChromo = null;
		int chromoIndex = 0;

		for(int i = 0; i < START_SIZE; i++)  {
			newChromo = new Chromosome(MAX_LENGTH);
			population.add(newChromo);
			chromoIndex = population.indexOf(newChromo);

			// Randomly choose the number of shuffles to perform.
			shuffles = getRandomNumber(MINIMUM_SHUFFLES, MAXIMUM_SHUFFLES);
			exchangeMutation(chromoIndex, shuffles);
			population.get(chromoIndex).computeConflicts();
		}
	}

	/* Changes the position of the queens in a chromosome randomly according to the number of exchanges
	 *
	 * @param: index of the chromosome
	 * @param: number of exhanges
	 */ 
	public void exchangeMutation(int index, int exchanges) {
		int tempData = 0;
		int gene1 = 0;
		int gene2 = 0;
		Chromosome thisChromo = null;
		thisChromo = population.get(index);

		for(int i = 0; i < exchanges; i++) {
			gene1 = getRandomNumber(0, MAX_LENGTH - 1);
			gene2 = getExclusiveRandomNumber(MAX_LENGTH - 1, gene1);

			// Exchange the chosen genes.
			tempData = thisChromo.getGene(gene1);
			thisChromo.setGene(gene1, thisChromo.getGene(gene2));
			thisChromo.setGene(gene2, tempData);
		}
		mutations++;
	}

	/* Gets a random number with the exception of the parameter
	 *
	 * @param: the maximum random number
	 * @param: number to to be chosen
	 * @return: random number
	 */ 
	public int getExclusiveRandomNumber(int high, int except) {
		boolean done = false;
		int getRand = 0;

		while(!done) {
			getRand = rand.nextInt(high);
			if(getRand != except){
				done = true;
			}
		}
		return getRand;  
	}

	/* Gets a random number in the range of the parameters
	 *
	 * @param: the minimum random number
	 * @param: the maximum random number
	 * @return: random number
	 */ 
	public int getRandomNumber(int low, int high) {
   		return (int)Math.round((high - low) * rand.nextDouble() + low);
	}
   /* gets the solutions
	 *
	 * @return: solutions
	 */  
	public ArrayList<Chromosome> getSolutions() {
		return solutions;
	}
	
	/* gets the epoch
	 *
	 * @return: epoch
	 */ 
	public int getEpoch() {
		return epoch;
	}
	
	/* gets the population size
	 *
	 * @return: pop size
	 */ 
	public int getPopSize() {
		return population.size();
	}
	
	/* gets the start size
	 *
	 * @return: start size
	 */ 
	public int getStartSize() {
		return START_SIZE;
	}
	
	/* gets the mating prob
	 *
	 * @return: mating prob
	 */ 
	public double getMatingProb() {
		return MATING_PROBABILITY;
	}
	
	/* gets the mutation rate
	 *
	 * @return: mutation rate
	 */ 
	public double getMutationRate() {
		return MUTATION_RATE;
	}
	
	/* gets the start size
	 *
	 * @return: start size
	 */ 
	public int getMinSelect() {
		return MIN_SELECT;
	}
	
	/* gets the mating prob
	 *
	 * @return: mating prob
	 */ 
	public double getMaxSelect() {
		return MAX_SELECT;
	}
	
	/* gets the mutation rate
	 *
	 * @return: mutation rate
	 */ 
	public double getOffspring() {
		return OFFSPRING_PER_GENERATION;
	}
	
	/* gets the max epoch
	 *
	 * @return: max epoch
	 */ 
	public int getMaxEpoch() {
		return MAX_EPOCHS;
	}

	/* gets the min shuffle
	 *
	 * @return: min shuffle
	 */ 
	public int getShuffleMin() {
		return MINIMUM_SHUFFLES;
	}

	/* gets the max shuffle
	 *
	 * @return: max shuffle
	 */ 
	public int getShuffleMax() {
		return MAXIMUM_SHUFFLES;
	}

	/* sets the mutation rate
	 *
	 * @param: new mutation rate value
	 */ 
	public void setMutation(double newMutation) {
		this.MUTATION_RATE = newMutation;
	}

	/* sets the new max epoch
	 *
	 * @param: new max epoch value
	 */ 	
	public void setEpoch(int newMaxEpoch) {
		this.MAX_EPOCHS = newMaxEpoch;
	}

}