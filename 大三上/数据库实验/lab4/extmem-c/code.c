#include <stdlib.h>
#include <stdio.h>
#include "extmem.h"


#define FINISHED 999

//根据块和索引获得该元组的第1个数字
int get_num1(unsigned char *blk,int index){
    char str[4];
    int k;
    for (k = 0; k < 4; k++){
        str[k] = *(blk + index*8 + k);
    }
    int X = atoi(str);
    return X;
}

//根据块和索引获得该元组的第2个数字
int get_num2(unsigned char *blk,int index){
    char str[4];
    int z;
    for (z = 0; z < 4; z++){
        str[z] = *(blk + index*8 + 4 + z);
    }
    int X = atoi(str);
    return X;
}

//将元组写入块相应位置
void write_num(char *str_1,char * str_2,int cnt,unsigned char *blk){
    //printf("cnt:%d\n",cnt);
    int i=0;
    for (i = 0; i < 4; i++){
        char a = str_1[i];
        *(blk +cnt*8+ i) = a;
        //printf("%c \n",a);
    }
    for (i = 4; i < 8; i++){
        char b = str_2[i-4];
        *(blk +cnt*8+ i) = b;
        //printf("%c \n",b);
    }
}

//把块的内容清空并且在块的末尾位置写上下一块的块号
void setBlockBlank(unsigned char *blk,int blkno){
    char str[4];
    itoa(blkno+1,str,10);
    int j;
    for(j=0;j<56;j++){
        *(blk+j)=' ';
    }
    int t;
    for(t=56;t<60;t++){
        *(blk+t)=str[t-56];
    }
    /**for(int j=60;j<63;j++){
        *(blk+j)=' ';
    }*/

}


//1.基于ExtMem程序库，使用C语言实现线性搜索算法，选出S.C=107的元组，记录IO读写次数，并将选择结果存放在磁盘上。
//（模拟实现 select S.C,S.D from S where S.C = 107）
void answer_1(){
    Buffer buf;  /*A buffer*/
    unsigned char *blk; /* A pointer to a block */
    /* Initialize the buffer*/
    if (!initBuffer(520, 64, &buf))
    {
        perror("Buffer Initialization Failed!\n");
    }

    //Process the data in the block
    int X = -1;
    int Y = -1;

    //由于 17.blk 至 48.blk 为关系 S 的元组数据,依次进行查询
    int j=17;
    int i=0;
    int cnt=0;
    int tuple_cnt=0;
    int q=0;

    char str_1[4];
    char str_2[4];

    unsigned char *blk_write=getNewBlockInBuffer(&buf);
    setBlockBlank(blk_write,100+q);


    printf("------------------------------\n");
    printf("基于线性搜索的选择算法 S.C=107\n");
    printf("------------------------------\n");



    for(j=17;j<49;j++){
        blk = readBlockFromDisk(j, &buf);
        printf("读入数据块%d\n",j);
        for (i = 0; i < 7; i++) //一个blk存7个元组加一个地址
        {

            X=get_num1(blk,i);
            Y=get_num2(blk,i);
            if(X==107){
                tuple_cnt++;
                printf("(X=%d, Y=%d)\n", X, Y);
                itoa(X,str_1,10);
                itoa(Y,str_2,10);
                if(cnt==7){
                    writeBlockToDisk(blk_write,100+q,&buf);
                    printf("注：结果写入磁盘:%d\n",100+q);
                    q++;
                    setBlockBlank(blk_write,100+q);
                    blk_write = getNewBlockInBuffer(&buf);
                    cnt=0;
                    write_num(str_1,str_2,cnt,blk_write);
                    cnt++;
                }else{
                    write_num(str_1,str_2,cnt,blk_write);
                    cnt++;
                }
            }
        }
        //需要释放块，否则缓冲区不够用
        freeBlockInBuffer(blk,&buf);
    }

    //记得要写块
    writeBlockToDisk(blk_write,100+q,&buf);
    printf("注：结果写入磁盘:%d\n",100+q);
    freeBuffer(&buf);

    printf("\n");
    printf("满足选择条件的元组一共%d个。\n",tuple_cnt);
    printf("IO读写一共%d次。\n", buf.numIO);
    printf("\n");
}

