.data
s0: .asciiz "round "
s1: .asciiz ": "
s2: .asciiz "\n"


.text
main:
main_b0:
# %1 = call i32 @getint()
li $v0, 5
syscall
move $t0, $v0
# move 1 -> %3
li $t1, 1
# move 1 -> %4
li $t2, 1
# move 1 -> %5
li $t3, 1
# br label %6
main_b6:
# %7 = add i32 %1, 1
addiu $t4, $t0, 1
# %8 = icmp slt i32 %3, %7
# br i1 %8, label %9, label %22
blt $t1, $t4, main_b9
j main_b22
main_b9:
# %10 = add i32 %5, %4
addu $t3, $t3, $t2
# %11 = sdiv i32 %3, 2
sra $v0, $t1, 31
srl $v0, $v0, 31
addu $v1, $t1, $v0
sra $t4, $v1, 1
# %12 = mul i32 %11, 2
addu $t4, $t4, $t4
# %13 = sub i32 %3, %12
subu $t4, $t1, $t4
# %14 = icmp eq i32 %13, 1
# br i1 %14, label %23, label %24
beq $t4, 1, main_b23
j main_b24
main_b15:
# %16 = add i32 %3, 1
addiu $t1, $t1, 1
# move %4 -> %t28
move $k0, $t2
sw $k0, -4($sp)
# move %16 -> %3
move $t1, $t1
# move %10 -> %4
move $t2, $t3
# move %t28 -> %5
lw $t3, -4($sp)
# br label %6
j main_b6
main_b22:
# ret i32 0
li $v0, 10
syscall
main_b23:
# br label %15
j main_b15
main_b24:
# call void @putstr(i8* getelementptr inbounds ([7 x i8], [7 x i8]* @.s.0, i64 0, i64 0))
la $a0, s0
li $v0, 4
syscall
# call void @putint(i32 %3)
move $a0, $t1
li $v0, 1
syscall
# call void @putstr(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.s.1, i64 0, i64 0))
la $a0, s1
li $v0, 4
syscall
# call void @putint(i32 %4)
move $a0, $t2
li $v0, 1
syscall
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.2, i64 0, i64 0))
la $a0, s2
li $v0, 4
syscall
# %25 = icmp sgt i32 %3, 19
# br i1 %25, label %26, label %27
bgt $t1, 19, main_b26
j main_b27
main_b26:
# br label %22
j main_b22
main_b27:
# br label %15
j main_b15
