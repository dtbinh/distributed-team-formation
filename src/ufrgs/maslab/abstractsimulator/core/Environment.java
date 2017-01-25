package ufrgs.maslab.abstractsimulator.core;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public abstract class Environment<E extends Entity> extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8L;
	
	public static int time = 0;
	
	/**
	 * 
	 * <ul>
	 * <li>value class</li>
	 * </ul>
	 * 
	 */
	private Map<Integer, Class<Value>> valClass = new HashMap<Integer, Class<Value>>();
	//private Map<Class<Value>,Integer> valClass = new HashMap<Class<Value>, Integer>();
	
	/**
	 * <ul>
	 * <li>variable class</li>
	 * </ul>
	 * 
	 */
	private HashMap<Class<Variable>, Integer> varClass = new HashMap<Class<Variable>,Integer>();
	
	/**
	 * <ul>
	 * <li>Set of variables of the environment</li>
	 * </ul>
	 * 
	 */
	private ArrayList<Variable> variableSet = new ArrayList<Variable>();
	
	/**
	 * <ul>
	 * <li>Set of values of the environment</li>
	 * </ul>
	 */
	private ArrayList<Value> valueSet = new ArrayList<Value>();
	
	/**
	 *  <ul>
	 *  <li>Map of allocation of variables and values in a given turn<li>
	 *  <li>Map Structure <Value Id, ArrayList<Variable Id>></li>
	 *  <ul>
	 */
	private Map<Integer, ArrayList<Integer>> allocationSet = new HashMap<Integer, ArrayList<Integer>>();
	
	/**
	 * <ul>
	 * <li>Id of the solved values at current time</li>
	 * </ul>
	 */
	private ArrayList<Value> solvedValues = new ArrayList<Value>();
	
	/**
	 * <ul>
	 * <li>List of values that reached the deadline</li>
	 * </ul>
	 */
	private ArrayList<Value> unsolvedValue = new ArrayList<Value>();
	
	/**
	 * <ul>
	 * 	<li>set of removed values</li>
	 * 	<li>all removed values are here</li>
	 * </ul>
	 */
	private ArrayList<Value> removedValues = new ArrayList<Value>();
		
	/**
	 *  overrides entity constructor
	 */
	public Environment()
	{
		super();
		//EnvironmentLogger.saveHeader();
		
	}
	
	/**
	 * overrides entity constructor
	 * @param id
	 */
	public Environment(Integer id)
	{
		super(id);
		//EnvironmentLogger.saveHeader();
		//HumanLogger.saveHeader();
		//FireBuildingTaskLogger.saveHeader();
	}
	
	/**
	 * <ul>
	 * <li>returns the set of variables of the environment</li>
	 * </ul>
	 * 
	 * @return ArrayList<var extends Variable>
	 */
	public ArrayList<Variable> getVariables(){
		
		/*if(this.getVarClass() == null)
			if(!this.variableSet.isEmpty())
				this.setVarClass(this.variableSet.iterator().next().getClass());*/
		return this.variableSet;
	}
	
	/**
	 * <ul>
	 * <li>returns the set of values of the environment</li>
	 * </ul>
	 * 
	 * @return ArrayList<val extends Value>
	 */
	public ArrayList<Value> getValues(){
		/*if(this.getValClass() == null)
			if(!this.valueSet.isEmpty())
				this.setValClass(this.valueSet.iterator().next().getClass());*/
		return this.valueSet;
	}
	
	/**
	 * <ul>
	 * <li>returns the list of ids of values that was solved by agents</li>
	 * </ul>
	 * 
	 * @return ArrayList<Integer>
	 */
	public ArrayList<Value> getSolvedValues(){
		return this.solvedValues;
	}
	
	/**
	 * <ul>
	 * <li>returns the list of ids of values that was unsolved</li>
	 * </ul>
	 * 
	 * @return ArrayList<Integer>
	 */
	public ArrayList<Value> getRemovedValues(){
		return this.removedValues;
	}

	/**
	 * <ul>
	 * <li>returns the map with ids of variables assigned to ids of values</li>
	 * <li>value id is keys</li>
	 * <li>list of variables id are the values of the map</li>
	 * </ul>
	 * 
	 * @return Map<Integer, Array<Integer>>
	 */
	public Map<Integer, ArrayList<Integer>> getAllocation() {
		return allocationSet;
	}

	/**
	 * <ul>
	 * <li>sets a map of values ids and variables ids</li>
	 * </ul>
	 * 
	 * @param allocation Map<Integer, ArrayList<Integer>>
	 */
	protected void setAllocation(Map<Integer, ArrayList<Integer>> allocation) {
		this.allocationSet = allocation;
	}
	
	
	/**
	 * <ul>
	 * <li>saves current allocation to the log file</li>
	 * <li>clear current allocation</li>
	 * <li>assigns agents to the new allocation</li>
	 * <li>O(2 agents) complexity</li>
	 * </ul>
	 * @param time
	 */
	public void allocateVariables(int time){
		this.getAllocation().clear();
		Iterator<Variable> var = this.getVariables().iterator();
		while(var.hasNext())
		{
			Variable v = var.next();
			this.allocateVariable(v.getValue(), v.getId());
		}
	}
	
	/**
	 * <ul>
	 * <li>assign one agent to one value</li>
	 * <li>if value wasn't mapped, creates the value mapping and assign variable</li>
	 * </ul>
	 * 
	 * @param value id which the agent will be assigned to
	 * @param variable id which will be assigned to some value
	 */
	private void allocateVariable(Integer value, Integer variable)
	{
		if(this.getAllocation().containsKey(value)){
			if(!this.getAllocation().get(value).contains(variable)){
				this.getAllocation().get(value).add(variable);
			}
		}else{
			this.getAllocation().put(value, new ArrayList<Integer>());
			this.getAllocation().get(value).add(variable);
		}
	}
	
	
	
	/**
	 * <ul>
	 * <li>deallocate variable</li>
	 * </ul>
	 * @param value
	 * @param variable
	 */
	public void deallocateVariable(Integer value, Integer variable)
	{
		if(this.getAllocation().containsKey(value)){
			this.getAllocation().get(value).remove(variable);
		}
		
	}
	
	/**
	 * <ul>
	 * <li></li>
	 * </ul>
	 * @param variable
	 * @param value
	 * @return Boolean
	 */
	/**
	private Boolean verifyAllocationPersistence(Integer variable, Integer value){
		
		//id da variavel
		Integer idxVar = this.getVariables().indexOf(this.findVariableByID(variable));
		var agent = this.getVariables().get(idxVar);
		//old value of the variable
		Integer oldVal = null;
		if(agent.isAllocated())
		{
			oldVal = agent.getValue();
		}
		
		if(this.getAllocation().containsKey(value))
			if(this.getAllocation().get(value).contains(variable))
				return false;
		
		
		
		return true;
	}*/
	
	/**
	 * <ul>
	 * <li>returns the object Value specified by the parameter idx</li>
	 * </ul>
	 * @param idx
	 * @return
	 */
	public Value findValueByID(int idx, Class<? extends Value> clazz){
		Value v = null;
		try {
			v = clazz.getConstructor(Integer.class).newInstance(idx);
			//v = this.valClass.getConstructor(Integer.class).newInstance(idx);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(this.getValues().contains(v))
		{
			int idxVal = this.getValues().indexOf(v);
			v = null;
			
			return this.getValues().get(idxVal);		
		}
		
		return null;
	}
	
	/**
	 * <ul>
	 * <li>returns the object variable specified by the parameters idx</li>
	 * </ul>
	 * @param idx
	 * @return
	 */
	public Variable findVariableByID(int idx, Class<? extends Variable> clazz){
		
		Variable v = null;
		try {
			v = clazz.getConstructor(Integer.class).newInstance(idx);
			//v = this.getVarClass().getConstructor(Integer.class).newInstance(idx);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int idxVar = this.getVariables().indexOf(v);
		v = null;
		return this.getVariables().get(idxVar);
	}

	public Map<Integer,Class<Value>> getValClass() {
		return valClass;
	}

	/**
	 * <ul>
	 * <li>function used to register value (tasks) components to the environment</li>
	 * </ul>
	 * 
	 * @param valClass Class of the value component
	 * @param ammount Ammount of the value component
	 */
	@SuppressWarnings("unchecked")
	public void registerValue(Integer id, Class<? extends Value> valClass) {
		this.valClass.put(id, (Class<Value>)valClass);
	}

	public HashMap<Class<Variable>,Integer> getVarClass() {
		return varClass;
	}

	
	public abstract void registerVariable(Class<? extends Variable> varClass, Integer ammount);

	public ArrayList<Value> getUnsolvedValue() {
		return unsolvedValue;
	}

	public void setUnsolvedValue(ArrayList<Value> unsolvedValue) {
		this.unsolvedValue = unsolvedValue;
	}
	
	
	/*
	protected Class<? extends Value> getValClass() {
		return valClass;
	}

	protected void setValClass(Class<? extends Value> valClass) {
		this.valClass = valClass;
	}

	protected Class<? extends Variable> getVarClass() {
		return varClass;
	}

	protected void setVarClass(Class<? extends Variable> varClass) {
		this.varClass = varClass;
	}*/


}
