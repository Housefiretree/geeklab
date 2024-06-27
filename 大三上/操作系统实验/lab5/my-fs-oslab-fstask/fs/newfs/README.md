# 基于FUSE的青春版EXT2文件系统

by 210010101 房煊梓

## 概述

本次实验的内容为基于FUSE开发一个青春版EXT2文件系统。

## 参考资料

1.https://hitsz-cslab.gitee.io/os-labs/
2.simplefs

## 环境配置

**实验环境**
- Ubuntu 22.04
- VSCode 1.84.2
- cmake  3.22.1
- gcc    11.4.0
- gdb    12.0


**相关插件**
- CMake
- CMake Tools
- C/C++ 

## FUSE操作

**挂载文件系统**

直接按F5键，或者输入以下命令：
```shell
./build/deadpoolfs --device=/home/students/210010101/ddriver -f -d -s ./tests/mnt/
```

**卸载文件系统**
```shell
fusermount -u ./tests/mnt/
```

**创建目录**
```shell
mkdir <父目录路径/目录名>
```

**创建文件**
```shell
touch <父目录路径/文件名>
```

**查看目录和文件**

查看目录：
```shell
ls <父目录路径/目录名>
```
查看文件：
```shell
ls <父目录路径/文件名>
```

## 运行测评程序

在终端进入tests文件夹，运行如下命令：
```shell
chmod +x test.sh && ./test.sh
```

## 附录 （NEWFS实现的操作）

- [x] mount
- [x] mkdir
- [x] touch
- [x] ls
- [x] umount