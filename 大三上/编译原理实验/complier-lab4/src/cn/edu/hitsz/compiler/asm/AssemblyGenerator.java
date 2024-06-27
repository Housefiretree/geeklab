package cn.edu.hitsz.compiler.asm;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.ir.*;
import cn.edu.hitsz.compiler.parser.IRGenerator;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.awt.print.PrinterGraphics;
import java.util.*;


/**
 * TODO: 实验四: 实现汇编生成
 * <br>
 * 在编译器的整体框架中, 代码生成可以称作后端, 而前面的所有工作都可称为前端.
 * <br>
 * 在前端完成的所有工作中, 都是与目标平台无关的, 而后端的工作为将前端生成的目标平台无关信息
 * 根据目标平台生成汇编代码. 前后端的分离有利于实现编译器面向不同平台生成汇编代码. 由于前后
 * 端分离的原因, 有可能前端生成的中间代码并不符合目标平台的汇编代码特点. 具体到本项目你可以
 * 尝试加入一个方法将中间代码调整为更接近 risc-v 汇编的形式, 这样会有利于汇编代码的生成.
 * <br>
 * 为保证实现上的自由, 框架中并未对后端提供基建, 在具体实现时可自行设计相关数据结构.
 *
 * @see AssemblyGenerator#run() 代码生成与寄存器分配
 */
public class AssemblyGenerator {

    /**
     * 加载前端提供的中间代码
     * <br>
     * 视具体实现而定, 在加载中或加载后会生成一些在代码生成中会用到的信息. 如变量的引用
     * 信息. 这些信息可以通过简单的映射维护, 或者自行增加记录信息的数据结构.
     *
     * @param originInstructions 前端提供的中间代码
     */


    /**保存预处理后的指令*/
    List<Instruction> preprocessedInstructions = new ArrayList<>();

    /**双射*/
    BMap<IRVariable,String> map = new BMap<>();

    /**usageMap，用于记录变量的使用*/
    Map<IRVariable,Integer> usageMap = new HashMap<>();


    /**空闲寄存器列表*/
    List<String> available_Regs = new ArrayList<>(
            Arrays.asList("t0","t1","t2","t3","t4","t5","t6")
    );

    /**生成的riscv指令列表*/
    List<String> riscvInstructions = new ArrayList<>(
            Arrays.asList(".text")
    );



    public void loadIR(List<Instruction> originInstructions) {
        // TODO: 读入前端提供的中间代码并生成所需要的信息
        //根据输入的原始指令进行预处理
        for (Instruction originInstruction:originInstructions) {
            InstructionKind kind = originInstruction.getKind();
            if(kind.isBinary()){
                //对于 BinaryOp(两个操作数的指令):
                IRValue LHS = originInstruction.getLHS();
                IRValue RHS = originInstruction.getRHS();
                if(LHS.isImmediate()&&RHS.isImmediate()){
                    //将操作两个立即数的 BinaryOp 直接进行求值得到结果, 然后替换成 MOV 指令
                    IRImmediate LHS_Immediate = (IRImmediate)LHS;
                    IRImmediate RHS_Immediate = (IRImmediate)RHS;

                    //直接求值
                    int res=0;
                    if(kind==InstructionKind.ADD){
                        res = LHS_Immediate.getValue() + RHS_Immediate.getValue();
                    }else if(kind==InstructionKind.SUB){
                        res = LHS_Immediate.getValue() - RHS_Immediate.getValue();
                    }else if(kind==InstructionKind.MUL){
                        res = LHS_Immediate.getValue() * RHS_Immediate.getValue();
                    }

                    //创建MOV指令，加入列表
                    IRVariable variable = IRVariable.temp();
                    IRImmediate immediate = IRImmediate.of(res);
                    Instruction newInstruction = Instruction.createMov(variable,immediate);
                    preprocessedInstructions.add(newInstruction);

                }else if(kind==InstructionKind.MUL&&(LHS.isImmediate()||RHS.isImmediate())
                        ||kind==InstructionKind.SUB&&LHS.isImmediate()){
                    //将操作一个立即数的乘法和左立即数减法调整
                    IRImmediate immediate =null;
                    IRVariable a = IRVariable.temp();

                    if(LHS.isImmediate()){
                        immediate =(IRImmediate) (originInstruction.getLHS());

                    }else if(RHS.isImmediate()){
                        immediate =(IRImmediate) (originInstruction.getRHS());
                    }

                    //前插一条MOV a,imm
                    Instruction newInstruction1 = Instruction.createMov(a,immediate);
                    preprocessedInstructions.add(newInstruction1);

                    //用a替换原立即数, 将指令调整为无立即数指令
                    Instruction newInstruction2=null;
                    IRVariable result = originInstruction.getResult();
                    if(LHS.isImmediate()){
                        if(kind==InstructionKind.SUB){
                            newInstruction2=Instruction.createSub(result,a,RHS);
                        }else{
                            newInstruction2=Instruction.createMul(result,a,RHS);
                        }
                    }else if(RHS.isImmediate()){
                        newInstruction2=Instruction.createMul(result,LHS,a);
                    }
                    preprocessedInstructions.add(newInstruction2);

                }else if(LHS.isImmediate()||RHS.isImmediate()){
                    //将操作一个立即数的指令 (除了乘法和左立即数减法) 进行调整, 使之满足 a := b op imm 的格式
                    Instruction newInstruction;
                    IRVariable result = originInstruction.getResult();
                    if(kind==InstructionKind.ADD&&LHS.isImmediate()){
                        newInstruction=Instruction.createAdd(result,RHS,LHS);
                    }else{
                        newInstruction=originInstruction;
                    }
                    preprocessedInstructions.add(newInstruction);

                }else{
                    preprocessedInstructions.add(originInstruction);
                }

            }else if(kind.isUnary()){
                preprocessedInstructions.add(originInstruction);
            }else if(kind.isReturn()){
                //根据语言规定, 当遇到 Ret 指令后直接舍弃后续指令.
                preprocessedInstructions.add(originInstruction);
                return;
            }else{

            }
        }


        //throw new NotImplementedException();
    }