//内排序算法
void internal_sort(int cmp_start_blkno,int cmp_end_blkno,int write_start_blkno,int group_num){
    Buffer buf;  /*A buffer*/
    /* Initialize the buffer*/
    if (!initBuffer(520, 64, &buf))
    {
        perror("Buffer Initialization Failed!\n");
    }


    //块的个数和等待比较的块
    int blks_num;
    unsigned char *blks_to_cmp[6];


    // 当前块号
    int curr_blkno = cmp_start_blkno;

    // 要比较的元组的块号和索引
    int cmp_blkno1, index1,cmp_blkno2, index2;

    // 两个要比较的数以及它们的第二列的数字
    int cmp_num1,cmp_num2,num1_2,num2_2;
    char str1[4],str2[4];

    int q=0;

    // 分组排序
    int i;
    int j;
    int k;
    for (i = 0; i < group_num; i++) {
        //循环读入块
        for (blks_num=0;blks_num<6&&curr_blkno<=cmp_end_blkno;blks_num++) {
            blks_to_cmp[blks_num] = readBlockFromDisk(curr_blkno, &buf);
            curr_blkno++;
        }
        //循环比较
        for (j=0;j<blks_num *7-1;j++) {
            //计算第一个数字的块号和索引
            cmp_blkno1=j/7;
            index1=j%7;

            for (k=j+1;k<blks_num*7;k++) {
                //第二个数字的块号和索引
                cmp_blkno2=k/7;
                index2=k%7;

                //获取要比较的两个数字
                cmp_num1 = get_num1(blks_to_cmp[cmp_blkno1], index1);
                cmp_num2 = get_num1(blks_to_cmp[cmp_blkno2], index2);
                //printf("%d cmp with %d\n",cmp_num1,cmp_num2);

                //若第一个数字大于第二个数字，则需要进行交换，即互相写到对方的位置
                if (cmp_num1>cmp_num2) {
                    num1_2=get_num2(blks_to_cmp[cmp_blkno1], index1);
                    num2_2=get_num2(blks_to_cmp[cmp_blkno2], index2);
                    //printf("num1>num2,( %d , %d )\n",cmp_num1,cmp_num2);
                    itoa(cmp_num1,str1,10);
                    itoa(num1_2,str2,10);
                    //printf("%s,%s\n",str1,str2);
                    write_num(str1,str2,index2,blks_to_cmp[cmp_blkno2]);
                    itoa(cmp_num2,str1,10);
                    itoa(num2_2,str2,10);
                    //printf("%s,%s\n",str1,str2);
                    write_num(str1,str2,index1,blks_to_cmp[cmp_blkno1]);
                }
            }
        }

        //printf("%d\n",blk_cnt);

        // 排序后的数据写到磁盘块里
        for (j = 0; j < blks_num; j++) {

            //记得要先把下一块的块号写好，不然会出错，出现乱码
            itoa(write_start_blkno+q+1,str1,10);
            int m;
            for(m=0;m<4;m++){
                str2[m]=' ';
            }
            write_num(str1,str2,7,blks_to_cmp[j]);

            writeBlockToDisk(blks_to_cmp[j], write_start_blkno+q, &buf);

            //printf("内排序写入块号:%d\n",write_start_blkno+q);

            q++;
            //printf("\n");

        }

    }

    freeBuffer(&buf);
}


//总排序算法
void total_sort(int sort_start_blkno,int sort_end_blkno,int write_start_blkno,int group_num){
    Buffer buf;  /*A buffer*/
    /* Initialize the buffer*/
    if (!initBuffer(520, 64, &buf))
    {
        perror("Buffer Initialization Failed!\n");
    }


    //排序块和写入块
    unsigned char *blk_sort=getNewBlockInBuffer(&buf);
    unsigned char *blk_write=getNewBlockInBuffer(&buf);


    //排序块中各数据对应的块和索引
    unsigned char *blks_to_sort[8];
    int blks_to_sort_index[8];

    //输出块号，开始为输入的write_start_blkno
    int write_blkno=write_start_blkno;
    int Poutput=0;

    setBlockBlank(blk_write,write_blkno);

    int num1=FINISHED;
    int num2=num1;
    char str_1[4],str_2[4];
    itoa(num2,str_2,10);

    //printf("%d %d\n",num1,num2);
    //printf("%s\n",str_2);
    int i;
    int m;
    for (i = 0; i < 8; i++) {
        //不知道为什么str_1就是没办法转换，所以直接用str_2了
        write_num(str_2,str_2,i,blk_sort);
    }

    int group_index[8];

    //分组
    for(i=0;i<group_num;i++){
        //调用read block函数，读Si的第一块存入Mi中
        blks_to_sort[i] = readBlockFromDisk(sort_start_blkno + i * 6, &buf);

        //将其第一个元素存入Mcompare的第ith个位置
        num1 = get_num1(blks_to_sort[i], 0);
        num2 = get_num2(blks_to_sort[i], 0);
        //printf("%d %d\n",num1,num2);
        itoa(num1,str_1,10);
        itoa(num2,str_2,10);
        write_num(str_1,str_2,i,blk_sort);
        //printf("%s %s\n",str_1,str_2);

        blks_to_sort_index[i]=1;
        group_index[i]=sort_start_blkno+i*6;
    }

    int min_num;
    int min_num_index;
    int curr_num;

    while(1){
        //求Mcmp中m个元素的最小值及其位置i
        min_num=FINISHED;
        min_num_index=0;
        for(i=0;i<8;i++){
            curr_num=get_num1(blk_sort,i);
            //printf("curr num:%d,curr index:%d\n",curr num,i);
            if(curr_num<min_num){
                min_num=curr_num;
                min_num_index=i;
            }
        }

        //printf("min_num:%d\n",min_num);


        if(min_num==FINISHED){
            //若min_num还是FINISHED，则说明没有找到最小值，直接退出
            //printf("break;\n");
            break;
        }else{
            //否则就是找到了最小值及其位置i

            //将第ith个位置的元素存入Moutput中的Poutput位置，Poutput指针按次序指向下一位置
            num2 = get_num2(blk_sort,min_num_index);
            itoa(min_num,str_1,10);
            itoa(num2,str_2,10);
            //printf("%d\n",min_num);
            //printf("%s %s\n",str_1,str_2);
            write_num(str_1,str_2,Poutput,blk_write);
            Poutput++;

            //如果Poutput指向结束位置
            if(Poutput==7){
                //调用write block按次序将Moutput写回磁盘
                itoa(write_blkno+1,str_1,10);

                for(m=0;m<4;m++){
                    str_2[m]=' ';
                }
                write_num(str_1,str_2,7,blk_write);

                writeBlockToDisk(blk_write,write_blkno,&buf);
                printf("注：结果写入磁盘:%d\n",write_blkno);


                blk_write = getNewBlockInBuffer(&buf);
                //setBlockBlank(blk_write,write_blkno);

                /**printf("write block to disk\n");

                for (r=0;r<63;r++){
                    printf("%c",*(blk_write+r));
                }
                printf("\n");*/

                //置Poutput为输出内存块的起始位置
                Poutput=0;
                write_blkno++;
                //然后继续进行
            }

            //如果Mi有下一个元素
            if(blks_to_sort_index[min_num_index]<7){
                //将Mi下一个元素存入Mcmp的第ith个位置
                num1=get_num1(blks_to_sort[min_num_index],blks_to_sort_index[min_num_index]);
                num2=get_num2(blks_to_sort[min_num_index],blks_to_sort_index[min_num_index]);
                blks_to_sort_index[min_num_index]++;
                itoa(num1,str_1,10);
                itoa(num2,str_2,10);
                write_num(str_1,str_2,min_num_index,blk_sort);
                //继续循环
            }else{
                //如果Si有下一块，即Si既不是每组的最后一块也不是整体的最后一块（最后一组不一定满，要单独判断）
                if((group_index[min_num_index]-sort_start_blkno+1)%6>0 && group_index[min_num_index]<sort_end_blkno){
                    freeBlockInBuffer(blks_to_sort[min_num_index], &buf);
                    //下一块
                    group_index[min_num_index]++;
                    //调用read block按次序读Si的下一块并存入Mi
                    blks_to_sort[min_num_index] = readBlockFromDisk(group_index[min_num_index], &buf);
                    //读下一块的第一个元素
                    num1=get_num1(blks_to_sort[min_num_index],0);
                    num2=get_num2(blks_to_sort[min_num_index],0);
                    itoa(num1,str_1,10);
                    itoa(num2,str_2,10);
                    write_num(str_1,str_2,min_num_index,blk_sort);
                    blks_to_sort_index[min_num_index] = 1;

                }else{
                    //否则，返回特殊值如FINISHED，以示Si子集合处理完毕，Mi为空，且使Mcmp的第ith个位置为该特殊值，
                    //表明该元素不参与Mcmp的比较操作
                    num2=FINISHED;
                    itoa(num2,str_2,10);
                    //printf("done\n");
                    //printf("%s %s",str_1,str_2);
                    write_num(str_2,str_2,min_num_index,blk_sort);
                    //然后又转步骤7即继续循环
                }

            }

        }

    }

    freeBuffer(&buf);
}



