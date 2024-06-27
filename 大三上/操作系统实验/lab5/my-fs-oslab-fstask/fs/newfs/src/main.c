/**
#define FUSE_USE_VERSION 26
#include <stdio.h>
#include <fuse.h>

//这里实现了一个遍历目录的功能，当用户在目录执行ls时，会回调到该函数，我们这里只是返回一个固定的文件Hello-FUSE。 
static int test_readdir(const char* path, void* buf, fuse_fill_dir_t filler, off_t offset, struct fuse_file_info* fi)
{

    return filler(buf, "Hello-FUSE", NULL, 0);
}

// 显示文件属性 
static int test_getattr(const char* path, struct stat *stbuf)
{
    if(strcmp(path, "/") == 0)
        stbuf->st_mode = 0755 | S_IFDIR;            // 说明该文件是目录文件
    else
        stbuf->st_mode = 0644 | S_IFREG;            // 说明该文件是普通文件
    return 0;
}

//这里是回调函数集合，这里实现的很简单
static struct fuse_operations ops = {
  .readdir = test_readdir,
  .getattr = test_getattr,
};

int main(int argc, char *argv[])
{
    int ret = 0;
    ret = fuse_main(argc, argv, &ops, NULL);
    return ret;
}*/