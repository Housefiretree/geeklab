#ifndef _TYPES_H_
#define _TYPES_H_

#define MAX_NAME_LEN    128     

typedef int          boolean;

struct custom_options {
	const char*        device;
};

typedef enum file_type {
    REG_FILE,           // 普通文件
    DIR                 // 目录文件
    //相比sfs删去了SYM_LINK
} FILE_TYPE;


/******************************************************************************
* SECTION: Macro
*******************************************************************************/
#define TRUE                    1
#define FALSE                   0
#define UINT32_BITS             32
#define UINT8_BITS              8

#define DPS_SUPER_OFS           0
#define DPS_ROOT_INO            0

//定义SEEK_SET
#define SEEK_SET                0

#define DPS_ERROR_NONE          0
#define DPS_ERROR_ACCESS        EACCES
#define DPS_ERROR_SEEK          ESPIPE     
#define DPS_ERROR_ISDIR         EISDIR
#define DPS_ERROR_NOSPACE       ENOSPC
#define DPS_ERROR_EXISTS        EEXIST
#define DPS_ERROR_NOTFOUND      ENOENT
#define DPS_ERROR_UNSUPPORTED   ENXIO
#define DPS_ERROR_IO            EIO     /* Error Input/Output */
#define DPS_ERROR_INVAL         EINVAL  /* Invalid Args */

#define DPS_INODE_PER_FILE      1
#define DPS_DATA_PER_FILE       6       //每个文件最多6个逻辑块
#define DPS_DEFAULT_PERM        0777

/******************************************************************************
* SECTION: Macro Function
*******************************************************************************/
#define DPS_IO_SZ()                     (deadpoolfs_super.sz_io)
#define DPS_DISK_SZ()                   (deadpoolfs_super.sz_disk)
#define DPS_DRIVER()                    (deadpoolfs_super.fd)
#define DPS_BLK_SZ()                    (deadpoolfs_super.blks_size)

#define DPS_ROUND_DOWN(value, round)    (value % round == 0 ? value : (value / round) * round)
#define DPS_ROUND_UP(value, round)      (value % round == 0 ? value : (value / round + 1) * round)

#define DPS_BLKS_SZ(blks)               (blks * DPS_BLK_SZ())   //注意每个逻辑块不再是一个IO的大小     
#define DPS_ASSIGN_FNAME(pdps_dentry, _fname)  memcpy(pdps_dentry->name, _fname, strlen(_fname))
//计算索引节点和数据区的办法与原来不同
#define DPS_INO_OFS(ino)                (deadpoolfs_super.ino_offset+ino*DPS_BLK_SZ())
#define DPS_DATA_OFS(data)              (deadpoolfs_super.data_offset+data*DPS_BLK_SZ())

#define DPS_IS_DIR(pinode)              (pinode->dentry->ftype == DIR)
#define DPS_IS_REG(pinode)              (pinode->dentry->ftype == REG_FILE)

/******************************************************************************
* SECTION: DPS Specific Structure - In memory structure
*******************************************************************************/
struct deadpoolfs_dentry;
struct deadpoolfs_inode;
struct deadpoolfs_super;
struct deadpoolfs_super {
    /* TODO: Define yourself 定义超级块的数据结构*/
    int      fd;            // 设备handler

    int sz_io;              // io大小
    int sz_disk;            // 磁盘大小
    int sz_usage;           // 使用大小

    /*逻辑块信息*/
    int blks_size;          // 逻辑块大小
    int blks_nums;          // 逻辑块数

    /*以下为磁盘布局分区信息*/
    /*超级块信息*/
    int sb_offset;          // 超级块于磁盘中的偏移，通常默认为0
    int sb_blks;            // 超级块于磁盘中的块数，通常默认为1

    /*索引节点位图信息*/
    uint8_t* ino_map;       // 索引节点位图
    int ino_map_offset;     // 索引节点位图于磁盘中的偏移
    int ino_map_blks;       // 索引节点位图于磁盘中的块数

    /*数据块位图信息*/
    uint8_t* data_map;      // 数据块位图
    int data_map_offset;    // 数据块位图于磁盘中的偏移
    int data_map_blks;      // 数据块位图于磁盘中的块数

    /*索引节点区信息*/
    int ino_offset;         // 索引节点区于磁盘中的偏移
    int ino_blks;           // 索引节点区于磁盘中的块数

    /*数据块区信息*/
    int data_offset;        // 数据块区于磁盘中的偏移
    int data_blks;          // 数据块区于磁盘中的块数

    /* 支持的限制 */
    int ino_max;            // 最大支持inode数
    int file_max;           // 支持文件最大大小

    /* 根目录索引 */
    int root_ino;           // 根目录对应的inode