//2.实现两阶段多路归并排序算法（TPMMS）：利用内存缓冲区将关系R和S分别排序，并将排序后的结果存放在磁盘上。
void answer_2(){

    printf("-----\n");
    printf("TPMMS\n");
    printf("-----\n");
    printf("\n");

    //待排序的块号：起始块号和结束块号
    int cmp_start_blkno;
    int cmp_end_blkno;

    //内排序和总排序需要写入的块的开头
    int internal_sort_write_start_blkno;
    int total_sort_write_start_blkno;

    //总排序的块号：起始块号和结束块号
    int sort_start_blkno;
    int sort_end_blkno;

    //分组的数量
    int group_num;


    //对R进行排序
    cmp_start_blkno=1;
    cmp_end_blkno=16;

    //内排序的结果写在150到165块，总排序的结果写在200到215块
    internal_sort_write_start_blkno=150;
    total_sort_write_start_blkno=200;

    sort_start_blkno=150;
    sort_end_blkno=165;

    group_num = (cmp_end_blkno - cmp_start_blkno) / 6 + 1;

    printf("---------\n");
    printf("关系R排序\n");
    printf("---------\n");

    //内排序
    internal_sort(cmp_start_blkno,cmp_end_blkno,internal_sort_write_start_blkno,group_num);
    //总排序
    total_sort(sort_start_blkno,sort_end_blkno,total_sort_write_start_blkno,group_num);

    printf("\n");






    //同理，对S进行排序
    cmp_start_blkno=17;
    cmp_end_blkno=48;

    //内排序的结果写在250到281块，总排序的结果写在300到331块
    internal_sort_write_start_blkno=250;
    total_sort_write_start_blkno=300;

    sort_start_blkno=250;
    sort_end_blkno=281;

    group_num = (cmp_end_blkno - cmp_start_blkno)/6+1;

    printf("---------\n");
    printf("关系S排序\n");
    printf("---------\n");

    //内排序
    internal_sort(cmp_start_blkno,cmp_end_blkno,internal_sort_write_start_blkno,group_num);
    //总排序
    total_sort(sort_start_blkno,sort_end_blkno,total_sort_write_start_blkno,group_num);

    printf("\n");


}

