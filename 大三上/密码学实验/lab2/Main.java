import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;


/**
 * @author 86188
 */
public class Main {
    /**RSA密钥生成:
     * 1.随机生成大数
     * 2.快速模幂运算mod
     * 3.Miller_Rabin算法
     * 4.gcd
     * 5.选择e
     * 6.扩展欧几里得算法
     * */

    /** 这里存放各个“常数”大数 */
    public static BigInteger number_0 = new BigInteger("0");
    public static BigInteger number_1 = new BigInteger("1");
    public static BigInteger number_2 = new BigInteger("2");
    public static BigInteger number_10 = new BigInteger("10");
    public static BigInteger number_1000 = new BigInteger("1000");
    public static BigInteger number_99 = new BigInteger("99");



    /**随机生成一个大数*/
    public static BigInteger getBigNum(){
        //最小数限制:2的129次方
        BigInteger minLimit = new BigInteger("2");
        minLimit = minLimit.pow(129);
        //最大数限制:2的129次方的256倍
        BigInteger maxLimit = new BigInteger("256");
        maxLimit = maxLimit.multiply(minLimit);
        BigInteger bigInteger = maxLimit.subtract(minLimit);
        Random randNum = new Random();
        int len = maxLimit.bitLength();
        BigInteger res = new BigInteger(len, randNum);
        if (res.compareTo(minLimit) < 0){
            res = res.add(minLimit);
        }
        if (res.compareTo(bigInteger) >= 0){
            res = res.mod(bigInteger).add(minLimit);
        }
        //System.out.println("The random BigInteger = "+res);

        return res;
    }

    /**快速模幂运算：计算a^b mod p的结果*/
    public static BigInteger mod(BigInteger num1, BigInteger num2, BigInteger num3){
        BigInteger a=num1;
        BigInteger b=num2;
        BigInteger p=num3;
        BigInteger res = new BigInteger("1");
        while(true){
            if(b.equals(number_0)){
                return res;
            }
            while((b.compareTo(number_0)>0)&&(b.mod(number_2)).equals(number_0)){
                a=(a.multiply(a)).mod(p);
                b=b.divide(number_2);
            }
            b=b.subtract(number_1);
            res=(a.multiply(res)).mod(p);
        }
    }

    /**Miller_Rabin算法判断n是否是素数*/
    public static boolean millerRabin(BigInteger n){
        //找出整数k,q,其中k>0,q是奇数，使得n-1==2^k*q
        BigInteger k=new BigInteger("0");
        BigInteger q=n.subtract(number_1);
        while((q.mod(number_2)).equals(number_0)){
            k=k.add(number_1);
            q=q.divide(number_2);
        }
        //System.out.println("k:"+k+"  "+"q:"+q);

        //随机选取整数a,1<a<n-1  所以a∈[2,n-2]
        BigInteger minLimit = number_2;
        BigInteger maxLimit = n.subtract(number_2);
        maxLimit = maxLimit.multiply(minLimit);
        BigInteger bigInteger = maxLimit.subtract(minLimit);
        Random randNum = new Random();
        int len = maxLimit.bitLength();
        BigInteger a = new BigInteger(len, randNum);
        if (a.compareTo(minLimit) < 0){
            a= a.add(minLimit);
        }
        if (a.compareTo(bigInteger) >= 0){
            a = a.mod(bigInteger).add(minLimit);
        }
        //System.out.println("a:"+a);

        //if a^q mod n ==1或者n-1，返回“很可能为素数”，这里定为true
        if(mod(a,q,n).equals(number_1)||mod(a,q,n).equals(n.subtract(number_1))){
            //System.out.println("它很可能是素数");
            return true;
        }


        //for循环
        for(BigInteger j=number_0;j.compareTo(k)<0;j=j.add(number_1)){
            int l = j.intValue();
            //System.out.println("j:"+j);
            if(mod(a,(number_2.pow(l)).multiply(q),n).equals(n.subtract(number_1))){
                //System.out.println("它很可能是素数");
                return true;
            }
        }

        //合数，定为false
        return false;
    }

    /**gcd，求两个数的最大公因子*/
    public static BigInteger gcd(BigInteger a, BigInteger b){
        if (a.compareTo(b)<0) {
            //当a<b时，交换一下
            BigInteger k = a;
            a = b;
            b = k;
        }
        if (a.mod(b).equals(number_0)==false) {
            return gcd(b, a.mod(b));
        } else {
            return b;
        }
    }

