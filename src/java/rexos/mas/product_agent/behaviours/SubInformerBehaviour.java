package rexos.mas.product_agent.behaviours;

import java.io.IOException;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import rexos.mas.behaviours.ReceiveBehaviour;
import rexos.mas.data.LogLevel;
import rexos.mas.data.ProductionStep;
import rexos.mas.data.BehaviourStatus;
import rexos.utillities.log.Logger;

public class SubInformerBehaviour extends ReceiveBehaviour {

	
	private static final long serialVersionUID = 1L;

	private AID _targetEquiplet;
	private long _timeslotDuration;
	private ProductionStep _productionStep;
	private InformerBehaviour _parentBehaviour;
	private String _conversationId;

	private static int SUBINFORMER_TIMEOUT = 10000;

	private int _currentState = 0;

	/**
	 * Constructs SubInformerbehavior
	 * @param myAgent
	 * @param parentBehaviour
	 * @param productionStep
	 * @param targetEquiplet
	 */
	public SubInformerBehaviour(Agent myAgent,
			InformerBehaviour parentBehaviour, ProductionStep productionStep,
			AID targetEquiplet) {
		super(
				myAgent,
				SUBINFORMER_TIMEOUT,
				MessageTemplate
						.and(MessageTemplate.MatchConversationId(productionStep
								.getConversationIdForEquiplet(targetEquiplet)),
								MessageTemplate.or(
										MessageTemplate
												.MatchOntology("CanPerformStep"),
										MessageTemplate
												.MatchOntology("ProductionDuration"))));
		_parentBehaviour = parentBehaviour;
		_productionStep = productionStep;
		_targetEquiplet = targetEquiplet;
		_conversationId = _productionStep
				.getConversationIdForEquiplet(_targetEquiplet);
	}

	/**
	 * Starts the sub informer behavior
	 */
	@Override
	public void onStart() {
		super.onStart();
		try {
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.setConversationId(_conversationId);
			message.addReceiver(_targetEquiplet);
			message.setOntology("CanPerformStep");
			message.setContentObject(_productionStep);
			myAgent.send(message);
			Logger.logAclMessage(message, 's');
		} catch (IOException e) {
			Logger.log(LogLevel.ERROR, e);
		}
	}

	/**
	 * Handles the messages which the behavior receives
	 * @param message
	 */
	@Override
	public void handle(ACLMessage message) {
		
		try {
			if (message != null) {
				Logger.logAclMessage(message, 'r');
				switch (_currentState) {
				case 0:
					if (message.getPerformative() == ACLMessage.CONFIRM) 
					{
						ACLMessage newMessage = new ACLMessage(
								ACLMessage.REQUEST);
						newMessage.setConversationId(_conversationId);
						newMessage.addReceiver(_targetEquiplet);
						newMessage.setOntology("GetProductionDuration");
						newMessage.setContentObject(_productionStep);
						myAgent.send(newMessage);
						Logger.logAclMessage(newMessage, 's');
						_currentState++;
					} 
					else 
					{
						Logger.log(LogLevel.ERROR, "Received something different than Confirm.");
						_parentBehaviour.callbackSubInformerBehaviour(
								BehaviourStatus.ERROR, this);
					}
					break;
				case 1:
					if (message.getPerformative() == ACLMessage.INFORM) 
					{
						this._timeslotDuration = (Long) message.getContentObject();
						_parentBehaviour.callbackSubInformerBehaviour(BehaviourStatus.COMPLETED, this);
					} 
					else 
					{
						Logger.log(LogLevel.ERROR, "Received something different than Inform.");
						_parentBehaviour.callbackSubInformerBehaviour(BehaviourStatus.ERROR, this);
					}
					break;
				default:
					break;
				}
			} else {
				Logger.log(LogLevel.ERROR, "Message can't be null!");
				_parentBehaviour.callbackSubInformerBehaviour(BehaviourStatus.ERROR, this);
			}
		} catch (IOException | UnreadableException e) {
			Logger.log(LogLevel.ERROR, e);
			_parentBehaviour.callbackSubInformerBehaviour(BehaviourStatus.ERROR, this);
		}
	}

	/**
	 * Get time slot duration
	 * @return
	 */
	public long getTimeslotDuration() {
		return _timeslotDuration;
	}

	/**
	 * Gets the target AID of the equiplet which needs to perform the current production step
	 * @return
	 */
	public AID getTargetEquiplet() {
		return _targetEquiplet;
	}

	/**
	 * Gets the unique production step ID
	 * @return
	 */
	public int getProductionStepId() {
		return _productionStep.getId();
	}

}