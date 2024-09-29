package backend;

import backend.enums.AsmOp;
import backend.enums.Register;
import backend.text.*;

public class OptimizedDivision {
    private static long calculateMagicNumber(int divisor) {
        long nc = ((1L << 31) - ((1L << 31) % divisor) - 1) / divisor;
        long t = 32;
        while ((1L << t) <= nc * (divisor - (1L << t) % divisor)) {
            t++;
        }
        return ((1L << t) + divisor - (1L << t) % divisor) / divisor;
    }

    public static void makeVarDivConst(Register varReg, int constInt, Register targetReg) {
        int abs = Math.abs(constInt);
        if (abs == 1) {
            if (constInt < 0) {
                new CalcAsm(targetReg, AsmOp.SUBU, Register.ZERO, varReg);
            } else {
                new MoveAsm(targetReg, varReg);
            }
            return;
        }

        if ((abs & (abs - 1)) == 0) {
            // Power of 2 case
            int shift = 31 - Integer.numberOfLeadingZeros(abs);
            Register dividend = getDividend(varReg, abs);
            new CalcAsm(targetReg, AsmOp.SRA, dividend, shift);
        } else if (abs < 32) {
            // Use pre-calculated magic numbers for small divisors
            long m = calculateMagicNumber(abs);
            int n = (int) ((m << 32) >>> 32);
            int shift = (int) (m >>> 32);
            new LiAsm(Register.V0, n);
            new MulDivAsm(varReg, AsmOp.MULT, Register.V0);
            new MDRegAsm(AsmOp.MFHI, Register.V1);
            new CalcAsm(Register.V0, AsmOp.SRA, Register.V1, shift);
            new CalcAsm(Register.A0, AsmOp.SRL, varReg, 31);
            new CalcAsm(targetReg, AsmOp.ADDU, Register.V0, Register.A0);
        } else {
            // Original logic for larger divisors
            long t = 32;
            long nc = ((long) 1 << 31) - (((long) 1 << 31) % abs) - 1;
            while (((long) 1 << t) <= nc * (abs - ((long) 1 << t) % abs)) {
                t++;
            }
            long m = ((((long) 1 << t) + (long) abs - ((long) 1 << t) % abs) / (long) abs);
            int n = (int) ((m << 32) >>> 32);
            int shift = (int) (t - 32);
            new LiAsm(Register.V0, n);
            if (m >= 0x80000000L) {
                new MDRegAsm(AsmOp.MTHI, varReg);
                new MulDivAsm(varReg, AsmOp.MADD, Register.V0);
            } else {
                new MulDivAsm(varReg, AsmOp.MULT, Register.V0);
            }
            new MDRegAsm(AsmOp.MFHI, Register.V1);
            new CalcAsm(Register.V0, AsmOp.SRA, Register.V1, shift);
            new CalcAsm(Register.A0, AsmOp.SRL, varReg, 31);
            new CalcAsm(targetReg, AsmOp.ADDU, Register.V0, Register.A0);
        }
        if (constInt < 0) {
            new CalcAsm(targetReg, AsmOp.SUBU, Register.ZERO, targetReg);
        }
    }

    private static Register getDividend(Register oldDividend, int abs) {
        int l = 31 - Integer.numberOfLeadingZeros(abs);
        new CalcAsm(Register.V0, AsmOp.SRA, oldDividend, 31);
        if (l > 0) {
            new CalcAsm(Register.V0, AsmOp.SRL, Register.V0, 32 - l);
        }
        new CalcAsm(Register.V1, AsmOp.ADDU, oldDividend, Register.V0);
        return Register.V1;
    }
}