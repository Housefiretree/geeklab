package cn.edu.hitsz.compiler.lexer;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * TODO: 实验一: 实现词法分析
 * <br>
 * 你可能需要参考的框架代码如下:
 *
 * @see Token 词法单元的实现
 * @see TokenKind 词法单元类型的实现
 */
public class LexicalAnalyzer {
    private final SymbolTable symbolTable;
    private String text;
    private List<Token> Tokens = new ArrayList<>();

    public LexicalAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }


    /**
     * 从给予的路径中读取并加载文件内容
     *
     * @param path 路径
     */


    public void loadFile(String path) {
        // TODO: 词法分析前的缓冲区实现
        // 可自由实现各类缓冲区
        // 或直接采用完整读入方法

        //这里直接读入
        text=FileUtils.readFile(path);
        //System.out.println(text);        //测试一下能否正常读入

        //throw new NotImplementedException();
    }

    /**
     * 执行词法分析, 准备好用于返回的 token 列表 <br>
     * 需要维护实验一所需的符号表条目, 而得在语法分析中才能确定的符号表条目的成员可以先设置为 null
     */
    public void run() {
        // TODO: 自动机实现的词法分析过程

        //System.out.println("run start\n");

        //获取读入文件的长度
        int text_len = text.length();

        //打印文件长度
        //System.out.println("text_len:"+text_len+"\n");

        //定义状态，初始状态为0
        int state=0;
        //定义读入的字符ch
        char ch;
        //用两个字符串id和num来分别保存读入的标识符或关键字和数字
        String id="";
        String num="";

        //token
        Token token;

        //定义pointer指向字符，for循环读字符
        for(int pointer=0;pointer<=text_len;pointer++){
            //打印一下当前状态和当前指向的字符
            //System.out.println("state:"+state)
            // if(pointer<text_len){
            //    System.out.println(text.charAt(pointer));
            //}

            //利用switch-case实现状态机
            switch (state){
                case 0:
                    //状态为0，表示准备读入字符

                    //读取当前字符存入ch（注意当pointer等于text_len时给ch赋值为空格符）
                    if(pointer<text_len){
                        ch=text.charAt(pointer);
                    }else{
                        ch=' ';
                    }

                    //当前字符为制表符，回车符，换行符或空格符
                    if(ch=='\t'||ch=='\r'||ch=='\n'||ch==' '){
                        //设置状态为0，跳过，去读下一个字符
                        state=0;
                        continue;
                    }

                    //当读到的字符为字母或下划线，则可能是标识符或关键字，将字符加入id串，设置状态为1，下次循环进入状态1
                    if(Character.isAlphabetic(ch)||ch=='_'){
                        id+=ch;
                        state=1;
                    }
                    //当读到的字符为非0的数字，则为IntConst，将字符加入num串，设置状态为2，下次循环进入状态2
                    else if(Character.isDigit(ch)&&ch!='0'){
                        num+=ch;
                        state=2;
                    }else{
                        //其他情况，根据读入的字符分别对应设置不同的状态，下次循环进行状态转移
                        switch (ch){
                            case '=':
                                state = 3;
                                break;
                            case ',':
                                state = 4;
                                break;
                            case ';':
                                //测试一下是否读到了分号
                                //System.out.println("read Semicolon");
                                state = 5;
                                break;
                            case '+':
                                state = 6;
                                break;
                            case '-':
                                state = 7;
                                break;
                            case '*':
                                state = 8;
                                break;
                            case '/':
                                state = 9;
                                break;
                            case '(':
                                state = 10;
                                break;
                            case ')':
                                state = 11;
                                break;
                            default:
                                state=0;
                                break;
                        }
                        //由于上面设置的状态在下次循环才进入，为了防止有符号被跳过，pointer要减一
                        pointer--;
                    }
                    break;
                case 1:
                    //状态为1，表示标识符或关键字

                    //读取当前符号
                    ch=text.charAt(pointer);
                    //如果ch是字母或数字或下划线，则将其识别为id串的一部分，加入id串，并且设置状态为1，下次循环进入状态1
                    if(Character.isAlphabetic(ch)||Character.isDigit(ch)||ch=='_'){
                        id+=ch;
                        state=1;
                    }else{
                        //如果读入的不是以上情况，则说明这个“单词”已经结束了，识别id串是关键字还是标识符，加入词法单元
                        switch (id){
                            case "return":
                                token=Token.simple("return");
                                Tokens.add(token);
                                //System.out.println(id);
                                break;
                            case "int":
                                token=Token.simple("int");
                                Tokens.add(token);
                                //System.out.println(id);
                                break;
                            default:
                                token=Token.normal("id",id);
                                Tokens.add(token);
                                //检测符号表中是否已含有该标识符, 若无向符号表加入该标识符即可
                                if (!symbolTable.has(id)) {
                                    symbolTable.add(id);
                                    // System.out.println("new id: "+id);
                                }
                                //System.out.println("id: "+id);
                                break;
                        }
                        //“单词”已经读完，重置id串和state
                        id="";
                        state=0;
                        //为防止有符号被跳过，pointer回退
                        pointer--;
                    }
                    break;
                case 2:
                    //状态为2，表示IntConst

                    //读入当前字符
                    ch=text.charAt(pointer);
                    //如果ch是数字，则将其识别为num串的一部分，加入num串，并且设置状态为2，下次循环进入状态2
                    if(Character.isDigit(ch)){
                        num+=ch;
                        state=2;
                    }else{
                        //如果读入的不是以上情况，则“单词”已结束，加入词法单元
                        token=Token.normal("IntConst",num);
                        Tokens.add(token);
                        //System.out.println(num);
                        //“单词”已经读完，重置num串和state
                        num="";
                        state=0;
                        //为防止有符号被跳过，pointer回退
                        pointer--;
                    }
                    break;
                //其他情况较为简单，直接根据相应的符号加入词法单元并且重置状态即可
                case 3:
                    token=Token.simple("=");
                    Tokens.add(token);
                    state=0;
                    break;
                case 4:
                    token=Token.simple(",");
                    Tokens.add(token);
                    state=0;
                    break;
                case 5:
                    //System.out.println("add ;");
                    token=Token.simple("Semicolon");
                    Tokens.add(token);
                    state=0;
                    break;
                case 6:
                    token=Token.simple("+");
                    Tokens.add(token);
                    state=0;
                    break;
                case 7:
                    token=Token.simple("-");
                    Tokens.add(token);
                    state=0;
                    break;
                case 8:
                    token=Token.simple("*");
                    Tokens.add(token);
                    state=0;
                    break;
                case 9:
                    token=Token.simple("/");
                    Tokens.add(token);
                    state=0;
                    break;
                case 10:
                    token=Token.simple("(");
                    Tokens.add(token);
                    state=0;
                    break;
                case 11:
                    token=Token.simple(")");
                    Tokens.add(token);
                    state=0;
                    break;
                default:
                    state=0;
                    break;
            }

        }

        //已经读完所有字符
        Tokens.add(Token.eof());
        //System.out.println("read over\n");
        //throw new NotImplementedException();
    }

    /**
     * 获得词法分析的结果, 保证在调用了 run 方法之后调用
     *
     * @return Token 列表
     */
    public Iterable<Token> getTokens() {
        // TODO: 从词法分析过程中获取 Token 列表
        // 词法分析过程可以使用 Stream 或 Iterator 实现按需分析
        // 亦可以直接分析完整个文件
        // 总之实现过程能转化为一列表即可

        //System.out.println("getTokens\n");
        return Tokens;

        //throw new NotImplementedException();
    }

    public void dumpTokens(String path) {
        //System.out.println("dump Tokens\n");
        FileUtils.writeLines(
                path,
                StreamSupport.stream(getTokens().spliterator(), false).map(Token::toString).toList()
        );
    }


}
