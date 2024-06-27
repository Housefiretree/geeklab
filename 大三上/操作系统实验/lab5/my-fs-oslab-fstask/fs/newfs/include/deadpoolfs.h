#ifndef _DEADPOOLFS_H_
#define _DEADPOOLFS_H_

#define FUSE_USE_VERSION 26
#include "stdio.h"
#include "stdlib.h"
#include <unistd.h>
#include "fcntl.h"
#include "string.h"
#include "fuse.h"
#include <stddef.h>
#include "ddriver.h"
#include "errno.h"
#include "types.h"

#define DEADPOOLFS_MAGIC           8888    /* TODO: Define by yourself 定义幻数 */
#define DEADPOOLFS_DEFAULT_PERM    0777   /* 全权限打开 */

/******************************************************************************
* SECTION: macro debug
*******************************************************************************/
#define DPS_DBG(fmt, ...) do { printf("DPS_DBG: " fmt, ##__VA_ARGS__); } while(0) 

/******************************************************************************
* SECTION: deadpoolfs_utils.c
*******************************************************************************/
char* 			   deadpoolfs_get_fname(const char* path);
int 			   deadpoolfss_calc_lvl(const char * path);
int 			   deadpoolfs_driver_read(int offset, uint8_t *out_content, int size);
int 			   deadpoolfs_driver_write(int offset, uint8_t *in_content, int size);


int 			   deadpoolfs_mount(struct custom_options options);
int 			   deadpoolfs_umount();

int 			   deadpoolfs_alloc_dentry(struct deadpoolfs_inode * inode, struct deadpoolfs_dentry * dentry);
int 			   deadpoolfs_drop_dentry(struct deadpoolfs_inode * inode, struct deadpoolfs_dentry * dentry);
struct deadpoolfs_inode*  deadpoolfs_alloc_inode(struct deadpoolfs_dentry * dentry);
int 			   deadpoolfs_alloc_data(struct deadpoolfs_inode * inode);
int 			   deadpoolfs_sync_inode(struct deadpoolfs_inode * inode);
int 			   deadpoolfs_drop_inode(struct deadpoolfs_inode * inode);
struct deadpoolfs_inode*  deadpoolfs_read_inode(struct deadpoolfs_dentry * dentry, int ino);
struct deadpoolfs_dentry* deadpoolfs_get_dentry(struct deadpoolfs_inode * inode, int dir);

struct deadpoolfs_dentry* deadpoolfs_lookup(const char * path, boolean * is_find, boolean* is_root);

/******************************************************************************
* SECTION: deadpoolfs.c
*******************************************************************************/
void* 			   deadpoolfs_init(struct fuse_conn_info *);
void  			   deadpoolfs_destroy(void *);
int   			   deadpoolfs_mkdir(const char *, mode_t);
int   			   deadpoolfs_getattr(const char *, struct stat *);
int   			   deadpoolfs_readdir(const char *, void *, fuse_fill_dir_t, off_t,
						                struct fuse_file_info *);
int   			   deadpoolfs_mknod(const char *, mode_t, dev_t);
int   			   deadpoolfs_write(const char *, const char *, size_t, off_t,
					                  struct fuse_file_info *);
int   			   deadpoolfs_read(const char *, char *, size_t, off_t,
					                 struct fuse_file_info *);
int   			   deadpoolfs_access(const char *, int);
int   			   deadpoolfs_unlink(const char *);
int   			   deadpoolfs_rmdir(const char *);
int   			   deadpoolfs_rename(const char *, const char *);
int   			   deadpoolfs_utimens(const char *, const struct timespec tv[2]);
int   			   deadpoolfs_truncate(const char *, off_t);
			
int   			   deadpoolfs_open(const char *, struct fuse_file_info *);
int   			   deadpoolfs_opendir(const char *, struct fuse_file_info *);

#endif  /* _deadpoolfs_H_ */