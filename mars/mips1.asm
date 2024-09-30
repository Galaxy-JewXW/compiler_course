.data
s0: .asciiz "print int : "
s1: .asciiz "\n"
s2: .asciiz "19373479\n"


.text
main:
main_b0:
# call void @putstr(i8* getelementptr inbounds ([10 x i8], [10 x i8]* @.s.2, i64 0, i64 0))
la $a0, s2
li $v0, 4
syscall
# %1 = call i32 @getint()
li $v0, 5
syscall
move $t0, $v0
# %2 = call i32 @getint()
syscall
move $t1, $v0
# %3 = call i32 @getint()
syscall
move $t2, $v0
# %4 = call i32 @getint()
syscall
move $t3, $v0
# %5 = icmp sgt i32 %1, 5
# br i1 %5, label %6, label %155
bgt $t0, 5, main_b6
j main_b155
main_b6:
# move 5 -> %8
li $t4, 5
# br label %7
j main_b7
main_b155:
# move %1 -> %8
move $t4, $t0
# br label %7
j main_b7
main_b7:
# move %4 -> %10
move $t0, $t3
# move %3 -> %11
move $t5, $t2
# move %2 -> %12
move $t6, $t1
# move 10 -> %13
li $t7, 10
# br label %9
j main_b9
main_b9:
# %14 = icmp ne i32 %13, 0
# br i1 %14, label %15, label %18
bne $t7, 0, main_b15
j main_b18
main_b15:
# %16 = sub i32 %13, 1
addiu $t7, $t7, -1
# %17 = icmp sge i32 %12, %16
# br i1 %17, label %22, label %166
bge $t6, $t7, main_b22
j main_b166
main_b18:
# %19 = icmp eq i32 0, %13
li $k0, 0
seq $t1, $k0, $t7
# %20 = zext i1 %19 to i32
move $t1, $t1
# %21 = icmp ne i32 %20, 0
# br i1 %21, label %52, label %54
bne $t1, 0, main_b52
j main_b54
main_b22:
# %23 = add i32 %16, 1
addiu $s0, $t7, 1
# %24 = sdiv i32 %12, %23
div $t6, $s0
mflo $s0
# %25 = add i32 %24, %16
addu $s0, $s0, $t7
# move %25 -> %27
move $s0, $s0
# br label %26
j main_b26
main_b166:
# move %12 -> %27
move $s0, $t6
# br label %26
j main_b26
main_b26:
# %28 = icmp sle i32 %11, %16
# br i1 %28, label %29, label %31
ble $t5, $t7, main_b29
j main_b31
main_b29:
# %30 = mul i32 %11, %16
mul $t5, $t5, $t7
# move %30 -> %35
move $t5, $t5
# br label %34
j main_b34
main_b31:
# %32 = add i32 %16, 3
addiu $t6, $t7, 3
# %33 = srem i32 %11, %32
div $t5, $t6
mfhi $t5
# move %33 -> %35
move $t5, $t5
# br label %34
j main_b34
main_b34:
# move %10 -> %37
move $t6, $t0
# br label %36
j main_b36
main_b36:
# %38 = icmp slt i32 %37, %35
# br i1 %38, label %39, label %175
blt $t6, $t5, main_b39
j main_b175
main_b39:
# %40 = add i32 %37, %16
addu $s1, $t6, $t7
# %41 = icmp eq i32 %40, %8
# br i1 %41, label %46, label %47
beq $s1, $t4, main_b46
j main_b47
main_b42:
# move %43 -> %37
move $t6, $t6
# br label %36
j main_b36
main_b175:
# move %37 -> %45
move $t0, $t6
# br label %44
j main_b44
main_b44:
# move %45 -> %10
move $t0, $t0
# move %35 -> %11
move $t5, $t5
# move %27 -> %12
move $t6, $s0
# move %16 -> %13
move $t7, $t7
# br label %9
j main_b9
main_b46:
# move %40 -> %45
move $t0, $s1
# br label %44
j main_b44
main_b47:
# %48 = icmp ne i32 %40, %27
# br i1 %48, label %49, label %51
bne $s1, $s0, main_b49
j main_b51
main_b49:
# %50 = add i32 %27, %40
addu $t6, $s0, $s1
# move %50 -> %43
move $t6, $t6
# br label %42
j main_b42
main_b51:
# move %40 -> %43
move $t6, $s1
# br label %42
j main_b42
main_b52:
# call void @putstr(i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.s.0, i64 0, i64 0))
la $a0, s0
li $v0, 4
syscall
# call void @putint(i32 %13)
move $a0, $t7
li $v0, 1
syscall
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
la $a0, s1
li $v0, 4
syscall
# br label %53
j main_b53
main_b53:
# br label %54
j main_b54
main_b54:
# call void @putstr(i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.s.0, i64 0, i64 0))
la $a0, s0
syscall
# call void @putint(i32 %8)
move $a0, $t4
li $v0, 1
syscall
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
la $a0, s1
li $v0, 4
syscall
# br label %55
j main_b55
main_b55:
# call void @putstr(i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.s.0, i64 0, i64 0))
la $a0, s0
syscall
# call void @putint(i32 %12)
move $a0, $t6
li $v0, 1
syscall
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
la $a0, s1
li $v0, 4
syscall
# br label %56
j main_b56
main_b56:
# call void @putstr(i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.s.0, i64 0, i64 0))
la $a0, s0
syscall
# call void @putint(i32 %11)
move $a0, $t5
li $v0, 1
syscall
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
la $a0, s1
li $v0, 4
syscall
# br label %57
j main_b57
main_b57:
# call void @putstr(i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.s.0, i64 0, i64 0))
la $a0, s0
syscall
# call void @putint(i32 %10)
move $a0, $t0
li $v0, 1
syscall
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
la $a0, s1
li $v0, 4
syscall
# br label %58
j main_b58
main_b58:
# %59 = mul i32 %10, %11
mul $t1, $t0, $t5
# %60 = icmp ne i32 %11, 0
# br i1 %60, label %61, label %65
bne $t5, 0, main_b61
j main_b65
main_b61:
# %62 = srem i32 %10, %11
div $t0, $t5
mfhi $t2
# %63 = add i32 %59, %62
addu $t2, $t1, $t2
# %64 = sdiv i32 %10, %11
div $t0, $t5
mflo $t0
# move %64 -> %69
move $t0, $t0
# move %63 -> %70
move $t2, $t2
# br label %68
j main_b68
main_b65:
# %66 = add i32 %59, %10
addu $t2, $t1, $t0
# %67 = sdiv i32 %10, 2
sra $v0, $t0, 31
srl $v0, $v0, 31
addu $v1, $t0, $v0
sra $t0, $v1, 1
# move %67 -> %69
move $t0, $t0
# move %66 -> %70
move $t2, $t2
# br label %68
j main_b68
main_b68:
# %71 = sub i32 %70, %59
subu $t2, $t2, $t1
# %72 = icmp slt i32 %59, 0
# br i1 %72, label %73, label %182
blt $t1, 0, main_b73
j main_b182
main_b73:
# %74 = sub i32 0, %59
li $k1, 0
subu $t3, $k1, $t1
# move %74 -> %76
move $t3, $t3
# br label %75
j main_b75
main_b182:
# move %59 -> %76
move $t3, $t1
# br label %75
j main_b75
main_b75:
# %77 = add i32 1, %76
addiu $t3, $t3, 1
# %78 = add i32 %71, %69
addu $t0, $t2, $t0
# %79 = mul i32 %77, %78
mul $t0, $t3, $t0
# br label %80
j main_b80
main_b80:
# %81 = mul i32 %12, 10
sll $v0, $t6, 1
sll $v1, $t6, 3
addu $t1, $v0, $v1
# br label %82
j main_b82
main_b82:
# %83 = mul i32 %79, %81
mul $t2, $t0, $t1
# %84 = icmp ne i32 %81, 0
# br i1 %84, label %85, label %89
bne $t1, 0, main_b85
j main_b89
main_b85:
# %86 = srem i32 %79, %81
div $t0, $t1
mfhi $t3
# %87 = add i32 %83, %86
addu $t3, $t2, $t3
# %88 = sdiv i32 %79, %81
div $t0, $t1
mflo $t1
# move %88 -> %93
move $t1, $t1
# move %87 -> %94
move $t3, $t3
# br label %92
j main_b92
main_b89:
# %90 = add i32 %83, %79
addu $t1, $t2, $t0
# %91 = sdiv i32 %79, 2
sra $v0, $t0, 31
srl $v0, $v0, 31
addu $v1, $t0, $v0
sra $t3, $v1, 1
# move %93 -> %t187
move $k0, $t1
lw $k0, -4($sp)
# move %91 -> %93
move $t1, $t3
# move %t187 -> %94
lw $t3, -4($sp)
# br label %92
j main_b92
main_b92:
# %95 = sub i32 %94, %83
subu $t3, $t3, $t2
# %96 = icmp slt i32 %83, 0
# br i1 %96, label %97, label %191
blt $t2, 0, main_b97
j main_b191
main_b97:
# %98 = sub i32 0, %83
li $k1, 0
subu $t4, $k1, $t2
# move %98 -> %100
move $t4, $t4
# br label %99
j main_b99
main_b191:
# move %83 -> %100
move $t4, $t2
# br label %99
j main_b99
main_b99:
# %101 = add i32 1, %100
addiu $t4, $t4, 1
# %102 = add i32 %95, %93
addu $t1, $t3, $t1
# %103 = mul i32 %101, %102
mul $t1, $t4, $t1
# br label %104
j main_b104
main_b104:
# br label %105
j main_b105
main_b105:
# call void @putstr(i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.s.0, i64 0, i64 0))
la $a0, s0
li $v0, 4
syscall
# call void @putint(i32 %79)
move $a0, $t0
li $v0, 1
syscall
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
la $a0, s1
li $v0, 4
syscall
# br label %106
j main_b106
main_b106:
# call void @putstr(i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.s.0, i64 0, i64 0))
la $a0, s0
syscall
# call void @putint(i32 %103)
move $a0, $t1
li $v0, 1
syscall
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
la $a0, s1
li $v0, 4
syscall
# br label %107
j main_b107
main_b107:
# br label %108
j main_b108
main_b108:
# %109 = add i32 1, 0
li $k0, 1
addiu $t0, $k0, 0
# %110 = add i32 -10, -5
li $k0, -10
addiu $t1, $k0, -5
# %111 = mul i32 %109, %110
mul $t0, $t0, $t1
# br label %112
j main_b112
main_b112:
# br label %113
j main_b113
main_b113:
# %114 = sub i32 40, 39
li $k0, 40
addiu $t1, $k0, -39
# br label %115
j main_b115
main_b115:
# %116 = add i32 1, 39
li $k0, 1
addiu $t2, $k0, 39
# %117 = add i32 %114, 4
addiu $t1, $t1, 4
# %118 = mul i32 %116, %117
mul $t1, $t2, $t1
# br label %119
j main_b119
main_b119:
# %120 = mul i32 %111, %118
mul $t2, $t0, $t1
# %121 = icmp ne i32 %118, 0
# br i1 %121, label %122, label %126
bne $t1, 0, main_b122
j main_b126
main_b122:
# %123 = srem i32 %111, %118
div $t0, $t1
mfhi $t3
# %124 = add i32 %120, %123
addu $t3, $t2, $t3
# %125 = sdiv i32 %111, %118
div $t0, $t1
mflo $t0
# move %125 -> %130
move $t0, $t0
# move %124 -> %131
move $t1, $t3
# br label %129
j main_b129
main_b126:
# %127 = add i32 %120, %111
addu $t1, $t2, $t0
# %128 = sdiv i32 %111, 2
sra $v0, $t0, 31
srl $v0, $v0, 31
addu $v1, $t0, $v0
sra $t0, $v1, 1
# move %128 -> %130
move $t0, $t0
# move %127 -> %131
move $t1, $t1
# br label %129
j main_b129
main_b129:
# %132 = sub i32 %131, %120
subu $t1, $t1, $t2
# %133 = icmp slt i32 %120, 0
# br i1 %133, label %134, label %198
blt $t2, 0, main_b134
j main_b198
main_b134:
# %135 = sub i32 0, %120
li $k1, 0
subu $t3, $k1, $t2
# move %135 -> %137
move $t3, $t3
# br label %136
j main_b136
main_b198:
# move %120 -> %137
move $t3, $t2
# br label %136
j main_b136
main_b136:
# %138 = add i32 1, %137
addiu $t3, $t3, 1
# %139 = add i32 %132, %130
addu $t0, $t1, $t0
# %140 = mul i32 %138, %139
mul $t0, $t3, $t0
# br label %141
j main_b141
main_b141:
# call void @putstr(i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.s.0, i64 0, i64 0))
la $a0, s0
li $v0, 4
syscall
# call void @putint(i32 %140)
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
funcDef_void:
funcDef_void_b0:
# ret void
jr $ra
funcDef_0:
funcDef_0_b1:
# %2 = mul i32 %0, 10
sll $v0, $a1, 1
sll $v1, $a1, 3
addu $t0, $v0, $v1
# ret i32 %2
move $v0, $t0
jr $ra
funcDef_1:
funcDef_1_b2:
# %3 = mul i32 %0, %1
mul $t0, $a1, $a2
# %4 = icmp ne i32 %1, 0
# br i1 %4, label %5, label %9
bne $a2, 0, funcDef_1_b5
j funcDef_1_b9
funcDef_1_b5:
# %6 = srem i32 %0, %1
div $a1, $a2
mfhi $t1
# %7 = add i32 %3, %6
addu $t1, $t0, $t1
# %8 = sdiv i32 %0, %1
div $a1, $a2
mflo $t2
# move %13 -> %t146
move $k0, $t1
lw $k0, -12($sp)
# move %8 -> %13
move $t1, $t2
# move %t146 -> %14
lw $t2, -12($sp)
# br label %12
j funcDef_1_b12
funcDef_1_b9:
# %10 = add i32 %3, %0
addu $t1, $t0, $a1
# %11 = sdiv i32 %0, 2
sra $v0, $a1, 31
srl $v0, $v0, 31
addu $v1, $a1, $v0
sra $t2, $v1, 1
# move %13 -> %t148
move $k0, $t1
lw $k0, -16($sp)
# move %11 -> %13
move $t1, $t2
# move %t148 -> %14
lw $t2, -16($sp)
# br label %12
j funcDef_1_b12
funcDef_1_b12:
# %15 = sub i32 %14, %3
subu $t2, $t2, $t0
# %16 = icmp slt i32 %3, 0
# br i1 %16, label %17, label %152
blt $t0, 0, funcDef_1_b17
j funcDef_1_b152
funcDef_1_b17:
# %18 = sub i32 0, %3
li $k1, 0
subu $t3, $k1, $t0
# move %18 -> %20
move $t3, $t3
# br label %19
j funcDef_1_b19
funcDef_1_b152:
# move %3 -> %20
move $t3, $t0
# br label %19
j funcDef_1_b19
funcDef_1_b19:
# %21 = add i32 1, %20
addiu $t3, $t3, 1
# %22 = add i32 %15, %13
addu $t1, $t2, $t1
# %23 = mul i32 %21, %22
mul $t1, $t3, $t1
# ret i32 %23
move $v0, $t1
jr $ra
printInt:
printInt_b1:
# call void @putstr(i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.s.0, i64 0, i64 0))
la $a0, s0
li $v0, 4
syscall
# call void @putint(i32 %0)
move $a0, $a1
li $v0, 1
syscall
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
la $a0, s1
li $v0, 4
syscall
# ret void
jr $ra
