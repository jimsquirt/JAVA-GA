This repository contains a JAVA code implementation for the Genetic Algorithm in solving the N-Queens problem.

Classes include:

Chromosome.java - class which contains the solutions.
GeneticAlgorithm.java - class which implements the genetic algorithm for N-Queens. Algorithm parameters are defined here.
Writer.java - class which holds a string list to be written in a log file.
TesterGA.java - class which runs the tests and invokes the creation of the log file. 

How to use:

Install JAVA JDK.
Compile and run TesterGA.java along with its required classes in your preferred editor.

Sample log file:

GA-N4-0.001-1000.txt

Genetic Algorithm
Parameters
MAX_LENGTH/N: 4
STARTING_POPULATION: 40
MAX_EPOCHS: 1000
MATING_PROBABILITY: 0.7
MUTATION_RATE: 0.001
MIN_SELECTED_PARENTS: 10
MAX_SELECTED_PARENTS: 30.0
OFFSPRING_PER_GENERATION: 20.0
MINIMUM_SHUFFLES: 8
MAXIMUM_SHUFFLES: 20

Run: 1
Runtime in nanoseconds: 19100551
Found at epoch: 1
Population size: 60

. Q . . 
. . . Q 
Q . . . 
. . Q . 

. . Q . 
Q . . . 
. . . Q 
. Q . . 

. Q . . 
. . . Q 
Q . . . 
. . Q . 

. . Q . 
Q . . . 
. . . Q 
. Q . . 

. . Q . 
Q . . . 
. . . Q 
. Q . . 

. . Q . 
Q . . . 
. . . Q 
. Q . . 

. . Q . 
Q . . . 
. . . Q 
. Q . . 

Run: 2
Runtime in nanoseconds: 18995721
Found at epoch: 1
Population size: 70

. Q . . 
. . . Q 
Q . . . 
. . Q . 

. . Q . 
Q . . . 
. . . Q 
. Q . . 

. Q . . 
. . . Q 
Q . . . 
. . Q . 

. . Q . 
Q . . . 
. . . Q 
. Q . . 

. . Q . 
Q . . . 
. . . Q 
. Q . . 

Run: 3
Runtime in nanoseconds: 9974971
Found at epoch: 1
Population size: 64

. . Q . 
Q . . . 
. . . Q 
. Q . . 

. . Q . 
Q . . . 
. . . Q 
. Q . . 



...