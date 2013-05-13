/**
 * @file DeltaRobotModule.java
 * @brief Provides a deltaRobotModule.
 * @date Created: 12-04-13
 *
 * @author Thierry Gerritse
 * 
 * @section LICENSE
 * License: newBSD
 *
 * Copyright � 2013, HU University of Applied Sciences Utrecht.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * - Neither the name of the HU University of Applied Sciences Utrecht nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE HU UNIVERSITY OF APPLIED SCIENCES UTRECHT
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package rexos.mas.hardware_agent;

import java.util.ArrayList;

import org.bson.BasicBSONObject;

import rexos.mas.data.Position;
import rexos.mas.equiplet_agent.StepStatusCode;

import com.mongodb.BasicDBObject;

public class DeltaRobotModule implements Module {
	double safeMovementPlane = 6;
	
	public DeltaRobotModule(){
	}

	@Override
	public EquipletStepMessage[] getEquipletSteps(int stepType,BasicDBObject parameters) {
		EquipletStepMessage[] equipletSteps;
		ArrayList<EquipletStepMessage> steps;
		
		switch(stepType){
		
		case 1: //Move to savePlane
			steps = new ArrayList<EquipletStepMessage>();
			steps.add(moveToSafePlane(parameters));
			equipletSteps = new EquipletStepMessage[steps.size()];
			return steps.toArray(equipletSteps);
		case 2: //Move
			steps = new ArrayList<EquipletStepMessage>();
			steps.add(moveToSafePlane(parameters));
			steps.add(new EquipletStepMessage(null, new InstructionData(),
					StepStatusCode.EVALUATING, new TimeData(3)));//to desired position(x, y)
			steps.add(new EquipletStepMessage(null, new InstructionData(),
					StepStatusCode.EVALUATING, new TimeData(3)));//down to desired position(z)
			equipletSteps = new EquipletStepMessage[steps.size()];
			return steps.toArray(equipletSteps);
		default:
			break;
		}
		return new EquipletStepMessage[0];
	}

	@Override
	public int[] isLeadingForSteps() {
		return null;
	}
	
	private EquipletStepMessage moveToSafePlane(BasicDBObject parameters){
		double extraSize = parameters.getDouble("extraSize");
		
		BasicDBObject lookUpParameters = new BasicDBObject("ID",
				((BasicBSONObject) parameters.get("postion")).get("relativeToPart"));
		BasicDBObject payload = new BasicDBObject("z", extraSize + safeMovementPlane);
		InstructionData instructionData = new InstructionData("move", "deltarobot", "FIND_ID",
				lookUpParameters, payload);
		EquipletStepMessage step = new EquipletStepMessage(null, instructionData,
				StepStatusCode.EVALUATING, new TimeData(4));
		return step;
	}
	
	private EquipletStepMessage move(BasicDBObject parameters){
		double extraSize = parameters.getDouble("extraSize");
		
		Position position = new Position((BasicDBObject)parameters.get("position"));
		
		BasicDBObject lookUpParameters = new BasicDBObject("ID", position.getRelativeToPart());
		BasicDBObject payload = new BasicDBObject("z", extraSize + position.getZ());
		payload.put("x", position.getX());
		payload.put("y", position.getY());
		InstructionData instructionData = new InstructionData("move", "deltarobot", "FIND_ID",
				lookUpParameters, payload);
		EquipletStepMessage step = new EquipletStepMessage(null, instructionData,
				StepStatusCode.EVALUATING, new TimeData(4));
		return step;
	}
}