//创建索引
int create_index(int start_blkno,int end_blkno,int write_start_blkno){
    Buffer buf;  /*A buffer*/
    /* Initialize the buffer*/
    if (!initBuffer(520, 64, &buf))
    {
        perror("Buffer Initialization Failed!\n");
    }

    unsigned char *blk;
    unsigned char *blk_write=getNewBlockInBuffer(&buf);
    setBlockBlank(blk_write,write_start_blkno);

    //索引的两个数字
    int X,Y;
    char str_1[4],str_2[4];

    int cnt=0;
    int q=0;

    //对开始块到结束块创建索引
    for (int i=start_blkno;i<end_blkno+1;i++) {
        //读入块
        blk=readBlockFromDisk(i,&buf);
        //X指示块号
        X=i;
        //Y指示第一列的数字即S.C
        Y=get_num1(blk,0);
        //记得释放，不然缓冲区不够用
        freeBlockInBuffer(blk, &buf);

        //printf("num(%d %d)\n",X,Y);

        //写
        itoa(X,str_1,10);
        itoa(Y,str_2,10);
        write_num(str_1,str_2,cnt,blk_write);
        cnt++;

        if (cnt== 7) {
            writeBlockToDisk(blk_write,write_start_blkno+q,&buf);
            //printf("write_blkno:%d\n",write_start_blkno+q);
            cnt= 0;
            q++;
            blk_write= getNewBlockInBuffer(&buf);
            setBlockBlank(blk_write,write_start_blkno+q);
        }
    }
    //记得要写块
    writeBlockToDisk(blk_write,write_start_blkno+q,&buf);
    //printf("write_blkno:%d\n",write_start_blkno+q);


    //printf("建立索引的IO次数: %d\n", buf.numIO);
    freeBuffer(&buf);

    int write_end_blkno=write_start_blkno+q;
    return write_end_blkno;

}

//根据索引找元组
void find_tuple_by_index(int target_num1,int index_write_start_blkno,int index_write_end_blkno,int find_write_start_blkno){
    Buffer buf;  /*A buffer*/
    /* Initialize the buffer*/
    if (!initBuffer(520, 64, &buf))
    {
        perror("Buffer Initialization Failed!\n");
    }


    unsigned char *blk;
    unsigned char *blk_write=getNewBlockInBuffer(&buf);
    setBlockBlank(blk_write,find_write_start_blkno);
    int cnt=0;
    int q=0;

    //需要找的最小块号和最大块号
    int min_blkno_find = 0;
    int max_blkno_find = 0;

    int X,Y;
    char str_1[4],str_2[4];

    //flag，标记是否找到最大块号
    int max_blkno_find_flag=0;

    //记录一共有几个符合要求的元组
    int target_cnt=0;

    for(int i=index_write_start_blkno;i<index_write_end_blkno+1;i++) {
        printf("读入索引块%d\n",i);
        //读入索引块
        blk = readBlockFromDisk(i, &buf);
        for (int j = 0; j < 7; j++) {
            //检查每个元组
            X = get_num1(blk,j);
            Y = get_num2(blk,j);
            if (Y<target_num1) {
                //由于从小到大排序，当Y小于目标值时，将X赋给最小块号
                min_blkno_find = X;
            }else if(Y>target_num1) {
                //由于从小到大排序，当Y大于目标值时，将X赋给最大块号
                max_blkno_find = X;
                //将flag置为1，表示已经找到了最大块号
                max_blkno_find_flag=1;
                //往下找只会更大，不用再往下找了，出循环
                break;
            }
        }
        freeBlockInBuffer(blk, &buf);
        //如果已经找到最大块号，往下的索引块中的数只会更大，不用再往下找了，出循环
        if (max_blkno_find_flag == 1) {
            break;
        }
    }

    //从最小块号到最大块号找
    for (int i=min_blkno_find;i<max_blkno_find;i++) {
        //读入块
        blk = readBlockFromDisk(i, &buf);
        printf("读入数据块%d\n", i);
        for (int j = 0; j < 7; j++) {
            //检查每个元组
            X=get_num1(blk,j);
            Y=get_num2(blk,j);

            if (X==target_num1) {
                //找到了一个，需要写
                target_cnt++;

                printf("(X=%d, Y=%d)\n", X,Y);
                itoa(X,str_1,10);
                itoa(Y,str_2,10);
                write_num(str_1,str_2,cnt,blk_write);
                cnt++;

                if (cnt== 7) {
                    writeBlockToDisk(blk_write,find_write_start_blkno+q,&buf);
                    printf("注：结果写入磁盘块%d\n",find_write_start_blkno+q);
                    //printf("write_blkno:%d\n",find_write_start_blkno+q);
                    cnt= 0;
                    q++;
                    blk_write= getNewBlockInBuffer(&buf);
                    setBlockBlank(blk_write,find_write_start_blkno+q);
                }
            }
        }
        freeBlockInBuffer(blk, &buf);
    }

    //记得要写块
    writeBlockToDisk(blk_write,find_write_start_blkno+q,&buf);
    printf("注：结果写入磁盘块%d\n",find_write_start_blkno+q);
    //printf("write_blkno:%d\n",find_write_start_blkno+q);

    freeBuffer(&buf);

    printf("满足选择条件的元组一共%d个。\n", target_cnt);
    printf("IO读写一共%d次。\n", buf.numIO);
    printf("\n");

}


