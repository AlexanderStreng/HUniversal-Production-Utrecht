/**
 * @file EffectorBoundaries.h
 * @brief Represents the effector's moving volume.
 *
 * @author 1.0 Lukas Vermond
 * @author 1.0 Kasper van Nieuwland
 * @author 1.1 Koen Braham
 * @author 1.1 Daan Veltman
 *
 * @section LICENSE
 * License: newBSD
 *  
 * Copyright © 2012, HU University of Applied Sciences Utrecht.
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

#pragma once

#include <iostream>
#include <DataTypes/Point3D.h>
#include <Motor/StepperMotor.h>
#include <DeltaRobot/Measures.h>
#include <DeltaRobot/InverseKinematicsModel.h>
#include <vector>

namespace DeltaRobot{
	/**
	 * This class represents a delta robot's effector work field.
	 * This work field is stored as a 3D bitmap (bool array).
	 * Every "pixel" in this map is called a voxel.
	 **/
	class EffectorBoundaries{
	public:
		~EffectorBoundaries();

		// TODO: change Motor::StepperMotor to two doubles for max and min angle
		static EffectorBoundaries* generateEffectorBoundaries(const InverseKinematicsModel& model,  Motor::StepperMotor* (&motors)[3], double voxelSize);
		
		bool checkPath(const DataTypes::Point3D<double>& from, const DataTypes::Point3D<double>& to) const;
		
		/**
		 * Gets the boundariesBitmap.
		 *
		 * @return A const bool pointer to the voxel bitmap.
		 **/
		inline const bool* getBitmap() const{ return boundariesBitmap; }
		
		/**
		 * Gets the depth used to make the boundaries bitmap.
		 *
		 * @return The depth in voxels.
		 **/
		inline int getDepth() const{ return depth; }

		/**
		 * Gets the height used to make the boundaries bitmap.
		 *
		 * @return The height in voxels.
		 **/
		inline int getHeight() const{ return height; }
		
		/**
		 * Gets the width used to make the boundaries bitmap.
		 *
		 * @return The width in voxels.
		 **/
		inline int getWidth() const{ return width; }

		/**
		 * Gets the size of a side of a voxel.
		 *
		 * @return Voxel size in mm.
		 **/
		inline double getVoxelSize() const{ return voxelSize; }

	private:
		EffectorBoundaries(const InverseKinematicsModel& model,  Motor::StepperMotor* (&motors)[3], double voxelSize);

		/**
		 * Represents a 3-dimensional point in the 3D voxel array.
		 **/
		typedef struct BitmapCoordinate{
			int x, y, z;
			BitmapCoordinate(int x, int y, int z) : x(x), y(y), z(z){}
		} BitmapCoordinate;

		bool hasInvalidNeighbours(const BitmapCoordinate& coordinate, char* pointValidityCache) const;
		bool isValid(const BitmapCoordinate& coordinate, char* pointValidityCache) const;
		void generateBoundariesBitmap();

		/**
		 * Converts a bitmap coordinate to a real life coordinate.
		 * 
		 * @param coordinate The bitmap coordinate.
		 **/
		inline DataTypes::Point3D<double> fromBitmapCoordinate(EffectorBoundaries::BitmapCoordinate coordinate) const{
			return DataTypes::Point3D<double>(
					(double) coordinate.x * voxelSize + Measures::BOUNDARY_BOX_MIN_X,
					(double) coordinate.y * voxelSize + Measures::BOUNDARY_BOX_MIN_Y,
					(double) coordinate.z * voxelSize + Measures::BOUNDARY_BOX_MIN_Z);
		}

		/**
		 * Converts a real life coordinate to a bitmap coordinate.
		 * 
		 * @param coordinate The real life coordinate.
		 **/
		inline EffectorBoundaries::BitmapCoordinate fromRealCoordinate(DataTypes::Point3D<double> coordinate) const{
			return EffectorBoundaries::BitmapCoordinate(
				(coordinate.x - Measures::BOUNDARY_BOX_MIN_X) / voxelSize,
				(coordinate.y - Measures::BOUNDARY_BOX_MIN_Y) / voxelSize,
				(coordinate.z - Measures::BOUNDARY_BOX_MIN_Z) / voxelSize);
		}

		enum cacheEntry{
			UNKNOWN,
			VALID,
			INVALID
		};

		/**
		 * @var int width
		 * The width of the boundary bitmap.
		 **/
		int width;
		
		/**
		 * @var int height
		 * The height of the boundary bitmap.
		 **/
		int height; 

		/**
		 * @var int depth
		 * The depth of the boundary bitmap.
		 **/
		int depth;

		/**
		 * @var bool* boundariesBitmap
		 * A pointer to the boundaries bitmap.
		 **/
		bool* boundariesBitmap;

		/**
		 * @var InverseKinematicsModel& kinematics
		 * A reference to the InverseKinematicsModel of the deltarobot, which is used to calculate the boundaries.
		 **/
		const InverseKinematicsModel &kinematics;

		/**
		 * @var StepperMotor* motors
		 * An array holding pointers to the three StepperMotors that are connected to the DeltaRobot. This array HAS to be of size 3. The EffectorBoundaries needs to know the motors to find out their max and min angles.
		 **/
		Motor::StepperMotor* (&motors)[3];

		/**
		 * @var double voxelSize
		 * The size of the voxels in the boundary bitmap.
		 **/
		double voxelSize;
	};
}
