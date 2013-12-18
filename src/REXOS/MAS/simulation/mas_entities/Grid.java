/**                                     ______  _______   __ _____  _____
 *                  ...++,              | ___ \|  ___\ \ / /|  _  |/  ___|
 *                .+MM9WMMN.M,          | |_/ /| |__  \ V / | | | |\ `--.
 *              .&MMMm..dM# dMMr        |    / |  __| /   \ | | | | `--. \
 *            MMMMMMMMMMMM%.MMMN        | |\ \ | |___/ /^\ \\ \_/ //\__/ /
 *           .MMMMMMM#=`.gNMMMMM.       \_| \_|\____/\/   \/ \___/ \____/
 *             7HMM9`   .MMMMMM#`		
 *                     ...MMMMMF .      
 *         dN.       .jMN, TMMM`.MM     	@file 	Grid.java
 *         .MN.      MMMMM;  ?^ ,THM		@brief 	...
 *          dM@      dMMM3  .ga...g,    	@date Created:	2013-12-17
 *       ..MMM#      ,MMr  .MMMMMMMMr   
 *     .dMMMM@`       TMMp   ?TMMMMMN   	@author	Tommas Bakker
 *   .dMMMMMF           7Y=d9  dMMMMMr    
 *  .MMMMMMF        JMMm.?T!   JMMMMM#		@section LICENSE
 *  MMMMMMM!       .MMMML .MMMMMMMMMM#  	License:	newBSD
 *  MMMMMM@        dMMMMM, ?MMMMMMMMMF    
 *  MMMMMMN,      .MMMMMMF .MMMMMMMM#`    	Copyright � 2013, HU University of Applied Sciences Utrecht. 
 *  JMMMMMMMm.    MMMMMM#!.MMMMMMMMM'.		All rights reserved.
 *   WMMMMMMMMNNN,.TMMM@ .MMMMMMMM#`.M  
 *    JMMMMMMMMMMMN,?MD  TYYYYYYY= dM     
 *                                        
 *	Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *	- Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *	- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *	- Neither the name of the HU University of Applied Sciences Utrecht nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *   "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *   THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *   ARE DISCLAIMED. IN NO EVENT SHALL THE HU UNIVERSITY OF APPLIED SCIENCES UTRECHT
 *   BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *   CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 *   GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 *   HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *   LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *   OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/

package simulation.mas_entities;

import java.io.File;
import java.io.Reader;
import java.util.Date;

import agents.data_classes.Matrix;
import simulation.CSVReader;
import simulation.Updateable;
import simulation.data.Capability;


public class Grid implements Updateable{
	private static final double defaultDistance = 1.0;
	
	private Equiplet[][] equiplets;
	
	private Matrix distanceMatrix;
	
	public Grid(Equiplet[][] equiplets) {
		this.equiplets = equiplets;
		distanceMatrix = new Matrix(equiplets.length * equiplets[0].length, equiplets.length * equiplets[0].length);
	}
	public Grid(String equipletLayoutCsvFilePath) {
		String[][] fields = CSVReader.parseCsvFile(equipletLayoutCsvFilePath);
		
		distanceMatrix = new Matrix(fields.length * fields[0].length, fields.length * fields[0].length);
		initializeDistanceMatrix();
		
		equiplets = new Equiplet[fields.length][fields[0].length];
		try {
			for(int i = 0; i < fields.length; i++) {
				for(int j = 0; j < fields[i].length; j++) {
					int capabilityId = Integer.parseInt(fields[i][j].trim());
					Capability cap = Capability.getAvailableCapabilitiesById(capabilityId);
					// TODO: Allow for more than 1 capability
					equiplets[i][j] = new Equiplet(new Capability[] {cap});
				}
			}
		} catch (NumberFormatException ex) {
			System.err.println("equipletLayoutCsv has an entry which could not be converted to int");
			throw ex;
		}
	}
	private void initializeDistanceMatrix() {
		for(int i = 0; i < distanceMatrix.getNumberOfRows(); i++) {
			int sourceEquipletY = i / equiplets.length; 
			int sourceEquipletX = i % equiplets.length;
			for(int j = 0; j < distanceMatrix.getNumberOfColumns(); j++) {
				int targetEquipletY = j / equiplets.length; 
				int targetEquipletX = j % equiplets.length;
				
				double distance = 
						Math.abs(targetEquipletY - sourceEquipletY) * defaultDistance + 
						Math.abs(targetEquipletX - sourceEquipletX) * defaultDistance;
				distanceMatrix.set(i, j, distance);
			}
		}
	}
	
	public Matrix getDistanceMatrix(){
		return distanceMatrix;
	}
	
	@Override
	public void update(Date time) {
		for(int i = 0; i < equiplets.length; i++) {
			for(int j = 0; j < equiplets[i].length; j++) {
				equiplets[i][j].update(time);
			}
		}
		
	}
}