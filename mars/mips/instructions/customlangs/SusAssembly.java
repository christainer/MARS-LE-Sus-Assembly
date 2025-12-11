package mars.mips.instructions.customlangs;
import mars.mips.hardware.*;
import mars.*;
import mars.util.*;
import mars.mips.instructions.*;

public class SusAssembly extends CustomAssembly {

    public static boolean susFlag = false;
    public static final int PBASE = 0x10008000;

    private static int randomState = 0x12345678;

    private static int nextRandom() {
        randomState = randomState * 1103515245 + 12345;
        return randomState;
    }

    @Override
    public String getName() {
        return "SUS-32 Assembly";
    }

    @Override
    public String getDescription() {
        return "Among Us themed custom instruction set (SUS-32)";
    }

    @Override
    protected void populate() {

        //Register Instructions C-type

        instructionList.add(
            new BasicInstruction("taskadd $t3,$t1,$t2",
             "TASKADD : R[rd] = R[rs] + R[rt]",
             BasicInstructionFormat.R_FORMAT,
             "000000 sssss ttttt fffff 00000 100000",
             new SimulationCode()
             {
                 public void simulate(ProgramStatement statement) throws ProcessingException
                 {
                     int[] operands = statement.getOperands();
                     int rd = operands[0];
                     int rs = operands[1];
                     int rt = operands[2];

                     int value = RegisterFile.getValue(rs) + RegisterFile.getValue(rt);
                     RegisterFile.updateRegister(rd, value);
                 }
             }
            )
        );

        instructionList.add(
            new BasicInstruction("tasksub $t3,$t1,$t2",
             "TASKSUB : R[rd] = R[rs] - R[rt]",
             BasicInstructionFormat.R_FORMAT,
             "000000 sssss ttttt fffff 00000 100010",
             new SimulationCode()
             {
                 public void simulate(ProgramStatement statement) throws ProcessingException
                 {
                     int[] operands = statement.getOperands();
                     int rd = operands[0];
                     int rs = operands[1];
                     int rt = operands[2];

                     int value = RegisterFile.getValue(rs) - RegisterFile.getValue(rt);
                     RegisterFile.updateRegister(rd, value);
                 }
             }
            )
        );

        instructionList.add(
            new BasicInstruction("taskand $t3,$t1,$t2",
             "TASKAND : R[rd] = R[rs] & R[rt]",
             BasicInstructionFormat.R_FORMAT,
             "000000 sssss ttttt fffff 00000 100100",
             new SimulationCode()
             {
                 public void simulate(ProgramStatement statement) throws ProcessingException
                 {
                     int[] operands = statement.getOperands();
                     int rd = operands[0];
                     int rs = operands[1];
                     int rt = operands[2];

                     int value = RegisterFile.getValue(rs) & RegisterFile.getValue(rt);
                     RegisterFile.updateRegister(rd, value);
                 }
             }
            )
        );

        instructionList.add(
            new BasicInstruction("taskor $t3,$t1,$t2",
             "TASKOR : R[rd] = R[rs] | R[rt]",
             BasicInstructionFormat.R_FORMAT,
             "000000 sssss ttttt fffff 00000 100101",
             new SimulationCode()
             {
                 public void simulate(ProgramStatement statement) throws ProcessingException
                 {
                     int[] operands = statement.getOperands();
                     int rd = operands[0];
                     int rs = operands[1];
                     int rt = operands[2];

                     int value = RegisterFile.getValue(rs) | RegisterFile.getValue(rt);
                     RegisterFile.updateRegister(rd, value);
                 }
             }
            )
        );

        instructionList.add(
            new BasicInstruction("taskxor $t3,$t1,$t2",
             "TASKXOR : R[rd] = R[rs] ^ R[rt]",
             BasicInstructionFormat.R_FORMAT,
             "000000 sssss ttttt fffff 00000 100110",
             new SimulationCode()
             {
                 public void simulate(ProgramStatement statement) throws ProcessingException
                 {
                     int[] operands = statement.getOperands();
                     int rd = operands[0];
                     int rs = operands[1];
                     int rt = operands[2];

                     int value = RegisterFile.getValue(rs) ^ RegisterFile.getValue(rt);
                     RegisterFile.updateRegister(rd, value);
                 }
             }
            )
        );

        instructionList.add(
            new BasicInstruction("suspeek $t1",
             "SUSPEEK : R[rt] = susFlag (0 or 1)",
             BasicInstructionFormat.R_FORMAT,
             "000000 fffff 00000 00000 00000 101010",
             new SimulationCode()
             {
                 public void simulate(ProgramStatement statement) throws ProcessingException
                 {
                     int[] operands = statement.getOperands();
                     int rt = operands[0];

                     int value = SusAssembly.susFlag ? 1 : 0;
                     RegisterFile.updateRegister(rt, value);
                 }
             }
            )
        );

        //Immediate Instructions T-type

        instructionList.add(
            new BasicInstruction("taskset $t0, $t1, -100",
                "TASKSET : R[rt] = sign-extended immediate (rs is ignored, use $zero)",
                BasicInstructionFormat.I_FORMAT,
                "001000 sssss fffff tttttttttttttttt",
                new SimulationCode()
                {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                    {
                        int[] operands = statement.getOperands();
                        int rt = operands[0];
                        int rs = operands[1];
                        int imm = operands[2];

                        int value = (short) imm;
                        RegisterFile.updateRegister(rt, value);
                    }
                }
            )
        );

        instructionList.add(
            new BasicInstruction("loadtask $t2,$t1,imm",
             "LOADTASK : R[rt] = M[R[rs] + imm]",
             BasicInstructionFormat.I_FORMAT,
             "100011 sssss fffff tttttttttttttttt",
             new SimulationCode()
             {
                 public void simulate(ProgramStatement statement) throws ProcessingException
                 {
                     int[] operands = statement.getOperands();
                     int rt = operands[0];
                     int rs = operands[1];
                     int imm = operands[2];

                     int base = RegisterFile.getValue(rs);
                     int offset = (short) imm;
                     int address = base + offset;

                     try {
                         int value = Globals.memory.getWord(address);
                         RegisterFile.updateRegister(rt, value);
                     } catch (AddressErrorException e) {
                         throw new ProcessingException(statement, e);
                     }
                 }
             }
            )
        );

        instructionList.add(
            new BasicInstruction("savetask $t2,$t1,imm",
             "SAVETASK : M[R[rs] + imm] = R[rt]",
             BasicInstructionFormat.I_FORMAT,
             "101011 sssss fffff tttttttttttttttt",
             new SimulationCode()
             {
                 public void simulate(ProgramStatement statement) throws ProcessingException
                 {
                     int[] operands = statement.getOperands();
                     int rt = operands[0];
                     int rs = operands[1];
                     int imm = operands[2];

                     int base = RegisterFile.getValue(rs);
                     int offset = (short) imm;
                     int address = base + offset;

                     try {
                         Globals.memory.setWord(address, RegisterFile.getValue(rt));
                     } catch (AddressErrorException e) {
                         throw new ProcessingException(statement, e);
                     }
                 }
             }
            )
        );

        instructionList.add(
            new BasicInstruction("susbeq $t1,$t2,label",
             "SUSBEQ : if (R[rs] == R[rt]) branch to label",
             BasicInstructionFormat.I_BRANCH_FORMAT,
             "000100 fffff sssss tttttttttttttttt",
             new SimulationCode()
             {
                 public void simulate(ProgramStatement statement) throws ProcessingException
                 {
                     int[] operands = statement.getOperands();
                     int rsVal = RegisterFile.getValue(operands[0]);
                     int rtVal = RegisterFile.getValue(operands[1]);

                     if (rsVal == rtVal) {
                         Globals.instructionSet.processBranch(operands[2]);
                     }
                 }
             }
            )
        );

        instructionList.add(
            new BasicInstruction("susbne $t1,$t2,label",
             "SUSBNE : if (R[rs] != R[rt]) branch to label",
             BasicInstructionFormat.I_BRANCH_FORMAT,
             "000101 fffff sssss tttttttttttttttt",
             new SimulationCode()
             {
                 public void simulate(ProgramStatement statement) throws ProcessingException
                 {
                     int[] operands = statement.getOperands();
                     int rsVal = RegisterFile.getValue(operands[0]);
                     int rtVal = RegisterFile.getValue(operands[1]);

                     if (rsVal != rtVal) {
                         Globals.instructionSet.processBranch(operands[2]);
                     }
                 }
             }
            )
        );

        // Jump Instructions V-type

        instructionList.add(
            new BasicInstruction("vent label",
             "VENT : Unconditional jump to label",
             BasicInstructionFormat.I_BRANCH_FORMAT,
             "000010 00000 00000 ffffffffffffffff",
             new SimulationCode()
             {
                 public void simulate(ProgramStatement statement) throws ProcessingException
                 {
                     int[] operands = statement.getOperands();
                     Globals.instructionSet.processBranch(operands[0]);
                 }
             }
            )
        );

        instructionList.add(
            new BasicInstruction("ventlink label",
             "VENTLINK : Save $ra then jump to label",
             BasicInstructionFormat.I_BRANCH_FORMAT,
             "000011 00000 00000 ffffffffffffffff",
             new SimulationCode()
             {
                 public void simulate(ProgramStatement statement) throws ProcessingException
                 {
                     int[] operands = statement.getOperands();
                     int currentPC = statement.getAddress();
                     RegisterFile.updateRegister(31, currentPC + 4);
                     Globals.instructionSet.processBranch(operands[0]);
                 }
             }
            )
        );



        // Special Instructions (Includes C-types, T-types, V-types. Special functions)

        instructionList.add(
            new BasicInstruction("report $t1",
             "REPORT : Set susFlag if R[rs] != 0",
             BasicInstructionFormat.R_FORMAT,
             "000000 fffff 00000 00000 00000 101000",
             new SimulationCode()
             {
                 public void simulate(ProgramStatement statement) throws ProcessingException
                 {
                     int[] operands = statement.getOperands();
                     int rs = operands[0];

                     int value = RegisterFile.getValue(rs);
                     SusAssembly.susFlag = (value != 0);
                 }
             }
            )
        );

        instructionList.add(
            new BasicInstruction("sabotage $t3,$t1,$t2",
             "SABOTAGE : R[rd] = R[rs] ^ R[rt]",
             BasicInstructionFormat.R_FORMAT,
             "000000 sssss ttttt fffff 00000 101001",
             new SimulationCode()
             {
                 public void simulate(ProgramStatement statement) throws ProcessingException
                 {
                     int[] operands = statement.getOperands();
                     int rd = operands[0];
                     int rs = operands[1];
                     int rt = operands[2];

                     int value = RegisterFile.getValue(rs) ^ RegisterFile.getValue(rt);
                     RegisterFile.updateRegister(rd, value);
                 }
             }
            )
        );

        instructionList.add(
            new BasicInstruction("faketask $t2,$t1",
             "FAKETASK : Copy R[rs] to R[rd] and set susFlag = true",
             BasicInstructionFormat.R_FORMAT,
             "000000 sssss 00000 fffff 00000 101010",
             new SimulationCode()
             {
                 public void simulate(ProgramStatement statement) throws ProcessingException
                 {
                     int[] operands = statement.getOperands();
                     int rd = operands[0];
                     int rs = operands[1];

                     int value = RegisterFile.getValue(rs);
                     RegisterFile.updateRegister(rd, value);
                     susFlag = true;
                 }
             }
            )
        );

        instructionList.add(
            new BasicInstruction("clearvotes",
             "CLEARVOTES : Clear vote counters in $t0-$t3",
             BasicInstructionFormat.R_FORMAT,
             "000000 00000 00000 00000 00000 101011",
             new SimulationCode()
             {
                 public void simulate(ProgramStatement statement) throws ProcessingException
                 {
                     RegisterFile.updateRegister(8, 0);
                     RegisterFile.updateRegister(9, 0);
                     RegisterFile.updateRegister(10, 0);
                     RegisterFile.updateRegister(11, 0);
                 }
             }
            )
        );

        instructionList.add(
            new BasicInstruction("checksus $t1,$t2",
             "CHECKSUS : Set susFlag if R[rs] != R[rt]",
             BasicInstructionFormat.R_FORMAT,
             "000000 fffff sssss 00000 00000 101100",
             new SimulationCode()
             {
                 public void simulate(ProgramStatement statement) throws ProcessingException
                 {
                     int[] operands = statement.getOperands();
                     int rs = operands[0];
                     int rt = operands[1];

                     int v1 = RegisterFile.getValue(rs);
                     int v2 = RegisterFile.getValue(rt);

                     susFlag = (v1 != v2);
                 }
             }
            )
        );

        instructionList.add(
            new BasicInstruction("randomtask $t3",
             "RANDOMTASK : Put a pseudo-random value into R[rd]",
             BasicInstructionFormat.R_FORMAT,
             "000000 00000 00000 fffff 00000 101101",
             new SimulationCode()
             {
                 public void simulate(ProgramStatement statement) throws ProcessingException
                 {
                     int[] operands = statement.getOperands();
                     int rd = operands[0];

                     int value = nextRandom();
                     RegisterFile.updateRegister(rd, value);
                 }
             }
            )
        );

        instructionList.add(
            new BasicInstruction("ventifneg $t1,label",
             "VENTIFNEG : if (R[rs] < 0) branch to label",
             BasicInstructionFormat.I_BRANCH_FORMAT,
             "010000 fffff 00000 ssssssssssssssss",
             new SimulationCode()
             {
                 public void simulate(ProgramStatement statement) throws ProcessingException
                 {
                     int[] operands = statement.getOperands();
                     int rsVal = RegisterFile.getValue(operands[0]);

                     if (rsVal < 0) {
                         Globals.instructionSet.processBranch(operands[1]);
                     }
                 }
             }
            )
        );

        instructionList.add(
            new BasicInstruction("voteout $t1",
             "VOTEOUT : Eject player with index in R[rs]",
             BasicInstructionFormat.I_FORMAT,
             "010010 fffff 00000 0000000000000000",
             new SimulationCode()
             {
                 public void simulate(ProgramStatement statement) throws ProcessingException
                 {
                     int[] operands = statement.getOperands();
                     int rs = operands[0];

                     int index = RegisterFile.getValue(rs);
                     int address = PBASE + 4 * index;

                     try {
                         Globals.memory.setWord(address, 0);
                     } catch (AddressErrorException e) {
                         throw new ProcessingException(statement, e);
                     }
                 }
             }
            )
        );

        instructionList.add(
            new BasicInstruction("scanmed $t2",
             "SCANMED : Load medbay status from 0xFFFF0000 into R[rt]",
             BasicInstructionFormat.I_FORMAT,
             "010011 00000 fffff 0000000000000000",
             new SimulationCode()
             {
                 public void simulate(ProgramStatement statement) throws ProcessingException
                 {
                     int[] operands = statement.getOperands();
                     int rt = operands[0];

                     int medbayAddress = 0xFFFF0000;

                     try {
                         int value = Globals.memory.getWord(medbayAddress);
                         RegisterFile.updateRegister(rt, value);
                     } catch (AddressErrorException e) {
                         throw new ProcessingException(statement, e);
                     }
                 }
             }
            )
        );

        instructionList.add(
            new BasicInstruction("emergency label",
             "EMERGENCY : Call meeting handler at label",
             BasicInstructionFormat.I_BRANCH_FORMAT,
             "010001 00000 00000 ffffffffffffffff",
             new SimulationCode()
             {
                 public void simulate(ProgramStatement statement) throws ProcessingException
                 {
                     int[] operands = statement.getOperands();
                     int currentPC = statement.getAddress();
                     RegisterFile.updateRegister(31, currentPC + 4);
                     Globals.instructionSet.processBranch(operands[0]);
                 }
             }
            )
        );

    }
}
