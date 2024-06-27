#include "../include/deadpoolfs.h"

//大部分模仿sfs

extern struct deadpoolfs_super      deadpoolfs_super; 
extern struct custom_options        deadpoolfs_options;

/**
 * @brief 获取文件名
 * 
 * @param path 
 * @return char* 
 */
//get_fname与sfs完全一样
char* deadpoolfs_get_fname(const char* path) {
    char ch = '/';
    char *q = strrchr(path, ch) + 1;
    return q;
}
/**
 * @brief 计算路径的层级
 * exm: /av/c/d/f
 * -> lvl = 4
 * @param path 
 * @return int 
 */
//calc_lvl与sfs完全一样
int deadpoolfs_calc_lvl(const char * path) {
    // char* path_cpy = (char *)malloc(strlen(path));
    // strcpy(path_cpy, path);
    char* str = path;
    int   lvl = 0;
    if (strcmp(path, "/") == 0) {
        return lvl;
    }
    while (*str != NULL) {
        if (*str == '/') {
            lvl++;
        }
        str++;
    }
    return lvl;
}
/**
 * @brief 驱动读
 * 
 * @param offset 
 * @param out_content 
 * @param size 
 * @return int 
 */
//模仿sfs，但是一个逻辑块的大小不是一个IO块，应该使用BLK_SZ。
int deadpoolfs_driver_read(int offset, uint8_t *out_content, int size) {
    int      offset_aligned = DPS_ROUND_DOWN(offset, DPS_BLK_SZ());
    int      bias           = offset - offset_aligned;
    int      size_aligned   = DPS_ROUND_UP((size + bias), DPS_BLK_SZ());
    uint8_t* temp_content   = (uint8_t*)malloc(size_aligned);
    uint8_t* cur            = temp_content;
    // lseek(DPS_DRIVER(), offset_aligned, SEEK_SET);
    ddriver_seek(DPS_DRIVER(), offset_aligned, SEEK_SET);
    while (size_aligned != 0)
    {
        // read(DPS_DRIVER(), cur, DPS_IO_SZ());
        ddriver_read(DPS_DRIVER(), cur, DPS_IO_SZ());
        cur          += DPS_IO_SZ();
        size_aligned -= DPS_IO_SZ();   
    }
    memcpy(out_content, temp_content + bias, size);
    free(temp_content);
    return DPS_ERROR_NONE;
}
/**
 * @brief 驱动写
 * 
 * @param offset 
 * @param in_content 
 * @param size 
 * @return int 
 */
//与deadpoolfs_driver_read相似
int deadpoolfs_driver_write(int offset, uint8_t *in_content, int size) {
    int      offset_aligned = DPS_ROUND_DOWN(offset, DPS_BLK_SZ());
    int      bias           = offset - offset_aligned;
    int      size_aligned   = DPS_ROUND_UP((size + bias), DPS_BLK_SZ());
    uint8_t* temp_content   = (uint8_t*)malloc(size_aligned);
    uint8_t* cur            = temp_content;
    deadpoolfs_driver_read(offset_aligned, temp_content, size_aligned);
    memcpy(temp_content + bias, in_content, size);
    
    // lseek(DPS_DRIVER(), offset_aligned, SEEK_SET);
    ddriver_seek(DPS_DRIVER(), offset_aligned, SEEK_SET);
    while (size_aligned != 0)
    {
        // write(DPS_DRIVER(), cur, DPS_IO_SZ());
        ddriver_write(DPS_DRIVER(), cur, DPS_IO_SZ());
        cur          += DPS_IO_SZ();
        size_aligned -= DPS_IO_SZ();   
    }

    free(temp_content);
    return DPS_ERROR_NONE;
}
/**
 * @brief 为一个inode分配dentry，采用头插法
 * 
 * @param inode 
 * @param dentry 
 * @return int 
 */
int deadpoolfs_alloc_dentry(struct deadpoolfs_inode* inode, struct deadpoolfs_dentry* dentry) {
    //当需要新增加一个dentry到父目录时，采用头插法。
    if (inode->first_child == NULL) {
        inode->first_child = dentry;
    }
    else {
        //新增的dentry的兄弟指针指向原来第一个子文件dentry
        dentry->brother = inode->first_child;
        //修改父目录inode中的指针指向新增的dentry结构
        inode->first_child = dentry;
    }
    
    inode->dir_cnt++;
    return inode->dir_cnt;
}
/**
 * @brief 将dentry从inode的dentrys中取出
 * 
 * @param inode 
 * @param dentry 
 * @return int 
 */
