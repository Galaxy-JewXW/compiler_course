package pass.Analysis;

import IR.types.VoidType;
import IR.values.Value;
import IR.values.instructions.Operator;
import IR.values.instructions.men.MemInst;

import java.util.ArrayList;

public class PCInst extends MemInst {
    private final ArrayList<Value> targetPhi = new ArrayList<>();
    private final ArrayList<Value> valOfPhi = new ArrayList<>();

    public PCInst() {
        super(VoidType.voidType, Operator.PC);
    }

    public void addPair(Value phi, Value val) {
        this.targetPhi.add(phi);
        this.valOfPhi.add(val);
    }

    public ArrayList<Value> getTargetPhi() {
        return targetPhi;
    }

    public ArrayList<Value> getValOfPhi() {
        return valOfPhi;
    }

    @Override
    public String toString() {
        StringBuilder ans = new StringBuilder("PC:");
        for (int i = 0; i < valOfPhi.size(); i++) {
            ans.append('(');
            ans.append(targetPhi.get(i).getName()).append(" <- ");
            ans.append(valOfPhi.get(i).getName());
            ans.append(')');
        }
        return ans.toString();
    }
}
