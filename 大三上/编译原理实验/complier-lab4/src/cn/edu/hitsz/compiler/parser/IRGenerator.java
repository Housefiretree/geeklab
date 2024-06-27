package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.ir.IRImmediate;
import cn.edu.hitsz.compiler.ir.IRValue;
import cn.edu.hitsz.compiler.ir.IRVariable;
import cn.edu.hitsz.compiler.ir.Instruction;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.lexer.TokenKind;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// TODO: 实验三: 实现 IR 生成

/**
 *
 */
public class IRGenerator implements ActionObserver {
    List<Instruction> instructions = new ArrayList<>();
    Stack<Symbol> symbolStack = new Stack<>();

    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        // TODO
        //throw new NotImplementedException();
        //首先要判断token是立即数还是变量
        TokenKind currentKind = currentToken.getKind();
        int currentCode = currentKind.getCode();
        IRValue value;

        //由coding_map,IntConst的code为52
        if(currentCode==52){
            //获取立即数的值作为value
            int val = Integer.parseInt(currentToken.getText());
            value = IRImmediate.of(val);
        }else{
            //获取变量的名字作为value
            String val = currentToken.getText();
            value = IRVariable.named(val);
        }
        //入栈
        Symbol symbol = new Symbol(value);
        symbolStack.push(symbol);

    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        // TODO
        //throw new NotImplementedException();
        //根据产生式索引对应不同动作
        switch (production.index()){
            case 1 ->{  //P -> S_list;
                //符号栈变化
                //右部弹栈
                Symbol S_list = symbolStack.peek();
                symbolStack.pop();

                //将S_list的value给P
                Symbol P = new Symbol(production.head(),S_list.value);
                //左部压栈
                symbolStack.push(P);

                break;
            }
            case 2 ->{  //S_list -> S Semicolon S_list;
                //符号栈变化
                //右部弹栈
                for(int i=0;i<production.body().size();i++){
                    symbolStack.pop();
                }
                Symbol S_list = new Symbol(production.head(),IRVariable.named("S_list1"));
                //左部压栈
                symbolStack.push(S_list);

                break;
            }
            case 3 ->{  //S_list -> S Semicolon;
                //右部弹栈
                for(int i=0;i<production.body().size();i++){
                    symbolStack.pop();
                }

                Symbol S_list = new Symbol(production.head(),IRVariable.named("S_list2"));
                //左部压栈
                symbolStack.push(S_list);

                break;
            }
            case 4 ->{  //S -> D id;
                //右部弹栈
                for(int i=0;i<production.body().size();i++){
                    symbolStack.pop();
                }

                Symbol S = new Symbol(production.head(),IRVariable.named("S1"));
                //左部压栈
                symbolStack.push(S);

                break;

            }
            case 5 ->{  //D -> int;
                //符号栈变化
                //右部弹栈
                for(int i=0;i<production.body().size();i++){
                    symbolStack.pop();
                }

                Symbol D = new Symbol(production.head(),IRVariable.named("D"));
                //左部压栈
                symbolStack.push(D);

                break;
            }
            case 6 ->{  //S -> id = E;
                //符号栈变化
                //右部弹栈
                Symbol E = symbolStack.peek();
                symbolStack.pop();
                symbolStack.pop();
                Symbol id = symbolStack.peek();
                symbolStack.pop();

                //把id的value给S
                Symbol S = new Symbol(production.head(),id.value);
                //左部压栈
                symbolStack.push(S);

                //生成指令
                IRValue value = E.value;
                Instruction instruction = Instruction.createMov((IRVariable) id.value,value);
                instructions.add(instruction);

                break;
            }
            case 7 ->{  //S -> return E;
                //符号栈变化
                //右部弹栈
                Symbol E = symbolStack.peek();
                symbolStack.pop();
                symbolStack.pop();

                Symbol S = new Symbol(production.head(),IRVariable.named("S2"));
                //左部压栈
                symbolStack.push(S);

                //生成指令
                Instruction instruction = Instruction.createRet(E.value);
                instructions.add(instruction);

                break;
            }
            case 8 ->{  //E -> E + A;
                //符号栈变化
                //右部弹栈
                Symbol A = symbolStack.peek();
                symbolStack.pop();
                symbolStack.pop();
                Symbol E1 = symbolStack.peek();
                symbolStack.pop();

                IRVariable res = IRVariable.temp();
                Symbol E = new Symbol(production.head(),res);
                //左部压栈
                symbolStack.push(E);

                //生成指令
                Instruction instruction = Instruction.createAdd(res, E1.value,A.value);
                instructions.add(instruction);

                break;
            }
            case 9 ->{  //E -> E - A;
                //符号栈变化
                //右部弹栈
                Symbol A = symbolStack.peek();
                symbolStack.pop();
                symbolStack.pop();
                Symbol E1 = symbolStack.peek();
                symbolStack.pop();

                IRVariable res = IRVariable.temp();
                Symbol E = new Symbol(production.head(),res);
                //左部压栈
                symbolStack.push(E);

                //生成指令
                Instruction instruction = Instruction.createSub(res, E1.value,A.value);
                instructions.add(instruction);

                break;
            }
            case 10 ->{ //E -> A;
                //符号栈变化
                //右部弹栈
                Symbol A = symbolStack.peek();
                symbolStack.pop();

                //把A的value给E
                Symbol E = new Symbol(production.head(),A.value);
                //左部压栈
                symbolStack.push(E);

                break;
            }
            case 11 ->{ //A -> A * B;
                //符号栈变化
                //右部弹栈
                Symbol B = symbolStack.peek();
                symbolStack.pop();
                symbolStack.pop();
                Symbol A1 = symbolStack.peek();
                symbolStack.pop();

                IRVariable res = IRVariable.temp();
                Symbol A = new Symbol(production.head(),res);
                //左部压栈
                symbolStack.push(A);

                //生成指令
                Instruction instruction = Instruction.createMul(res, A1.value,B.value);
                instructions.add(instruction);

                break;
            }
            case 12 ->{ //A -> B;
                //符号栈变化
                //右部弹栈
                Symbol B = symbolStack.peek();
                symbolStack.pop();

                //把B的value给A
                Symbol A = new Symbol(production.head(),B.value);
                //左部压栈
                symbolStack.push(A);

                break;
            }
            case 13 ->{ //B -> ( E );
                //符号栈变化
                //右部弹栈
                symbolStack.pop();
                Symbol E = symbolStack.peek();
                symbolStack.pop();
                symbolStack.pop();

                //把E的value给B
                Symbol B = new Symbol(production.head(),E.value);
                //左部压栈
                symbolStack.push(B);

                break;
            }
            case 14 ->{ //B -> id;
                //符号栈变化
                //右部弹栈
                Symbol id = symbolStack.peek();
                symbolStack.pop();

                //把id的value给B
                Symbol B = new Symbol(production.head(),id.value);
                //左部压栈
                symbolStack.push(B);

                break;
            }
            case 15 ->{ //B -> IntConst;
                //符号栈变化
                //右部弹栈
                Symbol IntConst = symbolStack.peek();
                symbolStack.pop();

                //把IntConst的value给B
                Symbol B = new Symbol(production.head(),IntConst.value);
                //左部压栈
                symbolStack.push(B);

                break;
            }
            default -> {
                break;
            }
        }
    }


    @Override
    public void whenAccept(Status currentStatus) {
        // TODO
        //throw new NotImplementedException();
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        // TODO
        //throw new NotImplementedException();
    }

    public List<Instruction> getIR() {
        // TODO
        //throw new NotImplementedException();
        return instructions;
    }

    public void dumpIR(String path) {
        FileUtils.writeLines(path, getIR().stream().map(Instruction::toString).toList());
    }
}

