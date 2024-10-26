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
    # br label %b8
    j main_bb8

main_bb5:
    # move %v10 -> %v22
    move $t5, $t3
    # br label %b7

main_bb7:
    # br label %b19
    j main_bb19

main_bb8:
    # br label %b9

main_bb9:
    # %v10 = add i32 0, 1
    li $k0, 0
    addiu $t3, $k0, 1
    # br label %b11

main_bb11:
    # %v12 = icmp ne i32 %v10, 0
    # br i1 %v12, label %b13, label %b5
    beq $t3, 0, main_bb5

main_bb13:
    # move %v10 -> %v22
    move $t5, $t3
    # br label %b7
    j main_bb7

main_bb15:
    # move %v21 -> %v35
    # br label %b17

main_bb17:
    # br label %b18

main_bb18:
    # br label %b29
    j main_bb29

main_bb19:
    # br label %b20

main_bb20:
    # %v21 = add i32 %v22, 1
    addiu $t1, $t5, 1
    # br label %b22

main_bb22:
    # %v23 = icmp eq i32 0, %v21
    li $k0, 0
    seq $t3, $k0, $t1
    # %v24 = icmp ne i32 %v23, 0
    # br i1 %v24, label %b15, label %b25
    bne $t3, 0, main_bb15

main_bb25:
    # move %v21 -> %v35
    # br label %b17
    j main_bb17

main_bb27:
    # br label %b28

main_bb28:
    # br label %b42
    j main_bb42

main_bb29:
    # br label %b30

main_bb30:
    # %v31 = add i32 %v35, 1
    addiu $t1, $t1, 1
    # br label %b32

main_bb32:
    # %v33 = icmp ne i32 %v31, 0
    # br i1 %v33, label %b34, label %b28
    beq $t1, 0, main_bb28

main_bb34:
    # br label %b27
    j main_bb27

main_bb35:
    # move %v70 -> %v72
    move $t1, $t4
    # br label %b41
    j main_bb41

main_bb37:
    # move %v44 -> %v72
    move $t1, $t5
    # br label %b41
    j main_bb41

main_bb39:
    # move %v50 -> %v72
    move $t1, $t6
    # br label %b41

main_bb41:
    # br label %b62
    j main_bb62

main_bb42:
    # br label %b43

main_bb43:
    # %v44 = add i32 %v31, 1
    addiu $t5, $t1, 1
    # br label %b45

main_bb45:
    # %v46 = icmp eq i32 0, %v44
    li $k0, 0
    seq $t3, $k0, $t5
    # %v47 = icmp ne i32 %v46, 0
    # br i1 %v47, label %b48, label %b37
    beq $t3, 0, main_bb37

main_bb48:
    # br label %b49

main_bb49:
    # %v50 = add i32 %v44, 1
    addiu $t6, $t5, 1
    # br label %b51

main_bb51:
    # %v52 = icmp ne i32 %v50, 0
    # br i1 %v52, label %b53, label %b39
    beq $t6, 0, main_bb39

main_bb53:
    # move %v50 -> %v70
    move $t4, $t6
    # br label %b35
    j main_bb35

main_bb55:
    # br label %b56

main_bb56:
    # br label %b57

main_bb57:
    # %v58 = add i32 %v64, 1
    addiu $t3, $t3, 1
    # br label %b59

main_bb59:
    # %v60 = icmp eq i32 0, %v58
    li $k0, 0
    seq $t1, $k0, $t3
    # %v61 = icmp ne i32 %v60, 0
    # br i1 %v61, label %b67, label %b80
    bne $t1, 0, main_bb67
    j main_bb80

main_bb62:
    # br label %b63

main_bb63:
    # %v64 = add i32 %v72, 1
    addiu $t3, $t1, 1
    # br label %b65

main_bb65:
    # %v66 = icmp ne i32 %v64, 0
    # br i1 %v66, label %b55, label %b56
    bne $t3, 0, main_bb55
    j main_bb56

main_bb67:
    # move %v58 -> %v108
    move $t0, $t3
    # br label %b71
    j main_bb71

main_bb69:
    # move %v83 -> %v108
    move $t0, $t2
    # br label %b71

main_bb71:
    # move %v108 -> %v110
    # br label %b75
    j main_bb75

main_bb73:
    # move %v83 -> %v110
    move $t0, $t2
    # br label %b75

main_bb75:
    # br label %b76

main_bb76:
    # %v77 = add i32 %v110, 1
    addiu $t1, $t0, 1
    # br label %b78

main_bb78:
    # %v79 = icmp eq i32 1, %v77
    # br i1 %v79, label %b86, label %b87
    beq $t1, 1, main_bb86
    j main_bb87

main_bb80:
    # br label %b81

main_bb81:
    # br label %b82

main_bb82:
    # %v83 = add i32 %v58, 1
    addiu $t2, $t3, 1
    # br label %b84

main_bb84:
    # %v85 = icmp ne i32 %v83, 0
    # br i1 %v85, label %b69, label %b73
    bne $t2, 0, main_bb69
    j main_bb73

main_bb86:
    # br label %b87

main_bb87:
    # br label %b88

main_bb88:
    # %v89 = add i32 %v77, 1
    addiu $t0, $t1, 1
    # br label %b90

main_bb90:
    # %v91 = icmp ne i32 1, %v89
    # br i1 %v91, label %b92, label %b93
    beq $t0, 1, main_bb93

main_bb92:
    # br label %b93

main_bb93:
    # br label %b94

main_bb94:
    # call void @putint(i32 %v89)
    move $a0, $t0
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

