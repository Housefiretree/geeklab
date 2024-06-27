#include <stdlib.h>
#include <stdio.h>
#include "extmem.h"


#define FINISHED 999

//���ݿ��������ø�Ԫ��ĵ�1������
int get_num1(unsigned char *blk,int index){
    char str[4];
    int k;
    for (k = 0; k < 4; k++){
        str[k] = *(blk + index*8 + k);
    }
    int X = atoi(str);
    return X;
}

//���ݿ��������ø�Ԫ��ĵ�2������
int get_num2(unsigned char *blk,int index){
    char str[4];
    int z;
    for (z = 0; z < 4; z++){
        str[z] = *(blk + index*8 + 4 + z);
    }
    int X = atoi(str);
    return X;
}

//��Ԫ��д�����Ӧλ��
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

//�ѿ��������ղ����ڿ��ĩβλ��д����һ��Ŀ��
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


//1.����ExtMem����⣬ʹ��C����ʵ�����������㷨��ѡ��S.C=107��Ԫ�飬��¼IO��д����������ѡ��������ڴ����ϡ�
//��ģ��ʵ�� select S.C,S.D from S where S.C = 107��
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

    //���� 17.blk �� 48.blk Ϊ��ϵ S ��Ԫ������,���ν��в�ѯ
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
    printf("��������������ѡ���㷨 S.C=107\n");
    printf("------------------------------\n");



    for(j=17;j<49;j++){
        blk = readBlockFromDisk(j, &buf);
        printf("�������ݿ�%d\n",j);
        for (i = 0; i < 7; i++) //һ��blk��7��Ԫ���һ����ַ
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
                    printf("ע�����д�����:%d\n",100+q);
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
        //��Ҫ�ͷſ飬���򻺳���������
        freeBlockInBuffer(blk,&buf);
    }

    //�ǵ�Ҫд��
    writeBlockToDisk(blk_write,100+q,&buf);
    printf("ע�����д�����:%d\n",100+q);
    freeBuffer(&buf);

    printf("\n");
    printf("����ѡ��������Ԫ��һ��%d����\n",tuple_cnt);
    printf("IO��дһ��%d�Ρ�\n", buf.numIO);
    printf("\n");
}

