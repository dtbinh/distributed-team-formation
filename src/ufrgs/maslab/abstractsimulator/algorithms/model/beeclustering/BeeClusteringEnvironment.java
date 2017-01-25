package ufrgs.maslab.abstractsimulator.algorithms.model.beeclustering;

import java.util.ArrayList;

import ufrgs.maslab.abstractsimulator.core.Entity;
import ufrgs.maslab.abstractsimulator.core.Environment;
import ufrgs.maslab.abstractsimulator.core.Variable;
import ufrgs.maslab.abstractsimulator.util.Transmitter;

public class BeeClusteringEnvironment<E extends Entity> extends Environment<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6761781361421939204L;

	private String beeSettings = "bee.properties";
	
	private Double alpha = Transmitter.getDoubleConfigParameter(this.beeSettings, "bee.alpha");
	
	public ArrayList<Bee> beeSet = new ArrayList<Bee>();

	
	@SuppressWarnings("unchecked")
	@Override
	public void registerVariable(Class<? extends Variable> varClass, Integer ammount) {
		
		this.getVarClass().put((Class<Variable>)varClass, ammount);
		for(Object v : this.getVariables())
		{
			if(v instanceof Variable && v.getClass() == varClass)
			{
				this.beeSet.add(new Bee((Variable)v));
			}
		}
		
	}

	public Double getAlpha() {
		return alpha;
	}

	public void setAlpha(Double alpha) {
		this.alpha = alpha;
	}
	
}
