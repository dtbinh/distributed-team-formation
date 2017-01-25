package ufrgs.maslab.abstractsimulator.algorithms.model.beeclustering;

import java.util.ArrayList;
import java.util.HashSet;

public class UtilityEvaluator {

	protected ArrayList<Bee> parameters = new ArrayList<Bee>();
	
	protected HashSet<Bee> parametersSet = new HashSet<Bee>();
	
	private Double centroid = 0d;
	
	public void addParameter(Bee x)
	{
		if(this.parametersSet.add(x))
			this.parameters.add(x);
	}
	
	public ArrayList<Bee> getParameters()
	{
		return this.parameters;
	}

	public Double getCentroid() {
		return this.centroid;
	}
	
	public Double variance()
	{
		double sum = 0d;
		for(Bee p :this.parameters)
		{
			sum += Math.pow((p.getAbandonProbability() - this.getCentroid()),2);
		}
		double v = 1d;
		
		v = sum / this.parameters.size();
		return Math.sqrt(v);
		
	}
	
	public Double utilityCluster()
	{
		return 1 - this.variance();
	}
	
	public Double utilityClusterWithout(Bee i)
	{
		return 1 - this.varianceWithout(i);
	}
	
	public Double varianceWithout(Bee i)
	{
		double sum = 0d;
		for(Bee p :this.parameters)
		{
			if(i != p)
				sum += Math.pow((p.getAbandonProbability() - this.getCentroid()),2);
		}
		double v = 1d;
		
		v = sum /( this.parameters.size() - 1);
		return Math.sqrt(v);
		
	}

	public Double setCentroid() {
		double c = 0d;
		for(Bee p : this.parameters)
		{
			c += p.getAbandonProbability();
		}
		return (c / this.getParameters().size());
	}
	
	public Double setCentroidWithout(Bee i)
	{
		double c = 0d;
		for(Bee p : this.parameters)
		{
			if(p != i)
				c += p.getAbandonProbability(); 
		}
		return (c/(this.getParameters().size() - 1));
	}
	
	
	
	
	

}