    /**根据φ(n)选择e,要求e是随机的素数，满足1<e<φ(n)，且e和φ(n)互质*/
    public static BigInteger getE(BigInteger Euler_n){
        //首先在(1,φ(n))范围内生成e
        BigInteger minLimit = number_2;
        BigInteger maxLimit = Euler_n.subtract(number_1);
        maxLimit = maxLimit.multiply(minLimit);
        BigInteger bigInteger = maxLimit.subtract(minLimit);
        Random randNum = new Random();
        int len = maxLimit.bitLength();
        BigInteger e = new BigInteger(len, randNum);
        if (e.compareTo(minLimit) < 0){
            e= e.add(minLimit);
        }
        if (e.compareTo(bigInteger) >= 0){
            e = e.mod(bigInteger).add(minLimit);
        }

        //判断e是否为素数，是否与φ(n)互质，若不是，则重新生成
        while((millerRabin(e)==false)&&(gcd(e,Euler_n).equals(number_1)==false)){
            Random rand = new Random();
            e = new BigInteger(len, randNum);
            if (e.compareTo(minLimit) < 0){
                e= e.add(minLimit);
            }
            if (e.compareTo(bigInteger) >= 0){
                e = e.mod(bigInteger).add(minLimit);
            }
        }
        //返回e
        return e;
    }

    /**扩展欧几里得算法*/
    public static BigInteger x,y;
    public static BigInteger extendedEuclid(BigInteger a, BigInteger b){
        if(b.equals(number_0)){
            x=number_1;
            y=number_0;
        }else{
            //递归
            BigInteger d=extendedEuclid(b,a.mod(b));
            BigInteger t=x;
            x=y;
            y=t.subtract(a.divide(b).multiply(y));
        }
        return x;
    }

    public static int flag=0;
    /**对字符串s进行编码:直接将字符的ASCii码两个字符组成一个四位十进制数处理*/
    public static ArrayList<BigInteger> encode(String s){
        ArrayList<BigInteger> list = new ArrayList<BigInteger>();
        char ch;
        int ascii;
        BigInteger res;
        for(int i=0;i<s.length();i++){
            ch=s.charAt(i);
            ascii= (int) ch;
            //res=ascii*10+ascii%10;
            res=new BigInteger(String.valueOf(ascii*10+ascii%10));
            //如果如果最后一个分组不足4位，比如1个字母，明文分组可以自己设定一个2位数的值(>61)进行填充，解密时删除
            if(i==s.length()-1&&res.compareTo(number_1000)<0){
                //这里设置为99
                res=res.add(number_99);
                flag=1;
            }
            list.add(res);
        }
        return list;
    }


    /**加密算法:根据明文m和公钥(e,n)进行加密,输出密文。C=m^e mod n*/
    public static ArrayList<BigInteger> encrypt(ArrayList<BigInteger> plain_list,BigInteger e,BigInteger n){
        ArrayList<BigInteger>cipher_list=new ArrayList<BigInteger>();
        BigInteger res;
        for(int i=0;i<plain_list.size();i++){
            res=mod(plain_list.get(i),e,n);
            cipher_list.add(res);
        }
        return cipher_list;
    }

    /**将list转化为字符串*/
    public static String listToString(ArrayList<BigInteger> list, int group_length){
        String string="";
        String str="";
        String str_final;
        int str_length=0;
        int rest_length=0;
        for(int i=0;i<list.size();i++){
            //将数字转换成字符串
            str=list.get(i).toString();
            //字符串长度
            str_length=str.length();
            //重置str_final
            str_final="";
            //计算剩余长度，需要在前面补0
            rest_length=group_length-str_length;
            for(int j=0;j<rest_length;j++){
                str_final=str_final+"0";
            }
            //补完0之后加上原来的字符串即为最终字符串
            str_final=str_final+str;
            //将其加到整个的字符串里
            string=string+str_final;
        }
        return string;
    }

    /**将字符串转换成list*/
    public static ArrayList<BigInteger> stringToList(String s, int group_length){
        ArrayList<BigInteger> list = new ArrayList<BigInteger>();
        //计算完整分组的个数
        int groups=s.length()/group_length;
        //对于完整分组的部分：
        for(int i=0;i<groups;i++){
            String str="";
            for(int j=0;j<group_length;j++){
                str=str+s.charAt(i*group_length+j);
            }
            BigInteger num= new BigInteger(str);
            list.add(num);
        }
        //最后一小点同样需要转换
        if(s.length()%group_length!=0){
            String str="";
            for(int i=groups*group_length;i<s.length();i++){
                str=str+s.charAt(i);
            }
            BigInteger num= new BigInteger(str);
            list.add(num);
        }
        return list;
    }


    /**解密算法:根据密文c和私钥(d,n)进行解密,输出明文。m=c^d mod n*/
    public static ArrayList<BigInteger> decrypt(ArrayList<BigInteger> cipher_list,BigInteger d,BigInteger n){
        ArrayList<BigInteger>plain_list= new ArrayList<BigInteger>();
        BigInteger res;
        for(int i=0;i<cipher_list.size();i++){
            res=mod(cipher_list.get(i),d,n);
            plain_list.add(res);
        }
        return plain_list;
    }

