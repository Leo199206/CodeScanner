###以下为编译不同的so库，默认只执行最后一句，把你需要的版本配置放到最后一句即可

###同时编译armeabi，armeabi-v7a版本##
APP_ABI := armeabi-v7a arm64-v8a x86 x86_64
###只编译armeabi-v7a版本###
APP_ABI := armeabi-v7a
###编译所有版本###
APP_ABI := all
