.data
s0: .asciiz ", "
s1: .asciiz "\n"
a: .word 0, 1, 2, 3, 4, 5, 6, 7, 8, 9


.text
main:
main_b0:
# %1 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 1
la $k0, a
addiu $t0, $k0, 4
# move 0 -> %3
li $t1, 0
# br label %4
j main_b4
main_b4:
# %5 = icmp sle i32 %3, 1
# br i1 %5, label %6, label %39
ble $t1, 1, main_b6
j main_b39
main_b6:
# %7 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 0
la $k0, a
addiu $t2, $k0, 0
# %8 = load i32, i32* %7
lw $t3, 0($t2)
# %9 = add i32 %8, 36
addiu $t3, $t3, 36
# store i32 %9, i32* %7
sw $t3, 0($t2)
# %10 = load i32, i32* %1
lw $t2, 0($t0)
# %11 = add i32 %10, 36
addiu $t2, $t2, 36
# store i32 %11, i32* %1
sw $t2, 0($t0)
# %12 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 2
la $k0, a
addiu $t2, $k0, 8
# %13 = load i32, i32* %12
lw $t3, 0($t2)
# %14 = add i32 %13, 36
addiu $t3, $t3, 36
# store i32 %14, i32* %12
sw $t3, 0($t2)
# %15 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 3
la $k0, a
addiu $t2, $k0, 12
# %16 = load i32, i32* %15
lw $t3, 0($t2)
# %17 = add i32 %16, 36
addiu $t3, $t3, 36
# store i32 %17, i32* %15
sw $t3, 0($t2)
# %18 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 4
la $k0, a
addiu $t2, $k0, 16
# %19 = load i32, i32* %18
lw $t3, 0($t2)
# %20 = add i32 %19, 36
addiu $t3, $t3, 36
# store i32 %20, i32* %18
sw $t3, 0($t2)
# %21 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 5
la $k0, a
addiu $t2, $k0, 20
# %22 = load i32, i32* %21
lw $t3, 0($t2)
# %23 = add i32 %22, 36
addiu $t3, $t3, 36
# store i32 %23, i32* %21
sw $t3, 0($t2)
# %24 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 6
la $k0, a
addiu $t2, $k0, 24
# %25 = load i32, i32* %24
lw $t3, 0($t2)
# %26 = add i32 %25, 36
addiu $t3, $t3, 36
# store i32 %26, i32* %24
sw $t3, 0($t2)
# %27 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 7
la $k0, a
addiu $t2, $k0, 28
# %28 = load i32, i32* %27
lw $t3, 0($t2)
# %29 = add i32 %28, 36
addiu $t3, $t3, 36
# store i32 %29, i32* %27
sw $t3, 0($t2)
# %30 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 8
la $k0, a
addiu $t2, $k0, 32
# %31 = load i32, i32* %30
lw $t3, 0($t2)
# %32 = add i32 %31, 36
addiu $t3, $t3, 36
# store i32 %32, i32* %30
sw $t3, 0($t2)
# %33 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 9
la $k0, a
addiu $t2, $k0, 36
# %34 = load i32, i32* %33
lw $t3, 0($t2)
# %35 = add i32 %34, 36
addiu $t3, $t3, 36
# store i32 %35, i32* %33
sw $t3, 0($t2)
# %36 = add i32 %3, 1
addiu $t1, $t1, 1
# move %36 -> %3
move $t1, $t1
# br label %4
j main_b4
main_b39:
# move 0 -> %38
li $t0, 0
# br label %42
j main_b42
main_b42:
# %43 = icmp slt i32 %38, 10
# br i1 %43, label %44, label %50
blt $t0, 10, main_b44
j main_b50
main_b44:
# %45 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 %38
la $k0, a
sll $k1, $t0, 2
addu $t1, $k0, $k1
# %46 = load i32, i32* %45
lw $t1, 0($t1)
# call void @putint(i32 %46)
move $a0, $t1
li $v0, 1
syscall
# call void @putstr(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.s.0, i64 0, i64 0))
la $a0, s0
li $v0, 4
syscall
# %47 = add i32 %38, 1
addiu $t0, $t0, 1
# move %47 -> %38
move $t0, $t0
# br label %42
j main_b42
main_b50:
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
la $a0, s1
li $v0, 4
syscall
# call void @putint(i32 %38)
move $a0, $t0
li $v0, 1
syscall
# call void @putstr(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.s.0, i64 0, i64 0))
la $a0, s0
li $v0, 4
syscall
# call void @putint(i32 -8894)
li $a0, -8894
li $v0, 1
syscall
# call void @putstr(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.s.0, i64 0, i64 0))
la $a0, s0
li $v0, 4
syscall
# call void @putint(i32 -6)
li $a0, -6
li $v0, 1
syscall
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.s.1, i64 0, i64 0))
la $a0, s1
li $v0, 4
syscall
# ret i32 0
li $v0, 10
syscall