//drop_dentry与sfs一样，基本任务中好像未使用
int deadpoolfs_drop_dentry(struct deadpoolfs_inode * inode, struct deadpoolfs_dentry * dentry) {
    boolean is_find = FALSE;
    struct deadpoolfs_dentry* dentry_cursor;
    dentry_cursor = inode->first_child;
    
    if (dentry_cursor == dentry) {
        inode->first_child = dentry->brother;
        is_find = TRUE;
    }
    else {
        while (dentry_cursor)
        {
            if (dentry_cursor->brother == dentry) {
                dentry_cursor->brother = dentry->brother;
                is_find = TRUE;
                break;
            }
            dentry_cursor = dentry_cursor->brother;
        }
    }
    if (!is_find) {
        return -DPS_ERROR_NOTFOUND;
    }
    inode->dir_cnt--;
    return inode->dir_cnt;
}
/**
 * @brief 分配一个inode，占用位图
 * 
 * @param dentry 该dentry指向分配的inode
 * @return sfs_inode
 */
struct deadpoolfs_inode* deadpoolfs_alloc_inode(struct deadpoolfs_dentry * dentry) {
    //新创建的文件还需要为其分配一个索引节点inode：
    //通过逐个位遍历索引节点位图（参考上述数据块位图的遍历），找到一个空闲的索引节点编号。
    struct deadpoolfs_inode* inode;
    int byte_cursor = 0; 
    int bit_cursor  = 0; 
    int ino_cursor  = 0;
    boolean is_find_free_entry = FALSE;
    for(byte_cursor=0;byte_cursor<DPS_BLKS_SZ(deadpoolfs_super.ino_map_blks);byte_cursor++){
        for(bit_cursor=0;bit_cursor<8;bit_cursor++){
            if((deadpoolfs_super.ino_map[byte_cursor]&(0x1<<bit_cursor))==0){
                //当前ino_cursor位置空闲
                deadpoolfs_super.ino_map[byte_cursor]|=(0x1<<bit_cursor);
                is_find_free_entry=TRUE;
                break;
            }
            ino_cursor++;
        }
        if(is_find_free_entry){
            break;
        }
    }
    //如果没有空闲了，返回ERROR
    if (!is_find_free_entry || ino_cursor == deadpoolfs_super.ino_max)
        return -DPS_ERROR_NOSPACE;



    inode = (struct deadpoolfs_inode*)malloc(sizeof(struct deadpoolfs_inode));
    //并填写inode一些字段（后续在写回的时候写回到该编号对应的索引节点位置即可）。
    inode->ino  = ino_cursor; 
    inode->size = 0;
    inode->link = 1;
    inode->dentry=dentry;               //指向该inode的dentry
    inode->ftype = inode->dentry->ftype;
    inode->data_blk_num = 0;
    inode->first_child=NULL;            //指向第一个子节点
    inode->dir_cnt=0;
    
    //绑定该编号
    dentry->inode=inode;
    dentry->ino=inode->ino;

    return inode;
}
/**
 * @brief 分配数据块，占用位图
 * 
 * @param inode 需要分配数据块的inode
 * @return 
 */