    /* 其他信息 */
    boolean is_mounted;     // 是否已经挂载
    struct deadpoolfs_dentry* root_dentry;  //根目录项

};

struct deadpoolfs_inode {
    /* TODO: Define yourself 定义索引节点的数据结构*/
    /* inode编号 */
    uint32_t ino;                   // 在inode位图中的下标

    /* 文件的属性 */
    int size;                       // 文件已占用空间
    int link;                       // 链接数，默认为1
    FILE_TYPE ftype;                // 文件类型（目录类型、普通文件类型）
    int data_blk_num;               // 文件已经占用的数据块数

    /* 数据块的索引 */
    uint8_t* block_address[DPS_DATA_PER_FILE];      // 数据块地址       
    int block_pointer[DPS_DATA_PER_FILE];           // 数据块指针(可固定分配)

    struct deadpoolfs_dentry* dentry;           //指向该inode的dentry
    struct deadpoolfs_dentry* first_child;      //指向第一个子dentry

    /* 其他字段 */
    int dir_cnt;                    // 如果是目录类型文件，下面有几个目录项
};

struct deadpoolfs_dentry {
    /* TODO: Define yourself 定义目录项的数据结构*/
    /* 文件名称 */
    char     name[MAX_NAME_LEN];
    /* inode编号 */
    uint32_t ino;
    /* 文件类型 */
    FILE_TYPE ftype;
    /* 其他字段 */
    struct deadpoolfs_dentry* parent;   //父dentry
    struct deadpoolfs_dentry* brother;  //兄弟dentry
    struct deadpoolfs_inode* inode;     //对应的inode
};

//新建目录项
static inline struct deadpoolfs_dentry* new_dentry(char * fname, FILE_TYPE ftype) {
    struct deadpoolfs_dentry * dentry = (struct deadpoolfs_dentry *)malloc(sizeof(struct deadpoolfs_dentry));
    memset(dentry, 0, sizeof(struct deadpoolfs_dentry));
    DPS_ASSIGN_FNAME(dentry, fname);
    dentry->ftype   = ftype;
    dentry->ino     = -1;
    dentry->inode   = NULL;
    dentry->parent  = NULL;
    dentry->brother = NULL;   
    return dentry;                                         
}


/******************************************************************************
* SECTION: DPS Specific Structure - Disk structure
*******************************************************************************/
struct deadpoolfs_super_d {
    /* TODO: Define yourself 定义超级块的数据结构*/
    uint32_t magic;         // 幻数magic
    int sz_usage;           // 使用大小

    /*逻辑块信息*/
    int blks_size;          // 逻辑块大小
    int blks_nums;          // 逻辑块数

    /*以下为磁盘布局分区信息*/
    /*超级块信息*/
    int sb_offset;          // 超级块于磁盘中的偏移，通常默认为0
    int sb_blks;            // 超级块于磁盘中的块数，通常默认为1

    /*索引节点位图信息*/
    int ino_map_offset;     // 索引节点位图于磁盘中的偏移
    int ino_map_blks;       // 索引节点位图于磁盘中的块数

    /*数据块位图信息*/
    int data_map_offset;    // 数据块位图于磁盘中的偏移
    int data_map_blks;      // 数据块位图于磁盘中的块数

    /*索引节点区信息*/
    int ino_offset;         // 索引节点区于磁盘中的偏移
    int ino_blks;           // 索引节点区于磁盘中的块数

    /*数据块区信息*/
    int data_offset;        // 数据块区于磁盘中的偏移
    int data_blks;          // 数据块区于磁盘中的块数

    /* 支持的限制 */
    int ino_max;            // 最大支持inode数
    int file_max;           // 支持文件最大大小

    /* 根目录索引 */
    int root_ino;           // 根目录对应的inode

    /* 其他信息 */

};

struct deadpoolfs_inode_d {
    /* TODO: Define yourself 定义索引节点的数据结构*/
    /* inode编号 */
    uint32_t ino;                   // 在inode位图中的下标

    /* 文件的属性 */
    int size;                       // 文件已占用空间
    int link;                       // 链接数，默认为1
    FILE_TYPE ftype;                // 文件类型（目录类型、普通文件类型）
    int data_blk_num;               // 文件已经占用的数据块数

    /* 数据块的索引 */
    int block_pointer[DPS_DATA_PER_FILE];           // 数据块指针（可固定分配）

    /* 其他字段 */
    int dir_cnt;                    // 如果是目录类型文件，下面有几个目录项
};


struct deadpoolfs_dentry_d {
    /* TODO: Define yourself 定义目录项的数据结构*/
    /* 文件名称 */
    char     name[MAX_NAME_LEN];
    /* inode编号 */
    uint32_t ino;
    /* 文件类型 */
    FILE_TYPE ftype;
};



#endif /* _TYPES_H_ */