package ufrgs.maslab.abstractsimulator.variables;

import java.util.ArrayList;
import java.util.Random;

import ufrgs.maslab.abstractsimulator.constants.MessageType;
import ufrgs.maslab.abstractsimulator.core.Entity;
import ufrgs.maslab.abstractsimulator.core.interfaces.Platoon;
import ufrgs.maslab.abstractsimulator.exception.SimulatorException;
import ufrgs.maslab.abstractsimulator.log.FireFighterLogger;
import ufrgs.maslab.abstractsimulator.mailbox.message.Message;
import ufrgs.maslab.abstractsimulator.util.Transmitter;
import ufrgs.maslab.abstractsimulator.values.FireBuildingTask;
import ufrgs.maslab.abstractsimulator.values.Task;


public class FireFighter extends Human implements Platoon {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4608281129356235256L;
	
	/**
	 * <ul>
	 * <li>Default constructor of the fire fighter human</li>
	 * <li>Attributes</li>
	 * <li>Strength</li>
	 * <li>Dexterity</li>
	 * <li>Stamina</li>
	 * <li>Charisma</li>
	 * <li>Appearance</li>
	 * <li>Leadership</li>
	 * <li>Intelligence</li>
	 * <li>Reasoning</li>
	 * <li>Perception</li>
	 * <li>Will</li>
	 * <li>HP</li>
	 * </ul>
	 * <br/>
	 * <ul>
	 * <li>Skills</li>
	 * <li>Fire Fighting</li>
	 * <li>Advantages</li>
	 * </ul>
	 * 
	 */
	private Double mostDifficultyTask = Double.MIN_VALUE;
	
	private Double lessDifficultyTask = Double.MAX_VALUE;
	
	private Double priorProbability = null;
		
	public FireFighter()
	{
		super();
		this.configureFireFighterSkills();
	}
	
	/**
	 * ability to extinguish fire from buildings
	 */
	private int fireFighting;
	
	public void configureFireFighterSkills(){
		this.setFireFighting();
	}
	
	/**
	 * constructor to search for fire fighter
	 * @param id
	 */
	public FireFighter(Integer id)
	{
		super(id);
	}

	public int getFireFighting() {
		return fireFighting;
	}

	private void setFireFighting() {
		this.fireFighting = 1 + this.rollDices(5);
	}
	
	public void act(int time){
		if(!this.getDomain().isEmpty()){
			Task t = this.updateUtility();
			if(time <= 1){
				this.assign(t);
				//this.setPriorProbability(this.getUtilityAction());
			}else{
				if(this.getValueProbability() == null)	
				{
					this.assign(t);
				}
				if(this.getPriorProbability() == null)
				{
					
					this.setPriorProbability(this.getValueProbability());
					
					//this.getAnotherValue(this.getValue());
				}else{
					//System.out.println(this.getPriorProbability());
					//System.out.println(this.getValueProbability());
					//System.out.println((this.getPriorProbability() < this.getValueProbability()));
					if(this.getPriorProbability() < this.getValueProbability())
					{
						this.setPriorProbability(this.getValueProbability());
						if(this.getValue() != null && t.getId() == this.getValue())
						{
							this.getAnotherValue(this.getValue());
						}else{
							this.assign(t);
						}
					}else{
						this.assign(t);
						this.setPriorProbability(this.getValueProbability());
					}
				}
				
			}
		}else{
			Random r = new Random();
			this.setX(r.nextInt(Transmitter.getIntConfigParameter("config.properties", "maximum.xpos")));
			this.setY(r.nextInt(Transmitter.getIntConfigParameter("config.properties", "maximum.ypos")));
		}
	}

	@Override
	public void sendRadioMessage(Message msg) throws SimulatorException {
		if(msg.getType() != MessageType.RADIO)
			throw new SimulatorException(Transmitter.getProperty("exception.properties", "exception.not.radio.message"));
		this.getVoice().add(msg);
		
	}
	