int deadpoolfs_alloc_data(struct deadpoolfs_inode* inode) {
    //根据指导书，申请一个数据块的流程：通过逐个位查找数据块位图，找到第一个空闲数据块下表并返回。
    int byte_cursor = 0;
    int bit_cursor  = 0;
    boolean is_find_free_data = FALSE;
    //当文件占用块数已满时，报错
    if(inode->data_blk_num==DPS_DATA_PER_FILE){
            return -DPS_ERROR_INVAL;
    }
    for(byte_cursor=0;byte_cursor<DPS_BLKS_SZ(deadpoolfs_super.data_map_blks);byte_cursor++){
        for(bit_cursor=0;bit_cursor<8;bit_cursor++){
            if((deadpoolfs_super.data_map[byte_cursor]&(0x1<<bit_cursor))==0){
                //当前数据块位图位置空闲，数据块指针指向该位置，并且分配空间
                deadpoolfs_super.data_map[byte_cursor]|=(0x1<<bit_cursor);
                inode->block_pointer[inode->data_blk_num] = byte_cursor * 8 + bit_cursor;  
                inode->block_address[inode->data_blk_num]=(uint8_t *)malloc(DPS_BLK_SZ()); 
                inode->data_blk_num+=1;
                is_find_free_data = TRUE;
                break;
            }
        }
        if(is_find_free_data){
            break;
        }
    }
    //如果没有空闲了，返回ERROR
    if (!is_find_free_data)
        return -DPS_ERROR_NOSPACE;
    
    return DPS_ERROR_NONE;

}
/**
 * @brief 将内存inode及其下方结构全部刷回磁盘
 * 
 * @param inode 
 * @return int 
 */
int deadpoolfs_sync_inode(struct deadpoolfs_inode * inode) {
    struct deadpoolfs_inode_d  inode_d;
    struct deadpoolfs_dentry*  dentry_cursor;
    struct deadpoolfs_dentry_d dentry_d;


    //先写传入文件的索引节点struct inode_d
    //把inode的各个属性赋值给inode_d
    int ino             = inode->ino;
    inode_d.ino         = ino;
    inode_d.size        = inode->size;
    inode_d.link        = inode->link;
    inode_d.ftype       = inode->dentry->ftype;
    inode_d.data_blk_num= inode->data_blk_num;
    inode_d.dir_cnt     = inode->dir_cnt;
    int i;
    for(i=0;i<DPS_DATA_PER_FILE;i++){
        inode_d.block_pointer[i]=inode->block_pointer[i];
    }
    
    if (deadpoolfs_driver_write(DPS_INO_OFS(ino), (uint8_t *)&inode_d, 
                     sizeof(struct deadpoolfs_inode_d)) != DPS_ERROR_NONE) {
        DPS_DBG("[%s] io error\n", __func__);
        return -DPS_ERROR_IO;
    }
                                                      // Cycle 1: 写 INODE 
                                                      // Cycle 2: 写 数据 
    //再写这个文件的文件数据data
    int offset;
    if (DPS_IS_DIR(inode)) {
        //若为目录，则需要写回所有子文件目录项，故cursor从first_child开始                        
        dentry_cursor = inode->first_child;
        //偏移需根据block_pointer[i]来计算
        i=0;
        offset = DPS_DATA_OFS(inode->block_pointer[i]);
        while (dentry_cursor != NULL)
        {
            //当一个块已经写满时，需要写下一块
            if(i<DPS_DATA_PER_FILE&&offset-DPS_DATA_OFS(inode->block_pointer[i])==DPS_BLK_SZ()){
                    i++;
                    offset = DPS_DATA_OFS(inode->block_pointer[i]);
            }
            //写目录文件的文件数据data就是在写回所有子文件的目录项struct dentry_d
            memcpy(dentry_d.name, dentry_cursor->name, MAX_NAME_LEN);
            dentry_d.ftype = dentry_cursor->ftype;
            dentry_d.ino = dentry_cursor->ino;
            if (deadpoolfs_driver_write(offset, (uint8_t *)&dentry_d, 
                                    sizeof(struct deadpoolfs_dentry_d)) != DPS_ERROR_NONE) {
                DPS_DBG("[%s] io error\n", __func__);
                return -DPS_ERROR_IO;                     
            }
                
            //对于目录文件还会额外递归进入每个子文件，从而完成全部文件的写回
            if (dentry_cursor->inode != NULL) {
                deadpoolfs_sync_inode(dentry_cursor->inode);
            }

            dentry_cursor = dentry_cursor->brother;
            offset += sizeof(struct deadpoolfs_dentry_d);
        }
        
    }
    else if (DPS_IS_REG(inode)) {
        //普通文件不需要额外分配数据块，直接分块写入即可
        int i;
        for(i=0;i<inode->data_blk_num;i++){
            if (deadpoolfs_driver_write(DPS_DATA_OFS(inode->block_pointer[i]),
                            inode->block_address[i], DPS_BLK_SZ()) != DPS_ERROR_NONE) {
                DPS_DBG("[%s] io error\n", __func__);
                return -DPS_ERROR_IO;
            }
        }
    }
    return DPS_ERROR_NONE;
}
/**
 * @brief 删除内存中的一个inode， 暂时不释放
 * Case 1: Reg File
 * 
 *                  Inode
 *                /      \
 *            Dentry -> Dentry (Reg Dentry)
 *                       |
 *                      Inode  (Reg File)
 * 
 *  1) Step 1. Erase Bitmap     
 *  2) Step 2. Free Inode                      (Function of sfs_drop_inode)
 * ------------------------------------------------------------------------
 *  3) *Setp 3. Free Dentry belonging to Inode (Outsider)
 * ========================================================================
 * Case 2: Dir
 *                  Inode
 *                /      \
 *            Dentry -> Dentry (Dir Dentry)
 *                       |
 *                      Inode  (Dir)
 *                    /     \
 *                Dentry -> Dentry
 * 
 *   Recursive
 * @param inode 
 * @return int 
 */