//3.实现基于索引的关系选择算法：利用（2）中的排序结果为关系R或S分别建立索引文件，利用索引文件选出S.C=107的元组，并将选择结果存放在磁盘上。
//记录IO读写次数，与（1）中的结果对比。
void answer_3(){

    //创建索引
    int index_start_blkno=300;
    int index_end_blkno=331;
    int index_write_start_blkno=350;
    int index_write_end_blkno;
    index_write_end_blkno=create_index(index_start_blkno,index_end_blkno,index_write_start_blkno);
    //printf("index write end:%d\n",index_write_end_blkno);


    printf("--------------------------\n");
    printf("基于索引的选择算法 S.C=107\n");
    printf("--------------------------\n");

    //通过索引寻找S.C=107的元组
    int target_num1=107;
    int find_write_start_blkno=360;
    find_tuple_by_index(target_num1,index_write_start_blkno,index_write_end_blkno,find_write_start_blkno);

}


//连接S和R
void join_S_R(int S_start_blkno,int S_end_blkno,int R_start_blkno,int R_end_blkno,int join_write_start_blkno){
    Buffer buf;  /*A buffer*/
    /* Initialize the buffer*/
    if (!initBuffer(520, 64, &buf))
    {
        perror("Buffer Initialization Failed!\n");
    }


    //连接次数
    int join_cnt=0;

    unsigned char *blk_S;
    unsigned char *blk_R;
    unsigned char *blk_write = getNewBlockInBuffer(&buf);
    setBlockBlank(blk_write,join_write_start_blkno);

    //S,R的块号和索引
    int S_blkno,S_index,R_blkno,R_index;


    int cnt=0;
    int q=0;

    //S,R的第一列和第二列的数字
    int S_num1,R_num1,S_num2,R_num2;
    char str_3[4],str_4[4],str_5[4];

    //上次做连接时的S值
    int last_S_num = 0;
    //上次做连接时的R的第一块的块号和索引
    int last_R_blkno = R_start_blkno;
    int last_R_index = 0;
    //上次做连接时的R值
    int last_R_num = 0;

    //标记R.A是否已经大于S.C
    int flag=0;
    for (S_blkno=S_start_blkno; S_blkno <= S_end_blkno;S_blkno++) {
        //读入S的一块
        blk_S = readBlockFromDisk(S_blkno, &buf);
        //检查块内的所有元组
        for (S_index = 0; S_index < 7; S_index++) {
            //置flag为0
            flag=0;
            //获取S.C
            S_num1 = get_num1(blk_S,S_index);
            //若S.C的值仍然和上次一样，则R应该回到原来的位置进行连接
            if (S_num1==last_S_num) {
                R_blkno = last_R_blkno;
                R_index = last_R_index;
            }
            // 否则,更新S_num为S_num1
            else {
                last_S_num = S_num1;
            }


            for (R_blkno=R_start_blkno; R_blkno<=R_end_blkno; R_blkno++) {
                //读入R的一块
                blk_R = readBlockFromDisk(R_blkno, &buf);
                //检查块内所有元组
                for (R_index=0; R_index < 7; R_index++) {
                    //获取R.A
                    R_num1 = get_num1(blk_R,R_index);
                    //R.A大于S.C，出循环
                    if (R_num1>S_num1) {
                        flag=1;
                        break;
                    }
                    else if (R_num1 == S_num1) {
                        //若R.A等于S.C
                        //则连接次数要加1
                        join_cnt++;

                        //如果这次做连接的R值与上次不同,则更新，保证更新之后指示的是第一块
                        if (last_R_num != R_num1){
                            last_R_blkno = R_blkno;
                            last_R_index = R_index;
                            last_R_num = R_num1;
                        }

                        //获取S和R的元组的值，然后写，这里要写两个
                        S_num2=get_num2(blk_S,S_index);
                        R_num2=get_num2(blk_R,R_index);

                        //printf("S(%d %d),R(%d %d)\n",S_num1,S_num2,R_num1,R_num2);

                        //先写S
                        itoa(S_num1,str_3,10);
                        itoa(S_num2,str_4,10);
                        write_num(str_3,str_4,cnt,blk_write);
                        cnt++;
                        if (cnt== 7) {
                            writeBlockToDisk(blk_write,join_write_start_blkno+q,&buf);
                            //printf("write_blkno:%d\n",join_write_start_blkno+q);
                            printf("注：结果写入磁盘：%d\n",join_write_start_blkno+q);
                            cnt= 0;
                            q++;
                            blk_write= getNewBlockInBuffer(&buf);
                            setBlockBlank(blk_write,join_write_start_blkno+q);
                        }

                        //再写R
                        itoa(R_num1,str_3,10);
                        itoa(R_num2,str_4,10);
                        write_num(str_3,str_4,cnt,blk_write);
                        cnt++;
                        if (cnt== 7) {
                            writeBlockToDisk(blk_write,join_write_start_blkno+q,&buf);
                            //printf("write_blkno:%d\n",join_write_start_blkno+q);
                            printf("注：结果写入磁盘：%d\n",join_write_start_blkno+q);
                            cnt= 0;
                            q++;
                            blk_write= getNewBlockInBuffer(&buf);
                            setBlockBlank(blk_write,join_write_start_blkno+q);
                        }

                    }

                }

                //记得释放，不然缓冲区不够用
                freeBlockInBuffer(blk_R, &buf);

                //若flag为1，则R.A已经大于S.C，出循环
                if(flag==1){
                    break;
                }
            }

        }
        //记得释放，不然缓冲区不够用
        freeBlockInBuffer(blk_S, &buf);

    }

    //记得写
    writeBlockToDisk(blk_write,join_write_start_blkno+q,&buf);
    printf("注：结果写入磁盘：%d\n",join_write_start_blkno+q);

    freeBuffer(&buf);

    printf("总共连接%d次。\n", join_cnt);
    printf("\n");

}

