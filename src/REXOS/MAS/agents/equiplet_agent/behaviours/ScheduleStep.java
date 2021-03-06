/**
 * @file rexos/mas/equiplet_agent/behaviours/ScheduleStep.java
 * @brief Behaviour for handling the messages with the ontology ScheduleStep
 * @date Created: 2013-04-02
 * 
 * @author Wouter Veen
 * 
 * @section LICENSE
 *          License: newBSD
 * 
 *          Copyright � 2013, HU University of Applied Sciences Utrecht.
 *          All rights reserved.
 * 
 *          Redistribution and use in source and binary forms, with or without modification, are permitted provided that
 *          the following conditions are met:
 *          - Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *          following disclaimer.
 *          - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *          following disclaimer in the documentation and/or other materials provided with the distribution.
 *          - Neither the name of the HU University of Applied Sciences Utrecht nor the names of its contributors may be
 *          used to endorse or promote products derived from this software without specific prior written permission.
 * 
 *          THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *          "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *          THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *          ARE DISCLAIMED. IN NO EVENT SHALL THE HU UNIVERSITY OF APPLIED SCIENCES UTRECHT
 *          BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *          CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 *          GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 *          HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *          LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *          OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package agents.equiplet_agent.behaviours;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.List;

import libraries.blackboard_client.BlackboardClient;
import libraries.blackboard_client.data_classes.GeneralMongoException;
import libraries.blackboard_client.data_classes.InvalidDBNamespaceException;
import libraries.utillities.log.LogLevel;
import libraries.utillities.log.Logger;

import org.bson.types.ObjectId;

import agents.data_classes.ProductStep;
import agents.equiplet_agent.EquipletAgent;
import agents.shared_behaviours.ReceiveBehaviour;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

/**
 * Receive behaviour for receiving messages with the ontology: "ScheduleStep".
 * When it receives this message, it checks if it is possible to do plan the step on the given timeslot.
 * Sends an <code>ACLMessage.DISCONFIRM</code> when it is not possible. When its possible it sends a message
 * to the service agent with ontology: "ScheduleStep". and updates the blackboard.
 */
public class ScheduleStep extends ReceiveBehaviour {
	/**
	 * @var static final long serialVersionUID
	 *      The serial version UID for this class
	 */
	private static final long serialVersionUID = -3574738583814321426L;

	/**
	 * @var MessageTemplate MESSAGE_TEMPLATE
	 *      The messageTemplate this behaviour listens to. This behaviour
	 *      listens to the ontology: ScheduleStep.
	 */
	private static MessageTemplate MESSAGE_TEMPLATE = MessageTemplate.MatchOntology("ScheduleStep");

	/**
	 * @var EquipletAgent equipletAgent
	 *      The equipletAgent related to this behaviour.
	 */
	private EquipletAgent equipletAgent;

	/**
	 * @var BlackboardClient productStepsBlackboard
	 *      The productStepsBlackboard for this behaviour.
	 */
	private BlackboardClient productStepsBlackboard;

	/**
	 * Instantiates a new schedule step.
	 * 
	 * @param equipletAgent
	 *      The equipletAgent for this behaviour
	 * @param productStepsBlackboard
	 * 		The blackboardClient for the productStepsBlackboard.
	 */
	public ScheduleStep(EquipletAgent equipletAgent, BlackboardClient productStepsBlackboard) {
		super(equipletAgent, MESSAGE_TEMPLATE);
		this.equipletAgent = equipletAgent;
		this.productStepsBlackboard = productStepsBlackboard;
	}

