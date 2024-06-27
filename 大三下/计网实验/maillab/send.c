#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netdb.h>
#include <unistd.h>
#include <getopt.h>
#include "base64_utils.h"

#define MAX_SIZE 4095

char buf[MAX_SIZE+1];


// 将打印响应的动作写成一个函数，方便多次调用。做法参考Print welcome message。
void print_response(int s_fd)
{
    int r_size = 0;
    if ((r_size = recv(s_fd, buf, MAX_SIZE, 0)) == -1){
        perror("response");
        exit(EXIT_FAILURE);
    }
    buf[r_size] = '\0';
    printf("%s", buf);
}


// receiver: mail address of the recipient
// subject: mail subject
// msg: content of mail body or path to the file containing mail body
// att_path: path to the attachment
void send_mail(const char* receiver, const char* subject, const char* msg, const char* att_path)
{
    const char* end_msg = "\r\n.\r\n";
    const char* host_name = "smtp.163.com";     // TODO: Specify the mail server domain name
    const unsigned short port = 25;             // SMTP server port
    const char* user = "*************"; 	 // TODO: Specify the user
    const char* pass = "****************";      // TODO: Specify the password
    const char* from = "*************@163.com"; // TODO: Specify the mail address of the sender
    char dest_ip[16]; // Mail server IP address
    int s_fd; // socket file descriptor
    struct hostent *host;
    struct in_addr **addr_list;
    int i = 0;
    int r_size;

    // Get IP from domain name
    if ((host = gethostbyname(host_name)) == NULL)
    {
        herror("gethostbyname");
        exit(EXIT_FAILURE);
    }

    addr_list = (struct in_addr **) host->h_addr_list;
    while (addr_list[i] != NULL)
        ++i;
    strcpy(dest_ip, inet_ntoa(*addr_list[i-1]));
    

    // TODO: Create a socket, return the file descriptor to s_fd
    // 参数参考socket编程简介socket函数部分
    if((s_fd = socket(AF_INET,SOCK_STREAM,0)) == -1)
    {
        herror("socket");
        exit(EXIT_FAILURE);
    }
    // printf("socket success\n");

    // and establish a TCP connection to the mail server
    // servaddr参数,由sockaddr_in结构体表示
    struct sockaddr_in servaddr;
    // 地址族,IP协议必须为AF_INET
    servaddr.sin_family = AF_INET;
    // 端口号,注意大小端转换
    servaddr.sin_port = htons(port);
    // IP地址,需要使用inet_addr函数转换成32位二进制网络字节序的IPv4地址
    servaddr.sin_addr.s_addr = inet_addr(dest_ip);
    // 使用bzero函数填充0
    bzero(&(servaddr.sin_zero),sizeof(servaddr.sin_zero));
    // 发送连接请求,注意强制转换
    if((connect(s_fd, (struct sockaddr *)&servaddr, sizeof(servaddr))) == -1) {
        herror("connect");
        exit(EXIT_FAILURE);
    }
    // printf("connect success\n");

    // Print welcome message
    if ((r_size = recv(s_fd, buf, MAX_SIZE, 0)) == -1)
    {
        perror("recv");
        exit(EXIT_FAILURE);
    }
    buf[r_size] = '\0'; // Do not forget the null terminator
    printf("%s", buf);




    // Send EHLO command and print server response
    const char* EHLO = "EHLO 163.com\r\n"; // TODO: Enter EHLO command here
    strcpy(buf, EHLO);
    // 打印信息
    printf("%s",EHLO);
    // 发送
    send(s_fd, EHLO, strlen(EHLO), 0);
    // TODO: Print server response to EHLO command
    // 做法参考前面的Print welcome message
    print_response(s_fd);


    // TODO: Authentication. Server response should be printed out.
    // 1.AUTH login命令,做法类似于EHLO
    const char* AUTH_login = "AUTH login\r\n"; // TODO: Enter EHLO command here
    strcpy(buf, AUTH_login);
    // 打印信息
    printf("%s",AUTH_login);
    // 发送
    send(s_fd, AUTH_login, strlen(AUTH_login), 0);
    // TODO: Print server response to AUTH_login command
    print_response(s_fd);

    // 2.输入邮箱名,需要base64编码,并且需要打印服务器响应
    strcpy(buf,encode_str(user));
    // 打印信息
    printf("%s",buf);
    // 发送
    send(s_fd, buf, strlen(buf), 0);
    print_response(s_fd);

    // 3.输入授权码,需要base64编码,并且需要打印服务器响应
    strcpy(buf,encode_str(pass));
    // 打印信息
    printf("%s",buf);
    // 发送
    send(s_fd, buf, strlen(buf), 0);
    print_response(s_fd);



    // TODO: Send MAIL FROM command and print server response
    // 通过strcat追加字符串来完成相应格式
    strcpy(buf,"Mail FROM:<");
    strcat(buf,from);
    strcat(buf,">\r\n");
    // 打印信息
    printf("%s",buf);
    // 发送
    send(s_fd,buf,strlen(buf),0);
    print_response(s_fd);


    // TODO: Send RCPT TO command and print server response
    // 做法与MAIL FROM类似
    strcpy(buf,"Rcpt TO:<");
    strcat(buf,receiver);
    strcat(buf,">\r\n");
    // 打印信息
    printf("%s",buf);
    // 发送
    send(s_fd,buf,strlen(buf),0);
    print_response(s_fd);  


    // TODO: Send DATA command and print server response
    strcpy(buf,"data\r\n");
    // 打印信息
    printf("%s",buf);
    // 发送
    send(s_fd,buf,strlen(buf),0);
    print_response(s_fd); 


    // TODO: Send message data
    // 填写邮件头
    // From:写信人邮件地址
    strcpy(buf,"From:");
    strcat(buf,from); 
    strcat(buf,"\r\n"); 
    // To:收信人邮件地址
    strcat(buf,"To:");
    strcat(buf,receiver);
    strcat(buf,"\r\n");
    // MIME版本
    strcat(buf,"MIME-Version: 1.0"); 
    strcat(buf,"\r\n"); 
    // 内容的类型和格式
    strcat(buf,"Content-Type: multipart/mixed; ");
    // 分割边界
    strcat(buf,"boundary=qwertyuiopasdfghjklzxcvbnm");
    strcat(buf,"\r\n");
    // Subject:邮件主题
    strcat(buf,"Subject:");
    strcat(buf,subject);   
    strcat(buf,"\r\n");
    // 打印信息
    printf("%s",buf);
    // 发送   
    send(s_fd,buf,strlen(buf),0);

    // 如果消息部分不为空
    if(msg != NULL) {
        // 分割边界
        strcpy(buf,"\r\n");
        strcat(buf,"--qwertyuiopasdfghjklzxcvbnm");
        strcat(buf,"\r\n");
        // 内容的类型和格式
        strcat(buf,"Content-Type: text/plain");
        strcat(buf,"\r\n\r\n");
        // 打印信息
	    printf("%s",buf);
        // 发送   
	    send(s_fd,buf,strlen(buf),0);
	
        // 消息内容
        FILE*message = fopen(msg,"r");
        if(message != NULL) {
            // 若message不为空则说明msg是文件路径,需要从中读取消息内容
            int size = fread(buf,1,MAX_SIZE,message);
            buf[size] = '\0';
            strcat(buf,"\r\n");
            // 打印信息
            printf("%s",buf);
            // 发送
            send(s_fd,buf,strlen(buf),0);
            fclose(message);
        } else {
            // 若message为空则说明msg是消息本身
            strcpy(buf,msg);
            strcat(buf,"\r\n");
            // 打印信息
            printf("%s",buf);
            // 发送
            send(s_fd,buf,strlen(buf),0);
        }
    }

    // 如果附件路径不为空,则说明需要发送附件
    if(att_path != NULL) {
        // 分割边界
        strcpy(buf, "\r\n");
        strcat(buf, "--qwertyuiopasdfghjklzxcvbnm");
        strcat(buf, "\r\n");
        // 邮件附件可以用application/octet-stream子类型表示
        strcat(buf, "Content-Type: application/octet-stream");
        strcat(buf, "\r\n");
        // 附件名
        strcat(buf, "Content-Disposition: attachment; filename=");
        strcat(buf, att_path);
        strcat(buf, "\r\n");
        // 编码:base64
        strcat(buf, "Content-Transfer-Encoding: base64");
        strcat(buf, "\r\n\r\n");
        // 打印信息
        printf("%s",buf);
        // 发送
        send(s_fd, buf, strlen(buf), 0);

        // 对原始附件att_origin进行base64编码,保存在att_base64中
        FILE *att_origin = fopen(att_path,"rb");
        FILE *att_base64 = tmpfile();
        encode_file(att_origin,att_base64);
        fclose(att_origin);
        // 根据att_base64的大小来分配内存
        fseek(att_base64,0,SEEK_END);
        int att_size = ftell(att_base64);
        char*att_content = (char*)malloc((att_size+1)*sizeof(char));
        // 重置指针
        rewind(att_base64);
        // 读取附件内容并保存
        fread(att_content,1,att_size,att_base64);
        att_content[att_size] = '\0';
        fclose(att_base64);
        // 发送
        send(s_fd,att_content,strlen(att_content),0);
    }


    // TODO: Message ends with a single period
    strcpy(buf, end_msg);
    // 打印信息
    printf("%s",buf);
    // 发送
    send(s_fd,buf,strlen(buf),0);
    print_response(s_fd);


    // TODO: Send QUIT command and print server response
    strcpy(buf,"quit\r\n");
    // 打印信息
    printf("%s",buf);
    // 发送
    send(s_fd,buf,strlen(buf),0);
    print_response(s_fd); 


    close(s_fd);
}