//drop_inode在sfs基础上简单改了一下，基本任务中好像未使用
int deadpoolfs_drop_inode(struct deadpoolfs_inode * inode) {
    struct deadpoolfs_dentry*  dentry_cursor;
    struct deadpoolfs_dentry*  dentry_to_free;
    struct deadpoolfs_inode*   inode_cursor;

    int byte_cursor = 0; 
    int bit_cursor  = 0; 
    int ino_cursor  = 0;
    boolean is_find = FALSE;

    int j;

    if (inode == deadpoolfs_super.root_dentry->inode) {
        return DPS_ERROR_INVAL;
    }

    if (DPS_IS_DIR(inode)) {
        dentry_cursor = inode->first_child;
                                                      // 递归向下drop 
        while (dentry_cursor)
        {   
            inode_cursor = dentry_cursor->inode;
            deadpoolfs_drop_inode(inode_cursor);
            deadpoolfs_drop_dentry(inode, dentry_cursor);
            dentry_to_free = dentry_cursor;
            dentry_cursor = dentry_cursor->brother;
            free(dentry_to_free);
        }
    }
    else if (DPS_IS_REG(inode)) {
        for (byte_cursor = 0; byte_cursor < DPS_BLKS_SZ(deadpoolfs_super.ino_map_blks); 
            byte_cursor++)                            // 调整inodemap 
        {
            for (bit_cursor = 0; bit_cursor < UINT8_BITS; bit_cursor++) {
                if (ino_cursor == inode->ino) {
                     deadpoolfs_super.ino_map[byte_cursor] &= (uint8_t)(~(0x1 << bit_cursor));
                     is_find = TRUE;
                     break;
                }
                ino_cursor++;
            }
            if (is_find == TRUE) {
                break;
            }
        }
        for(j=0;j<DPS_DATA_PER_FILE;j++){
            //释放每个块
            if(inode->block_address[j]){
                free(inode->block_address[j]);
            }
        }
        free(inode);
    }
    return DPS_ERROR_NONE;
}
/**
 * @brief 
 * 
 * @param dentry dentry指向ino，读取该inode
 * @param ino inode唯一编号
 * @return struct deadpoolfs_inode* 
 */
struct deadpoolfs_inode* deadpoolfs_read_inode(struct deadpoolfs_dentry * dentry, int ino) {
    //与刷回磁盘的过程是相反的，从磁盘中读取inode
    struct deadpoolfs_inode* inode = (struct deadpoolfs_inode*)malloc(sizeof(struct deadpoolfs_inode));
    struct deadpoolfs_inode_d inode_d;
    struct deadpoolfs_dentry* sub_dentry;
    struct deadpoolfs_dentry_d dentry_d;
    int    dir_cnt = 0,i,m;
    //1.读inode
    //从磁盘读取inode_d
    if (deadpoolfs_driver_read(DPS_INO_OFS(ino), (uint8_t *)&inode_d, 
                        sizeof(struct deadpoolfs_inode_d)) != DPS_ERROR_NONE) {
        DPS_DBG("[%s] io error\n", __func__);
        return NULL;                    
    }
    //把属性赋给inode
    inode->dir_cnt = 0;
    inode->ino = inode_d.ino;
    inode->size = inode_d.size;
    inode->link = inode_d.link;
    inode->ftype = inode_d.ftype;
    inode->data_blk_num = inode_d.data_blk_num;
    inode->dentry = dentry;
    inode->first_child = NULL;
    int j;
    for(j=0;j<DPS_DATA_PER_FILE;j++){
        inode->block_pointer[j]=inode_d.block_pointer[j];
    }

