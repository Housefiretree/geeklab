import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import java.security.MessageDigest;


/**
 * @author 86188
 */
public class Main {
    /** 这里存放各个“常数”大数 */
    public static BigInteger number_0 = new BigInteger("0");
    public static BigInteger number_1 = new BigInteger("1");
    public static BigInteger number_2 = new BigInteger("2");
    public static BigInteger number_10 = new BigInteger("10");
    public static BigInteger number_1000 = new BigInteger("1000");
    public static BigInteger number_99 = new BigInteger("99");



    /**随机生成一个大数*/
    public static BigInteger getBigNum(){
        //最小数限制:2的128次方
        BigInteger minLimit = new BigInteger("2");
        minLimit = minLimit.pow(128);
        //最大数限制:2的128次方的2倍
        BigInteger maxLimit = new BigInteger("2");
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
        a=a.mod(p);
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

    /**扩展欧几里得算法*/
    public static BigInteger x,y;
    //结果if x<0 注意x要加上φ(n)
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


    /**计算p的本原根*/
    public static BigInteger getPrimitiveRoot(BigInteger p,BigInteger q){
        BigInteger g = number_0;
        BigInteger minLimit = number_2;
        BigInteger maxLimit = p.subtract(number_2);
        maxLimit = maxLimit.multiply(minLimit);
        BigInteger bigInteger = maxLimit.subtract(minLimit);
        int len = maxLimit.bitLength();
        while(true){
            Random randNum = new Random();
            g = new BigInteger(len, randNum);
            if (g.compareTo(minLimit) < 0){
                g= g.add(minLimit);
            }
            if (g.compareTo(bigInteger) >= 0){
                g = g.mod(bigInteger).add(minLimit);
            }
            if(mod(g,number_2,p).equals(number_1)==false && mod(g,q,p).equals(number_1)==false){
                break;
            }
        }

        return g;
    }

    public static BigInteger getK(BigInteger p){
        BigInteger k = number_0;
        BigInteger minLimit = number_1;
        BigInteger maxLimit = p.subtract(number_1);
        maxLimit = maxLimit.multiply(minLimit);
        BigInteger bigInteger = maxLimit.subtract(minLimit);
        int len = maxLimit.bitLength();

        while(true){
            Random randNum = new Random();
            k = new BigInteger(len, randNum);
            if (k.compareTo(minLimit) < 0){
                k= k.add(minLimit);
            }
            if (k.compareTo(bigInteger) >= 0){
                k = k.mod(bigInteger).add(minLimit);
            }
            if(gcd(k,p.subtract(number_1)).equals(number_1)){
                break;
            }
        }

        return k;

    }

    public static boolean check(BigInteger y,BigInteger r,BigInteger s,
                                BigInteger g,BigInteger Hm,BigInteger p){
        BigInteger res1 = (mod(y,r,p).multiply(mod(r,s,p))).mod(p);
        BigInteger res2 = mod(g,Hm,p);
        if(res1.equals(res2)){
            return true;
        }else{
            return false;
        }
    }

    public static String getHash(String msg) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        //传入要加密的字符串
        messageDigest.update(msg.getBytes());
        //得到要传入的类型结果
        byte byteBuffer[] = messageDigest.digest();
        StringBuffer strString = new StringBuffer();
        //遍历bytebuffer
        for (int i = 0; i < byteBuffer.length; i++)
        {
            String hex = Integer.toHexString(0xff & byteBuffer[i]);
            if (hex.length() == 1)
            {
                strString.append('0');
            }
            strString.append(hex);
        }
        //得到返回结果
        String Hash = strString.toString();

        return Hash;
    }

    public static void run(BigInteger p, BigInteger g,BigInteger x,BigInteger y) throws NoSuchAlgorithmException {
        //随机生成满足条件的k
        BigInteger k = getK(p);
        System.out.println("本次随机选取的k值为:"+k);

        //消息为学号
        String msg = "210010101";
        System.out.println("消息:"+msg);
        //用SHA256计算消息的Hash值
        String Hash = getHash(msg);
        Hash = new BigInteger(Hash,16).toString(10);
        //计算签名(r,s)
        BigInteger r = mod(g,k,p);
        BigInteger k_ = extendedEuclid(k,p.subtract(number_1));
        if(k_.compareTo(number_0)<0){
            k_=k_.add(p.subtract(number_1));
        }
        //System.out.println("k^-1:"+k_);
        BigInteger Hm = new BigInteger(Hash);
        //System.out.println("Hash:"+Hm);
        BigInteger s = k_.multiply(Hm.subtract(x.multiply(r))).mod(p.subtract(number_1));
        System.out.println("签名结果(r,s):");
        System.out.println("r="+r);
        System.out.println("s="+s);

        System.out.println("进行验证");
        boolean valid = check(y,r,s,g,Hm,p);
        if(valid){
            System.out.println("验证通过");
        }else{
            System.out.println("验证失败");
        }

        //修改消息，重新验证
        msg = "abcdefghi";
        System.out.println("篡改消息为:"+msg);
        Hash = getHash(msg);
        Hash = new BigInteger(Hash,16).toString(10);
        Hm = new BigInteger(Hash);
        //System.out.println(Hm);
        System.out.println("进行验证");
        valid = check(y,r,s,g,Hm,p);
        if(valid){
            System.out.println("篡改后验证通过");
        }else{
            System.out.println("篡改后验证失败");
        }
    }



    /**Main函数，整个流程*/
    public static void main(String[] args) throws NoSuchAlgorithmException {
            //密钥生成
            //生成一个大素数p
            BigInteger q = number_0;
            BigInteger p = number_0;
            do{
                q = getBigNum();
                p = q.multiply(number_2).add(number_1);
            }while(millerRabin(p)==false);


            //求出p的本原根g
            BigInteger g = getPrimitiveRoot(p,q);

            System.out.println("p="+p);
            System.out.println("q="+q);
            System.out.println("g="+g);

            //随机选择整数x,1<x<p-1
            BigInteger minLimit = number_2;
            BigInteger maxLimit = p.subtract(number_2);
            maxLimit = maxLimit.multiply(minLimit);
            BigInteger bigInteger = maxLimit.subtract(minLimit);
            Random randNum = new Random();
            int len = maxLimit.bitLength();
            BigInteger x = new BigInteger(len, randNum);
            if (x.compareTo(minLimit) < 0){
                x= x.add(minLimit);
            }
            if (x.compareTo(bigInteger) >= 0){
                x = x.mod(bigInteger).add(minLimit);
            }

            //计算y
            BigInteger y = mod(g,x,p);



            for(int i=0;i<2;i++){
                System.out.println("第"+(i+1)+"组");

                System.out.println("公钥(y,p,g):");
                System.out.println("y="+y);
                System.out.println("p="+p);
                System.out.println("g="+g);
                System.out.println("私钥x="+x);

                run(p,g,x,y);
            }

    }

}