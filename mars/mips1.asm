.data:
s0: .asciiz "print int : "
s1: .asciiz "\n"
s2: .asciiz "19373479\n"
s3: .asciiz "\nprint int : "
s4: .asciiz "\nprint int : "
s5: .asciiz "\nprint int : "
s6: .asciiz "\nprint int : "

.text:
func_main:
main_bb0:
    # call void @putstr(i8* getelementptr inbounds ([10 x i8], [10 x i8]* @.s.2, i64 0, i64 0))
    la $a0, s2
    li $v0, 4
    syscall
    # %v1 = call i32 @getint()
    li $v0, 5
    syscall
    move $t9, $v0
    # %v2 = call i32 @getint()
    li $v0, 5
    syscall
    sw $v0, -4($sp)
    # %v3 = call i32 @getint()
    li $v0, 5
    syscall
    move $t7, $v0
    # %v4 = call i32 @getint()
    li $v0, 5
    syscall
    sw $v0, -8($sp)
    # %v5 = icmp sgt i32 %v1, 5
    # br i1 %v5, label %b6, label %b8
    ble $t9, 5, main_bb8

main_bb6:
    # move 5 -> %v8
    li $t2, 5
    # br label %b10
    j main_bb10

main_bb8:
    # move %v1 -> %v8
    move $t2, $t9
    # br label %b10

main_bb10:
    # move %v4 -> %v75
    lw $t9, -8($sp)
    # move %v3 -> %v76
    move $fp, $t7
    # move %v2 -> %v77
    lw $t5, -4($sp)
    # move 10 -> %v78
    li $t8, 10
    # br label %b91
    j main_bb91

main_bb12:
    # %v13 = sub i32 %v78, 1
    addiu $t8, $t8, -1
    # %v14 = icmp sge i32 %v77, %v13
    # br i1 %v14, label %b35, label %b40
    bge $t5, $t8, main_bb35
    j main_bb40

main_bb15:
    # move %v13 -> %v40
    move $t5, $t4
    # br label %b50
    j main_bb50

main_bb17:
    # %v18 = sub i32 0, %v114
    li $k1, 0
    subu $s3, $k1, $t6
    # move %v18 -> %v111
    move $t4, $s3
    # br label %b123
    j main_bb123

main_bb20:
    # move %v64 -> %v13
    lw $t4, -36($sp)
    # br label %b15
    j main_bb15

main_bb22:
    # %v23 = mul i32 %v76, %v13
    mul $t3, $fp, $t8
    # move %v23 -> %v38
    move $k0, $t3
    sw $k0, -12($sp)
    # br label %b48
    j main_bb48

main_bb25:
    # %v26 = sdiv i32 %v113, %v108
    div $t5, $t4
    mflo $s2
    # %v27 = srem i32 %v113, %v108
    div $t5, $t4
    mfhi $t1
    # %v28 = add i32 %v114, %v27
    addu $k0, $t6, $t1
    sw $k0, -16($sp)
    # move %v26 -> %v107
    move $t3, $s2
    # move %v28 -> %v108
    lw $t2, -16($sp)
    # br label %b119
    j main_bb119

main_bb30:
    # %v31 = sdiv i32 %v135, %v131
    div $t1, $t4
    mflo $k0
    sw $k0, -20($sp)
    # %v32 = srem i32 %v135, %v131
    div $t1, $t4
    mfhi $t1
    # %v33 = add i32 %v136, %v32
    addu $k0, $t3, $t1
    sw $k0, -24($sp)
    # move %v31 -> %v63
    lw $t2, -20($sp)
    # move %v33 -> %v64
    lw $t0, -24($sp)
    # br label %b78
    j main_bb78

main_bb35:
    # %v36 = add i32 %v13, 1
    addiu $s4, $t8, 1
    # %v37 = sdiv i32 %v77, %v36
    div $t5, $s4
    mflo $s4
    # %v38 = add i32 %v37, %v13
    addu $s6, $s4, $t8
    # move %v38 -> %v32
    move $s4, $s6
    # br label %b42
    j main_bb42

main_bb40:
    # move %v77 -> %v32
    move $s4, $t5
    # br label %b42

main_bb42:
    # %v43 = icmp sle i32 %v76, %v13
    # br i1 %v43, label %b22, label %b44
    ble $fp, $t8, main_bb22

main_bb44:
    # %v45 = add i32 %v13, 3
    addiu $t5, $t8, 3
    # %v46 = srem i32 %v76, %v45
    div $fp, $t5
    mfhi $gp
    # move %v46 -> %v38
    move $k0, $gp
    sw $k0, -12($sp)
    # br label %b48

