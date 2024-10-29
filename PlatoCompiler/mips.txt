.data:
s0: .asciiz "print int : "
s1: .asciiz "\n"
s2: .asciiz "19373479\n"
s3: .asciiz "a: "
s4: .asciiz "c: "
s5: .asciiz "d1: "
s6: .asciiz "d2: "
s7: .asciiz "e1: "
s8: .asciiz "break\n"
s9: .asciiz "e2: "

.text:
func_main:
main_bb0:
    # call void @putstr(i8* getelementptr inbounds ([10 x i8], [10 x i8]* @.s.2, i64 0, i64 0))
    la $a0, s2
    li $v0, 4
    syscall
    # move 5 -> %v44
    li $t0, 5
    # move 4 -> %v45
    li $t5, 4
    # move 3 -> %v46
    li $t4, 3
    # move 10 -> %v47
    li $t3, 10
    # br label %b52
    j main_bb52

main_bb2:
    # %v3 = add i32 %v30, %v17
    addu $t1, $t4, $s0
    # call void @putstr(i8* getelementptr inbounds ([5 x i8], [5 x i8]* @.s.9, i64 0, i64 0))
    la $a0, s9
    li $v0, 4
    syscall
    # call void @putint(i32 %v3)
    move $a0, $t1
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # move %v3 -> %v4
    move $t6, $t1
    # br label %b5

main_bb5:
    # move %v4 -> %v38
    move $t3, $t6
    # br label %b45
    j main_bb45

main_bb7:
    # %v8 = add i32 %v33, 1
    addiu $t3, $t7, 1
    # %v9 = sdiv i32 %v46, %v8
    div $t4, $t3
    mflo $t3
    # %v10 = add i32 %v9, %v33
    addu $s2, $t3, $t7
    # call void @putstr(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.s.4, i64 0, i64 0))
    la $a0, s4
    li $v0, 4
    syscall
    # call void @putint(i32 %v10)
    move $a0, $s2
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # move %v10 -> %v30
    move $t4, $s2
    # br label %b37
    j main_bb37

main_bb12:
    # move %v17 -> %v4
    move $t6, $s0
    # br label %b5
    j main_bb5

main_bb14:
    # call void @putstr(i8* getelementptr inbounds ([7 x i8], [7 x i8]* @.s.8, i64 0, i64 0))
    la $a0, s8
    li $v0, 4
    syscall
    # move %v17 -> %v41
    move $t0, $s0
    # br label %b49
    j main_bb49

main_bb16:
    # %v17 = add i32 %v38, %v33
    addu $s0, $t3, $t7
    # call void @putstr(i8* getelementptr inbounds ([5 x i8], [5 x i8]* @.s.7, i64 0, i64 0))
    la $a0, s7
    li $v0, 4
    syscall
    # call void @putint(i32 %v17)
    move $a0, $s0
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # %v18 = icmp eq i32 %v17, 2
    # br i1 %v18, label %b14, label %b20
    beq $s0, 2, main_bb14
    j main_bb20

main_bb19:
    # br label %b57
    j main_bb57

main_bb20:
    # %v21 = icmp ne i32 %v17, %v30
    # br i1 %v21, label %b2, label %b12
    bne $s0, $t4, main_bb2
    j main_bb12

main_bb22:
    # %v23 = mul i32 %v45, %v33
    mul $t2, $t5, $t7
    # call void @putstr(i8* getelementptr inbounds ([5 x i8], [5 x i8]* @.s.5, i64 0, i64 0))
    la $a0, s5
    li $v0, 4
    syscall
    # call void @putint(i32 %v23)
    move $a0, $t2
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # move %v23 -> %v36
    move $t5, $t2
    # br label %b43
    j main_bb43

main_bb25:
    # br label %b26

main_bb26:
    # call void @putstr(i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # call void @putint(i32 %v47)
    move $a0, $t3
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # br label %b19
    j main_bb19

main_bb27:
    # br label %b28

main_bb28:
    # call void @putstr(i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # call void @putint(i32 %v45)
    move $a0, $t5
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # br label %b29

main_bb29:
    # br label %b30

main_bb30:
    # call void @putstr(i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # call void @putint(i32 %v44)
    move $a0, $t0
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # br label %b31

main_bb31:
    # ret i32 0
    li $v0, 10
    syscall

main_bb32:
    # %v33 = sub i32 %v47, 1
    addiu $t7, $t3, -1
    # call void @putstr(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.s.3, i64 0, i64 0))
    la $a0, s3
    li $v0, 4
    syscall
    # call void @putint(i32 %v33)
    move $a0, $t7
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # %v34 = icmp sge i32 %v46, %v33
    # br i1 %v34, label %b7, label %b35
    bge $t4, $t7, main_bb7

main_bb35:
    # move %v46 -> %v30
    # br label %b37

main_bb37:
    # %v38 = icmp sle i32 %v45, %v33
    # br i1 %v38, label %b22, label %b39
    ble $t5, $t7, main_bb22

main_bb39:
    # %v40 = add i32 %v33, 3
    addiu $t3, $t7, 3
    # %v41 = srem i32 %v45, %v40
    div $t5, $t3
    mfhi $s1
    # call void @putstr(i8* getelementptr inbounds ([5 x i8], [5 x i8]* @.s.6, i64 0, i64 0))
    la $a0, s6
    li $v0, 4
    syscall
    # call void @putint(i32 %v41)
    move $a0, $s1
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # move %v41 -> %v36
    move $t5, $s1
    # br label %b43

main_bb43:
    # move %v44 -> %v38
    move $t3, $t0
    # br label %b45

main_bb45:
    # %v46 = icmp slt i32 %v38, %v36
    # br i1 %v46, label %b16, label %b47
    blt $t3, $t5, main_bb16

main_bb47:
    # move %v38 -> %v41
    move $t0, $t3
    # br label %b49

main_bb49:
    # br label %b50

main_bb50:
    # move %v41 -> %v44
    # move %v36 -> %v45
    # move %v30 -> %v46
    # move %v33 -> %v47
    move $t3, $t7
    # br label %b52

main_bb52:
    # %v53 = icmp ne i32 %v47, 0
    # br i1 %v53, label %b32, label %b54
    bne $t3, 0, main_bb32

main_bb54:
    # %v55 = icmp eq i32 0, %v47
    li $k0, 0
    seq $t1, $k0, $t3
    # %v56 = icmp ne i32 %v55, 0
    # br i1 %v56, label %b25, label %b57
    bne $t1, 0, main_bb25

main_bb57:
    # br label %b58

main_bb58:
    # call void @putstr(i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # call void @putint(i32 2)
    li $a0, 2
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # br label %b59

main_bb59:
    # br label %b60

main_bb60:
    # call void @putstr(i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # call void @putint(i32 %v46)
    move $a0, $t4
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # br label %b27
    j main_bb27

