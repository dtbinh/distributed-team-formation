package ufrgs.maslab.abstractsimulator.core;

import java.util.HashMap;

public abstract class Evaluator {
	
	public abstract Double qualitySolution(Environment<? extends Entity> env);
	
	public abstract HashMap<Integer, Double> quality();
	
	public abstract HashMap<Integer, Double> idleVariables();
	
	public abstract HashMap<Integer, Double> notAllocatedValues();
	
	public abstract HashMap<Integer, Double> allocatedValues();
	
	public abstract int getTime();

}
