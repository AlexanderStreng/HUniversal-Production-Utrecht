cmake_minimum_required(VERSION 2.8.3)
project(stewart_gough_simulation_node)

## Find catkin and any catkin packages
find_package(catkin REQUIRED COMPONENTS roscpp std_msgs std_msgs message_generation gazebo_ros rexos_gazebo)

find_package(gazebo REQUIRED)
  	
file(GLOB_RECURSE srvs RELATIVE "${CMAKE_CURRENT_SOURCE_DIR}/srv" "*.srv")


add_service_files(
FILES rotate.srv
)

generate_messages (
	DEPENDENCIES std_msgs
)

## Declare a catkin package
catkin_package(
INCLUDE_DIRS include 
LIBRARIES  
CATKIN_DEPENDS roscpp message_runtime rexos_gazebo
DEPENDS 
gazebo_ros
)


link_directories(${GAZEBO_LIBRARY_DIRS})

file(GLOB_RECURSE sources "src" "*.cpp" "*.c")
include_directories(include ${catkin_INCLUDE_DIRS} ${GAZEBO_LIBRARIES})
add_executable(stewart_gough_simulation_node ${sources})
target_link_libraries(stewart_gough_simulation_node ${catkin_LIBRARIES})
add_dependencies(stewart_gough_simulation_node stewart_gough_simulation_node_gencpp)