    //2.读数据
    int offset;
    if (DPS_IS_DIR(inode)) {
        //若为目录,dir_cnt为目录项个数
        dir_cnt = inode_d.dir_cnt;
        m=0;    
        offset = DPS_DATA_OFS(inode->block_pointer[m]);
        //遍历所有的目录项，创建子目录项sub_dentry分配给inode
        for(i=0;i<dir_cnt;i++){
            //当一个块已经读完时，需要读下一块
            if(m<DPS_DATA_PER_FILE&&offset-DPS_DATA_OFS(inode->block_pointer[m])==DPS_BLK_SZ()){
                    m++;
                    offset = DPS_DATA_OFS(inode->block_pointer[m]);
            }
            if (deadpoolfs_driver_read(offset, (uint8_t *)&dentry_d, 
                                    sizeof(struct deadpoolfs_dentry_d)) != DPS_ERROR_NONE) {
                DPS_DBG("[%s] io error\n", __func__);
                return NULL;                    
            }

            //为新增的dentry预先分配数据块
            if((inode->dir_cnt*sizeof(dentry))%DPS_BLK_SZ()==0){
                int alloc_data=1;
                alloc_data = deadpoolfs_alloc_data(inode);
                printf("alloc_data:%d\n",alloc_data);
            }

            sub_dentry = new_dentry(dentry_d.name, dentry_d.ftype);
            sub_dentry->parent = inode->dentry;
            sub_dentry->ino    = dentry_d.ino; 
            deadpoolfs_alloc_dentry(inode, sub_dentry);
                
            offset += sizeof(struct deadpoolfs_dentry_d);
        }
    }
    else if (DPS_IS_REG(inode)) {
        //普通文件不需要额外分配数据块，直接分块读入即可
        for(i=0;i<inode->data_blk_num;i++){
            if (deadpoolfs_driver_read(DPS_DATA_OFS(inode->block_pointer[i]), (uint8_t *)inode->block_address[i], 
                            DPS_BLK_SZ()) != DPS_ERROR_NONE) {
                DPS_DBG("[%s] io error\n", __func__);
                return NULL;                    
            }
        }
    
    }
    return inode;
}
/**
 * @brief 
 * 
 * @param inode 
 * @param dir [0...]
 * @return struct deadpoolfs_dentry* 
 */
//get_dentry与sfs一样
struct deadpoolfs_dentry* deadpoolfs_get_dentry(struct deadpoolfs_inode * inode, int dir) {
    struct deadpoolfs_dentry* dentry_cursor = inode->first_child;
    int    cnt = 0;
    while (dentry_cursor)
    {
        if (dir == cnt) {
            return dentry_cursor;
        }
        cnt++;
        dentry_cursor = dentry_cursor->brother;
    }
    return NULL;
}
/**
 * @brief 
 * path: /qwe/ad  total_lvl = 2,
 *      1) find /'s inode       lvl = 1
 *      2) find qwe's dentry 
 *      3) find qwe's inode     lvl = 2
 *      4) find ad's dentry
 *
 * path: /qwe     total_lvl = 1,
 *      1) find /'s inode       lvl = 1
 *      2) find qwe's dentry
 * 
 * @param path 
 * @return struct sfs_inode* 
 */
//lookup与sfs一样
struct deadpoolfs_dentry* deadpoolfs_lookup(const char * path, boolean* is_find, boolean* is_root) {
    struct deadpoolfs_dentry* dentry_cursor = deadpoolfs_super.root_dentry;
    struct deadpoolfs_dentry* dentry_ret = NULL;
    struct deadpoolfs_inode*  inode; 
    int   total_lvl = deadpoolfs_calc_lvl(path);
    int   lvl = 0;
    boolean is_hit;
    char* fname = NULL;
    char* path_cpy = (char*)malloc(sizeof(path));
    *is_root = FALSE;
    strcpy(path_cpy, path);