//4.实现基于排序的连接操作算法（Sort-Merge-Join）：对关系S和R计算S.C连接R.A ，并统计连接次数，将连接结果存放在磁盘上。
void answer_4(){
    int S_start_blkno=300;
    int S_end_blkno=331;
    int R_start_blkno=200;
    int R_end_blkno=215;
    int join_write_start_blkno=400;

    printf("------------------\n");
    printf("基于排序的连接算法\n");
    printf("------------------\n");

    join_S_R(S_start_blkno,S_end_blkno,R_start_blkno,R_end_blkno,join_write_start_blkno);


}


//交操作
void set_intersection(int S_start_blkno,int S_end_blkno,int R_start_blkno,int R_end_blkno,int write_start_blkno){
    Buffer buf;  /*A buffer*/
    /* Initialize the buffer*/
    if (!initBuffer(520, 64, &buf))
    {
        perror("Buffer Initialization Failed!\n");
    }

    //交个数
    int intersection_cnt=0;

    unsigned char *blk_S;
    unsigned char *blk_R;
    unsigned char *blk_write = getNewBlockInBuffer(&buf);
    setBlockBlank(blk_write,write_start_blkno);

    //S,R的块号和索引
    int S_blkno,S_index,R_blkno,R_index;


    int cnt=0;
    int q=0;

    //S,R的第一列和第二列的数字
    int S_num1,R_num1,S_num2,R_num2;
    char str_1[4],str_2[4];

    //上次做交操作时的S值
    int last_S_num = 0;
    //上次做交操作时的R的第一块的块号和索引
    int last_R_blkno = R_start_blkno;
    int last_R_index = 0;
    //上次做交操作时的R值
    int last_R_num = 0;

    //标记是否写回
    int flag=0;


    for (S_blkno=S_start_blkno;S_blkno<=S_end_blkno;S_blkno++) {
        //读入S的一块
        blk_S = readBlockFromDisk(S_blkno, &buf);
        //检查块内的所有元组
        for (S_index = 0;S_index < 7;S_index++) {
            //置flag为0
            flag=0;
            //取S.C
            S_num1 = get_num1(blk_S,S_index);
            //若S.C的值仍然和上次一样，则R应该回到原来的位置进行交操作
            if (last_S_num == S_num1) {
                R_blkno= last_R_blkno;
                R_index = last_R_index;
            }
            //否则,更新S_num为S_num1
            else {
                last_S_num=S_num1;
            }


            for (R_blkno=R_start_blkno;R_blkno<=R_end_blkno;R_blkno++) {
                //读入R的一块
                blk_R = readBlockFromDisk(R_blkno, &buf);
                //检查块内的所有元组
                for (R_index=0; R_index < 7;R_index++){
                    //获取R.A
                    R_num1 = get_num1(blk_R,R_index);
                    //R.A大于S.C，出循环
                    if (R_num1>S_num1) {
                        break;
                    }
                    else if (R_num1==S_num1) {
                        //若R.A等于S.C
                        //如果这次做交操作的R值与上次不同,则更新，保证更新之后指示的是第一块
                        if (last_R_num!= R_num1) {
                            last_R_blkno= R_blkno;
                            last_R_index= R_index;
                            last_R_num = R_num1;
                        }
                    }

                    //获取S和R的元组的值
                    S_num2 = get_num2(blk_S,S_index);
                    R_num2 = get_num2(blk_R,R_index);

                    //当这两个元组完全一样时，则找到了交集的一个元组，需要写回
                    if (S_num1==R_num1&&S_num2==R_num2) {
                        printf("(X = %d, Y = %d)\n", R_num1,R_num2);
                        flag=1;
                    }
                }
                freeBlockInBuffer(blk_R, &buf);

                //若R.A大于S.C，则不需要再往后找了，出循环
                if (R_num1>S_num1) {
                    break;
                }
            }

            //flag为1表示需要写回
            if(flag==1){
                intersection_cnt++;
                //要写
                itoa(S_num1,str_1,10);
                itoa(S_num2,str_2,10);
                write_num(str_1,str_2,cnt,blk_write);
                cnt++;
                if (cnt== 7) {
                    writeBlockToDisk(blk_write,write_start_blkno+q,&buf);
                    //printf("write_blkno:%d\n",join_write_start_blkno+q);
                    printf("注：结果写入磁盘：%d\n",write_start_blkno+q);
                    cnt= 0;
                    q++;
                    blk_write= getNewBlockInBuffer(&buf);
                    setBlockBlank(blk_write,write_start_blkno+q);
                }

            }
        }

        freeBlockInBuffer(blk_S, &buf);

    }

    //记得写
    writeBlockToDisk(blk_write,write_start_blkno+q,&buf);
    //printf("write_blkno:%d\n",join_write_start_blkno+q);
    printf("注：结果写入磁盘：%d\n",write_start_blkno+q);

    freeBuffer(&buf);

    printf("S和R的交集有%d个元组。\n", intersection_cnt);
    printf("\n");

}


