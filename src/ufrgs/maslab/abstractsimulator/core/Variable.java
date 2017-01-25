package ufrgs.maslab.abstractsimulator.core;

import java.util.ArrayList;
//import java.util.HashMap;


import java.util.HashMap;
import java.util.Random;

import ufrgs.maslab.abstractsimulator.mailbox.MailBox;
import ufrgs.maslab.abstractsimulator.mailbox.message.Message;
import ufrgs.maslab.abstractsimulator.util.Transmitter;
import ufrgs.maslab.abstractsimulator.values.Task;

public abstract class Variable extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8643465426657959565L;
			
	/**
	 * <ul>
	 * <li>current value assigned to this variable</li>
	 * </ul>
	 */
	private Integer value = null;
	
	private Double valueProbability = null;
	
	private ArrayList<Double> utilityDomain = new ArrayList<Double>();

	/**
	 * <ul>
	 * <li>old value assigned to this variable</li>
	 * </ul>
	 */
	private Integer oldValue = null;
	
	/**
	 * <ul>
	 * <li>utility of the current percetion</li>
	 * </ul>
	 */
	private Double utilityPerception = Double.MIN_VALUE;
	
	/**
	 * <ul>
	 * <li>utility of the chosen action</li>
	 * </ul>
	 */
	private Double utilityAction = null;
	/**
	 * <ul>
	 * <li>if this variable is allocated to some value or not</li>
	 * </ul>
	 */
	private boolean allocated = false;
	
	/**
	 * <ul>
	 * <li>lifetime of the variable</li>
	 * </ul>
	 */
	private int time = 0;

	/**
	 * <ul>
	 * <li>stores the most distance task in the radius of the agent</li>
	 * </ul>
	 */
	private Double mostDistanceTask = Double.MIN_VALUE;
	
	/**
	 * <ul>
	 * <li>stores the most closest task in the radius of the agent</li>
	 * </ul>
	 */
	private Double mostClosestTask = Double.MAX_VALUE;
	
	/**
	 * <ul>
	 * <li>domain of the variable</li>
	 * <li>map of values</li>
	 * <li>value is the key and class is the value</li>
	 * </ul>
	 */
	//private HashMap<Integer,Class<? extends Entity>> domain = new HashMap<Integer,Class<? extends Entity>>();
	private ArrayList<Entity> domain = new ArrayList<Entity>();
	
	/**
	 * <ul>
	 * <li>utility of values</li>
	 * </ul>
	 */
	private ArrayList<Double> domainValues = new ArrayList<Double>();
	
	private Integer x;
	
	private Integer longestX = Integer.MIN_VALUE;
	
	private Integer closestX = Integer.MAX_VALUE;
	
	private Integer longestY = Integer.MIN_VALUE;
	
	private Integer closestY = Integer.MAX_VALUE;
	
	public Integer getLongestX() {
		return longestX;
	}

	public void setLongestX(Integer longestX) {
		this.longestX = longestX;
	}

	public Integer getClosestX() {
		return closestX;
	}

	public void setClosestX(Integer closestX) {
		this.closestX = closestX;
	}

	public Integer getLongestY() {
		return longestY;
	}

	public void setLongestY(Integer longestY) {
		this.longestY = longestY;
	}

	public Integer getClosestY() {
		return closestY;
	}

	public void setClosestY(Integer closestY) {
		this.closestY = closestY;
	}

	private Integer y;
	
	private MailBox mail = new MailBox();
	
	public Variable(Integer id) {
		super(id);
	}

	public Variable() {
		super();
		Random r = new Random();
		this.setX(r.nextInt(Transmitter.getIntConfigParameter("config.properties", "maximum.xpos")));
		this.setY(r.nextInt(Transmitter.getIntConfigParameter("config.properties", "maximum.ypos")));
	}

	/**
	 * <ul>
	 * <li>returns the value assigned to this variable</li>
	 * </ul>
	 * @return
	 */
	public Integer getValue() {
		return this.value;
	}
	
	/**
	 * return the utility of the current state
	 * @return
	 */
	public Double getUtilityPerception()
	{
		return this.utilityPerception;
	}
	
	/**
	 * set the utility of the current state
	 * @return
	 */
	protected void setUtilityPerception(Double d)
	{
		this.utilityPerception = d;
	}

	/**
	 * <ul>
	 * <li>assigns the value id to this variable</li>
	 * <li>if the current value is different of the new value</li>
	 * <li>then stores the current value to the old value</li>
	 * </ul>
	 * @param value
	 */
	public void assign(Task value) {
		
		if(value != null)
		{
			if(this.value != null && this.value != value.getId())
			{
				this.setOldValue(this.value);
				this.value = value.getId();
			}else{
				this.value = value.getId();
				this.setOldValue(null);
			}
			/*
			 * will be included after
			if(this.domain.contains(value))
			{
				this.value = value;
			}else{
				throw new SimulatorException("Variable does not has in domain.");
			}*/
		}else
		{
			this.oldValue = this.value;
			this.value = null;
		}
	}
	
	/**
	 * <ul>
	 * <li>abstract function act</li>
	 * <li>all actions of the variable must be performed here</li>
	 * <li>act will be executed by simulator engine</li>
	 * </ul>
	 * @param time
	 */
	public abstract void act(int time);
		
	/**
	 * <ul>
	 * <li>returns the domain of the variable</li>
	 * </ul>
	 * @return
	 */
	/*public HashMap<Integer,Class<? extends Entity>> getDomain() {
		return this.domain;
	}*/
	
	public ArrayList<Entity> getDomain(){
		return this.domain;
	}

	
	/**
	 * <ul>
	 * <li>insert one item (value) to domain of the agent</li>
	 * </ul>
	 * @param value
	 */
	/*public void insertDomain(Integer value, Class<? extends Entity>clazz)
	{
		if(!this.getDomain().containsKey(value))
			this.getDomain().put(value, clazz);
	}*/
	public void insertDomain(Entity value)
	{
		if(!this.getDomain().contains(value))
			this.getDomain().add(value);
	}

	/**
	 * <ul>
	 * <li>returns if the variable is allocated to some value</li>
	 * </ul>
	 * @return
	 */
	public boolean isAllocated() {
		return this.allocated;
	}

	/**
	 * <ul>
	 * <li>sets the allocation of the variable</li>
	 * </ul>
	 * @param allocated
	 */
	public void setAllocated(boolean allocated) {
		this.allocated = allocated;
	}

	/**
	 * <ul>
	 * <li>returns the age, in turns, of this variable</li>
	 * </ul>
	 * @return
	 */
	public int getTime() {
		return time;
	}

	/**
	 * <ul>
	 * <li>sets the time of this variable</li>
	 * </ul>
	 * @param time
	 */
	public void setTime(int time) {
		this.time = time;
	}

	
	public Integer getX(){
		return this.x;
	}
	
	public Integer getY(){
		return this.y;
	}
	
	public void setY(Integer y)
	{
		this.y = y;
	}
	
	public void setX(Integer x){
		this.x = x;
	}

	/**
	 * return all new messages
	 * @return
	 */
	public ArrayList<Message> getNewMail() {
		return mail.getInBox();
	}
	
	public abstract void logger();

	public abstract void header();

	/*public void setDomain(HashMap<Integer, Class<? extends Entity>> domain) {
		this.domain = domain;
	}*/
	public void setDomain(ArrayList<Entity> domain)
	{
		this.domain = domain;
	}

	public ArrayList<Double> getDomainValues() {
		return domainValues;
	}

	public void setDomainValues(ArrayList<Double> domainValues) {
		this.domainValues = domainValues;
	}

	public Double getMostDistanceTask() {
		return mostDistanceTask;
	}

	public void setMostDistancetTask(Double mostDifficultTask) {
		this.mostDistanceTask = mostDifficultTask;
	}

	public Double getMostClosestTask() {
		return mostClosestTask;
	}

	public void setMostClosestTask(Double mostClosestTask) {
		this.mostClosestTask = mostClosestTask;
	}
	
	public void getAnotherValue(Integer oldTask)
	{
		Integer v = oldTask;
		Random r = new Random();
		Integer newV = v;
		Task t = null;
		while(newV == v)
		{
			
			t = (Task)this.getDomain().get(r.nextInt(this.getDomain().size()));
			newV = t.getId();
		}
		this.assign(t);
		
	}
	
	public void getAnotherValue()
	{
		this.getAnotherValue(this.getValue());
	}

	public Integer getOldValue() {
		return oldValue;
	}

	public void setOldValue(Integer oldValue) {
		this.oldValue = oldValue;
	}

	public Double getValueProbability() {
		return valueProbability;
	}
	
	public void setValueProbability(double d)
	{
		this.valueProbability = d;
	}

	public ArrayList<Double> getUtilityDomain() {
		return utilityDomain;
	}

	public void setUtilityDomain(ArrayList<Double> utilityDomain) {
		this.utilityDomain = utilityDomain;
	}

	public Double getUtilityAction() {
		return utilityAction;
	}

	public void setUtilityAction(Double utilityAction) {
		this.utilityAction = utilityAction;
	}

	

}