main_bb48:
    # move %v75 -> %v40
    move $t5, $t9
    # br label %b50

main_bb50:
    # %v51 = icmp slt i32 %v40, %v38
    # br i1 %v51, label %b63, label %b87
    lw $k1, -12($sp)
    blt $t5, $k1, main_bb63
    j main_bb87

main_bb52:
    # call void @putstr(i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # call void @putint(i32 %v78)
    move $a0, $t8
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # br label %b96
    j main_bb96

main_bb53:
    # %v54 = sdiv i32 %v75, %v76
    div $t9, $fp
    mflo $t1
    # %v55 = srem i32 %v75, %v76
    div $t9, $fp
    mfhi $t3
    # %v56 = add i32 %v98, %v55
    addu $k0, $t2, $t3
    sw $k0, -28($sp)
    # move %v54 -> %v91
    # move %v56 -> %v92
    lw $t7, -28($sp)
    # br label %b103
    j main_bb103

main_bb58:
    # move %v64 -> %v73
    lw $t5, -36($sp)
    # br label %b89
    j main_bb89

main_bb60:
    # %v61 = sub i32 0, %v136
    li $k1, 0
    subu $k0, $k1, $t3
    sw $k0, -32($sp)
    # move %v61 -> %v67
    lw $t4, -32($sp)
    # br label %b82
    j main_bb82

main_bb63:
    # %v64 = add i32 %v40, %v13
    addu $k0, $t5, $t8
    sw $k0, -36($sp)
    # %v65 = icmp eq i32 %v64, %v8
    # br i1 %v65, label %b58, label %b66
    lw $k0, -36($sp)
    beq $k0, $t2, main_bb58

main_bb66:
    # %v67 = icmp ne i32 %v64, %v32
    # br i1 %v67, label %b68, label %b20
    lw $k0, -36($sp)
    beq $k0, $s4, main_bb20

main_bb68:
    # %v69 = add i32 %v32, %v64
    lw $k1, -36($sp)
    addu $s1, $s4, $k1
    # move %v69 -> %v13
    move $t4, $s1
    # br label %b15
    j main_bb15

main_bb71:
    # %v72 = sub i32 0, %v98
    li $k1, 0
    subu $k0, $k1, $t2
    sw $k0, -40($sp)
    # move %v72 -> %v95
    lw $t6, -40($sp)
    # br label %b107
    j main_bb107

main_bb74:
    # %v75 = sdiv i32 %v135, 2
    sra $v0, $t1, 31
    srl $v0, $v0, 31
    addu $v1, $t1, $v0
    sra $s5, $v1, 1
    # %v76 = add i32 %v136, %v135
    addu $t0, $t3, $t1
    # move %v75 -> %v63
    move $t2, $s5
    # move %v76 -> %v64
    # br label %b78

main_bb78:
    # %v79 = icmp slt i32 %v136, 0
    # br i1 %v79, label %b60, label %b80
    blt $t3, 0, main_bb60

main_bb80:
    # move %v136 -> %v67
    move $t4, $t3
    # br label %b82

main_bb82:
    # call void @putstr(i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # %v83 = sub i32 %v64, %v136
    subu $t0, $t0, $t3
    # %v84 = add i32 %v83, %v63
    addu $t1, $t0, $t2
    # %v85 = add i32 1, %v67
    addiu $t0, $t4, 1
    # %v86 = mul i32 %v85, %v84
    mul $t0, $t0, $t1
    # call void @putint(i32 %v86)
    move $a0, $t0
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # ret i32 0
    li $v0, 10
    syscall

main_bb87:
    # move %v40 -> %v73
    # br label %b89

main_bb89:
    # move %v73 -> %v75
    move $t9, $t5
    # move %v38 -> %v76
    lw $fp, -12($sp)
    # move %v32 -> %v77
    move $t5, $s4
    # move %v13 -> %v78
    # br label %b91

main_bb91:
    # %v92 = icmp ne i32 %v78, 0
    # br i1 %v92, label %b12, label %b93
    bne $t8, 0, main_bb12

main_bb93:
    # %v94 = icmp eq i32 0, %v78
    li $k0, 0
    seq $t3, $k0, $t8
    # %v95 = icmp ne i32 %v94, 0
    # br i1 %v95, label %b52, label %b96
    bne $t3, 0, main_bb52

