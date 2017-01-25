package ufrgs.maslab.abstractsimulator.algorithms.model.beeclustering.algorithm;

import java.util.ArrayList;

import ufrgs.maslab.abstractsimulator.algorithms.model.beeclustering.Bee;
import ufrgs.maslab.abstractsimulator.algorithms.model.beeclustering.BeeClusterEvaluator;
import ufrgs.maslab.abstractsimulator.algorithms.model.beeclustering.BeeClusteringEnvironment;
import ufrgs.maslab.abstractsimulator.algorithms.model.beeclustering.NectarSource;
import ufrgs.maslab.abstractsimulator.algorithms.model.beeclustering.UtilityEvaluator;
import ufrgs.maslab.abstractsimulator.core.BlackBox;
import ufrgs.maslab.abstractsimulator.core.Entity;
import ufrgs.maslab.abstractsimulator.core.Environment;
import ufrgs.maslab.abstractsimulator.core.Value;
import ufrgs.maslab.abstractsimulator.core.simulators.DefaultSimulation;
import ufrgs.maslab.abstractsimulator.exception.SimulatorException;
import ufrgs.maslab.abstractsimulator.util.Transmitter;
import ufrgs.maslab.abstractsimulator.util.WriteFile;
import ufrgs.maslab.abstractsimulator.values.Task;

public class BeeClustering extends DefaultSimulation {


	private String logFileName = Transmitter.getProperty("files.properties", "algorithm.log")+"_"+BlackBox.getAlgorithmName()+"_"+BlackBox.getAlgorithmRun();
	
	private Boolean debug = Transmitter.getBooleanConfigParameter("config.properties", "config.debug");
	
