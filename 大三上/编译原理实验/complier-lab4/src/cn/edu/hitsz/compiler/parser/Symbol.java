package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.ir.IRValue;
import cn.edu.hitsz.compiler.ir.IRVariable;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.NonTerminal;
import cn.edu.hitsz.compiler.symtab.SourceCodeType;

/**
 * @author 86188
 */
public class Symbol {
    Token token;
    NonTerminal nonTerminal;

    //增加数据类型SourceCodeType作为语义分析栈的数据结构
    SourceCodeType type;
    //语义栈基础上，增加IRValue数据类型
    IRValue value;

    private Symbol(Token token, NonTerminal nonTerminal){
        this.token = token;
        this.nonTerminal = nonTerminal;
    }

    public Symbol(Token token){
        this(token, (NonTerminal) null);
    }

    public Symbol(NonTerminal nonTerminal){
        this(null, nonTerminal);
    }

    //增加相关构造方法
    public Symbol(Token token,NonTerminal nonTerminal,SourceCodeType type){
        this.token = token;
        this.nonTerminal = nonTerminal;
        this.type = type;
    }
    public Symbol(NonTerminal nonTerminal,SourceCodeType type){
        this(null,nonTerminal,type);
    }

    public Symbol(IRValue value){
        this.token = null;
        this.nonTerminal = null;
        this.type = null;
        this.value = value;
    }

    public Symbol(NonTerminal nonTerminal,IRValue value){
        this.token = null;
        this.nonTerminal = nonTerminal;
        this.type = null;
        this.value = value;
    }

    public Symbol(Token token, SourceCodeType type) {
        this.token = token;
        this.nonTerminal = null;
        this.type = type;
    }

    public boolean isToken(){
        return this.token != null;
    }

    public boolean isNonterminal(){
        return this.nonTerminal != null;
    }

    public Token Symbol_getToken(){ return this.token;}

    public NonTerminal Symbol_getNonTerminal(){ return this.nonTerminal;}

}
