package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.lexer.TokenKind;
import cn.edu.hitsz.compiler.parser.table.NonTerminal;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.parser.table.Term;
import cn.edu.hitsz.compiler.symtab.SourceCodeType;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.symtab.SymbolTableEntry;

import java.util.List;
import java.util.Stack;

// TODO: 实验三: 实现语义分析

//要不用新的数据结构算了？？？？
public class SemanticAnalyzer implements ActionObserver {
    SymbolTable symbolTable;
    Stack<Symbol> symbolStack = new Stack<>();


    @Override
    public void whenAccept(Status currentStatus) {
        // TODO: 该过程在遇到 Accept 时要采取的代码动作
        //throw new NotImplementedException();
    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        // TODO: 该过程在遇到 reduce production 时要采取的代码动作
        //throw new NotImplementedException();
        //语义分析，更新符号表
        //
        switch (production.index()){
            case 4 ->{  //S -> D id;
                //当S->D id这条产生式归约时，取出D的type，这个type就是id的type，更新符号表中相应变量的type信息，
                //压入空记录占位(D id被归约为S，S不需要携带信息)；

                //lookup 获得id的名字，用于在符号表中查找
                String id_name = symbolStack.peek().Symbol_getToken().getText();
                //弹栈
                symbolStack.pop();
                //现在栈顶为D
                Symbol D = symbolStack.peek();
                //更新符号表，把id的类型更新为D的类型
                symbolTable.get(id_name).setType(D.type);
                //再把D弹栈
                symbolStack.pop();
                //压入空记录占位
                Symbol S = new Symbol(production.head());
                symbolStack.push(S);

                break;
            }
            case 5 ->{  //D -> int;
                //当D->int这条产生式归约时，int这个token应该在语义分析栈中，把这个token的type类型赋值给D的type;
                NonTerminal nonTerminal = production.head();
                //D.type = Int
                Symbol D = new Symbol(nonTerminal,SourceCodeType.Int);
                //弹出右部，压入左部
                symbolStack.pop();
                symbolStack.push(D);

                break;
            }
            default -> {    //其他情况压入空记录占位即可
                //先把产生式右部弹栈
                for(int i=0;i<production.body().size();i++){
                    symbolStack.pop();
                }
                //再把产生式左部压栈
                Symbol S = new Symbol(production.head());
                symbolStack.push(S);

                break;
            }
        }



    }

    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        // TODO: 该过程在遇到 shift 时要采取的代码动作
        //throw new NotImplementedException();
        //Shift时将符号的type属性（从Token中获得）入栈
        String currentName = currentToken.getText();
        Symbol currentSymbol;
        //currentSymbol = new Symbol(currentToken);
        if(currentName.length()==0){
            currentSymbol = new Symbol(currentToken);
        }else if(currentToken.getKind().getCode()==52){
            //System.out.println("text:"+currentName);
            currentSymbol = new Symbol(currentToken,SourceCodeType.Int);
        }else{
            //System.out.println("text:"+currentName+",   type:"+symbolTable.get(currentName).getType());
            currentSymbol = new Symbol(currentToken,symbolTable.get(currentName).getType());
        }
        /**if(currentToken.getKind().getCode()==52){
            currentSymbol = new Symbol(currentToken,SourceCodeType.Int);
        }else{
            currentSymbol = new Symbol(currentToken);
        }*/
        symbolStack.push(currentSymbol);
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        // TODO: 设计你可能需要的符号表存储结构
        // 如果需要使用符号表的话, 可以将它或者它的一部分信息存起来, 比如使用一个成员变量存储
        //throw new NotImplementedException();
        symbolTable = table;
    }
}