//差操作
void set_difference(int S_start_blkno,int S_end_blkno,int R_start_blkno,int R_end_blkno,int write_start_blkno){
    Buffer buf;  /*A buffer*/
    /* Initialize the buffer*/
    if (!initBuffer(520, 64, &buf))
    {
        perror("Buffer Initialization Failed!\n");
    }

    //差个数
    int difference_cnt=0;

    unsigned char *blk_S;
    unsigned char *blk_R;
    unsigned char *blk_write = getNewBlockInBuffer(&buf);
    setBlockBlank(blk_write,write_start_blkno);

    //S,R的块号和索引
    int S_blkno,S_index,R_blkno,R_index;


    int cnt=0;
    int q=0;

    //S,R的第一列和第二列的数字
    int S_num1,R_num1,S_num2,R_num2;
    char str_1[4],str_2[4];

    //上次做差操作时的S值
    int last_S_num = 0;
    //上次做差操作时的R的第一块的块号和索引
    int last_R_blkno = R_start_blkno;
    int last_R_index = 0;
    //上次做差操作时的R值
    int last_R_num = 0;

    //标记是否写回
    int flag=0;


    for (S_blkno=S_start_blkno;S_blkno<=S_end_blkno;S_blkno++) {
        //读入S的一块
        blk_S = readBlockFromDisk(S_blkno, &buf);
        //检查块内的所有元组
        for (S_index = 0;S_index < 7;S_index++) {
            //置flag为1
            flag=1;
            //取S.C
            S_num1 = get_num1(blk_S,S_index);
            //若S.C的值仍然和上次一样，则R应该回到原来的位置进行差操作
            if (last_S_num == S_num1) {
                R_blkno= last_R_blkno;
                R_index = last_R_index;
            }
            //否则,更新S_num为S_num1
            else {
                last_S_num=S_num1;
            }


            for (R_blkno=R_start_blkno;R_blkno<=R_end_blkno;R_blkno++) {
                //读入R的一块
                blk_R = readBlockFromDisk(R_blkno, &buf);
                //检查块内的所有元组
                for (R_index=0; R_index < 7;R_index++){
                    //获取R.A
                    R_num1 = get_num1(blk_R,R_index);
                    //R.A大于S.C，出循环
                    if (R_num1>S_num1) {
                        break;
                    }
                    else if (R_num1==S_num1) {
                        //若R.A等于S.C
                        //如果这次做差操作的R值与上次不同,则更新，保证更新之后指示的是第一块
                        if (last_R_num!= R_num1) {
                            last_R_blkno= R_blkno;
                            last_R_index= R_index;
                            last_R_num = R_num1;
                        }
                    }

                    //获取S和R的元组的值
                    S_num2 = get_num2(blk_S,S_index);
                    R_num2 = get_num2(blk_R,R_index);

                    //当这两个元组完全一样时，不要写回
                    if (S_num1==R_num1&&S_num2==R_num2) {
                        flag=0;
                    }
                }
                freeBlockInBuffer(blk_R, &buf);

                //若R.A大于S.C，则不需要再往后找了，出循环
                if (R_num1>S_num1) {
                    break;
                }
            }

            //flag为1表示需要写回
            if(flag==1){
                difference_cnt++;
                //要写
                itoa(S_num1,str_1,10);
                itoa(S_num2,str_2,10);
                write_num(str_1,str_2,cnt,blk_write);
                cnt++;
                if (cnt== 7) {
                    writeBlockToDisk(blk_write,write_start_blkno+q,&buf);
                    //printf("write_blkno:%d\n",join_write_start_blkno+q);
                    printf("注：结果写入磁盘：%d\n",write_start_blkno+q);
                    cnt= 0;
                    q++;
                    blk_write= getNewBlockInBuffer(&buf);
                    setBlockBlank(blk_write,write_start_blkno+q);
                }

            }
        }

        freeBlockInBuffer(blk_S, &buf);

    }

    //记得写
    writeBlockToDisk(blk_write,write_start_blkno+q,&buf);
    //printf("write_blkno:%d\n",join_write_start_blkno+q);
    printf("注：结果写入磁盘：%d\n",write_start_blkno+q);

    freeBuffer(&buf);

    printf("S和R的差集（S-R）有%d个元组。\n", difference_cnt);
    printf("\n");

}