//�������㷨
void internal_sort(int cmp_start_blkno,int cmp_end_blkno,int write_start_blkno,int group_num){
    Buffer buf;  /*A buffer*/
    /* Initialize the buffer*/
    if (!initBuffer(520, 64, &buf))
    {
        perror("Buffer Initialization Failed!\n");
    }


    //��ĸ����͵ȴ��ȽϵĿ�
    int blks_num;
    unsigned char *blks_to_cmp[6];


    // ��ǰ���
    int curr_blkno = cmp_start_blkno;

    // Ҫ�Ƚϵ�Ԫ��Ŀ�ź�����
    int cmp_blkno1, index1,cmp_blkno2, index2;

    // ����Ҫ�Ƚϵ����Լ����ǵĵڶ��е�����
    int cmp_num1,cmp_num2,num1_2,num2_2;
    char str1[4],str2[4];

    int q=0;

    // ��������
    int i;
    int j;
    int k;
    for (i = 0; i < group_num; i++) {
        //ѭ�������
        for (blks_num=0;blks_num<6&&curr_blkno<=cmp_end_blkno;blks_num++) {
            blks_to_cmp[blks_num] = readBlockFromDisk(curr_blkno, &buf);
            curr_blkno++;
        }
        //ѭ���Ƚ�
        for (j=0;j<blks_num *7-1;j++) {
            //�����һ�����ֵĿ�ź�����
            cmp_blkno1=j/7;
            index1=j%7;

            for (k=j+1;k<blks_num*7;k++) {
                //�ڶ������ֵĿ�ź�����
                cmp_blkno2=k/7;
                index2=k%7;

                //��ȡҪ�Ƚϵ���������
                cmp_num1 = get_num1(blks_to_cmp[cmp_blkno1], index1);
                cmp_num2 = get_num1(blks_to_cmp[cmp_blkno2], index2);
                //printf("%d cmp with %d\n",cmp_num1,cmp_num2);

                //����һ�����ִ��ڵڶ������֣�����Ҫ���н�����������д���Է���λ��
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

        // ����������д�����̿���
        for (j = 0; j < blks_num; j++) {

            //�ǵ�Ҫ�Ȱ���һ��Ŀ��д�ã���Ȼ�������������
            itoa(write_start_blkno+q+1,str1,10);
            int m;
            for(m=0;m<4;m++){
                str2[m]=' ';
            }
            write_num(str1,str2,7,blks_to_cmp[j]);

            writeBlockToDisk(blks_to_cmp[j], write_start_blkno+q, &buf);

            //printf("������д����:%d\n",write_start_blkno+q);

            q++;
            //printf("\n");

        }

    }

    freeBuffer(&buf);
}


//�������㷨
void total_sort(int sort_start_blkno,int sort_end_blkno,int write_start_blkno,int group_num){
    Buffer buf;  /*A buffer*/
    /* Initialize the buffer*/
    if (!initBuffer(520, 64, &buf))
    {
        perror("Buffer Initialization Failed!\n");
    }


    //������д���
    unsigned char *blk_sort=getNewBlockInBuffer(&buf);
    unsigned char *blk_write=getNewBlockInBuffer(&buf);


    //������и����ݶ�Ӧ�Ŀ������
    unsigned char *blks_to_sort[8];
    int blks_to_sort_index[8];

    //�����ţ���ʼΪ�����write_start_blkno
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
        //��֪��Ϊʲôstr_1����û�취ת��������ֱ����str_2��
        write_num(str_2,str_2,i,blk_sort);
    }

    int group_index[8];

    //����
    for(i=0;i<group_num;i++){
        //����read block��������Si�ĵ�һ�����Mi��
        blks_to_sort[i] = readBlockFromDisk(sort_start_blkno + i * 6, &buf);

        //�����һ��Ԫ�ش���Mcompare�ĵ�ith��λ��
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
        //��Mcmp��m��Ԫ�ص���Сֵ����λ��i
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
            //��min_num����FINISHED����˵��û���ҵ���Сֵ��ֱ���˳�
            //printf("break;\n");
            break;
        }else{
            //��������ҵ�����Сֵ����λ��i

            //����ith��λ�õ�Ԫ�ش���Moutput�е�Poutputλ�ã�Poutputָ�밴����ָ����һλ��
            num2 = get_num2(blk_sort,min_num_index);
            itoa(min_num,str_1,10);
            itoa(num2,str_2,10);
            //printf("%d\n",min_num);
            //printf("%s %s\n",str_1,str_2);
            write_num(str_1,str_2,Poutput,blk_write);
            Poutput++;

            //���Poutputָ�����λ��
            if(Poutput==7){
                //����write block������Moutputд�ش���
                itoa(write_blkno+1,str_1,10);

                for(m=0;m<4;m++){
                    str_2[m]=' ';
                }
                write_num(str_1,str_2,7,blk_write);

                writeBlockToDisk(blk_write,write_blkno,&buf);
                printf("ע�����д�����:%d\n",write_blkno);


                blk_write = getNewBlockInBuffer(&buf);
                //setBlockBlank(blk_write,write_blkno);

                /**printf("write block to disk\n");

                for (r=0;r<63;r++){
                    printf("%c",*(blk_write+r));
                }
                printf("\n");*/

                //��PoutputΪ����ڴ�����ʼλ��
                Poutput=0;
                write_blkno++;
                //Ȼ���������
            }

            //���Mi����һ��Ԫ��
            if(blks_to_sort_index[min_num_index]<7){
                //��Mi��һ��Ԫ�ش���Mcmp�ĵ�ith��λ��
                num1=get_num1(blks_to_sort[min_num_index],blks_to_sort_index[min_num_index]);
                num2=get_num2(blks_to_sort[min_num_index],blks_to_sort_index[min_num_index]);
                blks_to_sort_index[min_num_index]++;
                itoa(num1,str_1,10);
                itoa(num2,str_2,10);
                write_num(str_1,str_2,min_num_index,blk_sort);
                //����ѭ��
            }else{
                //���Si����һ�飬��Si�Ȳ���ÿ������һ��Ҳ������������һ�飨���һ�鲻һ������Ҫ�����жϣ�
                if((group_index[min_num_index]-sort_start_blkno+1)%6>0 && group_index[min_num_index]<sort_end_blkno){
                    freeBlockInBuffer(blks_to_sort[min_num_index], &buf);
                    //��һ��
                    group_index[min_num_index]++;
                    //����read block�������Si����һ�鲢����Mi
                    blks_to_sort[min_num_index] = readBlockFromDisk(group_index[min_num_index], &buf);
                    //����һ��ĵ�һ��Ԫ��
                    num1=get_num1(blks_to_sort[min_num_index],0);
                    num2=get_num2(blks_to_sort[min_num_index],0);
                    itoa(num1,str_1,10);
                    itoa(num2,str_2,10);
                    write_num(str_1,str_2,min_num_index,blk_sort);
                    blks_to_sort_index[min_num_index] = 1;

                }else{
                    //���򣬷�������ֵ��FINISHED����ʾSi�Ӽ��ϴ�����ϣ�MiΪ�գ���ʹMcmp�ĵ�ith��λ��Ϊ������ֵ��
                    //������Ԫ�ز�����Mcmp�ıȽϲ���
                    num2=FINISHED;
                    itoa(num2,str_2,10);
                    //printf("done\n");
                    //printf("%s %s",str_1,str_2);
                    write_num(str_2,str_2,min_num_index,blk_sort);
                    //Ȼ����ת����7������ѭ��
                }

            }

        }

    }

    freeBuffer(&buf);
}



