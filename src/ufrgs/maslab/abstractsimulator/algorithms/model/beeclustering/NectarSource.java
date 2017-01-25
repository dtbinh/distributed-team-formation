package ufrgs.maslab.abstractsimulator.algorithms.model.beeclustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import ufrgs.maslab.abstractsimulator.core.Value;



public class NectarSource {

	/**
     * Like Node or Edge. Look at their doc, please.
     */
    public static HashMap<Integer, NectarSource> table = new HashMap<Integer, NectarSource>();
	
    private ArrayList<Bee> dancingBee = new ArrayList<Bee>();
    
    private ArrayList<Bee> watchingBee = new ArrayList<Bee>();
    
    public ArrayList<Bee> getWatchingBee() {
		return watchingBee;
	}

	public void setWatchingBee(ArrayList<Bee> watchingBee) {
		this.watchingBee = watchingBee;
	}

	public ArrayList<Bee> getVisitingBee() {
		return visitingBee;
	}

	public void setVisitingBee(ArrayList<Bee> visitingBee) {
		this.visitingBee = visitingBee;
	}

	private ArrayList<Bee> visitingBee = new ArrayList<Bee>();
    
    private Value task = null;
    
    /**
     * The FunctionEvaluator that implements the function represented by this NectarSource.
     */
    private UtilityEvaluator functionEvaluator;
    

    /**
     * id.. what else?
     */
    private int id;

    
	public NectarSource(int id, Value v)
	{
		this.id = id;
		this.setTask(v);
	}
	
	public boolean equals(Object n) {
        return (n instanceof NectarSource)
                && (this.getId() == ((NectarSource) n).getId());
    }

	private int getId() {
		return this.id;
	}
	
	public int hashCode() {
        return ("NectarSource_" + this.id).hashCode();
    }
	
	public boolean visit()
	{
		Random r = new Random();
		if(r.nextDouble() <= this.getVisitingProbability() && this.getVisitingProbability() > 0.0)
			return true;
		return false;
	}
	
	public Double getVisitingProbability()
	{
		double d = 1d;
		if(this.functionEvaluator.parameters.isEmpty()){
			d = 1;
		}else{
			d = this.functionEvaluator.parameters.size();
		}
			
		return this.dancingBee.size() / d;
	}
	
	public void removeBee(Bee i)
	{
		if(this.functionEvaluator.parameters.contains(i))
			this.functionEvaluator.parameters.remove(i);
		if(this.functionEvaluator.parametersSet.contains(i))
			this.functionEvaluator.parametersSet.remove(i);
		if(this.dancingBee.contains(i))
			this.dancingBee.remove(i);
		if(this.watchingBee.contains(i))
			this.watchingBee.remove(i);
		if(this.visitingBee.contains(i))
			this.visitingBee.remove(i);
	}
	
	public static NectarSource putNectarSource(Integer id, Value v, UtilityEvaluator u)
	{
		if(!NectarSource.table.containsKey(id))
		{
			NectarSource.table.put(id, new NectarSource(id, v));
			NectarSource.table.get(id).setFunctionEvaluator(new UtilityEvaluator());
		}
		return NectarSource.table.get(id);
	}
	
	public static NectarSource getNectarSource(Integer id)
	{
		if(table.containsKey(id))
			return table.get(id);
		return null;
	}
	
	public void setFunctionEvaluator(UtilityEvaluator function)
	{
		this.functionEvaluator = function;
	}

	public UtilityEvaluator getFunctionEvaluator() {
		return functionEvaluator;
	}

	public ArrayList<Bee> getDancingBee() {
		return dancingBee;
	}
	
	public void addWatchingBee(Bee watchingBee) {
		if(this.functionEvaluator.parametersSet.contains(watchingBee)){
			this.watchingBee.add(watchingBee);
		}else{
			this.functionEvaluator.addParameter(watchingBee);
			this.watchingBee.add(watchingBee);
		}
	}

	public void addVisitingBee(Bee visitingBee) {
		if(this.functionEvaluator.parametersSet.contains(visitingBee)){
			this.visitingBee.add(visitingBee);
		}else{
			this.functionEvaluator.addParameter(visitingBee);
			this.visitingBee.add(visitingBee);
		}
	}
	
	public void addDancingBee(Bee dancingBee) {
		if(this.functionEvaluator.parametersSet.contains(dancingBee)){
			this.dancingBee.add(dancingBee);
		}else{
			this.functionEvaluator.addParameter(dancingBee);
			this.dancingBee.add(dancingBee);
		}
	}
	
	public static void removeNectarSource(Integer n)
	{
		table.remove(n);
	}

	public Value getTask() {
		return task;
	}

	public void setTask(Value task) {
		this.task = task;
	}

	
    
    
}
