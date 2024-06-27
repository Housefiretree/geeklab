#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netdb.h>
#include <unistd.h>

#define MAX_SIZE 65535

char buf[MAX_SIZE+1];


// 将打印响应的动作写成一个函数，方便多次调用。做法参考Print welcome message。（和send处一样）
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


void recv_mail()
{
    const char* host_name = "pop.163.com";      // TODO: Specify the mail server domain name
    const unsigned short port = 110;            // POP3 server port
    const char* user = "***********@163.com";   // TODO: Specify the user
    const char* pass = "****************";     // TODO: Specify the password
    char dest_ip[16];
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

    // 这里的TO DO做法和send处的完全一致
    // TODO: Create a socket,return the file descriptor to s_fd
    // 参数参考socket编程简介socket函数部分
    if((s_fd = socket(AF_INET,SOCK_STREAM,0)) == -1)
    {
        herror("socket");
        exit(EXIT_FAILURE);
    }

    // and establish a TCP connection to the POP3 server
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
    if(connect(s_fd, (struct sockaddr *)&servaddr, sizeof(servaddr)) == -1) {
        herror("connect");
        exit(EXIT_FAILURE);
    }

    // Print welcome message
    if ((r_size = recv(s_fd, buf, MAX_SIZE, 0)) == -1)
    {
        perror("recv");
        exit(EXIT_FAILURE);
    }
    buf[r_size] = '\0'; // Do not forget the null terminator
    printf("%s", buf);


    // TODO: Send user and password and print server response
    // 通过strcat追加字符串来完成相应格式。注意此处邮箱和授权码都不需要加密。
    // 1.邮箱
    strcpy(buf,"user ");
    strcat(buf,user);
    strcat(buf,"\r\n");
    // 打印信息
    printf("%s",buf);
    send(s_fd,buf,strlen(buf),0);
    print_response(s_fd);

    //2.授权码
    strcpy(buf,"pass ");
    strcat(buf,pass);
    strcat(buf,"\r\n");
    // 打印信息
    printf("%s",buf);
    send(s_fd,buf,strlen(buf),0);
    print_response(s_fd);


    // TODO: Send STAT command and print server response
    strcpy(buf,"stat\r\n");
    // 打印信息
    printf("%s",buf);
    send(s_fd,buf,strlen(buf),0);
    print_response(s_fd);


    // TODO: Send LIST command and print server response
    strcpy(buf,"list\r\n");
    // 打印信息
    printf("%s",buf);
    send(s_fd,buf,strlen(buf),0);
    print_response(s_fd);


    // TODO: Retrieve the first mail and print its content
    strcpy(buf,"retr 1\r\n");
    // 打印信息
    printf("%s",buf);
    send(s_fd,buf,strlen(buf),0);
    print_response(s_fd);


    // TODO: Send QUIT command and print server response
    strcpy(buf,"quit\r\n");
    send(s_fd,buf,strlen(buf),0);
    // 打印信息
    printf("%s",buf);
    print_response(s_fd);


    close(s_fd);
}

int main(int argc, char* argv[])
{
    recv_mail();
    exit(0);
}
