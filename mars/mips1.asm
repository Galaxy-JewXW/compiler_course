.data:
s0: .asciiz "21376218\n"
s1: .asciiz "\n"
s2: .asciiz "Finish test1\n"

.text:
func_main:
main_bb0:
    # call void @putstr(i8* getelementptr inbounds ([10 x i8], [10 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # br label %b1

main_bb1:
    # br label %b2

main_bb2:
    # br label %b3

main_bb3:
    # br label %b4

main_bb4:
    # br label %b5

main_bb5:
    # br label %b6

main_bb6:
    # br label %b7

main_bb7:
    # %v8 = add i32 0, 1
    li $k0, 0
    addiu $t3, $k0, 1
    # %v9 = icmp ne i32 %v8, 0
    # br i1 %v9, label %b40, label %b10
    bne $t3, 0, main_bb40

main_bb10:
    # br label %b11

main_bb11:
    # br label %b12

main_bb12:
    # br label %b13

main_bb13:
    # %v14 = add i32 %v8, 1
    addiu $t3, $t3, 1
    # %v15 = icmp eq i32 0, %v14
    li $k0, 0
    seq $t0, $k0, $t3
    # %v16 = icmp ne i32 %v15, 0
    # br i1 %v16, label %b18, label %b17
    bne $t0, 0, main_bb18

main_bb17:
    # br label %b18

main_bb18:
    # br label %b19

main_bb19:
    # br label %b20

main_bb20:
    # br label %b21

main_bb21:
    # br label %b22

main_bb22:
    # %v23 = add i32 %v14, 1
    addiu $t3, $t3, 1
    # %v24 = icmp ne i32 %v23, 0
    # br i1 %v24, label %b37, label %b25
    bne $t3, 0, main_bb37

main_bb25:
    # br label %b26

main_bb26:
    # br label %b27

main_bb27:
    # br label %b28

main_bb28:
    # %v29 = add i32 %v23, 1
    addiu $t3, $t3, 1
    # %v30 = icmp eq i32 0, %v29
    li $k0, 0
    seq $t0, $k0, $t3
    # %v31 = icmp ne i32 %v30, 0
    # br i1 %v31, label %b48, label %b66
    bne $t0, 0, main_bb48
    j main_bb66

main_bb32:
    # %v33 = add i32 %v29, 1
    addiu $t1, $t3, 1
    # %v34 = icmp ne i32 %v33, 0
    # br i1 %v34, label %b35, label %b68
    beq $t1, 0, main_bb68

main_bb35:
    # br label %b64
    j main_bb64

main_bb36:
    # br label %b76
    j main_bb76

main_bb37:
    # br label %b39
    j main_bb39

main_bb38:
    # br label %b57
    j main_bb57

main_bb39:
    # br label %b25
    j main_bb25

main_bb40:
    # br label %b10
    j main_bb10

main_bb41:
    # move %v79 -> %v44
    move $t2, $t1
    # br label %b45
    j main_bb45

main_bb43:
    # move %v86 -> %v44
    move $t2, $t4
    # br label %b45

main_bb45:
    # move %v44 -> %v49
    move $t0, $t2
    # br label %b52
    j main_bb52

main_bb47:
    # br label %b62
    j main_bb62

main_bb48:
    # br label %b49

main_bb49:
    # br label %b32
    j main_bb32

main_bb50:
    # move %v86 -> %v49
    move $t0, $t4
    # br label %b52

main_bb52:
    # br label %b53

main_bb53:
    # br label %b54

main_bb54:
    # %v55 = add i32 %v49, 1
    addiu $t0, $t0, 1
    # %v56 = icmp eq i32 1, %v55
    # br i1 %v56, label %b38, label %b57
    beq $t0, 1, main_bb38

main_bb57:
    # br label %b58

main_bb58:
    # br label %b59

main_bb59:
    # %v60 = add i32 %v55, 1
    addiu $t1, $t0, 1
    # %v61 = icmp ne i32 1, %v60
    # br i1 %v61, label %b47, label %b62
    bne $t1, 1, main_bb47

main_bb62:
    # br label %b63

main_bb63:
    # call void @putint(i32 %v60)
    move $a0, $t1
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # call void @putstr(i8* getelementptr inbounds ([14 x i8], [14 x i8]* @.s.2, i64 0, i64 0))
    la $a0, s2
    syscall
    # ret i32 0
    li $v0, 10
    syscall

main_bb64:
    # move %v33 -> %v63
    move $t0, $t1
    # br label %b70
    j main_bb70

main_bb66:
    # move %v29 -> %v63
    move $t0, $t3
    # br label %b70
    j main_bb70

main_bb68:
    # move %v33 -> %v63
    move $t0, $t1
    # br label %b70

main_bb70:
    # br label %b71

main_bb71:
    # br label %b72

main_bb72:
    # br label %b73

main_bb73:
    # %v74 = add i32 %v63, 1
    addiu $t0, $t0, 1
    # %v75 = icmp ne i32 %v74, 0
    # br i1 %v75, label %b36, label %b76
    bne $t0, 0, main_bb36

main_bb76:
    # br label %b77

main_bb77:
    # br label %b78

main_bb78:
    # %v79 = add i32 %v74, 1
    addiu $t1, $t0, 1
    # %v80 = icmp eq i32 0, %v79
    li $k0, 0
    seq $t0, $k0, $t1
    # %v81 = icmp ne i32 %v80, 0
    # br i1 %v81, label %b41, label %b82
    bne $t0, 0, main_bb41

main_bb82:
    # br label %b83

main_bb83:
    # br label %b84

main_bb84:
    # br label %b85

main_bb85:
    # %v86 = add i32 %v79, 1
    addiu $t4, $t1, 1
    # %v87 = icmp ne i32 %v86, 0
    # br i1 %v87, label %b43, label %b50
    bne $t4, 0, main_bb43
    j main_bb50

