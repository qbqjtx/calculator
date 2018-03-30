package model;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public class ExpTree {
    private ExpTree left;
    private ExpTree right;
    private Expression self;

    public ExpTree(@Nullable ExpTree left, @NotNull Expression self, @Nullable ExpTree right) {
        this.left = left;
        this.right = right;
        this.self = self;

    }

    public ExpTree(@NotNull Expression self) {
        this.self = self;
        this.left = null;
        this.right = null;
    }

    public Expression getSelf() {
        return self;
    }

    public ExpTree getRight() {
        return right;
    }

    public ExpTree getLeft() {
        return left;
    }


    public double evaluate() {
        // number comes in 3 forms, 100, -100, and 1/100
        if (left == null && right == null) {
            String s = self.getValue();
            if (s.charAt(0) == '-')
                return 0-Double.valueOf(s.substring(1));
            else if (s.contains("1/")) {
                if (Double.valueOf(s.substring(2)) == 0)
                    throw new IllegalArgumentException("can't divide by 0");
                else
                    return 1.0/Double.valueOf(s.substring(2));
            }
            else
                return Double.valueOf(s);
        }
        else if (left != null && right != null) {
            switch (self.getValue()) {
                case "+": return left.evaluate() + right.evaluate();
                case "*": return left.evaluate() * right.evaluate();
                case "/":
                    if (right.evaluate() == 0) throw new IllegalArgumentException("can't divide by 0");
                    return left.evaluate() / right.evaluate();
            }
        }
        throw new IllegalArgumentException("ExpTree: unable to evaluate");
    }


//    /*
//        string input: 0123456789.()+-/*
//        function input: =, c, del
//
//        elements in abstract syntax tree (AST): numbers, operators
//     */


}