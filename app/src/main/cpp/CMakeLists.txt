# ${ANDROID_ABI} 构建的COU
# ${CMAKE_SOURCE_DIR} cmakeList.txt所在的文件夹路径

# cmake的最低版本
cmake_minimum_required(VERSION 3.4.1)

# 为了确保 CMake 可以在编译时定位头文件
# 这样就可以使用 #include <xx.h> 引入
# 否则需要使用 #include "path/xx"
# 头文件
include_directories(include)

# 源文件 最终生成的 so 多文件设置
# 编译头文件
file(GLOB my_source_path ${CMAKE_SOURCE_DIR}/*.cpp ${CMAKE_SOURCE_DIR}/*.c)
add_library(OpenCV SHARED ${my_source_path})

# 设置静态库以导入的形式加入到项目中来
# 添加动态库
add_library(lib_opencv SHARED IMPORTED)
# 设置 lib_opencv 动态库的真实地址
set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libopencv_java4.so)


#设置动态依赖库   会从 ndk 中的 platforms/android-21/arch-arm/usr/lib/ 中查找log库 并赋值给log_lib
# find_library(log-lib log)
#
# find_library(jnigraphics-lib jnigraphics)

# 设置依赖的库
# 如果是静态库，需要根据 add_library中设置的变量名B去查找
# 如果是动态库,需要根据  CMAKE_CXX_FLAGS 改变后的路径下去按照库名查找
# target_link_libraries( 要生成的库A  依赖的库B  ${log-lib} )
target_link_libraries(
        OpenCV
        log # 日志
        jnigraphics # 图像
        lib_opencv
)