    /**
     * 执行代码生成.
     * <br>
     * 根据理论课的做法, 在代码生成时同时完成寄存器分配的工作. 若你觉得这样的做法不好,
     * 也可以将寄存器分配和代码生成分开进行.
     * <br>
     * 提示: 寄存器分配中需要的信息较多, 关于全局的与代码生成过程无关的信息建议在代码生
     * 成前完成建立, 与代码生成的过程相关的信息可自行设计数据结构进行记录并动态维护.
     */

    void usageMap_Add(IRVariable variable){
        if(usageMap.containsKey(variable)){
            //如果已经有了，则值加1
            usageMap.put(variable,usageMap.get(variable)+1);
        }else{
            //否则添加到usageMap中，并且将值置为1
            usageMap.put(variable,1);
        }
    }

    /**寄存器选择算法*/
    String Reg_Allocate(IRVariable variable){
        String reg=null;

        if(map.containsKey(variable)){
            //如果已经在寄存器中，使用当前寄存器
            reg=map.getByKey(variable);
        }else{
            if(!available_Regs.isEmpty()){
                //若还有空闲寄存器，分配一个（这里选择列表最后一个）
                reg=available_Regs.get(available_Regs.size()-1);
                map.replace(variable,reg);
                //并且将分配的寄存器从空闲寄存器列表中移出
                available_Regs.remove(available_Regs.size()-1);
            }else{
                //若空闲寄存器列表为空，即没有空闲寄存器了，则需要夺取不再使用的变量所占的寄存器
                for (IRVariable var : usageMap.keySet()) {
                    //看usageMap，当寄存器中有该变量且对应的值为0，说明不再使用，则夺取该寄存器
                    if(map.getByKey(var)!=null&&usageMap.get(var)==0){
                        reg=map.getByKey(var);
                        break;
                    }
                }
                //将变量存入寄存器
                map.replace(variable,reg);
            }

        }

        //分配之后，计数减1
        usageMap.put(variable,usageMap.get(variable)-1);
        //System.out.println("reg:"+reg);

        return reg;

    }