//2.ʵ�����׶ζ�·�鲢�����㷨��TPMMS���������ڴ滺��������ϵR��S�ֱ����򣬲��������Ľ������ڴ����ϡ�
void answer_2(){

    printf("-----\n");
    printf("TPMMS\n");
    printf("-----\n");
    printf("\n");

    //������Ŀ�ţ���ʼ��źͽ������
    int cmp_start_blkno;
    int cmp_end_blkno;

    //���������������Ҫд��Ŀ�Ŀ�ͷ
    int internal_sort_write_start_blkno;
    int total_sort_write_start_blkno;

    //������Ŀ�ţ���ʼ��źͽ������
    int sort_start_blkno;
    int sort_end_blkno;

    //���������
    int group_num;


    //��R��������
    cmp_start_blkno=1;
    cmp_end_blkno=16;

    //������Ľ��д��150��165�飬������Ľ��д��200��215��
    internal_sort_write_start_blkno=150;
    total_sort_write_start_blkno=200;

    sort_start_blkno=150;
    sort_end_blkno=165;

    group_num = (cmp_end_blkno - cmp_start_blkno) / 6 + 1;

    printf("---------\n");
    printf("��ϵR����\n");
    printf("---------\n");

    //������
    internal_sort(cmp_start_blkno,cmp_end_blkno,internal_sort_write_start_blkno,group_num);
    //������
    total_sort(sort_start_blkno,sort_end_blkno,total_sort_write_start_blkno,group_num);

    printf("\n");






    //ͬ����S��������
    cmp_start_blkno=17;
    cmp_end_blkno=48;

    //������Ľ��д��250��281�飬������Ľ��д��300��331��
    internal_sort_write_start_blkno=250;
    total_sort_write_start_blkno=300;

    sort_start_blkno=250;
    sort_end_blkno=281;

    group_num = (cmp_end_blkno - cmp_start_blkno)/6+1;

    printf("---------\n");
    printf("��ϵS����\n");
    printf("---------\n");

    //������
    internal_sort(cmp_start_blkno,cmp_end_blkno,internal_sort_write_start_blkno,group_num);
    //������
    total_sort(sort_start_blkno,sort_end_blkno,total_sort_write_start_blkno,group_num);

    printf("\n");


}

//��������
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

    //��������������
    int X,Y;
    char str_1[4],str_2[4];

    int cnt=0;
    int q=0;

    //�Կ�ʼ�鵽�����鴴������
    for (int i=start_blkno;i<end_blkno+1;i++) {
        //�����
        blk=readBlockFromDisk(i,&buf);
        //Xָʾ���
        X=i;
        //Yָʾ��һ�е����ּ�S.C
        Y=get_num1(blk,0);
        //�ǵ��ͷţ���Ȼ������������
        freeBlockInBuffer(blk, &buf);

        //printf("num(%d %d)\n",X,Y);

        //д
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
    //�ǵ�Ҫд��
    writeBlockToDisk(blk_write,write_start_blkno+q,&buf);
    //printf("write_blkno:%d\n",write_start_blkno+q);


    //printf("����������IO����: %d\n", buf.numIO);
    freeBuffer(&buf);

    int write_end_blkno=write_start_blkno+q;
    return write_end_blkno;

}

