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
# br label %2
j main_b2
main_b2:
# %4 = icmp sle i32 %3, 100
# br i1 %4, label %5, label %36
ble $t1, 100, main_b5
j main_b36
main_b5:
# %6 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 0
la $k0, a
addiu $t2, $k0, 0
# %7 = load i32, i32* %6
lw $t3, 0($t2)
# %8 = add i32 %7, 36
addiu $t3, $t3, 36
# store i32 %8, i32* %6
sw $t3, 0($t2)
# %9 = load i32, i32* %1
lw $t2, 0($t0)
# %10 = add i32 %9, 36
addiu $t2, $t2, 36
# store i32 %10, i32* %1
sw $t2, 0($t0)
# %11 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 2
la $k0, a
addiu $t2, $k0, 8
# %12 = load i32, i32* %11
lw $t3, 0($t2)
# %13 = add i32 %12, 36
addiu $t3, $t3, 36
# store i32 %13, i32* %11
sw $t3, 0($t2)
# %14 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 3
la $k0, a
addiu $t2, $k0, 12
# %15 = load i32, i32* %14
lw $t3, 0($t2)
# %16 = add i32 %15, 36
addiu $t3, $t3, 36
# store i32 %16, i32* %14
sw $t3, 0($t2)
# %17 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 4
la $k0, a
addiu $t2, $k0, 16
# %18 = load i32, i32* %17
lw $t3, 0($t2)
# %19 = add i32 %18, 36
addiu $t3, $t3, 36
# store i32 %19, i32* %17
sw $t3, 0($t2)
# %20 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 5
la $k0, a
addiu $t2, $k0, 20
# %21 = load i32, i32* %20
lw $t3, 0($t2)
# %22 = add i32 %21, 36
addiu $t3, $t3, 36
# store i32 %22, i32* %20
sw $t3, 0($t2)
# %23 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 6
la $k0, a
addiu $t2, $k0, 24
# %24 = load i32, i32* %23
lw $t3, 0($t2)
# %25 = add i32 %24, 36
addiu $t3, $t3, 36
# store i32 %25, i32* %23
sw $t3, 0($t2)
# %26 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 7
la $k0, a
addiu $t2, $k0, 28
# %27 = load i32, i32* %26
lw $t3, 0($t2)
# %28 = add i32 %27, 36
addiu $t3, $t3, 36
# store i32 %28, i32* %26
sw $t3, 0($t2)
# %29 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 8
la $k0, a
addiu $t2, $k0, 32
# %30 = load i32, i32* %29
lw $t3, 0($t2)
# %31 = add i32 %30, 36
addiu $t3, $t3, 36
# store i32 %31, i32* %29
sw $t3, 0($t2)
# %32 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 9
la $k0, a
addiu $t2, $k0, 36
# %33 = load i32, i32* %32
lw $t3, 0($t2)
# %34 = add i32 %33, 36
addiu $t3, $t3, 36
# store i32 %34, i32* %32
sw $t3, 0($t2)
# %35 = add i32 %3, 1
addiu $t1, $t1, 1
# move %35 -> %3
move $t1, $t1
# br label %2
j main_b2
main_b36:
# move 0 -> %38
li $t0, 0
# br label %37
j main_b37
main_b37:
# %39 = icmp slt i32 %38, 10
# br i1 %39, label %40, label %44
blt $t0, 10, main_b40
j main_b44
main_b40:
# %41 = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 %38
la $k0, a
sll $k1, $t0, 2
addu $t1, $k0, $k1
# %42 = load i32, i32* %41
lw $t1, 0($t1)
# call void @putint(i32 %42)
move $a0, $t1
li $v0, 1
syscall
# call void @putstr(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.s.0, i64 0, i64 0))
la $a0, s0
li $v0, 4
syscall
# %43 = add i32 %38, 1
addiu $t0, $t0, 1
# move %43 -> %38
move $t0, $t0
# br label %37
j main_b37
main_b44:
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