    public void run() {
        // TODO: 执行寄存器分配与代码生成

        for (Instruction preprocessedInstruction:preprocessedInstructions) {
            InstructionKind kind = preprocessedInstruction.getKind();
            if(kind.isBinary()){
                //对于 BinaryOp(两个操作数的指令):
                IRValue LHS = preprocessedInstruction.getLHS();
                IRValue RHS = preprocessedInstruction.getRHS();
                IRValue result = preprocessedInstruction.getResult();
                //若LHS是变量
                if(LHS.isIRVariable()){
                    usageMap_Add((IRVariable) LHS);
                }
                //若RHS是变量
                if(RHS.isIRVariable()){
                    usageMap_Add((IRVariable) RHS);
                }
                //若result是变量
                if(result.isIRVariable()){
                    usageMap_Add((IRVariable) result);
                }

            }else if(kind.isUnary()){
                IRValue result = preprocessedInstruction.getResult();
                IRValue from = preprocessedInstruction.getFrom();

                //若result是变量
                if(result.isIRVariable()){
                    usageMap_Add((IRVariable) result);
                }
                //若from是变量
                if(from.isIRVariable()){
                    usageMap_Add((IRVariable) from);
                }

            }else if(kind.isReturn()){
                IRValue returnValue = preprocessedInstruction.getReturnValue();
                //若returnValue是变量
                if(returnValue.isIRVariable()){
                    usageMap_Add((IRVariable) returnValue);
                }
            }else{

            }
        }


        for (Instruction preprocessedInstruction:preprocessedInstructions){
            InstructionKind kind = preprocessedInstruction.getKind();
            String riscvInstruction;

            if(kind.isBinary()){
                IRVariable Result = preprocessedInstruction.getResult();
                IRValue LHS = preprocessedInstruction.getLHS();
                IRValue RHS = preprocessedInstruction.getRHS();

                String lhs = "" ;
                String rhs = "" ;
                String result = "";
                String op = "";

                //使用寄存器选择算法为result选择寄存器
                result = Reg_Allocate(Result);

                //当LHS为变量时，使用寄存器分配算法为其分配寄存器；当LHS为立即数时，直接获取
                if(LHS.isIRVariable()){
                    lhs = Reg_Allocate((IRVariable) LHS);
                }else if(LHS.isImmediate()){
                    lhs = lhs + (((IRImmediate) LHS).getValue());
                }

                //RHS同理
                if(RHS.isIRVariable()){
                    rhs = Reg_Allocate((IRVariable) RHS);
                }else if(RHS.isImmediate()){
                    rhs = rhs + (((IRImmediate) RHS).getValue());
                }


                //相应的操作op
                if(RHS.isImmediate()){
                    if(kind==InstructionKind.ADD){
                        op="addi";
                    }else if(kind==InstructionKind.MUL){
                        op="muli";
                    }
                }else{
                    if(kind==InstructionKind.ADD){
                        op="add";
                    }else if(kind==InstructionKind.MUL){
                        op="mul";
                    }else if(kind==InstructionKind.SUB){
                        op="sub";
                    }
                }

                //生成汇编指令
                riscvInstruction= "    " + op + " " + result + " " + lhs + " " + rhs;
                riscvInstructions.add(riscvInstruction);
            }else if(kind.isUnary()){
                IRVariable Result = preprocessedInstruction.getResult();
                IRValue From = preprocessedInstruction.getFrom();

                String from = "";
                String result = "";
                String op = " ";

                //使用寄存器选择算法为result选择寄存器
                result = Reg_Allocate(Result);

                //当From为变量时，使用寄存器分配算法为其分配寄存器；当From为立即数时，直接获取
                if(From.isIRVariable()){
                    from = Reg_Allocate((IRVariable) From);
                }else if(From.isImmediate()){
                    from = from + ((IRImmediate) From).getValue();
                }

                //相应的操作op
                if(From.isIRVariable()){
                    op="mv";
                }else if(From.isImmediate()){
                    op="li";
                }

                //生成汇编指令
                riscvInstruction ="    " + op + " "  + result + " " + from ;
                riscvInstructions.add(riscvInstruction);

            }else if(kind.isReturn()){
                //return，分配寄存器并生成汇编指令，最终结果写入a0
                IRValue ReturnValue = preprocessedInstruction.getReturnValue();
                String returnValue = Reg_Allocate((IRVariable) ReturnValue);
                riscvInstruction = "    "+"mv"+" "+"a0"+" "+returnValue;
                riscvInstructions.add(riscvInstruction);
            }else{

            }
        }


        //throw new NotImplementedException();
    }


    /**
     * 输出汇编代码到文件
     *
     * @param path 输出文件路径
     */
    public void dump(String path) {
        // TODO: 输出汇编代码到文件
        /**for (String riscvInstruction: riscvInstructions) {
         System.out.println(riscvInstruction);
         }*/
        FileUtils.writeLines(path, riscvInstructions.stream().toList());
        //throw new NotImplementedException();
    }
}