main_bb96:
    # call void @putstr(i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # call void @putint(i32 %v8)
    move $a0, $t2
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([14 x i8], [14 x i8]* @.s.3, i64 0, i64 0))
    la $a0, s3
    li $v0, 4
    syscall
    # call void @putint(i32 %v77)
    move $a0, $t5
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([14 x i8], [14 x i8]* @.s.4, i64 0, i64 0))
    la $a0, s4
    li $v0, 4
    syscall
    # call void @putint(i32 %v76)
    move $a0, $fp
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([14 x i8], [14 x i8]* @.s.5, i64 0, i64 0))
    la $a0, s5
    li $v0, 4
    syscall
    # call void @putint(i32 %v75)
    move $a0, $t9
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # %v97 = icmp ne i32 %v76, 0
    # %v98 = mul i32 %v75, %v76
    mul $t2, $t9, $fp
    # br i1 %v97, label %b53, label %b99
    bne $fp, 0, main_bb53

main_bb99:
    # %v100 = sdiv i32 %v75, 2
    sra $v0, $t9, 31
    srl $v0, $v0, 31
    addu $v1, $t9, $v0
    sra $t6, $v1, 1
    # %v101 = add i32 %v98, %v75
    addu $s0, $t2, $t9
    # move %v100 -> %v91
    move $t1, $t6
    # move %v101 -> %v92
    move $t7, $s0
    # br label %b103

main_bb103:
    # %v104 = icmp slt i32 %v98, 0
    # br i1 %v104, label %b71, label %b105
    blt $t2, 0, main_bb71

main_bb105:
    # move %v98 -> %v95
    move $t6, $t2
    # br label %b107

main_bb107:
    # %v108 = mul i32 %v77, 10
    sll $v0, $t5, 1
    sll $v1, $t5, 3
    addu $t4, $v0, $v1
    # %v109 = icmp ne i32 %v108, 0
    # %v110 = sub i32 %v92, %v98
    subu $t2, $t7, $t2
    # %v111 = add i32 %v110, %v91
    addu $t2, $t2, $t1
    # %v112 = add i32 1, %v95
    addiu $t1, $t6, 1
    # %v113 = mul i32 %v112, %v111
    mul $t5, $t1, $t2
    # %v114 = mul i32 %v113, %v108
    mul $t6, $t5, $t4
    # br i1 %v109, label %b25, label %b115
    bne $t4, 0, main_bb25

main_bb115:
    # %v116 = sdiv i32 %v113, 2
    sra $v0, $t5, 31
    srl $v0, $v0, 31
    addu $v1, $t5, $v0
    sra $k0, $v1, 1
    sw $k0, -44($sp)
    # %v117 = add i32 %v114, %v113
    addu $s7, $t6, $t5
    # move %v116 -> %v107
    lw $t3, -44($sp)
    # move %v117 -> %v108
    move $t2, $s7
    # br label %b119

main_bb119:
    # %v120 = icmp slt i32 %v114, 0
    # br i1 %v120, label %b17, label %b121
    blt $t6, 0, main_bb17

main_bb121:
    # move %v114 -> %v111
    move $t4, $t6
    # br label %b123

main_bb123:
    # call void @putstr(i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.s.0, i64 0, i64 0))
    la $a0, s0
    li $v0, 4
    syscall
    # call void @putint(i32 %v113)
    move $a0, $t5
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([14 x i8], [14 x i8]* @.s.6, i64 0, i64 0))
    la $a0, s6
    li $v0, 4
    syscall
    # %v124 = sub i32 %v108, %v114
    subu $t1, $t2, $t6
    # %v125 = add i32 %v124, %v107
    addu $t2, $t1, $t3
    # %v126 = add i32 1, %v111
    addiu $t1, $t4, 1
    # %v127 = mul i32 %v126, %v125
    mul $t1, $t1, $t2
    # call void @putint(i32 %v127)
    move $a0, $t1
    li $v0, 1
    syscall
    # call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
    la $a0, s1
    li $v0, 4
    syscall
    # %v128 = sub i32 40, 39
    li $k0, 40
    addiu $t1, $k0, -39
    # %v129 = add i32 %v128, 4
    addiu $t2, $t1, 4
    # %v130 = add i32 1, 39
    li $k0, 1
    addiu $t1, $k0, 39
    # %v131 = mul i32 %v130, %v129
    mul $t4, $t1, $t2
    # %v132 = icmp ne i32 %v131, 0
    # %v133 = add i32 -10, -5
    li $k0, -10
    addiu $t3, $k0, -5
    # %v134 = add i32 1, 0
    li $k0, 1
    move $t1, $k0
    # %v135 = mul i32 %v134, %v133
    mul $t1, $t1, $t3
    # %v136 = mul i32 %v135, %v131
    mul $t3, $t1, $t4
    # br i1 %v132, label %b30, label %b74
    bne $t4, 0, main_bb30
    j main_bb74

