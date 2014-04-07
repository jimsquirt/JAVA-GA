/* TesterGA.java
 *
 * Runs GeneticAlgorithm.java and logs the results into a file using Writer.java.
 * GA testing setup is according to pass/fail criteria
 * Pass criteria - 50 success
 * Fail criteria - 100 failures
 *
 * @author: James M. Bayon-on
 * @version: 1.3
 */

public class TesterGA {
	Writer logWriter;
	GeneticAlgorithm ga;
	int MAX_RUN;
	int MAX_LENGTH;
	long[] runtimes;

	/* Instantiates the TesterGA class
	 *
	 */
	public TesterGA() {
		logWriter = new Writer();
		MAX_RUN = 50;
		runtimes = new long[MAX_RUN];
	}

	/* Test method accepts the N/max length, and parameters mutation rate and max epoch to set for the GA accordingly.
	 *
	 * @param: max length/n
	 * @param: mutation rate for GA
	 * @param: max epoch for GA
	 */
	public void test(int maxLength, double mutationRate, int maxEpoch) {
		MAX_LENGTH = maxLength;
		ga = new GeneticAlgorithm(MAX_LENGTH);										//define ga here
		ga.setMutation(mutationRate);
		ga.setEpoch(maxEpoch);
		long testStart = System.nanoTime();
		String filepath = "GA-N"+MAX_LENGTH+"-"+mutationRate+"-"+maxEpoch+".txt";
		long startTime = 0;
        long endTime = 0;
        long totalTime = 0;
        int fail = 0;
        int success = 0;
        
		logParameters();
        
        for(int i = 0; i < MAX_RUN; ) {												//run 50 sucess to pass passing criteria
        	startTime = System.nanoTime();
        	if(ga.algorithm()) {
        		endTime = System.nanoTime();
        		totalTime = endTime - startTime;
        		
        		System.out.println("Done");
        		System.out.println("run "+(i+1));
            	System.out.println("time in nanoseconds: "+totalTime);
            	System.out.println("Success!");
            	
            	runtimes[i] = totalTime;
            	i++;
            	success++;
            	
            	//write to log
            	logWriter.add((String)("Run: "+i));
            	logWriter.add((String)("Runtime in nanoseconds: "+totalTime));
            	logWriter.add((String)("Found at epoch: "+ga.getEpoch()));
            	logWriter.add((String)("Population size: "+ga.getPopSize()));
            	logWriter.add("");
            	
            	for(Chromosome c: ga.getSolutions()) {								//write solutions to log file
					logWriter.add(c);
					logWriter.add("");
    			}
        	} else {																//count failures for failing criteria
        		fail++;
        		System.out.println("Fail!");
        	}
        	
        	if(fail >= 100) {
        		System.out.println("Cannot find solution with these params");
        		break;
        	}
        	startTime = 0;															//reset time
        	endTime = 0;
        	totalTime = 0;
        }
	
        System.out.println("Number of Success: " +success);
        System.out.println("Number of failures: "+fail);
        logWriter.add("Runtime summary");
        logWriter.add("");
        
		for(int x = 0; x < runtimes.length; x++){									//print runtime summary
			logWriter.add(Long.toString(runtimes[x]));
		}
		
		long testEnd = System.nanoTime();
		logWriter.add(Long.toString(testStart));
		logWriter.add(Long.toString(testEnd));
		logWriter.add(Long.toString(testEnd - testStart));
		
      
       	logWriter.writeFile(filepath);
       	printRuntimes();
	}

	/* Converts the parameters of GA to string and adds it to the string list in the writer class
	 *
	 */
	public void logParameters() {
        logWriter.add("Genetic Algorithm");
        logWriter.add("Parameters");
        logWriter.add((String)("MAX_LENGTH/N: "+MAX_LENGTH));
        logWriter.add((String)("STARTING_POPULATION: "+ga.getStartSize()));
        logWriter.add((String)("MAX_EPOCHS: "+ga.getMaxEpoch()));
        logWriter.add((String)("MATING_PROBABILITY: "+ga.getMatingProb()));
        logWriter.add((String)("MUTATION_RATE: "+ga.getMutationRate()));
        logWriter.add((String)("MIN_SELECTED_PARENTS: "+ga.getMinSelect()));
        logWriter.add((String)("MAX_SELECTED_PARENTS: "+ga.getMaxSelect()));
        logWriter.add((String)("OFFSPRING_PER_GENERATION: "+ga.getOffspring()));
        logWriter.add((String)("MINIMUM_SHUFFLES: "+ga.getShuffleMin()));
        logWriter.add((String)("MAXIMUM_SHUFFLES: "+ga.getShuffleMax()));
        logWriter.add("");
	}

	/* Prints the runtime summary in the console
	 *
	 */
	public void printRuntimes() {
		for(long x: runtimes){
			System.out.println("run with time "+x+" nanoseconds");
		}	
	}

	public static void main(String args[]) {
		TesterGA tester = new TesterGA();

		tester.test(4, 0.001, 1000);
/*		tester.test(8, 0.001, 1000);
		tester.test(12, 0.001, 1000);
		tester.test(16, 0.001, 1000);
		tester.test(20, 0.001, 1000);
		
		tester.test(20, 0.001, 1000);
		tester.test(20, 0.005, 1000);
		tester.test(20, 0.01, 1000);
		tester.test(20, 0.05, 1000);
		tester.test(20, 0.1, 1000);
		
		tester.test(16, 0.001, 5000);
		tester.test(16, 0.005, 5000);
		tester.test(16, 0.01, 5000);
		tester.test(16, 0.05, 5000);
		tester.test(16, 0.1, 5000);
		
		tester.test(20, 0.001, 5000);
		tester.test(20, 0.005, 5000);
		tester.test(20, 0.01, 5000);
		tester.test(20, 0.05, 5000);
		tester.test(20, 0.1, 5000);
		
 		tester.test(16, 0.001, 1000);
		tester.test(16, 0.005, 1000);
		tester.test(16, 0.01, 1000);
		tester.test(16, 0.05, 1000);
		tester.test(16, 0.1, 1000);
			
		tester.test(16, 0.001, 5000);
		tester.test(16, 0.005, 5000);
		tester.test(16, 0.01, 5000);
		tester.test(16, 0.05, 5000);
		tester.test(16, 0.1, 5000);

		tester.test(16, 0.001, 10000);
		tester.test(16, 0.005, 10000);
		tester.test(16, 0.01, 10000);
		tester.test(16, 0.05, 10000);
		tester.test(16, 0.1, 10000);

		tester.test(20, 0.001, 1000);
		tester.test(20, 0.005, 1000);
		tester.test(20, 0.01, 1000);
		tester.test(20, 0.05, 1000);
		tester.test(20, 0.1, 1000);
		
		tester.test(20, 0.001, 5000);
		tester.test(20, 0.005, 5000);
		tester.test(20, 0.01, 5000);
		tester.test(20, 0.05, 5000);
		tester.test(20, 0.1, 5000);

		tester.test(20, 0.001, 10000);
		tester.test(20, 0.005, 10000);
		tester.test(20, 0.01, 10000);
		tester.test(20, 0.05, 10000);
		tester.test(20, 0.1, 10000);	
*/
		
	}
}