    if (total_lvl == 0) {                           // 根目录 
        *is_find = TRUE;
        *is_root = TRUE;
        dentry_ret = deadpoolfs_super.root_dentry;
    }
    fname = strtok(path_cpy, "/");       
    while (fname)
    {   
        lvl++;
        if (dentry_cursor->inode == NULL) {           // Cache机制 
            deadpoolfs_read_inode(dentry_cursor, dentry_cursor->ino);
        }

        inode = dentry_cursor->inode;

        if (DPS_IS_REG(inode) && lvl < total_lvl) {
            DPS_DBG("[%s] not a dir\n", __func__);
            dentry_ret = inode->dentry;
            break;
        }
        if (DPS_IS_DIR(inode)) {
            dentry_cursor = inode->first_child;
            is_hit        = FALSE;

            while (dentry_cursor)
            {
                if (memcmp(dentry_cursor->name, fname, strlen(fname)) == 0) {
                    is_hit = TRUE;
                    break;
                }
                dentry_cursor = dentry_cursor->brother;
            }
            
            if (!is_hit) {
                *is_find = FALSE;
                DPS_DBG("[%s] not found %s\n", __func__, fname);
                dentry_ret = inode->dentry;
                break;
            }

            if (is_hit && lvl == total_lvl) {
                *is_find = TRUE;
                dentry_ret = dentry_cursor;
                break;
            }
        }
        fname = strtok(NULL, "/"); 
    }

    if (dentry_ret->inode == NULL) {
        dentry_ret->inode = deadpoolfs_read_inode(dentry_ret, dentry_ret->ino);
    }
    
    return dentry_ret;
}
/**
 * @brief 挂载sfs, Layout 如下
 * 
 * Layout
 * | Super | Inode Map | Data |
 * 
 * IO_SZ = BLK_SZ
 * 
 * 每个Inode占用一个Blk
 * @param options 
 * @return int 
 */