//并操作
void set_union(int S_start_blkno,int S_end_blkno,int R_start_blkno,int R_end_blkno,int write_start_blkno){
    Buffer buf;  /*A buffer*/
    /* Initialize the buffer*/
    if (!initBuffer(520, 64, &buf))
    {
        perror("Buffer Initialization Failed!\n");
    }

    //并个数
    int union_cnt=0;

    unsigned char *blk_S;
    unsigned char *blk_R;
    unsigned char *blk_write = getNewBlockInBuffer(&buf);
    setBlockBlank(blk_write,write_start_blkno);

    //S,R的块号和索引
    int S_blkno,S_index,R_blkno,R_index;


    int cnt=0;
    int q=0;

    //S,R的第一列和第二列的数字
    int S_num1,R_num1,S_num2,R_num2;
    char str_1[4],str_2[4];

    //上次做并操作时的S值
    int last_S_num = 0;
    //上次做并操作时的R的第一块的块号和索引
    int last_R_blkno = R_start_blkno;
    int last_R_index = 0;
    //上次做并操作时的R值
    int last_R_num = 0;

    //flag标记是否写回，而flag1标记S.C的值是否和上次一样
    int flag=0;
    int flag1=0;

    //R的元组和索引
    R_index=0;
    R_blkno=R_start_blkno;

    for (S_blkno=S_start_blkno;S_blkno<=S_end_blkno;S_blkno++) {
        //读入S的一块
        blk_S = readBlockFromDisk(S_blkno, &buf);
        //检查块内的所有元组
        for (S_index = 0;S_index < 7;S_index++) {
            //置flag为1
            flag=1;
            //取S.C
            S_num1 = get_num1(blk_S,S_index);
            //若S.C的值仍然和上次一样，则R应该回到原来的位置进行并操作
            if (last_S_num == S_num1) {
                flag1=1;
                R_blkno= last_R_blkno;
                R_index = last_R_index;
            }
            //否则,更新S_num为S_num1
            else {
                last_S_num=S_num1;
                flag1=0;
            }


            for (;R_blkno<=R_end_blkno;R_blkno++) {
                //读入R的一块
                blk_R = readBlockFromDisk(R_blkno, &buf);
                //检查块内的所有元组
                for (; R_index < 7;R_index++){
                    //获取R.A
                    R_num1 = get_num1(blk_R,R_index);
                    //R.A大于S.C，出循环
                    if (R_num1>S_num1) {
                        break;
                    }
                    else if (R_num1==S_num1) {
                        //若R.A等于S.C
                        //如果这次做并操作的R值与上次不同,则更新，保证更新之后指示的是第一块
                        if (last_R_num!= R_num1) {
                            last_R_blkno= R_blkno;
                            last_R_index= R_index;
                            last_R_num = R_num1;
                        }
                    }

                    //获取S和R的元组的值
                    S_num2 = get_num2(blk_S,S_index);
                    R_num2 = get_num2(blk_R,R_index);

                    if(flag1==1&&S_num1==R_num1&&S_num2==R_num2){
                        //当flag1为1且两个元组完全一样，需要将flag置0
                        flag=0;
                    }else if(flag1==1||(S_num1==R_num1&&S_num2==R_num2)){
                        //do nothing
                    }else{
                        union_cnt++;
                        //要写
                        itoa(R_num1,str_1,10);
                        itoa(R_num2,str_2,10);
                        write_num(str_1,str_2,cnt,blk_write);
                        cnt++;
                        if (cnt== 7) {
                            writeBlockToDisk(blk_write,write_start_blkno+q,&buf);
                            //printf("write_blkno:%d\n",join_write_start_blkno+q);
                            printf("注：结果写入磁盘：%d\n",write_start_blkno+q);
                            cnt= 0;
                            q++;
                            blk_write= getNewBlockInBuffer(&buf);
                            setBlockBlank(blk_write,write_start_blkno+q);
                        }
                    }
                }
                freeBlockInBuffer(blk_R, &buf);

                //若R.A大于S.C，则不需要再往后找了，出循环
                if (R_num1>S_num1) {
                    break;
                }else{
                    //否则，下次应当从0开始
                    R_index=0;
                }
            }

            //flag为1表示需要写回
            if(flag==1){
                union_cnt++;
                //要写
                itoa(S_num1,str_1,10);
                itoa(S_num2,str_2,10);
                write_num(str_1,str_2,cnt,blk_write);
                cnt++;
                if (cnt== 7) {
                    writeBlockToDisk(blk_write,write_start_blkno+q,&buf);
                    //printf("write_blkno:%d\n",join_write_start_blkno+q);
                    printf("注：结果写入磁盘：%d\n",write_start_blkno+q);
                    cnt= 0;
                    q++;
                    blk_write= getNewBlockInBuffer(&buf);
                    setBlockBlank(blk_write,write_start_blkno+q);
                }

            }
        }

        freeBlockInBuffer(blk_S, &buf);

    }

    //记得写
    writeBlockToDisk(blk_write,write_start_blkno+q,&buf);
    //printf("write_blkno:%d\n",join_write_start_blkno+q);
    printf("注：结果写入磁盘：%d\n",write_start_blkno+q);

    freeBuffer(&buf);

    printf("S和R的并集有%d个元组。\n", union_cnt);
    printf("\n");
}

//5. 实现基于排序或散列的两趟扫描算法，实现集合操作算法：并,交,差。将结果存放在磁盘上，并统计并、交、差操作后的元组个数。
void answer_5_and_addition(){
    int S_start_blkno=300;
    int S_end_blkno=331;
    int R_start_blkno=200;
    int R_end_blkno=215;
    int operation_write_start_blkno;

    printf("----------------------\n");
    printf("基于排序的集合的交算法\n");
    printf("----------------------\n");

    operation_write_start_blkno=500;
    set_intersection(S_start_blkno,S_end_blkno,R_start_blkno,R_end_blkno,operation_write_start_blkno);

    printf("----------------------\n");
    printf("基于排序的集合的差算法\n");
    printf("----------------------\n");

    operation_write_start_blkno=550;
    set_difference(S_start_blkno,S_end_blkno,R_start_blkno,R_end_blkno,operation_write_start_blkno);

    printf("----------------------\n");
    printf("基于排序的集合的并算法\n");
    printf("----------------------\n");

    operation_write_start_blkno=600;
    set_union(S_start_blkno,S_end_blkno,R_start_blkno,R_end_blkno,operation_write_start_blkno);

}

//主函数，调用以上所写函数来解决问题
int main(int argc, char **argv){

    answer_1();

    answer_2();

    answer_3();

    answer_4();

    answer_5_and_addition();

    return 0;

}