int main(int argc, char* argv[])
{
    int opt;
    char* s_arg = NULL;
    char* m_arg = NULL;
    char* a_arg = NULL;
    char* recipient = NULL;
    const char* optstring = ":s:m:a:";
    while ((opt = getopt(argc, argv, optstring)) != -1)
    {
        switch (opt)
        {
        case 's':
            s_arg = optarg;
            break;
        case 'm':
            m_arg = optarg;
            break;
        case 'a':
            a_arg = optarg;
            break;
        case ':':
            fprintf(stderr, "Option %c needs an argument.\n", optopt);
            exit(EXIT_FAILURE);
        case '?':
            fprintf(stderr, "Unknown option: %c.\n", optopt);
            exit(EXIT_FAILURE);
        default:
            fprintf(stderr, "Unknown error.\n");
            exit(EXIT_FAILURE);
        }
    }

    if (optind == argc)
    {
        fprintf(stderr, "Recipient not specified.\n");
        exit(EXIT_FAILURE);
    }
    else if (optind < argc - 1)
    {
        fprintf(stderr, "Too many arguments.\n");
        exit(EXIT_FAILURE);
    }
    else
    {
        recipient = argv[optind];
        send_mail(recipient, s_arg, m_arg, a_arg);
        exit(0);
    }
}