    /**对数字列表解码*/
    public static String decode(ArrayList<BigInteger>plain_list){
        String plain="";
        for(int i=0;i<plain_list.size();i++){
            if(i==plain_list.size()-1&&flag==1){
                //解密时删掉之前填充的值
                //plain=plain+(char)((plain_list.get(i)-99)/10);
                plain = plain+(char)((plain_list.get(i).subtract(number_99)).divide(number_10)).intValue();
                return plain;
            }
            //plain=plain+(char)(plain_list.get(i)/10);
            //注意这里变字符的方式
            plain = plain+(char)(plain_list.get(i).divide(number_10)).intValue();
        }
        return plain;
    }


    /**Main函数，整个加密解密流程*/
    public static void main(String[] args) throws IOException {
        //RSA密钥生成
        //(1)生成两个大数p和q，当其不是素数时重新生成
        BigInteger p,q;
        do{
            p=getBigNum();
        }while(!millerRabin(p));
        do{
            q=getBigNum();
        }while(!millerRabin(q));
        System.out.println("生成p:"+p);
        System.out.println("生成q:"+q);
        //(2)计算两个素数的乘积n
        BigInteger n=p.multiply(q);
        System.out.println("计算n=p*q:"+n);
        //(3)计算欧拉函数φ(n)
        BigInteger Euler_n=(p.subtract(BigInteger.valueOf(1))).multiply(q.subtract(BigInteger.valueOf(1)));
        System.out.println("计算φ(n)=(p-1)*(q-1):"+Euler_n);
        //(4)选择符合条件的e
        BigInteger e=getE(Euler_n);
        System.out.println("选择e:"+e);
        //(5)求出d
        BigInteger d=extendedEuclid(e,Euler_n);
        if(d.compareTo(number_0)<0){d=d.add(Euler_n);}
        System.out.println("求出d:"+d);


        //加密
        //(1)从lab2-Plaintext中获取待加密的明文
        System.out.println("开始加密");
        Date encrypt_begin = new Date();
        String file_path="src/lab2-Plaintext.txt";
        Path filePath = Path.of(file_path);
        String plaintext = Files.readString(filePath);
        //(2)对明文进行编码
        ArrayList<BigInteger>plain_encoded_list=encode(plaintext);
        //(3)加密
        System.out.println("根据明文和公钥(e,n)进行加密");
        ArrayList<BigInteger>cipher_list=encrypt(plain_encoded_list,e,n);
        //(4)注意密文分组需要处理长度，设置密文分组长度为最大可能的长度
        int cipher_group_length=(int)(Math.log10(n.doubleValue())/ Math.log10(2))+1;
        //将加密得到的数字列表转化为一个字符串
        String cipher_string = listToString(cipher_list,cipher_group_length);
        //将这个字符串转换成数字列表
        ArrayList<BigInteger>cipher_string_List = stringToList(cipher_string,1);
        //(5)将数字列表转换成字符串，得到密文
        String cipher="";
        char ch;
        int offset=70;
        for(int i=0;i<cipher_string_List.size();i++){
            //将数字转换成ascii
            ch=(char)(cipher_string_List.get(i).add(BigInteger.valueOf(offset))).intValue();
            //添加到密文字符串里
            cipher=cipher+ch;
        }
        //(6)将加密后的密文输出到屏幕上
        Date encrypt_end = new Date();
        System.out.println("加密结束，加密用时："+(encrypt_end.getTime()-encrypt_begin.getTime())+"ms");
        System.out.println("得到密文：");
        System.out.println(cipher);


        //解密
        //(1)开始解密
        System.out.println("开始解密");
        Date decrypt_begin = new Date();
        //将密文(字符串)转换成数字列表
        ArrayList<BigInteger>cipher_List_from=new ArrayList<BigInteger>();
        BigInteger number;
        for(int i=0;i<cipher.length();i++){
            //将ascii转换成数字
            number=new BigInteger(String.valueOf((cipher.charAt(i))-offset));
            cipher_List_from.add(number);
        }
        //将数字列表转换成字符串
        String get_cipher_str=listToString(cipher_List_from,1);
        //将字符串转换成数字列表
        ArrayList<BigInteger> get_cipher_list = stringToList(get_cipher_str,cipher_group_length);
        //(2)解密
        System.out.println("根据密文和私钥(d,n)进行解密");
        ArrayList<BigInteger>decrept_plain_list=decrypt(get_cipher_list,d,n);
        //对解密得到的明文数字列表进行解码，得到明文字符串
        String decrept_plain_text=decode(decrept_plain_list);
        //(3)将解密后的明文输出到屏幕上
        Date decrypt_end = new Date();
        System.out.println("解密结束，解密用时："+(decrypt_end.getTime()-decrypt_begin.getTime())+"ms");
        System.out.println("得到明文:");
        System.out.println(decrept_plain_text);
    }
}