	private String exceptionFile = "exception.properties"; 
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void simulate(Entity entity) throws SimulatorException {
		
		if(!(entity instanceof BeeClusteringEnvironment))
			throw new SimulatorException(Transmitter.getProperty(this.exceptionFile, "exception.not.beeclustering"));
		
		BeeClusteringEnvironment<Entity> env = (BeeClusteringEnvironment<Entity>)entity;
		
		//erase messages
		/*for(Variable v: env.getVariables())
		{
			if(v instanceof Human)
				((Human)v).getEar().clear();
			if(v instanceof Agent)
				((Agent)v).getRadioMessage().clear();
					
		}*/
		ArrayList<Value> tmp = (ArrayList<Value>) env.getSolvedValues().clone();
		for(Value v : tmp)
		{
			if(NectarSource.getNectarSource(v.getId()) != null){
				NectarSource.removeNectarSource(v.getId());
				if(this.debug)
				{
					String step1 = "action:;remove;nectar:;"+v.getId()+";message:;Searching values in solved list to remove. Nectar Source "+v.getId()+" removed.";
					//String step1 = "worker agent "+x.getId()+" send message with the value "+qTmp+" to factor "+f.getId();

					WriteFile.getInstance().openFile(this.logFileName);
					WriteFile.getInstance().write(step1, this.logFileName);
				}
			}
		}
		
		ArrayList<Value> tmpUnsolved = (ArrayList<Value>) env.getUnsolvedValue().clone();
		for(Value v : tmpUnsolved)
		{
			if(NectarSource.getNectarSource(v.getId()) != null)
			{
				NectarSource.removeNectarSource(v.getId());
				if(this.debug)
				{
					String step1 = "action:;remove;nectar:;"+v.getId()+";message:;Searching values in unsolved list to remove. Nectar Source "+v.getId()+" was unsolved and it was removed.";
					//String step1 = "worker agent "+x.getId()+" send message with the value "+qTmp+" to factor "+f.getId();

					WriteFile.getInstance().openFile(this.logFileName);
					WriteFile.getInstance().write(step1, this.logFileName);
				}
			}
		}
		
		env.getRemovedValues().addAll(env.getSolvedValues());
		env.getRemovedValues().addAll(env.getUnsolvedValue());
		env.getSolvedValues().clear();
		env.getUnsolvedValue().clear();
		
		for(Bee i : env.beeSet)
		{
			
			if(Environment.time <= 0 && i.agent.getValue() != null)
			{
				if(this.debug)
				{
					String step1 = "Initializing. Bee "+i.getId();

					WriteFile.getInstance().openFile(this.logFileName);
					WriteFile.getInstance().write(step1, this.logFileName);
				}
				i.setState("v");
				i.setLastUtility(i.getAbandonProbability());
				Value v = env.findValueByID(i.agent.getValue(), env.getValClass().get(i.agent.getValue()));
				if(this.debug)
				{
					String step1 = "bee:;"+i.getId()+";state:;visiting;nectar:;"+v.getId();
					//String step1 = "worker agent "+x.getId()+" send message with the value "+qTmp+" to factor "+f.getId();

					WriteFile.getInstance().openFile(this.logFileName);
					WriteFile.getInstance().write(step1, this.logFileName);
				}
				
				NectarSource.putNectarSource(i.agent.getValue(), v, new UtilityEvaluator());
				NectarSource.getNectarSource(i.agent.getValue()).getFunctionEvaluator().addParameter(i);
				if(this.debug)
				{
					String step1 = "find:;nectar:;"+i.agent.getValue()+";message:;finding nectar source "+i.agent.getValue();
					WriteFile.getInstance().openFile(this.logFileName);
					WriteFile.getInstance().write(step1, this.logFileName);
				}
				
			}else if(i.agent.getValue() != null){
				if(!NectarSource.table.containsKey(i.agent.getValue()))
				{
					Value v = env.findValueByID(i.agent.getValue(), env.getValClass().get(i.agent.getValue()));
					NectarSource.putNectarSource(i.agent.getValue(), v, new UtilityEvaluator());
					NectarSource.getNectarSource(i.agent.getValue()).getFunctionEvaluator().addParameter(i);
				}
				//if visiting
				if(i.getState() == "v")
				{
					if(i.abandon())
					{
						i.setState("w");
						//remove bee from the group
						if(NectarSource.getNectarSource(i.agent.getValue()) != null)
							NectarSource.getNectarSource(i.agent.getValue()).removeBee(i);
						
						if(NectarSource.getNectarSource(i.agent.getValue()).getFunctionEvaluator().getParameters().isEmpty())
						{
							NectarSource.removeNectarSource(i.agent.getValue());
							if(this.debug)
							{
								String step1 = "removing;nectar_source:;"+i.agent.getValue();
								WriteFile.getInstance().openFile(this.logFileName);
								WriteFile.getInstance().write(step1, this.logFileName);
							}
						}
						//old value
						i.agent.setOldValue(i.agent.getValue());
						i.agent.assign(null);
						
						if(this.debug)
						{
							String step1 = "bee:;"+i.getId()+";action:;watching;nectar:;"+i.agent.getOldValue()+";message:;bee "+i.getId()+" stopped to visit nectar source "+i.agent.getOldValue()+" and goes to watch.";
							WriteFile.getInstance().openFile(this.logFileName);
							WriteFile.getInstance().write(step1, this.logFileName);
							
							String step2 = "bee:;"+i.getId()+";abandon_rate:;"+i.agent.getUtilityAction();
							WriteFile.getInstance().openFile(this.logFileName);
							WriteFile.getInstance().write(step2, this.logFileName);
						}
						
					}else{
						double uc = 0d;
						double uc_i = 0d;
						
						if(NectarSource.getNectarSource(i.agent.getValue()) == null){
							Value v = env.findValueByID(i.agent.getValue(), env.getValClass().get(i.agent.getValue()));
							NectarSource.putNectarSource(i.agent.getValue(), v, new UtilityEvaluator());
							
							NectarSource.getNectarSource(i.agent.getValue()).getFunctionEvaluator().addParameter(i);
						}
						
						uc = NectarSource.getNectarSource(i.agent.getValue()).getFunctionEvaluator().utilityCluster();
						uc_i = NectarSource.getNectarSource(i.agent.getValue()).getFunctionEvaluator().utilityClusterWithout(i);
						
						if(uc < uc_i){
							
							if(NectarSource.getNectarSource(i.agent.getValue()).getDancingBee().contains(i) )
								NectarSource.getNectarSource(i.agent.getValue()).removeBee(i);
							
							i.agent.getAnotherValue(i.agent.getValue());
							
							if(NectarSource.getNectarSource(i.agent.getValue()) == null){
								Value v = env.findValueByID(i.agent.getValue(), env.getValClass().get(i.agent.getValue()));
								NectarSource.putNectarSource(i.agent.getValue(), v, new UtilityEvaluator());
								
								NectarSource.getNectarSource(i.agent.getValue()).getFunctionEvaluator().addParameter(i);
								NectarSource.getNectarSource(i.agent.getValue()).addDancingBee(i);
							}else{
								NectarSource.getNectarSource(i.agent.getValue()).addDancingBee(i);
							}
							
							if(this.debug)
							{
								String step1 = "bee:;"+i.getId()+";action:;change;from_nectar:;"+i.agent.getOldValue()+";to_nectar:;"+i.agent.getValue()+";message:;bee "+i.getId()+" changes to the group "+i.agent.getValue()+" and it is dancing.;";
								WriteFile.getInstance().openFile(this.logFileName);
								WriteFile.getInstance().write(step1, this.logFileName);
								
								String step2= "utility_with_you;"+uc+";utility_with_out_you:;"+uc_i;
								WriteFile.getInstance().openFile(this.logFileName);
								WriteFile.getInstance().write(step2, this.logFileName);
							}
						}else{
							
							NectarSource.getNectarSource(i.agent.getValue()).addDancingBee(i);
							
							if(this.debug)
							{
								String step1 = "bee:;"+i.getId()+";action:;dancing;nectar:;"+i.agent.getValue()+";message:;bee "+i.getId()+" feels that it is usefull and frantically dance for nectar "+i.agent.getValue();
								WriteFile.getInstance().openFile(this.logFileName);
								WriteFile.getInstance().write(step1, this.logFileName);
								
								String step2= "utility_with_you;"+uc+";utility_with_out_you:;"+uc_i;
								WriteFile.getInstance().openFile(this.logFileName);
								WriteFile.getInstance().write(step2, this.logFileName);
							}
						}
						i.setState("d");
						
					}
				
				//if dancing
				}else if(i.getState() == "d")
				{
					double u = NectarSource.getNectarSource(i.agent.getValue()).getFunctionEvaluator().utilityCluster();
					
					i.updateStimulusAndThreshold(u, env.getAlpha());
					
					if(this.debug)
					{
						String step1 = "bee:;"+i.getId()+";current_action;dancing;update_stimulus_to:;"+i.getStimulus()+""
								+ ";update_threshold_to:;"+i.getThreshold()
								+ ";message:;bee "+i.getId()+" is feeling the vibe.";
						WriteFile.getInstance().openFile(this.logFileName);
						WriteFile.getInstance().write(step1, this.logFileName);
					}
					
					if(!i.dancing()){
						i.setState("v");
						NectarSource.getNectarSource(i.agent.getValue()).getDancingBee().remove(i);
						if(this.debug)
						{
							String step1 = "bee:;"+i.getId()+";current_action;visit;message:;bee "+i.getId()+" is visiting now.";
							WriteFile.getInstance().openFile(this.logFileName);
							WriteFile.getInstance().write(step1, this.logFileName);
						}
					}
					
				//if watching
				}else if(i.getState() == "w")
				{
					
					
					boolean t = false;
					while(t == false)
					{
						for(NectarSource n : NectarSource.table.values())
						{
							if(n.getFunctionEvaluator().getParameters().size() > 0){
								//System.out.println("nectar "+n.toString());
								//System.out.println("total "+n.getFunctionEvaluator().getParameters().size());
								//System.out.println("dancing "+n.getDancingBee().size());
							}
							if(n.getDancingBee().size() > 0)
							{
								if(i.agent.getValue() != null && n.getTask().getId() != i.agent.getValue())
								{
									if(n.visit())
									{
										if(n.getTask() instanceof Task)
										{
											i.agent.assign((Task)n.getTask());
											n.getFunctionEvaluator().addParameter(i);
											i.setState("v");
											if(this.debug)
											{
												String step1 = "bee:;"+i.getId()+";old_action;watching;new_action:;visiting;nectar_source:;"+i.agent.getValue();
												WriteFile.getInstance().openFile(this.logFileName);
												WriteFile.getInstance().write(step1, this.logFileName);
											}
										}
										t = true;
										break;
									}
								}
							}else{
								i.setState("d");
								i.agent.assign((Task)n.getTask());
								n.getFunctionEvaluator().addParameter(i);
								if(this.debug)
								{
									String step1 = "bee:;"+i.getId()+";new_action;dancing;nectar_source:;"+i.agent.getValue();
									WriteFile.getInstance().openFile(this.logFileName);
									WriteFile.getInstance().write(step1, this.logFileName);
								}
								t = true;
								break;
							}
						}
					}
					
				}
				
			}
			
			if(this.debug)
				BeeClusterEvaluator.measureBehaviour(i);
		}
		
		

		
		
	}

	@Override
	public void simulate(Entity... entity) throws SimulatorException {
		// TODO Auto-generated method stub
		
	}


}