	/**
	 * Function to handle the incoming messages for this behaviour. Handles the response to the ScheduleStep question
	 * and asks the service agent to schedule.
	 * 
	 * @param message
	 *            - The received message.
	 */
	@Override
	public void handle(ACLMessage message) 
	{
		try 
		{
			// Gets the timeslot out of the message content.
			long start = (Long) message.getContentObject();
			Logger.log(LogLevel.INFORMATION, "Equiplet agent received schedulestep request. Trying to schedule ts: %d%n", start);

			// Gets the scheduledata out of the productstep.
			ObjectId productStepId = equipletAgent.getRelatedObjectId(message.getConversationId());
			
	//		ScheduleData scheduleData =
	//				new ScheduleData((BasicDBObject) (productStepsBlackboard.findDocumentById(productStepId)).get("scheduleData"));
			
	//		long end = start + scheduleData.getDuration();
	//		Logger.log(LogLevel.DEBUG, "start: " + start + " duration: " + scheduleData.getDuration() + " end: " + end);

			// Gets planned steps
			List<DBObject> plannedSteps =
					productStepsBlackboard.findDocuments(QueryBuilder.start("scheduleData.startTime").greaterThan(-1).get());

			boolean fitsInSchedule = true;

			Logger.log(LogLevel.DEBUG, "I currently have " + plannedSteps.size() + " planned steps.");
			// check if other steps not are scheduled.
			for(DBObject plannedStep : plannedSteps) {
				ProductStep productStep = new ProductStep((BasicDBObject) plannedStep);
			//	ScheduleData stepScheduleData = productStep.getScheduleData();

			/*	long scheduledStepStart = stepScheduleData.getStartTime();
				long scheduledStepEnd = scheduledStepStart + stepScheduleData.getDuration();

				if(start >= scheduledStepStart && start <= scheduledStepEnd) 
				{
					Logger.log(LogLevel.ERROR, "FitInSchedule is false!\nstart: " + start + " scheduledStepStart: " + scheduledStepStart +
							"\nend: " + end + " scheduledStepEnd: " +  scheduledStepEnd + " \nend - start: " + (end - start) + 
							"\nscheduledEnd - scheduledStart " + (scheduledStepEnd - scheduledStepStart) +
							"\nend - scheduledStepEnd: " + (end - scheduledStepEnd) + 
							"\nstart - scheduledStepStart: " + (start - scheduledStepStart));
					fitsInSchedule = false;
				} 
				else if(end >= scheduledStepStart && end <= scheduledStepEnd) 
				{
					Logger.log(LogLevel.ERROR, "FitInSchedule is false!\nstart: " + start + " scheduledStepStart: " + scheduledStepStart +
							"\nend: " + end + " scheduledStepEnd: " +  scheduledStepEnd + " \nend - start: " + (end - start) + 
							"\nscheduledEnd - scheduledStart " + (scheduledStepEnd - scheduledStepStart) +
							"\nend - scheduledStepEnd: " + (end - scheduledStepEnd) + 
							"\nstart - scheduledStepStart: " + (start - scheduledStepStart));
					fitsInSchedule = false;
				} 
				else if(start <= scheduledStepStart && end >= scheduledStepEnd) 
				{
					Logger.log(LogLevel.ERROR, "FitInSchedule is false!\nstart: " + start + " scheduledStepStart: " + scheduledStepStart +
							"\nend: " + end + " scheduledStepEnd: " +  scheduledStepEnd + " \nend - start: " + (end - start) + 
							"\nscheduledEnd - scheduledStart " + (scheduledStepEnd - scheduledStepStart) +
							"\nend - scheduledStepEnd: " + (end - scheduledStepEnd) + 
							"\nstart - scheduledStepStart: " + (start - scheduledStepStart));
					fitsInSchedule = false;
				}*/
				
			}
			
			if(fitsInSchedule) {
	//			scheduleData.setStartTime(start);
	//			productStepsBlackboard.updateDocuments(new BasicDBObject("_id", productStepId), new BasicDBObject("$set",
	//					new BasicDBObject("scheduleData", scheduleData.toBasicDBObject())));

				ACLMessage scheduleMessage = new ACLMessage(ACLMessage.REQUEST);
				scheduleMessage.addReceiver(equipletAgent.getServiceAgent());
				scheduleMessage.setOntology("ScheduleStep");
				scheduleMessage.setContentObject(productStepId);
				scheduleMessage.setConversationId(message.getConversationId());
				equipletAgent.send(scheduleMessage);
				Logger.log(LogLevel.DEBUG, "You fit in my schedule, msg accepted send.");
			}
			else 
			{
				Logger.log(LogLevel.ERROR, "ScheduleStep disconfirm");
				ACLMessage reply = message.createReply();
				reply.setPerformative(ACLMessage.DISCONFIRM);
				myAgent.send(reply);
			}
		}
		catch(IOException | InvalidDBNamespaceException | GeneralMongoException | UnreadableException e) 
		{
			Logger.log(LogLevel.ERROR, "", e);
			myAgent.doDelete();
		}
	}
}