	/**
	 * include the relative evaluation of the agent with
	 * respect to their domain values and normalized by its experience in the
	 * environment
	 */
	protected Task updateUtility()
	{
		Double mostFeasibleTask = Double.MAX_VALUE;
		FireBuildingTask bestTask = null;
		double utility = 0d;
		
		this.setUtilityDomain(new ArrayList<Double>());
		
		for(Entity val : this.getDomain())
		{
			Double value = 0d;
			FireBuildingTask b = null;
			if(val instanceof FireBuildingTask){
				//compute utility of the task b according to the perception of the agent
				b = (FireBuildingTask)val;
				
				value = this.computePerceptionOfTask(b);
				this.getUtilityDomain().add(value);
				//assign most feasible task
				if(value < mostFeasibleTask){
					mostFeasibleTask = value;
					bestTask = b;
				}
				utility += value;
			}else{
				this.setUtilityPerception(1d);
			}
		}
		if(this.getDomain().size() > 0){
			this.setUtilityPerception(utility / this.getDomain().size());
		}else{
			this.setUtilityPerception(utility / 1);
		}
		
		//utility of the action
		int id = -1;
		id = this.getDomain().indexOf(bestTask);
		double ua = 1d;
		if(id != -1)
		{
			ua = this.getUtilityDomain().get(id) / this.getUtilityPerception();
		}
		this.setUtilityAction(ua);
		
		return bestTask;
	}
	
	/**
	 * compute the distance between the agent and the task b.
	 * distance is the measure of the \omega(x and y position) (1- \omega)(
	 * normalization of the input data occurs according to the experience of the agent  
	 * 
	 * @param b
	 * @return
	 */
	public Double computePerceptionOfTask(FireBuildingTask b)
	{
		Double dist = 0d;
		//normalization bounds
		if(this.mostDifficultyTask < b.getValue()){
			this.mostDifficultyTask = b.getValue();
		}
		if(this.lessDifficultyTask > b.getValue())
		{
			this.lessDifficultyTask = b.getValue();
		}
		
		Double minX = this.getClosestX().doubleValue();
		Double minY = this.getClosestY().doubleValue();
		Double maxX = this.getLongestX().doubleValue(); //Transmitter.getDoubleConfigParameter("config.properties", "maximum.xpos");
		Double maxY = this.getLongestY().doubleValue(); //Transmitter.getDoubleConfigParameter("config.properties", "maximum.ypos");
		Double minSkill = 1d;
		Double maxSkill = 10d;
		
		Double aX = this.normalization(this.getX().doubleValue(), minX, maxX);
		Double aY = this.normalization(this.getY().doubleValue(), minY, maxY);
		Double aSkill = this.normalization((double)(this.getFireFighting() + this.getDexterity()), minSkill, maxSkill);
		
		Double tX = this.normalization(b.getX().doubleValue(), minX, maxX);
		Double tY = this.normalization(b.getY().doubleValue(), minY, maxY);
		Double tDifficulty = this.normalization(b.getValue(), this.lessDifficultyTask, this.mostDifficultyTask);
		dist = Math.pow((aX - tX), 2);
		dist += Math.pow((aY - tY), 2);
		dist = dist * this.getOmega();
		dist += ((1 - this.getOmega())* Math.pow((aSkill - tDifficulty), 2));
		double p = 0d;
		p = Math.sqrt(dist);
		return p;
	}
	
	/**
	 * min max normalization of one value
	 * @param value
	 * @param min
	 * @param max
	 * @return
	 */
	protected Double normalization(Double value, Double min, Double max)
	{
		double denominator = (max - min);
		if(denominator == 0)
			denominator = 1;
		Double normalizedValue = 0d;
		normalizedValue = (value - min) / denominator;
		return normalizedValue;
	}

	public Double getPriorProbability() {
		return priorProbability;
	}

	public void setPriorProbability(Double priorProbability) {
		this.priorProbability = priorProbability;
	}
	
	public void logger(){
		FireFighterLogger.logFireFighter(this);
	}
	
	public void header(){
		FireFighterLogger.saveHeader();
	}

	

}
