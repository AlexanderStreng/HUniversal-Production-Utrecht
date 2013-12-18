/**                                     ______  _______   __ _____  _____
 *                  ...++,              | ___ \|  ___\ \ / /|  _  |/  ___|
 *                .+MM9WMMN.M,          | |_/ /| |__  \ V / | | | |\ `--.
 *              .&MMMm..dM# dMMr        |    / |  __| /   \ | | | | `--. \
 *            MMMMMMMMMMMM%.MMMN        | |\ \ | |___/ /^\ \\ \_/ //\__/ /
 *           .MMMMMMM#=`.gNMMMMM.       \_| \_|\____/\/   \/ \___/ \____/
 *             7HMM9`   .MMMMMM#`		
 *                     ...MMMMMF .      
 *         dN.       .jMN, TMMM`.MM     	@file 	Capability.java
 *         .MN.      MMMMM;  ?^ ,THM		@brief 	Dataclass for the representation of the capabilities that an equiplet can hold
 *          dM@      dMMM3  .ga...g,    	@date Created:	2013-12-17
 *       ..MMM#      ,MMr  .MMMMMMMMr   
 *     .dMMMM@`       TMMp   ?TMMMMMN   	@author	Roy Scheefhals
 *   .dMMMMMF           7Y=d9  dMMMMMr    	@author Tommas Bakker
 *  .MMMMMMF        JMMm.?T!   JMMMMM#		
 *  MMMMMMM!       .MMMML .MMMMMMMMMM#  	@section LICENSE
 *  MMMMMM@        dMMMMM, ?MMMMMMMMMF    	License:	newBSD
 *  MMMMMMN,      .MMMMMMF .MMMMMMMM#`    	
 *  JMMMMMMMm.    MMMMMM#!.MMMMMMMMM'.		Copyright � 2013, HU University of Applied Sciences Utrecht. 
 *   WMMMMMMMMNNN,.TMMM@ .MMMMMMMM#`.M  	All rights reserved.
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

package simulation.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import simulation.CSVReader;


public class Capability {
	private int id;
	private String name;
	
	private static HashMap<Integer, Capability> availableCapabilities = new HashMap<Integer, Capability>();
	
	public static void loadCapabilities(String capabilitiesFilePath) {
		String[][] fields = CSVReader.parseCsvFile(capabilitiesFilePath);
		parseCapabilitiesCSV(fields);
	}
	
	public Capability(int id, String name){
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public static void parseCapabilitiesCSV(String[][] fields){
		try{
			for(int i = 0; i < fields.length; i++) {
				int capabilityId = Integer.parseInt(fields[i][0]);
				String capabiityName = fields[i][1];
				
				availableCapabilities.put(capabilityId, new Capability(capabilityId, capabiityName));
			}
		} catch(NumberFormatException e){
			System.err.println("Could not parse capabilties");
			e.printStackTrace();
		} catch(NoSuchElementException e1){
			System.err.println("wrong amount of elements when parsing capabilities");
			e1.printStackTrace();
		}
		printAvailableCapabilities();
	}
	
	public static Capability getAvailableCapabilitiesById(int id){
		return availableCapabilities.get(id);
	}
	private static void printAvailableCapabilities(){
		Collection<Capability> values = availableCapabilities.values();
		for ( Capability c1 : values){
			System.out.println("id: " + c1.getId() + ", name: " + c1.getName());
		}
	}
}