//����������Ԫ��
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

    //��Ҫ�ҵ���С��ź������
    int min_blkno_find = 0;
    int max_blkno_find = 0;

    int X,Y;
    char str_1[4],str_2[4];

    //flag������Ƿ��ҵ������
    int max_blkno_find_flag=0;

    //��¼һ���м�������Ҫ���Ԫ��
    int target_cnt=0;

    for(int i=index_write_start_blkno;i<index_write_end_blkno+1;i++) {
        printf("����������%d\n",i);
        //����������
        blk = readBlockFromDisk(i, &buf);
        for (int j = 0; j < 7; j++) {
            //���ÿ��Ԫ��
            X = get_num1(blk,j);
            Y = get_num2(blk,j);
            if (Y<target_num1) {
                //���ڴ�С�������򣬵�YС��Ŀ��ֵʱ����X������С���
                min_blkno_find = X;
            }else if(Y>target_num1) {
                //���ڴ�С�������򣬵�Y����Ŀ��ֵʱ����X���������
                max_blkno_find = X;
                //��flag��Ϊ1����ʾ�Ѿ��ҵ��������
                max_blkno_find_flag=1;
                //������ֻ����󣬲������������ˣ���ѭ��
                break;
            }
        }
        freeBlockInBuffer(blk, &buf);
        //����Ѿ��ҵ�����ţ����µ��������е���ֻ����󣬲������������ˣ���ѭ��
        if (max_blkno_find_flag == 1) {
            break;
        }
    }

    //����С��ŵ��������
    for (int i=min_blkno_find;i<max_blkno_find;i++) {
        //�����
        blk = readBlockFromDisk(i, &buf);
        printf("�������ݿ�%d\n", i);
        for (int j = 0; j < 7; j++) {
            //���ÿ��Ԫ��
            X=get_num1(blk,j);
            Y=get_num2(blk,j);

            if (X==target_num1) {
                //�ҵ���һ������Ҫд
                target_cnt++;

                printf("(X=%d, Y=%d)\n", X,Y);
                itoa(X,str_1,10);
                itoa(Y,str_2,10);
                write_num(str_1,str_2,cnt,blk_write);
                cnt++;

                if (cnt== 7) {
                    writeBlockToDisk(blk_write,find_write_start_blkno+q,&buf);
                    printf("ע�����д����̿�%d\n",find_write_start_blkno+q);
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

    //�ǵ�Ҫд��
    writeBlockToDisk(blk_write,find_write_start_blkno+q,&buf);
    printf("ע�����д����̿�%d\n",find_write_start_blkno+q);
    //printf("write_blkno:%d\n",find_write_start_blkno+q);

    freeBuffer(&buf);

    printf("����ѡ��������Ԫ��һ��%d����\n", target_cnt);
    printf("IO��дһ��%d�Ρ�\n", buf.numIO);
    printf("\n");

}


//3.ʵ�ֻ��������Ĺ�ϵѡ���㷨�����ã�2���е�������Ϊ��ϵR��S�ֱ��������ļ������������ļ�ѡ��S.C=107��Ԫ�飬����ѡ��������ڴ����ϡ�
//��¼IO��д�������루1���еĽ���Աȡ�
void answer_3(){

    //��������
    int index_start_blkno=300;
    int index_end_blkno=331;
    int index_write_start_blkno=350;
    int index_write_end_blkno;
    index_write_end_blkno=create_index(index_start_blkno,index_end_blkno,index_write_start_blkno);
    //printf("index write end:%d\n",index_write_end_blkno);


    printf("--------------------------\n");
    printf("����������ѡ���㷨 S.C=107\n");
    printf("--------------------------\n");

    //ͨ������Ѱ��S.C=107��Ԫ��
    int target_num1=107;
    int find_write_start_blkno=360;
    find_tuple_by_index(target_num1,index_write_start_blkno,index_write_end_blkno,find_write_start_blkno);

}


//����S��R
void join_S_R(int S_start_blkno,int S_end_blkno,int R_start_blkno,int R_end_blkno,int join_write_start_blkno){
    Buffer buf;  /*A buffer*/
    /* Initialize the buffer*/
    if (!initBuffer(520, 64, &buf))
    {
        perror("Buffer Initialization Failed!\n");
    }


    //���Ӵ���
    int join_cnt=0;

    unsigned char *blk_S;
    unsigned char *blk_R;
    unsigned char *blk_write = getNewBlockInBuffer(&buf);
    setBlockBlank(blk_write,join_write_start_blkno);

    //S,R�Ŀ�ź�����
    int S_blkno,S_index,R_blkno,R_index;


    int cnt=0;
    int q=0;

    //S,R�ĵ�һ�к͵ڶ��е�����
    int S_num1,R_num1,S_num2,R_num2;
    char str_3[4],str_4[4],str_5[4];

    //�ϴ�������ʱ��Sֵ
    int last_S_num = 0;
    //�ϴ�������ʱ��R�ĵ�һ��Ŀ�ź�����
    int last_R_blkno = R_start_blkno;
    int last_R_index = 0;
    //�ϴ�������ʱ��Rֵ
    int last_R_num = 0;

    //���R.A�Ƿ��Ѿ�����S.C
    int flag=0;
    for (S_blkno=S_start_blkno; S_blkno <= S_end_blkno;S_blkno++) {
        //����S��һ��
        blk_S = readBlockFromDisk(S_blkno, &buf);
        //�����ڵ�����Ԫ��
        for (S_index = 0; S_index < 7; S_index++) {
            //��flagΪ0
            flag=0;
            //��ȡS.C
            S_num1 = get_num1(blk_S,S_index);
            //��S.C��ֵ��Ȼ���ϴ�һ������RӦ�ûص�ԭ����λ�ý�������
            if (S_num1==last_S_num) {
                R_blkno = last_R_blkno;
                R_index = last_R_index;
            }
            // ����,����S_numΪS_num1
            else {
                last_S_num = S_num1;
            }


            for (R_blkno=R_start_blkno; R_blkno<=R_end_blkno; R_blkno++) {
                //����R��һ��
                blk_R = readBlockFromDisk(R_blkno, &buf);
                //����������Ԫ��
                for (R_index=0; R_index < 7; R_index++) {
                    //��ȡR.A
                    R_num1 = get_num1(blk_R,R_index);
                    //R.A����S.C����ѭ��
                    if (R_num1>S_num1) {
                        flag=1;
                        break;
                    }
                    else if (R_num1 == S_num1) {
                        //��R.A����S.C
                        //�����Ӵ���Ҫ��1
                        join_cnt++;

                        //�����������ӵ�Rֵ���ϴβ�ͬ,����£���֤����֮��ָʾ���ǵ�һ��
                        if (last_R_num != R_num1){
                            last_R_blkno = R_blkno;
                            last_R_index = R_index;
                            last_R_num = R_num1;
                        }

                        //��ȡS��R��Ԫ���ֵ��Ȼ��д������Ҫд����
                        S_num2=get_num2(blk_S,S_index);
                        R_num2=get_num2(blk_R,R_index);

                        //printf("S(%d %d),R(%d %d)\n",S_num1,S_num2,R_num1,R_num2);

                        //��дS
                        itoa(S_num1,str_3,10);
                        itoa(S_num2,str_4,10);
                        write_num(str_3,str_4,cnt,blk_write);
                        cnt++;
                        if (cnt== 7) {
                            writeBlockToDisk(blk_write,join_write_start_blkno+q,&buf);
                            //printf("write_blkno:%d\n",join_write_start_blkno+q);
                            printf("ע�����д����̣�%d\n",join_write_start_blkno+q);
                            cnt= 0;
                            q++;
                            blk_write= getNewBlockInBuffer(&buf);
                            setBlockBlank(blk_write,join_write_start_blkno+q);
                        }

                        //��дR
                        itoa(R_num1,str_3,10);
                        itoa(R_num2,str_4,10);
                        write_num(str_3,str_4,cnt,blk_write);
                        cnt++;
                        if (cnt== 7) {
                            writeBlockToDisk(blk_write,join_write_start_blkno+q,&buf);
                            //printf("write_blkno:%d\n",join_write_start_blkno+q);
                            printf("ע�����д����̣�%d\n",join_write_start_blkno+q);
                            cnt= 0;
                            q++;
                            blk_write= getNewBlockInBuffer(&buf);
                            setBlockBlank(blk_write,join_write_start_blkno+q);
                        }

                    }

                }

                //�ǵ��ͷţ���Ȼ������������
                freeBlockInBuffer(blk_R, &buf);

                //��flagΪ1����R.A�Ѿ�����S.C����ѭ��
                if(flag==1){
                    break;
                }
            }

        }
        //�ǵ��ͷţ���Ȼ������������
        freeBlockInBuffer(blk_S, &buf);

    }

    //�ǵ�д
    writeBlockToDisk(blk_write,join_write_start_blkno+q,&buf);
    printf("ע�����д����̣�%d\n",join_write_start_blkno+q);

    freeBuffer(&buf);

    printf("�ܹ�����%d�Ρ�\n", join_cnt);
    printf("\n");

}

//4.ʵ�ֻ�����������Ӳ����㷨��Sort-Merge-Join�����Թ�ϵS��R����S.C����R.A ����ͳ�����Ӵ����������ӽ������ڴ����ϡ�
void answer_4(){
    int S_start_blkno=300;
    int S_end_blkno=331;
    int R_start_blkno=200;
    int R_end_blkno=215;
    int join_write_start_blkno=400;

    printf("------------------\n");
    printf("��������������㷨\n");
    printf("------------------\n");

    join_S_R(S_start_blkno,S_end_blkno,R_start_blkno,R_end_blkno,join_write_start_blkno);


}


//������
void set_intersection(int S_start_blkno,int S_end_blkno,int R_start_blkno,int R_end_blkno,int write_start_blkno){
    Buffer buf;  /*A buffer*/
    /* Initialize the buffer*/
    if (!initBuffer(520, 64, &buf))
    {
        perror("Buffer Initialization Failed!\n");
    }

    //������
    int intersection_cnt=0;

    unsigned char *blk_S;
    unsigned char *blk_R;
    unsigned char *blk_write = getNewBlockInBuffer(&buf);
    setBlockBlank(blk_write,write_start_blkno);

    //S,R�Ŀ�ź�����
    int S_blkno,S_index,R_blkno,R_index;


    int cnt=0;
    int q=0;

    //S,R�ĵ�һ�к͵ڶ��е�����
    int S_num1,R_num1,S_num2,R_num2;
    char str_1[4],str_2[4];

    //�ϴ���������ʱ��Sֵ
    int last_S_num = 0;
    //�ϴ���������ʱ��R�ĵ�һ��Ŀ�ź�����
    int last_R_blkno = R_start_blkno;
    int last_R_index = 0;
    //�ϴ���������ʱ��Rֵ
    int last_R_num = 0;

    //����Ƿ�д��
    int flag=0;


    for (S_blkno=S_start_blkno;S_blkno<=S_end_blkno;S_blkno++) {
        //����S��һ��
        blk_S = readBlockFromDisk(S_blkno, &buf);
        //�����ڵ�����Ԫ��
        for (S_index = 0;S_index < 7;S_index++) {
            //��flagΪ0
            flag=0;
            //ȡS.C
            S_num1 = get_num1(blk_S,S_index);
            //��S.C��ֵ��Ȼ���ϴ�һ������RӦ�ûص�ԭ����λ�ý��н�����
            if (last_S_num == S_num1) {
                R_blkno= last_R_blkno;
                R_index = last_R_index;
            }
            //����,����S_numΪS_num1
            else {
                last_S_num=S_num1;
            }


            for (R_blkno=R_start_blkno;R_blkno<=R_end_blkno;R_blkno++) {
                //����R��һ��
                blk_R = readBlockFromDisk(R_blkno, &buf);
                //�����ڵ�����Ԫ��
                for (R_index=0; R_index < 7;R_index++){
                    //��ȡR.A
                    R_num1 = get_num1(blk_R,R_index);
                    //R.A����S.C����ѭ��
                    if (R_num1>S_num1) {
                        break;
                    }
                    else if (R_num1==S_num1) {
                        //��R.A����S.C
                        //����������������Rֵ���ϴβ�ͬ,����£���֤����֮��ָʾ���ǵ�һ��
                        if (last_R_num!= R_num1) {
                            last_R_blkno= R_blkno;
                            last_R_index= R_index;
                            last_R_num = R_num1;
                        }
                    }

                    //��ȡS��R��Ԫ���ֵ
                    S_num2 = get_num2(blk_S,S_index);
                    R_num2 = get_num2(blk_R,R_index);

                    //��������Ԫ����ȫһ��ʱ�����ҵ��˽�����һ��Ԫ�飬��Ҫд��
                    if (S_num1==R_num1&&S_num2==R_num2) {
                        printf("(X = %d, Y = %d)\n", R_num1,R_num2);
                        flag=1;
                    }
                }
                freeBlockInBuffer(blk_R, &buf);

                //��R.A����S.C������Ҫ���������ˣ���ѭ��
                if (R_num1>S_num1) {
                    break;
                }
            }

            //flagΪ1��ʾ��Ҫд��
            if(flag==1){
                intersection_cnt++;
                //Ҫд
                itoa(S_num1,str_1,10);
                itoa(S_num2,str_2,10);
                write_num(str_1,str_2,cnt,blk_write);
                cnt++;
                if (cnt== 7) {
                    writeBlockToDisk(blk_write,write_start_blkno+q,&buf);
                    //printf("write_blkno:%d\n",join_write_start_blkno+q);
                    printf("ע�����д����̣�%d\n",write_start_blkno+q);
                    cnt= 0;
                    q++;
                    blk_write= getNewBlockInBuffer(&buf);
                    setBlockBlank(blk_write,write_start_blkno+q);
                }

            }
        }

        freeBlockInBuffer(blk_S, &buf);

    }

    //�ǵ�д
    writeBlockToDisk(blk_write,write_start_blkno+q,&buf);
    //printf("write_blkno:%d\n",join_write_start_blkno+q);
    printf("ע�����д����̣�%d\n",write_start_blkno+q);

    freeBuffer(&buf);

    printf("S��R�Ľ�����%d��Ԫ�顣\n", intersection_cnt);
    printf("\n");

}


//�����
void set_difference(int S_start_blkno,int S_end_blkno,int R_start_blkno,int R_end_blkno,int write_start_blkno){
    Buffer buf;  /*A buffer*/
    /* Initialize the buffer*/
    if (!initBuffer(520, 64, &buf))
    {
        perror("Buffer Initialization Failed!\n");
    }

    //�����
    int difference_cnt=0;

    unsigned char *blk_S;
    unsigned char *blk_R;
    unsigned char *blk_write = getNewBlockInBuffer(&buf);
    setBlockBlank(blk_write,write_start_blkno);

    //S,R�Ŀ�ź�����
    int S_blkno,S_index,R_blkno,R_index;


    int cnt=0;
    int q=0;

    //S,R�ĵ�һ�к͵ڶ��е�����
    int S_num1,R_num1,S_num2,R_num2;
    char str_1[4],str_2[4];

    //�ϴ��������ʱ��Sֵ
    int last_S_num = 0;
    //�ϴ��������ʱ��R�ĵ�һ��Ŀ�ź�����
    int last_R_blkno = R_start_blkno;
    int last_R_index = 0;
    //�ϴ��������ʱ��Rֵ
    int last_R_num = 0;

    //����Ƿ�д��
    int flag=0;


    for (S_blkno=S_start_blkno;S_blkno<=S_end_blkno;S_blkno++) {
        //����S��һ��
        blk_S = readBlockFromDisk(S_blkno, &buf);
        //�����ڵ�����Ԫ��
        for (S_index = 0;S_index < 7;S_index++) {
            //��flagΪ1
            flag=1;
            //ȡS.C
            S_num1 = get_num1(blk_S,S_index);
            //��S.C��ֵ��Ȼ���ϴ�һ������RӦ�ûص�ԭ����λ�ý��в����
            if (last_S_num == S_num1) {
                R_blkno= last_R_blkno;
                R_index = last_R_index;
            }
            //����,����S_numΪS_num1
            else {
                last_S_num=S_num1;
            }


            for (R_blkno=R_start_blkno;R_blkno<=R_end_blkno;R_blkno++) {
                //����R��һ��
                blk_R = readBlockFromDisk(R_blkno, &buf);
                //�����ڵ�����Ԫ��
                for (R_index=0; R_index < 7;R_index++){
                    //��ȡR.A
                    R_num1 = get_num1(blk_R,R_index);
                    //R.A����S.C����ѭ��
                    if (R_num1>S_num1) {
                        break;
                    }
                    else if (R_num1==S_num1) {
                        //��R.A����S.C
                        //���������������Rֵ���ϴβ�ͬ,����£���֤����֮��ָʾ���ǵ�һ��
                        if (last_R_num!= R_num1) {
                            last_R_blkno= R_blkno;
                            last_R_index= R_index;
                            last_R_num = R_num1;
                        }
                    }

                    //��ȡS��R��Ԫ���ֵ
                    S_num2 = get_num2(blk_S,S_index);
                    R_num2 = get_num2(blk_R,R_index);

                    //��������Ԫ����ȫһ��ʱ����Ҫд��
                    if (S_num1==R_num1&&S_num2==R_num2) {
                        flag=0;
                    }
                }
                freeBlockInBuffer(blk_R, &buf);

                //��R.A����S.C������Ҫ���������ˣ���ѭ��
                if (R_num1>S_num1) {
                    break;
                }
            }

            //flagΪ1��ʾ��Ҫд��
            if(flag==1){
                difference_cnt++;
                //Ҫд
                itoa(S_num1,str_1,10);
                itoa(S_num2,str_2,10);
                write_num(str_1,str_2,cnt,blk_write);
                cnt++;
                if (cnt== 7) {
                    writeBlockToDisk(blk_write,write_start_blkno+q,&buf);
                    //printf("write_blkno:%d\n",join_write_start_blkno+q);
                    printf("ע�����д����̣�%d\n",write_start_blkno+q);
                    cnt= 0;
                    q++;
                    blk_write= getNewBlockInBuffer(&buf);
                    setBlockBlank(blk_write,write_start_blkno+q);
                }

            }
        }

        freeBlockInBuffer(blk_S, &buf);

    }

    //�ǵ�д
    writeBlockToDisk(blk_write,write_start_blkno+q,&buf);
    //printf("write_blkno:%d\n",join_write_start_blkno+q);
    printf("ע�����д����̣�%d\n",write_start_blkno+q);

    freeBuffer(&buf);

    printf("S��R�Ĳ��S-R����%d��Ԫ�顣\n", difference_cnt);
    printf("\n");

}


//������
void set_union(int S_start_blkno,int S_end_blkno,int R_start_blkno,int R_end_blkno,int write_start_blkno){
    Buffer buf;  /*A buffer*/
    /* Initialize the buffer*/
    if (!initBuffer(520, 64, &buf))
    {
        perror("Buffer Initialization Failed!\n");
    }

    //������
    int union_cnt=0;

    unsigned char *blk_S;
    unsigned char *blk_R;
    unsigned char *blk_write = getNewBlockInBuffer(&buf);
    setBlockBlank(blk_write,write_start_blkno);

    //S,R�Ŀ�ź�����
    int S_blkno,S_index,R_blkno,R_index;


    int cnt=0;
    int q=0;

    //S,R�ĵ�һ�к͵ڶ��е�����
    int S_num1,R_num1,S_num2,R_num2;
    char str_1[4],str_2[4];

    //�ϴ���������ʱ��Sֵ
    int last_S_num = 0;
    //�ϴ���������ʱ��R�ĵ�һ��Ŀ�ź�����
    int last_R_blkno = R_start_blkno;
    int last_R_index = 0;
    //�ϴ���������ʱ��Rֵ
    int last_R_num = 0;

    //flag����Ƿ�д�أ���flag1���S.C��ֵ�Ƿ���ϴ�һ��
    int flag=0;
    int flag1=0;

    //R��Ԫ�������
    R_index=0;
    R_blkno=R_start_blkno;

    for (S_blkno=S_start_blkno;S_blkno<=S_end_blkno;S_blkno++) {
        //����S��һ��
        blk_S = readBlockFromDisk(S_blkno, &buf);
        //�����ڵ�����Ԫ��
        for (S_index = 0;S_index < 7;S_index++) {
            //��flagΪ1
            flag=1;
            //ȡS.C
            S_num1 = get_num1(blk_S,S_index);
            //��S.C��ֵ��Ȼ���ϴ�һ������RӦ�ûص�ԭ����λ�ý��в�����
            if (last_S_num == S_num1) {
                flag1=1;
                R_blkno= last_R_blkno;
                R_index = last_R_index;
            }
            //����,����S_numΪS_num1
            else {
                last_S_num=S_num1;
                flag1=0;
            }


            for (;R_blkno<=R_end_blkno;R_blkno++) {
                //����R��һ��
                blk_R = readBlockFromDisk(R_blkno, &buf);
                //�����ڵ�����Ԫ��
                for (; R_index < 7;R_index++){
                    //��ȡR.A
                    R_num1 = get_num1(blk_R,R_index);
                    //R.A����S.C����ѭ��
                    if (R_num1>S_num1) {
                        break;
                    }
                    else if (R_num1==S_num1) {
                        //��R.A����S.C
                        //����������������Rֵ���ϴβ�ͬ,����£���֤����֮��ָʾ���ǵ�һ��
                        if (last_R_num!= R_num1) {
                            last_R_blkno= R_blkno;
                            last_R_index= R_index;
                            last_R_num = R_num1;
                        }
                    }

                    //��ȡS��R��Ԫ���ֵ
                    S_num2 = get_num2(blk_S,S_index);
                    R_num2 = get_num2(blk_R,R_index);

                    if(flag1==1&&S_num1==R_num1&&S_num2==R_num2){
                        //��flag1Ϊ1������Ԫ����ȫһ������Ҫ��flag��0
                        flag=0;
                    }else if(flag1==1||(S_num1==R_num1&&S_num2==R_num2)){
                        //do nothing
                    }else{
                        union_cnt++;
                        //Ҫд
                        itoa(R_num1,str_1,10);
                        itoa(R_num2,str_2,10);
                        write_num(str_1,str_2,cnt,blk_write);
                        cnt++;
                        if (cnt== 7) {
                            writeBlockToDisk(blk_write,write_start_blkno+q,&buf);
                            //printf("write_blkno:%d\n",join_write_start_blkno+q);
                            printf("ע�����д����̣�%d\n",write_start_blkno+q);
                            cnt= 0;
                            q++;
                            blk_write= getNewBlockInBuffer(&buf);
                            setBlockBlank(blk_write,write_start_blkno+q);
                        }
                    }
                }
                freeBlockInBuffer(blk_R, &buf);

                //��R.A����S.C������Ҫ���������ˣ���ѭ��
                if (R_num1>S_num1) {
                    break;
                }else{
                    //�����´�Ӧ����0��ʼ
                    R_index=0;
                }
            }

            //flagΪ1��ʾ��Ҫд��
            if(flag==1){
                union_cnt++;
                //Ҫд
                itoa(S_num1,str_1,10);
                itoa(S_num2,str_2,10);
                write_num(str_1,str_2,cnt,blk_write);
                cnt++;
                if (cnt== 7) {
                    writeBlockToDisk(blk_write,write_start_blkno+q,&buf);
                    //printf("write_blkno:%d\n",join_write_start_blkno+q);
                    printf("ע�����д����̣�%d\n",write_start_blkno+q);
                    cnt= 0;
                    q++;
                    blk_write= getNewBlockInBuffer(&buf);
                    setBlockBlank(blk_write,write_start_blkno+q);
                }

            }
        }

        freeBlockInBuffer(blk_S, &buf);

    }

    //�ǵ�д
    writeBlockToDisk(blk_write,write_start_blkno+q,&buf);
    //printf("write_blkno:%d\n",join_write_start_blkno+q);
    printf("ע�����д����̣�%d\n",write_start_blkno+q);

    freeBuffer(&buf);

    printf("S��R�Ĳ�����%d��Ԫ�顣\n", union_cnt);
    printf("\n");
}

//5. ʵ�ֻ��������ɢ�е�����ɨ���㷨��ʵ�ּ��ϲ����㷨����,��,����������ڴ����ϣ���ͳ�Ʋ���������������Ԫ�������
void answer_5_and_addition(){
    int S_start_blkno=300;
    int S_end_blkno=331;
    int R_start_blkno=200;
    int R_end_blkno=215;
    int operation_write_start_blkno;

    printf("----------------------\n");
    printf("��������ļ��ϵĽ��㷨\n");
    printf("----------------------\n");

    operation_write_start_blkno=500;
    set_intersection(S_start_blkno,S_end_blkno,R_start_blkno,R_end_blkno,operation_write_start_blkno);

    printf("----------------------\n");
    printf("��������ļ��ϵĲ��㷨\n");
    printf("----------------------\n");

    operation_write_start_blkno=550;
    set_difference(S_start_blkno,S_end_blkno,R_start_blkno,R_end_blkno,operation_write_start_blkno);

    printf("----------------------\n");
    printf("��������ļ��ϵĲ��㷨\n");
    printf("----------------------\n");

    operation_write_start_blkno=600;
    set_union(S_start_blkno,S_end_blkno,R_start_blkno,R_end_blkno,operation_write_start_blkno);

}

//������������������д�������������
int main(int argc, char **argv){

    answer_1();

    answer_2();

    answer_3();

    answer_4();

    answer_5_and_addition();

    return 0;

}