//mount与sfs相似
int deadpoolfs_mount(struct custom_options options){ 
    int                         ret = DPS_ERROR_NONE;
    int                         driver_fd;
    struct deadpoolfs_super_d   deadpoolfs_super_d; 
    struct deadpoolfs_dentry*   root_dentry;
    struct deadpoolfs_inode*    root_inode;

    int                 inode_num;
    int                 map_inode_blks;

    int                 data_num;
    int                 map_data_blks;
    
    int                 super_blks;
    boolean             is_init = FALSE;

    deadpoolfs_super.is_mounted = FALSE;

    //打开驱动
    // driver_fd = open(options.device, O_RDWR);
    driver_fd = ddriver_open(options.device);

    if (driver_fd < 0) {
        return driver_fd;
    }

    //填充超级块相关信息
    deadpoolfs_super.fd = driver_fd;
    ddriver_ioctl(DPS_DRIVER(), IOC_REQ_DEVICE_SIZE,  &deadpoolfs_super.sz_disk);
    ddriver_ioctl(DPS_DRIVER(), IOC_REQ_DEVICE_IO_SZ, &deadpoolfs_super.sz_io);
    deadpoolfs_super.blks_size = 2*deadpoolfs_super.sz_io;
    deadpoolfs_super.blks_nums = 4096;
    
    //根目录项
    root_dentry = new_dentry("/", DIR);

    if (deadpoolfs_driver_read(DPS_SUPER_OFS, (uint8_t *)(&deadpoolfs_super_d), 
                        sizeof(struct deadpoolfs_super_d)) != DPS_ERROR_NONE) {
        return -DPS_ERROR_IO;
    }   
                                                      // 读取super 
    if (deadpoolfs_super_d.magic != DEADPOOLFS_MAGIC) {     // 幻数无，则为第一次挂载，超级块的构建需要完全重新进行
                                                      // 估算各部分大小 
        //超级块的块数
        super_blks = DPS_ROUND_UP(sizeof(struct deadpoolfs_super_d), DPS_BLK_SZ()) / DPS_BLK_SZ();
        //索引节点的数量
        inode_num  =  DPS_DISK_SZ() / ((DPS_DATA_PER_FILE + DPS_INODE_PER_FILE) * DPS_BLK_SZ());
        //索引节点位图块数
        map_inode_blks = DPS_ROUND_UP(DPS_ROUND_UP(inode_num, UINT32_BITS), DPS_BLK_SZ()) 
                          / DPS_BLK_SZ();
        //数据块位图块数
        map_data_blks  =  1;
        //数据块的数量
        data_num    =  4096-super_blks-inode_num-map_inode_blks-map_data_blks;      

                                                      // 布局layout 
        deadpoolfs_super.ino_max = inode_num;
        
        deadpoolfs_super_d.magic = DEADPOOLFS_MAGIC;
        deadpoolfs_super_d.sz_usage = 0;
        deadpoolfs_super_d.blks_size = deadpoolfs_super.blks_size;
        deadpoolfs_super_d.blks_nums = deadpoolfs_super.blks_nums;
        deadpoolfs_super_d.sb_offset = 0;
        deadpoolfs_super_d.sb_blks = super_blks;
        deadpoolfs_super_d.ino_map_offset = DPS_SUPER_OFS + DPS_BLKS_SZ(super_blks);
        deadpoolfs_super_d.ino_map_blks = map_inode_blks;
        deadpoolfs_super_d.data_map_offset = deadpoolfs_super_d.ino_map_offset + DPS_BLKS_SZ(map_inode_blks);    
        deadpoolfs_super_d.data_map_blks = map_data_blks;      
        deadpoolfs_super_d.ino_offset = deadpoolfs_super_d.data_map_offset + DPS_BLKS_SZ(map_data_blks);        
        deadpoolfs_super_d.ino_blks = inode_num;           
        deadpoolfs_super_d.data_offset = deadpoolfs_super_d.ino_offset + DPS_BLKS_SZ(deadpoolfs_super_d.ino_blks);        
        deadpoolfs_super_d.data_blks = data_num;          
        deadpoolfs_super_d.ino_max = inode_num;            
        deadpoolfs_super_d.file_max = inode_num;           
        deadpoolfs_super_d.root_ino = 0;            
        
        DPS_DBG("inode map blocks: %d\n", map_inode_blks);
        is_init = TRUE;
    }
    deadpoolfs_super.sz_usage   = deadpoolfs_super_d.sz_usage;      // 建立 in-memory 结构 
    
    deadpoolfs_super.sb_offset = deadpoolfs_super_d.sb_offset;          
    deadpoolfs_super.sb_blks = deadpoolfs_super_d.sb_blks;            
    deadpoolfs_super.ino_map = (uint8_t *)malloc(DPS_BLKS_SZ(deadpoolfs_super_d.ino_map_blks));     
    deadpoolfs_super.ino_map_offset = deadpoolfs_super_d.ino_map_offset;     
    deadpoolfs_super.ino_map_blks = deadpoolfs_super_d.ino_map_blks;       
    deadpoolfs_super.data_map = (uint8_t *)malloc(DPS_BLKS_SZ(deadpoolfs_super_d.data_map_blks));      
    deadpoolfs_super.data_map_offset = deadpoolfs_super_d.data_map_offset;    
    deadpoolfs_super.data_map_blks = deadpoolfs_super_d.data_map_blks;      
    deadpoolfs_super.ino_offset = deadpoolfs_super_d.ino_offset;         
    deadpoolfs_super.ino_blks = deadpoolfs_super_d.ino_blks;           
    deadpoolfs_super.data_offset = deadpoolfs_super_d.data_offset;        
    deadpoolfs_super.data_blks = deadpoolfs_super_d.data_blks;          
    deadpoolfs_super.ino_max = deadpoolfs_super_d.ino_max;            
    deadpoolfs_super.file_max = deadpoolfs_super_d.file_max;           
    deadpoolfs_super.root_ino = deadpoolfs_super_d.root_ino;           

    //读取索引节点位图
    if (deadpoolfs_driver_read(deadpoolfs_super_d.ino_map_offset, (uint8_t *)(deadpoolfs_super.ino_map), 
                        DPS_BLKS_SZ(deadpoolfs_super_d.ino_map_blks)) != DPS_ERROR_NONE) {
        return -DPS_ERROR_IO;
    }

    //与读取索引节点位图相似，读取数据块位图
    if (deadpoolfs_driver_read(deadpoolfs_super_d.data_map_offset, (uint8_t *)(deadpoolfs_super.data_map), 
                        DPS_BLKS_SZ(deadpoolfs_super_d.data_map_blks)) != DPS_ERROR_NONE) {
        return -DPS_ERROR_IO;
    }

    
    if (is_init) {                                    // 分配根节点 
        root_inode = deadpoolfs_alloc_inode(root_dentry);
        deadpoolfs_sync_inode(root_inode);
    }

    
    root_inode            = deadpoolfs_read_inode(root_dentry, DPS_ROOT_INO);
    root_dentry->inode    = root_inode;
    deadpoolfs_super.root_dentry = root_dentry;
    deadpoolfs_super.is_mounted  = TRUE;

    //deadpoolfs_dump_map();
    return ret;
}
/**
 * @brief 
 * 
 * @return int 
 */
