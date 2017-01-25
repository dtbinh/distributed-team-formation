package ufrgs.maslab.abstractsimulator.algorithms.model.beeclustering;

import java.util.Random;

import ufrgs.maslab.abstractsimulator.core.Variable;
import ufrgs.maslab.abstractsimulator.exception.SimulatorException;
import ufrgs.maslab.abstractsimulator.util.Transmitter;

public class Bee {

	
	/**
	 * object which wears the variable node implementation
	 */
	public Variable agent = null;
	
	private int id;

	private Double lastUtility;
	/**
	 * v - visiting
	 * w - watching
	 * d - dancing
	 */
	private String state;

	private Double stimulus = 0d;
	
	private Double threshold = 1d;
	
	private String exceptionFile = "exception.properties";
	
	public Bee(Variable var)
	{
		this.setId(var.getId());
		this.agent = var;
		
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int hashCode() {
        return ("Bee_" + this.id).hashCode();
    }

	public String getState() {
		return state;
	}
	
	public boolean abandon()
	{
		Random r = new Random();
		if(r.nextDouble() <= (this.getAbandonProbability()) && this.getAbandonProbability( ) > 0.0)
			return true;
		
		return false;
		
		
	}
	
	public boolean dancing()
	{
		Random r = new Random();
		if(r.nextDouble() <= this.getDancingProbability() && this.getDancingProbability() > 0.0)
			return true;
		return false;
	}
	
	public Double getAbandonProbability()
	{
		return this.agent.getUtilityAction();
	}
	
	public Double getDancingProbability()
	{
		return (Math.pow(this.stimulus,2) / (Math.pow(this.stimulus, 2) + Math.pow(this.threshold, 2)));
	}

	public void setState(String state) {
		this.state = state;
	}

	public Double getStimulus() {
		return stimulus;
	}

	public void setStimulus(Double stimulus) {
		this.stimulus = stimulus;
	}

	public Double getThreshold() {
		return threshold;
	}

	public void setThreshold(Double threshold) {
		this.threshold = threshold;
	}

	public Double getLastUtility() {
		return lastUtility;
	}

	public void setLastUtility(Double lastUtility) {
		this.lastUtility = lastUtility;
	}

	public void updateStimulusAndThreshold(double u, Double alpha) {
		double s = 0d;
		double t = 0d;
		if(u > this.lastUtility)
		{
			s = this.getStimulus() + alpha;
			t = this.getThreshold() - alpha;
		}else{
			s = this.getStimulus() - alpha;
			t = this.getThreshold() + alpha;
		}
		this.setLastUtility(u);
		this.setStimulus(s);
		this.setThreshold(t);
		
	}
	
}
