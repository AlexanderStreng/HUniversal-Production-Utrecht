/**                                     ______  _______   __ _____  _____
 *                  ...++,              | ___ \|  ___\ \ / /|  _  |/  ___|
 *                .+MM9WMMN.M,          | |_/ /| |__  \ V / | | | |\ `--.
 *              .&MMMm..dM# dMMr        |    / |  __| /   \ | | | | `--. \
 *            MMMMMMMMMMMM%.MMMN        | |\ \ | |___/ /^\ \\ \_/ //\__/ /
 *           .MMMMMMM#=`.gNMMMMM.       \_| \_|\____/\/   \/ \___/ \____/
 *             7HMM9`   .MMMMMM#`		
 *                     ...MMMMMF .      
 *         dN.       .jMN, TMMM`.MM     	@file 	ProgressWorkerThread.java
 *         .MN.      MMMMM;  ?^ ,THM		@brief 	...
 *          dM@      dMMM3  .ga...g,    	@date Created:	2013-12-18
 *       ..MMM#      ,MMr  .MMMMMMMMr   
 *     .dMMMM@`       TMMp   ?TMMMMMN   	@author	Alexander Hustinx
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
package simulation.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import simulation.data.Capability;
import simulation.data.TimeSlot;
import simulation.mas_entities.Equiplet;
import simulation.mas_entities.Equiplet.EquipletState;
import simulation.mas_entities.Product.FailureReason;
import simulation.mas_entities.Product;

class ProgressWorkerThread extends SwingWorker<Void, Void> {
	
	public static final int UPDATE_INTERVAL = 100;
	
	private MainGUI mG;
	private JComponent[] components;
	DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	double previousProgress = 0;
	long previousTime;
	
	public ProgressWorkerThread(MainGUI mG){
		super();
		this.mG = mG;
		components = mG.getProgressComponents();
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		System.out.println("[DEBUG]\t\tCreated ProgressWorkerThread");
	}
	
	@Override
	public Void doInBackground() {
		previousTime = System.currentTimeMillis();
		while(!isCancelled()){
			//@ TODO Update progressBar with Sim-data
			Date date = new Date();
			
			
			double currentProgress = mG.getSimulation().getProgress();
			double progressIncrement = currentProgress - previousProgress;
			long currentTime = System.currentTimeMillis();
			if(progressIncrement != 0.0) {
				int milliesRemaining = (int)(((1.0 - currentProgress) / progressIncrement) * (currentTime - previousTime));
				date.setTime(milliesRemaining);
				((JTextField) 	components[1]).setText(dateFormat.format(date));
				
			}
			previousProgress = currentProgress;
			previousTime = currentTime;
			((JProgressBar) components[0]).setValue((int)(currentProgress * 100));
			mG.setProgressComponents(components);
			
			mG.capabilitiesText.setText(	String.valueOf(mG.getSimulation().getCurrentSimulationTime() - mG.getSimulation().getStartSimulationTime()));
			mG.productsText.setText(		String.valueOf(TimeSlot.getCurrentTimeSlot(mG.getSimulation(), mG.getGrid().getGridProperties())));
			mG.gridText.setText("");
			
			if(mG.getSimulation().getIsFinished() == true) {
				System.out.println("FINISHED SiM");
				try{
				String path = "";
				
				FileWriter productsInGridFile = new FileWriter(path + "productsInGrid.csv");
				PrintWriter productsInGridWriter = new PrintWriter(productsInGridFile);
				HashMap<Long, Integer> productsInProgres = mG.productDataCollector.productInProgres;
				Long[] keys1 = productsInProgres.keySet().toArray(new Long[productsInProgres.size()]);
				Arrays.sort(keys1);
				for (Long key : keys1) {
					productsInGridWriter.println(productsInProgres.get(key));
				}
				productsInGridWriter.close();
				productsInGridFile.close();
				
				FileWriter productsFile = new FileWriter(path + "products.csv");
				PrintWriter productsWriter = new PrintWriter(productsFile);
				HashMap<Long, Integer> products = mG.productDataCollector.products;
				Long[] keys2 = products.keySet().toArray(new Long[products.size()]);
				Arrays.sort(keys2);
				for (Long key : keys2) {
					productsWriter.println(products.get(key));
				}
				productsWriter.close();
				productsFile.close();
				
				// does the FailureReason.values() change?
				FailureReason[] failureReasonTypes = FailureReason.values();
				
				FileWriter productsFailedFile = new FileWriter(path + "productsFailed.csv");
				PrintWriter productsFailedWriter = new PrintWriter(productsFailedFile);
				HashMap<Long, HashMap<FailureReason, Integer>> productsFailed = mG.productDataCollector.productsOverDeadline;
				Long[] keys3 = productsFailed.keySet().toArray(new Long[productsFailed.size()]);
				Arrays.sort(keys3);
				for (FailureReason failureReason : failureReasonTypes) {
					productsFailedWriter.print(failureReason + "\t");
				}
				productsFailedWriter.println();
				for (Long key : keys3) {
					for (FailureReason failureReason : failureReasonTypes) {
						productsFailedWriter.print(productsFailed.get(key).get(failureReason) + "\t");
					}
					productsFailedWriter.println();
				}
				productsFailedWriter.close();
				productsFailedFile.close();
				
				FileWriter productsInBatchFailedFile = new FileWriter(path + "productsInBatchFailed.csv");
				PrintWriter productsInBatchFailedWriter = new PrintWriter(productsInBatchFailedFile);
				HashMap<Long, HashMap<FailureReason, Integer>> productsInBatchFailed = mG.productDataCollector.productsInBatchOverDeadline;
				Long[] keys4 = productsInBatchFailed.keySet().toArray(new Long[productsInBatchFailed.size()]);
				Arrays.sort(keys4);
				for (FailureReason failureReason : failureReasonTypes) {
					productsInBatchFailedWriter.print(failureReason + "\t");
				}
				productsInBatchFailedWriter.println();
				for (Long key : keys4) {
					for (FailureReason failureReason : failureReasonTypes) {
						productsInBatchFailedWriter.print(productsInBatchFailed.get(key).get(failureReason) + "\t");
					}
					productsInBatchFailedWriter.println();
				}
				productsInBatchFailedWriter.close();
				productsInBatchFailedFile.close();
				
				
				
				FileWriter equipletLoadFile = new FileWriter(path + "equipletLoad.csv");
				PrintWriter equipletLoadWriter = new PrintWriter(equipletLoadFile);
				HashMap<Long, HashMap<Equiplet, Double>> equipletLoads = mG.equipletDataCollector.getEquipletLoads();
				Long[] keys5 = equipletLoads.keySet().toArray(new Long[equipletLoads.size()]);
				Arrays.sort(keys5);
				for (Equiplet eq : equipletLoads.get(keys5[0]).keySet()) {
					equipletLoadWriter.print(eq.getName() + "\t");
				}
				equipletLoadWriter.println();
				for (Long key : keys5) {
					for (Double load : equipletLoads.get(key).values()) {
						equipletLoadWriter.print(load + "\t");
					}
					equipletLoadWriter.println();
				}
				equipletLoadWriter.close();
				equipletLoadFile.close();
				
				FileWriter equipletCurrentLoadFile = new FileWriter(path + "equipletCurrentLoad.csv");
				PrintWriter equipletCurrentLoadWriter = new PrintWriter(equipletCurrentLoadFile);
				HashMap<Long, HashMap<Equiplet, Double>> equipletCurrentLoads = mG.equipletDataCollector.getEquipletCurrentLoads();
				Long[] keys6 = equipletCurrentLoads.keySet().toArray(new Long[equipletCurrentLoads.size()]);
				Arrays.sort(keys6);
				for (Equiplet eq : equipletCurrentLoads.get(keys6[0]).keySet()) {
					equipletCurrentLoadWriter.print(eq.getName() + "\t");
				}
				equipletCurrentLoadWriter.println();
				for (Long key : keys6) {
					for (Double load : equipletCurrentLoads.get(key).values()) {
						equipletCurrentLoadWriter.print(load + "\t");
					}
					equipletCurrentLoadWriter.println();
				}
				equipletCurrentLoadWriter.close();
				equipletCurrentLoadFile.close();
				
				FileWriter equipletStateFile = new FileWriter(path + "equipletState.csv");
				PrintWriter equipletStateWriter = new PrintWriter(equipletStateFile);
				HashMap<Long, HashMap<Equiplet, EquipletState>> equipletState = mG.equipletDataCollector.getEquipletState();
				Long[] keys7 = equipletState.keySet().toArray(new Long[equipletState.size()]);
				Arrays.sort(keys7);
				for (Equiplet eq : equipletState.get(keys7[0]).keySet()) {
					equipletStateWriter.print(eq.getName() + "\t");
				}
				equipletStateWriter.println();
				for (Long key : keys7) {
					for (EquipletState state : equipletState.get(key).values()) {
						equipletStateWriter.print(state + "\t");
					}
					equipletStateWriter.println();
				}
				equipletStateWriter.close();
				equipletStateFile.close();
				
				
				System.out.println("totalDurationInTimeSlots " + mG.dps1.totalDurationInTimeSlots);
				
				} catch(Exception ex) {
					// tralala
				}
				
				this.cancel(false);
			}
			
			try {
				Thread.sleep(UPDATE_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	protected void done(){
		System.out.println("[DEBUG]\t\tCancelled ProgressWorkerThread");
	}
}

