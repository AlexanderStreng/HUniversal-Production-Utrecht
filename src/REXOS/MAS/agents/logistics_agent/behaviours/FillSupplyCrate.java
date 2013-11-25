package agents.logistics_agent.behaviours;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import agents.logistics_agent.LogisticsAgent;
import agents.shared_behaviours.ReceiveBehaviour;

public class FillSupplyCrate extends ReceiveBehaviour {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1639907611674316437L;

	private static final MessageTemplate MESSAGE_TEMPLATE = MessageTemplate.MatchOntology("FillSupplyCrate");
	
	private LogisticsAgent logisticsAgent;
	
	public FillSupplyCrate(LogisticsAgent logisticsAgent){
		super(logisticsAgent, MESSAGE_TEMPLATE);
		this.logisticsAgent = logisticsAgent;
	}
	
	@Override
	public void handle(ACLMessage message) {
		logisticsAgent.fillSupplyCrate();
	}

}