//umount与sfs相似
int deadpoolfs_umount() {
    struct deadpoolfs_super_d  deadpoolfs_super_d; 

    if (!deadpoolfs_super.is_mounted) {
        return DPS_ERROR_NONE;
    }

    deadpoolfs_sync_inode(deadpoolfs_super.root_dentry->inode);     // 从根节点向下刷写节点 
                                                    
    deadpoolfs_super_d.magic = DEADPOOLFS_MAGIC;
    deadpoolfs_super_d.sz_usage = deadpoolfs_super.sz_usage;
    deadpoolfs_super_d.blks_size = deadpoolfs_super.blks_size;          
    deadpoolfs_super_d.blks_nums = deadpoolfs_super.blks_nums;          
    deadpoolfs_super_d.sb_offset = deadpoolfs_super.sb_offset;          
    deadpoolfs_super_d.sb_blks = deadpoolfs_super.sb_blks;            
    deadpoolfs_super_d.ino_map_offset = deadpoolfs_super.ino_map_offset;     
    deadpoolfs_super_d.ino_map_blks = deadpoolfs_super.ino_map_blks;       
    deadpoolfs_super_d.data_map_offset = deadpoolfs_super.data_map_offset;    
    deadpoolfs_super_d.data_map_blks = deadpoolfs_super.data_map_blks;      
    deadpoolfs_super_d.ino_offset = deadpoolfs_super.ino_offset;         
    deadpoolfs_super_d.ino_blks = deadpoolfs_super.ino_blks;           
    deadpoolfs_super_d.data_offset = deadpoolfs_super.data_offset;        
    deadpoolfs_super_d.data_blks = deadpoolfs_super.data_blks;          
    deadpoolfs_super_d.ino_max = deadpoolfs_super.ino_max;            
    deadpoolfs_super_d.file_max = deadpoolfs_super.file_max;           
    deadpoolfs_super_d.root_ino = deadpoolfs_super.root_ino;


    //写超级块
    if (deadpoolfs_driver_write(DPS_SUPER_OFS, (uint8_t *)&deadpoolfs_super_d, 
                     sizeof(struct deadpoolfs_super_d)) != DPS_ERROR_NONE) {
        return -DPS_ERROR_IO;
    }

    //写索引节点位图
    if (deadpoolfs_driver_write(deadpoolfs_super_d.ino_map_offset, (uint8_t *)(deadpoolfs_super.ino_map), 
                         DPS_BLKS_SZ(deadpoolfs_super_d.ino_map_blks)) != DPS_ERROR_NONE) {
        return -DPS_ERROR_IO;
    }

    //与写索引节点位图相似，写数据块位图
    if (deadpoolfs_driver_write(deadpoolfs_super_d.data_map_offset, (uint8_t *)(deadpoolfs_super.data_map), 
                         DPS_BLKS_SZ(deadpoolfs_super_d.data_map_blks)) != DPS_ERROR_NONE) {
        return -DPS_ERROR_IO;
    }

    free(deadpoolfs_super.ino_map);
    free(deadpoolfs_super.data_map);
    ddriver_close(DPS_DRIVER());

    return DPS_ERROR_NONE;
}
